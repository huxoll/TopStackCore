package com.msi.tough.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.elasticbeanstalk.model.EnvironmentDescription;
import com.msi.tough.cf.elasticbeanstalk.OptionSettingsType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.MapUtil;
import com.msi.tough.core.StringHelper;
import com.msi.tough.engine.aws.elasticbeanstalk.ConfigurationTemplateHandler;
import com.msi.tough.model.ApplicationBean;
import com.msi.tough.model.ConfigBean;
import com.msi.tough.model.ConfigTemplateBean;
import com.msi.tough.model.EnvironmentBean;
import com.msi.tough.model.VersionBean;

public class EBSUtil {
	private static Logger logger = Appctx.getLogger(EBSUtil.class.getName());

	public static Set<ConfigBean> copyConfigs(final Set<ConfigBean> options) {
		final Set<ConfigBean> newOpts = new HashSet<ConfigBean>();
		for (final ConfigBean mem : options) {
			final ConfigBean nm = new ConfigBean();
			nm.setNameSpace(mem.getNameSpace());
			nm.setOption(mem.getOption());
			nm.setValue(mem.getValue());
			newOpts.add(nm);
		}
		return newOpts;
	}

	@SuppressWarnings("unchecked")
	public static EnvironmentBean getEnvironment(final Session s,
			final String envId) {
		final String sql = "from EnvironmentBean where env_id=" + "\'" + envId
				+ "\'";
		final Query query = s.createQuery(sql);
		final List<EnvironmentBean> l = query.list();
		if (l == null || l.size() != 1) {
			return null;
		}
		return l.get(0);
	}

	/*
	 * public static Set<OptionSettingsMemberBean> copyOptions( final
	 * Set<OptionSettingsMemberBean> options) { final
	 * Set<OptionSettingsMemberBean> newOpts = new
	 * HashSet<OptionSettingsMemberBean>(); for (final OptionSettingsMemberBean
	 * mem : options) { final OptionSettingsMemberBean nm = new
	 * OptionSettingsMemberBean(); nm.setNamespace(mem.getNamespace());
	 * nm.setOptionName(mem.getOptionName());
	 * nm.setOptionValue(mem.getOptionValue()); newOpts.add(nm); } return
	 * newOpts; }
	 */

	@SuppressWarnings("unchecked")
	public static EnvironmentBean readEnvironment(final Session s,
			final String environmentName) {
		final String sql = "from EnvironmentBean where name=" + "\'"
				+ environmentName + "\'";
		final Query query = s.createQuery(sql);
		final List<EnvironmentBean> l = query.list();
		if (l == null || l.size() != 1) {
			return null;
		}
		return l.get(0);
	}

	@SuppressWarnings("unchecked")
	public static List<ConfigTemplateBean> readTemplate(final Session s,
			final long userId, final String appName, final String envName,
			final String stackName, final String templateName) {
		final List<String> sa = new ArrayList<String>();
		if (appName != null) {
			sa.add("app_name='" + appName + "'");
		}
		if (envName != null) {
			sa.add("env_name='" + envName + "'");
		}
		if (stackName != null) {
			sa.add("stack='" + stackName + "'");
		}
		if (templateName != null) {
			sa.add("name='" + templateName + "'");
		}

		String sql = "";
		for (int i = 0; i < sa.size(); ++i) {
			sql += sa.get(i);
			if (i < sa.size() - 1) {
				sql += " and ";
			}
		}

		String hql = "from ConfigTemplateBean where user_id=" + userId;
		if (!sql.equals("")) {
			hql += " and " + sql;
		}
		logger.debug("HQL in String format: " + hql);
		final Query q = s.createQuery("from ConfigTemplateBean where user_id="
				+ userId + (sql.length() > 0 ? " and " + sql : ""));
		logger.debug("HQL Query to read Configuration Template: "
				+ q.toString());
		return q.list();
	}

	/*
	 * @SuppressWarnings("unchecked") public static List<OptionSettingsBean>
	 * readOptions(final Session s, final long userId, final String appName,
	 * final String envName, final String stackName, final String templateName)
	 * { final List<String> sa = new ArrayList<String>(); if (appName != null) {
	 * sa.add("app_name='" + appName + "'"); } if (envName != null) {
	 * sa.add("env_name='" + envName + "'"); } if (stackName != null) {
	 * sa.add("stack_name='" + stackName + "'"); } if (templateName != null) {
	 * sa.add("template_name='" + templateName + "'"); }
	 * 
	 * String sql = ""; for(int i = 0; i < sa.size(); ++i){ sql += sa.get(i);
	 * if(i < sa.size() - 1){ sql += " and "; } }
	 * 
	 * String hql = "from OptionSettingsBean where user_id=" + userId;
	 * if(!sql.equals("")){ hql += " and " + sql; }
	 * logger.debug("HQL in String format: " + hql); final Query q =
	 * s.createQuery("from OptionSettingsBean where user_id=" + userId +
	 * (sql.length() > 0 ? " and " + sql : ""));
	 * logger.debug("HQL Query to read Options: " + q.toString()); return
	 * q.list(); }
	 */

