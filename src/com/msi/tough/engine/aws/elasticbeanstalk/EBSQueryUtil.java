package com.msi.tough.engine.aws.elasticbeanstalk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.elasticbeanstalk.model.ApplicationDescription;
import com.amazonaws.services.elasticbeanstalk.model.ApplicationVersionDescription;
import com.amazonaws.services.elasticbeanstalk.model.ConfigurationOptionSetting;
import com.amazonaws.services.elasticbeanstalk.model.CreateConfigurationTemplateResult;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.elasticbeanstalk.json.Application;
import com.msi.tough.elasticbeanstalk.json.ApplicationVersion;
import com.msi.tough.elasticbeanstalk.json.ConfigurationTemplate;
import com.msi.tough.elasticbeanstalk.json.EBSDataBag;
import com.msi.tough.elasticbeanstalk.json.ElasticBeanStalkCloudFormation;
import com.msi.tough.elasticbeanstalk.json.Environment;
import com.msi.tough.elasticbeanstalk.json.OptionSetting;
import com.msi.tough.elasticbeanstalk.json.OptionToRemove;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.ApplicationBean;
import com.msi.tough.model.ConfigTemplateBean;
import com.msi.tough.model.EnvironmentBean;
import com.msi.tough.model.VersionBean;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.ChefUtil;

public class EBSQueryUtil {
	final static Logger logger = LoggerFactory.getLogger(EBSQueryUtil.class);

	private static void addIfNotNull(final Application app, final String key,
			final Object value) {
		if (value != null) {
			app.addProperty(key, value);
		}
	}

	private static void addIfNotNull(final Environment env, final String key,
			final Object value) {
		if (value != null) {
			env.addProperty(key, value);
		}
	}

	public static String CallCloudFormationDirect(final Session session,
			final AccountBean ac, final EnvironmentBean eb,
			final List<OptionSetting> opSettings,
			final List<OptionToRemove> opsRemove) throws Exception {

		// Cloud formation is "parameterized" by a combination of Parameter
		// objects defined in the JSON template
		// and "hard coded" values inside the JSON template. As of 2011-12-07,
		// there is no way to pass the "ID"
		// of a CF type, such as "AWS::Elasticache::CacheCluster" (e.g.
		// CacheClusterID) as a parameter.

		/*
		 * if(opSettings != null && opSettings.size() > 0){
		 * parameterValues.put("OptionSettings", opSettings); } if(opsRemove !=
		 * null && opsRemove.size() > 0){ parameterValues.put("OptionsToRemove",
		 * opsRemove); }
		 * 
		 * logger.debug("ParameterValues Map as json template: \n" +
		 * JsonUtil.toJsonPrettyPrintString(parameterValues));
		 */

		final ApplicationBean ab = AccountUtil.readApplication(session, ac,
				eb.getApplicationName());
		final String stackName = eb.getStack();
		final long userId = ac.getId();

		// Retrieve the CF TEMPLATE
		final String jsonTemplate = getCloudFormationTemplate(eb, ab,
				opSettings, opsRemove);
		logger.debug("jsonTemplate generated:\n"
				+ JsonUtil.toJsonPrettyPrintString(JsonUtil.load(jsonTemplate)));

		logger.debug("Calling CFUtil.runAWSScript()...\n" + "Stack: "
				+ stackName + "\n" + "User Id: " + userId + "\n");

		final String stackId = "__ecache_" + stackName;
		CFUtil.runAsyncAWSScript(stackId, userId, jsonTemplate,
				new TemplateContext(null));
		logger.debug("CFUtil.runAWSScript() returns: " + stackId);

		// return stackId;
		return stackId;
	}

	public static boolean createEBSDatabag(final String dataBagName,
			final EBSDataBag dataBag) {

		boolean createdSuccessfully = true;

		try {
			final ChefUtil chefUtil = new ChefUtil();
			ChefUtil.createDatabag(dataBagName);
			ChefUtil.createDatabagItem(dataBagName, "configs");
			ChefUtil.putDatabagItem(dataBagName, "configs",
					dataBag.toJsonString());
			logger.debug("Successfully created a databag: " + dataBagName);

		} catch (final Exception ex) {
			ex.printStackTrace();
			logger.debug("Exception creating Data Bag Item " + dataBagName
					+ " " + ex.getMessage());
			createdSuccessfully = false;
		}

		return createdSuccessfully;
	}

