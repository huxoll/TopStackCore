package com.msi.tough.utils;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.msi.tough.model.CertificateBean;

public class IAMUtil {
	@SuppressWarnings("unchecked")
	public static CertificateBean getCertificate(final Session s, final long id) {
		final Query q = s.createQuery("from CertificateBean where id=" + id);
		final List<CertificateBean> l = q.list();
		if (l != null && l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static CertificateBean getCertificate(final Session s,
			final long acid, final String name) {
		final Query q = s.createQuery("from CertificateBean where accountId="
				+ acid + " and name='" + name + "'");
		final List<CertificateBean> l = q.list();
		if (l != null && l.size() > 0) {
			return l.get(0);
		}
		return null;
	}
}
