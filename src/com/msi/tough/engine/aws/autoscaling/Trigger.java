package com.msi.tough.engine.aws.autoscaling;

import com.msi.tough.cf.CFType;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;

public class Trigger extends BaseProvider {
	@Override
	public CFType create0(final CallStruct call) throws Exception {
		// final Account ac = (Account) task.getRequiredProperty("__ACCOUNT__");
		// final String name = (String) task.getRequiredProperty("__NAME__");
		// final String applicationName = (String) task
		// .getProperty("ApplicationName");
		// final String cnamePrefix = (String) task.getProperty("CNAMEPrefix");
		// final Object optionSettings = task.getProperty("OptionSettings");
		// final Object optionsToRemove = task.getProperty("OptionsToRemove");
		// final String solutionStackName = (String) task
		// .getProperty("SolutionStackName");
		// final String templateName = (String) task
		// .getRequiredProperty("TemplateName");
		// final String versionLabel = (String)
		// task.getProperty("VersionLabel");
		// final String chefRole = "role[transcend_ebs_" + solutionStackName +
		// "]";
		//
		// final String databag = "transcend-ebs-" + ac.getId() + "-" + name;
		//
		// final String avz = (String) ConfigurationUtil.getConfiguration(Arrays
		// .asList(new String[] { "ElasticacheAvailabilityZone" }));
		//
		// final AccountBean acb = AccountUtil.readAccount(s, ac.getId());
		//
		// final Map<String, Object> prop = task.getChildProperties();
		// final MapResource nameResource = new MapResource();
		// final String resourceName = name + "_instances";
		// nameResource.put("Id", resourceName);
		// prop.put(
		// "ImageId",
		// ConfigurationUtil.getConfiguration(Arrays.asList(new String[] {
		// "ImageId", avz, "RDS" })));
		// prop.put(
		// "InstanceType",
		// ConfigurationUtil.getConfiguration(Arrays.asList(new String[] {
		// "ElasticacheInstanceType", avz })));
		// prop.put("AvailabilityZone", avz);
		// prop.put(
		// "KernelId",
		// ConfigurationUtil.getConfiguration(Arrays.asList(new String[] {
		// "KernelId", avz, "RDS" })));
		// prop.put("KeyName", ac.getDefKeyName());
		// prop.put(
		// "RamdiskId",
		// ConfigurationUtil.getConfiguration(Arrays.asList(new String[] {
		// "RamdiskId", avz, "RDS" })));
		// prop.put("__INTERNAL_YN__", "Y");
		// prop.put("__ACID__", "" + ac.getId());
		// final String hostname = InstanceUtil.getHostName(avz);
		// prop.put("__HOSTNAME__", hostname);
		// prop.put("__CHEF_ROLES__", chefRole);
		// prop.put("__DATABAG__", databag);
		// prop.put("__TYPE__", "AWS::EC2::Instance");
		//
		// final String userdata = TemplateHelper.processFile(
		// "ChefInstanceUserdata", prop);
		// final JsonNode userload = JsonUtil.load(userdata);
		// final String userconv = template.getValue(prop, userload).toString();
		// final byte[] b64 = Base64.encode(userconv.getBytes());
		// prop.put("UserData", new String(b64));
		//
		// final List<Resource> insts = BaseProvider.ex(s, template, prop,
		// null);
		//
		// final MapResource ret = new MapResource();
		// ret.put("Instances", insts);
		// ret.put("Id", "aws:rds:" + StringHelper.randomStringFromTime());
		// return Arrays.asList(new Resource[] { ret });
		return null;
	}

	@Override
	protected boolean isResource() {
		return false;
	}
}
