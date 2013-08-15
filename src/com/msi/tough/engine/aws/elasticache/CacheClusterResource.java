package com.msi.tough.engine.aws.elasticache;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.codehaus.jackson.annotate.JsonProperty;

import com.msi.tough.core.JsonUtil;

public class CacheClusterResource {

	private String type;
	private LinkedHashMap<String, Object> properties = null;
	private ArrayList<String> dependsOn = null;

	public CacheClusterResource() {
		dependsOn = new ArrayList<String>();
		properties = new LinkedHashMap<String, Object>();
		type = "AWS::ElastiCache::CacheCluster";
	}

	public void addProperty(final String key, final Object value) {
		properties.put(key, value);
	}

	public void addRefParameter(final LinkedHashMap<String, Object> properties,
			final String parameter) {
		properties.put(parameter, JsonUtil.toSingleHash("Ref", parameter));
	}

	@JsonProperty("DependsOn")
	public ArrayList<String> getDependsOn() {
		return dependsOn;
	}

	@JsonProperty("Properties")
	public LinkedHashMap<String, Object> getProperties() {
		return properties;
	}

	@JsonProperty("Type")
	public String getType() {
		return type;
	}

	public void setDefaultProperties() {

		// Should move Elasticache Constants so they can be used here
		addRefParameter(properties, "AutoMinorVersionUpgrade");
		addRefParameter(properties, "CacheNodeType");
		addRefParameter(properties, "CacheParameterGroupName");
		addRefParameter(properties, "Engine");
		addRefParameter(properties, "EngineVersion");
		addRefParameter(properties, "NotificationTopicArn");
		addRefParameter(properties, "NumCacheNodes");
		addRefParameter(properties, "Port");
		addRefParameter(properties, "PreferredAvailabilityZone");
		// addRefParameter( properties, "CacheSecurityGroupNames");
	}

	public void setDependsOn(final ArrayList<String> dependsOn) {
		this.dependsOn = dependsOn;
	}

	public void setProperties(final LinkedHashMap<String, Object> properties) {
		this.properties = properties;
	}

	public void setType(final String type) {
		this.type = type;
	}
}
