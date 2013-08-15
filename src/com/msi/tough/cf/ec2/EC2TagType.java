package com.msi.tough.cf.ec2;

import com.msi.tough.cf.CFType;

public class EC2TagType extends CFType {
	private String key;
	private String value;

	@Override
	public Object getAtt(final String key) {
		return null;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	@Override
	public Object ref() {
		return this;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public void setValue(final String value) {
		this.value = value;
	}

}
