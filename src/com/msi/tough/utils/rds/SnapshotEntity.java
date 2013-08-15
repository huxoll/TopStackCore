package com.msi.tough.utils.rds;

/* 
 * This class persists the DBSnapshot Entity to the RDS private
 * MySQL database using Hibernate
 * 
 * void         insertSnapshot(String snapshotID, String userID, DBInstance instRec) 
 * void         deleteSnapshot(String snapshotID, String userID) 
 * DBSnapshots  selectSnapshot(String userID, String snapshotID, String instID, 
 *                             String marker, int maxRecords)    
 * DBSnapshot   getSnapshot(String userID, String snapshotID) 
 * void         insertRestoreInstance(DBInstance, instID, userID)
 *
 */

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.rds.model.DBSnapshot;
import com.msi.tough.core.BaseException;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsSnapshot;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.RDSUtil;

public class SnapshotEntity {
	private static String thisClass = "com.msi.tough.rds.dd.snapshotentity";
	final static Logger logger = LoggerFactory.getLogger(thisClass);
	
	/**************************************************************************
	 * Build validate and insert the new DBInstance record
	 * 
	 * @param restoreRec
	 * @param userID
	 * @throws BaseException
	 */
	// public static void insertRestoredInstance(Session sess, DBInstance
	// instRec,
	// String instID, String userID) throws BaseException {
	//
	// Logger logger = LoggerFactory.getLogger(thisClass);
	// String msg = "";
	// try {
	// // build and set the primary key record
	// RdsDbinstanceId iID = new RdsDbinstanceId();
	// iID.setDbinstanceId(instID);
	// iID.setUserId(userID);
	//
	// RdsDbinstance dbRec = new RdsDbinstance();
	// dbRec.setId(iID);
	//
	// dbRec.setDbinstanceClass(instRec.getDBInstanceClass());
	// dbRec.setAllocatedStorage(instRec.getAllocatedStorage().intValue());
	// dbRec.setEngine(instRec.getEngine());
	// dbRec.setEngineVersion(instRec.getEngineVersion());
	// dbRec.setAvailabilityZone(instRec.getAvailabilityZone());
	// dbRec.setMultiAz(instRec.getMultiAZ());
	// dbRec.setPort(instRec.getEndpoint().getPort().intValue());
	// dbRec.setAddress(instRec.getEndpoint().getAddress());
	// dbRec.setMasterUsername(instRec.getMasterUsername());
	// dbRec.setMasterUserPassword(InstanceEntity.getPassword(sess,
	// instID, userID));
	// dbRec.setDbName(instRec.getDBName());
	// dbRec.setAutoMinorVersionUpgrade(instRec
	// .getAutoMinorVersionUpgrade());
	// dbRec.setBackupRetentionPeriod(instRec.getBackupRetentionPeriod()
	// .intValue());
	// dbRec.setPreferredMaintenanceWindow(instRec
	// .getPreferredMaintenanceWindow());
	// dbRec.setPreferredBackupWindow(instRec.getPreferredBackupWindow());
	// dbRec.setAutoMinorVersionUpgrade(instRec
	// .getAutoMinorVersionUpgrade());
	//
	// // Perform cross field validation
	//
	// // PreferredBackupWindow cannot be set if BackupRetentionPeriod is 0
	// // since we default the PreferredBackupWindow to that of the
	// // AvailabilityZone all we need do is set PreferredBackupWindow to
	// // null
	// if (dbRec.getBackupRetentionPeriod() == 0) {
	// dbRec.setPreferredBackupWindow("");
	// }
	//
	// // Availability Zone parameter cannot be specified if the
	// // MultiAZ parameter is set to true
	// if (dbRec.getMultiAz()
	// && (dbRec.getAvailabilityZone() != null || dbRec
	// .getAvailabilityZone() != "")) {
	// msg = "insertRestoredInstance: Availability Zone cannot be "
	// + "specified if MultiAZ is set to true";
	// logger.error(msg);
	// throw new BaseException(msg);
	// }
	//
	// // Backup Retention Period Cannot be set to 0 if the DB Instance is
	// // a
	// // master instance with read replicas -- no need to check if creating
	// ReadReplicaDBInstanceIdentifierList rrList = InstanceEntity
	// .selectReadReplicas(sess, instID, userID);
	// if (rrList != null && dbRec.getBackupRetentionPeriod() == 0) {
	// msg = "insertRestoredInstance: Backup Retention Period cannot be "
	// + "set to 0 if the DBInstance has read replicas)";
	// logger.error(msg);
	// throw new BaseException(msg);
	// }
	//
	// // the following are set manually
	// dbRec.setInstanceCreateTime(RDSUtilities.getCurrentDateTime());
	// dbRec.setLatestRestorableTime(RDSUtilities.getCurrentDateTime());
	// dbRec.setDbinstanceStatus(RDSUtilities.STATUS_CREATING);
	// dbRec.setReadReplicaSourceDbinstanceIdentifier("masterinstance");
	//
	// // save / create the instance record
	// sess.save(dbRec);
	//
	// // Pending Modified ***********************************************
	// // TODO: Is this necessary for an insert?
	// InstanceEntity.insertPendingValues(sess, dbRec.getId()
	// .getDbinstanceId(), userID, dbRec.getAllocatedStorage(),
	// dbRec.getBackupRetentionPeriod(), dbRec.getEngineVersion(),
	// dbRec.getMasterUserPassword(), dbRec.getMultiAz(), dbRec
	// .getPort(), dbRec.getDbinstanceClass());
	//
	// /*
	// * DBParamterGroup *************************************************
	// * Copy the parameter groups from the source instance used to build
	// * the snapshot/backup to the new instance
	// */
	// DBParameterGroupStatus[] pStatArray = instRec
	// .getDBParameterGroups().getDBParameterGroup();
	//
	// if (pStatArray != null) {
	// int len = pStatArray.length;
	//
	// for (int i = 0; i < len; i++) {
	// InstanceEntity.insertDBParamGrpStatus(sess,
	// pStatArray[i].getDBParameterGroupName(), instID,
	// userID);
	// }
	// }
	//
	// /*
	// * DBSecurityGroup *************************************************
	// * Copy the parameter groups from the source instance used to build
	// * the snapshot/backup to the new instance
	// */
	// DBSecurityGroupMembership[] pMemArray = instRec
	// .getDBSecurityGroups().getDBSecurityGroup();
	//
	// if (pMemArray != null) {
	// int len = pMemArray.length;
	//
	// for (int i = 0; i < len; i++) {
	// InstanceEntity.insertSecGrpMembership(sess,
	// pMemArray[0].getDBSecurityGroupName(), instID,
	// userID);
	// }
	// }
	// // save everything.
	// logger.info("insertRestoredInstance: Successfully Inserted "
	// + "Instance into the Database");
	//
	// } catch (BaseException e) {
	// throw new BaseException(e.getMessage());
	// } catch (ConstraintViolationException dae) {
	// msg = "insertRestoredInstance: "
	// + dae.getSQLException().getMessage();
	// logger.info(msg);
	// throw new BaseException(msg);
	// } catch (Exception e) {
	// msg = "insertRestoredInstance " + e.getMessage();
	// logger.info(msg);
	// throw new BaseException(msg);
	// }
	// }

