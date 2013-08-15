package com.msi.tough.elasticbeanstalk.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.hibernate.Session;

import com.amazonaws.services.elasticbeanstalk.model.OptionSpecification;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.model.ConfigBean;
import com.msi.tough.model.ConfigTemplateBean;
import com.msi.tough.utils.AccountUtil;

public class EBSDataBag {
	@JsonProperty("OptionSettings")
	private HashMap<String, OptionSetting> options = null;

	@JsonProperty("SourceBundle")
	private HashMap<String, String> sourceBundle = null;

	@JsonProperty("AcId")
	private String acId;

	@JsonProperty("StackId")
	private String stackId;

	@JsonProperty("PostWaitUrl")
	private String postWaitUrl;

	public EBSDataBag() {
		options = new HashMap<String, OptionSetting>();
	}

	public void addOption(final OptionSetting os, final boolean check) {
		if (check) {
			final int limit = options.size();
			for (int i = 0; i < limit; ++i) {
				final String key = os.getOptionName();
				if (options.containsKey(key)) {
					options.remove(key);
					break;
				}
			}
		}
		options.put(os.getOptionName(), os);
	}

	public void addOptionToRemove(final OptionSpecification os) {
		if (os.getOptionName().equals("ImageId")) {
			addOption(new OptionSetting("ImageId", "ami-0000000c",
					"transcend:autoscaling:launchconfiguration"), true);
		} else if (os.getOptionName().equals("Notification Endpoint")) {
			addOption(new OptionSetting("Notification Endpoint", null,
					"transcend:elasticbeanstalk:sns:topics"), true);
		} else if (os.getOptionName().equals("PARAM1")) {
			addOption(new OptionSetting("PARAM1", null,
					"transcend:elasticbeanstalk:application:environment"), true);
		} else if (os.getOptionName().equals("PARAM2")) {
			addOption(new OptionSetting("PARAM2", null,
					"transcend:elasticbeanstalk:application:environment"), true);
		} else if (os.getOptionName().equals("PARAM3")) {
			addOption(new OptionSetting("PARAM3", null,
					"transcend:elasticbeanstalk:application:environment"), true);
		} else if (os.getOptionName().equals("PARAM4")) {
			addOption(new OptionSetting("PARAM4", null,
					"transcend:elasticbeanstalk:application:environment"), true);
		} else if (os.getOptionName().equals("PARAM5")) {
			addOption(new OptionSetting("PARAM5", null,
					"transcend:elasticbeanstalk:application:environment"), true);
		} else if (os.getOptionName().equals("JDBC_CONNECTION_STRING")) {
			addOption(new OptionSetting("JDBC_CONNECTION_STRING", null,
					"transcend:elasticbeanstalk:application:environment"), true);
		} else if (os.getOptionName().equals("SecurityGroups")) {
			addOption(new OptionSetting("SecurityGroups",
					"elasticbeanstalk-default",
					"transcend:autoscaling:launchconfiguration"), true);
		} else if (os.getOptionName().equals("UnhealthyThreshold")) {
			addOption(new OptionSetting("UnhealthyThreshold", "5",
					"transcend:elb:healthcheck"), true);
		} else if (os.getOptionName().equals("InstanceType")) {
			addOption(new OptionSetting("InstanceType", "m1.tiny",
					"transcend:autoscaling:launchconfiguration"), true);
		} else if (os.getOptionName().equals("Statistic")) {
			addOption(new OptionSetting("Statistic", "Average",
					"transcend:autoscaling:trigger"), true);
		} else if (os.getOptionName().equals("LoadBalancerHTTPSPort")) {
			addOption(new OptionSetting("LoadBalancerHTTPSPort", "OFF",
					"transcend:elb:loadbalancer"), true);
		} else if (os.getOptionName().equals("Stickiness Cookie Expiration")) {
			addOption(new OptionSetting("Stickiness Cookie Expiration", "0",
					"transcend:elb:policies"), true);
		} else if (os.getOptionName().equals("MeasureName")) {
			addOption(new OptionSetting("MeasureName", "NetworkOut",
					"transcend:autoscaling:trigger"), true);
		} else if (os.getOptionName().equals("Interval")) {
			addOption(new OptionSetting("Interval", "30",
					"transcend:elb:healthcheck"), true);
		} else if (os.getOptionName().equals("Application Healthcheck URL")) {
			addOption(new OptionSetting("Application Healthcheck URL", "/",
					"transcend:elasticbeanstalk:application"), true);
		} else if (os.getOptionName().equals("Notification Topic ARN")) {
			addOption(new OptionSetting("Notification Topic ARN", null,
					"transcend:elasticbeanstalk:sns:topics"), true);
		} else if (os.getOptionName().equals("LowerBreachScaleIncrement")) {
			addOption(new OptionSetting("LowerBreachScaleIncrement", "-1",
					"transcend:autoscaling:trigger"), true);
		} else if (os.getOptionName().equals("XX:MaxPermSize")) {
			addOption(new OptionSetting("XX:MaxPermSize", "64m",
					"transcend:elasticbeanstalk:container:tomcat:jvmoptions"),
					true);
		} else if (os.getOptionName().equals("UpperBreachScaleIncrement")) {
			addOption(new OptionSetting("UpperBreachScaleIncrement", "1",
					"transcend:autoscaling:trigger"), true);
		} else if (os.getOptionName().equals("MinSize")) {
			addOption(new OptionSetting("MinSize", "1",
					"transcend:autoscaling:asg"), true);
		} else if (os.getOptionName().equals("Custom Availability Zones")) {
			addOption(new OptionSetting("Custom Availability Zones", "nova",
					"transcend:autoscaling:asg"), true);
		} else if (os.getOptionName().equals("Availability Zones")) {
			addOption(new OptionSetting("Availability Zones", "Any 1",
					"transcend:autoscaling:asg"), true);
		} else if (os.getOptionName().equals("LogPublicationControl")) {
			addOption(new OptionSetting("LogPublicationControl", "false",
					"transcend:elasticbeanstalk:hostmanager"), true);
		} else if (os.getOptionName().equals("JVM Options")) {
			addOption(new OptionSetting("JVM Options", null,
					"transcend:elasticbeanstalk:container:tomcat:jvmoptions"),
					true);
		} else if (os.getOptionName().equals("Notification Topic Name")) {
			addOption(new OptionSetting("Notification Topic Name", null,
					"transcend:elasticbeanstalk:sns:topics"), true);
		} else if (os.getOptionName().equals("LoadBalancerHTTPPort")) {
			addOption(new OptionSetting("LoadBalancerHTTPPort", "80",
					"transcend:elb:loadbalancer"), true);
		} else if (os.getOptionName().equals("Timeout")) {
			addOption(new OptionSetting("Timeout", "5",
					"transcend:elb:healthcheck"), true);
		} else if (os.getOptionName().equals("BreachDuration")) {
			addOption(new OptionSetting("BreachDuration", "2",
					"transcend:autoscaling:trigger"), true);
		} else if (os.getOptionName().equals("MonitoringInterval")) {
			addOption(new OptionSetting("MonitoringInterval", "5 minute",
					"transcend:autoscaling:launchconfiguration"), true);
		} else if (os.getOptionName().equals("MaxSize")) {
			addOption(new OptionSetting("MaxSize", "4",
					"transcend:autoscaling:asg"), true);
		} else if (os.getOptionName().equals("LowerThreshold")) {
			addOption(new OptionSetting("LowerThreshold", "2000000",
					"transcend:autoscaling:trigger"), true);
		} else if (os.getOptionName().equals("AWS_SECRET_KEY")) {
			addOption(new OptionSetting("AWS_SECRET_KEY", null,
					"transcend:elasticbeanstalk:application:environment"), true);
		} else if (os.getOptionName().equals("AWS_ACCESS_KEY_ID")) {
			addOption(new OptionSetting("AWS_ACCESS_KEY_ID", null,
					"transcend:elasticbeanstalk:application:environment"), true);
		} else if (os.getOptionName().equals("UpperThresholds")) {
			addOption(new OptionSetting("UpperThresholds", "6000000",
					"transcend:autoscaling:trigger"), true);
		} else if (os.getOptionName().equals("Notification Protocol")) {
			addOption(new OptionSetting("Notification Protocol", "email",
					"transcend:elasticbeanstalk:sns:topics"), true);
		} else if (os.getOptionName().equals("Unit")) {
			addOption(new OptionSetting("Unit", "Bytes",
					"transcend:autoscaling:trigger"), true);
		} else if (os.getOptionName().equals("Xmx")) {
			addOption(new OptionSetting("Xmx", "256m",
					"transcend:elasticbeanstalk:container:tomcat:jvmoptions"),
					true);
		} else if (os.getOptionName().equals("Cooldown")) {
			addOption(new OptionSetting("Cooldown", "360",
					"transcend:autoscaling:asg"), true);
		} else if (os.getOptionName().equals("Period")) {
			addOption(new OptionSetting("Period", "1",
					"transcend:autoscaling:trigger"), true);
		} else if (os.getOptionName().equals("Xms")) {
			addOption(new OptionSetting("Xms", "256m",
					"transcend:elasticbeanstalk:container:tomcat:jvmoptions"),
					true);
		} else if (os.getOptionName().equals("EC2KeyName")) {
			addOption(new OptionSetting("EC2KeyName", null,
					"transcend:autoscaling:launchconfiguration"), true);
		} else if (os.getOptionName().equals("Stickiness Policy")) {
			addOption(new OptionSetting("Stickiness Policy", "false",
					"transcend:elb:policies"), true);
		} else if (os.getOptionName().equals("HealthyThreshold")) {
			addOption(new OptionSetting("HealthyThreshold", "3",
					"transcend:elb:healthcheck"), true);
		} else if (os.getOptionName().equals("SSLCertificateId")) {
			addOption(new OptionSetting("SSLCertificateId", null,
					"transcend:elb:loadbalancer"), true);
		} else {
			// TODO throw ElasticBeanStalkQueryFaults.invalidParameterValue();
		}
	}

