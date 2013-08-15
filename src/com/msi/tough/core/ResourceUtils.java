package com.msi.tough.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

public class ResourceUtils {

	/**
	 * Load the resource either using current class loader or using parent class
	 * loader
	 * 
	 * @param fileName
	 * @return
	 */
	public static File getFileFromClasspath(final String fileName) {
		File file = null;

		// Load the resource using current class loader
		URL url = Thread.currentThread().getClass().getResource(fileName);

		if (url != null) {
			file = new File(url.getFile());
		} else {
			// load the resource using the parent context
			url = Thread.currentThread().getContextClassLoader()
					.getResource(fileName);
			if (url != null) {
				file = new File(url.getFile());
			}
		}

		return file;
	}

	public static InputStream getInputStreamFromClasspathFile(
			final String fileName) throws FileNotFoundException {
		return new FileInputStream(getFileFromClasspath(fileName));
	}

	public static String readStream(final InputStream strm) throws Exception {
		final StringBuilder sb = new StringBuilder();
		final byte[] b = new byte[1024];
		for (;;) {
			final int c = strm.read(b);
			if (c <= 0) {
				break;
			}
			sb.append(new String(b, 0, c));
		}
		return sb.toString();
	}
}
