package com.msi.tough.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.ec2.InstanceType;
import com.msi.tough.cf.elasticloadbalancing.AppCookieStickinessPolicyType;
import com.msi.tough.cf.elasticloadbalancing.HealthCheckType;
import com.msi.tough.cf.elasticloadbalancing.LBCookieStickinessPolicyType;
import com.msi.tough.cf.elasticloadbalancing.ListenerType;
import com.msi.tough.cf.elasticloadbalancing.LoadBalancerType;
import com.msi.tough.cf.elasticloadbalancing.PolicyNameType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.core.MapUtil;
import com.msi.tough.core.StringHelper;
import com.msi.tough.engine.aws.ec2.DescribeInstance;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.InstanceBean;
import com.msi.tough.model.ListenerBean;
import com.msi.tough.model.LoadBalancerBean;

public class LoadBalancerUtil {
	private static Logger logger = Appctx.getLogger(LoadBalancerUtil.class
			.getName());

	public static void addInstaceToLoadBalancer(final Session s,
			final long acid, final LoadBalancerBean lb,
			final String instanceId, final String avzone,
			final boolean addedByLB) throws Exception {
		logger.debug("addInstaceToLoadBalancer " + acid + " "
				+ lb.getLoadBalancerName() + " " + instanceId);
		final CommaObject co = new CommaObject(lb.getInstances());
		co.add(instanceId);
		lb.setInstances(co.toString());
		s.save(lb);

		// find out if instance exists
		InstanceBean ib = InstanceUtil.getInstance(s, instanceId);
		boolean add = addedByLB;
		if (ib == null) {
			final AccountBean acb = AccountUtil.readAccount(s, acid);
			final AccountType ac = AccountUtil.toAccount(acb);
			InstanceType it = null;
			for (int i = 0; i < 5; i++) {
				try {
					it = (InstanceType) CFUtil
							.createResource(
									ac,
									"LBUpdate",
									DescribeInstance.TYPE,
									avzone,
									instanceId,
									MapUtil.create(
											"AvailabilityZone",
											avzone,
											"InstanceIds",
											Arrays.asList(new String[] { instanceId })));
					break;
				} catch (final Exception e) {
					e.printStackTrace();
					Thread.sleep(10000);
				}
			}
			if (it == null) {
				throw new Exception("DescribeInstance failed on " + instanceId);
			}
			ib = new InstanceBean();
			ib.setAddedByLB(addedByLB);
			ib.setAvzone(avzone);
			ib.setPublicIpId(it.getPublicIp());
			ib.setInstanceId(instanceId);
			ib.setPrivateIp(it.getPrivateIpAddress());
			ib.setUserId(acid);
		} else {
			add = ib.isAddedByLB();
		}
		if (add) {
			ib.setLbCount(ib.getLbCount() + 1);
		}
		s.save(ib);
		logger.info("addedInstaceToLoadBalancer " + acid + " "
				+ lb.getLoadBalancerName() + " " + instanceId);
	}

	public static void deleteLoadBalancer(final Session session,
			final long acid, final LoadBalancerBean lbean) {
		if (lbean.getInstances() != null) {
			for (String bid : lbean.getInstances().split(",")) {
				bid = bid.trim();
				if (bid.length() == 0) {
					continue;
				}
				removeInstaceFromLoadBalancer(session, acid, lbean, bid);
			}
		}
		if (lbean.getListeners() != null) {
			for (final ListenerBean l : lbean.getListeners()) {
				session.delete(l);
			}
			lbean.setLbInstances(null);
		}
		session.delete(lbean);
	}

	public static String getDatabagName(final long acid, final String name) {
		return "__elb-" + acid + "-" + name;
	}