	private static String getCloudFormationTemplate(final EnvironmentBean eb,
			final ApplicationBean ab, final List<OptionSetting> opSettings,
			final List<OptionToRemove> opsRemove)
			throws JsonGenerationException, JsonMappingException, IOException {

		String jsonTemplate = null;

		final ElasticBeanStalkCloudFormation cf = new ElasticBeanStalkCloudFormation();
		// add Application resource
		logger.debug("Adding Application resource to the Elasitc Beanstalk Cloud Formation template.");
		final String applicationName = eb.getApplicationName();
		final Application app = new Application();
		addIfNotNull(app, "Description", ab.getDesc());
		logger.debug("Adding ApplicationVersions to the Properties of Application resource.");
		final List<ApplicationVersion> ApplicationVersions = new ArrayList<ApplicationVersion>();
		final Set<VersionBean> versions = ab.getVersions();
		for (final VersionBean v : versions) {
			ApplicationVersions.add(new ApplicationVersion(v.getVersion(), v
					.getDesc(), v.getS3bucket(), v.getS3key()));
		}
		app.addProperty("ApplicationVersions", ApplicationVersions);
		logger.debug("Adding ConfigurationTemplates to the Properties of Application resource.");
		final Set<ConfigTemplateBean> templates = ab.getTemplates();
		final List<ConfigurationTemplate> ct = new ArrayList<ConfigurationTemplate>();
		for (final ConfigTemplateBean c : templates) {
			if (c.getName().equals(eb.getTemplate())) {
				final ConfigurationTemplate configTemp = new ConfigurationTemplate(
						c.getName(), c.getDesc());

				if (opSettings != null) {
					for (final OptionSetting os0 : opSettings) {
						configTemp.addOptionSetting(os0);
					}
				}
				if (opsRemove != null) {
					for (final OptionToRemove os1 : opsRemove) {
						configTemp.addOptionToRemove(os1);
					}
				}

				ct.add(configTemp);
			} else {
				ct.add(new ConfigurationTemplate(c.getName(), c.getDesc()));
			}
		}
		app.addProperty("ConfigurationTemplates", ct);

		cf.addResource(applicationName, app);

		// add Environment resource
		logger.debug("Adding Environment resource to the Elasitc Beanstalk Cloud Formation template.");
		final Environment env = new Environment();
		env.addProperty("ApplicationName",
				JsonUtil.toSingleHash("Ref", eb.getApplicationName()));
		addIfNotNull(env, "Description", eb.getDesc());
		env.addProperty("TemplateName", eb.getTemplate());
		env.addProperty("VersionLabel", eb.getVersion());
		cf.addResource(eb.getName(), env);

		jsonTemplate = cf.toJson().toString();

		return jsonTemplate;
	}

	public static void marshallApplication(final XMLNode xmem,
			final ApplicationDescription el) {
		final XMLNode versions = QueryUtil.addNode(xmem, "Versions");
		QueryUtil.addNode(xmem, "Description", el.getDescription());
		QueryUtil.addNode(xmem, "ApplicationName", el.getApplicationName());
		QueryUtil.addNode(xmem, "DateCreated", el.getDateCreated());
		QueryUtil.addNode(xmem, "DateUpdated", el.getDateUpdated());

		if (el.getConfigurationTemplates() != null
				&& el.getConfigurationTemplates().size() > 0) {
			final XMLNode ts = QueryUtil
					.addNode(xmem, "ConfigurationTemplates");
			for (final String i : el.getConfigurationTemplates()) {
				QueryUtil.addNode(ts, "member", i);
			}
		}

		if (el.getVersions() != null && el.getVersions().size() > 0) {
			final XMLNode vs = QueryUtil.addNode(xmem, "Versions");
			for (final String i : el.getVersions()) {
				QueryUtil.addNode(vs, "member", i);
			}
		}
	}

	public static void marshallApplicationVersion(final XMLNode xmem,
			final ApplicationVersionDescription el) {
		final XMLNode s3 = QueryUtil.addNode(xmem, "SourceBundle");
		QueryUtil.addNode(s3, "S3Bucket", el.getSourceBundle().getS3Bucket());
		QueryUtil.addNode(s3, "S3Key", el.getSourceBundle().getS3Key());
		QueryUtil.addNode(xmem, "VersionLabel", el.getVersionLabel());
		QueryUtil.addNode(xmem, "ApplicationName", el.getApplicationName());
		QueryUtil.addNode(xmem, "DateCreated", el.getDateCreated());
		QueryUtil.addNode(xmem, "DateUpdated", el.getDateUpdated());
		QueryUtil.addNode(xmem, "Description", el.getDescription());
	}

	public static void marshallConfigurationTemplate(final XMLNode nr,
			final CreateConfigurationTemplateResult o) {

		QueryUtil.addNode(nr, "SolutionStackName", o.getSolutionStackName());
		final List<ConfigurationOptionSetting> ops = o.getOptionSettings();
		if (ops != null && ops.size() > 0) {
			final XMLNode nops = QueryUtil.addNode(nr, "OptionSettings");
			for (final ConfigurationOptionSetting cs : ops) {
				final XMLNode m = QueryUtil.addNode(nops, "member");
				QueryUtil.addNode(m, "Namespace", cs.getNamespace());
				QueryUtil.addNode(m, "OptionName", cs.getOptionName());
				QueryUtil.addNode(m, "Value", cs.getValue());
			}
		}
		XMLNode os = QueryUtil.addNode(nr, "OptionSettings");
		List<ConfigurationOptionSetting> optionSettings = o.getOptionSettings();
		for(ConfigurationOptionSetting cos : optionSettings){
			XMLNode member = QueryUtil.addNode(os, "member");
			QueryUtil.addNode(member, "OptionName", cos.getOptionName());
			QueryUtil.addNode(member, "Value", cos.getValue());
			QueryUtil.addNode(member, "Namespace", cos.getNamespace());
		}
		QueryUtil.addNode(nr, "Description", o.getDescription());
		QueryUtil.addNode(nr, "ApplicationName", o.getApplicationName());
		QueryUtil.addNode(nr, "DateCreated", o.getDateCreated());
		QueryUtil.addNode(nr, "DateUpdated", o.getDateUpdated());
		QueryUtil.addNode(nr, "TemplateName", o.getTemplateName());
		if (o.getEnvironmentName() != null) {
			QueryUtil.addNode(nr, "DeploymentStatus", o.getDeploymentStatus());
			QueryUtil.addNode(nr, "Environ mentName", o.getEnvironmentName());
		}
	}

	public void addReference(final LinkedHashMap<String, Object> properties,
			final String parameter) {
		properties.put(parameter, JsonUtil.toSingleHash("Ref", parameter));
	}
}