	/***************************************************************************
	 * insert a snapshot record for a DBInstance This is shared by the
	 * deleteInstance and createSnapshot operation and does all of the basic
	 * validation before inserting a snapshot record. Validation are validate
	 * DBInstanceID, SnapshotID Check SnapshotQuota Confirm that snapshot does
	 * not already exist
	 * 
	 * @param snapshotID
	 * @param userID
	 * @param instRec
	 * @return
	 * @throws BaseException
	 */
	public static RdsSnapshot insertSnapshot(final Session sess,
			final String instID, final String snapshotID, final String vId,
			final long acid) {
		final String msg = "";
		logger.info("insertSnapshot: " + " account = " + acid
				+ " Source DBInstanceID = " + instID + " SnapshotID = "
				+ snapshotID);

		// check snapshot quota
		// if (!QuotaEntity.withinQuota(sess, RDSUtilities.Quota.QUOTA_SNAPSHOT,
		// userID, 1)) {
		// msg = "insertSnapshot: SnapshotQuotaExceeded: Snapshot request "
		// + "exceeds allocation";
		// logger.info(msg);
		// throw new BaseException(msg);
		// }

		// confirm that source instance exists and is in available status
		// Get the DBInstance Record to be snapshot
		final RdsDbinstance instRec = RDSUtil.getInstance(sess, instID, acid);
		if (instRec == null) {
			throw RDSQueryFaults.DBInstanceNotFound();
		}

		// confirm that snapshot doesn't already exist
		final List<RdsSnapshot> snapRec = RDSUtil.selectSnapshot(sess, acid,
				snapshotID, instID, "", 1);
		if (snapRec != null && !snapRec.isEmpty()) {
			throw RDSQueryFaults.DBSnapshotAlreadyExists();
		}

		final RdsSnapshot snpRec = new RdsSnapshot();
		snpRec.setDbsnapshotId(snapshotID);
		snpRec.setUserId(acid);
		snpRec.setDbinstanceId(instRec.getDbinstanceId());
		snpRec.setSnapshotCreateTime(RDSUtilities.getCurrentDateTime());
		snpRec.setVolumeId(vId);
		snpRec.setAllocatedStorage(instRec.getAllocatedStorage());
		snpRec.setDbinstanceClass(instRec.getDbinstanceClass());
		snpRec.setEngine(instRec.getEngine());
		snpRec.setMasterUsername(instRec.getMasterUsername());
		snpRec.setMasterPasswd(instRec.getMasterUserPassword());
		snpRec.setStatus("creating");
		snpRec.setSnapshotType("manual");
		snpRec.setDbparameterGroup(instRec.getDbParameterGroup());
		snpRec.setAvailabilityZone(instRec.getAvailabilityZone());
		snpRec.setEngineVersion(instRec.getEngineVersion());
		snpRec.setInstanceCreatedTime(instRec.getInstanceCreateTime());
		snpRec.setLicenseModel(instRec.getLicenseModel());
		snpRec.setPort(instRec.getPort());

		// save the instance record
		sess.save(snpRec);
		logger.info("insertSnapShot: Successfully Inserted Snapshot");
		return snpRec;
	}

