package com.msi.tough.cf.elasticache;

import com.msi.tough.cf.CFType;

public class SecurityGroupIngressType extends CFType {
	@Override
	public Object ref() {
		return getName();
	}
}
