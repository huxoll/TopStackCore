package com.msi.tough.dasein;

import org.junit.Test;

public class OpenStackLaunchInstanceTest {
	@Test
	public void testConnection() throws Exception {
		// final String accessKey = "rarora";
		// final String secretKey = "m0mentum3i";
		// final String endPoint =
		// "http://essexfe.momentumsoftware.com:5000/v2.0";
		// // final String cloud = "essex";
		// final String providerName = "OpenStack";
		// // final String region = "nova";
		// final String account = "f431e192e2bd49dd94fe614216c350f4";
		//
		// final ProviderLoader loader = new ProviderLoader();
		// final Map<String, String> prop = MapUtil.create("DSN_PROVIDER_CLASS",
		// NovaOpenStack.class.getName(), "DSN_ENDPOINT", endPoint,
		// /* "DSN_REGION", region, */"DSN_ACCOUNT", account,
		// "DSN_API_SHARED", accessKey, "DSN_API_SECRET", secretKey,
		// "DSN_API_VERSION", "2.0",
		// /*
		// * "DSN_CLOUD_NAME", cloud,
		// */"DSN_CLOUD_PROVIDER", providerName);
		// final CloudProvider provider = loader.getProvider(prop);
		//
		// // final ListImages lister = new ListImages(provider);
		// try {
		// final ComputeServices comp = provider.getComputeServices();
		// final VirtualMachineSupport vms = comp.getVirtualMachineSupport();
		// vms.getVirtualMachine("1fe06c62-659c-4ae6-b6e8-81bf5f3b64d6");
		// final VMLaunchOptions opts = VMLaunchOptions.getInstance("4",
		// "af8bb13c-a593-4d02-9aec-1f29b569537a", "host", "host",
		// "host");
		// opts.withBoostrapKey("esxrarora");
		// opts.behindFirewalls(new String[] { "default" });
		// final VirtualMachine vm = vms.launch(opts);
		// } finally {
		// provider.close();
		// }
	}
}