	@SuppressWarnings("unchecked")
	public static RdsSnapshot selectSnapshot(final Session sess,
			final String instanceId, final String snapshotId, final long userId) {
		RdsSnapshot result = null;
		String sql = "from RdsSnapshot where userId=" + userId;
		if (instanceId != null) {
			sql += " and dbinstanceId='" + instanceId + "'";
		}
		if (snapshotId != null) {
			sql += " and dbsnapshotId='" + snapshotId + "'";
		}
		final Query query = sess.createQuery(sql);
		final List<RdsSnapshot> l = query.list();
		if (l == null || l.size() == 0 || l.size() > 1) {
			logger.debug("selectSnapshot found " + l.size()
					+ " snapshots for userId=" + userId + "; DBInstanceId="
					+ instanceId + "; DBSnapshotId=" + snapshotId);
			return null;
		}
		result = l.get(0);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static RdsSnapshot selectSnapshotByVolumeId(final Session sess,
			final String instanceId, final String volId, final long userId) {
		RdsSnapshot result = null;
		String sql = "from RdsSnapshot where userId=" + userId;
		if (instanceId != null) {
			sql += " and dbinstanceId='" + instanceId + "'";
		}
		if (volId != null) {
			sql += " and volumeId='" + volId + "'";
		}
		final Query query = sess.createQuery(sql);
		final List<RdsSnapshot> l = query.list();
		if (l == null || l.size() == 0 || l.size() > 1) {
			logger.debug("selectSnapshot found " + l.size()
					+ " snapshots for userId=" + userId + "; DBInstanceId="
					+ instanceId + "; VolumeId=" + volId);
			return null;
		}
		result = l.get(0);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static List<RdsSnapshot> selectSnapshots(final Session sess,
			final String instanceId, final String snapshotId, final long userId) {
		String sql = "from RdsSnapshot where userId=" + userId;
		if (instanceId != null) {
			sql += " and dbinstanceId='" + instanceId + "'";
		}
		if (snapshotId != null) {
			sql += " and dbsnapshotId='" + snapshotId + "'";
		}
		final Query query = sess.createQuery(sql);
		final List<RdsSnapshot> l = query.list();
		return l;
	}

	public static DBSnapshot toDBSnapshot(final RdsSnapshot b) {
		final DBSnapshot snpRec = new DBSnapshot();
		snpRec.setDBSnapshotIdentifier(b.getDbsnapshotId());
		snpRec.setDBInstanceIdentifier(b.getDbinstanceId());
		snpRec.setAllocatedStorage(b.getAllocatedStorage());
		snpRec.setAvailabilityZone(b.getAvailabilityZone());
		snpRec.setEngine(b.getEngine());
		snpRec.setEngineVersion(b.getEngineVersion());
		snpRec.setMasterUsername(b.getMasterUsername());
		snpRec.setPort(b.getPort());
		snpRec.setInstanceCreateTime(b.getInstanceCreatedTime());
		snpRec.setSnapshotCreateTime(b.getSnapshotCreateTime());
		snpRec.setStatus(b.getStatus());
		snpRec.setLicenseModel(b.getLicenseModel());
		// TODO add the line below once AWS Java SDK is ready
		// snpRec.setSnapshotType(b.getSnapshotType());
		return snpRec;
	}

	/**************************************************************************
	 * Update the status and/or create time on the snapshot record
	 * 
	 * @param status
	 * @param adress
	 * @throws BaseException
	 */
	public static void updateSnapshotStatus(final Session sess,
			final long userID, final String snapshotID, final String status,
			final Date createDate) throws BaseException {
		final RdsSnapshot snpRec = RDSUtil
				.getSnapshot(sess, userID, snapshotID);
		// validate status
		if (status != null && status != "") {
			snpRec.setStatus(status);
		}
		if (createDate != null) {
			snpRec.setSnapshotCreateTime(createDate);
		}
		sess.save(snpRec);
	}
}
