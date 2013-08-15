package com.msi.tough.utils.rds;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.msi.tough.core.BaseException;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbinstance;

/*
 *  Utility Class For the RDS
 */

public class RDSUtilities {

	public enum Quota {
		QUOTA_INSTANCE, QUOTA_STORAGE, QUOTA_RESERVED, QUOTA_AUTHORIZATION, QUOTA_PARMGRP, QUOTA_SECGRP, QUOTA_SNAPSHOT
	}

	private static String thisClass = "com.msi.tough.utils.rds.RDSUtils";
	public static final String STATUS_ADDING = "adding";
	public static final String STATUS_ACTIVE = "active";
	public static final String STATUS_AVAILABLE = "available";
	public static final String STATUS_UPDATING = "updating";
	public static final String STATUS_AUTHORIZING = "authorizing";
	public static final String STATUS_AAUTHORIZED = "authorized";
	public static final String STATUS_BACKING_UP = "backing-up";
	public static final String STATUS_CREATING = "creating";
	public static final String STATUS_DELETED = "deleted";
	public static final String STATUS_DELETING = "deleting";
	public static final String STATUS_FAILED = "failed";
	public static final String STATUS_INCOMPATIBLE_RESTORE = "incompatible-restore";
	public static final String STATUS_INCOMPATIBLE_PARAMETERS = "incompatible-parameters";
	public static final String STATUS_IN_SYNC = "in-sync";
	public static final String STATUS_MODIFYING = "modifying";
	public static final String STATUS_PENDING_REBOOT = "pending-reboot";
	public static final String STATUS_REBOORING = "rebooting";
	public static final String STATUS_REMOVING = "removing";
	public static final String STATUS_RESETTING_MASTER_CREDENTIALS = "resetting-master-credentials";
	public static final String STATUS_REVOKING = "revoking";

	public static final String STATUS_STORAGE_FULL = "storage-full";
	public static final String QUOTA_INSTANCE = "InstanceQuota";
	public static final String QUOTA_STORAGE = "StorageQuota";
	public static final String QUOTA_RESERVED = "ReservedDBInstanceQuota";
	public static final String QUOTA_AUTHORIZATION = "AuthorizationQuota";
	public static final String QUOTA_PARMGRP = "DBParameterGroupQuota";
	public static final String QUOTA_SECGRP = "DBSecurityGroupQuota";

	public static final String QUOTA_SNAPSHOT = "SnapshotQuota";
	public static final String EVENT_SRC_DBINSTANCE = "db-instance";
	public static final String EVENT_SRC_PARAMGRP = "db-parameter-group";
	public static final String EVENT_SRC_SECGRP = "db-security-group";

	public static final String EVENT_SRC_SNAPS = "db-snapshot";
	public static final String PARAM_SRC_USER = "user";
	public static final String PARAM_SRC_ENGINE = "engine";

	public static final String PARAM_SRC_SYSTEM = "system";
	public static final String PARM_APPTYPE_STATIC = "static";

	public static final String PARM_APPTYPE_DYNAMIC = "dynamic";
	public static final String PARM_APPMETHOD_IMMEDIATE = "immediate";

	public static final String PARM_APPMETHOD_PENDING = "pending-reboot";
	public static final String INST_CLASS_SMALL = "m1.small";
	public static final String INST_CLASS_LARGE = "m1.large";
	public static final String INST_CLASS_XLARGE = "m1.xlarge";
	public static final String INST_CLASS_2LARGE = "m2.xlarge";
	public static final String INST_CLASS_2XLARGE = "m2.2xlarge";
	public static final String INST_CLASS_4XLARGE = "m2.4xlarge";
	public static final String INST_CLASS_C1XLARGE = "c1.xlarge";
	
	public static final String ENGINE_MYSQL = "MySQL";
	public static final String ENGINE_VERSION_55 = "5.5";
	public static final String ENGINE_FAMILY = "MySQL5.5";
	public static final String MYSQL_ROOT_USER = "root";
	public static final String MYSQL_ROOT_PASSWORD = "password";
	public static final String MYSQL_REPLICATION_USER = "repl";
	public static final String MYSQL_REPLICATION_PASSWORD = "replpassword";
	
	public static final String ENGINE_ORACLE_EE = "Oracle-ee";
	public static final String ENGINE_ORACLE_SE = "Oracle-se";
	public static final String ENGINE_ORACLE_SE1 = "Oracle-se1";
	public static final String ENGINE_VERSION_11_2_0_2_V2 = "11.2.0.2.V2";
	public static final String ENGINE_FAMILY_ORACLE = "Oracle11.2";
	public static final String ORACLE_ROOT_USER = "root";
	public static final String ORACLE_ROOT_PASSWORD = "password";
	public static final String ORACLE_REPLICATION_USER = "repl";
	public static final String ORACLE_REPLICATION_PASSWORD = "replpassword";
	
	public static final String AVAIL_ZN_EAST = "msi_east-ld";
	public static final String AVAIL_ZN_WEST = "msi_west-ld";
	public static final String AVAIL_ZN_SOUTH = "msi_south-ld";
	public static final String AVAIL_ZN_AP = "msi_ap-ld";
	public static final String AVAIL_ZN_EU = "msi_eu-ld";;

	public static final String MYSQL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**************************************************************************
	 * 
	 * Converts Integer to BigInteger
	 * 
	 * @param val
	 * @return
	 */
	public static int BigInt2int(BigInteger val) {
		if (val == null) {
			return 0;
		} else {
			return val.intValue();
		}
	}

