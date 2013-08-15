package com.msi.tough.engine.aws.ec2;

import java.util.Arrays;
import java.util.Collection;

import org.dasein.cloud.CloudErrorType;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.network.Direction;
import org.dasein.cloud.network.Firewall;
import org.dasein.cloud.network.FirewallRule;
import org.dasein.cloud.network.FirewallSupport;
import org.dasein.cloud.network.NetworkServices;
import org.dasein.cloud.openstack.nova.os.NovaException;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.ec2.SecurityGroupType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.BaseException;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.ConfigurationUtil;

public class SecurityGroup extends BaseProvider {
	private static Logger logger = Appctx.getLogger(SecurityGroup.class
			.getName());
	public static String TYPE = "AWS::EC2::SecurityGroup";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final String name = call.getName();
		final SecurityGroupType ret = new SecurityGroupType();
		ret.setName(name);
		final String groupDescription = (String) call
				.getRequiredProperty("GroupDescription");
		logger.debug("create name=" + name + " Description=" + groupDescription
				+ " AvailabilityZone = " + call.getAvailabilityZone());

		final CloudProvider cloudProvider = call.getCloudProvider();
		final NetworkServices network = cloudProvider.getNetworkServices();
		final FirewallSupport firewall = network.getFirewallSupport();

		String physicalId = null;
		final Collection<Firewall> sgs = firewall.list();
		for (final Firewall sg : sgs) {
			if (sg.getName().equals(name)) {
				physicalId = sg.getProviderFirewallId();
				break;
			}
		}

		if (physicalId == null) {
			final String retry = (String) ConfigurationUtil
					.getConfiguration(Arrays.asList(new String[] {
							"AWS::EC2::retryCount", TYPE }));
			final int retrycnt = retry == null ? 1 : Integer.parseInt(retry);

			// final CreateSecurityGroupRequest req = new
			// CreateSecurityGroupRequest();
			// req.setGroupName(name);
			// req.setDescription(groupDescription);
			for (int i = 0; i < retrycnt; i++) {
				try {
					// ec2.createSecurityGroup(req);
					physicalId = firewall.create(name, groupDescription);
					break;
				} catch (final CloudException e) {
				    if (e.getErrorType() == CloudErrorType.QUOTA ||
				            e.getProviderCode().contains("Quota")) {
				        throw new BaseException("Insufficient quota to " +
				        		"create security group.");
				    }
				} catch (final Exception e) {
				    if (i < retrycnt) {
				        Thread.sleep(10 * 1000);
				    }
					continue;
				}
			}
		}
		if (physicalId == null) {
		    throw new BaseException("Unable to create security group.");
		}
		Thread.sleep(1000);

		ret.setPhysicalId(physicalId);
		ret.setGroupName(name);
		ret.setGroupDescription(groupDescription);
		if (ret.getPhysicalId() != null) {
			call.setResourcesBean(CFUtil.updatePhysicalId(getResourceBeanId(),
					ret.getPhysicalId()));
		}

		logger.info("Security Group Created " + name);
		return ret;
	}

	@Override
	public Resource delete(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		final String name = call.getPhysicalId();
		logger.debug("Deleting " + ac.getId() + " " + name);

		final CloudProvider cloudProvider = call.getCloudProvider();
		final NetworkServices network = cloudProvider.getNetworkServices();
		final FirewallSupport firewall = network.getFirewallSupport();
		final String cloudType = ConfigurationUtil.getCloudType(call
				.getAvailabilityZone());
		if (cloudType.equals("Eucalyptus")) {
			// final CallStruct c0 = call.newCall(null);
			// c0.setInternalYN(true);
			// c0.setName(name);
			// final Map<String, Object> prop = new HashMap<String, Object>();
			// prop.put(Constants.AVAILABILITYZONE,
			// call.getProperty(Constants.AVAILABILITYZONE));
			// c0.setProperties(prop);
			// c0.setType(DescribeSecurityGroup.TYPE);
			// final DescribeSecurityGroup provider = new
			// DescribeSecurityGroup();
			// final SecurityGroupType g = (SecurityGroupType)
			// provider.create(c0);
			final Collection<FirewallRule> rules = firewall.getRules(name);
			for (final FirewallRule rule : rules) {
				firewall.revoke(name, Direction.INGRESS, rule.getCidr(),
						rule.getProtocol(), rule.getStartPort(),
						rule.getEndPort());
			}

			// final CloudProvider cloudProvider = call.getCloudProvider();
			// final NetworkServices network =
			// cloudProvider.getNetworkServices();
			// final FirewallSupport firewall = network.getFirewallSupport();
			// final ResourcesBean rb = call.getResourcesBean();
			// final Collection<FirewallRule> rules = firewall.getRules(rb
			// .getPhysicalId());
			// for (final FirewallRule rule : rules) {
			// }
			//
			// for (final AuthorizeSecurityGroupIngressType i : g
			// .getSecurityGroupIngress()) {
			// final CallStruct c1 = c0.newCall(null);
			// c1.setName(name);
			// final Map<String, Object> p1 = new HashMap<String, Object>();
			// p1.put(Constants.AVAILABILITYZONE,
			// call.getProperty(Constants.AVAILABILITYZONE));
			// p1.put(Constants.CIDRIP, i.getCidrIp());
			// p1.put(Constants.FROMPORT, i.getFromPort());
			// p1.put(Constants.TOPORT, i.getToPort());
			// p1.put(Constants.CIDRIP, i.getCidrIp());
			// p1.put(Constants.SOURCESECURITYGROUPNAME,
			// i.getSourceSecurityGroupName());
			// p1.put(Constants.SOURCESECURITYGROUPOWNERID,
			// i.getSourceSecurityGroupOwnerId());
			// c1.setProperties(p1);
			// c1.setType(SecurityGroupIngress.TYPE);
			// final SecurityGroupIngress prov1 = new SecurityGroupIngress();
			// prov1.delete(c1);
			// }
		}

		final Resource res = delete0(call);

		return HibernateUtil.withNewSession(new Operation<Resource>() {

			@Override
			public Resource ex(final Session s, final Object... args)
					throws Exception {

				CFUtil.deleteResourceRecords(s, ac.getId(), call.getStackId(),
						name, null);
				CFUtil.deleteResourceRecords(s, ac.getId(), call.getStackId(),
						null, name);
				logger.info("Deleted " + name);
				return res;
			}
		});
	}

	@Override
	public Resource delete0(final CallStruct call) throws Exception {
		final String retry = (String) ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { "AWS::EC2::retryCount", TYPE }));
		final int retrycnt = retry == null ? 1 : Integer.parseInt(retry);
		final CloudProvider cloudProvider = call.getCloudProvider();
		final NetworkServices network = cloudProvider.getNetworkServices();
		final FirewallSupport firewall = network.getFirewallSupport();
		String callid = call.getName();
		callid = call.getPhysicalId();
		if (call.getResourcesBean() != null && "CREATE_STARTED".equals(
		        call.getResourcesBean().getStatus())) {
		    logger.debug("SecurityGroup delete skipped, create not complete.");
		    return null;
		}
		for (int i = 0; i < retrycnt; i++) {
			try {
				firewall.delete(callid);
				break;
			} catch (final Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(10 * 1000);
				} catch (final InterruptedException e1) {
				}
				continue;
			}
		}
		logger.debug("SecurityGroup Deleted " + call.getPhysicalId() + " "
				+ call.getName());

		return null;
	}

	@Override
	protected boolean isResource() {
		return true;
	}
}
