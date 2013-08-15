package com.msi.tough.utils.rds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.rds.model.CreateDBParameterGroupRequest;
import com.amazonaws.services.rds.model.DBParameterGroup;
import com.amazonaws.services.rds.model.DescribeDBParametersResult;
import com.amazonaws.services.rds.model.Parameter;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.BaseException;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbparameterGroup;
import com.msi.tough.model.rds.RdsParameter;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.Constants;
import com.msi.tough.utils.RDSQueryFaults;

public class ParameterGroupEntity {
	private static Logger logger = Appctx.getLogger(ParameterGroupEntity.class
			.getName());

	public static List<RdsParameter> copyAndSetDynamicParamGroup(
			final Session s, final RdsDbparameterGroup paramGroup,
			final List<RdsParameter> original, final long userId) {
		final List<RdsParameter> copy = new ArrayList<RdsParameter>();
		for (final RdsParameter param : original) {
			if (param.getApplyType().equals(RDSUtilities.PARM_APPTYPE_DYNAMIC)) {
				logger.debug("Trying to get " + param.getParameterName()
						+ " parameter.");
				final RdsParameter temp = getParameter(s,
						paramGroup.getDbparameterGroupName(),
						param.getParameterName(), userId);
				logger.debug("Target Parameter: " + temp.toString());
				logger.debug("Copying " + param.toString());
				if (param.getIsModifiable()) {
					temp.setAllowedValues(param.getAllowedValues());
					temp.setApplyType(param.getApplyType());
					temp.setDataType(param.getDataType());
					temp.setDescription(param.getDescription());
					temp.setIsModifiable(param.getIsModifiable());
					temp.setMinimumEngineVersion(param
							.getMinimumEngineVersion());
					temp.setParameterName(param.getParameterName());
					temp.setParameterValue(param.getParameterValue());
					temp.setSource(param.getSource());
					copy.add(temp);
					s.save(temp);
				}
			}
		}
		return copy;
	}

	public static List<RdsParameter> copyAndSetParamGroup(final Session s,
			final RdsDbparameterGroup paramGroup,
			final List<RdsParameter> original) {
		final List<RdsParameter> copy = new ArrayList<RdsParameter>();
		for (final RdsParameter param : original) {
			final RdsParameter temp = new RdsParameter(paramGroup);
			logger.debug("Target Parameter: " + temp.toString());
			logger.debug("Copying " + param.toString());
			temp.setAllowedValues(param.getAllowedValues());
			temp.setApplyType(param.getApplyType());
			temp.setDataType(param.getDataType());
			temp.setDescription(param.getDescription());
			temp.setIsModifiable(param.getIsModifiable());
			temp.setMinimumEngineVersion(param.getMinimumEngineVersion());
			temp.setParameterName(param.getParameterName());
			temp.setParameterValue(param.getParameterValue());
			temp.setSource(param.getSource());
			copy.add(temp);
			s.save(temp);
		}
		paramGroup.setParameters(copy);
		return copy;
	}

	public static List<RdsParameter> copyAndSetStaticParamGroup(
			final Session s, final RdsDbparameterGroup paramGroup,
			final List<RdsParameter> original, final long userId) {
		final List<RdsParameter> copy = new ArrayList<RdsParameter>();
		for (final RdsParameter param : original) {
			if (param.getApplyType().equals(RDSUtilities.PARM_APPTYPE_STATIC)) {
				final RdsParameter temp = getParameter(s,
						paramGroup.getDbparameterGroupName(),
						param.getParameterName(), userId);
				logger.debug("Target Parameter: " + temp.toString());
				logger.debug("Copying " + param.toString());
				if (param.getIsModifiable()) {
					temp.setAllowedValues(param.getAllowedValues());
					temp.setApplyType(param.getApplyType());
					temp.setDataType(param.getDataType());
					temp.setDescription(param.getDescription());
					temp.setIsModifiable(param.getIsModifiable());
					temp.setMinimumEngineVersion(param
							.getMinimumEngineVersion());
					temp.setParameterName(param.getParameterName());
					temp.setParameterValue(param.getParameterValue());
					temp.setSource(param.getSource());
					copy.add(temp);
					s.save(temp);
				}
			}
		}
		return copy;
	}

