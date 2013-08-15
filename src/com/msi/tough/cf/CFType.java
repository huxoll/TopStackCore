package com.msi.tough.cf;

import java.util.Date;
import java.util.Map;

import com.msi.tough.core.JsonUtil;
import com.msi.tough.core.MapUtil;
import com.msi.tough.core.converter.ToJson;
import com.msi.tough.engine.resource.Resource;

/**
 * Base class implementing resources. All resources extend this class.
 * 
 * @author raj
 * 
 */
public class CFType implements ToJson, Resource {
	/**
	 * stack id under which this resource is created
	 */
	private String stackId;

	/**
	 * Resource owner account id
	 */
	private Long acId;

	/**
	 * Resource Name
	 */
	private String name;

	/**
	 * PhysicalID of the resource
	 */
	private String physicalId;

	/**
	 * Chef Databag associated with this resource
	 */
	private String databag;

	/**
	 * URL to call after the resource creation is complete
	 */
	private String postWaitUrl;

	/**
	 * Number of wait for this resource
	 */
	private String noWait;

	/**
	 * Time Created
	 */
	private Date createdTime;

	/**
	 * time resource was last updated
	 * 
	 */
	private Date updatedTime;

	public CFType() {
		createdTime = new Date();
		updatedTime = new Date();
	}

	public long getAcId() {
		return acId;
	}

	@Override
	public Object getAtt(final String key) {
		throw new RuntimeException(typeAsString() + " invalid Att " + key);
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public String getDatabag() {
		return databag;
	}

	public String getName() {
		return name;
	}

	public String getNoWait() {
		return noWait;
	}

	public String getPhysicalId() {
		return physicalId;
	}

	public String getPostWaitUrl() {
		return postWaitUrl;
	}

	public String getStackId() {
		return stackId;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	@Override
	public Object ref() {
		return this;
	}

	public void setAcId(final Long acId) {
		this.acId = acId;
	}

	public void setCreatedTime(final Date createdTime) {
		this.createdTime = createdTime;
	}

	public void setDatabag(final String databag) {
		this.databag = databag;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNoWait(final String noWait) {
		this.noWait = noWait;
	}

	public void setPhysicalId(final String physicalId) {
		this.physicalId = physicalId;
	}

	public void setPostWaitUrl(final String postWaitUrl) {
		this.postWaitUrl = postWaitUrl;
	}

	public void setStackId(final String stackId) {
		this.stackId = stackId;
	}

	public void setUpdatedTime(final Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String toCFString() throws Exception {
		return "{\"Type\" : \"" + typeAsString() + "\", \"Properties\" : "
				+ toJson() + "}";
	}

	/**
	 * Convert Resource to its JSON presentation
	 */
	@Override
	public String toJson() throws Exception {
		final Map<String, Object> map = toMap();
		if (map != null) {
			return JsonUtil.toJsonString(map);
		}
		return JsonUtil.toJsonString(this);
	}

	/**
	 * Convert Resource to a Attribute Map
	 * 
	 * @return map
	 * @throws Exception
	 */
	public Map<String, Object> toMap() throws Exception {
		final Map<String, Object> map = MapUtil.create("Id", "config");
		if (acId != null) {
			map.put("AcId", acId);
		}
		if (stackId != null) {
			map.put("StackId", stackId);
		}
		if (physicalId != null) {
			map.put("PhysicalId", physicalId);
		}
		if (name != null) {
			map.put("Name", name);
		}
		if (postWaitUrl != null) {
			map.put("PostWaitUrl", postWaitUrl);
		}
		return map;
	}

	/**
	 * return resource type as defined in AWS CF ex. AWS::EC2::Instance
	 * 
	 * @return resource type
	 */
	public String typeAsString() {
		return null;
	}

}
