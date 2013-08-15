package com.msi.tough.engine.aws.elasticache;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.engine.core.WaitHook;
import com.msi.tough.model.elasticache.CacheClusterBean;
import com.msi.tough.model.elasticache.CacheNodeBean;
import com.msi.tough.utils.EventUtil;

/**
 * Wait hook to be called on transcend_memcached chef role installation
 * completion
 * 
 * @author raj
 * 
 */
public class ElasticacheNodeWaitHook implements WaitHook {
	private static Logger logger = Appctx
			.getLogger(ElasticacheNodeWaitHook.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	public void postWait(final Session s, final boolean success,
			final long acid, final String stackId, final String physicalId,
			final String parameter, final Map<String, String[]> map) {

		// convert passed back parameter to Map using JSON
		// if (parameter != null) {
		// final Map<String, Object> m = JsonUtil.toMap(JsonUtil
		// .load(parameter));
		// }

		logger.debug("Changing status of Node " + physicalId);

		long cid = 0;
		final Query q = s.createQuery("from CacheNodeBean where instaceId='"
				+ physicalId + "'");
		final List<CacheNodeBean> l = q.list();
		if (l == null || l.size() == 0) {
			return;
		}
		final CacheNodeBean b = l.get(0);
		b.setNodeStatus("available");
		b.setParameterGroupStatus("in-sync");
		s.save(b);
		cid = b.getCacheCluster();

		final Query qcc = s
				.createQuery("from CacheClusterBean where id=" + cid);
		final List<CacheClusterBean> lcc = qcc.list();
		CacheClusterBean cc = null;
		if (lcc != null && lcc.size() > 0) {
			cc = lcc.get(0);
		}

		EventUtil.addEvent(s, acid, "Added cache node", "cache-cluster",
				new String[] { cc.getName(), b.getInstaceId() });

		if (cid != 0) {
			int pending = 0;
			{
				final Query q0 = s
						.createQuery("from CacheNodeBean where cacheCluster="
								+ cid);
				final List<CacheNodeBean> l0 = q0.list();
				if (l0 != null) {
					for (final CacheNodeBean b0 : l0) {
						if (!b0.getNodeStatus().equals("available")) {
							pending++;
						}
					}
				}
			}
			if (pending == 0) {
				cc.setCacheClusterStatus("available");
				cc.setParameterGroupStatus("in-sync");
				s.save(cc);
				EventUtil.addEvent(s, acid, "Cache cluster created",
						"cache-cluster", new String[] { cc.getName() });
			}
		}
	}
}
