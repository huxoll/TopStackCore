package com.msi.tough.engine.aws.elasticache;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.ec2.InstanceType;
import com.msi.tough.cf.elasticache.CacheClusterType;
import com.msi.tough.core.BaseException;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.core.MapUtil;
import com.msi.tough.core.StringHelper;
import com.msi.tough.engine.aws.ec2.Instance;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.model.elasticache.CacheClusterBean;
import com.msi.tough.model.elasticache.CacheNodeBean;
import com.msi.tough.model.elasticache.CacheNodeTypeBean;
import com.msi.tough.model.elasticache.CacheParameterGroupBean;
import com.msi.tough.model.elasticache.CacheSecurityGroupBean;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.ChefUtil;
import com.msi.tough.utils.ConfigurationUtil;
import com.msi.tough.utils.EcacheUtil;
import com.msi.tough.utils.ElasticacheFaults;

public class CacheCluster extends BaseProvider {

	private static final Logger logger = LoggerFactory
			.getLogger(CacheCluster.class.getName());

	public static String TYPE = "AWS::Elasticache::CacheCluster";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		final String name = call.getName();

		final String autoMinorVersionUpgrade = (String) call
				.getProperty("AutoMinorVersionUpgrade");
		final String rcacheNodeType = (String) call
				.getRequiredProperty("CacheNodeType");
		final String cacheParameterGroupName = (String) call
				.getProperty("CacheParameterGroupName");
		final Object rsecurityGroups = call
				.getRequiredProperty("CacheSecurityGroupNames");
		final List<String> secGrpList = JsonUtil.toStringList(rsecurityGroups);
		final String engine = (String) call.getRequiredProperty("Engine");
		final String engineVersion = (String) call.getProperty("EngineVersion");
		final String notificationTopicArn = (String) call
				.getProperty("NotificationTopicArn");
		final String numCacheNodes = (String) call
				.getRequiredProperty("NumCacheNodes");
		final int noCacheNodes = Integer.parseInt(numCacheNodes);
		final String sport = (String) call.getProperty("Port");
		final String port = StringHelper.isBlank(sport) || sport.equals("0") ? "11211"
				: sport;
		final String avz = (String) (call
				.getProperty(PREFERREDAVAILABILITYZONE) == null ? ac
				.getDefZone() : call.getProperty(PREFERREDAVAILABILITYZONE));

		final String chefRole = "transcend_" + engine + "_"
				+ engineVersion.replace('.', '_');
		final String databag = EcacheUtil.getDatabagName(ac.getId(), name);
		final String vmem = "1000";