	@SuppressWarnings("unchecked")
	public static RdsParameter getParameter(final Session sess,
			final String paramGrpName, final String paramName, final long userId) {
		final String sql = "SELECT param FROM RdsParameter param JOIN param.rdsParamGroup paramGrp WHERE paramGrp.account.id="
				+ userId
				+ " AND param.parameterName=\'"
				+ paramName
				+ "\' AND paramGrp.dbparameterGroupName=\'"
				+ paramGrpName
				+ "\'";
		logger.info("selectInstancesByParameterGroup: Query is " + sql);
		final Query query = sess.createQuery(sql);

		final List<RdsParameter> list = query.list();
		if (list.size() == 0 || list.size() > 1) {
			logger.debug("There are " + list.size()
					+ " elements in the list returned.");
			return null;
		}
		return list.get(0);
	}

	/**************************************************************************
	 * This returns the single named ParameterGroup record but uses the same
	 * implementation as selectParameterGroups that returns an array of records.
	 * 
	 * @param sess
	 * 
	 * @param paramGrpName
	 * @param userID
	 * @return
	 * @throws BaseException
	 */
	public static RdsDbparameterGroup getParameterGroup(final Session sess,
			final String paramGrpName, final long acid) {
		logger.info("getParameterGroup: " + " account = " + acid
				+ " ParameterGroupName = " + paramGrpName);
		final List<RdsDbparameterGroup> result = selectDBParameterGroups(sess,
				paramGrpName, acid, "", 1);
		if (result == null) {
			// don't throw exception - return null.
			logger.debug("getParameterGroup: No ParamerGroup record "
					+ "found for user = " + acid + " ParameterGroupName = "
					+ paramGrpName);
			return null;
		} else {
			if (result.size() != 1) {
				logger.debug("getParameterGroup: Found " + result.size()
						+ " records");
				return null;
			} else {
				return result.get(0);
			}
		}
	}

	/**************************************************************************
	 * insertParameterGroup
	 * 
	 * @param createDBParameterGroup
	 * @param userID
	 * @throws BaseException
	 */
	public static RdsDbparameterGroup insertParameterGroup(final Session sess,
			final CreateDBParameterGroupRequest createDBParameterGroup,
			final AccountBean ac) {
		final String paramGrpFamily = createDBParameterGroup
				.getDBParameterGroupFamily();
		final String paramGrpName = createDBParameterGroup
				.getDBParameterGroupName();
		final String description = createDBParameterGroup.getDescription();

		logger.info("insertParameterGroup: for " + " account = " + ac.getId()
				+ " ParameterGroupName" + paramGrpName + " ParamterGroupFamily"
				+ paramGrpFamily + " Description " + description);

		// build DBParameterGroup record
		final RdsDbparameterGroup paramGroup = new RdsDbparameterGroup(ac,
				paramGrpName, paramGrpFamily, description);

		sess.save(paramGroup);

		// copy the default parameters from the DBParameterGroupFamily
		final AccountBean sac = AccountUtil.readAccount(sess, 1L);
		if (paramGrpFamily.toUpperCase().equals("MYSQL5.1")) {
			logger.debug("Inserting Parameters into " + paramGrpName
					+ " with MySQL5.1 family Parameters");
			final RdsDbparameterGroup parent = getParameterGroup(sess,
					"default.mysql5.1", sac.getId());
			logger.debug("There are " + parent.getParameters().size()
					+ " parameters to copy.");
			copyAndSetParamGroup(sess, paramGroup, parent.getParameters());
		} else if (paramGrpFamily.toUpperCase().equals("MYSQL5.5")) {
			logger.debug("Inserting Parameters into " + paramGrpName
					+ " with MySQL5.5 family Parameters");
			final RdsDbparameterGroup parent = getParameterGroup(sess,
					"default.mysql5.5", sac.getId());
			logger.debug("There are " + parent.getParameters().size()
					+ " parameters to copy.");
			copyAndSetParamGroup(sess, paramGroup, parent.getParameters());
		}

		logger.info("insertParameterGroup: Successfully inserted "
				+ "DBParameter Group");
		return paramGroup;
	}

