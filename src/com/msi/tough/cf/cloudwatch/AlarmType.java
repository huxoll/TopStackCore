package com.msi.tough.cf.cloudwatch;

import java.util.List;
import java.util.Map;

import com.msi.tough.cf.CFType;

public class AlarmType extends CFType {
	private String actionsEnabled;
	private List<String> alarmActions;
	private String alarmDescription;
	private String comparisonOperator;
	private List<MetricDimensionType> dimensions;
	private String evaluationPeriods;
	private List<String> insufficientDataActions;
	private String metricName;
	private String namespace;
	private List<String> okActions;
	private String period;
	private String statistic;
	private String threshold;
	private String unit;

	public String getActionsEnabled() {
		return actionsEnabled;
	}

	public List<String> getAlarmActions() {
		return alarmActions;
	}

	public String getAlarmDescription() {
		return alarmDescription;
	}

	@Override
	public Object getAtt(final String key) {
		return super.getAtt(key);
	}

	public String getComparisonOperator() {
		return comparisonOperator;
	}

	public List<MetricDimensionType> getDimensions() {
		return dimensions;
	}

	public String getEvaluationPeriods() {
		return evaluationPeriods;
	}

	public List<String> getInsufficientDataActions() {
		return insufficientDataActions;
	}

	public String getMetricName() {
		return metricName;
	}

	public String getNamespace() {
		return namespace;
	}

	public List<String> getOkActions() {
		return okActions;
	}

	public String getPeriod() {
		return period;
	}

	public String getStatistic() {
		return statistic;
	}

	public String getThreshold() {
		return threshold;
	}

	public String getUnit() {
		return unit;
	}

	@Override
	public String ref() {
		return getName();
	}

	public void setActionsEnabled(final String actionsEnabled) {
		this.actionsEnabled = actionsEnabled;
	}

	public void setAlarmActions(final List<String> alarmActions) {
		this.alarmActions = alarmActions;
	}

	public void setAlarmDescription(final String alarmDescription) {
		this.alarmDescription = alarmDescription;
	}

	public void setComparisonOperator(final String comparisonOperator) {
		this.comparisonOperator = comparisonOperator;
	}

	public void setDimensions(final List<MetricDimensionType> dimensions) {
		this.dimensions = dimensions;
	}

	public void setEvaluationPeriods(final String evaluationPeriods) {
		this.evaluationPeriods = evaluationPeriods;
	}

	public void setInsufficientDataActions(
			final List<String> insufficientDataActions) {
		this.insufficientDataActions = insufficientDataActions;
	}

	public void setMetricName(final String metricName) {
		this.metricName = metricName;
	}

	public void setNamespace(final String namespace) {
		this.namespace = namespace;
	}

	public void setOkActions(final List<String> okActions) {
		this.okActions = okActions;
	}

	public void setPeriod(final String period) {
		this.period = period;
	}

	public void setStatistic(final String statistic) {
		this.statistic = statistic;
	}

	public void setThreshold(final String threshold) {
		this.threshold = threshold;
	}

	public void setUnit(final String unit) {
		this.unit = unit;
	}

	@Override
	public Map<String, Object> toMap() throws Exception {
		final Map<String, Object> map = super.toMap();
		return map;
	}

	@Override
	public String typeAsString() {
		return "AWS::CloudWatch::Alarm";
	}

}
