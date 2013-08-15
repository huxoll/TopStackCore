package com.msi.tough.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.msi.tough.cf.AccountType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.ExecutorHelper;
import com.msi.tough.core.ExecutorHelper.Executable;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.core.StringHelper;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.core.FailHook;
import com.msi.tough.engine.core.Provider;
import com.msi.tough.engine.core.Template;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.engine.core.TemplateExecutor;
import com.msi.tough.engine.core.WaitHook;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.InstanceBean;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.model.cf.CFStackBean;
import com.msi.tough.query.ErrorResponse;

public class CFUtil {
	private static Logger logger = Appctx.getLogger(CFUtil.class.getName());

	public static Resource createResource(final AccountType ac,
			final String stackId, final String type,
			final String availabilityZone, final String name,
			final Map<String, Object> parameterValues) throws Exception {
		final Provider prov = getProvider(type);
		final CallStruct call = new CallStruct();
		call.setAc(ac);
		call.setAvailabilityZone(availabilityZone);
		call.setCtx(new TemplateContext(null));
		call.setName(name);
		call.setProperties(parameterValues);
		call.setStackId(stackId);
		call.setType(type);
		return prov.create(call);
	}

	public static Resource createResource(final CallStruct call)
			throws Exception {
		final Provider prov = getProvider(call.getType());
		return prov.create(call);
	}