		// final AccountBean account = AccountUtil.readAccount(s, ac.getId());
		final Long ccid = HibernateUtil.withNewSession(new Operation<Long>() {

			@Override
			public Long ex(final Session s, final Object... args)
					throws Exception {
				final ResourcesBean resBean = getResourceBean(s);
				resBean.setWaitHook(ElasticacheClusterWaitHook.class.getName());
				s.save(resBean);
				final String preferredMaintenanceWindow = (String) call
						.getProperty("PreferredMaintenanceWindow");

				CacheClusterBean cacheCluster = EcacheUtil.getCacheClusterBean(
						s, ac.getId(), name);

				if (cacheCluster == null) {
					cacheCluster = new CacheClusterBean();
					cacheCluster.setAcid(ac.getId());
					cacheCluster.setCreatedTime(new Date());
					cacheCluster.setCacheClusterStatus("creating");
					cacheCluster.setName(name);
					cacheCluster.setNodeCount(numCacheNodes == null ? 1
							: Integer.parseInt(numCacheNodes));
					final CacheNodeTypeBean cacheNodeType = EcacheUtil
							.getCacheNodeTypeBean(s, rcacheNodeType);
					if (cacheNodeType == null) {
						throw ErrorResponse
								.invlidData("CacheNodeType not found:"
										+ rcacheNodeType);
					}
					cacheCluster.setNodeTypeId((long) cacheNodeType.getId());
					cacheCluster.setEngine(engine);
					cacheCluster.setEngineVersion(engineVersion);
					final CacheParameterGroupBean parameterGroup = EcacheUtil
							.getCacheParameterGroupBean(s, ac.getId(),
									cacheParameterGroupName);
					if (parameterGroup == null) {
						throw ElasticacheFaults.CacheParameterGroupNotFound();
					}
					cacheCluster.setParameterGroupId((long) parameterGroup
							.getId());
					cacheCluster.setPreferredAvailabilityZone(avz);
					final CommaObject requestedSecurityGroups = new CommaObject(
							secGrpList);
					cacheCluster.setSecurityGroups(requestedSecurityGroups
							.toString());
					cacheCluster.setStackId(call.getStackId());
					s.save(cacheCluster);
				}

				if (!StringHelper.isBlank(autoMinorVersionUpgrade)) {
					cacheCluster.setAutoMinorVersionUpgrade(Boolean
							.parseBoolean(autoMinorVersionUpgrade));
				}
				cacheCluster.setAcid(ac.getId());
				cacheCluster.setCreatedTime(new Date());
				cacheCluster.setCacheClusterStatus("creating");
				cacheCluster.setEngine(engine);
				cacheCluster.setEngineVersion(engineVersion);
				cacheCluster.setName(name);
				cacheCluster.setNodeCount(numCacheNodes == null ? 1 : Integer
						.parseInt(numCacheNodes));
				final CacheNodeTypeBean cacheNodeType = EcacheUtil
						.getCacheNodeTypeBean(s, rcacheNodeType);
				if (cacheNodeType == null) {
					throw ErrorResponse.invlidData("CacheNodeType not found:"
							+ rcacheNodeType);
				}
				cacheCluster.setNodeTypeId((long) cacheNodeType.getId());
				cacheCluster.setNotificationTopicArn(notificationTopicArn);
				// cacheCluster
				// .setNotificationTopicStatus(NotificationTopicStatus.inactive);
				logger.debug("Parameter Group: " + cacheParameterGroupName);
				final CacheParameterGroupBean parameterGroup = EcacheUtil
						.getCacheParameterGroupBean(s, ac.getId(),
								cacheParameterGroupName);
				if (parameterGroup == null) {
					// throw
					// ErrorResponse.invlidData("ParameterGroup not found:"+
					// request.getCacheParameterGroupName());
					// throw
					// ElasticacheFaults.CacheParameterGroupNotFound(requestId);
					throw new BaseException("invalid parameter");
				}
				cacheCluster.setParameterGroupId((long) parameterGroup.getId());
				cacheCluster.setPort(Integer.parseInt(port));
				cacheCluster.setPreferredAvailabilityZone(avz);
				cacheCluster
						.setPreferredMaintenanceWindow(preferredMaintenanceWindow);
				// All of the Security Group(s) are pre-created, we need to find
				// them
				// and setup the M2M relationship
				final CommaObject requestedSecurityGroups = new CommaObject(
						secGrpList);
				cacheCluster.setSecurityGroups(requestedSecurityGroups
						.toString());
				cacheCluster.setStackId(call.getStackId());
				/*
				 * else{ maxConnectionsParameter =
				 * ElasticacheDbUtils.getParameter(session, parameterGroup,
				 * Constants.MAXSIMULTANEOUSCONNECTIONS); } if(
				 * maxConnectionsParameter == null ){ throw
				 * ErrorResponse.invlidData(
				 * "Max Connections not found in Parameter Group:"); }
				 */

				// Determine the memory to allocate to the node
				// CacheNodeTypeSpecificParameterBean memoryParm = null;
				// CacheNodeTypeSpecificValueBean memoryValue = null;
				// for (final CacheNodeTypeSpecificParameterBean cacheNodeParm :
				// parameterGroup
				// .getNodeSpecificParameters()) {
				// if (cacheNodeParm.getParameterName().equalsIgnoreCase(
				// Constants.MAXCACHEMEMORY)) {
				// memoryParm = cacheNodeParm;
				//
				// for (final CacheNodeTypeSpecificValueBean cacheNodeValue :
				// cacheNodeParm
				// .getSpecificValues()) {
				// if (cacheNodeValue.getCacheNodeType().equals(cacheNodeType))
				// {
				// memoryValue = cacheNodeValue;
				// break;
				// }
				// }
				// break;
				// }
				// }
				// if (memoryParm == null || memoryValue == null) {
				// throw ErrorResponse
				// .invlidData("Unable to locate size for CacheNodeType:"
				// + rcacheNodeType);
				// }

				// Establish an Identity for the cache cluster
				s.save(cacheCluster);

				// Create the databag
				final int cacheSize = Integer.parseInt(vmem);
				// int maxConnections =
				// Integer.parseInt(maxConnectionsParameter.getParameterValue());
				final ElasticacheConfigDatabagItem configDataBagItem = new ElasticacheConfigDatabagItem(
						"config", cacheSize, Integer.parseInt(port),
						(String) ConfigurationUtil.getConfiguration(
								TRANSCENDURL, avz), ac.getAccessKey(), ac
								.getId(), call.getStackId());

				final ElasticacheParameterGroupDatabagItem parameterGroupDatabagItem = new ElasticacheParameterGroupDatabagItem(
						s, "parameters", parameterGroup, cacheNodeType);

				final ElasticacheDatabag bag = new ElasticacheDatabag(
						configDataBagItem, parameterGroupDatabagItem);

				logger.debug("StackIdDatabag: "
						+ JsonUtil.toJsonPrettyPrintString(bag));

				// Store the Databag
				ElasticacheServiceUtils.createElasticacheDatabag(databag, bag);
				return (long) cacheCluster.getId();
			}
		});

