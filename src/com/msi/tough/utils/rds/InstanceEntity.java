package com.msi.tough.utils.rds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBParameterGroupStatus;
import com.amazonaws.services.rds.model.DBSecurityGroupMembership;
import com.amazonaws.services.rds.model.Endpoint;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.BaseException;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.engine.core.Template;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsDbparameterGroup;
import com.msi.tough.model.rds.RdsDbsecurityGroup;
import com.msi.tough.model.rds.RdsParameter;
import com.msi.tough.utils.ConfigurationUtil;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.RDSUtil;

public class InstanceEntity {
	private static Logger logger = Appctx.getLogger(InstanceEntity.class
			.getName());

	private static String thisClass = "com.msi.tough.rds.Instance";

	public static void deleteInstance(final Session sess, final String instId,
			final AccountBean ab) {
		final RdsDbinstance deleteTarget = selectDBInstance(sess, instId,
				ab.getId());
		sess.delete(deleteTarget);
	}

	/**************************************************************************
	 * Inserts records into the following tables rds_dbinstance
	 * rds_security_group_membership rds_dbparameter_group_status This method
	 * also checks quota's
	 * 
	 * @param createRec
	 * @param userID
	 * @param address
	 * @param readReplica
	 * @throws BaseException
	 */
	public static RdsDbinstance insertDBInstance(final Session sess,
			final CreateDBInstanceRequest createRec, final AccountBean ac) {
		final Logger logger = LoggerFactory.getLogger(thisClass);
		final String instID = createRec.getDBInstanceIdentifier();
		final RdsDbinstance dbrec = RDSUtil.getInstance(sess, instID,
				ac.getId());
		if (dbrec != null) {
			throw RDSQueryFaults.DBInstanceAlreadyExists();
		}

		final String paramGrpName = createRec.getDBParameterGroupName();

		// check that ParameterGroup exists
		RdsDbparameterGroup pgRec = ParameterGroupEntity
				.getParameterGroup(sess, paramGrpName, ac.getId());
		if (pgRec == null) {
			List<RdsDbparameterGroup> paramGrps = ParameterGroupEntity.selectDBParameterGroups(sess, paramGrpName, 1, null, null);
			if(paramGrps == null || paramGrps.size() != 1){
				throw RDSQueryFaults.DBParameterGroupNotFound();
			}
			else{
				// default parameter group is being used; copy the group and parameters for this user
				ParameterGroupEntity.createDefaultDBParameterGroup(sess, ac, paramGrpName);
				pgRec = ParameterGroupEntity
						.getParameterGroup(sess, paramGrpName, ac.getId());
			}
		}

		/*
		 * DBSecurityGroup ************************************************* The
		 * create request contains a list of DBSecurityGroupNames we need to
		 * process each one
		 */
		final RdsDbinstance newInst = new RdsDbinstance(ac, pgRec);
		if (createRec.getDBSecurityGroups() == null
				|| createRec.getDBSecurityGroups().size() == 0) {
			// this line is never reached because "default" is set by default as DBSecurityGroup
		} else {
			// Process the array of SecurityGroup Names
			final String[] secGrpName = createRec.getDBSecurityGroups()
					.toArray(new String[0]);
			final int len = secGrpName.length;
			final List<RdsDbsecurityGroup> groups = newInst.getSecurityGroups();

			for (int i = 0; i < len; i++) {
				// check that the security group exists
				final RdsDbsecurityGroup sgmemRec = SecurityGroupEntity
						.getSecurityGroup(sess, secGrpName[i], ac.getId());
				if (sgmemRec == null && !secGrpName[i].equals("default")) {
					throw RDSQueryFaults.DBSecurityGroupNotFound();
				}
				groups.add(sgmemRec);
			}
		}
		newInst.setInstanceCreateTime(new Date());
		newInst.setLatestRestorableTime(new Date());
		newInst.setAllocatedStorage(createRec.getAllocatedStorage().intValue());
		newInst.setAutoMinorVersionUpgrade(createRec
				.getAutoMinorVersionUpgrade());
		newInst.setAvailabilityZone(createRec.getAvailabilityZone());
		newInst.setBackupRetentionPeriod(createRec.getBackupRetentionPeriod()
				.intValue());
		final String dbInstClass = createRec.getDBInstanceClass();
		final String instClass = (String)ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { dbInstClass, createRec.getAvailabilityZone() }));
		if (instClass == null) {
			throw RDSQueryFaults.InvalidParameterValue(dbInstClass
					+ " is not one of the valid DBInstance classes.");
		}
		newInst.setDbinstanceClass(dbInstClass);
		newInst.setDbinstanceId(createRec.getDBInstanceIdentifier());
		newInst.setDbName(createRec.getDBName());
		newInst.setDbParameterGroup(paramGrpName);
		newInst.setEngine(createRec.getEngine());
		newInst.setEngineVersion(createRec.getEngineVersion());
		newInst.setMasterUsername(createRec.getMasterUsername());
		newInst.setMasterUserPassword(createRec.getMasterUserPassword());
		newInst.setMultiAz(createRec.getMultiAZ());
		newInst.setPort(createRec.getPort().intValue());
		newInst.setPreferredBackupWindow(createRec.getPreferredBackupWindow());
		newInst.setPreferredMaintenanceWindow(createRec
				.getPreferredMaintenanceWindow());
		newInst.setLicenseModel(createRec.getLicenseModel());
		newInst.setDbinstanceStatus("creating");
		final List<String> DBSecurityGroupNames = createRec
				.getDBSecurityGroups();
		final List<RdsDbsecurityGroup> dbsecgrps = new LinkedList<RdsDbsecurityGroup>();
		for (final String dbSecGrpname : DBSecurityGroupNames) {
			final RdsDbsecurityGroup temp = SecurityGroupEntity
					.getSecurityGroup(sess, dbSecGrpname, ac.getId());
			if (temp == null) {
				throw RDSQueryFaults.DBSecurityGroupNotFound();
			}
			dbsecgrps.add(temp);
		}
		newInst.setSecurityGroups(dbsecgrps);

		// save / create the instance record
		sess.save(newInst);

		logger.info("insertDBInstance: Successfully Inserted Instance "
				+ "into the Database");
		return newInst;
	}


	public static Collection<RdsDbinstance> selectBySecurityGroup(
			final Session sess, final String dbSecGrpName, final AccountBean ac) {
		return InstanceEntity.selectBySecurityGroup(sess, dbSecGrpName, ac, 0);
	}

	/**************************************************************************
	 * Select all DBSecurityGroupMembership Records for the given DBInstance or
	 * for a DBSecurityGroup
	 * 
	 * @param sess
	 *            Hibernate Session
	 * @param paramGroupName
	 *            String name of group to use in select.
	 * @param limit
	 *            int max records to retrieve, 0 or less means all.
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings("unchecked")
	public static Collection<RdsDbinstance> selectBySecurityGroup(
			final Session sess, final String dbSecGrpName,
			final AccountBean ac, final int limit) {
		// String pgsql = " and securityGroups='" + dbSecGrpName + "'";
		final String sql = "SELECT inst FROM RdsDbinstance inst JOIN inst.securityGroups sec"
				+ " WHERE inst.account.id = "
				+ ac.getId()
				+ " AND sec.dbsecurityGroupName = '"
				+ dbSecGrpName
				+ "'"
				+ " ORDER BY inst.dbinstanceId";

		logger.info("selectInstancesByParameterGroup: Query is " + sql);
		final Query query = sess.createQuery(sql);

		if (limit != 0) {
			query.setFirstResult(0);
			query.setMaxResults(limit);
		}

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public static RdsDbinstance selectDBInstance(final Session sess,
			final String instanceID, final long userID) {
		RdsDbinstance result = null;
		final String sql = "from RdsDbinstance where account_id=" + userID
				+ " and db_instance_identifier='" + instanceID + "'";
		final Query query = sess.createQuery(sql);
		final List<RdsDbinstance> l = query.list();
		if (l == null || l.size() == 0 || l.size() > 1) {
			return null;
		}
		result = l.get(0);
		return result;
	}

	/**************************************************************************
	 * Select all RDSDbinstance Records for the given DBParameterGroup name
	 * 
	 * @param sess
	 *            Hibernate Session
	 * @param paramGroupName
	 *            String name of group to use in select.
	 * @param limit
	 *            int max records to retrieve, 0 or less means all.
	 * @return
	 * @throws BaseException
	 */
	public static Collection<RdsDbinstance> selectDBInstancesByParameterGroup(
			final Session sess, final String paramGroupName, final int limit,
			final AccountBean ac) {
		final List<RdsDbinstance> l = RDSUtil.selectInstancesByParameterGroup(
				sess, paramGroupName, ac, limit >= 0 ? limit : 0);
		return l;
	}

	/**************************************************************************
	 * Select all DBParameterGrpStatus Records for the given DBInstance or for a
	 * DBSecurityGroup
	 * 
	 * @param instanceID
	 * @param userID
	 * @return
	 * @throws BaseException
	 */
	public static Collection<DBParameterGroupStatus> selectDBParamGrpStatus(
			final Session sess, final String instanceID, final long acid) {
		final List<RdsDbinstance> l = RDSUtil.selectInstances(sess, instanceID,
				acid, null, 0);
		final RdsDbinstance b = l.get(0);
		final List<DBParameterGroupStatus> resp = new ArrayList<DBParameterGroupStatus>();
		final DBParameterGroupStatus statusRec = new DBParameterGroupStatus();
		statusRec.setDBParameterGroupName(b.getDbParameterGroup());
		statusRec.setParameterApplyStatus(b.getDbParameterGroupStatus());
		resp.add(statusRec);
		return resp;
	}

	@SuppressWarnings("unchecked")
	public static List<RdsDbinstance> selectInstances(final Session sess,
			final String instanceID, final AccountBean ac, final String marker) {
		String markerSql = "";
		String instanceSql = "";
		if (instanceID != null && instanceID != "") {
			instanceSql = " and dbinstanceId = '" + instanceID + "'";
		} else {
			if (marker != null && marker != "") {
				markerSql = " and dbinstanceId > '" + marker + "'";
			}
		}
		final String sql = "from RdsDbinstance where account = " + ac.getId()
				+ instanceSql + markerSql + " order by dbinstanceId";
		logger.info("selectInstance: Query is " + sql);
		final Query query = sess.createQuery(sql);
		return query.list();
	}

	/**************************************************************************
	 * selectDBInstances Return either the named DBInstance or all DBInstances
	 * for that user DBInstance is made up of information from the following
	 * tables DBInstance DBSecurityGroup DBParameterGroup PendingModifiedValues
	 * The last three groups are optional and loaded using the getRelatedRecords
	 * method (split out to make this method more maintainable).
	 * 
	 * @param instanceID
	 * @param userID
	 * @param marker
	 * @param maxRecords
	 * @return list of DBInstance records
	 * @throws BaseException
	 */
	@SuppressWarnings("unchecked")
	public static List<RdsDbinstance> selectInstances(final Session sess,
			final String instanceID, final AccountBean ac, final String marker,
			final int maxRecords) {
		String markerSql = "";
		String instanceSql = "";
		if (instanceID != null && instanceID != "") {
			instanceSql = " and dbinstanceId = '" + instanceID + "'";
		} else {
			if (marker != null && marker != "") {
				markerSql = " and dbinstanceId > '" + marker + "'";
			}
		}
		final String sql = "from RdsDbinstance where account = " + ac.getId()
				+ instanceSql + markerSql + " order by dbinstanceId";
		logger.info("selectInstance: Query is " + sql);
		final Query query = sess.createQuery(sql);
		if (maxRecords != 0) {
			query.setFirstResult(0);
			query.setMaxResults(maxRecords);
		}
		return query.list();
	}

	/************************************************************************
	 * Select the list of DBInstanceID's for all of those Instances that are
	 * read replica's of this Master DBInstance SQL is Select DBInstanceID from
	 * FROM DBInstance Where ReadReplicaSourceDBInstanceIdentifier =
	 * <instanceID>
	 * 
	 * @param instanceID
	 * @param userID
	 * @return list of ReadReplica (array of Strings - ReadReplicaInstanceID)
	 * @throws BaseException
	 */
	@SuppressWarnings({ "unchecked" })
	public static Collection<String> selectReadReplicas(final Session sess,
			final String instanceID, final long userID) {
		List<String> resp = null;
		final String sql = "from RdsDbinstance where userId=" + userID
				+ " and sourceDbinstanceId = '" + instanceID + "'";
		final Query query = sess.createQuery(sql);
		final List<RdsDbinstance> l = query.list();
		resp = new ArrayList<String>();
		for (final RdsDbinstance b : l) {
			resp.add(b.getDbinstanceId());
		}
		return resp;
	}

	/**************************************************************************
	 * select all SecurityGroupMemebership Records for this DBInstance or
	 * SecurityGroupName
	 * 
	 * @param instanceID
	 * @param userID
	 * @return
	 * @throws BaseException
	 */
	public static Collection<DBSecurityGroupMembership> selectSecGrpMembership(
			final Session sess, final RdsDbinstance b) {
		final List<DBSecurityGroupMembership> resp = new ArrayList<DBSecurityGroupMembership>();
		final List<RdsDbsecurityGroup> groups = b.getSecurityGroups();
		if (b.getSecurityGroups() != null) {
			for (final RdsDbsecurityGroup sg : groups) {
				final DBSecurityGroupMembership em = new DBSecurityGroupMembership();
				em.setDBSecurityGroupName(sg.getDbsecurityGroupName());
				em.setStatus("active");
				resp.add(em);
			}
		}
		return resp;
	}

	public static DBInstance toDBInstance(final RdsDbinstance b,
			final AccountBean ac) {
		final DBInstance instRec = new DBInstance();
		instRec.setDBInstanceIdentifier(b.getDbinstanceId());
		instRec.setReadReplicaSourceDBInstanceIdentifier(b
				.getSourceDbinstanceId());
		instRec.setReadReplicaDBInstanceIdentifiers(b.getReplicas());
		instRec.setDBInstanceClass(b.getDbinstanceClass());
		instRec.setAllocatedStorage(Integer.valueOf(b.getAllocatedStorage()));
		instRec.setInstanceCreateTime(b.getInstanceCreateTime());
		instRec.setDBInstanceStatus(b.getDbinstanceStatus());
		instRec.setEngine(b.getEngine());
		instRec.setEngineVersion(b.getEngineVersion());
		instRec.setAvailabilityZone(b.getAvailabilityZone());
		instRec.setMultiAZ(b.getMultiAz());
		instRec.setMasterUsername(b.getMasterUsername());
		instRec.setDBName(b.getDbName());
		instRec.setAutoMinorVersionUpgrade(b.getAutoMinorVersionUpgrade());
		instRec.setBackupRetentionPeriod(Integer.valueOf(b
				.getBackupRetentionPeriod()));
		instRec.setLatestRestorableTime(b.getLatestRestorableTime());
		instRec.setPreferredBackupWindow(b.getPreferredBackupWindow());
		instRec.setPreferredMaintenanceWindow(b.getPreferredMaintenanceWindow());
		instRec.setLicenseModel(b.getLicenseModel());
		final Endpoint endpoint = new Endpoint();
		endpoint.setAddress(b.getAddress());
		endpoint.setPort(Integer.valueOf(b.getPort()));
		instRec.setEndpoint(endpoint);
		String status = b.getDbinstanceStatus();
		if(status.equals("restoring")){
			status = "creating";
		}
		instRec.setDBInstanceStatus(status);
		final String dbparamName = b.getDbParameterGroup();
		final Collection<DBParameterGroupStatus> dBParameterGroups = new LinkedList<DBParameterGroupStatus>();
		final DBParameterGroupStatus dbparamStatus = new DBParameterGroupStatus();
		dbparamStatus.setDBParameterGroupName(dbparamName);
		if (b.getPendingRebootParameters() == null
				|| b.getPendingRebootParameters().size() == 0) {
			dbparamStatus.setParameterApplyStatus(RDSUtilities.STATUS_IN_SYNC);
		} else {
			dbparamStatus
					.setParameterApplyStatus(RDSUtilities.STATUS_PENDING_REBOOT);
		}
		dBParameterGroups.add(dbparamStatus);
		instRec.setDBParameterGroups(dBParameterGroups);

		final Collection<DBSecurityGroupMembership> dBSecurityGroups = new LinkedList<DBSecurityGroupMembership>();
		for (final RdsDbsecurityGroup secGrp : b.getSecurityGroups()) {
			final DBSecurityGroupMembership membership = new DBSecurityGroupMembership();
			membership.setDBSecurityGroupName(secGrp.getDbsecurityGroupName());
			membership.setStatus(secGrp.getStatus());
			dBSecurityGroups.add(membership);
		}
		instRec.setDBSecurityGroups(dBSecurityGroups);
		return instRec;
	}
}