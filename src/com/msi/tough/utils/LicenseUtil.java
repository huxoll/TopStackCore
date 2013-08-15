package com.msi.tough.utils;

import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;

import org.dom4j.Element;

public class LicenseUtil {
	public static byte[] dataMD5(final Element data) throws Exception {
		final Writer sw = new StringWriter();
		data.write(sw);
		sw.close();
		final String dataStr = sw.toString();
		final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(dataStr.getBytes());
		return messageDigest.digest();
	}
}
