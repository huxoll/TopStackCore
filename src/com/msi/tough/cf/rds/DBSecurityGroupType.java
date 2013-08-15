package com.msi.tough.cf.rds;

import com.msi.tough.cf.CFType;

public class DBSecurityGroupType extends CFType {

	@Override
	public Object ref() {
		return getName();
	}
}
