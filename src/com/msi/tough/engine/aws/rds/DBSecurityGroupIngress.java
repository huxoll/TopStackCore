package com.msi.tough.engine.aws.rds;

import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.rds.DBSecurityGroupIngressType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.core.MapUtil;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.model.rds.RdsDbsecurityGroup;
import com.msi.tough.model.rds.RdsEC2SecurityGroupBean;
import com.msi.tough.model.rds.RdsIPRangeBean;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.rds.SecurityGroupEntity;

public class DBSecurityGroupIngress extends BaseProvider {

	private static final Logger logger = Appctx
			.getLogger(DBSecurityGroupIngress.class.getName());

	public static String TYPE = "AWS::RDS::DBSecurityGroupIngress";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		final String name = call.getName();

		final String cidrip = (String) call.getProperty("CIDRIP");
		final String ec2SecGrpName = (String) call
				.getProperty("EC2SecurityGroupName");
		final String ec2SecGrpOwnerId = (String) call
				.getProperty("EC2SecurityGroupOwnerId");
		final String ec2SecGrpId = (String) call
				.getProperty("EC2SecurityGroupId");
		final String secGrpName = (String) call
				.getProperty("DBSecurityGroupName");

		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {
				final RdsDbsecurityGroup dbSecGrpRec = SecurityGroupEntity
						.getSecurityGroup(s, secGrpName, ac.getId());

				if (dbSecGrpRec == null) {
					throw RDSQueryFaults.DBSecurityGroupNotFound();
				}

				if (cidrip != null && !cidrip.equals("")) {
					final RdsIPRangeBean iprb = new RdsIPRangeBean(dbSecGrpRec
							.getId(), cidrip);
					s.save(iprb);
				}

				if (ec2SecGrpName != null && !ec2SecGrpName.equals("")) {
					final RdsEC2SecurityGroupBean egb = new RdsEC2SecurityGroupBean(
							dbSecGrpRec.getId(), ec2SecGrpName, ec2SecGrpId,
							ec2SecGrpOwnerId);
					dbSecGrpRec.getEC2SecGroupBean(s).add(egb);
					s.save(egb);
				}
				s.save(dbSecGrpRec);

				final ResourcesBean rb = getResourceBean(s);
				final Map<String, Object> map = MapUtil.create(
						"DBSecurityGroupName", secGrpName, "CIDRIP", cidrip,
						"Ec2SecurityGroupName", ec2SecGrpName,
						"Ec2SecurityGroupOwnerId", ec2SecGrpOwnerId);
				final String js = JsonUtil.toJsonStringIgnoreNullValues(map);
				rb.setResourceData(js);
				s.save(rb);
				return null;
			}
		});

		final DBSecurityGroupIngressType ret = new DBSecurityGroupIngressType();
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
		final String cidrip = map != null && map.containsKey("CIDRIP") ? (String) map
				.get("CIDRIP") : "";

		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {
				final RdsDbsecurityGroup dbSecGrpRec = SecurityGroupEntity
						.getSecurityGroup(s, secGrpName, ac.getId());

				if (dbSecGrpRec == null) {
					throw RDSQueryFaults.DBSecurityGroupNotFound();
				}
				if (cidrip != null && !cidrip.equals("")) {
					// IPRange is identified by dbGrpName, userID, cidrip
					SecurityGroupEntity.deleteIPRange(s, dbSecGrpRec,
							secGrpName, cidrip);
				}

				if (ec2SecGrpName != null && !ec2SecGrpName.equals("")) {
					// EC2SecurityGroup is identified by dbGrpName, ec2GrpName,
					// userID
					SecurityGroupEntity.deleteEc2Grp(s, dbSecGrpRec,
							secGrpName, ec2SecGrpName, ec2SecGrpOwnerId);
				}
				return null;
			}
		});
		logger.debug("SecurityGroupIngress deleted " + name);
		return null;
	}
}
