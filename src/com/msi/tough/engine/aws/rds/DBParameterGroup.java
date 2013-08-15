package com.msi.tough.engine.aws.rds;

import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.rds.model.CreateDBParameterGroupRequest;
import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.rds.DBParameterGroupType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.model.rds.RdsDbparameterGroup;
import com.msi.tough.model.rds.RdsParameter;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.rds.ParameterGroupEntity;

public class DBParameterGroup extends BaseProvider {

	private static final Logger logger = Appctx
			.getLogger(DBParameterGroup.class.getName());

	public static String TYPE = "AWS::RDS::DBParameterGroup";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		final String name = call.getName();

		final String family = (String) call.getProperty("Family");
		final String description = (String) call.getProperty("Description");

		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {

				final CreateDBParameterGroupRequest req = new CreateDBParameterGroupRequest();
				req.setDBParameterGroupFamily(family);
				req.setDBParameterGroupName(name);
				req.setDescription(description);
				ParameterGroupEntity.insertParameterGroup(s, req,
						AccountUtil.readAccount(s, ac.getId()));
				return null;
			}
		});

		final DBParameterGroupType ret = new DBParameterGroupType();
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
				final RdsDbparameterGroup pRec = ParameterGroupEntity
						.getParameterGroup(s, name, ac.getId());
				if (pRec == null) {
					throw RDSQueryFaults.DBParameterGroupNotFound();
				}
				final List<RdsParameter> params = pRec.getParameters();
				for (final RdsParameter temp : params) {
					s.delete(temp);
				}
				s.delete(pRec);
				return null;
			}
		});
		logger.debug("DBParameterGroup deleted " + name);
		return null;
	}
}
