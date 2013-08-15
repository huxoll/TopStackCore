package com.msi.tough.cf.ec2;

import com.msi.tough.cf.CFType;

public class AuthorizeSecurityGroupIngressType extends CFType {
	private String groupName;
	private String groupId;
	private String ipProtocol;
	private String cidrIp;
	private String sourceSecurityGroupName;
	private String sourceSecurityGroupId;
	private String sourceSecurityGroupOwnerId;
	private String fromPort;
	private String toPort;

	@Override
	public Object getAtt(final String key) {
		return null;
	}

	public String getCidrIp() {
		return cidrIp;
	}

	public String getFromPort() {
		return fromPort;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getIpProtocol() {
		return ipProtocol;
	}

	public String getSourceSecurityGroupId() {
		return sourceSecurityGroupId;
	}

	public String getSourceSecurityGroupName() {
		return sourceSecurityGroupName;
	}

	public String getSourceSecurityGroupOwnerId() {
		return sourceSecurityGroupOwnerId;
	}

	public String getToPort() {
		return toPort;
	}

	public void setCidrIp(final String cidrIp) {
		this.cidrIp = cidrIp;
	}

	public void setFromPort(final String fromPort) {
		this.fromPort = fromPort;
	}

	public void setGroupId(final String groupId) {
		this.groupId = groupId;
	}

	public void setGroupName(final String groupName) {
		this.groupName = groupName;
	}

	public void setIpProtocol(final String ipProtocol) {
		this.ipProtocol = ipProtocol;
	}

	public void setSourceSecurityGroupId(final String sourceSecurityGroupId) {
		this.sourceSecurityGroupId = sourceSecurityGroupId;
	}

	public void setSourceSecurityGroupName(final String sourceSecurityGroupName) {
		this.sourceSecurityGroupName = sourceSecurityGroupName;
	}

	public void setSourceSecurityGroupOwnerId(
			final String sourceSecurityGroupOwnerId) {
		this.sourceSecurityGroupOwnerId = sourceSecurityGroupOwnerId;
	}

	public void setToPort(final String toPort) {
		this.toPort = toPort;
	}

}
