package com.msi.tough.engine.aws.autoscaling;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.autoscaling.AutoScalingGroupType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.core.StringHelper;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.utils.AccountUtil;

public class AutoScalingGroup extends BaseProvider {
	private static Logger logger = Appctx.getLogger(AutoScalingGroup.class
			.getName());

	public static String TYPE = "AWS::AutoScaling::AutoScalingGroup";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		return HibernateUtil.withNewSession(new Operation<CFType>() {

			@SuppressWarnings("unchecked")
			@Override
			public CFType ex(final Session s, final Object... args)
					throws Exception {
				final AccountType ac = call.getAc();
				final String name = call.getName();
				final String availabilityZones = (String) call
						.getProperty("AvailabilityZones");
				final String cooldown = (String) call.getProperty("Cooldown");
				final String desiredCapacity = (String) call
						.getProperty("DesiredCapacity");
				final String healthCheckGracePeriod = (String) call
						.getProperty("HealthCheckGracePeriod");
				final String healthCheckType = (String) call
						.getProperty("HealthCheckType");
				final String launchConfigurationName = (String) call
						.getProperty("LaunchConfigurationName");
				final String loadBalancerNames = (String) call
						.getProperty("LoadBalancerNames");
				final List<String> terminationPolicies = (List<String>) call
						.getProperty("TerminationPolicies");
				final String maxSize = (String) call.getProperty("MaxSize");
				final String minSize = (String) call.getProperty("MinSize");
				final String notificationConfiguration = (String) call
						.getProperty("NotificationConfiguration");
				final String tags = (String) call.getProperty("Tags");

				final ASGroupBean b = new ASGroupBean();
				b.setStackId(call.getStackId());
				b.setAvzones(availabilityZones);
				if (!StringHelper.isBlank(maxSize)) {
					b.setMaxSz(Long.parseLong(maxSize));
				}
				if (!StringHelper.isBlank(minSize)) {
					b.setMinSz(Long.parseLong(minSize));
				}
				if (!StringHelper.isBlank(desiredCapacity)) {
					b.setCapacity(Long.parseLong(desiredCapacity));
				}
				if (b.getCapacity() < b.getMinSz()) {
					b.setCapacity(b.getMinSz());
				}
				if (b.getCapacity() > b.getMaxSz()) {
					b.setCapacity(b.getMaxSz());
				}
				if (!StringHelper.isBlank(cooldown)) {
					final long t = Long.parseLong(cooldown);
					b.setCooldown(t);
					b.setCooldownTime(new Date(System.currentTimeMillis() + t
							* 1000));
				} else {
					b.setCooldownTime(new Date());
				}
				b.setCreatedTime(new Date());
				b.setLaunchConfig(launchConfigurationName);
				b.setLoadBalancers(loadBalancerNames);
				final CommaObject terms = new CommaObject(terminationPolicies);
				b.setTerminationPolicies(terms.toString());
				b.setName(call.getName());
				// b.setTriggers(triggers);
				b.setUserId(ac.getId());
				b.setArn("arn:autoscaling:group:" + ac.getId() + ":"
						+ call.getName());
				if (call.getProperty("HealthCheckGracePeriod") != null) {
					b.setHealthCheckGracePeriod(Integer.parseInt((String) call
							.getProperty("HealthCheckGracePeriod")));
				}
				if (call.getProperty("HealthCheckType") != null) {
					b.setHealthCheckType(healthCheckType);
				}
				s.save(b);

				final AutoScalingGroupType ret = new AutoScalingGroupType();
				ret.setAcId(ac.getId());
				ret.setAvailabilityZones(availabilityZones);
				ret.setCooldown(cooldown);
				ret.setCreatedTime(new Date());
				ret.setHealthCheckGracePeriod(healthCheckGracePeriod);
				ret.setHealthCheckType(healthCheckType);
				ret.setLaunchConfigurationName(launchConfigurationName);
				ret.setLoadBalancerNames(loadBalancerNames);
				ret.setMaxSize(maxSize);
				ret.setMinSize(minSize);
				ret.setName(loadBalancerNames);
				ret.setNotificationConfiguration(notificationConfiguration);
				ret.setTags(tags);
				ret.setUpdatedTime(new Date());
				return ret;
			}
		});
	}

	@Override
	public Resource delete0(final CallStruct call) throws Exception {
		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {
				final AccountType ac = call.getAc();
				final String name = call.getPhysicalId();
				logger.debug("Deleting " + ac.getId() + " " + name);
				final AccountBean acb = AccountUtil.readAccount(s, ac.getId());
				final ASGroupBean g = ASUtil.readASGroup(s, acb.getId(), name);
				ASUtil.deleteASGroup(s, acb, g);
				return null;
			}
		});
		return null;
	}
}
