package com.msi.tough.engine.aws.elasticloadbalancing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.dasein.cloud.CloudProvider;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.ec2.InstanceType;
import com.msi.tough.cf.elasticloadbalancing.AppCookieStickinessPolicyType;
import com.msi.tough.cf.elasticloadbalancing.HealthCheckType;
import com.msi.tough.cf.elasticloadbalancing.LBCookieStickinessPolicyType;
import com.msi.tough.cf.elasticloadbalancing.ListenerType;
import com.msi.tough.cf.elasticloadbalancing.LoadBalancerType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.core.MapUtil;
import com.msi.tough.engine.aws.ec2.Instance;
import com.msi.tough.engine.aws.ec2.SecurityGroupIngress;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.model.InstanceBean;
import com.msi.tough.model.ListenerBean;
import com.msi.tough.model.LoadBalancerBean;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.ChefUtil;
import com.msi.tough.utils.ConfigurationUtil;
import com.msi.tough.utils.Constants;
import com.msi.tough.utils.InstanceUtil;
import com.msi.tough.utils.LoadBalancerUtil;
import com.msi.tough.utils.SecurityGroupUtils;

/**
 * Load Balancer provider
 * 
 * @author raj
 * 
 */
public class LoadBalancer extends BaseProvider {
	private final static Logger logger = Appctx.getLogger(LoadBalancer.class
			.getName());
	public static String TYPE = "AWS::ElasticLoadBalancing::LoadBalancer";

	@SuppressWarnings("unchecked")
	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		final String name = call.getName();
		final List<Object> listeners = (List<Object>) call
				.getProperty("Listeners");

		logger.debug("name=" + name + " " + call.toString());

		// get resource properties
		final LoadBalancerType lbt = new LoadBalancerType();
		final List<ListenerType> ltypes = new ArrayList<ListenerType>();
		final Object[] objs = HibernateUtil
				.withNewSession(new Operation<Object[]>() {

					@Override
					public Object[] ex(final Session session,
							final Object... args) throws Exception {
						final ResourcesBean resBean = getResourceBean(session);
						LoadBalancerBean tb = LoadBalancerUtil.read(session,
								ac.getId(), name);
						if (tb == null) {
							tb = new LoadBalancerBean();
							tb.setUserId(ac.getId());
							tb.setLoadBalancerName(name);
							tb.setCreatedTime(new Date());
							tb.setLbStatus("creating");
							tb.setDnsName("0.0.0.0");
							tb.setInconfig(true);
							tb.setReconfig(false);
							tb.setSgName(LoadBalancerUtil.getSecGrpName(
									ac.getId(), name));
							tb.setAvzones((String) call
									.getProperty("AvailabilityZones"));
						}
						tb.setStackId(call.getStackId());
						session.save(tb);
						updateData(session, ac, lbt, tb, call);
						session.save(tb);

						for (final Object o : listeners) {
							ListenerType ltype = null;
							if (o instanceof ListenerType) {
								ltype = (ListenerType) o;
							} else {
								final JsonNode j = (JsonNode) o;
								ltype = new ListenerType();
								ltype.setInstancePort(j.get("InstancePort")
										.getTextValue());
								ltype.setLoadBalancerPort(j.get(
										"LoadBalancerPort").getTextValue());
								ltype.setProtocol(j.get("Protocol")
										.getTextValue().toLowerCase());
							}
							ltypes.add(ltype);
						}
						lbt.setListeners(ltypes);
						return new Object[] { tb, resBean };
					}
				});
		final LoadBalancerBean lbbean = (LoadBalancerBean) objs[0];
		final ResourcesBean resBean = (ResourcesBean) objs[1];

		lbt.setPhysicalId(resBean.getPhysicalId());
		lbt.setStackId(call.getStackId());

		// create loadbalancer security group
		final String secGrpId = SecurityGroupUtils.createSecurityGroup(ac,
				new TemplateContext(), name, call.getStackId(),
				call.getAvailabilityZone(), lbt.getEc2SecGroup(),
				"Loadbalancer Security Group");

		lbt.setEc2SecGroupId(secGrpId);

