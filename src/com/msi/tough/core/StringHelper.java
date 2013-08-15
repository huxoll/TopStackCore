/*
 * StringHelper.java
 *
 * MSI Eucalyptus LoadBalancer Project
 * Copyright (C) Momentum SI
 *
 */

package com.msi.tough.core;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Helper class for String
 *
 * @author raj
 *
 */
public class StringHelper {

    public static final String EMPTY_STRING = "";

	public static String addStr(final String str, final String addStr,
			final String concat) {
		if (str == null || str.length() == 0) {
			return addStr;
		}
		return str + concat + addStr;
	}

	public static String[] bottom(final String in, final int bottom) {
		final String[] lines = in.split("\n");
		final String[] ret = new String[bottom];
		final int c = lines.length - bottom;
		for (int i = 0; i < bottom; i++) {
			ret[i] = lines[c + i];
		}
		return ret;
	}

	public static String column(final String in, final int num) {
		final String[] cs = in.split("\t");
		return cs[num];
	}

	public static String concat(final String[] in, final String delim) {
		final StringBuilder sb = new StringBuilder();
		boolean comma = false;
		for (final String s : in) {
			if (comma) {
				sb.append(delim);
			}
			sb.append(s);
			comma = true;
		}
		return sb.toString();
	}

	public static String[] cutLines(final String in, final int top,
			final int bottom) {
		final String[] lines = in.split("\n");
		final String[] ret = new String[lines.length - top - bottom];
		int c = 0;
		for (int i = top; i < lines.length - bottom; i++) {
			ret[c++] = lines[i];
		}
		return ret;
	}

	public static boolean isBlank(final String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static String nullToEmpty(final String str) {
	    if (str == null) {
	        return EMPTY_STRING;
	    }
	    return str;
	}

	public static String emptyToNull(final String str) {
	    if (str != null && str.trim().length() == 0) {
	        return null;
	    }
	    return str;
	}

	public static String join(final String[] arr, final String delim) {
		final StringBuilder sb = new StringBuilder();
		int i = 0;
		for (final String s : arr) {
			if (s == null || s.length() == 0) {
				continue;
			}
			if (i++ != 0) {
				sb.append(",");
			}
			sb.append(s);
		}
		return sb.toString();
	}

	public static String randomStringFromTime() {

		final String nowTicks = Long.toString(new Date().getTime());

		final StringBuilder sb = new StringBuilder();
		for (final Character ch : nowTicks.toCharArray()) {
			final int offset = Integer.parseInt(ch.toString());
			final char chr = (char) (65 + offset);

			sb.append(chr);
		}
		return sb.toString().toLowerCase();
	}

	public static String readFromFile(final String cert) {
		FileReader fr = null;
		final StringBuilder sb = new StringBuilder();
		try {
			fr = new FileReader(cert);
			for (;;) {
				final int c = fr.read();
				if (c == -1) {
					break;
				}
				sb.append((char) c);
			}
			return sb.toString();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (final IOException e) {
				}
			}
		}
	}

	public static String removeBackSlash(final String in) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < in.length(); i++) {
			final char c = in.charAt(i);
			if (c != '\\') {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String removeFirstLast(final String in) {
		return in.substring(1, in.length() - 1);
	}

	public static String[] split(final String s) {
		if (s == null) {
			return null;
		}
		final List<String> l = new ArrayList<String>();
		for (final String i : s.split(",")) {
			if (i == null) {
				continue;
			}
			final String sp = i.trim();
			if (sp.length() == 0) {
				continue;
			}
			l.add(sp);
		}
		return l.toArray(new String[0]);
	}

	public static String[] split(final String s, final String sep) {
		if (s == null) {
			return null;
		}
		final List<String> l = new ArrayList<String>();
		for (final String i : s.split(sep)) {
			if (i == null) {
				continue;
			}
			final String sp = i.trim();
			if (sp.length() == 0) {
				continue;
			}
			l.add(sp);
		}
		return l.toArray(new String[0]);
	}

	public static InputStream toInputStream(final String in) {
		return new ByteArrayInputStream(in.getBytes());
	}

	public static boolean validateName(final String in) {
		return validateRegex(in.toLowerCase(), "^[a-z0-9][-a-z0-9]+[a-z0-9]$");
	}

	public static boolean validateRegex(final String in, final String regex) {
		return Pattern.matches(regex, in);
	}

	/**
	 * Write a string to a file
	 *
	 * @param str
	 *            string to write
	 *
	 * @param path
	 *            file path
	 */
	public static void writeTofile(final String str, final String path) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(path);
			fw.write(str);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (final IOException e) {
				}
			}
		}
	}
}