	/**************************************************************************
	 * 
	 * Converts Date to Calendar
	 * 
	 * @param inDate
	 * @return
	 */
	public static Calendar Date2Calendar(Date inDate) {
		Calendar calendarValue = Calendar.getInstance();
		if (inDate != null) {
			calendarValue.setTime(inDate);
			return calendarValue;
		}
		return calendarValue;
	}

	/**************************************************************************
	 * 
	 * Determines if given boolean value is null and if so returns false
	 * 
	 * @param inValue
	 * @return
	 */
	public static Boolean defaultFalse(Boolean inValue) {
		if (inValue == null) {
			inValue = false;
		}
		return inValue.booleanValue();
	}

	/**************************************************************************
	 * 
	 * Determines if given boolean value is null and if so returns false
	 * 
	 * @param inValue
	 * @return
	 */
	public static Boolean defaultTrue(Boolean inValue) {
		if (inValue == null) {
			inValue = true;
		}
		return inValue.booleanValue();
	}

	/**************************************************************************
	 * 
	 * Returns current date and time as Date
	 * 
	 * @return
	 */
	public static Date getCurrentDateTime() {
		// calculate current date time in format "yyyy/MM/dd HH:mm:ss"
		Date currentTime = new Date();
		return currentTime;
	}

	/**************************************************************************
	 * 
	 * Converts the given BigInteger to Integer
	 * 
	 * @param val
	 * @return
	 */
	public static BigInteger int2BigInt(int val) {
		return BigInteger.valueOf(val);
	}

	/**************************************************************************
	 * 
	 * Converts the sting representation of a date (in database) to a Calendar
	 * (in XML message)
	 * 
	 * @param strDate
	 * @return Calender
	 * @throws BaseException
	 */
	public static Calendar Str2Cal(String strDate) {
		String msg = "";
		Logger logger = LoggerFactory.getLogger(thisClass);
		Calendar cal = null;
		try {

			DateFormat formatter = new SimpleDateFormat(MYSQL_DATE_FORMAT);
			Date date = formatter.parse(strDate);
			cal = Calendar.getInstance();
			cal.setTime(date);

		} catch (Exception e) {
			msg = "Str2Cal Error: Class: " + e.getClass().toString() + " Msg: "
					+ e.getMessage();
			logger.error(msg);
			throw new BaseException(msg);
		}
		return cal;
	}

	/**************************************************************************
	 * 
	 * Creates a string key to object map and puts parameters keyed by name.
	 * Generally used to along with the JSON template to cloud formation.
	 * 
	 * @param newInstRec RdsDBnstance new instance record
	 * @param instID String instance id of the new instance
	 * @param ac AccountBean user account bean object
	 * @return Map<String, Object> map
	 */
	public static Map<String, Object> PutParametersToHash(
			RdsDbinstance newInstRec, String instID, AccountBean ac) {
		final Map<String, Object> parameterValues = new HashMap<String, Object>();
		
		long acctId = ac.getId();
		String allcStrg = newInstRec.getAllocatedStorage().toString();
		String avZn = newInstRec.getAvailabilityZone();
		String bckRtn = newInstRec.getBackupRetentionPeriod().toString();
		String dbInstCl = newInstRec.getDbinstanceClass();
		String dbNm = newInstRec.getDbName();
		String pmGrpNm = newInstRec.getDbParameterGroup();
		String snpShtId = newInstRec.getSnapshotId();
		String eng =  newInstRec.getEngine();
		String engVsn = newInstRec.getEngineVersion();
		String uname = newInstRec.getMasterUsername();
		String psswd = newInstRec.getMasterUserPassword();
		String port = newInstRec.getPort().toString();
		String bckWndw = newInstRec.getPreferredBackupWindow();
		String mntWndw = newInstRec.getPreferredMaintenanceWindow();
		String dbSecGrp = SecurityGroupEntity.getCommaSeparatedGroups(newInstRec);
		String lmodel = newInstRec.getLicenseModel();
		boolean mltiAz = newInstRec.getMultiAz();
		
		
		// Required RDS parameter values
		parameterValues.put("AccountId", "" + acctId);
		parameterValues.put("AllocatedStorage",
				allcStrg);
		parameterValues.put("DBInstanceClass",
				dbInstCl);
		parameterValues.put("Engine", eng);
		parameterValues.put("MasterUsername",
				uname);
		parameterValues.put("MasterUserPassword",
				psswd);
			// Optional RDS parameter values
		parameterValues.put("AvailabilityZone",
				avZn);
		parameterValues.put("BackupRetentionPeriod", 
				bckRtn);
		parameterValues.put("DBInstanceId", instID);
		if(snpShtId != null && !snpShtId.isEmpty()){
			parameterValues.put("DBSnapshotIdentifier", 
					snpShtId);
		}
		if(dbNm != null){
		parameterValues.put("DBName", 
				dbNm);
		}
		parameterValues.put("DBParameterGroupName", 
				pmGrpNm);
		parameterValues.put("DBSecurityGroups", 
				dbSecGrp);
		parameterValues.put("EngineVersion", 
				engVsn);
		parameterValues.put("Port", 
				port);
		parameterValues.put("PreferredBackupWindow", 
				bckWndw);
		parameterValues.put("PreferredMaintenanceWindow", 
				mntWndw);
		parameterValues.put("LicenseModel", 
				lmodel);
		parameterValues.put("MultiAZ", 
				mltiAz);
		
		return parameterValues;
	}
}