	public void addS3Data(final String user, final String id,
			final String secret_key, final String s3bucket, final String s3key) {
		if (sourceBundle == null) {
			sourceBundle = new HashMap<String, String>();
		}
		sourceBundle.put("user", user);
		sourceBundle.put("id", id);
		sourceBundle.put("key", secret_key);
		sourceBundle.put("S3Bucket", s3bucket);
		sourceBundle.put("S3Key", s3key);
	}

	public String getAcId() {
		return acId;
	}

	public HashMap<String, OptionSetting> getOptions() {
		return options;
	}

	public String getOptionValue(final String opName) {
		final OptionSetting temp = options.get(opName);
		if (temp == null) {
			return null;
		}
		return temp.getOptionValue();
	}

	public String getPostWaitUrl() {
		return postWaitUrl;
	}

	public HashMap<String, String> getSourceBundle() {
		return sourceBundle;
	}

	public String getStackId() {
		return stackId;
	}

	public void populateDefaultOptions(final Session session) {
		final ConfigTemplateBean defaultConfigTemplate = AccountUtil
				.readDefaultConfigurationTemplate(session);
		final Set<ConfigBean> configurations = defaultConfigTemplate
				.getConfigs();
		final Iterator<ConfigBean> it = configurations.iterator();
		while (it.hasNext()) {
			final ConfigBean temp = it.next();
			options.put(temp.getOption(), new OptionSetting(
					temp.getNameSpace(), temp.getOption(), temp.getValue()));
		}
	}

	public void setAcId(final String acId) {
		this.acId = acId;
	}

	public void setOptions(final HashMap<String, OptionSetting> options) {
		this.options = options;
	}

	public void setPostWaitUrl(final String postWaitUrl) {
		this.postWaitUrl = postWaitUrl;
	}

	public void setSourceBundle(final HashMap<String, String> sourceBundle) {
		this.sourceBundle = sourceBundle;
	}

	public void setStackId(final String stackId) {
		this.stackId = stackId;
	}

	public String toJsonString() throws JsonGenerationException,
			JsonMappingException, IOException {
		return JsonUtil.toJsonString(this);
	}
}
