package com.msi.tough.openstack;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.hibernate.Session;

import com.msi.tough.core.JsonUtil;
import com.msi.tough.model.AccountBean;

public class OpenstackCredsCache {
	private static class Data {
		Date validity;
		String token;
	}

	private static final Map<String, Map<Long, Data>> creds = new HashMap<String, Map<Long, Data>>();
	private static final long validSec = 20L * 3600L;

	public static String getToken(final Session s, final AccountBean ac)
			throws Exception {
		final String avz = ac.getDefZone();
		final Long key = ac.getId();
		Map<Long, Data> map = creds.get(ac.getDefZone());
		if (map == null) {
			map = new HashMap<Long, Data>();
			creds.put(avz, map);
		}
		Data ret = map.get(key);
		if (ret == null) {
			ret = newToken(s, ac);
		}
		if (ret.validity.compareTo(new Date()) < 0) {
			ret = newToken(s, ac);
		}
		map.put(key, ret);
		return ret.token;
	}

	private static Data newToken(final Session s, final AccountBean ac)
			throws Exception {
		final Data ret = new Data();
		ret.validity = new Date(System.currentTimeMillis() + validSec * 1000);
		final String json = "{\"auth\":{\"passwordCredentials\":{\"username\":\""
				+ ac.getApiUsername()
				+ "\",\"password\":\""
				+ ac.getApiPassword()
				+ "\"},\"tenantId\":\""
				+ ac.getTenant()
				+ "\"}}";
		final String r = OpenstackUtil.post(s, ac, "v2.0/tokens", json, false,
				"tokens");
		final JsonNode n = JsonUtil.load(r);
		final JsonNode naccess = n.get("access");
		if (naccess != null) {
			final JsonNode ntoken = naccess.get("token");
			if (ntoken != null) {
				ret.token = ntoken.get("id").getTextValue();
			}
		}
		return ret;
	}
}
