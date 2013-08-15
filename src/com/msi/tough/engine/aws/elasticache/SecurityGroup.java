package com.msi.tough.engine.aws.elasticache;

import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.elasticache.SecurityGroupType;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.model.elasticache.CacheClusterBean;
import com.msi.tough.model.elasticache.CacheSecurityGroupBean;
import com.msi.tough.utils.EcacheUtil;
import com.msi.tough.utils.ElasticacheFaults;

public class SecurityGroup extends BaseProvider {

	private static final Logger logger = LoggerFactory
			.getLogger(CacheCluster.class.getName());

	public static String TYPE = "AWS::Elasticache::SecurityGroup";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		final String name = call.getName();
		final String description = (String) call.getProperty("Description");

		HibernateUtil.withNewSession(new Operation<CacheSecurityGroupBean>() {

			@Override
			public CacheSecurityGroupBean ex(final Session s,
					final Object... args) throws Exception {
				final CacheSecurityGroupBean secGrp = EcacheUtil
						.createSecurityGroup(s, ac.getId(), name, description,
								call.getStackId(), name);
				return secGrp;
			}
		});

		final SecurityGroupType ret = new SecurityGroupType();
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

				final CacheSecurityGroupBean securityGroup = EcacheUtil
						.getCacheSecurityGroupBean(s, ac.getId(), name);

				// Validate Exists
				if (securityGroup == null) {
					throw ElasticacheFaults.CacheSecurityGroupNotFound();
				}

				final List<CacheClusterBean> ccbs = EcacheUtil
						.selectCacheClusterBean(s, ac.getId(), null);
				boolean used = false;
				for (final CacheClusterBean ccb : ccbs) {
					if (ccb.getSecurityGroups().contains(name)) {
						used = true;
						break;
					}
				}
				if (used) {
					throw ElasticacheFaults.InvalidCacheSecurityGroupState();
				}

				EcacheUtil.deleteSecurityGroupBean(s, ac, name);
				return null;
			}
		});
		logger.debug("SecurityGroup deleted " + name);
		return null;
	}
}
