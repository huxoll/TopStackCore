package com.msi.tough.core;

import java.util.ArrayList;
import java.util.List;

public class SlashObject {
	private String separator = "/";
	private List<String> list;

	public SlashObject() {
		list = new ArrayList<String>();
	}

	public SlashObject(final List<String> in) {
		list = in == null ? new ArrayList<String>() : new ArrayList<String>(in);
	}

	public SlashObject(final String in) {
		this();
		if (in != null) {
			final String[] sp = StringHelper.split(in, separator);
			if (sp != null && sp.length > 0) {
				for (final String s : sp) {
					final String t = s.trim();
					if (t != null && t.length() > 0) {
						list.add(t);
					}
				}
			}
		}
	}

	public void add(final String str) {
		if (!toList().contains(str)) {
			list.add(str);
		}
	}

	public List<String> getList() {
		return list;
	}

	public String getSeparator() {
		return separator;
	}

	public void remove(final String str) {
		list.remove(str);
	}

	public void setList(final List<String> list) {
		this.list = list;
	}

	public void setSeparator(final String separator) {
		this.separator = separator;
	}

	public String[] toArray() {
		return list.toArray(new String[0]);
	}

	public List<String> toList() {
		return list;
	}

	@Override
	public String toString() {
		if (list.size() == 0) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		int i = 0;
		for (final String s : list) {
			if (s == null || s.length() == 0) {
				continue;
			}
			if (i++ != 0) {
				sb.append(separator);
			}
			sb.append(s);
		}
		return sb.toString();
	}
}
