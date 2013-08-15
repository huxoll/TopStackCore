package com.msi.tough.cf.ec2;

import com.msi.tough.cf.CFType;

public class KeyPairType extends CFType {
	private String name;
	private String material;

	@Override
	public Object getAtt(final String key) {
		return null;
	}

	public String getMaterial() {
		return material;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object ref() {
		return this;
	}

	public void setMaterial(final String material) {
		this.material = material;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}
}