	/*
	 * public static void removeOptions( final Set<OptionSettingsMemberBean>
	 * newOpts, final List<OptionSettingsType> toRemove) { if(toRemove != null
	 * && toRemove.size() > 0){ for (final OptionSettingsType cs : toRemove) {
	 * for (final OptionSettingsMemberBean mem : newOpts) { if
	 * (mem.getNamespace().equals(cs.getNameSpace()) &&
	 * mem.getOptionName().equals(cs.getOptionName())) { newOpts.remove(mem);
	 * break; } } } } }
	 */

	public static void removeConfigs(final Set<ConfigBean> newOpts,
			final List<OptionSettingsType> toRemove) {
		if (toRemove != null && toRemove.size() > 0) {
			for (final OptionSettingsType cs : toRemove) {
				for (final ConfigBean mem : newOpts) {
					if (mem.getNameSpace().equals(cs.getNameSpace())
							&& mem.getOption().equals(cs.getOptionName())) {
						newOpts.remove(mem);
						break;
					}
				}
			}
		}
	}

	/*
	 * public static void saveOptions(final Session session, final long acid,
	 * final Set<OptionSettingsMemberBean> newOpts, final String srcAppName,
	 * final String envId, final String stack, final String template) { final
	 * OptionSettingsBean newSet = new OptionSettingsBean();
	 * newSet.setUserId(acid); newSet.setAppName(srcAppName);
	 * newSet.setEnvName(envId); newSet.setStackName(stack);
	 * newSet.setTemplateName(template); session.save(newSet); for (final
	 * OptionSettingsMemberBean mem : newOpts) {
	 * mem.setOptionSettingsParent(newSet); session.save(mem); }
	 * newSet.setOptions(newOpts); session.save(newSet); }
	 */

	public static void saveConfigs(final Session session, final long acid,
			final Set<ConfigBean> newOpts, final String srcAppName,
			final String envId, final String stack, final String template) {
		final ConfigTemplateBean newSet = new ConfigTemplateBean();
		newSet.setUserId(acid);
		newSet.setSrcAppName(srcAppName);
		newSet.setEnvName(envId);
		newSet.setStack(stack);
		newSet.setName(template);
		session.save(newSet);
		for (final ConfigBean mem : newOpts) {
			session.save(mem);
		}
		newSet.setConfigs(newOpts);
		session.save(newSet);
	}

