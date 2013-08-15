package com.msi.tough.engine.aws.elasticloadbalancing;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.engine.core.FailHook;
import com.msi.tough.model.LoadBalancerBean;
import com.msi.tough.utils.LoadBalancerUtil;

public class LoadBalancerFailHook implements FailHook {
	private static Logger logger = Appctx.getLogger(LoadBalancerFailHook.class
			.getName());

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
				final LoadBalancerBean tb = LoadBalancerUtil.read(session,
						acid, physicalId);
				if (tb != null) {
					tb.setLbStatus("failed");
					session.save(tb);
				}
				return null;
			}
		});
	}
}
