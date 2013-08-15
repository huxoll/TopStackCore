package com.msi.tough.dasein;

import org.junit.Test;

public class VSphereTest {
	@Test
	public void testConnection() throws Exception {
		// final String accessKey = "Administrator";
		// final String secretKey = "M0mentum3I";
		// // final String endPoint =
		// // "http://vcenterserver.momentumsoftware.com";
		// final String endPoint = "https://172.16.5.12/sdk";
		// // final String endPoint = "https://172.16.5.12";
		// // final String cloud = "essex";
		// final String providerName = "VMware";
		// final String region = "CloudLab";
		// // final String account = "f431e192e2bd49dd94fe614216c350f4";
		//
		// final ProviderLoader loader = new ProviderLoader();
		// final Map<String, String> prop = MapUtil.create("DSN_PROVIDER_CLASS",
		// PrivateCloud.class.getName(), "DSN_ENDPOINT", endPoint,
		// "DSN_REGION", region, /* "DSN_ACCOUNT", account, */
		// "DSN_API_SHARED", accessKey, "DSN_API_SECRET", secretKey,
		// "DSN_API_VERSION", "2.0",
		// /*
		// * "DSN_CLOUD_NAME", cloud,
		// */"DSN_CLOUD_PROVIDER", providerName);
		// final CloudProvider provider = loader.getProvider(prop);
		//
		// // final ListImages lister = new ListImages(provider);
		// try {
		// // final ListServers lister = new ListServers(provider);
		// // lister.list();
		// final ComputeServices comp = provider.getComputeServices();
		// final MachineImageSupport imgs = comp.getImageSupport();
		// final Iterable<MachineImage> imgl = imgs.listMachineImages();
		// final Iterator<MachineImage> itr = imgl.iterator();
		// while (itr.hasNext()) {
		// final MachineImage i = itr.next();
		// }
		//
		// // final VirtualMachineSupport vms =
		// // comp.getVirtualMachineSupport();
		// // final Iterable<VirtualMachine> vml = vms.listVirtualMachines();
		// // final VirtualMachine vm = vml.iterator().next();
		// // vms.getVirtualMachine(vml.iterator().next());
		// } finally {
		// provider.close();
		// }
	}
}
