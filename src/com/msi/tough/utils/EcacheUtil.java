package com.msi.tough.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.IntNode;
import org.hibernate.Query;
import org.hibernate.Session;

import com.amazonaws.services.elasticache.model.CacheCluster;
import com.amazonaws.services.elasticache.model.CacheNode;
import com.amazonaws.services.elasticache.model.CacheNodeTypeSpecificParameter;
import com.amazonaws.services.elasticache.model.CacheNodeTypeSpecificValue;
import com.amazonaws.services.elasticache.model.CacheParameterGroup;
import com.amazonaws.services.elasticache.model.CacheParameterGroupStatus;
import com.amazonaws.services.elasticache.model.CacheSecurityGroup;
import com.amazonaws.services.elasticache.model.CacheSecurityGroupMembership;
import com.amazonaws.services.elasticache.model.DescribeCacheParametersResult;
import com.amazonaws.services.elasticache.model.EC2SecurityGroup;
import com.amazonaws.services.elasticache.model.Parameter;
import com.amazonaws.services.elasticache.model.ParameterNameValue;
import com.amazonaws.services.elasticache.model.PendingModifiedValues;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.ec2.AuthorizeSecurityGroupIngressType;
import com.msi.tough.cf.ec2.SecurityGroupType;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.ExecutorHelper;
import com.msi.tough.core.ExecutorHelper.Executable;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.core.StringHelper;
import com.msi.tough.engine.aws.ec2.Instance;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.elasticache.CacheClusterBean;
import com.msi.tough.model.elasticache.CacheNodeBean;
import com.msi.tough.model.elasticache.CacheNodeTypeBean;
import com.msi.tough.model.elasticache.CacheNodeTypeSpecificValueBean;
import com.msi.tough.model.elasticache.CacheParameterGroupBean;
import com.msi.tough.model.elasticache.CacheParameterGroupFamilyBean;
import com.msi.tough.model.elasticache.CacheSecurityGroupBean;
import com.msi.tough.model.elasticache.ParameterBean;
import com.msi.tough.query.QueryFaults;

public class EcacheUtil {
	public static final String DEFAULT_GROUP = "default";

	@SuppressWarnings("unchecked")
	public static void copyParameterGroup(final Session session,
			final long sourceGroupId, final long destGroupId) {

		final Query q = session.createQuery("from ParameterBean where groupId="
				+ sourceGroupId);
		final List<ParameterBean> l = q.list();

		for (final ParameterBean p : l) {
			final ParameterBean pb = new ParameterBean();
			pb.setGroupId(destGroupId);
			pb.setAllowedValues(p.getAllowedValues());
			pb.setDataType(p.getDataType());
			pb.setDescription(p.getDescription());
			pb.setMinimumEngineVersion(p.getMinimumEngineVersion());
			pb.setName(p.getName());
			pb.setParameterValue(p.getParameterValue());
			pb.setSource(p.getSource());
			pb.setModifiable(p.isModifiable());
			pb.setNodeSpecific(p.isNodeSpecific());
			session.save(pb);
		}
	}

	public static CacheParameterGroupBean createParameterGroup(
			final Session session, final long acid, final String familyName,
			final String groupName, final String description) {
		CacheParameterGroupBean b = getCacheParameterGroupBean(session, acid,
				groupName);
		if (b != null) {
			return b;
		}
		final CacheParameterGroupFamilyBean family = getParameterGroupFamily(
				session, familyName);
		if (family == null) {
			throw QueryFaults.InvalidParameterValue();
		}

		b = new CacheParameterGroupBean();
		b.setAcid(acid);
		b.setDescription(description);
		b.setFamilyId(family.getId());
		b.setName(groupName);
		session.save(b);

		final CacheParameterGroupBean familyParameterGroup = getFamilyParameterGroup(
				session, familyName);

		copyParameterGroup(session, familyParameterGroup.getId(), b.getId());
		return b;
	}

