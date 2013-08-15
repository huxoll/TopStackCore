package com.msi.tough.engine.aws.rds;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.engine.core.FailHook;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.utils.RDSUtil;

public class DBInstanceFailHook implements FailHook {
	private static Logger logger = Appctx.getLogger(DBInstanceFailHook.class
			.getName());

	private static RdsDbinstance selectDBInstance(final Session sess,
			final String instanceID, final long userID) {
		final List<RdsDbinstance> l = RDSUtil.selectInstances(sess, instanceID, userID, null, 0);
		if (l == null || l.size() == 0 || l.size() > 1) {
			logger.debug("There is no DBInstance with such identifier in this user's account...");
			return null;
		}
		return l.get(0);
	}

	@Override
	public void endFail(final long acid, final String stackId,
			final String physicalId, final String parameter) {
		// nothing to do will be taken care by stack deletion
	}

	@Override
	public void startFail(final long acid, final String stackId,
			final String physicalId, final String parameter) {
		HibernateUtil.withNewSession(new Operation<Object>() {
			@Override
			public Object ex(final Session session, final Object... args)
					throws Exception {
				final RdsDbinstance dbInstance = selectDBInstance(session,
						physicalId, acid);
				if (dbInstance != null) {
					dbInstance.setDbinstanceStatus("error");
					session.save(dbInstance);
				}
				return null;
			}
		});
	}
}
