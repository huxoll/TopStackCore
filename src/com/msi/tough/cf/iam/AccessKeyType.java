package com.msi.tough.cf.iam;

import com.msi.tough.cf.CFType;

public class AccessKeyType extends CFType {
	String serial;
	String status;
	String userName;
	String accessKeyId;
	String secretAccessKey;

	public String getAccessKeyId() {
		return accessKeyId;
	}

	@Override
	public Object getAtt(String key) {
		if (key.equals("SecretAccessKey")) {
			return secretAccessKey;
		}
		return super.getAtt(key);
	}

	public String getSecretAccessKey() {
		return secretAccessKey;
	}

	public String getSerial() {
		return serial;
	}

	public String getStatus() {
		return status;
	}

	public String getUserName() {
		return userName;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public void setSecretAccessKey(String secretAccessKey) {
		this.secretAccessKey = secretAccessKey;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String typeAsString() {
		return "AWS::IAM::AccessKey";
	}

}
