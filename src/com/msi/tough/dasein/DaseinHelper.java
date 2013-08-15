package com.msi.tough.dasein;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.compute.ComputeServices;
import org.dasein.cloud.compute.MachineImage;
import org.dasein.cloud.compute.MachineImageSupport;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VirtualMachineSupport;

import com.msi.tough.cf.AccountType;
import com.msi.tough.utils.ConfigurationUtil;

public class DaseinHelper {
	public static MachineImage getImage(final CloudProvider cloudProvider,
			final String machineImageId) throws Exception {
		final ComputeServices compute = cloudProvider.getComputeServices();
		final MachineImageSupport imageServ = compute.getImageSupport();
		return imageServ.getImage(machineImageId);
	}

	public static VirtualMachine getInstance(final CloudProvider cloudProvider,
			final String instanceId) throws Exception {
		final ComputeServices compute = cloudProvider.getComputeServices();
		final VirtualMachineSupport vmServ = compute.getVirtualMachineSupport();
		final VirtualMachine vm = vmServ.getVirtualMachine(instanceId);
		return vm;
	}

	public static CloudProvider getProvider(final String availabilityZone,
			final AccountType ac) throws Exception {
		return getProvider(availabilityZone == null ? ac.getDefZone()
				: availabilityZone, ac.getTenant(), ac.getAccessKey(),
				ac.getSecretKey());
	}

	public static CloudProvider getProvider(final String availabilityZone,
			final String tenant, final String accessKey, final String secretKey)
			throws Exception {
	    // Provider class is dependent only on cloud type, not on AVZ.
        final String cloudType = (String) ConfigurationUtil
                .getConfiguration(Arrays.asList(new String[] {
                        "CloudType", availabilityZone }));
        final String key = "cloud."+cloudType;
		final String cname = (String) ConfigurationUtil
				.getConfiguration(Arrays.asList(new String[] {
						"DSN_PROVIDER_CLASS", key }));
        final String providerName = (String) ConfigurationUtil
                .getConfiguration(Arrays.asList(new String[] {
                        "DSN_CLOUD_PROVIDER", key }));
		final String endpoint = (String) ConfigurationUtil
				.getConfiguration(Arrays.asList(new String[] { "DSN_ENDPOINT",
						availabilityZone }));
		final String regionId = (String) ConfigurationUtil
				.getConfiguration(Arrays.asList(new String[] { "DSN_REGION",
						availabilityZone }));
		final String version = (String) ConfigurationUtil
				.getConfiguration(Arrays.asList(new String[] {
						"DSN_API_VERSION", availabilityZone }));
		final String cloudName = (String) ConfigurationUtil
				.getConfiguration(Arrays.asList(new String[] {
						"DSN_CLOUD_NAME", availabilityZone }));

		final Map<String, String> prop = new HashMap<String, String>();
		prop.put("DSN_PROVIDER_CLASS", cname);
		prop.put("DSN_ENDPOINT", endpoint);
		if (regionId != null) {
			prop.put("DSN_REGION", regionId);
		}
		if (version != null) {
			prop.put("DSN_API_VERSION", version);
		}
		if (cloudName != null) {
			prop.put("DSN_CLOUD_NAME", cloudName);
		}
		prop.put("DSN_CLOUD_PROVIDER", providerName);
		prop.put("DSN_ACCOUNT", tenant);
		prop.put("DSN_API_SHARED", accessKey);
		prop.put("DSN_API_SECRET", secretKey);
		return new ProviderLoader().getProvider(prop);
	}
}
