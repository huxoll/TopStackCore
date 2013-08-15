package com.msi.tough.engine.aws.elasticache;

public class ElasticacheDbUtils {
	//
	// final static Logger logger = LoggerFactory
	// .getLogger(ElasticacheDbUtils.class);
	//
	// // Cache Cluster
	//
	// public static void copyNodeSpecificParameters(final Session session,
	// final CacheParameterGroupBean source,
	// final CacheParameterGroupBean dest) {
	//
	// if (source == null || dest == null) {
	// logger.debug("Cannot copy CreateCacheParameterGroup Node Specific settings");
	// return;
	// }
	//
	// for (final CacheNodeTypeSpecificParameterBean nsp : source
	// .getNodeSpecificParameters()) {
	// // Create the new Parameter, but not it's list of node specific
	// // values
	// final CacheNodeTypeSpecificParameterBean p = new
	// CacheNodeTypeSpecificParameterBean(
	// dest, nsp);
	//
	// session.save(p);
	//
	// copyNodeSpecificValues(session, nsp, p);
	// }
	// }
	//
	// private static void copyNodeSpecificValues(final Session session,
	// final CacheNodeTypeSpecificParameterBean source,
	// final CacheNodeTypeSpecificParameterBean dest) {
	//
	// if (source == null || dest == null) {
	// logger.debug("Cannot copy Node Specific Values settings");
	// return;
	// }
	//
	// for (final CacheNodeTypeSpecificValueBean v : source
	// .getSpecificValues()) {
	// final CacheNodeTypeSpecificValueBean newValue = new
	// CacheNodeTypeSpecificValueBean(
	// dest, v.getCacheNodeType(), v.getParameterValue());
	// session.save(newValue);
	// dest.getSpecificValues().add(newValue);
	// }
	// }
	//
	// public static CacheClusterBean getCacheCluster(final Session session,
	// final AccountBean account, final String cacheClusterId) {
	// return (CacheClusterBean) session
	// .createCriteria(CacheClusterBean.class)
	// .add(Restrictions.eq("account", account))
	// .add(Restrictions.eq("name", cacheClusterId)).uniqueResult();
	// }
	//
	// // Parameter Groups
	//
	// public static CacheNodeBean getCacheNode(final Session session,
	// final CacheClusterBean cacheCluster, final String name) {
	// final CacheNodeBean cacheNode = (CacheNodeBean) session
	// .createCriteria(CacheNodeBean.class)
	// .add(Restrictions.eq("cacheCluster", cacheCluster))
	// .add(Restrictions.eq("address", name)).uniqueResult();
	//
	// return cacheNode;
	// }
	//
	// public static CacheNodeTypeBean getCacheNodeType(final Session session,
	// final String nodeType) {
	// final CacheNodeTypeBean cacheNodeType = (CacheNodeTypeBean) session
	// .createCriteria(CacheNodeTypeBean.class)
	// .add(Restrictions.eq("type", nodeType)).uniqueResult();
	//
	// return cacheNodeType;
	// }
	//
	// public static SecurityGroupMembershipBean getDefaultSecurityGroup(
	// final Session session, final AccountBean account) {
	// return getSecurityGroup(session, account, "default", true);
	// }
	//
	// public static EngineVersionBean getEngineVersion(final Session session,
	// final EngineBean engine, final String engineVersion) {
	// final EngineVersionBean ev = (EngineVersionBean) session
	// .createCriteria(EngineVersionBean.class)
	// .add(Restrictions.eq("engine", engine))
	// .add(Restrictions.eq("version", engineVersion)).uniqueResult();
	//
	// return ev;
	// }
	//
	// // Example of restricting based on a parent table using a Criteria Query
	// public static EngineVersionBean getEngineVersion(final Session session,
	// final String engine, final String engineVersion) {
	//
	// EngineVersionBean evb = null;
	//
	// final Criteria criteria = session
	// .createCriteria(EngineVersionBean.class);
	//
	// if (StringUtils.isNullOrEmpty(engineVersion)) {
	// criteria.add(Restrictions.eq("isDefault", true));
	// } else {
	// criteria.add(Restrictions.eq("version", engineVersion));
	// }
	//
	// // Adding the parent table check before the base failed
	// criteria.createCriteria("engine", "e").add(
	// Restrictions.eq("e.name", engine));
	//
	// @SuppressWarnings("unchecked")
	// final List<EngineVersionBean> stuff = criteria.list();
	//
	// evb = (EngineVersionBean) criteria.uniqueResult();
	//
	// // for( EngineVersionBean b : stuff)
	// // {
	// // logger.debug(b.getEngineVersion());
	// // }
	//
	// return evb;
	// }
	//
	// public static List<CacheNodeTypeSpecificParameterBean>
	// getNodeSpecificParameters(
	// final Session session,
	// final CacheParameterGroupBean parameterGroup, final String source) {
	//
	// final Criteria nodeSpecificParameterCriteria = session.createCriteria(
	// CacheNodeTypeSpecificParameterBean.class).add(
	// Restrictions.eq("parameterGroup", parameterGroup));
	//
	// if (!StringUtils.isNullOrEmpty(source)) {
	// nodeSpecificParameterCriteria
	// .add(Restrictions.eq("source", source));
	// }
	//
	// @SuppressWarnings({ "unchecked" })
	// final List<CacheNodeTypeSpecificParameterBean> nodeSpecificParameters =
	// nodeSpecificParameterCriteria
	// .list();
	//
	// return nodeSpecificParameters;
	// }
	//
	// public static ParameterBean getParameter(final Session session,
	// final CacheParameterGroupBean parameterGroup,
	// final String parameterName) {
	// final ParameterBean parameter = (ParameterBean) session
	// .createCriteria(ParameterBean.class)
	// .add(Restrictions.eq("parameterGroup", parameterGroup))
	// .add(Restrictions.eq("parameterName", parameterName))
	// .uniqueResult();
	// return parameter;
	// }
	//
	// // Parameter / Node Specific Parameter
	//
	// public static CacheParameterGroupBean getParameterGroup(
	// final Session session, final AccountBean account,
	// final String parameterGroupName) {
	// CacheParameterGroupBean cpg = null;
	//
	// // cpg =
	// //
	// (CacheParameterGroupBean)session.createCriteria(CacheParameterGroupBean.class)
	// // .add(Restrictions.or(Restrictions.eq("account", account),
	// // Restrictions.isNull("account")))
	// // .add(Restrictions.eq("name", parameterGroupName))
	// // .uniqueResult();
	// cpg = (CacheParameterGroupBean) session
	// .createCriteria(CacheParameterGroupBean.class)
	// .add(Restrictions.eq("account", account))
	// .add(Restrictions.eq("name", parameterGroupName))
	// .uniqueResult();
	//
	// return cpg;
	// }
	//
	// /**
	// * Get the Parameter Group for this Account/Name or default from Engine
	// *
	// * @param session
	// * @param account
	// * @param parameterGroupName
	// * @param engineVersion
	// * @return CacheParameterGroupBean or null
	// */
	// public static CacheParameterGroupBean getParameterGroup(
	// final Session session, final AccountBean account,
	// final String parameterGroupName,
	// final EngineVersionBean engineVersion) {
	// CacheParameterGroupBean cpg = null;
	//
	// if (StringUtils.isNullOrEmpty(parameterGroupName)) {
	// return engineVersion.getEngine().getDefaultCacheParameterGroup();
	// }
	//
	// // cpg =
	// //
	// (CacheParameterGroupBean)session.createCriteria(CacheParameterGroupBean.class)
	// // .add(Restrictions.eq("account", account))
	// // .add(Restrictions.eq("name", parameterGroupName))
	// // .uniqueResult();
	// cpg = getParameterGroup(session, account, parameterGroupName);
	//
	// return cpg;
	// }
	//
	// // Cache Node Type
	//
	// public static List<ParameterBean> getParameters(final Session session,
	// final CacheParameterGroupBean parameterGroup, final String source) {
	//
	// final Criteria parameterCriteria = session.createCriteria(
	// ParameterBean.class).add(
	// Restrictions.eq("parameterGroup", parameterGroup));
	// if (!StringUtils.isNullOrEmpty(source)) {
	// parameterCriteria.add(Restrictions.eq("source", source));
	// }
	// @SuppressWarnings({ "unchecked" })
	// final List<ParameterBean> parameters = parameterCriteria.list();
	//
	// return parameters;
	// }
	//
	// // Cache Node
	//
	// public static SecurityGroupMembershipBean getSecurityGroup(
	// final Session session, final AccountBean account,
	// final String name, final Boolean isDefault) {
	// final SecurityGroupMembershipBean securityGroup = isDefault ?
	// (SecurityGroupMembershipBean) session
	// .createCriteria(SecurityGroupMembershipBean.class)
	// .add(Restrictions.isNull("account"))
	// .add(Restrictions.eq("groupName", "default")).uniqueResult()
	// : (SecurityGroupMembershipBean) session
	// .createCriteria(SecurityGroupMembershipBean.class)
	// .add(Restrictions.eq("account", account))
	// .add(Restrictions.eq("groupName", name)).uniqueResult();
	//
	// return securityGroup;
	// }
	//
	// // Engine Version
	//
	// public static List<SecurityGroupMembershipBean> getSecurityGroups(
	// final Session session, final AccountBean account,
	// final List<String> securityGroupNames) {
	// final List<SecurityGroupMembershipBean> securityGroups = new
	// ArrayList<SecurityGroupMembershipBean>();
	//
	// Boolean defaultAdded = false;
	// for (final String securityGroupName : securityGroupNames) {
	// if (securityGroupName.equalsIgnoreCase("default")) {
	// defaultAdded = true;
	//
	// final SecurityGroupMembershipBean securityGroup =
	// getDefaultSecurityGroup(
	// session, account);
	//
	// securityGroups.add(securityGroup);
	// } else {
	// final SecurityGroupMembershipBean securityGroup = getSecurityGroup(
	// session, account, securityGroupName, false);
	// if (securityGroup != null) {
	// securityGroups.add(securityGroup);
	// }
	// }
	// }
	//
	// if (defaultAdded == false && securityGroups.size() == 0) {
	// securityGroups.add(getDefaultSecurityGroup(session, account));
	// }
	//
	// return securityGroups;
	// }
	//
	// public static void setParameterGroupStatus(final Session session,
	// final CacheParameterGroupBean parameterGroup,
	// final CacheParameterGroupStatus status,
	// final List<CacheNodeToRebootBean> nodes) {
	//
	// if (!parameterGroup.hasStatus()) {
	// final CacheParameterGroupStatusBean newStatus = new
	// CacheParameterGroupStatusBean(
	// parameterGroup, status, nodes);
	// session.save(newStatus);
	// parameterGroup.setStatus(newStatus);
	// } else {
	// final CacheParameterGroupStatusBean oldStatus = parameterGroup
	// .getStatus();
	// oldStatus.setStatus(CacheParameterGroupStatus.modifying);
	// oldStatus.setNodes(nodes);
	// session.update(oldStatus);
	// }
	// }

}
