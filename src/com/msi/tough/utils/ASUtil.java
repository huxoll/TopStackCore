package com.msi.tough.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.msi.tough.cf.elasticloadbalancing.LoadBalancerType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.ASActivityLog;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.ASPolicyBean;
import com.msi.tough.model.ASScheduledBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.InstanceBean;
import com.msi.tough.model.LaunchConfigBean;
import com.msi.tough.model.LoadBalancerBean;

public class ASUtil {
	private static Logger logger = Appctx.getLogger(ASUtil.class.getName());

	// public static ASGroupBean create(Session s, BaseProvider task) {
	// ASGroupBean b = new ASGroupBean();
	// final Account ac = (Account) task.getRequiredProperty("__ACCOUNT__");
	// final String availabilityZones = (String) task
	// .getRequiredProperty("AvailabilityZones");
	// final String coolDown = (String) task.getProperty("CoolDown");
	// final String desiredCapacity = (String) task
	// .getProperty("DesiredCapacity");
	// final String healthCheckGracePeriod = (String) task
	// .getProperty("HealthCheckGracePeriod");
	// final String healthCheckType = (String) task
	// .getProperty("HealthCheckType");
	// final String launchConfigurationName = (String) task
	// .getRequiredProperty("LaunchConfigurationName");
	// final String loadBalancerNames = (String) task
	// .getProperty("LoadBalancerNames");
	// final String maxSize = (String) task.getRequiredProperty("MaxSize");
	// final String minSize = (String) task.getRequiredProperty("MinSize");
	// final String notificationConfiguration = (String) task
	// .getProperty("NotificationConfiguration");
	// final String vpcZoneIdentifier = (String) task
	// .getProperty("VPCZoneIdentifier");
	// b.setAvzones(availabilityZones);
	// b.setCapacity(Long.parseLong(desiredCapacity));
	// b.setCooldown(Long.parseLong(coolDown));
	// b.setCreatedTime(new Date());
	// b.setLaunchConfig(launchConfigurationName);
	// b.setLoadBalancers(loadBalancerNames);
	// b.setMaxSz(Long.parseLong(maxSize));
	// b.setMinSz(Long.parseLong(minSize));
	// b.setName((String) task.getRequiredProperty("__NAME__"));
	// b.setUserId(ac.getId());
	// return b;
	// }

	public static List<String> deleteASGroup(final Session s,
			final AccountBean ac, final ASGroupBean g) {
		if (g == null) {
			return new ArrayList<String>();
		}
		for (final ASPolicyBean i : readASPolicyForGroup(s, ac.getId(),
				g.getName())) {
			s.delete(i);
		}
		for (final ASScheduledBean i : readScheduledForGroup(s, ac.getId(),
				g.getName())) {
			s.delete(i);
		}
		final CommaObject insts = new CommaObject(g.getInstances());
		s.delete(g);
		return insts.toList();
	}

	public static void deregisterAutoScalingInstace(final Session s,
			final AccountBean ac, final String grpName, final String instanceId) {
		logger.debug("reading instance");
		final InstanceBean ib = InstanceUtil.getInstance(s, instanceId);
		logger.debug("reading group");
		final ASGroupBean g = readASGroup(s, ac.getId(), grpName);
		if (g.getInstances() == null) {
			return;
		}
		final CommaObject co = new CommaObject(g.getInstances());
		co.remove(instanceId);
		g.setInstances(co.toString());
		logger.debug("saving grp");
		s.save(g);
		s.delete(ib);
		logger.debug("returning");
	}

