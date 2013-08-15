package com.msi.tough.engine.aws.rds;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.engine.core.WaitHook;
import com.msi.tough.model.InstanceBean;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.InstanceUtil;

public class DBInstanceWaitHook implements WaitHook {
	private static Logger logger = Appctx.getLogger(DBInstanceWaitHook.class
			.getName());

	@SuppressWarnings("unchecked")
	private static RdsDbinstance selectDBInstance(final Session sess,
			final String instanceID, final long userID) {
		final String sql = "from RdsDbinstance where account_id=" + userID
				+ " and db_instance_identifier='" + instanceID + "'";
		logger.debug("HQL: " + sql);
		final Query query = sess.createQuery(sql);
		final List<RdsDbinstance> l = query.list();
		if (l == null || l.size() == 0 || l.size() > 1) {
			logger.debug("There is no DBInstance with such identifier in this user's account...");
			return null;
		}
		return l.get(0);
	}

	@Override
	public void postWait(final Session s, final boolean success,
			final long acid, final String stackId, final String physicalId,
			final String parameter, final Map<String, String[]> map) {
		logger.debug("postWait() is called with following parameters: \n"
				+ "Session: N/A\n" + "Success: " + success + "\n"
				+ "AccountId: " + acid + "\n" + "StackId: " + stackId + "\n"
				+ "PhysicalId: " + physicalId + "\n" + "Parameter: "
				+ parameter);

		// convert passed back parameter to Map using JSON
		if (parameter != null) {
			final Map<String, Object> m = JsonUtil.toMap(JsonUtil
					.load(parameter));
		}

		logger.debug("Updating the DBInstanceStatus...");
		final RdsDbinstance dbInstance = selectDBInstance(s, physicalId, acid);
		Boolean restoring = QueryUtil.getBoolean(map, "Restoring");
		logger.debug("Checking if the instance is restoring/replicating: " + restoring);
		if(restoring == null || restoring == false){
			dbInstance.setDbinstanceStatus("available");	
		}else{
			dbInstance.setDbinstanceStatus("restoring");
		}
		s.save(dbInstance);

		if (dbInstance.getAddress() == null) {
			try {
				final List<ResourcesBean> res = CFUtil.selectResourceRecords(s,
						acid, stackId, physicalId, null, false);
				if (res.size() == 1) {
					final String ec2id = res.get(0).getPhysicalId();
					final InstanceBean ib = InstanceUtil.getInstance(s, ec2id);
				}
			} catch (final Exception e) {
				logger.debug("Error has occurred while setting address for the DBInstance, "
						+ physicalId);
				e.printStackTrace();
			}
		}
	}

}