		// launch instances
		call.setWaitHookClass(ElasticacheNodeWaitHook.class.getName());
		final CommaObject cosec = HibernateUtil
				.withNewSession(new Operation<CommaObject>() {

					@Override
					public CommaObject ex(final Session s, final Object... args)
							throws Exception {
						final CommaObject cosec = new CommaObject();
						for (final String i : secGrpList) {
							final CacheSecurityGroupBean b = EcacheUtil
									.getCacheSecurityGroupBean(s, ac.getId(), i);
							cosec.add(b.getProviderId());
						}
						return cosec;
					}
				});
		for (int i = 0; i < noCacheNodes; i++) {
			final InstanceType inst = Instance.createChefInstance(ac,
					"instance_" + i, name, call, MapUtil.create(
							AVAILABILITYZONE, avz, CHEFROLES, chefRole,
							DATABAG, databag, SECURITYGROUPIDS,
							cosec.toString(), SERVICE, "cache"));
			HibernateUtil.withNewSession(new Operation<Object>() {

				@Override
				public Object ex(final Session s, final Object... args)
						throws Exception {
					final CacheNodeBean node = new CacheNodeBean();
					node.setAddress(inst.getPublicIp());
					node.setCacheCluster(ccid);
					node.setCreatedTime(new Date());
					node.setNodeStatus("creating");
					node.setParameterGroupStatus("launching");
					node.setInstaceId(inst.getInstanceId());
					s.save(node);
					return null;
				}
			});
		}
		final CacheClusterType ret = new CacheClusterType();
		ret.setName(name);
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Resource delete(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		final String name = call.getPhysicalId();
		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {
				final CacheClusterBean cluster = EcacheUtil
						.getCacheClusterBean(s, ac.getId(), name);
				if (cluster == null) {
					throw new Exception("CacheCluster not found " + name);
				}
				return null;
			}
		});

		// delete child resources
		CFUtil.deleteStackResources(ac, call.getStackId(), name, null);
		final String databag = EcacheUtil.getDatabagName(ac.getId(), name);
		ChefUtil.deleteDatabag(databag);

		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {
				final CacheClusterBean cluster = EcacheUtil
						.getCacheClusterBean(s, ac.getId(), name);
				if (cluster != null) {
					final Query q = s
							.createQuery("from CacheNodeBean where cacheCluster="
									+ cluster.getId());
					final List<CacheNodeBean> l = q.list();
					for (final CacheNodeBean n : l) {
						s.delete(n);
					}
					s.delete(cluster);
				}
				CFUtil.deleteResourceRecords(s, ac.getId(), call.getStackId(),
						name, null);
				CFUtil.deleteResourceRecords(s, ac.getId(), call.getStackId(),
						null, name);
				return null;
			}
		});
		return null;
	}
}
