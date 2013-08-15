package com.msi.tough.cf.iam;

import java.util.List;

import com.msi.tough.cf.CFType;

public class PolicyType extends CFType {
	private String policyName;
	private String policyDocument;
	private List<GroupType> groups;
	private List<UserType> users;

	@Override
	public Object getAtt(String key) {
		return super.getAtt(key);
	}

	public List<GroupType> getGroups() {
		return groups;
	}

	public String getPolicyDocument() {
		return policyDocument;
	}

	public String getPolicyName() {
		return policyName;
	}

	public List<UserType> getUsers() {
		return users;
	}

	public void setGroups(List<GroupType> groups) {
		this.groups = groups;
	}

	public void setPolicyDocument(String policyDocument) {
		this.policyDocument = policyDocument;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public void setUsers(List<UserType> users) {
		this.users = users;
	}

	@Override
	public String typeAsString() {
		return "AWS::IAM::Policy";
	}

}
