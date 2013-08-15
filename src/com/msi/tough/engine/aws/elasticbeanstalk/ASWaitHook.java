package com.msi.tough.engine.aws.elasticbeanstalk;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.engine.core.WaitHook;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.utils.CFUtil;

/**
 * Wait hook to be called on transcend_loadbalancer chef role installtion
 * completion
 * 
 * @author raj
 * 
 */
public class ASWaitHook implements WaitHook {
	private static Logger logger = Appctx.getLogger(ASWaitHook.class.getName());

	@Override
	public void postWait(final Session s, final boolean success,
			final long acid, final String stackId, final String physicalId,
			final String parameter, final Map<String, String[]> map) {
		try {
			logger.debug("ASWaitHook " + acid + " " + stackId + " "
					+ physicalId + " " + parameter);
			// convert passed back parameter to Map using JSON
			if (parameter != null) {
				final Map<String, Object> m = JsonUtil.toMap(JsonUtil
						.load(parameter));
			}

			// register the instance with load balancer
			final List<ResourcesBean> linst = CFUtil.selectResourceRecords(s,
					acid, stackId, null, physicalId, false);
			if (linst == null || linst.size() != 1) {
				logger.error("Instance not found " + physicalId);
				return;
			}
			final ResourcesBean instRes = linst.get(0);
			final String instParent = instRes.getParentId();
			if (instParent == null) {
				logger.error("Instance parent not found " + instParent);
				return;
			}
			final ASGroupBean asgrp = ASUtil.readASGroup(s, acid, instParent);
			if (asgrp == null) {
				logger.error("Instance ASGRP not found " + instParent);
				return;
			}

			ASUtil.reconfigAddInstance(s, asgrp, physicalId);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
