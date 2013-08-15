package com.msi.tough.engine.aws.elasticache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;

import com.msi.tough.cf.json.CFBooleanParameter;
import com.msi.tough.cf.json.CFConstrainedNumberParameter;
import com.msi.tough.cf.json.CFDefaultStringParameter;
import com.msi.tough.cf.json.CFStringParameter;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.utils.Constants;

public class ElasticacheCloudFormation {

	private String awsTemplateFormatVersion;
	private String description;
	private LinkedHashMap<String, Object> resources = null;
	private LinkedHashMap<String, Object> parameters = null;

	public ElasticacheCloudFormation() {
		resources = new LinkedHashMap<String, Object>();
		parameters = new LinkedHashMap<String, Object>();
	}

	public ElasticacheCloudFormation(
			final String cacheClusterId,
			final String description,
			final CacheClusterResource cacheClusterResource,
			final List<CacheSecurityGroupResource> cacheSecurityGroupResources,
			final List<CacheSecurityGroupIngressResource> cacheSecurityGroupIngressResources) {
		this();
		awsTemplateFormatVersion = "2010-09-09";
		this.description = description;

		// Parameters

		parameters
				.put(Constants.AUTOMINORVERSIONUPGRADE,
						new CFBooleanParameter(
								"Indicates that minor engine upgrades will be applied automatically to the cache cluster during the maintenance window.",
								"true"));
		parameters.put(Constants.CACHENODETYPE, new CacheNodeTypeParameter());
		parameters
				.put(Constants.CACHEPARAMETERGROUPNAME,
						new CFDefaultStringParameter(
								"The name of the cache parameter group associated with this cache cluster.",
								"default"));
		// parameters.put(Constants.CACHESECURITYGROUPNAMES, new
		// CFCommaDelimitedListParameter("A list of cache security group names associated with this cache cluster."));
		parameters.put(Constants.ENGINE, new CFDefaultStringParameter(
				"The version of the cache engine to be used for this cluster.",
				"memcached"));
		parameters.put(Constants.ENGINEVERSION, new CFDefaultStringParameter(
				"The version of the cache engine to be used for this cluster.",
				"1.4.5"));
		parameters
				.put(Constants.NOTIFICATIONTOPICARN,
						new CFStringParameter(
								"The Amazon Resource Name (ARN) of the Amazon Simple Notification Service (SNS) topic to which notifications will be sent."));
		parameters.put(Constants.NUMCACHENODES,
				new NumberOfCacheNodesParameter());
		parameters
				.put(Constants.PORT,
						new CFConstrainedNumberParameter(
								"The port number on which each of the cache nodes will accept connections.",
								11211, 0, 65535, "A valid port."));
		parameters
				.put(Constants.PREFERREDAVAILABILITYZONE,
						new CFStringParameter(
								"The EC2 Availability Zone that the cache cluster will be created in."));
		parameters
				.put(Constants.PREFERREDMAINENANCEWINDOW,
						new CFStringParameter(
								"The weekly time range (in UTC) during which system maintenance can occur."));

		// Resources

		// CacheClusterId is the "key" of the AWS::ElastiCache::CacheClusterType
		resources.put(cacheClusterId, cacheClusterResource);

		// Security Groups
		// ArrayList<LinkedHashMap<String,Object>> securityGroupRefs = new
		// ArrayList<LinkedHashMap<String,Object>>(cacheSecurityGroupResources.size());
		final ArrayList<String> securityGroupRefs = new ArrayList<String>(
				cacheSecurityGroupResources.size());
		for (final CacheSecurityGroupResource cacheSecurityGroupResource : cacheSecurityGroupResources) {
			resources.put(cacheSecurityGroupResource.getName(),
					cacheSecurityGroupResource);
			// Ref version
			// LinkedHashMap<String,Object> securityGroupRef =
			// JsonUtil.toSingleHash("Ref",
			// cacheSecurityGroupResource.getName());
			// securityGroupRefs.add(securityGroupRef);
			// List of Strings version
			securityGroupRefs.add(cacheSecurityGroupResource.getName());
		}
		cacheClusterResource.addProperty(Constants.CACHESECURITYGROUPNAMES,
				securityGroupRefs);
		cacheClusterResource.setDependsOn(securityGroupRefs);

		// Security Group Ingress
		// NOTE: In Elasticache, we have a list-of-ec2, but it looks like Cloud
		// Formation
		// has only a string, so we emit many...
		int ingressFictionalIndex = 1;
		for (final CacheSecurityGroupIngressResource csgir : cacheSecurityGroupIngressResources) {
			resources.put(
					String.format("%s-%d", csgir.getCacheSecurityGroupName(),
							ingressFictionalIndex++), csgir);
		}

	}

	@JsonProperty("AWSTemplateFormatVersion")
	public String getAWSTemplateFormatVersion() {
		return awsTemplateFormatVersion;
	}

	@JsonProperty("Description")
	public String getDescription() {
		return description;
	}

	@JsonProperty("Parameters")
	public LinkedHashMap<String, Object> getParameters() {
		return parameters;
	}

	@JsonProperty("Resources")
	public LinkedHashMap<String, Object> getResources() {
		return resources;
	}

	public void setAwsTemplateFormatVersion(
			final String awsTemplateFormatVersion) {
		this.awsTemplateFormatVersion = awsTemplateFormatVersion;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setParameters(final LinkedHashMap<String, Object> parameters) {
		this.parameters = parameters;
	}

	public void setResources(final LinkedHashMap<String, Object> resources) {
		this.resources = resources;
	}

	public String toJson() throws JsonGenerationException,
			JsonMappingException, IOException {
		return JsonUtil.toJsonString(this);
	}
}
