package com.msi.tough.engine.aws.cloudwatch;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.cloudwatch.AlarmType;
import com.msi.tough.cf.cloudwatch.MetricDimensionType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.model.monitor.AlarmBean;
import com.msi.tough.model.monitor.DimensionBean;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.CWUtil;

public class Alarm extends BaseProvider {
	private static Logger logger = Appctx.getLogger(Alarm.class.getName());

	public static String TYPE = "AWS::CloudWatch::Alarm";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {
				final String name = call.getName();

				final String actionsEnabled = (String) call
						.getProperty("ActionsEnabled");
				final List<String> alarmActions = CFUtil.toStringList(call
						.getProperty("AlarmActions"));
				final String alarmDescription = (String) call
						.getProperty("AlarmDescription");
				final String comparisonOperator = (String) call
						.getProperty("ComparisonOperator");
				final List<MetricDimensionType> dimensions = CWUtil
						.toMetricDimensionList(call.getProperty("Dimensions"));
				final String evaluationPeriods = (String) call
						.getProperty("EvaluationPeriods");
				final List<String> insufficientDataActions = CFUtil
						.toStringList(call
								.getProperty("InsufficientDataActions"));
				final String metricName = (String) call
						.getProperty("MetricName");
				final String namespace = (String) call.getProperty("Namespace");
				final List<String> okActions = CFUtil.toStringList(call
						.getProperty("OKActions"));
				final String period = (String) call.getProperty("Period");
				final String statistic = (String) call.getProperty("Statistic");
				final String threshold = (String) call.getProperty("Threshold");
				final String unit = (String) call.getProperty("Unit");

				final AlarmBean b = new AlarmBean();
				b.setUserId(ac.getId());
				final CommaObject calarm = new CommaObject(alarmActions);
				b.setActionNames(calarm.toString());
				b.setAlarmName(name);
				b.setDescription(alarmDescription);
				b.setComparator(comparisonOperator);
				b.setEnabled(Boolean.parseBoolean(actionsEnabled));
				b.setEvaluationPeriods(new BigInteger(evaluationPeriods));
				final CommaObject cin = new CommaObject(insufficientDataActions);
				b.setInsufficientDataActions(cin.toString());
				b.setLastUpdate(new Date());
				b.setMetricName(metricName);
				b.setNamespace(namespace);
				final CommaObject cok = new CommaObject(okActions);
				b.setOkActions(cok.toString());
				b.setPeriod(new BigInteger(period));
				b.setStatistic(statistic);
				b.setThreshold(Double.parseDouble(threshold));
				b.setUnit(unit);
				s.save(b);

				if (dimensions != null) {
					final Set<DimensionBean> dims = new HashSet<DimensionBean>();
					for (final MetricDimensionType d : dimensions) {
						final DimensionBean db = CWUtil.getDimensionBean(s,
								ac.getId(), d.getName(), d.getValue(), false);
						dims.add(db);
					}
					b.setDimensions(dims);
				}
				s.save(b);

				return null;
			}
		});
		final AlarmType ret = new AlarmType();
		ret.setAcId(ac.getId());
		return ret;
	}

	@Override
	public Resource delete0(final CallStruct call) throws Exception {
		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {
				final AccountType ac = call.getAc();
				final String name = call.getPhysicalId();
				logger.debug("Deleting " + ac.getId() + " " + name);
				final AlarmBean g = CWUtil.getAlarmBean(s, ac.getId(), name);
				g.setDimensions(null);
				s.save(g);
				s.delete(g);
				return null;
			}
		});
		return null;
	}
}
