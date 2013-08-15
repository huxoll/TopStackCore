package com.msi.tough.core;

import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.springframework.util.PropertyPlaceholderHelper;

public class SystemEnvUtils {
	private static Logger logger = Appctx.getLogger(SystemEnvUtils.class
			.getName());

	private static Properties properties = null;

	public static String expand(final String str) {
		if (properties == null) {
			properties = System.getProperties();
			final Map<String, String> env = System.getenv();
			for (final String envName : env.keySet()) {
				properties.put(envName, env.get(envName));
			}
		}
		final PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper(
				"${", "}");
		return helper.replacePlaceholders(str, properties);
	}

	public static void expandMap(final Map<String, Object> map) {
		for (final String k : map.keySet()) {
			final Object v = map.get(k);
			if (v instanceof String && ((String) v).indexOf('$') != -1) {
				final String vn = expand((String) v);
				map.put(k, vn);
				logger.debug("expanding " + v.toString() + " to " + vn);
			}
		}
	}
}