	public static List<RdsParameter> modifyParamGroupWithPartialList(
			final Session s, final RdsDbparameterGroup paramGroup,
			final List<RdsParameter> partialList, final long userId) {
		final List<RdsParameter> copy = new ArrayList<RdsParameter>();
		for (final RdsParameter param : partialList) {
			final RdsParameter temp = getParameter(s,
					paramGroup.getDbparameterGroupName(),
					param.getParameterName(), userId);
			logger.debug("Target Parameter: " + temp.toString());
			logger.debug("Copying " + param.toString());
			temp.setSource(Constants.USER);
			temp.setParameterValue(param.getParameterValue());
			copy.add(temp);
			s.save(temp);
		}
		return copy;
	}

	public static List<RdsParameter> resetParamGroupWithPartialList(
			final Session s, final RdsDbparameterGroup paramGroup,
			final List<RdsParameter> partialList, final long userId) {
		final List<RdsParameter> copy = new ArrayList<RdsParameter>();
		for (final RdsParameter param : partialList) {
			final RdsParameter temp = getParameter(s,
					paramGroup.getDbparameterGroupName(),
					param.getParameterName(), userId);
			logger.debug("Target Parameter: " + temp.toString());
			logger.debug("Copying " + param.toString());
			final RdsParameter default_param = getParameter(s, "default."
					+ paramGroup.getDbparameterGroupFamily().toLowerCase(),
					param.getParameterName(), 1);
			temp.setSource(default_param.getSource());
			temp.setParameterValue(param.getParameterValue());
			copy.add(temp);
			s.save(temp);
		}
		return copy;
	}

	/**************************************************************************
	 * Select list of DBParameterGroups either for 1. Named DBParameterGroup 2.
	 * All DBParameterGroups for that user
	 * 
	 * @param paramGrpName
	 * @param userID
	 * @param marker
	 * @param maxRecords
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings("unchecked")
	public static List<RdsDbparameterGroup> selectDBParameterGroups(
			final Session sess, final String paramGrpName, final long acid,
			final String marker, final Integer maxRecords) {

		String markerSql = "";
		String groupSql = "";
		if (marker != null && !"".equals(marker)) {
			markerSql = " and dbparameterGroupName > '" + marker + "'";
		}

		if (paramGrpName != null && !"".equals(paramGrpName)) {
			groupSql = " and dbparameterGroupName = '" + paramGrpName + "'";
		}

		final String sql = "from RdsDbparameterGroup where account= " + acid
				+ markerSql + groupSql + " order by dbparameterGroupName";

		logger.info("selectDBParameterGroups: SQL Query is " + sql);
		final Query query = sess.createQuery(sql);
		query.setFirstResult(0);
		if (maxRecords != null) {
			query.setMaxResults(maxRecords);
		}
		return query.list();
	}

	/**************************************************************************
	 * Select all of the DBParameters associated with the given DBParameterGroup
	 * DBParameterGroupName is required If source is provided only return
	 * DBParameters for that source (user | engine | system)
	 * 
	 * @param userID
	 * @param grpName
	 * @param source
	 * @param marker
	 * @param maxRecords
	 * @return
	 * @throws BaseException
	 */

