package com.msi.tough.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.MappingJsonFactory;

public class ValidateJSON {
	private static final MappingJsonFactory fac = new MappingJsonFactory();

	public static JsonNode load(final InputStream in) throws Exception {
		final JsonParser parser = fac.createJsonParser(in);
		parser.configure(Feature.ALLOW_COMMENTS, true);
		parser.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		parser.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		final JsonNode node = parser.readValueAsTree();
		return node;
	}

	public static void main(final String[] args) throws Exception {
		try {
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(args[0]));
			load(in);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
