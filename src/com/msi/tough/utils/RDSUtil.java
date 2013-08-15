package com.msi.tough.utils;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.BaseException;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsDbsecurityGroup;
import com.msi.tough.model.rds.RdsIPRangeBean;
import com.msi.tough.model.rds.RdsSnapshot;
import com.msi.tough.utils.rds.SecurityGroupEntity;

public class RDSUtil {
	private static Logger logger = Appctx.getLogger(RDSUtil.class.getName());
	public static final String DEFAULT_GROUP = "default";

	public static void deleteInstance(final Session sess, final String instId,
			final long acid) {
		final RdsDbinstance deleteTarget = getInstance(sess, instId, acid);
		sess.delete(deleteTarget);
	}

	public static void ensureDefaultSecurityGroup(final Session session,
			final long acid) throws Exception {
		if (getSecurityGroup(session, DEFAULT_GROUP, acid) != null) {
			return;
		}
		final AccountBean ac = AccountUtil.readAccount(session, acid);
		final RdsDbsecurityGroup grp = SecurityGroupEntity.insertSecurityGroup(
				session, ac, DEFAULT_GROUP, "Defaut RDS Security Group");
		final RdsIPRangeBean iprb = new RdsIPRangeBean(grp.getId(), "0.0.0.0/0");
		session.save(iprb);
	}

	public static RdsDbinstance getInstance(final Session sess,
			final String instID, final long acid) {
		final List<RdsDbinstance> l = selectInstances(sess, instID, acid, null,
				0);
		if (l == null || l.size() == 0) {
			return null;
		}
		return l.get(0);
	}

	public static RdsDbsecurityGroup getSecurityGroup(final Session sess,
			final String secGrpName, final long acid) {
		// select a list of records passing in specific secGrpName and
		// userID
		final List<RdsDbsecurityGroup> result = selectSecurityGroups(sess,
				secGrpName, acid, "", 1);

		if (result == null || result.isEmpty()) {
			// don't throw exception - return null.
			logger.debug("getSecurityGroup: No SecurityGroup record"
					+ " found for user " + acid + " SecurityGroupName = "
					+ secGrpName);
			return null;
		} else {
			return result.get(0);
		}
	}

	/***************************************************************************
	 * This returns the single named Snapshot record but uses the same
	 * implementation as selectSnapshot that returns an array of records.
	 *
	 * @param userID
	 * @param snapshotID
	 * @return DBSnapshot record
	 * @throws BaseException
	 */
	public static RdsSnapshot getSnapshot(final Session sess,
			final long userID, final String snapshotID) {
		final List<RdsSnapshot> result = selectSnapshot(sess, userID,
				snapshotID, null, null, 0);
		if (result == null) {
			logger.debug("getSnapshot: No record found for user " + userID
					+ " snapshot " + snapshotID);
			return null;
		}
		return result.get(0);
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
			final String instanceID, final long acid, final String marker,
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
		final String sql = "from RdsDbinstance where account = " + acid
				+ instanceSql + markerSql + " order by dbinstanceId";
		logger.info("selectInstance: Query is " + sql);
		final Query query = sess.createQuery(sql);
		if (maxRecords != 0) {
			query.setFirstResult(0);
			query.setMaxResults(maxRecords);
		}
		return query.list();
	}

	@SuppressWarnings("unchecked")
    public static List<RdsDbinstance> selectInstancesByParameterGroup(
			final Session sess, final String paramGroupName,
			final AccountBean ac, final int limit) {
		final String pgsql = " and dbParameterGroup='" + paramGroupName + "'";
		final String sql = "from RdsDbinstance where account = " + ac.getId()
				+ pgsql + " order by dbinstanceId";

		logger.info("selectInstancesByParameterGroup: Query is " + sql);
		final Query query = sess.createQuery(sql);

		if (limit != 0) {
			query.setFirstResult(0);
			query.setMaxResults(limit);
		}

		return (List<RdsDbinstance>) query.list();
	}

	@SuppressWarnings("unchecked")
	public static List<RdsDbsecurityGroup> selectSecurityGroups(
			final Session sess, final String secGrpName, final long acid,
			final String marker, final int maxRecords) {
		logger.info("selectSecurityGroup for " + " User " + acid
				+ " SecuityGroupName = " + secGrpName + " Marker = " + marker
				+ " MaxRecords = " + maxRecords);
		final String sql = RdsDbsecurityGroup.getSelectAllByGroupNameSQL(
				secGrpName, acid, marker);

		logger.info("selectSecurityGroup: SQL Query is: " + sql);
		final Query query = sess.createQuery(sql);
		if (maxRecords != 0) {
			query.setFirstResult(0);
			query.setMaxResults(maxRecords);
		}

		return query.list();
	}

	/**************************************************************************
	 * SelectSnapshot - returns a list of Snapshot records If
	 * DBSnapshotIdentifier is specified select that DBSnapshots record If
	 * DBInstanceIdentifier specified select all DBSnapshots records for that
	 * DBInstance if neither are specified then select all DBSnapshot records
	 * for that user.
	 *
	 * @param userID
	 * @param snapshotID
	 * @param instID
	 * @param marker
	 * @param maxRecords
	 * @return list of snapshot records
	 * @throws BaseException
	 */
	@SuppressWarnings("unchecked")
	public static List<RdsSnapshot> selectSnapshot(final Session sess,
			final long userID, final String snapshotID, final String instID,
			final String marker, final int maxRecords) throws BaseException {

		String markerSql = "";
		String instanceSql = "";
		if (marker != null && marker != "") {
			markerSql = " and dbsnapshotId > '" + marker + "'";
		}
		if (snapshotID != null && snapshotID != "") {
			instanceSql = " and dbsnapshotId = '" + snapshotID + "'";
		} else if (instID != null && instID != "") {
			instanceSql = " and dbinstanceId = '" + instID + "'";
		}

		final String sql = "from RdsSnapshot where userId=" + userID
				+ instanceSql + markerSql + " order by dbsnapshotId";

		logger.info("selectSnapshot: Query is " + sql);
		final Query query = sess.createQuery(sql);
		if (maxRecords != 0) {
			query.setFirstResult(0);
			query.setMaxResults(maxRecords);
		}
		return query.list();
	}

	// public static DBInstance toDBInstance(RdsDbinstance b) {
	// DBInstance r = new DBInstance();
	// r.setId(b.getId());
	// r.setDbinstanceId(b.getDbinstanceId());
	// r.setInstanceId(b.getInstanceId());
	// r.setReplicateUser(b.getReplicationUsername());
	// r.setReplicatePassword(b.getReplicationPassword());
	// r.setSourceDbinstanceId(b.getSourceDbinstanceId());
	// r.setSnapshotId(b.getSnapshotId());
	// return r;
	// }
	//
	// public static Snapshot toSnapshot(RdsSnapshot b) {
	// Snapshot r = new Snapshot();
	// r.setDbinstanceId(b.getDbinstanceId());
	// r.setDbsnapshotId(b.getDbsnapshotId());
	// r.setLogPointer(b.getLogPointer());
	// r.setSnapshotCreateTime(b.getSnapshotCreateTime());
	// r.setStatus(b.getStatus());
	// r.setUserId(b.getUserId());
	// String[] sa = b.getLogPointer().split(",");
	// if (sa.length == 2) {
	// r.setLogFile(sa[0]);
	// r.setLogPosition(sa[1]);
	// }
	// return r;
	// }

}