package com.msi.tough.openstack;

import java.util.Arrays;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.hibernate.Session;

import com.msi.tough.core.JsonUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.utils.ConfigurationUtil;

public class OpenstackUtil {

	public static String describeInstances(final Session s,
			final AccountBean ac, final String avz, final String instanceId)
			throws Exception {
		String path = null;
		if (instanceId != null) {
			path = "v2/" + ac.getTenant() + "/servers/" + instanceId
					+ "/detail.json";
		} else {
			path = "v2/" + ac.getTenant() + "/servers/detail.json";
		}

		return OpenstackUtil.get(s, ac, path, null, true, "servers");
	}

	public static String ec2toUUID(final Session s, final AccountBean ac,
			final String instanceId, final String avz) throws Exception {
		final String[] parts = instanceId.split("-");
		final String insid = "instance-" + parts[1];
		final String r = describeInstances(s, ac, avz, null);
		final JsonNode n = JsonUtil.load(r);
		final JsonNode srv = n.get("servers");

		if (srv instanceof ArrayNode) {
			final ArrayNode an = (ArrayNode) srv;
			for (int i = 0; i < an.size(); i++) {
				final JsonNode sn = an.get(i);
				final JsonNode nid = sn.get("OS-EXT-SRV-ATTR:instance_name");
				if (nid == null) {
					continue;
				}
				final String vid = nid.getTextValue();
				if (vid.equals(insid)) {
					return sn.get("id").getTextValue();
				}
			}
		}
		return null;
	}

	public static String get(final Session s, final AccountBean ac,
			final String actionPath, final String parameters,
			final boolean addToken, final String operation) throws Exception {
		GetMethod get = null;
		try {
			final String avz = ac.getDefZone();
			final String url = (String) ConfigurationUtil
					.getConfiguration(Arrays.asList(new String[] {
							"Openstack_API_URL", avz, operation }));

			get = new GetMethod(url + "/" + actionPath);
			get.setFollowRedirects(true);
			if (addToken) {
				final String token = OpenstackCredsCache.getToken(s, ac);
				get.addRequestHeader("X-Auth-Token", token);
			}
			get.addRequestHeader("Content-type", "application/json");
			if (parameters != null) {
				get.setQueryString(parameters);
			}
			final HttpClient client = new HttpClient();
			final int resultCode = client.executeMethod(get);
			return get.getResponseBodyAsString();
		} finally {
			if (get != null) {
				get.releaseConnection();
			}
		}
	}

	public static String post(final Session s, final AccountBean ac,
			final String actionPath, final String json, final boolean addToken,
			final String operation) throws Exception {
		PostMethod post = null;
		try {
			final String avz = ac.getDefZone();
			final String url = (String) ConfigurationUtil
					.getConfiguration(Arrays.asList(new String[] {
							"Openstack_API_URL", avz, operation }));

			post = new PostMethod(url + "/" + actionPath);
			if (addToken) {
				final String token = OpenstackCredsCache.getToken(s, ac);
				post.addRequestHeader("X-Auth-Token", token);
			}
			post.addRequestHeader("Content-type", "application/json");
			if (json != null) {
				post.setRequestBody(json);
			}
			final HttpClient client = new HttpClient();
			final int resultCode = client.executeMethod(post);
			return post.getResponseBodyAsString();
		} finally {
			if (post != null) {
				post.releaseConnection();
			}
		}
	}

	public static String UUIDtoEc2(final Session s, final AccountBean ac,
			final String instanceId, final String avz) throws Exception {
		final String r = describeInstances(s, ac, avz, null);
		final JsonNode n = JsonUtil.load(r);
		final JsonNode srv = n.get("servers");

		if (srv instanceof ArrayNode) {
			final ArrayNode an = (ArrayNode) srv;
			for (int i = 0; i < an.size(); i++) {
				final JsonNode sn = an.get(i);
				final JsonNode nid = sn.get("id");
				if (nid == null) {
					continue;
				}
				final String vid = nid.getTextValue();
				if (vid.equals(instanceId)) {
					final JsonNode name = sn
							.get("OS-EXT-SRV-ATTR:instance_name");
					if (name != null) {
						final String snm = name.getTextValue();
						final String[] parts = snm.split("-");
						return "i-" + parts[1];
					}
				}
			}
		}
		return null;
	}
}
