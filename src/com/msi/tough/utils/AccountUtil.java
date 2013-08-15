package com.msi.tough.utils;

import java.util.HashSet;
import java.util.Iterator;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.cf.AccountType;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.ApplicationBean;
import com.msi.tough.model.ConfigBean;
import com.msi.tough.model.ConfigTemplateBean;
import com.msi.tough.model.LoadBalancerBean;
import com.msi.tough.model.VersionBean;

public class AccountUtil {
	private static Logger logger = Appctx
			.getLogger(AccountUtil.class.getName());

	public static void addApplication(final AccountBean ac,
			final ApplicationBean ap) {
		if (ac.getApplications() == null) {
			ac.setApplications(new HashSet<ApplicationBean>());
		}
		ac.getApplications().add(ap);
	}

	public static void addASLoadBalancer(final ASGroupBean g,
			final LoadBalancerBean lb) {
		if (g.getLoadBalancers() == null) {
			g.setLoadBalancers(g.getLoadBalancers() + ",");
		}
		g.setLoadBalancers(g.getLoadBalancers() + lb);
	}

	//
	// public static void addLaunchConfig(final AccountBean ac,
	// final LaunchConfigBean lb) {
	// if (ac.getLaunchConfigs() == null) {
	// ac.setLaunchConfigs(new HashSet<LaunchConfigBean>());
	// }
	// ac.getLaunchConfigs().add(lb);
	// }

	public static void addTemplate(final ApplicationBean ap,
			final ConfigTemplateBean cb) {
		if (ap.getTemplates() == null) {
			ap.setTemplates(new HashSet<ConfigTemplateBean>());
		}
		ap.getTemplates().add(cb);
	}

	public static void addTemplateConfig(final ConfigTemplateBean cb,
			final ConfigBean cfb) {
		if (cb.getConfigs() == null) {
			cb.setConfigs(new HashSet<ConfigBean>());
		}
		cb.getConfigs().add(cfb);
	}

	public static void addVersion(final ApplicationBean apb,
			final VersionBean vb) {
		if (apb.getVersions() == null) {
			apb.setVersions(new HashSet<VersionBean>());
		}
		apb.getVersions().add(vb);

	}

	public static VersionBean getVersion(final Session s,
			final ApplicationBean ap, final String version) {
		if (ap.getVersions() == null) {
			return null;
		}
		for (final VersionBean vb : ap.getVersions()) {
			if (vb.getVersion().equals(version)) {
				return vb;
			}
		}
		return null;
	}

	/**
	 * Read a load balancer configuration from database
	 *
	 * @param session
	 *            hibernate session to use
	 * @param name
	 *            name of the load balancer
	 * @return load balancer from database
	 */
	public static AccountBean readAccount(final Session session, final Long acid) {
		try {
			return (AccountBean) session
					.load(AccountBean.class, new Long(acid));
		} catch (final ObjectNotFoundException e) {
			return null;
		}
	}

	public static AccountBean readAccount(final Session session,
	        final String accessKey) {
	    try {
	        final Query q = session
	                .createQuery("from AccountBean where accessKey = '"
	                        + accessKey + "'");
	        q.setMaxResults(1);
	        AccountBean account = (AccountBean) q.uniqueResult();
	        if (account == null) {
	            return null;
	        }
	        account.getId();
	        session.evict(account);
	    return account;
	    } catch (final Exception e) {
	        logger.warn("Exception loading account; returning null.");
	        return null;
	    }
	}

	@SuppressWarnings("unchecked")
	public static AccountBean readAccountApi(final Session session,
			final String apiKey) {
		try {
		    if (apiKey == null) {
		        return null;
		    }
			final Query q = session
					.createQuery("from AccountBean where apiUsername= '"
							+ apiKey + "'");
			// q.list();
			final Iterator<AccountBean> i = q.iterate();
			if (!i.hasNext()) {
				return null;
			}
			final AccountBean o = i.next();
			return o;
		} catch (final Exception e) {
			return null;
		}
	}

	public static ApplicationBean readApplication(final Session session,
			final AccountBean ac, final String applicationName) {
		for (final ApplicationBean b : ac.getApplications()) {
			if (b.getName().equals(applicationName)) {
				return b;
			}
		}
		return null;
	}

	public static VersionBean readApplicationVersion(final Session s,
			final ApplicationBean apb, final String version) {
		for (final VersionBean b : apb.getVersions()) {
			if (b.getVersion().equals(version)) {
				return b;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static ConfigTemplateBean readDefaultConfigurationTemplate(
			final Session session) {
		final String templateName = "DefaultConfiguration";
		final Query q = session
				.createQuery("from ConfigTemplateBean where name = '"
						+ templateName + "'");
		final Iterator<ConfigTemplateBean> i = q.iterate();
		if (!i.hasNext()) {
			logger.debug("DefaultConfiguration is not found. Returning null.");
			return null;
		}
		final ConfigTemplateBean o = i.next();
		logger.debug("DefaultConfiguration is found. Returning the target object.");
		return o;
	}

	public static VersionBean readLatestApplicationVersion(final Session s,
			final ApplicationBean apb) {
		try {
			final Query q = s
					.createQuery("from VersionBean where application_name = '"
							+ apb.getName() + "'"
							+ " order by updated_time DESC");
			// q.list();
			@SuppressWarnings("unchecked")
            final Iterator<VersionBean> i = q.iterate();
			if (!i.hasNext()) {
				return null;
			}
			final VersionBean o = i.next();
			return o;
		} catch (final Exception e) {
			return null;
		}
	}

	public static ConfigTemplateBean readTemplate(final Session session,
			final ApplicationBean ap, final String templateName) {
		for (final ConfigTemplateBean tb : ap.getTemplates()) {
			if (tb.getName().equals(templateName)) {
				return tb;
			}
		}
		return null;
	}

	//
	// @SuppressWarnings("unchecked")
	// public static List<ResourcesBean> selectResources(final Session s,
	// final long userId, final String stackName) {
	// String sql = "from ResourcesBean where userId=" + userId;
	// if (stackName != null) {
	// sql += " and stackName='" + stackName + "'";
	// }
	// final Query q = s.createQuery(sql);
	// return q.list();
	// }

	public static AccountType toAccount(final AccountBean b) {
		final AccountType ac = new AccountType();
		ac.setAccessKey(b.getAccessKey());
		ac.setDefKeyName(b.getDefKeyName());
		ac.setDefSecurityGroups(b.getDefSecurityGroups());
		ac.setId(b.getId());
		ac.setName(b.getName());
		ac.setSecretKey(b.getSecretKey());
		ac.setDefZone(b.getDefZone());
		ac.setTenant(b.getTenant());
		return ac;
	}
}
