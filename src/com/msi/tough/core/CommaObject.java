package com.msi.tough.core;

import java.util.List;

public class CommaObject extends GenericCommaObject<String> {

	public CommaObject() {
	    super();
	    sampleObj = new String();
	}

	public CommaObject(final List<String> in) {
        super(in);
	}

	public CommaObject(final String in) {
		this();
		setString(in);
	}

    public void setString(final String in) {
        if (in != null) {
            final String[] sp = StringHelper.split(in, getSeparator());
            if (sp != null && sp.length > 0) {
                for (final String s : sp) {
                    final String t = s.trim();
                    if (t != null && t.length() > 0) {
                        add(t);
                    }
                }
            }
        }
    }

	public void add(final String item) {
	    if (!toList().contains(item)) {
	        list.add(item);
	    }
	}

    public boolean isEmpty(String item) {
        return item == null || item.length() == 0;
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
				sb.append(getSeparator());
			}
			sb.append(s);
		}
		return sb.toString();
	}
}
