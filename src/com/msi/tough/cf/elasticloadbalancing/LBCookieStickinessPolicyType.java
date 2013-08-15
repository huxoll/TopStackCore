package com.msi.tough.cf.elasticloadbalancing;

import java.util.Map;

import com.msi.tough.cf.CFType;

public class LBCookieStickinessPolicyType extends CFType implements
		PolicyNameType {
	private String cookieExpirationPeriod;
	private String policyName;

	public String getCookieExpirationPeriod() {
		return cookieExpirationPeriod;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setCookieExpirationPeriod(final String cookieExpirationPeriod) {
		this.cookieExpirationPeriod = cookieExpirationPeriod;
	}

	public void setPolicyName(final String policyName) {
		this.policyName = policyName;
	}

	@Override
	public Map<String, Object> toMap() throws Exception {
		final Map<String, Object> map = super.toMap();
		map.put("CookieExpirationPeriod", cookieExpirationPeriod);
		map.put("PolicyName", policyName);
		return map;
	}

}
