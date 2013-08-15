package com.msi.tough.cf.elasticloadbalancing;

import java.util.Map;

import com.msi.tough.cf.CFType;

public class AppCookieStickinessPolicyType extends CFType implements
		PolicyNameType {
	private String cookieName;
	private String policyName;

	public String getCookieName() {
		return cookieName;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setCookieName(final String cookieName) {
		this.cookieName = cookieName;
	}

	public void setPolicyName(final String policyName) {
		this.policyName = policyName;
	}

	@Override
	public Map<String, Object> toMap() throws Exception {
		final Map<String, Object> map = super.toMap();
		map.put("CookieName", cookieName);
		map.put("PolicyName", policyName);
		return map;
	}

}
