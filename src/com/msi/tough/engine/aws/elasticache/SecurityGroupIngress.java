package com.msi.tough.engine.aws.elasticache;

import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.elasticache.SecurityGroupIngressType;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.core.MapUtil;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.model.elasticache.CacheSecurityGroupBean;
import com.msi.tough.utils.EcacheUtil;
import com.msi.tough.utils.ElasticacheFaults;
import com.msi.tough.utils.SecurityGroupUtils;

public class SecurityGroupIngress extends BaseProvider {

	private static final Logger logger = LoggerFactory
			.getLogger(CacheCluster.class.getName());

	public static String TYPE = "AWS::Elasticache::SecurityGroupIngress";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		final String name = call.getName();
		final String secGrpName = (String) call
				.getProperty("CacheSecurityGroupName");
		final String ec2SecGrpName = (String) call
				.getProperty("EC2SecurityGroupName");
		final String ec2SecGrpOwnerId = (String) call
				.getProperty("EC2SecurityGroupOwnerId");

		HibernateUtil.withNewSession(new Operation<CacheSecurityGroupBean>() {

			@Override
			public CacheSecurityGroupBean ex(final Session s,
					final Object... args) throws Exception {

				final CacheSecurityGroupBean secGrp = EcacheUtil
						.getCacheSecurityGroupBean(s, ac.getId(), secGrpName);
				if (secGrp == null) {
					throw ElasticacheFaults.CacheSecurityGroupNotFound();
				}

				logger.debug("AuthorizeCacheSecurityGroupIngress");
				logger.debug("SecurityGroupName = " + secGrpName);
				logger.debug("Ec2SecurityGroupName = " + ec2SecGrpName);
				logger.debug("Ec2SecurityGroupOwnerId = " + ec2SecGrpOwnerId);

				SecurityGroupUtils.authorizeSecurityGroupIngress(ac,
						secGrp.getProviderId(), ec2SecGrpName,
						secGrp.getStackId(), ec2SecGrpOwnerId, 11211, name);

				final ResourcesBean rb = getResourceBean(s);
				final Map<String, Object> map = MapUtil.create(
						"SecurityGroupName", secGrpName,
						"Ec2SecurityGroupName", ec2SecGrpName,
						"Ec2SecurityGroupOwnerId", ec2SecGrpOwnerId);
				final String js = JsonUtil.toJsonStringIgnoreNullValues(map);
				rb.setResourceData(js);
				s.save(rb);
				return secGrp;
			}
		});

		final SecurityGroupIngressType ret = new SecurityGroupIngressType();
		ret.setName(name);
		return ret;
	}

	@Override
	public Resource delete0(final CallStruct call) throws Exception {
		super.delete0(call);
		final AccountType ac = call.getAc();
		final String name = call.getPhysicalId();

		final ResourcesBean rb = call.getResourcesBean();
		Map<String, Object> map = null;
		if (rb.getResourceData() != null) {
			final JsonNode n = JsonUtil.load(rb.getResourceData());
			map = JsonUtil.toMap(n);
		}
		final String secGrpName = map != null
				&& map.containsKey("SecurityGroupName") ? (String) map
				.get("SecurityGroupName") : "";
		final String ec2SecGrpName = map != null
				&& map.containsKey("Ec2SecurityGroupName") ? (String) map
				.get("Ec2SecurityGroupName") : "";
		final String ec2SecGrpOwnerId = map != null
				&& map.containsKey("Ec2SecurityGroupOwnerId") ? (String) map
				.get("Ec2SecurityGroupOwnerId") : "";

		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {

				final CacheSecurityGroupBean secGrp = EcacheUtil
						.getCacheSecurityGroupBean(s, ac.getId(), secGrpName);
				if (secGrp == null) {
					throw ElasticacheFaults.CacheSecurityGroupNotFound();
				}
				getResourceBean(s);
				logger.debug("AuthorizeCacheSecurityGroupIngress");
				logger.debug("SecurityGroupName = " + secGrpName);
				logger.debug("Ec2SecurityGroupName = " + ec2SecGrpName);
				logger.debug("Ec2SecurityGroupOwnerId = " + ec2SecGrpOwnerId);

				// final SecurityGroupType st = SecurityGroupUtils
				// .describeSecurityGroup(AccountUtil.toAccount(account),
				// account.getDefZone(), secGrp.getProviderId());

				// SecurityGroupUtils.revokeSecurityGroupIngress(ac,
				// secGrp.getProviderId(), ec2SecGrpName,
				// secGrp.getStackId(), ec2SecGrpOwnerId, 11211);
				return null;
			}
		});
		logger.debug("SecurityGroupIngress deleted " + name);
		return null;
	}
}
