package com.msi.tough.cf.ec2;

import com.msi.tough.cf.CFType;

public class VolumeAttachmentType extends CFType {

	@Override
	public Object ref() {
		return getName();
	}

}
