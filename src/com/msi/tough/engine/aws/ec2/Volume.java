package com.msi.tough.engine.aws.ec2;

import java.text.ParseException;
import java.util.Arrays;

import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.compute.ComputeServices;
import org.dasein.cloud.compute.VolumeCreateOptions;
import org.dasein.cloud.compute.VolumeState;
import org.dasein.cloud.compute.VolumeSupport;
import org.dasein.util.uom.storage.Gigabyte;
import org.dasein.util.uom.storage.Storage;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DetachVolumeRequest;
import com.amazonaws.services.ec2.model.DetachVolumeResult;
import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.ec2.VolumeAttachmentType;
import com.msi.tough.cf.ec2.VolumeType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.utils.ConfigurationUtil;
import com.msi.tough.utils.Constants;

public class Volume extends BaseProvider {
	private static Logger logger = Appctx.getLogger(Volume.class.getName());
	public static String TYPE = "AWS::EC2::Volume";

	public static VolumeAttachmentType attach(final CallStruct call)
			throws Exception {
		final AccountType ac = call.getAc();
		final String instanceId = (String) call.getProperty("InstanceId");
		final String volumeId = (String) call.getProperty("VolumeId");
		String avZone = (String) call.getProperty("AvailabilityZone");
		if (avZone == null) {
			avZone = call.getAvailabilityZone();
		}
		if (avZone == null) {
			avZone = ac.getDefZone();
		}
		final String device = (String) call.getProperty("Device");
		logger.debug("InstanceId = " + instanceId + "; VolumeId = " + volumeId
				+ "; Device = " + device);
		if(device == null){
			throw ErrorResponse.InternalFailure();
		}

		final CloudProvider cloudProvider = call.getCloudProvider();
		final ComputeServices comp = cloudProvider.getComputeServices();
		final VolumeSupport volserv = comp.getVolumeSupport();

		logger.debug("Check the status of the volume...");
		int failCount = 0;
		boolean available = false;
		org.dasein.cloud.compute.Volume dvrRes = null;
		while (failCount < 10 && !available) {
			dvrRes = describe(call);
			final VolumeState state = dvrRes.getCurrentState();
			logger.debug("Current volume state: " + state);
			if (state.compareTo(VolumeState.AVAILABLE) == 0) {
				available = true;
				break;
			}
			++failCount;
			try {
				Thread.sleep(5000);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!available) {
			logger.error("Volume was never set to available state...");
		}

		try {
			try {
				volserv.attach(volumeId, instanceId, device);
			} catch (final Exception e) {
				logger.error(e.getClass().getName() + " is caught.");
				throw new ParseException(e.getMessage(), 0);
			}
		} catch (final ParseException e) {
			logger.error(e.getClass().getName()
					+ " is caught, but this can be ignored since it doesn't affect the process.");
		}

		// wait to make sure that the volume is attached before proceeding
		logger.debug("Check the status of the volume...");
		failCount = 0;
		available = false;
		while (failCount < 10 && !available) {
			dvrRes = describe(call);
			final VolumeState state = dvrRes.getCurrentState();
			logger.debug("Current volume state: " + state);
			if (dvrRes.getProviderVirtualMachineId() != null
					&& state.compareTo(VolumeState.PENDING) != 0) {
				available = true;
				break;
			}
			++failCount;
			try {
				Thread.sleep(5000);
			} catch (final InterruptedException e) {
				e.printStackTrace();
				// TODO throw internal error
			}
		}
		if (!available) {
			logger.error("Volume was never set to attached state...");
		}
		final VolumeAttachmentType r = new VolumeAttachmentType();
		r.setName(call.getName());
		return r;
	}

	public static org.dasein.cloud.compute.Volume describe(final CallStruct call)
			throws Exception {
		final CloudProvider cloudProvider = call.getCloudProvider();
		final ComputeServices comp = cloudProvider.getComputeServices();
		final VolumeSupport volserv = comp.getVolumeSupport();
		return volserv.getVolume((String) call.getProperty("VolumeId"));
	}

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final VolumeType ret = new VolumeType();
		final AccountType ac = call.getAc();
		ret.setName(call.getName());
		final String avZone = (String) call
				.getRequiredProperty(Constants.AVAILABILITYZONE);
		ret.setAvailabilityZone(avZone);
		ret.setSize("" + call.getRequiredProperty("Size"));
		ret.setSnapshotId((String) call.getProperty("SnapshotId"));

		call.setAvailabilityZone(avZone);
		final CloudProvider cloudProvider = call.getCloudProvider();
		final ComputeServices comp = cloudProvider.getComputeServices();
		final VolumeSupport volserv = comp.getVolumeSupport();

		final String retry = (String) ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { "AWS::EC2::retryCount", TYPE }));
		final int retrycnt = retry == null ? 1 : Integer.parseInt(retry);
		String vol = null;
		int i = 0;
		for (i = 0; i < retrycnt; i++) {
			try {
				final Storage<Gigabyte> storage = new Storage<Gigabyte>(
						Integer.parseInt(ret.getSize()), Storage.GIGABYTE);
				final VolumeCreateOptions options = VolumeCreateOptions
						.getInstance(storage, "VOL", "VOL");
				options.inDataCenter(avZone);
				vol = volserv.createVolume(options);
				break;
			} catch (final Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		if(retrycnt == i){
			throw ErrorResponse.InternalFailure();
		}
		ret.setVolumeId(vol);
		logger.info("Volume allocated " + ret.getVolumeId());

		HibernateUtil.withNewSession(new Operation<Object>() {
			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {

				final ResourcesBean resBean = getResourceBean(s);
				resBean.setPhysicalId(ret.getVolumeId());
				s.save(resBean);
				return null;
			}
		});
		return ret;
	}

	@Override
	public Resource delete0(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();

		final String volumeId = call.getPhysicalId();
		final CloudProvider cloudProvider = call.getCloudProvider();
		final ComputeServices comp = cloudProvider.getComputeServices();
		final VolumeSupport volserv = comp.getVolumeSupport();
		try {
			final org.dasein.cloud.compute.Volume vol = volserv
					.getVolume(volumeId);
			if (vol != null && vol.getProviderVirtualMachineId() != null) {
				volserv.detach(volumeId);
			}
		} catch (final NullPointerException e) {
			logger.warn("Nullpointer from the dasein api... ignoring the exception.");
		}

		volserv.remove(volumeId);
		logger.info("Volume Deleted " + volumeId);

		return null;
	}

	public void detach(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		final String volumeId = (String) call.getProperty("VolumeId");
		final String avZone = (String) call
				.getProperty(Constants.AVAILABILITYZONE);
		logger.debug("VolumeId: " + volumeId + "; AvailabilityZone: " + avZone);		
		final CloudProvider cloudProvider = call.getCloudProvider();
		final ComputeServices comp = cloudProvider.getComputeServices();
		final VolumeSupport volserv = comp.getVolumeSupport();
		volserv.detach(volumeId);

		// wait to make sure that the volume is available after detach
		logger.debug("Check the status of the volume...");
		int failCount = 0;
		boolean available = false;
		while (failCount < 10 && !available) {
			final org.dasein.cloud.compute.Volume dvrRes = describe(call);
			final VolumeState state = dvrRes.getCurrentState();
			logger.debug("Current volume state: " + state);
			if (state.compareTo(VolumeState.AVAILABLE) == 0 && dvrRes.getProviderVirtualMachineId() == null) {
				available = true;
				break;
			}
			++failCount;
			try {
				Thread.sleep(5000);
			} catch (final InterruptedException e) {
				e.printStackTrace();
				// TODO throw internal error
			}
		}
		if (failCount == 5 && !available) {
			logger.error("Volume was never set to available state...");
			// TODO throw internal error
		}
	}

	@Override
	protected boolean isResource() {
		return true;
	}
}