	@SuppressWarnings("unchecked")
	public static DescribeDBParametersResult selectParameters(
			final Session sess, final AccountBean ac, final String grpName,
			final String source, final String marker, final int maxRecords) {
		final DescribeDBParametersResult resp = new DescribeDBParametersResult();

		logger.info("selectParameters:" + " account = " + ac.getId()
				+ " DBParameterGroupName = " + grpName + " Source = " + source
				+ " marker = " + marker + " maxRecord = " + maxRecords);

		// confirm that the DBParameterGroup exists
		final RdsDbparameterGroup paramGrp = getParameterGroup(sess, grpName,
				ac.getId());
		if (paramGrp == null) {
			logger.error("selectParameters: DBParameterGroupNotFound: ParameterGroup"
					+ " not found for" + grpName);
			throw RDSQueryFaults.DBParameterGroupNotFound();
		} else {
			String markerSql = "";
			String selSql = "";
			String grpSql = "";

			if (marker != null && !marker.equals("")) {
				markerSql = " AND param.id > '" + marker + "'";
			}

			if (grpName != null && !grpName.equals("")) {
				grpSql = " AND pgroup.dbparameterGroupName = '" + grpName + "'";
			}

			if (source != null && !source.equals("")) {
				selSql = " AND param.source = '" + source + "'";
			}

			final String sql = "SELECT param FROM RdsParameter param INNER JOIN param.rdsParamGroup pgroup WHERE pgroup.account.id = "
					+ ac.getId()
					+ grpSql
					+ selSql
					+ markerSql
					+ " ORDER BY param.id";

			logger.info("selectParameters: Query is " + sql);
			final Query query = sess.createQuery(sql);
			if (maxRecords != 0) {
				query.setFirstResult(0);
				query.setMaxResults(maxRecords + 1);
			}

			final List<RdsParameter> parameters = query.list();
			logger.debug(parameters.size()
					+ " parameters are returned. If you see an additional parameter, it is to verify whether marker should be returned or not.");

			final Collection<Parameter> paramsConverted = new LinkedList<Parameter>();
			int lim = parameters.size();
			if (lim > maxRecords) {
				--lim;
				final String token = "" + parameters.get(lim - 1).getId();
				resp.setMarker(token);
			}
			for (int i = 0; i < lim; ++i) {
				final RdsParameter parameter = parameters.get(i);
				final Parameter paramConverted = new Parameter();
				paramConverted.setAllowedValues(parameter.getAllowedValues());
				paramConverted.setApplyMethod(parameter.getApplyMethod());
				paramConverted.setApplyType(parameter.getApplyType());
				paramConverted.setDataType(parameter.getDataType());
				paramConverted.setDescription(parameter.getDescription());
				paramConverted.setIsModifiable(parameter.getIsModifiable());
				paramConverted.setMinimumEngineVersion(parameter
						.getMinimumEngineVersion());
				paramConverted.setParameterName(parameter.getParameterName());
				paramConverted.setParameterValue(parameter.getParameterValue());
				paramConverted.setSource(parameter.getSource());
				paramsConverted.add(paramConverted);
			}
			resp.setParameters(paramsConverted);
		}
		return resp;
	}

	public static DBParameterGroup toDBParameterGroup(
			final RdsDbparameterGroup p) {
		final DBParameterGroup g = new DBParameterGroup();
		g.setDBParameterGroupFamily(p.getDbparameterGroupFamily());
		g.setDBParameterGroupName(p.getDbparameterGroupName());
		g.setDescription(p.getDescription());
		return g;
	}

	public static void createDefaultDBParameterGroup(final Session sess,
			final AccountBean ac, String dbParamGrpName) {
		List<RdsDbparameterGroup> paramGrps = ParameterGroupEntity.selectDBParameterGroups(sess, dbParamGrpName, 1, null, 1);
		if(paramGrps != null && paramGrps.size() != 0){
			final RdsDbparameterGroup defaultSystemGroup = paramGrps.get(0);
			final RdsDbparameterGroup newDefaultParamGroup = new RdsDbparameterGroup();
			newDefaultParamGroup.setAccount(ac);
			newDefaultParamGroup.setDbparameterGroupFamily(defaultSystemGroup.getDbparameterGroupFamily());
			newDefaultParamGroup.setDbparameterGroupName(defaultSystemGroup.getDbparameterGroupName());
			newDefaultParamGroup.setDescription(defaultSystemGroup.getDescription());
			
			HibernateUtil.withSession(
					new HibernateUtil.Operation<Object>() {
						@Override
						public Object ex(final Session session,
								final Object... args) throws Exception {
							sess.save(newDefaultParamGroup);
							return null;
						}
					});
			
			HibernateUtil.withSession(
					new HibernateUtil.Operation<Object>() {
						@Override
						public Object ex(final Session session,
								final Object... args) throws Exception {
							List<RdsParameter> newParams = new LinkedList<RdsParameter>();
							for(RdsParameter p : defaultSystemGroup.getParameters()){
								RdsParameter e = new RdsParameter();
								e.setAllowedValues(p.getAllowedValues());
								e.setApplyMethod(p.getApplyMethod());
								e.setApplyType(p.getApplyType());
								e.setDataType(p.getDataType());
								e.setDescription(p.getDescription());
								e.setIsModifiable(p.getIsModifiable());
								e.setMinimumEngineVersion(p.getMinimumEngineVersion());
								e.setParameterName(p.getParameterName());
								e.setParameterValue(p.getParameterValue());
								e.setSource(p.getSource());
								e.setRdsParamGroup(newDefaultParamGroup);
								sess.save(e);
								newParams.add(e);
							}
							newDefaultParamGroup.setParameters(newParams);
							sess.save(newDefaultParamGroup);
							return null;
						}
					});	
		}
	}
}
