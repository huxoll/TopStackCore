package com.msi.tough.engine.aws.ec2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.compute.ComputeServices;
import org.dasein.cloud.compute.VMLaunchOptions;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VirtualMachineSupport;
import org.dasein.cloud.compute.VmState;
import org.dasein.cloud.compute.VolumeState;
import org.dasein.cloud.compute.VolumeSupport;
import org.dasein.cloud.network.IPVersion;
import org.dasein.cloud.network.IpAddress;
import org.dasein.cloud.network.IpAddressSupport;
import org.dasein.cloud.network.NetworkServices;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.EbsInstanceBlockDevice;
import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.InstanceBlockDeviceMapping;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.ec2.InstanceType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.core.StringHelper;
import com.msi.tough.core.TemplateHelper;
import com.msi.tough.dasein.DaseinHelper;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.engine.utils.InstanceUtils;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.InstanceBean;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.ChefUtil;
import com.msi.tough.utils.ConfigurationUtil;
import com.msi.tough.utils.Constants;
import com.msi.tough.utils.InstanceUtil;
//import com.transcend.compute.utils.ComputeFaults;

/**
 * Instance provider
 * 
 * @author raj
 * 
 */
public class Instance extends BaseProvider implements Constants {
	private final static Logger logger = Appctx.getLogger(Instance.class
			.getName());
	public static String TYPE = "AWS::EC2::Instance";
	
	public static InstanceType createChefInstance(final AccountType ac,
			final String name, final String parentId,
			final CallStruct parentCall, final Map<String, Object> prop)
			throws Exception {
		InstanceUtil.putDefInstanceValues(prop, ac);
		final String avz = (String) prop.get(AVAILABILITYZONE);
		String instType = (String) prop.get(INSTANCETYPE);

		if (instType != null) {
			prop.put(INSTANCETYPE, instType);
		} else {
			instType = (String) ConfigurationUtil.getConfiguration(
					INSTANCETYPE, avz, "LoadBalancer");
			prop.put(INSTANCETYPE, instType);
		}

		// read user data required to launch an instance with chef role
		String targetFTL = "ChefInstanceUserdata";
		if (prop.get("UseDBInstanceUserdata") != null
				&& (Boolean) prop.get("UseDBInstanceUserdata") == true) {
			targetFTL = "DBInstanceUserdata";
		}
		final String userdata = TemplateHelper.processFile(targetFTL, prop);
		final JsonNode userload = JsonUtil.load(userdata);
		final String userconv = parentCall.getCtx().getValue(userload)
				.toString();
		// final byte[] b64 = Base64.encode(userconv.getBytes());
		prop.put(USERDATA, userconv);

		// build instance call structure
		final CallStruct c0 = parentCall.newCall(parentId);
		c0.setAc(ac);
		c0.setName(name);
		c0.setProperties(prop);
		c0.setNoWait(1);
		c0.setType(Instance.TYPE);
		c0.setWaitHookClass(parentCall.getWaitHookClass());
		c0.setResourceData(parentCall.getResourceData());
		c0.getProperties().put(SECURITYGROUPIDS, prop.get(SECURITYGROUPIDS));
		logger.debug("createChefInstance " + c0.toString());
		final InstanceType inst = (InstanceType) CFUtil.createResource(c0);
		return inst;
	}