		// create loadbalancer security ingress
		if (listeners != null) {
			for (final ListenerType ltype : ltypes) {
				SecurityGroupUtils.authorizeSecurityGroupIngress(ac,
						lbt.getEc2SecGroupId(),
						Integer.parseInt(ltype.getLoadBalancerPort()),
						call.getStackId());
			}
		}
		logger.info("security group created & ingress set");

		// lbbean = LoadBalancerUtil.read(s, ac.getId(), name);

		// create loadbalancer databag
		ChefUtil.createDatabagItem(lbt.getDatabag(), "config", lbt.toJson());

		// launch instance implementing loadbalancer
		final CloudProvider cloudProvider = call.getCloudProvider();
		// final String callSecGrp = cloudProvider.getProviderName().equals(
		// "Eucalyptus") ? lbt.getEc2SecGroup() : lbt.getEc2SecGroupId();
		final String callSecGrp = lbt.getEc2SecGroupId();
		final InstanceType res = Instance.createChefInstance(ac, name
				+ "_instance", resBean.getPhysicalId(), call, MapUtil.create(
				AVAILABILITYZONE, lbt.getAvailabilityZones(), CHEFROLES,
				"transcend_loadbalancer", DATABAG, lbt.getDatabag(),
				SECURITYGROUPIDS, callSecGrp, SERVICE, "elb"));
		logger.debug("instance launched");

		// update resource attributes
		lbt.setDnsName((String) res.getAtt(PUBLICIP));
		lbt.setNoWait("-1");
		final InstanceBean lbinst = HibernateUtil
				.withNewSession(new Operation<InstanceBean>() {

					@Override
					public InstanceBean ex(final Session session,
							final Object... args) throws Exception {
						final LoadBalancerBean tb = LoadBalancerUtil.read(
								session, ac.getId(), name);
						tb.setDnsName((String) res.getAtt(PUBLICIP));
						tb.setLbInstances(res.getInstanceId());
						tb.setLbStatus(res.getStatus());
						tb.setSgId(secGrpId);
						session.save(tb);
						return InstanceUtil.getInstance(session,
								tb.getLbInstances());
					}
				});
		logger.debug("instance data updated");