	public static boolean executeASPolicy(final Session s, final long acid,
			final String policyName) {
		final ASPolicyBean b = readASPolicy(s, acid, policyName);
		final ASGroupBean grp = readASGroup(s, acid, b.getGrpName());
		if (grp.getCooldownTime() != null
				&& grp.getCooldownTime().compareTo(new Date()) > 0) {
			return false;
		}
		long newCap = grp.getCapacity();
		if (b.getAdjustmentType().equals(Constants.CHANGEINCAPACITY)) {
			newCap = grp.getCapacity() + b.getScalingAdjustment();
		}
		if (b.getAdjustmentType().equals(Constants.EXACTCAPACITY)) {
			newCap = b.getScalingAdjustment();
		}
		if (b.getAdjustmentType().equals(Constants.PERCENTCHANGEINCAPACITY)) {
			newCap = grp.getCapacity() + grp.getCapacity()
					* b.getScalingAdjustment() / 100;
		}
		if (newCap > grp.getMaxSz()) {
			newCap = grp.getMaxSz();
		}
		if (newCap < grp.getMinSz()) {
			newCap = grp.getMinSz();
		}
		final long diff = newCap - grp.getCapacity();
		if (diff != 0) {
			grp.setCapacity(newCap);
			if (grp.getCooldownTime() != null) {
				grp.setCooldownTime(new Date(System.currentTimeMillis()
						+ grp.getCooldown() * 1000));
			}
			s.save(grp);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static List<ASActivityLog> readASActivityLog(final Session session,
			final long acid, final long grpId) {
		final Query q = session.createQuery("from ASActivityLog where userId="
				+ acid + " and grpId=" + grpId);
		return q.list();
	}

	@SuppressWarnings("unchecked")
	public static List<ASGroupBean> readASGroup(final Session session,
			final long acid) {
		final Query q = session.createQuery("from ASGroupBean where userId="
				+ acid + " order by name");
		final List<ASGroupBean> l = q.list();
		if (l == null || l.size() == 0) {
			return null;
		}
		return l;
	}

	@SuppressWarnings("unchecked")
	public static ASGroupBean readASGroup(final Session session,
			final long acid, final String grpName) {
		final Query q = session.createQuery("from ASGroupBean where userId="
				+ acid + " and name='" + grpName + "'");
		final List<ASGroupBean> l = q.list();
		if (l == null || l.size() == 0) {
			return null;
		}
		return l.get(0);
	}

	@SuppressWarnings("unchecked")
	public static ASPolicyBean readASPolicy(final Session session,
			final long acid, final String name) {
		final Query q = session.createQuery("from ASPolicyBean where userId="
				+ acid + " and name='" + name + "'");
		final List<ASPolicyBean> l = q.list();
		if (l == null || l.size() == 0) {
			return null;
		}
		return l.get(0);
	}

	@SuppressWarnings("unchecked")
	public static List<ASPolicyBean> readASPolicyForGroup(
			final Session session, final long acid, final String grpName) {
		final Query q = session.createQuery("from ASPolicyBean where userId="
				+ acid + " and grpName='" + grpName + "'");
		return q.list();
	}

	public static List<LaunchConfigBean> readLaunchConfig(
			final Session session, final long acid) {
		final Query q = session
				.createQuery("from LaunchConfigBean where userId=" + acid
						+ " order by name");
		final List<LaunchConfigBean> l = q.list();
		if (l == null || l.size() == 0) {
			return null;
		}
		return l;
	}

	@SuppressWarnings("unchecked")
	public static LaunchConfigBean readLaunchConfig(final Session session,
			final long acid, final String launchConfigName) {
		final Query q = session
				.createQuery("from LaunchConfigBean where userId=" + acid
						+ " and name='" + launchConfigName + "'");
		final List<LaunchConfigBean> l = q.list();
		if (l == null || l.size() == 0) {
			return null;
		}
		return l.get(0);
	}

	@SuppressWarnings("unchecked")
	public static ASScheduledBean readScheduled(final Session session,
			final long acid, final String grpName, final String policyName) {
		final Query q = session
				.createQuery("from ASScheduledBean where userId=" + acid
						+ " and grpName='" + grpName + "' and name='"
						+ policyName + "'");
		final List<ASScheduledBean> l = q.list();
		if (l == null || l.size() == 0) {
			return null;
		}
		return l.get(0);
	}

	@SuppressWarnings("unchecked")
	public static List<ASScheduledBean> readScheduledForGroup(
			final Session session, final long acid, final String grpName) {
		final Query q = session
				.createQuery("from ASScheduledBean where userId=" + acid
						+ " and grpName='" + grpName + "'");
		return q.list();
	}

	public static void reconfigAddInstance(final Session s,
			final ASGroupBean asgrp, final String physicalId) throws Exception {
		final String lb = asgrp.getLoadBalancers();

		final LoadBalancerBean lbean = LoadBalancerUtil.read(s,
				asgrp.getUserId(), lb);
		if (lbean == null) {
			logger.error("Instance LB not found " + lb);
			return;
		}

		final LoadBalancerType lbtype = LoadBalancerUtil.toLoadBalancerType(s,
				lbean);

		final List<String> instances = lbtype.getInstances();
		instances.add(physicalId);

		final String script = LoadBalancerUtil.toJson(lbtype);
		CFUtil.runAWSScript(s, lbtype.getStackId(), asgrp.getUserId(), script,
				new TemplateContext(null), false);

	}

	public static void reconfigLBInstance(final Session s,
			final ASGroupBean asgrp, final boolean remove,
			final String physicalId) throws Exception {
		final CommaObject co = new CommaObject(asgrp.getLoadBalancers());
		for (final String lb : co.toList()) {
			final LoadBalancerBean lbean = LoadBalancerUtil.read(s,
					asgrp.getUserId(), lb);
			if (lbean == null) {
				logger.error("Instance LB not found " + lb);
				return;
			}

			final LoadBalancerType lbtype = LoadBalancerUtil
					.toLoadBalancerType(s, lbean);

			final List<String> instances = lbtype.getInstances();
			if (remove) {
				instances.remove(physicalId);
			} else {
				instances.add(physicalId);
			}

			final String script = LoadBalancerUtil.toJson(lbtype);
			CFUtil.runAWSScript(s, lbtype.getStackId(), asgrp.getUserId(),
					script, new TemplateContext(null), false);
		}
	}

	public static AutoScalingGroup toAutoScalingGroup(final ASGroupBean g) {
		final AutoScalingGroup b = new AutoScalingGroup();
		// b.setAutoScalingGroupARN(autoScalingGroupARN);
		b.setAutoScalingGroupName(g.getName());
		// b.setAvailabilityZones(availabilityZones);
		// b.setCreatedTime(createdTime);
		b.setDefaultCooldown((int) g.getCooldown());
		b.setDesiredCapacity((int) g.getCapacity());
		// b.setEnabledMetrics(enabledMetrics);
		// b.setHealthCheckGracePeriod(g.get);
		// b.setHealthCheckType(healthCheckType);
		// b.setInstances(instances);
		b.setLaunchConfigurationName(g.getLaunchConfig());
		final CommaObject co = new CommaObject(g.getLoadBalancers());
		b.setLoadBalancerNames(co.toList());
		b.setMaxSize((int) g.getMaxSz());
		b.setMinSize((int) g.getMinSz());
		// b.setPlacementGroup(placementGroup);
		// b.setSuspendedProcesses(suspendedProcesses);
		// b.setVPCZoneIdentifier(vPCZoneIdentifier);
		return b;
	}
}