	public static CacheSecurityGroupBean createSecurityGroup(
			final Session session, final long acid, final String groupName,
			final String description, String stackId, final String parentId)
			throws Exception {
		CacheSecurityGroupBean b = getCacheSecurityGroupBean(session, acid,
				groupName);
		if (b != null) {
			return b;
		}

		if (stackId == null) {
			stackId = "__ecache_" + StringHelper.randomStringFromTime();
		}
		final String secGrpName = getSecurityGroupName(acid, groupName);

		final AccountBean account = AccountUtil.readAccount(session, acid);

		final String pid = SecurityGroupUtils.createSecurityGroup(
				AccountUtil.toAccount(account), new TemplateContext(null),
				parentId, stackId, account.getDefZone(), secGrpName,
				description);

		b = new CacheSecurityGroupBean();
		b.setAcid(acid);
		b.setDescription(description);
		b.setName(groupName);
		b.setStackId(stackId);
		b.setProviderId(pid);
		b.setProviderName(secGrpName);
		session.save(b);

		return b;
	}

	public static void deleteParameterGroupBean(final Session session,
			final long acid, final String name) {
		final CacheParameterGroupBean b = getCacheParameterGroupBean(session,
				acid, name);
		final List<ParameterBean> ps = selectParameterBean(session, b.getId());
		for (final ParameterBean p : ps) {
			session.delete(p);
		}
		session.delete(b);
	}

	public static void deleteSecurityGroupBean(final Session session,
			final AccountType ac, final String name) throws Exception {
		final CacheSecurityGroupBean b = getCacheSecurityGroupBean(session,
				ac.getId(), name);
		CFUtil.deleteStackResources(ac, b.getStackId(), name, null);
		session.delete(b);
	}

	@SuppressWarnings("unchecked")
	public static void ensureDefaultParameterGroup(final Session session,
			final long acid) {
		final Query q = session
				.createQuery("from CacheParameterGroupFamilyBean");
		final List<CacheParameterGroupFamilyBean> l = q.list();
		if (l != null) {
			for (final CacheParameterGroupFamilyBean b : l) {
				if (getCacheParameterGroupBean(session, acid, DEFAULT_GROUP) != null) {
					return;
				}
				createParameterGroup(session, acid, b.getFamily(),
						DEFAULT_GROUP, "Defaut Parameter Group");
			}
		}
	}

	public static void ensureDefaultSecurityGroup(final Session session,
			final long acid) throws Exception {
		if (getCacheSecurityGroupBean(session, acid, DEFAULT_GROUP) != null) {
			return;
		}
		final CacheSecurityGroupBean grp = createSecurityGroup(session, acid,
				DEFAULT_GROUP, "Defaut Elasticache Security Group", null, null);
		final AccountBean account = AccountUtil.readAccount(session, acid);
		// final String secGrpName = getSecurityGroupName(acid, DEFAULT_GROUP);
		SecurityGroupUtils.authorizeSecurityGroupIngress(
				AccountUtil.toAccount(account), grp.getProviderId(), 11211,
				grp.getStackId());
	}

	public static CacheClusterBean getCacheClusterBean(final Session session,
			final long acid, final String name) {
		final List<CacheClusterBean> l = selectCacheClusterBean(session, acid,
				name);
		if (l == null || l.size() == 0) {
			return null;
		}
		return l.get(0);
	}

