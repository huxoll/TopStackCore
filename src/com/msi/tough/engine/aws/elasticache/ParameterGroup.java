package com.msi.tough.engine.aws.elasticache;

import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.elasticache.ParameterGroupType;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.model.elasticache.CacheClusterBean;
import com.msi.tough.model.elasticache.CacheParameterGroupBean;
import com.msi.tough.utils.EcacheUtil;
import com.msi.tough.utils.ElasticacheFaults;

public class ParameterGroup extends BaseProvider {

	private static final Logger logger = LoggerFactory
			.getLogger(CacheCluster.class.getName());

	public static String TYPE = "AWS::Elasticache::ParameterGroup";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		final String name = call.getName();

		final String cacheParameterGroupFamily = (String) call
				.getProperty("CacheParameterGroupFamily");
		final String description = (String) call.getProperty("Description");

		HibernateUtil.withNewSession(new Operation<CacheParameterGroupBean>() {

			@Override
			public CacheParameterGroupBean ex(final Session s,
					final Object... args) throws Exception {

				final CacheParameterGroupBean cacheParameterGroup = EcacheUtil
						.createParameterGroup(s, ac.getId(),
								cacheParameterGroupFamily, name, description);
				return cacheParameterGroup;
			}
		});

		final ParameterGroupType ret = new ParameterGroupType();
		ret.setName(name);
		return ret;
	}

	@Override
	public Resource delete0(final CallStruct call) throws Exception {
		super.delete0(call);
		final AccountType ac = call.getAc();
		final String name = call.getPhysicalId();

		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {

				final CacheParameterGroupBean pgrp = EcacheUtil
						.getCacheParameterGroupBean(s, ac.getId(), name);

				final List<CacheClusterBean> ccbs = EcacheUtil
						.selectCacheClusterBean(s, ac.getId(), null);
				boolean used = false;
				for (final CacheClusterBean ccb : ccbs) {
					if (ccb.getParameterGroupId() == pgrp.getId()) {
						used = true;
						break;
					}
				}
				if (used) {
					throw ElasticacheFaults.InvalidCacheParameterGroupState();
				}

				EcacheUtil.deleteParameterGroupBean(s, ac.getId(), name);
				return null;
			}
		});
		logger.debug("ParameterGroup deleted " + name);
		return null;
	}
}
