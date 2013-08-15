package com.msi.tough.engine.aws.rds;

import org.hibernate.Session;

import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.engine.core.FailHook;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsSnapshot;
import com.msi.tough.utils.rds.InstanceEntity;
import com.msi.tough.utils.rds.SnapshotEntity;

public class DBSnapshotFailHook implements FailHook{

	@Override
	public void endFail(final long acid, final String stackId, final String physicalId,
			final String parameter) {
		// nothing to do
	}

	@Override
	public void startFail(final long acid, final String stackId, final String physicalId,
			final String parameter) {
		HibernateUtil.withNewSession(new Operation<Object>() {
			@Override
			public Object ex(final Session session, final Object... args)
					throws Exception {
				RdsSnapshot failed = SnapshotEntity.selectSnapshot(session, null, physicalId, acid);
				failed.setStatus("error");
				String dbInstId = failed.getDbinstanceId();
				RdsDbinstance inst = InstanceEntity.selectDBInstance(session, dbInstId, acid);
				inst.setDbinstanceStatus("available");
				session.save(inst);
				session.save(failed);
				return null;
			}
		});
	}

}
