package com.msi.tough.engine.aws.elasticache;

import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.engine.core.WaitHook;

/**
 * Wait hook to be called on transcend_memcached chef role installation
 * completion
 * 
 * @author raj
 * 
 */
public class ElasticacheClusterWaitHook implements WaitHook {
	private static Logger logger = Appctx
			.getLogger(ElasticacheClusterWaitHook.class.getName());

	@Override
	public void postWait(final Session s, final boolean success,
			final long acid, final String stackId, final String physicalId,
			final String parameter, Map<String, String[]> map) {

		// convert passed back parameter to Map using JSON
		// if (parameter != null) {
		// final Map<String, Object> m = JsonUtil.toMap(JsonUtil
		// .load(parameter));
		// }
		//
		// logger.debug("Changing status of Cluster " + physicalId);
		//
		// final AccountBean ac = AccountUtil.readAccount(s, acid);
		// final CacheClusterBean cc = EcacheUtil.getCacheClusterBean(s,
		// ac.getId(), physicalId);
		// if (cc != null) {
		// cc.setCacheClusterStatus("ready");
		// }
		// s.save(cc);
	}
}
