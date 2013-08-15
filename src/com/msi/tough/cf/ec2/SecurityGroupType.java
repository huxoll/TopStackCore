package com.msi.tough.cf.ec2;

import java.util.List;

import com.msi.tough.cf.CFType;

public class SecurityGroupType extends CFType {
	private String groupDescription;
	private String groupName;
	private List<AuthorizeSecurityGroupIngressType> securityGroupIngress;

	@Override
	public Object getAtt(final String key) {
		return null;
	}

	public String getGroupDescription() {
		return groupDescription;
	}

	public String getGroupName() {
		return groupName;
	}

	public List<AuthorizeSecurityGroupIngressType> getSecurityGroupIngress() {
		return securityGroupIngress;
	}

	@Override
	public Object ref() {
		return groupName;
	}

	public void setGroupDescription(final String groupDescription) {
		this.groupDescription = groupDescription;
	}

	public void setGroupName(final String groupName) {
		this.groupName = groupName;
	}

	public void setSecurityGroupIngress(
			final List<AuthorizeSecurityGroupIngressType> securityGroupIngress) {
		this.securityGroupIngress = securityGroupIngress;
	}
}
