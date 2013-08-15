package com.msi.tough.engine.aws.elasticloadbalancing;

import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.engine.core.WaitHook;
import com.msi.tough.model.LoadBalancerBean;
import com.msi.tough.utils.LoadBalancerUtil;

/**
 * Wait hook to be called on transcend_loadbalancer chef role installtion
 * completion
 *
 * @author raj
 *
 */
public class LoadBalancerWaitHook implements WaitHook {
	private static Logger logger = Appctx.getLogger(LoadBalancerWaitHook.class
			.getName());

	@Override
	public void postWait(final Session s, final boolean success,
			final long acid, final String stackId, final String physicalId,
			final String parameter, final Map<String, String[]> map) {

		// convert passed back parameter to Map using JSON
		if (parameter != null) {
			final Map<String, Object> m = JsonUtil.toMap(JsonUtil
					.load(parameter));
		}

		logger.debug("Changing status to ready for LB " + physicalId);
		// change the load balancer status to ready
		final LoadBalancerBean lb = LoadBalancerUtil.read(s, acid, physicalId);
		if (lb == null) {
			logger.debug("LB not found" + physicalId);
		} else {
			lb.setLbStatus("ready");
			s.save(lb);
		}
	}
}