	@SuppressWarnings("unchecked")
	public static CacheNodeTypeBean getCacheNodeTypeBean(final Session session,
			final long id) {
		final Query q = session.createQuery("from CacheNodeTypeBean where id="
				+ id);
		final List<CacheNodeTypeBean> l = q.list();
		if (l != null && l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static CacheNodeTypeBean getCacheNodeTypeBean(final Session session,
			final String type) {
		final Query q = session
				.createQuery("from CacheNodeTypeBean where type='" + type + "'");
		final List<CacheNodeTypeBean> l = q.list();
		if (l != null && l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static CacheNodeTypeSpecificValueBean getCacheNodeTypeSpecificValueBean(
			final Session session, final long parameterId, final long nodeTypeId) {
		final Query q = session
				.createQuery("from CacheNodeTypeSpecificValueBean where parameterId="
						+ parameterId + " and nodeTypeId=" + nodeTypeId);
		final List<CacheNodeTypeSpecificValueBean> l = q.list();
		if (l != null && l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static CacheParameterGroupBean getCacheParameterGroupBean(
			final Session session, final long acid, final String name) {
		final Query q = session
				.createQuery("from CacheParameterGroupBean where acid=" + acid
						+ " and name='" + name + "'");
		final List<CacheParameterGroupBean> l = q.list();
		if (l != null && l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static CacheSecurityGroupBean getCacheSecurityGroupBean(
			final Session session, final long acid, final String name) {
		final Query q = session
				.createQuery("from CacheSecurityGroupBean where acid=" + acid
						+ " and name='" + name + "'");
		final List<CacheSecurityGroupBean> l = q.list();
		if (l != null && l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	public static String getDatabagName(final long acid, final String name) {
		return "__ecache-" + acid + "-" + name;
	}

	public static CacheParameterGroupBean getFamilyParameterGroup(
			final Session session, final String familyName) {
		return getCacheParameterGroupBean(session, 0, DEFAULT_GROUP + "."
				+ familyName);
	}

	@SuppressWarnings("unchecked")
	public static CacheParameterGroupFamilyBean getParameterGroupFamily(
			final Session session, final int id) {
		final Query q = session
				.createQuery("from CacheParameterGroupFamilyBean where id="
						+ id);
		final List<CacheParameterGroupFamilyBean> l = q.list();
		if (l != null && l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static CacheParameterGroupFamilyBean getParameterGroupFamily(
			final Session session, final long familyId) {
		final Query q = session
				.createQuery("from CacheParameterGroupFamilyBean where id="
						+ familyId);
		final List<CacheParameterGroupFamilyBean> l = q.list();
		if (l != null && l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static CacheParameterGroupFamilyBean getParameterGroupFamily(
			final Session session, final String familyName) {
		final Query q = session
				.createQuery("from CacheParameterGroupFamilyBean where family='"
						+ familyName + "'");
		final List<CacheParameterGroupFamilyBean> l = q.list();
		if (l != null && l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	public static XMLNode getRootNode(final String tag) {
		final XMLNode nodeRoot = new XMLNode(tag);
		nodeRoot.addAttr("xmlns",
				"http://elasticache.amazonaws.com/doc/2012-03-09/");
		return nodeRoot;
	}

	public static String getSecurityGroupName(final long acid,
			final String groupName) {
		return "__ecache_" + acid + "_" + groupName;
	}

	public static String getStackName(final long acid, final String name) {
		return String.format("__ecache-%d-%s", acid, name);
	}

	public static void rebootCluster(final Session session,
			final CacheClusterBean cc, final AccountType account) {

		cc.setCacheClusterStatus("pending-reboot");
		if (cc.getParameterGroupStatus() == null
				|| !cc.getParameterGroupStatus().equals("in-sync")) {
			cc.setParameterGroupStatus("pending-reboot");
		} else {
			cc.setParameterGroupStatus("in-sync");
		}
		session.save(cc);
		final List<CacheNodeBean> lnb = EcacheUtil.selectCacheNodeBean(session,
				cc.getId());
		final List<String> instances = new ArrayList<String>();
		for (final CacheNodeBean nb : lnb) {
			nb.setNodeStatus("pending-reboot");
			nb.setParameterGroupStatus(cc.getParameterGroupStatus());
			session.save(nb);
			instances.add(nb.getInstaceId());
		}

		final String databag = getDatabagName(account.getId(), cc.getName());
		try {
			final String bag = ChefUtil.getDatabagItem(databag, "config");
			final JsonNode o = JsonUtil.load(bag);
			final Map<String, Object> m = JsonUtil.toMap(o);
			String revision = "0";
			if (m.containsKey("revision")) {
				final Object obj = m.get("revision");
				if (obj instanceof IntNode) {
					revision = ((IntNode) obj).getValueAsText();
				}
				if (obj instanceof String) {
					revision = (String) obj;
				}
			}
			revision = String.valueOf(Integer.parseInt(revision) + 1);
			m.put("revision", revision);
			final String json = JsonUtil.toJsonString(m);
			ChefUtil.putDatabagItem(databag, "config", json);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		final Executable r = new ExecutorHelper.Executable() {
			@Override
			public void run() {
				try {
					rebootInstance(account, instances);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		};
		ExecutorHelper.execute(r);
	}

	private static void rebootInstance(final AccountType account,
			final List<String> instances) throws Exception {
		for (final String c : instances) {
			final CallStruct call = new CallStruct();
			call.setCtx(new TemplateContext(null));
			call.setAc(account);
			call.setPhysicalId(c);
			final Instance prov = new Instance();
			prov.reboot(call);
		}
	}

	@SuppressWarnings("unchecked")
	public static void resetParameters(final Session session,
			final AccountType ac, final String name,
			final List<ParameterNameValue> vals,
			final Boolean resetAllParameters) {

		final CacheParameterGroupBean pgrp = getCacheParameterGroupBean(
				session, ac.getId(), name);
		final CacheParameterGroupFamilyBean fb = getParameterGroupFamily(
				session, pgrp.getFamilyId());
		final CacheParameterGroupBean fgrp = getFamilyParameterGroup(session,
				fb.getFamily());
		final Query fq = session
				.createQuery("from ParameterBean where groupId=" + fgrp.getId());
		final List<ParameterBean> fl = fq.list();

		final Query q = session.createQuery("from ParameterBean where groupId="
				+ pgrp.getId());
		final List<ParameterBean> l = q.list();

		for (final ParameterBean pb : l) {
			boolean reset = false;
			if (resetAllParameters) {
				reset = true;
			} else {
				for (final ParameterNameValue v : vals) {
					if (v.getParameterName().equals(pb.getName())) {
						reset = true;
						break;
					}
				}
			}
			if (reset) {
				for (final ParameterBean f : fl) {
					if (f.getName().equals(pb.getName())) {
						pb.setParameterValue(f.getParameterValue());
						pb.setSource(f.getSource());
						session.save(pb);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static List<CacheClusterBean> selectCacheClusterBean(
			final Session session, final long acid, final String name) {
		final Query q = session.createQuery("from CacheClusterBean where acid="
				+ acid + (name != null ? " and name='" + name + "'" : ""));
		return q.list();
	}

	@SuppressWarnings("unchecked")
	public static List<CacheNodeBean> selectCacheNodeBean(
			final Session session, final long clusterId) {
		final Query q = session
				.createQuery("from CacheNodeBean where cacheCluster="
						+ clusterId);
		final List<CacheNodeBean> l = q.list();
		return l;
	}

	@SuppressWarnings("unchecked")
	public static List<ParameterBean> selectParameterBean(
			final Session session, final long grpid) {
		final Query q = session.createQuery("from ParameterBean where groupId="
				+ grpid);
		final List<ParameterBean> l = q.list();
		return l;
	}

	@SuppressWarnings("unchecked")
	public static CacheCluster toAwsCacheCluster(final Session session,
			final CacheClusterBean b) {

		// Convert to AMAZON object for DTO
		final CacheCluster awsCacheCluster = new CacheCluster();
		awsCacheCluster.setAutoMinorVersionUpgrade(b
				.getAutoMinorVersionUpgrade());
		awsCacheCluster.setCacheClusterCreateTime(b.getCreatedTime());
		awsCacheCluster.setCacheClusterId(b.getName());
		awsCacheCluster.setCacheClusterStatus(b.getCacheClusterStatus());
		final CacheNodeTypeBean nodeType = getCacheNodeTypeBean(session,
				b.getNodeTypeId());
		if (nodeType != null) {
			awsCacheCluster.setCacheNodeType(nodeType.getType());
		}

		// cache nodes
		{
			final Collection<CacheNode> awsCacheNodes = new ArrayList<CacheNode>();
			final List<CacheNodeBean> l = selectCacheNodeBean(session,
					b.getId());
			int seq = 0;
			for (final CacheNodeBean n : l) {
				seq++;
				final CacheNode awsCacheNode = new CacheNode();
				awsCacheNode.setCacheNodeCreateTime(n.getCreatedTime());
				awsCacheNode.setCacheNodeStatus(n.getNodeStatus());
				awsCacheNode.setCacheNodeId(Integer.toString(seq));
				final com.amazonaws.services.elasticache.model.Endpoint endpoint = new com.amazonaws.services.elasticache.model.Endpoint();
				endpoint.setAddress(n.getAddress());
				endpoint.setPort(b.getPort());
				awsCacheNode.setEndpoint(endpoint);
				awsCacheNodes.add(awsCacheNode);
				awsCacheNode.setParameterGroupStatus(n
						.getParameterGroupStatus());
			}
			awsCacheCluster.setCacheNodes(awsCacheNodes);
		}

		// Parameter Group
		{
			final Collection<CacheNode> awsCacheNodes = new ArrayList<CacheNode>();
			final Query q = session
					.createQuery("from CacheParameterGroupBean where id="
							+ b.getParameterGroupId());
			final List<CacheParameterGroupBean> l = q.list();
			final CacheParameterGroupBean pgb = l.get(0);
			final CacheParameterGroupStatus cacheParameterGroup = new CacheParameterGroupStatus();
			cacheParameterGroup.setCacheParameterGroupName(pgb.getName());
			cacheParameterGroup.setParameterApplyStatus(b
					.getParameterGroupStatus());
			awsCacheCluster.setCacheParameterGroup(cacheParameterGroup);
		}

		// Security Group
		{
			final CommaObject sgs = new CommaObject(b.getSecurityGroups());
			final Collection<CacheSecurityGroupMembership> awsCacheSecurityGroups = new ArrayList<CacheSecurityGroupMembership>();
			for (final String sg : sgs.toList()) {
				final com.amazonaws.services.elasticache.model.CacheSecurityGroupMembership awsSecurityGroup = new com.amazonaws.services.elasticache.model.CacheSecurityGroupMembership();
				awsSecurityGroup.setCacheSecurityGroupName(sg);
				awsCacheSecurityGroups.add(awsSecurityGroup);
			}
			awsCacheCluster.setCacheSecurityGroups(awsCacheSecurityGroups);
		}
		// // Notification Configuration
		// final NotificationConfiguration nf = new NotificationConfiguration();
		// nf.setTopicArn(getNotificationTopicArn());
		// nf.setTopicStatus(getNotificationTopicStatus().name());
		// awsCacheCluster.setNotificationConfiguration(nf);

		awsCacheCluster.setNumCacheNodes(b.getNodeCount());

		// Pending Modified Values
		final PendingModifiedValues pending = new PendingModifiedValues();
		if (b.getNewEngineVersion() != null
				&& !b.getNewEngineVersion().equals(b.getEngineVersion())) {
			pending.setEngineVersion(b.getNewEngineVersion());
		}
		if (b.getNewNodeCount() != null && b.getNodeCount() != null
				&& b.getNodeCount() != b.getNewNodeCount()) {
			pending.setNumCacheNodes(b.getNewNodeCount());
			if (b.getNewNodeCount() < b.getNodeCount()) {
				final Collection<String> remove = new ArrayList<String>();
				final List<CacheNodeBean> nodes = EcacheUtil
						.selectCacheNodeBean(session, b.getId());
				int i = 0;
				for (final CacheNodeBean node : nodes) {
					i++;
					if (node.getNodeStatus().equals("removing")) {
						remove.add("" + i);
					}
				}
				pending.setCacheNodeIdsToRemove(remove);
			}
		}
		awsCacheCluster.setPendingModifiedValues(pending);

		awsCacheCluster.setPreferredAvailabilityZone(b
				.getPreferredAvailabilityZone());

		awsCacheCluster.setPreferredMaintenanceWindow(b
				.getPreferredMaintenanceWindow());

		// engine
		awsCacheCluster.setEngine(b.getEngine());
		awsCacheCluster.setEngineVersion(b.getEngineVersion());

		return awsCacheCluster;
	}

	public static CacheParameterGroup toAwsCacheParameterGroup(
			final Session session, final CacheParameterGroupBean b) {
		final CacheParameterGroup awsCacheParameterGroup = new CacheParameterGroup();
		final CacheParameterGroupFamilyBean family = getParameterGroupFamily(
				session, b.getFamilyId());
		if (family != null) {
			awsCacheParameterGroup.setCacheParameterGroupFamily(family
					.getFamily());
		}
		awsCacheParameterGroup.setCacheParameterGroupName(b.getName());
		awsCacheParameterGroup.setDescription(b.getDescription());

		return awsCacheParameterGroup;
	}

	public static CacheSecurityGroup toAwsCacheSecurityGroup(
			final Session session, final CacheSecurityGroupBean b)
			throws Exception {
		final CacheSecurityGroup grp = new CacheSecurityGroup();
		grp.setCacheSecurityGroupName(b.getName());
		grp.setDescription(b.getDescription());
		final AccountBean ac = AccountUtil.readAccount(session, b.getAcid());
		grp.setOwnerId(ac.getId() + "");

		final SecurityGroupType st = SecurityGroupUtils.describeSecurityGroup(
				AccountUtil.toAccount(ac), ac.getDefZone(), b.getProviderId());
		if (st.getSecurityGroupIngress() != null) {
			final Collection<EC2SecurityGroup> grps = new ArrayList<EC2SecurityGroup>();
			for (final AuthorizeSecurityGroupIngressType t : st
					.getSecurityGroupIngress()) {
				if (t.getSourceSecurityGroupName() != null) {
					final EC2SecurityGroup g = new EC2SecurityGroup();
					g.setEC2SecurityGroupName(t.getSourceSecurityGroupName());
					g.setEC2SecurityGroupOwnerId(ac.getTenant() + "");
					g.setStatus("authorized");
					grps.add(g);
				}
			}
			grp.setEC2SecurityGroups(grps);
		}
		return grp;
	}

	public static Parameter toAwsParameter(final ParameterBean b) {
		final Parameter awsParameter = new Parameter();
		awsParameter.setAllowedValues(b.getAllowedValues());
		awsParameter.setDataType(b.getDataType());
		awsParameter.setDescription(b.getDescription());
		awsParameter.setIsModifiable(b.isModifiable());
		awsParameter.setMinimumEngineVersion("" + b.getMinimumEngineVersion());
		awsParameter.setParameterName(b.getName());
		awsParameter.setParameterValue(b.getParameterValue());
		awsParameter.setSource(b.getSource());
		return awsParameter;
	}

	@SuppressWarnings("unchecked")
	public static DescribeCacheParametersResult toDescribeCacheParametersResult(
			final Session session, final long grpId, final String source) {
		final DescribeCacheParametersResult ret = new DescribeCacheParametersResult();
		final List<ParameterBean> l = EcacheUtil.selectParameterBean(session,
				grpId);
		final Collection<CacheNodeTypeSpecificParameter> specificParameters = new ArrayList<CacheNodeTypeSpecificParameter>();
		final Collection<Parameter> parameters = new ArrayList<Parameter>();
		for (final ParameterBean b : l) {
			if (source != null && b.getSource() != null
					&& !source.equals(b.getSource())) {
				continue;
			}
			if (b.isNodeSpecific()) {
				final CacheNodeTypeSpecificParameter p = new CacheNodeTypeSpecificParameter();
				p.setAllowedValues(b.getAllowedValues());
				final Collection<CacheNodeTypeSpecificValue> cacheNodeTypeSpecificValues = new ArrayList<CacheNodeTypeSpecificValue>();
				final Query qtype = session
						.createQuery("from CacheNodeTypeBean");
				final List<CacheNodeTypeBean> ltype = qtype.list();
				for (final CacheNodeTypeBean type : ltype) {
					final CacheNodeTypeSpecificValueBean vb = EcacheUtil
							.getCacheNodeTypeSpecificValueBean(session,
									b.getId(), type.getId());
					if (vb != null) {
						final CacheNodeTypeSpecificValue v = new CacheNodeTypeSpecificValue();
						v.setCacheNodeType(type.getType());
						v.setValue(vb.getParameterValue());
						cacheNodeTypeSpecificValues.add(v);
					}
				}
				p.setCacheNodeTypeSpecificValues(cacheNodeTypeSpecificValues);
				p.setDataType(b.getDataType());
				p.setDescription(b.getDescription());
				p.setIsModifiable(b.isModifiable());
				p.setMinimumEngineVersion("" + b.getMinimumEngineVersion());
				p.setParameterName(b.getName());
				p.setSource(b.getSource());
				specificParameters.add(p);
			} else {
				final Parameter p = new Parameter();
				p.setAllowedValues(b.getAllowedValues());
				p.setParameterValue(b.getParameterValue());
				p.setDataType(b.getDataType());
				p.setDescription(b.getDescription());
				p.setIsModifiable(b.isModifiable());
				p.setMinimumEngineVersion("" + b.getMinimumEngineVersion());
				p.setParameterName(b.getName());
				p.setSource(b.getSource());
				parameters.add(p);
			}
		}
		ret.setCacheNodeTypeSpecificParameters(specificParameters);
		ret.setParameters(parameters);
		return ret;
	}

	public static boolean updateParameter(final Session session,
			final CacheParameterGroupBean parameterGroup,
			final ParameterNameValue nv) {
		//
		// final ParameterBean parameter = parameterGroup.getParameter(nv
		// .getParameterName());
		// if (parameter == null) {
		// throw QueryFaults.InvalidParameterValue();
		// }
		//
		// if (!parameter.getIsModifiable()) {
		// final String message = "Attempt to modify read only parameter:"
		// + nv.getParameterName();
		// logger.debug(message);
		// throw QueryFaults.InvalidParameterValue();
		// }
		//
		// if (!parameter.getParameterValue().equalsIgnoreCase(
		// nv.getParameterValue())) {
		//
		// // String message = String.format("Setting parameter %s = %s",
		// // parameter.getParameterName(), nv.getParameterValue()) ;
		// // logger.debug(message) ;
		// parameter.setParameterValue(nv.getParameterValue());
		// session.save(parameter);
		// }

		return true;
	}

	@SuppressWarnings("unchecked")
	public static void updateParameters(final Session session,
			final long sourceGroupId, final List<ParameterNameValue> vals) {

		final Query q = session.createQuery("from ParameterBean where groupId="
				+ sourceGroupId);
		final List<ParameterBean> l = q.list();

		for (final ParameterBean pb : l) {
			for (final ParameterNameValue v : vals) {
				if (v.getParameterName().equals(pb.getName())) {
					if (v.getParameterValue() == null) {
						throw QueryFaults.InvalidParameterValue();
					}
					if (!pb.isModifiable()) {
						throw QueryFaults.InvalidParameterCombination(pb
								.getName() + " is a non-modifiable parameter");
					}
					pb.setParameterValue(v.getParameterValue());
					pb.setSource("user");
					session.save(pb);
					break;
				}
			}
		}
	}
}
