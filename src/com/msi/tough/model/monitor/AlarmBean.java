package com.msi.tough.model.monitor;

import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.Session;

import com.msi.tough.utils.Constants;

@Entity
@Table(name = "alarm")
public class AlarmBean implements Constants {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	// alarm enabled
	private Boolean enabled;
	// action names
	private String actionNames;
	private String description;
	// alarm name
	private String alarmName;
	// comparison operator
	// GreaterThanOrEqualToThreshold, GreaterThanThreshold,
	// LessThanThreshold and LessThanOrEqualToThreshold
	private String comparator;
	// dimensions
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "alarm_dimension", joinColumns = { @JoinColumn(name = "alarm_id") }, inverseJoinColumns = { @JoinColumn(name = "dimension_id") })
	private Set<DimensionBean> dimensions;

	private BigInteger evaluationPeriods;
	// insufficient data actions
	private String insufficientDataActions;
	// metric name
	private String metricName;
	// namespace
	private String namespace;
	// ok Actions
	private String okActions;
	// period
	private BigInteger period;
	// statistic
	private String statistic;
	// threshold
	private double threshold;
	// unit
	private String unit;
	// alarm state : Possible values are OK, ALARM, or INSUFFICIENT_DATA
	private String state;
	// reason for the alarm state being set
	private String stateReason;
	// reason for the alarm state to be set in JSON
	private String stateReasonData;
	// // Same as Amazon Resource Name
	// private String resourceName;

	// account Id of the user who set the alarm.
	private long userId;
	private Date lastUpdate;
	private Date periodStart;

	public String getActionNames() {
		return actionNames;
	}

	public String getAlarmName() {
		return alarmName;
	}

	public String getComparator() {
		return comparator;
	}

	public String getDescription() {
		return description;
	}

	public Set<DimensionBean> getDimensions() {
		return dimensions;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public BigInteger getEvaluationPeriods() {
		return evaluationPeriods;
	}

	public Long getId() {
		return id;
	}

	public String getInsufficientDataActions() {
		return insufficientDataActions;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public String getMetricName() {
		return metricName;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getOkActions() {
		return okActions;
	}

	public BigInteger getPeriod() {
		return period;
	}

	public Date getPeriodStart() {
		return periodStart;
	}

	public String getState() {
		return state;
	}

	public String getStateReason() {
		return stateReason;
	}

	public String getStateReasonData() {
		return stateReasonData;
	}

	public String getStatistic() {
		return statistic;
	}

	public double getThreshold() {
		return threshold;
	}

	public String getUnit() {
		return unit;
	}

	public long getUserId() {
		return userId;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void save(final Session session) {
		session.saveOrUpdate(this);
	}

	public void setActionNames(final String actionNames) {
		this.actionNames = actionNames;
	}

	public void setAlarmName(final String alarmName) {
		this.alarmName = alarmName;
	}

	public void setComparator(final String comparator) {
		this.comparator = comparator;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setDimensions(final Set<DimensionBean> dimensions) {
		this.dimensions = dimensions;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public void setEnabled(final Boolean enabled) {
		this.enabled = enabled;
	}

	public void setEvaluationPeriods(final BigInteger evaluationPeriods) {
		this.evaluationPeriods = evaluationPeriods;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setInsufficientDataActions(final String insufficientDataActions) {
		this.insufficientDataActions = insufficientDataActions;
	}

	public void setLastUpdate(final Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void setMetricName(final String metricName) {
		this.metricName = metricName;
	}

	public void setNamespace(final String namespace) {
		this.namespace = namespace;
	}

	public void setOkActions(final String okActions) {
		this.okActions = okActions;
	}

	public void setPeriod(final BigInteger period) {
		this.period = period;
	}

	public void setPeriodStart(final Date periodStart) {
		this.periodStart = periodStart;
	}

	public void setState(final String state) {
		this.state = state;
	}

	public void setStateReason(final String stateReason) {
		this.stateReason = stateReason;
	}

	public void setStateReasonData(final String stateReasonData) {
		this.stateReasonData = stateReasonData;
	}

	public void setStatistic(final String statistic) {
		this.statistic = statistic;
	}

	public void setThreshold(final double threshold) {
		this.threshold = threshold;
	}

	public void setUnit(final String unit) {
		this.unit = unit;
	}

	public void setUserId(final long userId) {
		this.userId = userId;
	}
}
