package com.msi.tough.engine.core;

import java.util.Map;

import org.dasein.cloud.CloudProvider;

import com.msi.tough.cf.AccountType;
import com.msi.tough.dasein.DaseinHelper;
import com.msi.tough.model.ResourcesBean;

public class CallStruct {
	/**
	 * Context of the CF template under which new resource is being created.
	 */
	private TemplateContext ctx;

	/**
	 * New resource's name
	 */
	private String name;

	/**
	 * New resource's type
	 */
	private String type;

	/**
	 * Stackid
	 */
	private String stackId;

	/**
	 * calling user account
	 * 
	 */
	private AccountType ac;

	/**
	 * parent resource physicalId
	 * 
	 */
	private String parentId;

	/*
	 * properties to be passed to for resource creation they should be same
	 * defined in AWS CF for the resource
	 */
	private Map<String, Object> properties;

	/**
	 * Resource Description
	 */
	private String description;

	/**
	 * Number of resources new resources has to wait for 0=no wait, n>0 wait for
	 * n resources to send post wait messsage, n<0 wait for child resouces to
	 * complete.
	 */
	private int noWait;

	/**
	 * Physical resource id
	 */
	private String physicalId;

	/**
	 * Availability Zone for resource creation
	 */
	private String availabilityZone;

	private String waitHookClass;
	private String resourceData;

	private ResourcesBean resourcesBean;

	private boolean syncMode;
	CloudProvider cloudProvider;

	public AccountType getAc() {
		return ac;
	}

	public String getAvailabilityZone() {
		return availabilityZone;
	}

	public CloudProvider getCloudProvider() throws Exception {
		if (availabilityZone == null) {
			availabilityZone = ac.getDefZone();
		}
		if (cloudProvider == null) {
			cloudProvider = DaseinHelper.getProvider(availabilityZone, ac);
		}
		return cloudProvider;
	}

	public TemplateContext getCtx() {
		return ctx;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public int getNoWait() {
		return noWait;
	}

	public String getParentId() {
		return parentId;
	}

	public String getPhysicalId() {
		return physicalId;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public Object getProperty(final String key) {
		if (properties == null) {
			return null;
		}
		return properties.get(key);
	}

	public Object getRequiredProperty(final String key) {
		final Object obj = properties.get(key);
		if (obj == null) {
			throw new RuntimeException("Property can't be blank " + key);
		}
		return obj;
	}

	public String getResourceData() {
		return resourceData;
	}

	public ResourcesBean getResourcesBean() {
		return resourcesBean;
	}

	public String getStackId() {
		return stackId;
	}

	public String getType() {
		return type;
	}

	public String getWaitHookClass() {
		return waitHookClass;
	}

	public boolean isSyncMode() {
		return syncMode;
	}

	public CallStruct newCall(final String parentId) throws Exception {
		final CallStruct call = new CallStruct();
		call.setAc(ac);
		call.setCtx(ctx);
		call.setParentId(parentId);
		call.setStackId(stackId);
		call.setAvailabilityZone(availabilityZone);
		call.setCloudProvider(cloudProvider);
		return call;
	}

	public void setAc(final AccountType ac) {
		this.ac = ac;
	}

	public void setAvailabilityZone(final String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}

	public void setCloudProvider(final CloudProvider cloudProvider) {
		this.cloudProvider = cloudProvider;
	}

	public void setCtx(final TemplateContext ctx) {
		this.ctx = ctx;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNoWait(final int noWait) {
		this.noWait = noWait;
	}

	public void setParentId(final String parentId) {
		this.parentId = parentId;
	}

	public void setPhysicalId(final String physicalId) {
		this.physicalId = physicalId;
	}

	public void setProperties(final Map<String, Object> properties) {
		this.properties = properties;
	}

	public void setResourceData(final String resourceData) {
		this.resourceData = resourceData;
	}

	public void setResourcesBean(final ResourcesBean resourcesBean) {
		this.resourcesBean = resourcesBean;
	}

	public void setStackId(final String stackId) {
		this.stackId = stackId;
	}

	public void setSyncMode(final boolean syncMode) {
		this.syncMode = syncMode;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public void setWaitHookClass(final String waitHookClass) {
		this.waitHookClass = waitHookClass;
	}

	@Override
	public String toString() {
		return "name=" + name + ",type=" + type + ",parentId=" + parentId
				+ ",availabilityZone=" + availabilityZone + ",properties="
				+ (properties == null ? "" : properties.toString());
	}
}