		logger.debug("Loadbalancer created " + lbbean.getLoadBalancerName());
		if (lbbean.getReconfig() != null && lbbean.getReconfig()) {
			reconfigure(lbbean.getLoadBalancerName(), lbt, ac, lbinst);
		}
		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session session, final Object... args)
					throws Exception {
				final LoadBalancerBean tb = LoadBalancerUtil.read(session,
						ac.getId(), name);
				tb.setInconfig(false);
				tb.setReconfig(false);
				session.save(tb);
				return null;
			}
		});
		logger.info("load balancer created " + lbt);
		return lbt;
	}

	@Override
	public Resource delete(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		final String name = call.getPhysicalId();
		logger.debug("Deleting " + ac.getId() + " " + name);

		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session session, final Object... args)
					throws Exception {
				final LoadBalancerBean tb = LoadBalancerUtil.read(session,
						ac.getId(), name);
				tb.setLbStatus("deleting");
				session.save(tb);
				return null;
			}
		});
		logger.info("status set to deleting " + ac.getId() + " " + name);
		return super.delete(call);
	}

	@Override
	public Resource delete0(final CallStruct call) throws Exception {
		super.delete0(call);
		final AccountType ac = call.getAc();
		final String name = call.getPhysicalId();

		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {
				final LoadBalancerBean lb = LoadBalancerUtil.read(s,
						ac.getId(), name);
				ChefUtil.deleteDatabag(lb.getDatabag());
				LoadBalancerUtil.deleteLoadBalancer(s, ac.getId(), lb);
				return null;
			}
		});
		logger.debug("Loadbalancer deleted " + name);
		return null;
	}

	@Override
	protected String failHookClazz() {
		return LoadBalancerFailHook.class.getName();
	}

	@Override
	public boolean hasChanged(final Session s, final CallStruct call)
			throws Exception {
		return true;
	}

	private void reconfigure(final String name, final LoadBalancerType lbt,
			final AccountType ac, final InstanceBean inst) {
		logger.debug("Loadbalancer reconfigure " + name);
		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session session, final Object... args)
					throws Exception {
				final LoadBalancerBean lbean = LoadBalancerUtil.read(session,
						ac.getId(), name);
				ChefUtil.createDatabagItem(lbean.getDatabag(), CONFIG,
						lbt.toJson());

				if (inst != null) {
					final String keyDir = (String) ConfigurationUtil
							.getConfiguration(Arrays
									.asList(new String[] { "KEYS_DIR" }));
					final String key = keyDir + "/" + ac.getDefKeyName()
							+ ".pem";
					final String ip = InstanceUtil.getIP(inst.getPublicIpId(),
							inst.getPrivateIp(), inst.getAvzone());

					CFUtil.executeCommand(ac, null, "ssh", "-i", key, "-o",
							"StrictHostKeyChecking=false", "-o",
							"UserKnownHostsFile=/dev/null", "root@" + ip,
							"rm /etc/haproxy/haproxy.cfg");

					CFUtil.runChefClient(session, ac,
							new CommaObject(inst.getInstanceId()).toList());
				}
				lbean.setInconfig(false);
				lbean.setReconfig(false);
				session.save(lbean);
				logger.debug("Loadbalancer reconfigured "
						+ lbean.getLoadBalancerName());
				return null;
			}
		});
		logger.info("Loadbalancer reconfigured " + name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public CFType update0(final CallStruct call) throws Exception {
		logger.debug("Loadbalancer update" + call);
		final AccountType ac = call.getAc();
		final String name = call.getName();
		final Boolean alreadyin = HibernateUtil
				.withNewSession(new Operation<Boolean>() {

					@Override
					public Boolean ex(final Session session,
							final Object... args) throws Exception {
						final LoadBalancerBean tb = LoadBalancerUtil.read(
								session, ac.getId(), name);
						final Boolean in = tb.getInconfig();
						if (in != null && in) {
							tb.setReconfig(true);
						}
						tb.setInconfig(true);
						session.save(tb);
						return in;
					}
				});
		final LoadBalancerType lbt = HibernateUtil
				.withNewSession(new Operation<LoadBalancerType>() {

					@Override
					public LoadBalancerType ex(final Session s,
							final Object... args) throws Exception {
						final LoadBalancerBean lbean = LoadBalancerUtil.read(s,
								ac.getId(), name);
						LoadBalancerType lbt = LoadBalancerUtil
								.toLoadBalancerType(s, lbean);

						final CommaObject oldInsts = new CommaObject(lbean
								.getInstances());
						final List<String> instances = (List<String>) call
								.getProperty("Instances");
						final String isInternal = (String) call
								.getProperty("IsInternalInstances");
						boolean addedByLB = true;
						if (isInternal != null) {
							addedByLB = !Boolean.parseBoolean(isInternal);
						}
						final CommaObject newInsts = new CommaObject(instances);
						lbean.setInstances(newInsts.toString());

						final List<String> nlist = newInsts.toList();
						for (final String i : oldInsts.toList()) {
							if (nlist.contains(i)) {
								continue;
							}
							LoadBalancerUtil.removeInstaceFromLoadBalancer(s,
									ac.getId(), lbean, i);
						}
						final List<String> olist = oldInsts.toList();
						for (final String i : newInsts.toList()) {
							if (olist.contains(i)) {
								continue;
							}
							LoadBalancerUtil.addInstaceToLoadBalancer(s,
									ac.getId(), lbean, i, lbean.getAvzones(),
									addedByLB);
						}
						updateData(s, ac, lbt, lbean, call);

						lbt = LoadBalancerUtil.toLoadBalancerType(s, lbean);
						final List<Object> listeners = (List<Object>) call
								.getProperty("Listeners");
						if (listeners == null) {
							lbean.setListeners(null);
							s.save(lbean);
						} else {
							final Set<ListenerBean> lbeanListeners = lbean
									.getListeners();
							final List<ListenerType> ltypes = new ArrayList<ListenerType>();
							for (final Object o : listeners) {
								ListenerType ltype = null;
								if (o instanceof ListenerType) {
									ltype = (ListenerType) o;
								} else {
									final JsonNode j = (JsonNode) o;
									ltype = new ListenerType();
									ltype.setInstancePort(j.get("InstancePort")
											.getTextValue());
									ltype.setLoadBalancerPort(j.get(
											"LoadBalancerPort").getTextValue());
									ltype.setProtocol(j.get("Protocol")
											.getTextValue().toLowerCase());
								}
								ltypes.add(ltype);
							}
							final Set<ListenerBean> delBean = new HashSet<ListenerBean>();
							for (final ListenerBean ilistener : lbeanListeners) {
								boolean del = true;
								for (final ListenerType il : ltypes) {
									if (Long.parseLong(il.getLoadBalancerPort()) == ilistener
											.getLoadBalancerPort()) {
										del = false;
										break;
									}
								}
								if (del) {
									delBean.add(ilistener);

									// create loadbalancer security ingress
									{
										final CallStruct c = call.newCall(lbt
												.getEc2SecGroup());
										c.setAc(ac);
										c.setCtx(new TemplateContext(null));
										c.setName(lbean.getSgId()
												+ "_"
												+ ilistener
														.getLoadBalancerPort());
										c.setPhysicalId(c.getName());
										c.setParentId(lbean.getSgId());
										final Map<String, Object> properties = new HashMap<String, Object>();
										properties.put(
												Constants.AVAILABILITYZONE,
												ac.getDefZone());
										properties.put(Constants.GROUPNAME,
												lbt.getEc2SecGroup());
										properties.put(Constants.FROMPORT,
												(int) ilistener
														.getLoadBalancerPort());
										properties.put(Constants.TOPORT,
												(int) ilistener
														.getLoadBalancerPort());
										properties.put(Constants.CIDRIP,
												"0.0.0.0/0");
										c.setProperties(properties);
										c.setType(SecurityGroupIngress.TYPE);
										final SecurityGroupIngress provider = new SecurityGroupIngress();
										provider.delete(c);
									}
								}
							}
							for (final ListenerBean idel : delBean) {
								lbeanListeners.remove(idel);
								s.delete(idel);
							}

							for (final ListenerType iltype : ltypes) {
								boolean add = true;
								for (final ListenerBean ilistener : lbeanListeners) {
									if (Long.parseLong(iltype
											.getLoadBalancerPort()) == ilistener
											.getLoadBalancerPort()) {
										add = false;
										break;
									}
								}
								if (add) {
									final ListenerBean lsnb = new ListenerBean();
									lsnb.setProtocol(iltype.getProtocol());
									lsnb.setInstancePort(Long.parseLong(iltype
											.getInstancePort()));
									lsnb.setLoadBalancerPort(Long
											.parseLong(iltype
													.getLoadBalancerPort()));
									s.save(lsnb);
									lbeanListeners.add(lsnb);

									// create loadbalancer security ingress
									{
										final CallStruct c = call.newCall(lbean
												.getEc2SecGroup());
										c.setAc(ac);
										c.setCtx(new TemplateContext(null));
										c.setName(lbean.getSgId() + "_"
												+ lsnb.getLoadBalancerPort());
										c.setParentId(lbean.getSgId());
										final Map<String, Object> properties = new HashMap<String, Object>();
										properties.put(
												Constants.AVAILABILITYZONE,
												ac.getDefZone());
										properties.put(Constants.GROUPNAME,
												lbt.getEc2SecGroup());
										properties.put(Constants.FROMPORT,
												(int) lsnb
														.getLoadBalancerPort());
										properties.put(Constants.TOPORT,
												(int) lsnb
														.getLoadBalancerPort());
										properties.put(Constants.CIDRIP,
												"0.0.0.0/0");
										c.setProperties(properties);
										c.setType(SecurityGroupIngress.TYPE);
										final SecurityGroupIngress provider = new SecurityGroupIngress();
										provider.create(c);
									}
								}
							}
							lbt.setListeners(ltypes);
							lbean.setListeners(lbeanListeners);
							s.save(lbean);
						}
						return lbt;
					}
				});
		// final LoadBalancerBean lbean = LoadBalancerUtil.read(s0, ac.getId(),
		// name);
		lbt.setPostWaitUrl(null);
		lbt.setStackId(call.getStackId());
		if (alreadyin == null || !alreadyin) {
			final InstanceBean lbinst = HibernateUtil
					.withNewSession(new Operation<InstanceBean>() {

						@Override
						public InstanceBean ex(final Session session,
								final Object... args) throws Exception {
							final LoadBalancerBean tb = LoadBalancerUtil.read(
									session, ac.getId(), name);
							final Boolean in = tb.getInconfig();
							return InstanceUtil.getInstance(session,
									tb.getLbInstances());
						}
					});
			reconfigure(name, lbt, ac, lbinst);
		}
		logger.info("Loadbalancer updated" + name);
		return lbt;
	}

	@SuppressWarnings("unchecked")
	private void updateData(final Session s, final AccountType ac,
			final LoadBalancerType lbt, final LoadBalancerBean lb,
			final CallStruct call) {
		lbt.setAvailabilityZones((String) call.getProperty("AvailabilityZones"));
		final HealthCheckType hc = LoadBalancerUtil.toHealthCheckType(call
				.getProperty("HealthCheck"));
		lbt.setHealthCheck(hc);
		if (hc != null) {
			lb.setHealthyThreshold(Long.parseLong(hc.getHealthyThreshold()));
			lb.setUnhealthyThreshold(Long.parseLong(hc.getUnhealthyThreshold()));
			lb.setInterval(Long.parseLong(hc.getInterval()));
			lb.setTimeout(Long.parseLong(hc.getTimeout()));
			lb.setTarget(hc.getTarget());
		}

		final List<String> insts = (List<String>) call.getProperty("Instances");
		lbt.setInstances(insts);
		final List<InstanceType> idata = new ArrayList<InstanceType>();
		if (insts != null) {
			for (final String str : insts) {
				final InstanceBean ib = InstanceUtil.getInstance(s, str);
				if (ib != null) {
					final InstanceType it = InstanceUtil.toInstanceType(ib);
					idata.add(it);
				}
			}
		}
		lbt.setInstanceData(idata);

		if (call.getProperty("AppCookieStickinessPolicy") != null) {
			final List<AppCookieStickinessPolicyType> appList = LoadBalancerUtil
					.toAppCookieStickinessPolicy(call
							.getProperty("AppCookieStickinessPolicy"));
			if (appList != null) {
				lbt.setAppCookieStickinessPolicy(appList);
				lb.setAppCookieStickinessPolicy(JsonUtil.toJsonString(call
						.getProperty("AppCookieStickinessPolicy")));
			}
		} else {
			lb.setAppCookieStickinessPolicy(null);
		}

		if (call.getProperty("LBCookieStickinessPolicy") != null) {
			final List<LBCookieStickinessPolicyType> lblist = LoadBalancerUtil
					.toLBCookieStickinessPolicy(call
							.getProperty("LBCookieStickinessPolicy"));
			if (lblist != null) {
				lbt.setLbCookieStickinessPolicy(lblist);
				lb.setLbCookieStickinessPolicy(JsonUtil.toJsonString(call
						.getProperty("LBCookieStickinessPolicy")));
			}
		} else {
			lb.setLbCookieStickinessPolicy(null);
		}
		// There will be one databag per load balancer
		final String databag = LoadBalancerUtil.getDatabagName(ac.getId(),
				call.getName());
		lbt.setDatabag(databag);
		lbt.setEc2SecGroup(lb.getSgName());
		lbt.setAcId(ac.getId());
		// the url on which transcend_loadbalancer chef role will call back when
		// its installation is complete
		lbt.setPostWaitUrl((String) ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { "TRANSCEND_URL",
						lbt.getAvailabilityZones() })));
		lb.setDatabag(databag);
		lb.setEc2SecGroup(lbt.getEc2SecGroup());
		s.save(lb);
	}

	@Override
	protected String waitHookClazz() {
		return LoadBalancerWaitHook.class.getName();
	}
}