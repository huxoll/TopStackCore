package com.msi.tough.engine.aws.ec2;

import org.slf4j.Logger;

import com.msi.tough.cf.CFType;
import com.msi.tough.core.Appctx;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;

public class VolumeAttachment extends BaseProvider {
	private static Logger logger = Appctx.getLogger(VolumeAttachment.class
			.getName());
	public static String TYPE = "AWS::EC2::VolumeAttachment";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		return Volume.attach(call);
	}

	@Override
	protected boolean isResource() {
		return true;
	}
}
