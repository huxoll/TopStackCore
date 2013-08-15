package com.msi.tough.cf.iam;

import java.util.List;

import com.msi.tough.cf.CFType;

public class GroupType extends CFType {
	private String path;
	private List<PolicyType> policies;
	private String groupName;
	private String arn;

	public String getArn() {
		return arn;
	}

	@Override
	public Object getAtt(String key) {
		if (key.equals("Arn")) {
			return arn;
		}
		return super.getAtt(key);
	}

	public String getGroupName() {
		return groupName;
	}

	public String getPath() {
		return path;
	}

	public List<PolicyType> getPolicies() {
		return policies;
	}

	@Override
	public Object ref() {
		return groupName;
	}

	public void setArn(String arn) {
		this.arn = arn;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setPolicies(List<PolicyType> policies) {
		this.policies = policies;
	}

	@Override
	public String typeAsString() {
		return "AWS::IAM::Group";
	}

}
