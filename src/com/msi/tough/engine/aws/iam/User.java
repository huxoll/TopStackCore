package com.msi.tough.engine.aws.iam;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.iam.UserType;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;

public class User extends BaseProvider {
	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		final UserType res = new UserType();
		res.setAcId(ac.getId());
		return res;
	}

	@Override
	protected boolean isResource() {
		return false;
	}
}