	@SuppressWarnings("unchecked")
	public static List<EnvironmentBean> selectEnvironments(final Session s,
			final long userID, final String applicationName,
			final String environmentId, final String environmentName,
			final String versionLabel) {
		final List<String> parts = new ArrayList<String>();
		if (applicationName != null) {
			parts.add("applicationName = '" + applicationName + "'");
		}
		if (environmentId != null) {
			parts.add("envId = '" + environmentId + "'");
		}
		if (environmentName != null) {
			parts.add("name = '" + environmentName + "'");
		}
		if (versionLabel != null) {
			parts.add("version = '" + versionLabel + "'");
		}
		// r.getIncludedDeletedBackTo();
		// r.getIncludeDeleted();
		// r.getRequestClientOptions();
		final String sql = "from EnvironmentBean where userId = "
				+ userID
				+ (parts.size() > 0 ? " and "
						+ StringHelper.concat(parts.toArray(new String[1]),
								" and ") : "") + " order by name";
		logger.info("selectEnvironments: Query is " + sql);
		final Query query = s.createQuery(sql);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public static List<VersionBean> selectVersion(final Session s,
			final long userId, final String applicationName,
			final String version) {
		final List<String> parts = new ArrayList<String>();
		if (applicationName != null) {
			parts.add("applicationName='" + applicationName + "'");
		}
		if (version != null) {
			parts.add("version='" + version + "'");
		}
		final String sql = (parts.size() > 0 ? " and " : "")
				+ StringHelper.concat(parts.toArray(new String[1]), " and ");
		final Query q = s.createQuery("from VersionBean where userId=" + userId
				+ sql);
		return q.list();
	}

	public static EnvironmentDescription toEnvironmentDescription(
			final EnvironmentBean b) {
		final EnvironmentDescription o = new EnvironmentDescription();
		o.setApplicationName(b.getApplicationName());
		o.setCNAME(b.getDnsPrefix());
		o.setDateCreated(b.getCreatedTime());
		o.setDateUpdated(b.getUpdatedTime());
		o.setDescription(b.getDesc());
		o.setEndpointURL(b.getUrl());
		o.setEnvironmentId("" + b.getId());
		o.setEnvironmentName(b.getName());
		o.setHealth(b.getHealth());
		o.setSolutionStackName(b.getStack());
		o.setStatus(b.getStatus());
		o.setTemplateName(b.getTemplate());
		o.setVersionLabel(b.getVersion());
		return o;
	}

	@SuppressWarnings("unchecked")
	public static List<OptionSettingsType> toOptionSettingsList(final Object obj) {
		if (obj == null) {
			return null;
		}
		if (!(obj instanceof List)) {
			throw new RuntimeException("Not a list");
		}
		final List<OptionSettingsType> l = new ArrayList<OptionSettingsType>();
		for (final Object o : (List<Object>) obj) {
			if (o instanceof OptionSettingsType) {
				l.add((OptionSettingsType) o);
				continue;
			} else if (o instanceof JsonNode) {
				final JsonNode j = (JsonNode) o;
				final OptionSettingsType a = new OptionSettingsType();
				if (j.get("NameSpace") != null) {
					a.setNameSpace(j.get("NameSpace").getValueAsText());
				}
				if (j.get("OptionName") != null) {
					a.setOptionName(j.get("OptionName").getValueAsText());
				}
				if (j.get("Value") != null) {
					a.setValue(j.get("Value").getValueAsText());
				}
				l.add(a);
				continue;
			}
			throw new RuntimeException("Couldn't convert " + o);
		}
		return l;
	}

	public static Map<String, Object> toRequestMap(final Session session,
			final ApplicationBean ap) {
		final Map<String, Object> r = new HashMap<String, Object>();
		r.put("Description", ap.getDesc());
		ap.getTemplates();
		final Set<VersionBean> vers = ap.getVersions();
		if (vers != null && vers.size() > 0) {
			final List<Map<String, Object>> vl = new ArrayList<Map<String, Object>>();
			r.put("ApplicationVersions", vl);
			for (final VersionBean v : vers) {
				final Map<String, Object> vm = new HashMap<String, Object>();
				vm.put("Description", v.getDesc());
				vm.put("VersionLabel", v.getVersion());
				final Map<String, Object> bundle = MapUtil.create("S3Bucket",
						v.getS3bucket(), "S3Key", v.getS3key());
				vm.put("SourceBundle", bundle);
				vl.add(vm);
			}
		}
		return r;
	}

	/*
	 * public static void updateOptions( final Set<OptionSettingsMemberBean>
	 * newOpts, final List<OptionSettingsType> optionSettings) {
	 * if(optionSettings != null & optionSettings.size() > 0){ for (final
	 * OptionSettingsType cs : optionSettings) { if
	 * (!ConfigurationTemplateHandler.OptionSettingIsValid( cs.getNameSpace(),
	 * cs.getOptionName(), cs.getValue())) { throw new
	 * RuntimeException("Invalid option " + cs.getNameSpace() + " " +
	 * cs.getOptionName()); } for (final OptionSettingsMemberBean mem : newOpts)
	 * { if (mem.getNamespace().equals(cs.getNameSpace()) &&
	 * mem.getOptionName().equals(cs.getOptionName())) {
	 * mem.setOptionValue(cs.getValue()); break; } } } } }
	 */

	public static void updateConfigs(final Set<ConfigBean> newOpts,
			final List<OptionSettingsType> optionSettings) {
		if (optionSettings != null & optionSettings.size() > 0) {
			for (final OptionSettingsType cs : optionSettings) {
				if (!ConfigurationTemplateHandler.OptionSettingIsValid(
						cs.getNameSpace(), cs.getOptionName(), cs.getValue())) {
					throw new RuntimeException("Invalid option "
							+ cs.getNameSpace() + " " + cs.getOptionName());
				}
				for (final ConfigBean mem : newOpts) {
					if (mem.getNameSpace().equals(cs.getNameSpace())
							&& mem.getOption().equals(cs.getOptionName())) {
						mem.setValue(cs.getValue());
						break;
					}
				}
			}
		}
	}

}