	public static InstanceType createChefInstanceWithVolume(
			final AccountType ac, final String name, final String parentId,
			final CallStruct parentCall, final Map<String, Object> prop,
			final String volId) throws Exception {
		final String avz = (String) prop.get(AVAILABILITYZONE);
		final InstanceType inst = createChefInstance(ac, name, parentId,
				parentCall, prop);

		// check instance status; wait if it's not available
		final String instanceId = inst.getPhysicalId();
		int failCount = 0;
		parentCall.setAvailabilityZone(avz);
		final CloudProvider cloudProvider = parentCall.getCloudProvider();
		final ComputeServices compute = cloudProvider.getComputeServices();
		final VirtualMachineSupport vmServ = compute.getVirtualMachineSupport();
		boolean available = false;
		while (!available && failCount < 50) {
			final VirtualMachine vm = vmServ.getVirtualMachine(instanceId);
			if (vm.getCurrentState().equals(VmState.RUNNING)) {
				available = true;
			}
			if (vm.getCurrentState().equals(VmState.TERMINATED)
					|| vm.getCurrentState().equals("error")) {
				throw ErrorResponse.InternalFailure();
			}
			Thread.sleep(3000);
			++failCount;
		}
		if (!available) {
			throw ErrorResponse.InternalFailure();
		}

		boolean volReady = false;
		failCount = 0;
		final VolumeSupport volSupport = compute.getVolumeSupport();
		while (!volReady && failCount < 50) {
			final org.dasein.cloud.compute.Volume v = volSupport
					.getVolume(volId);
			if (VolumeState.AVAILABLE.equals(v.getCurrentState())) {
				volReady = true;
			} else {
				Thread.sleep(5000);
				++failCount;
			}
		}
		if (!volReady) {
			throw ErrorResponse.InternalFailure();
		}

		{
			final CallStruct call = parentCall.newCall(null);
			final Volume volProvider = new Volume();
			final Map<String, Object> properties = new HashMap<String, Object>();
			if (parentCall.getProperties().containsKey("Device")) {
				properties.put("Device",
						parentCall.getProperties().get("Device"));
			}
			properties.put("InstanceId", instanceId);
			properties.put("VolumeId", volId);
			properties.put("AvailabilityZone", call.getAvailabilityZone());
			call.setProperties(properties);
			Volume.attach(call);
		}

		{
			final CallStruct call = parentCall.newCall(null);
			final Volume volProvider = new Volume();
			org.dasein.cloud.compute.Volume vol = Volume.describe(call);
			int failTry = 0;
			while (failTry == 5
					&& !vol.getCurrentState().toString().equals("in-use")) {
				logger.debug("Volume state is still "
						+ vol.getCurrentState().toString());
				Thread.sleep(10000);
				++failTry;

				vol = Volume.describe(call);
			}
		}

		return inst;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public CFType create0(final CallStruct call) throws Exception {
		// can only support one instance creation at a time
		if (call.getProperty(MINCOUNT) != null) {
			final int min = Integer.parseInt((String) call
					.getProperty(MINCOUNT));
			if (min != 1) {
				throw new Exception("MinCount should be 1");
			}
		}
		if (call.getProperty(MAXCOUNT) != null) {
			final int max = Integer.parseInt((String) call
					.getProperty(MAXCOUNT));
			if (max != 1) {
				throw new Exception("MaxCount should be 1");
			}
		}
		logger.debug("createInstance " + call.toString());

		final AccountType ac = call.getAc();
		final CloudProvider cloudProvider = call.getCloudProvider();


		// save instance in DB
		final InstanceType ins = new InstanceType();
		ins.setAcId(ac.getId());
		ins.setAvailabilityZone((String) call.getProperty(AVAILABILITYZONE));
		ins.setImageId((String) call.getRequiredProperty(IMAGEID));
			
		ins.setInstanceType((String) call.getRequiredProperty(INSTANCETYPE));
		logger.debug("**Setting InstanceType for the new instance: " + (String) call.getRequiredProperty(INSTANCETYPE));
		final String kid = (String) call.getProperty(KERNELID);
		if (!StringHelper.isBlank(kid)) {
			ins.setKernelId(kid);
		}
		String keyName = (String) call.getProperty(KEYNAME);
		if (keyName == null) {
			keyName = ac.getDefKeyName();
		}
		ins.setKeyName(keyName);
		final String rid = (String) call.getProperty(RAMDISKID);
		if (!StringHelper.isBlank(rid)) {
			ins.setRamDiskId(rid);
		}
		String securityGroupIds = null;
		{
			Object o = call.getProperty(SECURITYGROUPIDS);
			if (o instanceof List) {
				o = ((List) o).get(0);
			}
			if (o instanceof String) {
				securityGroupIds = (String) o;
			}
			if (o instanceof JsonNode) {
				securityGroupIds = ((JsonNode) o).getTextValue();
			}
		}
		if (securityGroupIds != null) {
			logger.debug("Setting security group for the new instance: "
					+ securityGroupIds);
			ins.setSecurityGroupIds(securityGroupIds);
		}
		ins.setUserData((String) call.getProperty(USERDATA));
		ins.setChefRoles((String) call.getProperty(CHEFROLES));
		ins.setDatabag((String) call.getProperty(DATABAG));
		ins.setHostname((String) call.getProperty(HOSTNAME));
		ins.setNoWait("" + call.getNoWait());

		if (ins.getHostname() == null) {
			ins.setHostname(call.getStackId() + "_" + call.getName());
		}
		
		

		// call to launch an instance
		call.setAvailabilityZone(ins.getAvailabilityZone());

		final ComputeServices compute = cloudProvider.getComputeServices();
		final VirtualMachineSupport vmSupport = compute.getVirtualMachineSupport();
		final NetworkServices network = cloudProvider.getNetworkServices();
		String instanceType = ins.getInstanceType();
        
		final VMLaunchOptions opts = VMLaunchOptions.getInstance(
				instanceType, ins.getImageId(), ins.getHostname(),
				ins.getHostname(), ins.getHostname());
		opts.withBoostrapKey(ins.getKeyName());
		if (kid != null) {
			opts.getMetaData().put("kernelId", kid);
		}
		if (rid != null) {
			opts.getMetaData().put("ramdiskId", rid);
		}
		if (ins.getUserData() != null) {
			opts.withUserData(ins.getUserData());
		}
		if (securityGroupIds != null) {
			final CommaObject seco = new CommaObject(securityGroupIds);
			opts.behindFirewalls(seco.toArray());
		}
		
		VirtualMachine vm = vmSupport.launch(opts);
		

		// get pvtip address of the launched instance
		logger.info("instance launched " + vm.getProviderVirtualMachineId());
		InstanceUtils.toResource(ins, vm);
		String pvtip = ins.getPrivateIpAddress();
		String publicIp = ins.getPublicIp();
		final String instanceId = ins.getInstanceId();

		call.setResourcesBean(CFUtil.updatePhysicalId(getResourceBeanId(),
				instanceId));

		final long ibid = HibernateUtil.withNewSession(new Operation<Long>() {

			@Override
			public Long ex(final Session s, final Object... args)
					throws Exception {
				final InstanceBean ib = new InstanceBean();
				ib.setUserId(ac.getId());
				ib.setAvzone(ins.getAvailabilityZone());
				ib.setChefRoles(ins.getChefRoles());
				ib.setDatabag(ins.getDatabag());
				ib.setHostname(ins.getHostname());
				ib.setStatus("Pending");
				ib.setHealth("Unhealthy");
				ib.setInstanceId(ins.getPhysicalId());
				s.save(ib);
				return ib.getId();
			}
		});
		logger.info("instance status=Pending");

		logger.info("instance updated in ELB " + ins.getInstanceId());

		final String sallocIp = (String) ConfigurationUtil
				.getConfiguration(Arrays.asList(new String[] {
						"AllocatePublicIP", ins.getAvailabilityZone() }));
		final boolean allocIp = sallocIp != null
				&& sallocIp.equalsIgnoreCase("true") ? true : false;
		logger.debug("allocIp=" + allocIp);

		// if pvtip is not assigned loop thru describe instance until it is
		// assigned

		final String retry = (String) ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { "AWS::EC2::retryCount",
						"AWS::EC2::AllocateAddress" }));
		final int retrycnt = retry == null ? 1 : Integer.parseInt(retry);
		for (int a = 0; a < retrycnt; a++) {
			vm = vmSupport.getVirtualMachine(instanceId);
			if (vm.getPrivateIpAddresses() != null
					&& vm.getPrivateIpAddresses().length > 0) {
				pvtip = vm.getPrivateIpAddresses()[0];
			}
			if (vm.getPublicIpAddresses() != null
					&& vm.getPublicIpAddresses().length > 0) {
				publicIp = vm.getPublicIpAddresses()[0];
			}
			if (pvtip == null
					|| pvtip.length() == 0
					|| pvtip.equals("0.0.0.0")
					|| !allocIp
					&& (publicIp == null || publicIp.length() == 0
							|| publicIp.equals("0.0.0.0") || pvtip
							.equals(publicIp))) {
				logger.debug("sleeping to get pvt ip");
				Thread.sleep(10 * 1000);
			} else {
				break;
			}
		}
		logger.debug("pvtip found for instance:" + pvtip);

