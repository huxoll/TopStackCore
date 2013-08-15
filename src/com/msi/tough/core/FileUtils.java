package com.msi.tough.core;

public class FileUtils {
	public static void chmod(final String path, final String mode)
			throws Exception {
		final String[] cmdarray = new String[] { "chmod", mode, path };
		Runtime.getRuntime().exec(cmdarray, null, null);
	}
}
