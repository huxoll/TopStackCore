package com.msi.tough.utils.rds;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.rds.model.DBEngineVersion;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.BaseException;
import com.msi.tough.core.StringHelper;
import com.msi.tough.model.rds.RdsDbengine;

public class EngineEntity {
	private static Logger logger = Appctx.getLogger(ParameterGroupEntity.class
			.getName());

	/**************************************************************************
	 * Returns the named DBEngine record
	 * 
	 * @param engine
	 * @param engineVersion
	 * @return
	 * @throws BaseException
	 */
	public static RdsDbengine getDBEngine(Session sess, String engine,
			String engineVersion) {
		RdsDbengine resp = null;
		String engineFamily = engine + engineVersion;

		logger.info("getDBEngine: " + " Engine = " + engine
				+ " ParameterGroupName = " + engineVersion + " EngineFamily = "
				+ engineFamily);
		// build primary key
		resp = (RdsDbengine) sess.get(RdsDbengine.class, engineFamily);
		if (resp == null) {
			logger.debug("Cannot find DBEngine Record for " + engineFamily);
		}
		return resp;
	}

	/**************************************************************************
	 * Select a list of DB Engine details You can restrict the list returned by
	 * one or more of the following 1. DBParameterGroupFamily 2. DefaultOnly .
	 * 3. Engine 4. EngineVersion
	 * 
	 * @param userID
	 * @param grpFamily
	 * @param engine
	 * @param engineVersion
	 * @param engineVersion2
	 * @param defaultOnly
	 * @param marker
	 * @param maxRecords
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings("unchecked")
	public static List<RdsDbengine> selectDBEngineVersions(Session sess,
			String userID, String grpFamily, String engine,
			String engineVersion, boolean defaultOnly, String marker,
			int maxRecords) {
		// Build SQL statement
		String sql = null;

		// if default return the default record
		if (defaultOnly) {
			sql = "from RdsDbengine where _default = true ";
		} else {
			// The others build up cumulatively - you could ask for one or
			// more of these
			List<String> parts = new ArrayList<String>();
			if (marker != null && !marker.equals("")) {
				parts.add("dbparameterGroupFamily > '" + marker + "'");
			}

			if (grpFamily != null && !grpFamily.equals("")) {
				parts.add(" dbparameterGroupFamily = '" + grpFamily + "'");
			}

			if (engine != null && !engine.equals("")) {
				parts.add("engine = '" + engine + "'");
			}

			if (engineVersion != null && !engineVersion.equals("")) {
				parts.add("engine_version = '" + engineVersion + "'");
			}
			sql = "from RdsDbengine ";
			if (parts.size() != 0) {
				sql += " where "
						+ StringHelper.concat(parts.toArray(new String[0]),
								" and ");
			}
			sql += " order by dbparameterGroupFamily";
		}

		logger.info("selectDBEngineVersion: Query is " + sql);
		Query query = sess.createQuery(sql);
		if (maxRecords > 0) {
			query.setMaxResults(maxRecords);
		}
		return query.list();
	}

	public static DBEngineVersion toDBEngineVersion(RdsDbengine b) {
		DBEngineVersion engineRec = new DBEngineVersion();
		engineRec.setDBParameterGroupFamily(b.getDbparameterGroupFamily());
		engineRec.setEngine(b.getEngine());
		engineRec.setEngineVersion(b.getEngineVersion());
		return engineRec;
	}
}
