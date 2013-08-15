package com.msi.tough.cf.ec2;

import java.util.Map;

import com.msi.tough.cf.CFType;

public class InstanceType extends CFType {
	private String availabilityZone;
	private String privateDnsName;
	private String publicDnsName;
	private String publicIp;
	private String status;

	private String disableApiTermination;
	private String imageId;
	private String instanceId;
	private String uuid;
	private String instanceType;
	private String kernelId;
	private String KeyName;
	private String monitoring;
	private String placementGroupName;
	private String privateIpAddress;
	private String ramDiskId;
	private String securityGroupIds;
	private String sourceDestCheck;
	private String subnetId;
	private String tags;
	private String tenancy;
	private String userData;
	private String volumes;

	private String hostname;
	private String chefRoles;

	@Override
	public Object getAtt(final String key) {
		if (key.equals("AvailabilityZone")) {
			return availabilityZone;
		}
		if (key.equals("PrivateDnsName")) {
			return privateDnsName;
		}
		if (key.equals("PublicDnsName")) {
			return publicDnsName;
		}
		if (key.equals("PrivateIp")) {
			return privateIpAddress;
		}
		if (key.equals("PublicIp")) {
			return publicIp;
		}
		return super.getAtt(key);
	}

	public String getAvailabilityZone() {
		return availabilityZone;
	}

	public String getChefRoles() {
		return chefRoles;
	}

	public String getDisableApiTermination() {
		return disableApiTermination;
	}

	public String getHostname() {
		return hostname;
	}

	public String getImageId() {
		return imageId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public String getKernelId() {
		return kernelId;
	}

	public String getKeyName() {
		return KeyName;
	}

	public String getMonitoring() {
		return monitoring;
	}

	public String getPlacementGroupName() {
		return placementGroupName;
	}

	public String getPrivateDnsName() {
		return privateDnsName;
	}

	public String getPrivateIpAddress() {
		return privateIpAddress;
	}

	public String getPublicDnsName() {
		return publicDnsName;
	}

	public String getPublicIp() {
		return publicIp;
	}

	public String getRamDiskId() {
		return ramDiskId;
	}

	public String getSecurityGroupIds() {
		return securityGroupIds;
	}

	public String getSourceDestCheck() {
		return sourceDestCheck;
	}

	public String getStatus() {
		return status;
	}

	public String getSubnetId() {
		return subnetId;
	}

	public String getTags() {
		return tags;
	}

	public String getTenancy() {
		return tenancy;
	}

	public String getUserData() {
		return userData;
	}

	public String getUuid() {
		return uuid;
	}

	public String getVolumes() {
		return volumes;
	}

	@Override
	public String ref() {
		return getPhysicalId();
	}

	public void setAvailabilityZone(final String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}

	public void setChefRoles(final String chefRoles) {
		this.chefRoles = chefRoles;
	}

	public void setDisableApiTermination(final String disableApiTermination) {
		this.disableApiTermination = disableApiTermination;
	}

	public void setHostname(final String hostname) {
		this.hostname = hostname;
	}

	public void setImageId(final String imageId) {
		this.imageId = imageId;
	}

	public void setInstanceId(final String instanceId) {
		this.instanceId = instanceId;
	}

	public void setInstanceType(final String instanceType) {
		this.instanceType = instanceType;
	}

	public void setKernelId(final String kernelId) {
		this.kernelId = kernelId;
	}

	public void setKeyName(final String keyName) {
		KeyName = keyName;
	}

	public void setMonitoring(final String monitoring) {
		this.monitoring = monitoring;
	}

	public void setPlacementGroupName(final String placementGroupName) {
		this.placementGroupName = placementGroupName;
	}

	public void setPrivateDnsName(final String privateDnsName) {
		this.privateDnsName = privateDnsName;
	}

	public void setPrivateIpAddress(final String privateIpAddress) {
		this.privateIpAddress = privateIpAddress;
	}

	public void setPublicDnsName(final String publicDnsName) {
		this.publicDnsName = publicDnsName;
	}

	public void setPublicIp(final String publicIp) {
		this.publicIp = publicIp;
	}

	public void setRamDiskId(final String ramDiskId) {
		this.ramDiskId = ramDiskId;
	}

	public void setSecurityGroupIds(final String securityGroupIds) {
		this.securityGroupIds = securityGroupIds;
	}

	public void setSourceDestCheck(final String sourceDestCheck) {
		this.sourceDestCheck = sourceDestCheck;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public void setSubnetId(final String subnetId) {
		this.subnetId = subnetId;
	}

	public void setTags(final String tags) {
		this.tags = tags;
	}

	public void setTenancy(final String tenancy) {
		this.tenancy = tenancy;
	}

	public void setUserData(final String userData) {
		this.userData = userData;
	}

	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}

	public void setVolumes(final String volumes) {
		this.volumes = volumes;
	}

	@Override
	public Map<String, Object> toMap() throws Exception {
		final Map<String, Object> map = super.toMap();
		return map;
	}

	@Override
	public String typeAsString() {
		return "AWS::EC2::Instance";
	}

}
