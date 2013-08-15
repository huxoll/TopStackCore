package com.msi.tough.cf.elasticache;

import com.msi.tough.cf.CFType;

public class ParameterGroupType extends CFType {
	@Override
	public Object ref() {
		return getName();
	}
}