		// find out if a new public ip address need to be allocated for an
		// instance for the availability zoen or is it automatucally assigned.
		// Eucalyptus zones are automatically assiged and Openstack zones we
		// need to assign it separately
		String publicIpId = null;
		if (allocIp) {
			// allocate public ip
			final IpAddressSupport ipsupport = network.getIpAddressSupport();
			publicIpId = ipsupport.request(IPVersion.IPV4);
			final IpAddress addrs = ipsupport.getIpAddress(publicIpId);
			publicIp = addrs.getAddress();
			ipsupport.assign(publicIpId, instanceId);
			ins.setPublicIp(publicIp);
		} else {
			vm = vmSupport.getVirtualMachine(instanceId);
			publicIp = vm.getPublicIpAddresses()[0];
		}
		final String publicIpId0 = publicIpId;
		final String publicIp0 = publicIp;
		final String pvtip0 = pvtip;
		HibernateUtil.withNewSession(new Operation<Object>() {

			@SuppressWarnings("unchecked")
			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {
				final Query q = s.createQuery("from InstanceBean where id="
						+ ibid);
				final List<InstanceBean> l = q.list();
				if (l.size() > 0) {
					logger.debug("adding instanceBean ");
					final InstanceBean ib = l.get(0);
					ib.setPublicIpId(publicIpId0);
					ib.setPublicIp(publicIp0);
					ib.setPrivateIp(pvtip0);
					ib.setStatus("InService");
					ib.setHealth("Healthy");
					s.save(ib);
					ins.setStatus(ib.getStatus());
					logger.info("Instance created " + ins.getInstanceId() + " "
							+ " " + ib.getChefRoles() + " " + ib.getDatabag());
				}
				return null;
			}
		});
		ins.setPublicIp(publicIp);
		ins.setPrivateIpAddress(pvtip);
		return ins;
	}

	@Override
	public Resource delete(final CallStruct call) throws Exception {

		logger.info("delete " + call.toString());
		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {
				final String id = call.getPhysicalId();
				final InstanceBean ib = InstanceUtil.getInstance(s, id);
				if (ib != null) {
					ib.setStatus("Terminating");
					ib.setHealth("Unhealthy");
					s.save(ib);
				}
				return null;
			}
		});

		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {
				// get instance to delete
				final AccountType ac = call.getAc();
				// final BasicAWSCredentials cred = new BasicAWSCredentials(ac
				// .getAccessKey(), ac.getSecretKey());
				final String id = call.getPhysicalId();
				final InstanceBean ib = InstanceUtil.getInstance(s, id);
				if (ib != null) {
					final String publicIp = ib.getPublicIpId();

					// if chef instance delete chef roles
					if (ib.getChefRoles() != null) {
						try {
							ChefUtil.deleteClient(ib.getHostname());
							ChefUtil.deleteNode(ib.getHostname());
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}

					call.setAvailabilityZone(ib.getAvzone());
					final CloudProvider cloudProvider = call.getCloudProvider();
					final ComputeServices compServ = cloudProvider
							.getComputeServices();
					final VirtualMachineSupport vmServ = compServ
							.getVirtualMachineSupport();

					// terminate instance
					try {
						vmServ.terminate(id);
					} catch (final Exception e) {
						e.printStackTrace();
					}
					logger.info("instance terminated " + id);

					// if public ip was separately allocated for this instance,
					// then
					// dissasociate and release it
					try {
						final String allocIp = (String) ConfigurationUtil
								.getConfiguration(Arrays.asList(new String[] {
										"AllocatePublicIP", ib.getAvzone() }));
						if (allocIp.equalsIgnoreCase("true")) {
							logger.debug("releasing associcated ip");
							final NetworkServices netServ = cloudProvider
									.getNetworkServices();

							final IpAddressSupport ipServ = netServ
									.getIpAddressSupport();
							ipServ.releaseFromServer(publicIp);
							ipServ.releaseFromPool(publicIp);
						}
					} catch (final Exception e) {
						e.printStackTrace();
					}

					// wait for the instance to be deleted if requested
					logger.debug("WaitForTermination requested by user = true");
					for (int i = 10; i > 0; i--) {
						logger.debug("Waiting for the instance to be terminated.");
						try {
							// Eucalyptus just returns an empty result or empty
							// reservation
							final VirtualMachine vm = vmServ
									.getVirtualMachine(id);
							logger.debug("DescribeInstances with "
									+ vm.getCurrentState().toString());
							if (vm.getCurrentState().equals(VmState.TERMINATED)) {
								logger.debug("InstanceNotFound is no longer running.");
								break;
							}
						} catch (final Exception e) {
							break;
						}
						Thread.sleep(5000);
					}

					// delete instance record
					s.delete(ib);
					logger.info("instance deleted " + ib.getInstanceId());
				}

				// delete resource bean records for the instance from stack
				// resource hierarchy
				CFUtil.deleteResourceRecords(s, ac.getId(), call.getStackId(),
						id, null);
				CFUtil.deleteResourceRecords(s, ac.getId(), call.getStackId(),
						null, id);
				return null;
			}
		});
		return null;
	}

	@Override
	protected boolean isLicenseRequired() {
		return true;
	}

	public Resource reboot(final CallStruct call) throws Exception {
		logger.info("reboot " + call.toString());
		final CloudProvider cloudProvider = call.getCloudProvider();
		final ComputeServices compServ = cloudProvider.getComputeServices();
		final VirtualMachineSupport vmServ = compServ
				.getVirtualMachineSupport();
		vmServ.reboot(call.getPhysicalId());
		return null;
	}
}
