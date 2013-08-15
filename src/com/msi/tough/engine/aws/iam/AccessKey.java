package com.msi.tough.engine.aws.iam;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.iam.AccessKeyType;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;

public class AccessKey extends BaseProvider {
	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		final AccessKeyType res = new AccessKeyType();
		res.setAccessKeyId(ac.getAccessKey());
		res.setSecretAccessKey(ac.getSecretKey());
		return res;
	}

	@Override
	protected boolean isResource() {
		return false;
	}
}
