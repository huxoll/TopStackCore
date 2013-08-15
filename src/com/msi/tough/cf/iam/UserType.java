package com.msi.tough.cf.iam;

import java.util.List;

import com.msi.tough.cf.CFType;

public class UserType extends CFType {
	String path;
	List<GroupType> groups;
	LoginProfileType loginProfile;
	List<PolicyType> policies;
	String userName;
	String arn;

	@Override
	public Object getAtt(String key) {
		if (key.equals("Arn")) {
			return arn;
		}
		return super.getAtt(key);
	}

	@Override
	public Object ref() {
		return userName;
	}

	@Override
	public String typeAsString() {
		return "AWS::IAM::User";
	}

}
