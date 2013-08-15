package com.msi.tough.cf.iam;

import com.msi.tough.cf.CFType;

public class LoginProfileType extends CFType {
	private String password;

	@Override
	public Object getAtt(String key) {
		if (key.equals("Password")) {
			return password;
		}
		return super.getAtt(key);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String typeAsString() {
		return "AWS::IAM::User";
	}

}
