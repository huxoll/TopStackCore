package com.msi.tough.utils;

import java.util.Arrays;

public class AVZUtil {
	public static String getCloudType(String zone) {
		if (zone == null) {
			return null;
		}
		return (String) ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { "CloudType", zone }));
	}
}
