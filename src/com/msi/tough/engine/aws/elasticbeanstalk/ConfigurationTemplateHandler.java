package com.msi.tough.engine.aws.elasticbeanstalk;

public class ConfigurationTemplateHandler {
	//
	// public static void addConfig(String option, String value, String
	// namespace,
	// ConfigTemplateBean cb,
	// List<ConfigurationOptionSetting> optionSettings) {
	// final ConfigBean config = new ConfigBean();
	// config.setOption(option);
	// config.setValue(value);
	// config.setNameSpace(namespace);
	// AccountUtil.addTemplateConfig(cb, config);
	//
	// final ConfigurationOptionSetting optionSetting = new
	// ConfigurationOptionSetting();
	// optionSetting.setOptionName(option);
	// optionSetting.setValue(value);
	// optionSetting.setNamespace(namespace);
	// optionSettings.add(optionSetting);
	// }

	public static boolean OptionSettingIsValid(final String namespace,
			final String optionName, final String value) {
		// TODO see if namespace needs to be validated or not
		if (optionName.equals("ImageId")) {
			// value must be one of the valid image id
			// if ((!value.equals("ami-0000000c") &&
			// !value.equals("ami-00000008"))
			// || !namespace.equals("aws:autoscaling:launchconfiguration")) {
			// return false;
			// }

		} else if (optionName.equals("Notification Endpoint")) {
			if (!namespace.equals("aws:elasticbeanstalk:sns:topics")) {
				return false;
			}
		} else if (optionName.equals("PARAM1")) {
			// value can be any string
			if (!namespace
					.equals("aws:elasticbeanstalk:application:environment")) {
				return false;
			}
		} else if (optionName.equals("PARAM2")) {
			// value can be any string
			if (!namespace
					.equals("aws:elasticbeanstalk:application:environment")) {
				return false;
			}
		} else if (optionName.equals("PARAM3")) {
			// value can be any string
			if (!namespace
					.equals("aws:elasticbeanstalk:application:environment")) {
				return false;
			}
		} else if (optionName.equals("PARAM4")) {
			// value can be any string
			if (!namespace
					.equals("aws:elasticbeanstalk:application:environment")) {
				return false;
			}
		} else if (optionName.equals("PARAM5")) {
			// value can be any string
			if (!namespace
					.equals("aws:elasticbeanstalk:application:environment")) {
				return false;
			}
		} else if (optionName.equals("JDBC_CONNECTION_STRING")) {

		} else if (optionName.equals("SecurityGroups")) {
			// value must be one of the security groups' name; the group must
			// have port 80 open
			if (!namespace.equals("aws:autoscaling:launchconfiguration")) {
				return false;
			}
		} else if (optionName.equals("UnhealthyThreshold")) {
			// value must be an int
			int v;
			try {
				v = Integer.valueOf(value);
			} catch (final NumberFormatException e) {
				return false;
			}
			if (v < 2 || v > 10 || !namespace.equals("aws:elb:healthcheck")) {
				return false;
			}
		} else if (optionName.equals("InstanceType")) {
			// value must be one of the valid instance type, e.i., c1.medium,
			// etc.
			// if (!value.equals("m1.tiny") && !value.equals("m1.small")
			// && !value.equals("m1.medium") && !value.equals("m1.large")
			// && !value.equals("m1.xlarge")
			// || !namespace.equals("aws:autoscaling:launchconfiguration")) {
			// return false;
			// }
		} else if (optionName.equals("Statistic")) {
			if (!value.equals("Minimum") && !value.equals("Maximum")
					&& !value.equals("Sum") && !value.equals("Average")
					|| !namespace.equals("aws:autoscaling:trigger")) {
				return false;
			}
		} else if (optionName.equals("LoadBalancerHTTPSPort")) {
			// value must be OFF, 443, 8443, or 5443
			// if (!value.equals("OFF") && !value.equals("443")
			// && !value.equals("8443") && !value.equals("5443")
			// || !namespace.equals("aws:elb:loadbalancer")) {
			// return false;
			// }
		} else if (optionName.equals("Stickiness Cookie Expiration")) {
			// value must be an int
			int v;
			try {
				v = Integer.valueOf(value);
			} catch (final NumberFormatException e) {
				return false;
			}
			if (v < 0 || v > 1000000 || !namespace.equals("aws:elb:policies")) {
				return false;
			}
		} else if (optionName.equals("MeasureName")) {
			// value must be one of the Monitor's measurement name, i.e.,
			// NetworkOut
			if (!value.equals("CPUUtilization")
					&& !value.equals("DiskReadBytes")
					&& !value.equals("DiskWriteBytes")
					&& !value.equals("DiskReadOps")
					&& !value.equals("DiskWriteOps")
					&& !value.equals("NetworkIn")
					&& !value.equals("NetworkOut") && !value.equals("Latency")
					&& !value.equals("RequestCount")
					&& !value.equals("HealthyHostCount")
					&& !value.equals("UnhealthyHostCount")
					|| !namespace.equals("aws:autoscaling:trigger")) {
				return false;
			}
		} else if (optionName.equals("Interval")) {
			// value must be an int
			int v;
			try {
				v = Integer.valueOf(value);
			} catch (final NumberFormatException e) {
				return false;
			}
			if (v < 5 || v > 300 || !namespace.equals("aws:elb:healthcheck")) {
				return false;
			}
		} else if (optionName.equals("Application Healthcheck URL")) {
			if (!namespace.equals("aws:elasticbeanstalk:application")) {
				return false;
			}
		} else if (optionName.equals("Notification Topic ARN")) {
			// value must be a string representing a valid TopicArn
			if (!namespace.equals("aws:elasticbeanstalk:sns:topics")) {
				return false;
			}
		} else if (optionName.equals("LowerBreachScaleIncrement")) {
			// value must be an int
			try {
				Integer.valueOf(value);
			} catch (final NumberFormatException e) {
				return false;
			}
			if (!namespace.equals("aws:autoscaling:trigger")) {
				return false;
			}
		} else if (optionName.equals("XX:MaxPermSize")) {
			// value must be an int + 'm' char at the end
			if (value.charAt(value.length() - 1) != 'm') {
				return false;
			}
			try {
				Integer.valueOf(value.substring(0, value.length() - 1));
			} catch (final NumberFormatException e) {
				return false;
			}
		} else if (optionName.equals("UpperBreachScaleIncrement")) {
			// value must be an int
			try {
				Integer.valueOf(value);
			} catch (final NumberFormatException e) {
				return false;
			}
			if (!namespace.equals("aws:autoscaling:trigger")) {
				return false;
			}
		} else if (optionName.equals("MinSize")) {
			// value must be an int
			int v;
			try {
				v = Integer.valueOf(value);
			} catch (final NumberFormatException e) {
				return false;
			}
			if (v < 1 || v > 10000 || !namespace.equals("aws:autoscaling:asg")) {
				return false;
			}
		} else if (optionName.equals("Custom Availability Zones")) {
			// value must be a valid avaliability zone name; "nova" is the only
			// one for us
			if (!value.equals("nova")
					|| !namespace.equals("aws:autoscaling:asg")) {
				return false;
			}
		} else if (optionName.equals("Availability Zones")) {
			if (!value.equals("Any 1") && !value.equals("Any 2")) {
				return false;
			}
		} else if (optionName.equals("LogPublicationControl")) {
			// value must be a boolean
			if (!value.equals("true") && !value.equals("false")
					|| !namespace.equals("aws:elasticbeanstalk:hostmanager")) {
				return false;
			}
		} else if (optionName.equals("JVM Options")) {
			if (!namespace
					.equals("aws:elasticbeanstalk:container:tomcat:jvmoptions")) {
				return false;
			}
		} else if (optionName.equals("Notification Topic Name")) {
			if (!namespace.equals("aws:elasticbeanstalk:sns:topics")) {
				return false;
			}
		} else if (optionName.equals("LoadBalancerHTTPPort")) {
			// if (!value.equals("OFF") && !value.equals("80")
			// && !value.equals("8080")
			// || !namespace.equals("aws:elb:loadbalancer")) {
			// return false;
			// }
		} else if (optionName.equals("Timeout")) {
			// value must be an int
			int v;
			try {
				v = Integer.valueOf(value);
			} catch (final NumberFormatException e) {
				return false;
			}
			if (v < 2 || v > 60 || !namespace.equals("aws:elb:healthcheck")) {
				return false;
			}
		} else if (optionName.equals("BreachDuration")) {
			// value must be an int
			int v;
			try {
				v = Integer.valueOf(value);
			} catch (final NumberFormatException e) {
				return false;
			}
			if (v < 1 || v > 600
					|| !namespace.equals("aws:autoscaling:trigger")) {
				return false;
			}
		} else if (optionName.equals("MonitoringInterval")) {
			if (!value.equals("1 minute") && !value.equals("5 minute")
					|| !namespace.equals("aws:autoscaling:launchconfiguration")) {
				return false;
			}
		} else if (optionName.equals("MaxSize")) {
			// value must be an int
			int v;
			try {
				v = Integer.valueOf(value);
			} catch (final NumberFormatException e) {
				return false;
			}
			if (v < 1 || v > 10000 || !namespace.equals("aws:autoscaling:asg")) {
				return false;
			}
		} else if (optionName.equals("LowerThreshold")) {
			// value must be an int
			int v;
			try {
				v = Integer.valueOf(value);
			} catch (final NumberFormatException e) {
				return false;
			}
			if (v < 0 || v > 20000000
					|| !namespace.equals("aws:autoscaling:trigger")) {
				return false;
			}
		} else if (optionName.equals("AWS_SECRET_KEY")) {
			// value can be any string
		} else if (optionName.equals("AWS_ACCESS_KEY_ID")) {
			// value can be any string
		} else if (optionName.equals("UpperThreshold")) {
			// value must be an int
			int v;
			try {
				v = Integer.valueOf(value);
			} catch (final NumberFormatException e) {
				return false;
			}
			if (v < 0 || v > 20000000
					|| !namespace.equals("aws:autoscaling:trigger")) {
				return false;
			}
		} else if (optionName.equals("Notification Protocol")) {
			// value must be one of the protocols supported by SNS; must match
			// the Notification Protocol's endpoint type; email, sns, sqs, http,
			// etc.
			if (!value.equals("email") && !value.equals("sqs")
					&& !value.equals("http") && !value.equals("https")
					&& !value.equals("email-json")
					|| !namespace.equals("aws:elasticbeanstalk:sns:topics")) {
				return false;
			}
		} else if (optionName.equals("Unit")) {
			// value must be the unit of whatever is in MeasureName option
			// setting
			// TODO further validation may be needed by comparing MeasureName
			if (!value.equals("Seconds") && !value.equals("Percent")
					&& !value.equals("Bytes") && !value.equals("Bits")
					&& !value.equals("Count") && !value.equals("Bytes/Second")
					&& !value.equals("Bits/Second")
					&& !value.equals("Count/Second") && !value.equals("None")
					|| !namespace.equals("aws:autoscaling:trigger")) {
				return false;
			}
		} else if (optionName.equals("Xmx")) {
			// value must be an int + 'm' char at the end
			if (value.charAt(value.length() - 1) != 'm') {
				return false;
			}
			try {
				Integer.valueOf(value.substring(0, value.length() - 1));
			} catch (final NumberFormatException e) {
				return false;
			}
			if (!namespace
					.equals("aws:elasticbeanstalk:container:tomcat:jvmoptions")) {
				return false;
			}
		} else if (optionName.equals("Cooldown")) {
			// value must be an int
			int v;
			try {
				v = Integer.valueOf(value);
			} catch (final NumberFormatException e) {
				return false;
			}
			if (v < 0 || v > 10000 || !namespace.equals("aws:autoscaling:asg")) {
				return false;
			}
		} else if (optionName.equals("Period")) {
			// value must be an int
			int v;
			try {
				v = Integer.valueOf(value);
			} catch (final NumberFormatException e) {
				return false;
			}
			if (v < 1 || v > 600
					|| !namespace.equals("aws:autoscaling:trigger")) {
				return false;
			}
		} else if (optionName.equals("Xms")) {
			// value must be an int + 'm' char at the end
			if (value.charAt(value.length() - 1) != 'm') {
				return false;
			}
			try {
				Integer.valueOf(value.substring(0, value.length() - 1));
			} catch (final NumberFormatException e) {
				return false;
			}
			if (!namespace
					.equals("aws:elasticbeanstalk:container:tomcat:jvmoptions")) {
				return false;
			}
		} else if (optionName.equals("EC2KeyName")) {
			// value can be any string
			if (!namespace.equals("aws:autoscaling:launchconfiguration")) {
				return false;
			}
		} else if (optionName.equals("Stickiness Policy")) {
			// value must be a boolean
			if (!value.equals("true") && !value.equals("false")
					|| !namespace.equals("aws:elb:policies")) {
				return false;
			}
		} else if (optionName.equals("HealthyThreshold")) {
			// value must be an int
			int v;
			try {
				v = Integer.valueOf(value);
			} catch (final NumberFormatException e) {
				return false;
			}
			if (v < 2 || v > 10 || !namespace.equals("aws:elb:healthcheck")) {
				return false;
			}
		} else if (optionName.equals("SSLCertificateId")) {
			// value must be any string
			if (!namespace.equals("aws:elb:loadbalancer")) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}
	//
	// public static void addConfigurations(final ConfigTemplateBean cb,
	// List<ConfigurationOptionSetting> optionSettings) {
	// addConfig("ImageId", "ami-0000000c",
	// "transcend:autoscaling:launchconfiguration", cb, optionSettings);
	// addConfig("Notification Endpoint", null,
	// "transcend:elasticbeanstalk:sns:topics", cb, optionSettings);
	// addConfig("PARAM1", null,
	// "transcend:elasticbeanstalk:application:environment", cb,
	// optionSettings);
	// addConfig("PARAM2", null,
	// "transcend:elasticbeanstalk:application:environment", cb,
	// optionSettings);
	// addConfig("PARAM3", null,
	// "transcend:elasticbeanstalk:application:environment", cb,
	// optionSettings);
	// addConfig("PARAM4", null,
	// "transcend:elasticbeanstalk:application:environment", cb,
	// optionSettings);
	// addConfig("PARAM5", null,
	// "transcend:elasticbeanstalk:application:environment", cb,
	// optionSettings);
	// addConfig("JDBC_CONNECTION_STRING", null,
	// "transcend:elasticbeanstalk:application:environment", cb,
	// optionSettings);
	// addConfig("SecurityGroups", "elasticbeanstalk-default",
	// "transcend:autoscaling:launchconfiguration", cb, optionSettings);
	// addConfig("UnhealthyThreshold", "5", "transcend:elb:healthcheck", cb,
	// optionSettings);
	// addConfig("InstanceType", "m1.tiny",
	// "transcend:autoscaling:launchconfiguration", cb, optionSettings);
	// addConfig("Statistic", "Average", "transcend:autoscaling:trigger", cb,
	// optionSettings);
	// addConfig("LoadBalancerHTTPSPort", "OFF", "transcend:elb:loadbalancer",
	// cb, optionSettings);
	// addConfig("Stickiness Cookie Expiration", "0", "transcend:elb:policies",
	// cb, optionSettings);
	// addConfig("MeasureName", "NetworkOut", "transcend:autoscaling:trigger",
	// cb, optionSettings);
	// addConfig("Interval", "30", "transcend:elb:healthcheck", cb,
	// optionSettings);
	// addConfig("Application Healthcheck URL", "/",
	// "transcend:elasticbeanstalk:application", cb, optionSettings);
	// addConfig("Notification Topic ARN", null,
	// "transcend:elasticbeanstalk:sns:topics", cb, optionSettings);
	// addConfig("LowerBreachScaleIncrement", "-1",
	// "transcend:autoscaling:trigger", cb, optionSettings);
	// addConfig("XX:MaxPermSize", "64m",
	// "transcend:elasticbeanstalk:container:tomcat:jvmoptions", cb,
	// optionSettings);
	// addConfig("UpperBreachScaleIncrement", "1",
	// "transcend:autoscaling:trigger", cb, optionSettings);
	// addConfig("MinSize", "1", "transcend:autoscaling:asg", cb,
	// optionSettings);
	// addConfig("Custom Availability Zones", "nova",
	// "transcend:autoscaling:asg", cb, optionSettings);
	// addConfig("Availability Zones", "Any 1", "transcend:autoscaling:asg", cb,
	// optionSettings);
	// addConfig("LogPublicationControl", "false",
	// "transcend:elasticbeanstalk:hostmanager", cb, optionSettings);
	// addConfig("JVM Options", null,
	// "transcend:elasticbeanstalk:container:tomcat:jvmoptions", cb,
	// optionSettings);
	// addConfig("Notification Topic Name", null,
	// "transcend:elasticbeanstalk:sns:topics", cb, optionSettings);
	// addConfig("LoadBalancerHTTPPort", "80", "transcend:elb:loadbalancer", cb,
	// optionSettings);
	// addConfig("Timeout", "5", "transcend:elb:healthcheck", cb,
	// optionSettings);
	// addConfig("BreachDuration", "2", "transcend:autoscaling:trigger", cb,
	// optionSettings);
	// addConfig("MonitoringInterval", "5 minute",
	// "transcend:autoscaling:launchconfiguration", cb, optionSettings);
	// addConfig("MaxSize", "4", "transcend:autoscaling:asg", cb,
	// optionSettings);
	// addConfig("LowerThreshold", "2000000", "transcend:autoscaling:trigger",
	// cb, optionSettings);
	// addConfig("AWS_SECRET_KEY", null,
	// "transcend:elasticbeanstalk:application:environment", cb,
	// optionSettings);
	// addConfig("AWS_ACCESS_KEY_ID", null,
	// "transcend:elasticbeanstalk:application:environment", cb,
	// optionSettings);
	// addConfig("UpperThresholds", "6000000", "transcend:autoscaling:trigger",
	// cb, optionSettings);
	// addConfig("Notification Protocol", "email",
	// "transcend:elasticbeanstalk:sns:topics", cb, optionSettings);
	// addConfig("Unit", "Bytes", "transcend:autoscaling:trigger", cb,
	// optionSettings);
	// addConfig("Xmx", "256m",
	// "transcend:elasticbeanstalk:container:tomcat:jvmoptions", cb,
	// optionSettings);
	// addConfig("Cooldown", "360", "transcend:autoscaling:asg", cb,
	// optionSettings);
	// addConfig("Period", "1", "transcend:autoscaling:trigger", cb,
	// optionSettings);
	// addConfig("Xms", "256m",
	// "transcend:elasticbeanstalk:container:tomcat:jvmoptions", cb,
	// optionSettings);
	// addConfig("EC2KeyName", null,
	// "transcend:autoscaling:launchconfiguration", cb, optionSettings);
	// addConfig("Stickiness Policy", "false", "transcend:elb:policies", cb,
	// optionSettings);
	// addConfig("HealthyThreshold", "3", "transcend:elb:healthcheck", cb,
	// optionSettings);
	// addConfig("SSLCertificateId", null, "transcend:elb:loadbalancer", cb,
	// optionSettings);
	// }
}
