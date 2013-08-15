package com.msi.tough.utils;

/**
 * @author jlomax
 * 
 */
public final class ValidateAWS {

	// private ValidateAWS() { throw new AssertionError(); }

	/**
	 * Test AWS hyphenated id, must start with lower case letter, alphanumeric +
	 * hyphen, cannot have two hypens in a row or end with a hyphen.
	 * 
	 * @param s
	 *            string to test
	 * @param maxLength
	 *            maximum length to allow
	 * @return true if valid
	 */
	public static Boolean isAWSHyphenatedId(final String s, final int maxLength) {
		// Other regex candidates
		// ^[a-zA-Z0-9]+(-[a-zA-Z0-9]+)*$
		// ^[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9]$
		final java.util.regex.Pattern p = java.util.regex.Pattern
				.compile("^[a-z](?:[^-]|-(?!-)){1," + maxLength + "}$");
		final java.util.regex.Matcher m = p.matcher(s.toLowerCase());
		return m.find();
	}

}