	public static void createResourceAsync(final AccountType ac,
			final String stackId, final String type,
			final String availabilityZone, final String name,
			final Map<String, Object> parameterValues) throws Exception {

		HibernateUtil.withSession(new HibernateUtil.Operation<Object>() {
			@Override
			public Object ex(final Session s, final Object... as)
					throws Exception {
				final List<ResourcesBean> recs = selectResourceRecords(s,
						ac.getId(), stackId, null, null, false);
				if (recs != null && recs.size() > 0) {
					throw new RuntimeException("Stackid already in use "
							+ stackId);
				}
				return null;
			}
		});

		final Executable r = new ExecutorHelper.Executable() {
			@Override
			public void run() {
				try {
					createResource(ac, stackId, type, availabilityZone, name,
							parameterValues);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		};
		ExecutorHelper.execute(r);
	}

	public static void createResourceAsync(final CallStruct call)
			throws Exception {
		final Executable r = new ExecutorHelper.Executable() {
			@Override
			public void run() {
				try {
					createResource(call);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		};
		ExecutorHelper.execute(r);
	}

	public static void deleteAsyncStackResources(final AccountType ac,
			final String stackId, final String parentId, final String physicalId)
			throws Exception {
		final ExecutorService exsrv = Appctx.getExecutorService();
		if (exsrv != null) {
			final Runnable r = new Runnable() {

				@Override
				public void run() {
					try {
						deleteStackResources(ac, stackId, parentId, physicalId);
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}

			};
			exsrv.submit(r);
		} else {
			try {
				deleteStackResources(ac, stackId, parentId, physicalId);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteResourceRecords(final Session s,
			final long userId, final String stackId, final String parentId,
			final String physicalId) throws Exception {
		logger.debug("deleteResourceRecords " + userId + " " + stackId + " "
				+ parentId + " " + physicalId);
		final List<ResourcesBean> l = selectResourceRecords(s, userId, stackId,
				parentId, physicalId, true);
		for (final ResourcesBean r : l) {
			if (!r.getPhysicalId().equals(r.getParentId())) {
				deleteResourceRecords(s, userId, stackId, r.getPhysicalId(),
						null);
			}
			s.delete(r);
		}
	}

	public static int deleteStackResources(final AccountType ac,
			final String stackId, final String parentId, final String physicalId)
			throws Exception {
		if (stackId == null) {
			return 0;
		}
		logger.debug("deleteStackResources " + ac.getId() + " " + stackId + " "
				+ parentId + " " + physicalId);

		final List<ResourcesBean> l = HibernateUtil
				.withNewSession(new Operation<List<ResourcesBean>>() {

					@Override
					public List<ResourcesBean> ex(final Session s,
							final Object... args) throws Exception {
						return selectDescResourceRecords(s, ac.getId(),
								stackId, parentId, physicalId);
					}
				});
		for (final ResourcesBean r : l) {
			try {
				logger.info("deleteStackResources resource "
						+ r.getPhysicalId() + " " + r.getType());
				final CallStruct call = new CallStruct();
				call.setCtx(new TemplateContext(null));
				call.setAc(ac);
				call.setPhysicalId(r.getPhysicalId());
				call.setAvailabilityZone(r.getAvailabilityZone());
				call.setParentId(r.getParentId());
				call.setStackId(stackId);
				call.setType(r.getType());
				call.setResourcesBean(r);
				call.setResourceData(r.getResourceData());
				final Provider prov = getProvider(r.getType());
				prov.delete(call);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		return l.size();
	}

	public static void executeCommand(final AccountType ac, final String dir,
			final List<String> args) throws Exception {
		final Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("Command", args);
		if (dir != null) {
			properties.put("Directory", dir);
		}
		final CallStruct call = new CallStruct();
		call.setAc(ac);
		call.setCtx(new TemplateContext(null));
		call.setProperties(properties);
		call.setType("TRANSCEND::CORE::Execute");
		logger.info("executeCommand " + call.toString());
		CFUtil.getProvider(call.getType()).create(call);
	}

	public static void executeCommand(final AccountType ac, final String dir,
			final String... args) throws Exception {
		executeCommand(ac, dir, Arrays.asList(args));
	}

	public static void failStack(final long userId, final String stackId) {
		logger.error("failStack " + userId + " " + stackId);

		HibernateUtil.withNewSession(new Operation<Object>() {
			@Override
			public Object ex(final Session session, final Object... args)
					throws Exception {
				final List<ResourcesBean> resources = selectResourceRecords(
						session, userId, stackId, null, null, true);
				for (final ResourcesBean rb : resources) {
					final String failHookClazz = rb.getFailHook();
					if (failHookClazz != null) {
						final Object c = Class.forName(failHookClazz)
								.newInstance();
						if (!(c instanceof FailHook)) {
							throw ErrorResponse.InternalFailure();
						} else {
							final FailHook fh = (FailHook) c;
							fh.startFail(userId, stackId, rb.getPhysicalId(),
									null);
						}
					}
				}
				for (final ResourcesBean rb : resources) {
					final String failHookClazz = rb.getFailHook();
					if (failHookClazz != null) {
						final Object c = Class.forName(failHookClazz)
								.newInstance();
						if (!(c instanceof FailHook)) {
							throw ErrorResponse.InternalFailure();
						} else {
							final FailHook fh = (FailHook) c;
							fh.endFail(userId, stackId, rb.getPhysicalId(),
									null);
						}
					}
				}
				return null;
			}
		});
	}

	public static List<String> getAvailabilityZones(final AccountBean ac) {
		final BasicAWSCredentials cred = new BasicAWSCredentials(
				ac.getAccessKey(), ac.getSecretKey());
		final AmazonEC2Client ec2 = new AmazonEC2Client(cred);
		ec2.setEndpoint((String) ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { Constants.EC2URL, ac.getDefZone() })));
		final DescribeAvailabilityZonesResult avzRes = ec2
				.describeAvailabilityZones();
		final List<AvailabilityZone> avzl = avzRes.getAvailabilityZones();
		final List<String> avzs = new ArrayList<String>();
		for (final AvailabilityZone a : avzl) {
			avzs.add(a.getZoneName());
		}
		return avzs;
	}

	public static Provider getProvider(final String type) throws Exception {
		final StringBuilder sb = new StringBuilder();
		int pos = 0;
		for (;;) {
			final int idx = type.indexOf("::", pos);
			if (idx == -1) {
				sb.append(type.substring(pos));
				break;
			}
			sb.append(type.substring(pos, idx).toLowerCase()).append(".");
			pos = idx + 2;
		}
		final String clazz = "com.msi.tough.engine." + sb.toString();

		final Object c = Class.forName(clazz).newInstance();
		if (!(c instanceof Provider)) {
			throw new Exception("Clazz is not of interface Provider " + clazz);
		}
		return (Provider) c;
	}

	@SuppressWarnings("unchecked")
	public static ResourcesBean getResourceBean(final Session s,
			final long resourceBeanId) {
		final Query q = s.createQuery("from ResourcesBean where id="
				+ resourceBeanId);
		final List<ResourcesBean> l = q.list();
		if (l != null && l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	public static int getResourceCount(final Session s) throws Exception {
		final List<ResourcesBean> l = selectResourceRecords(s, 0, null, null,
				null, false);
		final List<String> types = Arrays
				.asList(new String[] { "AWS::EC2::Instance" });
		int cnt = 0;
		for (final ResourcesBean b : l) {
			final String type = b.getType();
			if (types.contains(type)) {
				cnt++;
			}
		}
		return cnt;
	}

	public static List<String> getSecurityGroups(final AccountBean ac) {
		final BasicAWSCredentials cred = new BasicAWSCredentials(
				ac.getAccessKey(), ac.getSecretKey());
		final AmazonEC2Client ec2 = new AmazonEC2Client(cred);
		ec2.setEndpoint((String) ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { Constants.EC2URL, ac.getDefZone() })));
		final DescribeSecurityGroupsResult res = ec2.describeSecurityGroups();
		final List<SecurityGroup> l = res.getSecurityGroups();
		final List<String> sl = new ArrayList<String>();
		for (final SecurityGroup a : l) {
			sl.add(a.getGroupName());
		}
		return sl;
	}

	@SuppressWarnings("unchecked")
	public static void postWaitMessage(final Session s, final String stackId,
			final long acid, String physicalId, final String hostname,
			final String status, final boolean entryPoint, final Map<String, String[]> map) throws Exception {
		logger.debug("postWait " + stackId + " " + acid + " " + physicalId
				+ " " + hostname + " " + status);
		if (StringHelper.isBlank(physicalId) && !StringHelper.isBlank(hostname)) {
			final InstanceBean inst = InstanceUtil.getInstanceByHostName(s,
					acid, hostname);
			if (inst == null) {
				logger.debug("Called for non registered instance " + hostname);
				return;
			}
			physicalId = inst.getInstanceId();
		}
		final String sql = "from ResourcesBean where userId=" + acid
				+ " and stackId='" + stackId + "' and physicalId='"
				+ physicalId + "'";
		final Query q = s.createQuery(sql);
		final ResourcesBean r = (ResourcesBean) q.list().get(0);
		final Integer no = r.getNoWait();

		// if (no == null || no == 0) {
		// return;
		// }
		if (no < 0) {
			final String sql0 = "from ResourcesBean where userId=" + acid
					+ " and stackId='" + stackId + "' and parentId='"
					+ physicalId + "'";
			final Query q0 = s.createQuery(sql0);
			final List<ResourcesBean> l0 = q0.list();
			boolean sw = true;
			for (final ResourcesBean r1 : l0) {
				if (entryPoint) {
					r1.setNoWait(0);
					resourceComplete(s, r1, true, acid, stackId,
							r1.getPhysicalId(), map);
				} else {
					if (r1.getNoWait() != null && r1.getNoWait() != 0) {
						sw = false;
						break;
					}
				}
			}
			if (sw) {
				resourceComplete(s, r, true, acid, stackId, physicalId, map);
				if (r.getParentId() != null) {
					postWaitMessage(s, stackId, acid, r.getParentId(), null,
							status, false, map);
				}
			}
		} else {
			resourceComplete(s, r, true, acid, stackId, physicalId, map);
			if (r.getParentId() != null) {
				postWaitMessage(s, stackId, acid, r.getParentId(), null,
						status, false, map);
			}
		}
	}

	private static void resourceComplete(final Session s,
			final ResourcesBean r, final boolean success, final long userId,
			final String stackId, final String physicalId, final Map<String, String[]> map) throws Exception {
		r.setNoWait(0);
		r.setStatus("CREATE_COMPLETE");
		s.save(r);
		final String clazz = r.getWaitHook();
		if (clazz != null) {
			final Object c = Class.forName(clazz).newInstance();
			if (!(c instanceof WaitHook)) {
				throw new Exception("Clazz is not of interface WaitHook "
						+ clazz);
			}
			final WaitHook w = (WaitHook) c;
			w.postWait(s, success, userId, stackId, physicalId,
					r.getResourceData(), map);
		}
	}

	public static void runAsyncAWSScript(final String stackId,
			final long userId, final String script,
			final TemplateContext paramterValues) throws Exception {
		final ExecutorService exsrv = Appctx.getExecutorService();
		final Runnable r = new Runnable() {

			@Override
			public void run() {
			    try {
			        HibernateUtil
			        .withSession(new HibernateUtil.Operation<Object>() {

			            @Override
			            public Object ex(final Session s,
			                    final Object... as) throws Exception {
			                runAWSScript(s, stackId, userId, script,
			                        paramterValues, false);
			                return null;
			            }
			        });
			    } catch (Exception e) {
			        logger.error("Failed to run async AWS script.  StackId:" +
			                stackId + " script: " + script,
			                e);
			    }
			}

		};
		exsrv.submit(r);
	}

	public static Map<String, Object> runAWSScript(final Session s,
			final String stackId, final long userId, final String script,
			final TemplateContext parameterValues, final boolean update)
			throws Exception {
		final AccountBean ac = AccountUtil.readAccount(s, userId);
		final TemplateExecutor ex = Appctx.getBean("TemplateExecutor");
		final InputStream in = StringHelper.toInputStream(script);
		final Template template = new Template(in);
		in.close();
		parameterValues.setMappings(template.getMappings());
		Map<String, Object> ret = null;
		if (update) {
			ret = ex.update(s, AccountUtil.toAccount(ac), stackId,
					ac.getDefZone(), parameterValues, template);
		} else {
			ret = ex.create(s, AccountUtil.toAccount(ac), stackId,
					ac.getDefZone(), parameterValues, template);
		}
		final List<CFStackBean> l = CFUtil.selectStack(s, ac.getId(), stackId);
		if (l != null && l.size() > 0) {
			final CFStackBean b = l.get(0);
			b.setStatus("created");
			s.save(b);
		}
		return ret;
	}

	public static Map<String, Object> runAWSScript(final String stackId,
			final long userId, final String script,
			final TemplateContext paramterValues) throws Exception {
		return HibernateUtil
				.withSession(new HibernateUtil.Operation<Map<String, Object>>() {

					@Override
					public Map<String, Object> ex(final Session s,
							final Object... as) throws Exception {
						return runAWSScript(s, stackId, userId, script,
								paramterValues, false);
					}
				});
	}

	public static void runChefClient(final Session s, final AccountType ac,
			final List<String> instances) throws Exception {
		final String keyDir = (String) ConfigurationUtil
				.getConfiguration(Arrays.asList(new String[] { "KEYS_DIR" }));
		final String key = keyDir + "/" + ac.getDefKeyName() + ".pem";
		for (final String i : instances) {
			final InstanceBean ib = InstanceUtil.getInstance(s, i);
			CFUtil.executeCommand(
					ac,
					null,
					"ssh",
					"-i",
					key,
					"-o",
					"StrictHostKeyChecking=false",
					"root@"
							+ InstanceUtil.getIP(ib.getPublicIp(),
									ib.getPrivateIp(), ib.getAvzone()),
					"chef-client");
		}
	}

	@SuppressWarnings("unchecked")
	public static List<ResourcesBean> selectDescResourceRecords(
			final Session s, final long userId, final String stackId,
			final String parentId, final String physicalId) throws Exception {
		final CommaObject co = new CommaObject();
		co.setSeparator(" and ");
		if (userId != 0) {
			co.add("userId=" + userId);
		}
		if (stackId != null) {
			co.add("stackId='" + stackId + "'");
		}

		if (parentId != null) {
			co.add("parentId='" + parentId + "'");
		} else {
			co.add("parentId=null");
		}
		if (physicalId != null) {
			co.add("physicalId='" + physicalId + "'");
		}
		final String sql = "from ResourcesBean"
				+ (co.getList().size() > 0 ? " where " + co.toString() : "")
				+ " order by id desc";
		final Query q = s.createQuery(sql);
		return q.list();
	}

	@SuppressWarnings("unchecked")
	public static List<ResourcesBean> selectResourceRecords(final Session s,
			final long userId, final String stackId, final String parentId,
			final String physicalId, final boolean forDeletion)
			throws Exception {
		final CommaObject co = new CommaObject();
		co.setSeparator(" and ");
		if (userId != 0) {
			co.add("userId=" + userId);
		}
		if (stackId != null) {
			co.add("stackId='" + stackId + "'");
		}
		if (parentId != null) {
			co.add("parentId='" + parentId + "'");
			if (!forDeletion) {
				co.add("physicalId<>parentId");
			}
		} else {
			co.add("parentId=null");
		}
		if (physicalId != null) {
			co.add("physicalId='" + physicalId + "'");
		}
		final String sql = "from ResourcesBean"
				+ (co.getList().size() > 0 ? " where " + co.toString() : "");
		final Query q = s.createQuery(sql);
		return q.list();
	}

	@SuppressWarnings("unchecked")
	public static List<ResourcesBean> selectResourceRecordsOfType(
			final Session s, final long userId, final String stackId,
			final String parentId, final String type) throws Exception {
		final CommaObject co = new CommaObject();
		co.setSeparator(" and ");
		if (userId != 0) {
			co.add("userId=" + userId);
		}
		if (stackId != null) {
			co.add("stackId='" + stackId + "'");
		}
		if (parentId != null) {
			co.add("parentId='" + parentId + "'");
		}
		if (type != null) {
			co.add("type='" + type + "'");
		}
		final String sql = "from ResourcesBean"
				+ (co.getList().size() > 0 ? " where " + co.toString() : "");
		final Query q = s.createQuery(sql);
		return q.list();
	}

	@SuppressWarnings("unchecked")
	public static List<CFStackBean> selectStack(final Session s,
			final long userId, final String stackName) {
		String sql = "from CFStackBean where userId=" + userId;
		if (stackName != null) {
			sql += " and stackName='" + stackName + "'";
		}
		final Query q = s.createQuery(sql);
		return q.list();
	}

	@SuppressWarnings("unchecked")
	public static List<String> toStringList(final Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof List) {
			return (List<String>) obj;
		}
		if (obj instanceof ArrayNode) {
			final List<String> l = new ArrayList<String>();
			final ArrayNode anode = (ArrayNode) obj;
			for (final JsonNode o : anode) {
				l.add(o.getTextValue());
			}
			return l;
		}
		throw new RuntimeException("Couldn't convert " + obj);
	}

	public static void updateAsyncAWSScript(final String stackId,
			final long userId, final String script,
			final TemplateContext paramterValues) throws Exception {
		final ExecutorService exsrv = Appctx.getExecutorService();
		final Runnable r = new Runnable() {

			@Override
			public void run() {
				HibernateUtil
						.withSession(new HibernateUtil.Operation<Object>() {

							@Override
							public Object ex(final Session s,
									final Object... as) throws Exception {
								runAWSScript(s, stackId, userId, script,
										paramterValues, true);
								return null;
							}
						});
			}

		};
		exsrv.submit(r);
	}

	public static void updateAWSScript(final String stackId, final long userId,
			final String script, final TemplateContext paramterValues)
			throws Exception {
		HibernateUtil.withSession(new HibernateUtil.Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... as)
					throws Exception {
				runAWSScript(s, stackId, userId, script, paramterValues, true);
				return null;
			}
		});
	}

	public static ResourcesBean updatePhysicalId(final long resourceBeanId,
			final String physicalId) {
		return HibernateUtil.withNewSession(new Operation<ResourcesBean>() {
			@Override
			public ResourcesBean ex(final Session s, final Object... args)
					throws Exception {
				final ResourcesBean resBean = getResourceBean(s, resourceBeanId);
				resBean.setPhysicalId(physicalId);
				s.save(resBean);
				return resBean;
			}
		});
	}

	public static Resource updateResource(final CallStruct call)
			throws Exception {
		return getProvider(call.getType()).update(call);
	}

}
