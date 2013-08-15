package com.msi.tough.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.msi.tough.core.Appctx;

public class ConfigurationUtil {
	public static String getCloudType(final String availabilityZone) {
		return (String) getConfiguration("CloudType", availabilityZone);
	}

	@SuppressWarnings("unchecked")
	public static Object getConfiguration(final List<String> args) {
		final String key = args.get(0).toString();
		final String module = args.size() >= 2 ? args.get(1).toString() : null;
		final String subModule = args.size() >= 3 ? args.get(2).toString()
				: null;
		final String subModule2 = args.size() >= 4 ? args.get(3).toString()
				: null;
		final Map<String, Object> cfg = Appctx.getConfiguration();
		Object ret = cfg.get(key);
		Map<String, Object> m = cfg;
		if (module != null) {
			final Object o = m.get(module);
			if (!(o instanceof Map)) {
				return ret;
			}
			m = (Map<String, Object>) o;
			if (m.containsKey(key)) {
				ret = m.get(key);
			}
			if (subModule != null) {
				final Object so = m.get(subModule);
				if (!(so instanceof Map)) {
					return ret;
				}
				m = (Map<String, Object>) so;
				if (m.containsKey(key)) {
					ret = m.get(key);
				}
				if (subModule2 != null) {
					final Object so2 = m.get(subModule2);
					if (!(so2 instanceof Map)) {
						return ret;
					}
					m = (Map<String, Object>) so2;
					if (m.containsKey(key)) {
						ret = m.get(key);
					}
				}
			}
		}
		return ret;
	}

	public static Object getConfiguration(final String... args) {
		return getConfiguration(Arrays.asList(args));
	}

}
