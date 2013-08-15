package com.msi.tough.utils.rds;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.rds.model.DBSecurityGroup;
import com.amazonaws.services.rds.model.EC2SecurityGroup;
import com.amazonaws.services.rds.model.IPRange;
import com.msi.tough.core.BaseException;
import com.msi.tough.core.CommaObject;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsDbsecurityGroup;
import com.msi.tough.model.rds.RdsEC2SecurityGroupBean;
import com.msi.tough.model.rds.RdsIPRangeBean;

public class SecurityGroupEntity {

	private static String thisClass = SecurityGroupEntity.class.getName();

	public static void deleteEc2Grp(final Session sess,
			final RdsDbsecurityGroup dbSecGrpRec, final String dbSecGrpName,
			final String ec2SecGrpName, final String ec2Owner) {
		final List<RdsEC2SecurityGroupBean> beans = dbSecGrpRec
				.getEC2SecGroupBean(sess);
		for (final RdsEC2SecurityGroupBean bean : beans) {
			if (ec2Owner.equalsIgnoreCase(bean.getOwnId())
					&& ec2SecGrpName.equalsIgnoreCase(bean.getName())) {
				sess.delete(bean);
			}
		}
	}

	public static void deleteIPRange(final Session sess,
			final RdsDbsecurityGroup dbSecGrpRec, final String dbSecGrpName,
			final String cidrip) {
		final List<RdsIPRangeBean> beans = dbSecGrpRec.getIPRange(sess);
		for (final RdsIPRangeBean bean : beans) {
			if (cidrip.equalsIgnoreCase(bean.getCidrip())) {
				sess.delete(bean);
			}
		}
	}

	public static void deleteSecurityGroup(final Session sess,
			final RdsDbsecurityGroup secGrp) throws Exception {

		// delete the record for this DBSecurityGroup
		for (final RdsIPRangeBean ip : secGrp.getIPRange(sess)) {
			sess.delete(ip);
		}
		for (final RdsEC2SecurityGroupBean sec : secGrp
				.getEC2SecGroupBean(sess)) {
			sess.delete(sec);
		}
		sess.delete(secGrp);
	}

	public static String getCommaSeparatedGroups(final RdsDbinstance newInstRec) {
		final CommaObject co = new CommaObject();
		for (final RdsDbsecurityGroup group : newInstRec.getSecurityGroups()) {
			final String name = group.getDbsecurityGroupName();
			co.add(name);
		}
		return co.toString();
	}

	/**************************************************************************
	 * This returns the single named SecurityGroup record but uses the same
	 * implementation as selectSecurityGroups that returns an array of records.
	 * 
	 * @param secGrpName
	 * @param userID
	 * @return
	 * @throws BaseException
	 */
	public static RdsDbsecurityGroup getSecurityGroup(final Session sess,
			final String secGrpName, final long acid) {
		final Logger logger = LoggerFactory.getLogger(thisClass);

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

	public static RdsDbsecurityGroup insertCloneSecurityGroup(
			final Session sess, final RdsDbsecurityGroup original,
			final int port, final AccountBean ac) throws BaseException {
		final String secGrpName = "clone-" + port + "-"
				+ original.getDbsecurityGroupName();
		final String desc = original.getDbsecurityGroupDescription();
		return insertSecurityGroup(sess, ac, secGrpName, desc);
	}

	public static RdsDbsecurityGroup insertSecurityGroup(final Session sess,
			final AccountBean ac, final String secGrpName, final String desc)
			throws BaseException {

		// build the primary key
		final RdsDbsecurityGroup secGrp = new RdsDbsecurityGroup();
		secGrp.setDbsecurityGroupName(secGrpName);
		secGrp.setAccount(ac);
		secGrp.setDbsecurityGroupDescription(desc);
		secGrp.setStatus(RDSUtilities.STATUS_ACTIVE);

		sess.save(secGrp);
		return secGrp;
	}

	/**************************************************************************
	 * Select SecurityGroups if securityGroupName is supplied then select that
	 * security group else select all security groups
	 * 
	 * @param secGrpName
	 * @param userID
	 * @param marker
	 * @param maxRecords
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings("unchecked")
	public static List<RdsDbsecurityGroup> selectAllSecurityGroups(
			final Session sess, final String secGrpName, final long acid,
			final String marker, final int maxRecords) {
		final Logger logger = LoggerFactory.getLogger(thisClass);
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

	@SuppressWarnings("unchecked")
	public static List<RdsDbsecurityGroup> selectSecurityGroups(
			final Session sess, final String secGrpName, final long acid,
			final String marker, final int maxRecords) {
		final Logger logger = LoggerFactory.getLogger(thisClass);
		logger.info("selectSecurityGroup for " + " User " + acid
				+ " SecuityGroupName = " + secGrpName + " Marker = " + marker
				+ " MaxRecords = " + maxRecords);

		final String sql = RdsDbsecurityGroup.getSelectByGroupNameSQL(
				secGrpName, acid, marker);

		logger.info("selectSecurityGroup: SQL Query is: " + sql);
		final Query query = sess.createQuery(sql);
		if (maxRecords != 0) {
			query.setFirstResult(0);
			query.setMaxResults(maxRecords);
		}

		return query.list();
	}

	public static DBSecurityGroup toDBSecurityGroup(final Session s,
			final RdsDbsecurityGroup r) {

		final DBSecurityGroup g = new DBSecurityGroup();
		g.setDBSecurityGroupDescription(r.getDbsecurityGroupDescription());
		g.setDBSecurityGroupName(r.getDbsecurityGroupName());
		final List<EC2SecurityGroup> l = new ArrayList<EC2SecurityGroup>();
		for (final RdsEC2SecurityGroupBean rdsEC2SecGrp : r
				.getEC2SecGroupBean(s)) {
			final EC2SecurityGroup ec2g = new EC2SecurityGroup();
			ec2g.setEC2SecurityGroupName(rdsEC2SecGrp.getName());
			ec2g.setEC2SecurityGroupOwnerId(rdsEC2SecGrp.getOwnId());
			ec2g.setStatus(rdsEC2SecGrp.getStatus());
			l.add(ec2g);
		}
		g.setEC2SecurityGroups(l);

		final List<IPRange> ipl = new ArrayList<IPRange>();
		for (final RdsIPRangeBean ipRange : r.getIPRange(s)) {
			final IPRange ip = new IPRange();
			ip.setCIDRIP(ipRange.getCidrip());
			ip.setStatus(ipRange.getStatus());
			ipl.add(ip);
		}
		g.setIPRanges(ipl);
		g.setOwnerId(r.getAccount().getTenant());
		return g;
	}
}
