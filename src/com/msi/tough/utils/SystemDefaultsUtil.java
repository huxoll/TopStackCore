package com.msi.tough.utils;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.msi.tough.model.SystemDefaultsBean;

public class SystemDefaultsUtil {
	@SuppressWarnings("unchecked")
	public static String getDefault(Session sess, String type) {
		//String sql = "from SystemDefaultsBean where type='" + type + "' and def=true";
		String sql = "from SystemDefaultsBean where type='" + type + "' and def=true";
		Query query = sess.createQuery(sql);
		List<SystemDefaultsBean> l = query.list();
		if (l == null || l.size() != 1) {
			return null;
		}
		return l.get(0).getValue();
	}

	@SuppressWarnings("unchecked")
	public static List<SystemDefaultsBean> getDefaults(Session sess, String type) {
		String sql = "from SystemDefaultsBean where type='" + type + "'";
		Query query = sess.createQuery(sql);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public static String getSystemKeyValue(Session sess, String type, String key) {
		String sql = "from SystemDefaultsBean where type='" + type
				+ "' and key='" + key + "'";
		Query query = sess.createQuery(sql);
		List<SystemDefaultsBean> l = query.list();
		if (l == null || l.size() != 1) {
			return null;
		}
		return l.get(0).getValue();
	}
}
