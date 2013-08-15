package com.msi.tough.cf.ec2;

import com.msi.tough.engine.resource.Resource;

public class AssociateAddressType implements Resource {
	private String availabilityZone;
	private String publicIp;

	@Override
	public Object getAtt(String key) {
		if (key.equals("PublicIp")) {
			return publicIp;
		}
		return null;
	}

	public String getAvailabilityZone() {
		return availabilityZone;
	}

	public String getPublicIp() {
		return publicIp;
	}

	@Override
	public Object ref() {
		return this;
	}

	public void setAvailabilityZone(String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}

	public void setPublicIp(String publicIp) {
		this.publicIp = publicIp;
	}

}
