package com.msi.tough.engine.aws.elasticbeanstalk;

import org.slf4j.Logger;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.core.Appctx;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;

public class Environment extends BaseProvider {
	private static Logger logger = Appctx
			.getLogger(Environment.class.getName());
	public static String TYPE = "AWS::ElasticBeanstalk::Environment";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		logger.debug("Preparing to execute Environment launch...");
		// final AccountType ac = call.getAc();
		// final String name = call.getName();
		//
		// final String applicationName = (String) call
		// .getProperty("ApplicationName");
		// final String cnamePrefix = (String) call.getProperty("CNAMEPrefix");
		// final List<OptionSettingsType> optionSettings = EBSUtil
		// .toOptionSettingsList(call.getProperty("OptionSettings"));
		// final List<OptionSettingsType> optionsToRemove = EBSUtil
		// .toOptionSettingsList(call.getProperty("OptionsToRemove"));
		// String solutionStackName = (String) call
		// .getRequiredProperty("SolutionStackName");
		// final String templateName = (String) call
		// .getRequiredProperty("TemplateName");
		// final String versionLabel = (String)
		// call.getProperty("VersionLabel");
		// final String desc = (String) call.getProperty("Description");
		//
		// String chefRole = null;
		// if (solutionStackName != null
		// && (solutionStackName
		// .equals("32bit Amazon Linux running Tomcat 7") || solutionStackName
		// .equals("64bit Amazon Linux running Tomcat 7"))) {
		// logger.debug("Tomcat 7 is chosen as a part of the stack.");
		// chefRole = "transcend_ebs_tomcat7";
		// } else {
		// chefRole = "transcend_ebs_tomcat6";
		// logger.debug("Tomcat 6 is chosen as a part of the stack.");
		// }
		//
		// logger.debug("Properties" + "\n" + "Account(required): " + ac.getId()
		// + "\n" + "Name(required): " + name + "\n" + "ApplicationName: "
		// + applicationName + "\n" + "CNAMEPrefix: " + cnamePrefix + "\n"
		// + "SolutionStack: " + solutionStackName + "\n"
		// + "TemplateName: " + templateName + "\n" + "VersionLabel: "
		// + versionLabel + "\n" + "ChefRole: " + chefRole + "\n");
		//
		// logger.debug("Checking the database to see if OptionSettings and OptionsToRemove have been issued...\n"
		// + "Checking for OptionSettingsBean with the name of "
		// + "transcend-elasticbeanstalk-optionsettings-"
		// + ac.getId()
		// + "-"
		// + name
		// + "-"
		// + applicationName
		// + "\n"
		// + "Checking for OptionSettingsBean with the name of "
		// + "transcend-elasticbeanstalk-optionstoremove-"
		// + ac.getId()
		// + "-" + name + "-" + applicationName);
		//
		// logger.debug("Getting details about the application version...");
		// final AccountBean ab = AccountUtil.readAccount(s, ac.getId());
		// final ApplicationBean apb = AccountUtil.readApplication(s, ab,
		// applicationName);
		// VersionBean vb = AccountUtil.readApplicationVersion(s, apb,
		// versionLabel);
		// if (vb == null && apb.getVersions() != null) {
		// vb = AccountUtil.readLatestApplicationVersion(s, apb);
		// }
		// if (vb == null) {
		// logger.debug("No version exists in the chosen application.");
		// // TODO figure out what error/exception is thrown by AWS in this
		// // case and do the same
		// }
		//
		// logger.debug("Processing OptionSettings and OptionsToRemove...");
		// ConfigTemplateBean ctb = null;
		// if (templateName != null) {
		// ctb = EBSUtil.readTemplate(s, ac.getId(), applicationName, null,
		// null, templateName).get(0);
		// } else {
		// if (solutionStackName == null) {
		// solutionStackName = "32bit Amazon Linux running Tomcat 6";
		// }
		// ctb = EBSUtil.readTemplate(s, 0, null, null, solutionStackName,
		// null).get(0);
		// }
		//
		// final String databagName = "__ebs-" + ac.getId() + "-" + name;
		// final String lbName = name + "_lb";
		// final String asgName = name + "_asg";
		// final String lcfgName = name + "_lcfg";
		// final String upPolicyName = name + "_up";
		// final String downPolicyName = name + "_down";
		// final String upAlarmName = name + "_upAlarm";
		// final String downAlarmName = name + "_downAlarm";
		//
		// final EnvironmentBean evb = new EnvironmentBean();
		// evb.setResourcesStack(call.getStackId());
		// evb.setDatabag(databagName);
		// evb.setAsGroup(asgName);
		// evb.setLaunchConfig(lcfgName);
		// evb.setLoadBalancer(lbName);
		// evb.setUserId(ac.getId());
		// evb.setApplicationName(applicationName);
		// evb.setName(name);
		// evb.setDesc(desc);
		// evb.setDnsPrefix(cnamePrefix);
		// evb.setStack(solutionStackName);
		// evb.setTemplate(templateName);
		// evb.setVersion(versionLabel);
		// evb.setCreatedTime(new Date());
		// evb.setUpdatedTime(new Date());
		// evb.setStatus("Launching");
		// evb.setHealth("grey");
		// s.save(evb);
		//
		// final Set<ConfigBean> newOpts =
		// EBSUtil.copyConfigs(ctb.getConfigs());
		// EBSUtil.updateConfigs(newOpts, optionSettings);
		// EBSUtil.removeConfigs(newOpts, optionsToRemove);
		// EBSUtil.saveConfigs(s, ac.getId(), newOpts, applicationName, name,
		// solutionStackName, templateName);
		//
		// boolean databagCreated = false;
		// final EBSDataBag databag = new EBSDataBag();
		// databag.setAcId("" + ac.getId());
		// databag.setPostWaitUrl((String) ConfigurationUtil
		// .getConfiguration(Arrays
		// .asList(new String[] { "TRANSCEND_URL" })));
		//
		// databag.setStackId(call.getStackId());
		// databag.addS3Data(ac.getDefKeyName(), "" + ac.getId(),
		// ac.getSecretKey(), vb.getS3bucket(), vb.getS3key());
		//
		// logger.debug(templateName + " template is being used.");
		// for (final ConfigBean config : newOpts) {
		// databag.addOption(
		// new OptionSetting(config.getNameSpace(),
		// config.getOption(), config.getValue()), false);
		// }
		//
		// databagCreated = EBSQueryUtil.createEBSDatabag(databagName, databag);
		// logger.debug("Databag: \n"
		// + JsonUtil.toJsonPrettyPrintString(JsonUtil.load(databag
		// .toJsonString())));
		//
		// if (!databagCreated) {
		// logger.debug("Data Bag could not be created... Internal Failure!");
		// // throw ElasticBeanStalkQueryFaults.internalFailure();
		// }
		//
		// final String avz = (String) ConfigurationUtil.getConfiguration(Arrays
		// .asList(new String[] { "ElasticacheAvailabilityZone" }));
		//
		// String imageId = databag.getOptionValue("ImageId");
		// if (imageId == null) {
		// logger.debug("ImageId info is not found in the databag; using the default.");
		// imageId = (String) ConfigurationUtil.getConfiguration(Arrays
		// .asList(new String[] { "ImageId", avz, "Elastcache" }));
		// }
		// String instanceType = databag.getOptionValue("InstanceType");
		// if (instanceType == null) {
		// logger.debug("InstanceType info is not found in the databag; using the default.");
		// instanceType = (String) ConfigurationUtil.getConfiguration(Arrays
		// .asList(new String[] { "ElasticacheInstanceType", avz }));
		// }
		// String availabilityZone = databag
		// .getOptionValue("Custom Availability Zones");
		// if (availabilityZone == null) {
		// logger.debug("AvailabilityZone info is not found in the databag; using the default.");
		// availabilityZone = avz;
		// }
		// final String kernelId = (String) ConfigurationUtil
		// .getConfiguration(Arrays
		// .asList(new String[] { "KernelId", avz }));
		// final String ramdiskId = (String) ConfigurationUtil
		// .getConfiguration(Arrays
		// .asList(new String[] { "RamdiskId", avz }));
		//
		// // create LB
		// LoadBalancerType lb = null;
		// {
		// final Map<String, Object> prop = new HashMap<String, Object>();
		// prop.put("AvailabilityZones", avz);
		// final List<ListenerType> listeners = new ArrayList<ListenerType>();
		// final ListenerType listener = new ListenerType();
		// listener.setLoadBalancerPort("8080");
		// listener.setInstancePort("8080");
		// listener.setProtocol("http");
		// listeners.add(listener);
		// prop.put("Listeners", listeners);
		//
		// final CallStruct c0 = call.newCall(name);
		// c0.setInternalYN(true);
		// c0.setProperties(prop);
		// c0.setType("AWS::ElasticLoadBalancing::LoadBalancer");
		// c0.setName(lbName);
		// lb = (LoadBalancerType) CFUtil.createResource(s, c0);
		// }
		//
		// // create AS LaunchConfig
		// LaunchConfiguartionType launchConfig = null;
		// {
		// final Map<String, Object> prop = new HashMap<String, Object>();
		// prop.put("ImageId", imageId);
		// prop.put("InstanceType", instanceType);
		// prop.put("KernelId", kernelId);
		// prop.put("RamdiskId", ramdiskId);
		// prop.put("KeyName", ac.getDefKeyName());
		// prop.put("SecurityGroups", ac.getDefSecurityGroups());
		// // prop.put("UserData", ramdiskId);
		// prop.put("ChefRoles", chefRole);
		// prop.put("Databag", databagName);
		// prop.put("WaitHookClass", ASWaitHook.class.getName());
		//
		// final CallStruct c0 = call.newCall(name);
		// c0.setName(lcfgName);
		// c0.setInternalYN(true);
		// c0.setProperties(prop);
		// c0.setType("AWS::AutoScaling::LaunchConfiguration");
		// launchConfig = (LaunchConfiguartionType) CFUtil.createResource(s,
		// c0);
		// }
		//
		// // create ASG
		// AutoScalingGroupType asg = null;
		// {
		// final Map<String, Object> prop = new HashMap<String, Object>();
		// prop.put("AvailabilityZones", avz);
		// prop.put("Cooldown", "300");
		// prop.put("DesiredCapacity", "1");
		// prop.put("HealthCheckGracePeriod", "1");
		// prop.put("HealthCheckType", "1");
		// prop.put("LaunchConfigurationName", lcfgName);
		// prop.put("LoadBalancerNames", lbName);
		// prop.put("MaxSize", "3");
		// prop.put("MinSize", "1");
		// // prop.put("NotificationConfiguration", "");
		// // prop.put("Tags");
		// final CallStruct c0 = call.newCall(name);
		// c0.setName(asgName);
		// c0.setInternalYN(true);
		// c0.setProperties(prop);
		// c0.setType(AutoScalingGroup.TYPE);
		// asg = (AutoScalingGroupType) CFUtil.createResource(s, c0);
		// }
		//
		// // create ASPolicy up
		// {
		// final Map<String, Object> prop = new HashMap<String, Object>();
		// prop.put("AdjustmentType", "ChangeInCapacity");
		// prop.put("AutoScalingGroupName", asgName);
		// prop.put("ScalingAdjustment", "1");
		// final CallStruct c0 = call.newCall(name);
		// c0.setName(upPolicyName);
		// c0.setInternalYN(true);
		// c0.setProperties(prop);
		// c0.setType(ScalingPolicy.TYPE);
		// final ScalingPolicyType r = (ScalingPolicyType) CFUtil
		// .createResource(s, c0);
		// }
		//
		// // create ASPolicy down
		// {
		// final Map<String, Object> prop = new HashMap<String, Object>();
		// prop.put("AdjustmentType", "ChangeInCapacity");
		// prop.put("AutoScalingGroupName", asgName);
		// prop.put("ScalingAdjustment", "-1");
		// final CallStruct c0 = call.newCall(name);
		// c0.setName(downPolicyName);
		// c0.setInternalYN(true);
		// c0.setProperties(prop);
		// c0.setType(ScalingPolicy.TYPE);
		// final ScalingPolicyType r = (ScalingPolicyType) CFUtil
		// .createResource(s, c0);
		// }
		//
		// // create Alarm up
		// {
		// final Map<String, Object> prop = new HashMap<String, Object>();
		// prop.put("ActionsEnabled", "true");
		// prop.put("AlarmActions",
		// Arrays.asList(new String[] { upPolicyName }));
		// prop.put("ComparisonOperator", "GreaterThanThreshold");
		// final List<MetricDimensionType> dimensions = new
		// ArrayList<MetricDimensionType>();
		// final MetricDimensionType dim = new MetricDimensionType();
		// dim.setName("AutoScalingGroup");
		// dim.setValue(asgName);
		// dimensions.add(dim);
		// prop.put("Dimensions", dimensions);
		// prop.put("EvaluationPeriods", "1");
		// // final List<String> insufficientDataActions = CFUtil
		// // .toStringList(call.getProperty("InsufficientDataActions"));
		// // final List<String> okActions = CFUtil.toStringList(call
		// // .getProperty("OKActions"));
		// // prop.put("MetricName", "CPUUtilization");
		// // prop.put("Namespace", "MSI/EC2");
		// // prop.put("Period", "60");
		// // prop.put("Statistic", "Average");
		// // prop.put("Threshold", "40");
		// // prop.put("Unit", "Percent");
		// prop.put("MetricName", "Latency");
		// prop.put("Namespace", "MSI/EC2");
		// prop.put("Period", "60");
		// prop.put("Statistic", "Average");
		// prop.put("Threshold", "3");
		// prop.put("Unit", "Second");
		// final CallStruct c0 = call.newCall(name);
		// c0.setName(upAlarmName);
		// c0.setInternalYN(true);
		// c0.setProperties(prop);
		// c0.setType(Alarm.TYPE);
		// final AlarmType r = (AlarmType) CFUtil.createResource(s, c0);
		// }
		//
		// // create Alarm down
		// {
		// final Map<String, Object> prop = new HashMap<String, Object>();
		// prop.put("ActionsEnabled", "true");
		// prop.put("AlarmActions",
		// Arrays.asList(new String[] { downPolicyName }));
		// prop.put("ComparisonOperator", "LessThanThreshold");
		// final List<MetricDimensionType> dimensions = new
		// ArrayList<MetricDimensionType>();
		// final MetricDimensionType dim = new MetricDimensionType();
		// dim.setName("AutoScalingGroup");
		// dim.setValue(asgName);
		// dimensions.add(dim);
		// prop.put("Dimensions", dimensions);
		// prop.put("EvaluationPeriods", "1");
		// // final List<String> insufficientDataActions = CFUtil
		// // .toStringList(call.getProperty("InsufficientDataActions"));
		// // final List<String> okActions = CFUtil.toStringList(call
		// // .getProperty("OKActions"));
		// prop.put("MetricName", "CPUUtilization");
		// prop.put("Namespace", "MSI/EC2");
		// prop.put("Period", "60");
		// prop.put("Statistic", "Average");
		// prop.put("Threshold", "1");
		// prop.put("Unit", "Percent");
		// final CallStruct c0 = call.newCall(name);
		// c0.setName(downAlarmName);
		// c0.setInternalYN(true);
		// c0.setProperties(prop);
		// c0.setType(Alarm.TYPE);
		// final AlarmType r = (AlarmType) CFUtil.createResource(s, c0);
		// }
		//
		// final EnvironmentType ret = new EnvironmentType();
		// ret.setApplicationName(applicationName);
		// ret.setCnamePrefix(cnamePrefix);
		// ret.setCreatedTime(new Date());
		// ret.setDesc(desc);
		// ret.setName(name);
		// ret.setOptionSettings(optionSettings);
		// ret.setOptionsToRemove(optionsToRemove);
		// ret.setSolutionStackName(solutionStackName);
		// ret.setTemplateName(templateName);
		// ret.setUpdatedTime(new Date());
		// ret.setVersionLabel(versionLabel);
		// logger.debug("Environment created " + name);
		// return ret;
		return null;
	}

	@Override
	public Resource delete(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		final String name = call.getPhysicalId();
		// final List<EnvironmentBean> leb = EBSUtil.selectEnvironments(s,
		// ac.getId(), null, null, name, null);
		// final EnvironmentBean eb = leb.get(0);
		// CFUtil.deleteStackResources(s, ac.getId(), call.getStackId(), name,
		// null);
		// ChefUtil.deleteDatabag(eb.getDatabag());
		// CFUtil.deleteResourceRecords(s, ac.getId(), call.getStackId(), name,
		// null);
		// CFUtil.deleteResourceRecords(s, ac.getId(), call.getStackId(), null,
		// name);
		// s.delete(eb);
		// logger.debug("Environment deleted " + name);
		return null;
	}

	@Override
	protected String waitHookClazz() {
		return EnvironmentWaitHook.class.getName();
	}
}
