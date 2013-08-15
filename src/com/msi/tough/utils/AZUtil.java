package com.msi.tough.utils;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.msi.tough.model.AZMapBean;

public class AZUtil {
	@SuppressWarnings("unchecked")
	public static String getMap(Session session, String azone, String type,
			String key) {
		Query q = session
				.createQuery("from AZMapBean where availabilityZone ='" + azone
						+ "' and mapType='" + type + "' and mapKey = '" + key
						+ "'");
		List<AZMapBean> l = q.list();
		if (l == null || l.size() != 1) {
			return null;
		}
		return l.get(0).getMapValue();
	}
}
