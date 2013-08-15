package com.msi.tough.engine.aws.elasticache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.cloudformation.model.CreateStackRequest;
import com.amazonaws.services.cloudformation.model.CreateStackResult;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.core.converter.ToJson;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.elasticache.CacheClusterBean;
import com.msi.tough.utils.ChefUtil;

public class ElasticacheServiceUtils {

	final static Logger logger = LoggerFactory
			.getLogger(ElasticacheServiceUtils.class);

	private static void addParameterValue(final String name,
			final Object value, final Map<String, Object> parameterValues) {
		if (value != null) {
			parameterValues.put(name, value);
		}
	}

	private static void addParameterValue(final String name,
			final String value, final Map<String, JsonNode> parameterValues) {
		if (value != null) {
			parameterValues.put(name, new TextNode(value));
		}
	}

	public static void CallCloudFormation(final AccountBean ac) {

		// NOT in use, this is an example WS call

		final BasicAWSCredentials cred = new BasicAWSCredentials(
				ac.getAccessKey(), ac.getSecretKey());
		final AmazonCloudFormationClient client = new com.amazonaws.services.cloudformation.AmazonCloudFormationClient(
				cred);

		final String url = (String) Appctx.getConfiguration().get(
				"CloudFormationURL");
		logger.debug("CloudFormation URL" + url);

		client.setEndpoint(url);

		// Object o = new
		// com.amazonaws.services.cloudformation.model.CreateStackRequest();
		final CreateStackRequest request = new CreateStackRequest();
		final CreateStackResult result = client.createStack(request);
	}

	private static boolean createDatabagItem(final ChefUtil chefUtil,
			final String dataBagName, final String dataBagItemName,
			final ToJson databag) {

		boolean createdSuccessfully = true;
		try {
			ChefUtil.createDatabagItem(dataBagName, dataBagItemName);
			ChefUtil.putDatabagItem(dataBagName, dataBagItemName,
					databag.toJson());
		} catch (final Exception ex) {
			logger.debug("Exception creating Data Bag Item " + dataBagName
					+ " " + ex.getMessage());
			createdSuccessfully = false;
		}
		return createdSuccessfully;
	}

	public static boolean createElasticacheDatabag(final String dataBagName,
			final ElasticacheDatabag dataBag) {

		boolean createdSuccessfully = true;

		try {
			final ChefUtil chefUtil = new ChefUtil();

			ChefUtil.createDatabag(dataBagName);

			createdSuccessfully = createDatabagItem(chefUtil, dataBagName,
					"config", dataBag.getConfig());
			if (createdSuccessfully) {
				createdSuccessfully = createDatabagItem(chefUtil, dataBagName,
						"parameters", dataBag.getParameterGroup());
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
			logger.debug("Exception creating Data Bag Item " + dataBagName
					+ " " + ex.getMessage());
			createdSuccessfully = false;
		}

		return createdSuccessfully;
	}

	private static String getCloudFormationTemplate(final CacheClusterBean cc) {

		String jsonTemplate = null;

		// Define Security Group(s)
		// These include both references to the Security Group Resource(s) in
		// the Cluster Properties
		// and the groups themselves
		final List<CacheSecurityGroupResource> securityGroupResources = new ArrayList<CacheSecurityGroupResource>();
		final ArrayList<LinkedHashMap<String, Object>> cacheSecurityGroupNames = new ArrayList<LinkedHashMap<String, Object>>();
		final List<CacheSecurityGroupIngressResource> securityGroupIngressResources = new ArrayList<CacheSecurityGroupIngressResource>();

		final CommaObject cosec = new CommaObject(cc.getSecurityGroups());

		for (final String secGrp : cosec.getList()) {
			final LinkedHashMap<String, Object> securityGroup = JsonUtil
					.toSingleHash("Ref", secGrp);
			cacheSecurityGroupNames.add(securityGroup);
		}

		// Define the AWS::ElastiCache::CacheCluster Resource
		final CacheClusterResource cacheClusterResource = new CacheClusterResource();
		cacheClusterResource.setDefaultProperties();

		// Wrapper object for entire JSON Template serialization
		final ElasticacheCloudFormation cfc = new ElasticacheCloudFormation(
				cc.getName(), "Elasticache Cluster Template",
				cacheClusterResource, securityGroupResources,
				securityGroupIngressResources);

		try {

			jsonTemplate = cfc.toJson();

			logger.debug(JsonUtil.toJsonPrettyPrintString(JsonUtil
					.load(jsonTemplate)));
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonTemplate;
	}

}