	public static String getHealth(final Session session, final long acid,
			final String id, final String target) throws Exception {
		String ret = "failed";
		// final InstanceBean ib = InstanceUtil.getInstance(session, id);
		final AccountBean ac = AccountUtil.readAccount(session, acid);
		final String dns = InstanceUtil.getInstanceDns(session,
				AccountUtil.toAccount(ac), id);
		if (dns == null) {
			return ret;
		}
		// Create an instance of HttpClient.
		final HttpClient client = new HttpClient();
		final String[] parts = getTargetParts(target);
		final String url = parts[0] + "://" + dns + ":" + parts[1] + parts[2];
		// Create a method instance.
		final GetMethod method = new GetMethod(url);
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(0, false));
		try {
			// Execute the method.
			final int statusCode = client.executeMethod(method);
			if (statusCode == HttpStatus.SC_OK) {
				ret = "running";
			}
		} catch (final Exception e) {
		} finally {
			// Release the connection.
			method.releaseConnection();
		}
		return ret;
	}

	public static String getSecGrpName(final long acid, final String name) {
		return "__elb_" + acid + "_" + name;
	}

	public static String[] getTargetParts(final String target) {
		final String[] parts = new String[3];
		final int i = target.indexOf(":");
		if (i == -1) {
			return null;
		}
		final int i2 = target.indexOf("/");
		parts[0] = target.substring(0, i);
		parts[1] = target.substring(i + 1, i2);
		parts[2] = target.substring(i2);
		return parts;
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
	@SuppressWarnings("unchecked")
	public static LoadBalancerBean read(final Session session, final long acid,
			final String name) {
		final Query q = session
				.createQuery("from LoadBalancerBean where userId=" + acid
						+ " and loadBalancerName='" + name + "'");
		final List<LoadBalancerBean> l = q.list();
		if (l == null || l.size() == 0) {
			return null;
		}
		return l.get(0);
	}

	public static void removeInstaceFromLoadBalancer(final Session s,
			final long acid, final LoadBalancerBean lb, String instanceId) {
		logger.debug("removeInstaceFromLoadBalancer " + acid + " "
				+ lb.getLoadBalancerName() + " " + instanceId);
		instanceId = instanceId.trim();
		if (instanceId == null || instanceId.length() == 0) {
			return;
		}
		final InstanceBean b = InstanceUtil.getInstance(s, instanceId);
		if (lb.getInstances() != null) {
			final CommaObject co = new CommaObject(lb.getInstances());
			co.remove(instanceId);
			lb.setInstances(co.toString());
			s.save(lb);
		}
		logger.info("removedInstaceFromLoadBalancer " + acid + " "
				+ lb.getLoadBalancerName() + " " + instanceId);
		// if (b != null && b.isAddedByLB()) {
		// if (b.getLbCount() <= 1) {
		// s.delete(b);
		// } else {
		// b.setLbCount(b.getLbCount() - 1);
		// s.save(b);
		// }
		// }
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<AppCookieStickinessPolicyType> toAppCookieStickinessPolicy(
			final Object o) {
		if (o == null) {
			return null;
		}
		if (o instanceof List) {
			final List l = (List) o;
			if (l.size() == 0) {
				return (List<AppCookieStickinessPolicyType>) o;
			}
			if (l.get(0) instanceof AppCookieStickinessPolicyType) {
				return (List<AppCookieStickinessPolicyType>) o;
			} else {
				final List<AppCookieStickinessPolicyType> ret = new ArrayList<AppCookieStickinessPolicyType>();
				for (final Object a : l) {
					if (a instanceof ObjectNode) {
						final ObjectNode n = (ObjectNode) a;
						final AppCookieStickinessPolicyType p = new AppCookieStickinessPolicyType();
						p.setCookieName(n.get("CookieName").getTextValue());
						p.setPolicyName(n.get("PolicyName").getTextValue());
						ret.add(p);
					} else {
						throw new RuntimeException("Invalid list of data type "
								+ a.getClass().getName());
					}
				}
				return ret;
			}
		}
		if (o instanceof JsonNode) {
			final ArrayNode jn = (ArrayNode) o;
			final List<AppCookieStickinessPolicyType> ret = new ArrayList<AppCookieStickinessPolicyType>();
			for (int i = 0; i < jn.size(); i++) {
				final JsonNode n = jn.get(i);
				final AppCookieStickinessPolicyType p = new AppCookieStickinessPolicyType();
				p.setCookieName(n.get("CookieName").getTextValue());
				p.setPolicyName(n.get("PolicyName").getTextValue());
				ret.add(p);
			}
			return ret;
		}
		throw new RuntimeException("Invalid data type "
				+ o.getClass().getName());
	}

	@SuppressWarnings("unchecked")
	public static HealthCheckType toHealthCheckType(final Object o) {
		if (o == null) {
			return null;
		}
		if (o instanceof HealthCheckType) {
			return (HealthCheckType) o;
		}
		if (o instanceof JsonNode) {
			final HealthCheckType hc = new HealthCheckType();
			final JsonNode n = (JsonNode) o;
			hc.setHealthyThreshold(n.get("HealthyThreshold").getTextValue());
			hc.setInterval(n.get("Interval").getTextValue());
			hc.setTarget(n.get("Target").getTextValue());
			hc.setTimeout(n.get("Timeout").getTextValue());
			hc.setUnhealthyThreshold(n.get("UnhealthyThreshold").getTextValue());
			return hc;
		}
		if (o instanceof Map) {
			final Map<String, String> m = (Map<String, String>) o;
			final HealthCheckType hc = new HealthCheckType();
			hc.setHealthyThreshold(m.get("HealthyThreshold"));
			hc.setInterval(m.get("Interval"));
			hc.setTarget(m.get("Target"));
			hc.setTimeout(m.get("Timeout"));
			hc.setUnhealthyThreshold(m.get("UnhealthyThreshold"));
			return hc;
		}
		throw new RuntimeException("Invalid data type "
				+ o.getClass().getName());
	}

	public static String toJson(final LoadBalancerType lbtype) throws Exception {
		final String lbscript = lbtype.toCFString();
		final JsonNode json = JsonUtil.load(lbscript);
		final Map<String, Object> lbmap = MapUtil
				.create(lbtype.getName(), json);
		final Map<String, Object> scrmap = MapUtil.create("Resources", lbmap);
		return JsonUtil.toJsonString(scrmap);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<LBCookieStickinessPolicyType> toLBCookieStickinessPolicy(
			final Object o) {
		if (o == null) {
			return null;
		}
		if (o instanceof List) {
			final List l = (List) o;
			if (l.size() == 0) {
				return (List<LBCookieStickinessPolicyType>) o;
			}
			if (l.get(0) instanceof LBCookieStickinessPolicyType) {
				return (List<LBCookieStickinessPolicyType>) o;
			} else {
				final List<LBCookieStickinessPolicyType> ret = new ArrayList<LBCookieStickinessPolicyType>();
				for (final Object a : l) {
					if (a instanceof ObjectNode) {
						final ObjectNode n = (ObjectNode) a;
						final LBCookieStickinessPolicyType p = new LBCookieStickinessPolicyType();
						p.setPolicyName(n.get("PolicyName").getTextValue());
						p.setCookieExpirationPeriod(n.get(
								"CookieExpirationPeriod").getTextValue());
						ret.add(p);
					} else {
						throw new RuntimeException("Invalid list of data type "
								+ a.getClass().getName());
					}
				}
				return ret;
			}
		}
		if (o instanceof JsonNode) {
			final ArrayNode jn = (ArrayNode) o;
			final List<LBCookieStickinessPolicyType> ret = new ArrayList<LBCookieStickinessPolicyType>();
			for (int i = 0; i < jn.size(); i++) {
				final JsonNode n = jn.get(i);
				final LBCookieStickinessPolicyType p = new LBCookieStickinessPolicyType();
				p.setPolicyName(n.get("PolicyName").getTextValue());
				p.setCookieExpirationPeriod(n.get("CookieExpirationPeriod")
						.getTextValue());
				ret.add(p);
			}
			return ret;
		}
		throw new RuntimeException("Invalid data type "
				+ o.getClass().getName());
	}

	public static LoadBalancerType toLoadBalancerType(final Session s,
			final LoadBalancerBean lbean) {
		final LoadBalancerType lbtype = new LoadBalancerType();
		lbtype.setAcId(lbean.getUserId());
		if (!StringHelper.isBlank(lbean.getAppCookieStickinessPolicy())) {
			final JsonNode jn = JsonUtil.load(lbean
					.getAppCookieStickinessPolicy());
			lbtype.setAppCookieStickinessPolicy(LoadBalancerUtil
					.toAppCookieStickinessPolicy(jn));
		}
		if (!StringHelper.isBlank(lbean.getLbCookieStickinessPolicy())) {
			final JsonNode jn = JsonUtil.load(lbean
					.getLbCookieStickinessPolicy());
			lbtype.setLbCookieStickinessPolicy(LoadBalancerUtil
					.toLBCookieStickinessPolicy(jn));
		}
		lbtype.setAvailabilityZones(lbean.getAvzones());
		lbtype.setDatabag(lbean.getDatabag());
		lbtype.setDnsName(lbean.getDnsName());
		if (!StringHelper.isBlank(lbean.getTarget())) {
			final HealthCheckType hc = new HealthCheckType();
			hc.setHealthyThreshold("" + lbean.getHealthyThreshold());
			hc.setInterval("" + lbean.getInterval());
			hc.setTarget(lbean.getTarget());
			hc.setTimeout("" + lbean.getTimeout());
			hc.setUnhealthyThreshold("" + lbean.getUnhealthyThreshold());
			lbtype.setHealthCheck(hc);
		}

		final CommaObject coi = new CommaObject(lbean.getInstances());
		lbtype.setInstances(coi.toList());
		final List<InstanceType> instanceData = new ArrayList<InstanceType>();
		for (final String si : coi.toList()) {
			final InstanceBean ib = InstanceUtil.getInstance(s, si);
			if (ib != null) {
				final InstanceType it = InstanceUtil.toInstanceType(ib);
				instanceData.add(it);
			}
		}
		lbtype.setInstanceData(instanceData);

		final List<ListenerType> lsns = new ArrayList<ListenerType>();
		for (final ListenerBean l : lbean.getListeners()) {
			final ListenerType lt = new ListenerType();
			lt.setAcId(lbean.getUserId());
			lt.setInstancePort("" + l.getInstancePort());
			lt.setLoadBalancerPort("" + l.getLoadBalancerPort());
			final String jpolicies = l.getPolicyNames();
			if (jpolicies != null) {
				final List<PolicyNameType> nms = new ArrayList<PolicyNameType>();
				final ArrayNode npolicies = (ArrayNode) JsonUtil
						.load(jpolicies);
				for (int pc = 0; pc < npolicies.size(); pc++) {
					final JsonNode npolicy = npolicies.get(pc);
					final String type = npolicy.get("Type").getTextValue();
					PolicyNameType tpnm = null;
					if (type.equals("")) {
						final AppCookieStickinessPolicyType t = new AppCookieStickinessPolicyType();
						t.setCookieName(npolicy.get("CookieName")
								.getTextValue());
						t.setPolicyName(npolicy.get("PolicyName")
								.getTextValue());
						tpnm = t;
					} else {
						final LBCookieStickinessPolicyType t = new LBCookieStickinessPolicyType();
						t.setCookieExpirationPeriod(npolicy.get(
								"CookieExpirationPeriod").getTextValue());
						t.setPolicyName(npolicy.get("PolicyName")
								.getTextValue());
						tpnm = t;
					}
					nms.add(tpnm);
				}
				lt.setPolicyNames(nms);
			}
			lt.setProtocol(l.getProtocol());
			lt.setSSLCertificateId(l.getSslCertificateId());
			lt.setStackId(lbean.getStackId());
			lsns.add(lt);
		}
		lbtype.setListeners(lsns);
		lbtype.setPhysicalId(lbean.getPhysicalId());
		lbtype.setName(lbean.getLoadBalancerName());
		// lbtype.setPhysicalId(lbean.get);
		// lbtype.setPostWaitUrl(lbean.post);
		// lbtype.setSecurityGroups(lbean.getSe);
		lbtype.setStackId(lbean.getStackId());
		// lbtype.setSubnets(lbean.getSu);
		lbtype.setEc2SecGroup(lbean.getSgName());
		return lbtype;
	}
}
