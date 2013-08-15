/*
 * MapUtil.java
 *
 * MSI Eucalyptus LoadBalancer Project
 * Copyright (C) Momentum SI
 *
 */
package com.msi.tough.core;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.amazonaws.util.DateUtils;

/**
 * Helper class for managing maps
 * 
 * @author raj
 * 
 */
public class MapUtil {
	public static final Map<String, Object> addNode(
			final Map<String, Object> node, final String tag) {
		final Map<String, Object> n = new HashMap<String, Object>();
		node.put("tag", n);
		return n;
	}

	public static void addNode(final Map<String, Object> n, final String tag,
			final BigInteger value) {
		if (value != null) {
			addNode(n, tag, value.toString());
		}
	}

	public static void addNode(final Map<String, Object> node,
			final String tag, final Boolean value) {
		if (value != null) {
			addNode(node, tag, "" + value);
		}
	}

	public static void addNode(final Map<String, Object> n, final String tag,
			final Calendar value) {
		if (value != null) {
			addNode(n, tag, new DateUtils().formatIso8601Date(value.getTime()));
		}
	}

	public static void addNode(final Map<String, Object> n, final String tag,
			final Date value) {
		if (value != null) {
			addNode(n, tag, new DateUtils().formatIso8601Date(value));
		}
	}

	public static void addNode(final Map<String, Object> n, final String tag,
			final Double value) {
		if (value != null) {
			addNode(n, tag, value.toString());
		}
	}

	public static void addNode(final Map<String, Object> n, final String tag,
			final Integer value) {
		if (value != null) {
			addNode(n, tag, value.toString());
		}
	}

	public static void addNode(final Map<String, Object> n, final String tag,
			final Long value) {
		if (value != null) {
			addNode(n, tag, value.toString());
		}
	}

	public static void addNode(final Map<String, Object> node,
			final String tag, final String value) {
		if (value != null && value.length() > 0) {
			node.put(tag, value);
		}
	}

	/**
	 * Create a map from an array of parameters
	 * 
	 * @param <R>
	 *            Generic type of objects in map
	 * @param objects
	 *            Array of name & objects pairs
	 * @return Map<String, R>
	 */
	@SuppressWarnings("unchecked")
	public static <R> Map<String, R> create(final Object... objects) {
		final HashMap<String, R> map = new HashMap<String, R>();
		int i = 0;
		Object k = null;
		R v = null;
		for (final Object o : objects) {
			if (i == 0) {
				k = o;
			} else {
				v = (R) o;
				map.put(k.toString(), v);
			}
			i = 1 - i;
		}
		if (i == 1) {
			throw new RuntimeException("odd.no.args");
		}
		return map;
	}

	public static Map<String, Serializable> createSerailizable(
			final Serializable... objects) {
		final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
		int i = 0;
		Object k = null;
		Serializable v = null;
		for (final Object o : objects) {
			if (i == 0) {
				k = o;
			} else {
				v = (Serializable) o;
				map.put(k.toString(), v);
			}
			i = 1 - i;
		}
		if (i == 1) {
			throw new RuntimeException("odd.no.args");
		}
		return map;
	}

	public static Properties loadProperties(final String file) {
		final Properties props = new Properties();
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			props.load(fr);
			return props;
		} catch (final FileNotFoundException fe) {
			System.out.println("File not found" + file);
			return null;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static <K, V> Map<K, V> merge(final Map<K, V> map1,
			final Map<K, V> map2) {
		final HashMap<K, V> ret = new HashMap<K, V>(map1);
		ret.putAll(map2);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static <T> Map<String, T> toMap(final Map<String, Serializable> in) {
		final Map<String, T> o = new HashMap<String, T>();
		for (final Entry<String, Serializable> i : in.entrySet()) {
			o.put(i.getKey(), (T) i.getValue());
		}
		return o;
	}

	public static Map<String, Serializable> toSerializable(
			final Map<String, ?> in) {
		final Map<String, Serializable> o = new HashMap<String, Serializable>();
		for (final Entry<String, ?> i : in.entrySet()) {
			Serializable s = null;
			try {
				s = (Serializable) i.getValue();
			} catch (final Exception e) {
				throw new RuntimeException("Not serializable " + i.getKey());
			}
			o.put(i.getKey(), s);
		}
		return o;
	}

}
