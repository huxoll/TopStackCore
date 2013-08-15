package com.msi.tough.cf.ec2;

import com.msi.tough.cf.CFType;

public class AllocateAddressType extends CFType {
	private String availabilityZone;
	private String publicIp;
	private String instanceId;
	private String status;

	@Override
	public Object getAtt(final String key) {
		if (key.equals("PublicIp")) {
			return publicIp;
		}
		return null;
	}

	public String getAvailabilityZone() {
		return availabilityZone;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public String getPublicIp() {
		return publicIp;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public Object ref() {
		return this;
	}

	public void setAvailabilityZone(final String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}

	public void setInstanceId(final String instanceId) {
		this.instanceId = instanceId;
	}

	public void setPublicIp(final String publicIp) {
		this.publicIp = publicIp;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

}
