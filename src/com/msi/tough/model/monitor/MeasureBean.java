/**
 * 
 */
package com.msi.tough.model.monitor;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.Session;
import org.hibernate.annotations.Index;

import com.amazonaws.util.DateUtils;

/**
 * A measure is an observed value with a name, a set of dimensions, a namespace,
 * a unit, and a timestamp. Input to CloudWatch. The raw data that's monitored.
 * A window of times worth of Measures are aggregated into a Metric. - unique
 * name - 1+ dimensions - timestamp - a unit (value) - a namespace / the service
 * recorded
 * 
 * @author heathm
 * 
 */
@Entity
@Table(name = "measures")
@org.hibernate.annotations.Table(appliesTo = "measures", indexes = { @Index(name = "measure_idx", columnNames = {
		"timestmp", "name" }) })
public class MeasureBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "timestmp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp; // ISO 8601 format possibly represent this another
							// way internally in the future that's more
							// convenient for calculation.
	@Column(name = "name")
	private String name;

	@Column(name = "namespace")
	private String namespace; // identifies the service that this is recorded
								// from (eg. EC2/AWS)

	@Column(name = "unit")
	private String unit; // Possibly make this an enum of Seconds, Percent,
							// Bytes, Bits, Count, Bytes, Bits/Second,
							// Count/Second, and None.
	@Column(name = "value")
	private Double value;

	// dimensions
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "measure_dimension", joinColumns = { @JoinColumn(name = "measure_id") }, inverseJoinColumns = { @JoinColumn(name = "dimension_id") })
	private Set<DimensionBean> dimensions;

	public MeasureBean() {
	}

	//
	// public MeasureBean(final String instanceId, final String name,
	// final String namespace, final String unit, final String value,
	// final long millis) {
	// this.instanceId = instanceId;
	// this.name = name;
	// this.namespace = namespace;
	// // dimensions.addAll(dimensions);
	// this.unit = unit;
	// this.value = value;
	// milliseconds = millis;
	// final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	// cal.setTimeInMillis(millis);
	// /*
	// * SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	// * // try { // timestamp = df.parse(new
	// * DateUtils().formatIso8601Date(cal.getTime())); // } catch
	// * (ParseException e) {
	// * e.printStackTrace(); }
	// */
	// // cal.add(Calendar.MILLISECOND,
	// // cal.getTimeZone().getOffset(cal.getTime().getTime())*-1);
	// timestamp = cal.getTime();
	// }

	public Set<DimensionBean> getDimensions() {
		return dimensions;
	}

	public long getId() {
		return id;
	}

	public String getISO8601Timestamp() {
		return new DateUtils().formatIso8601Date(timestamp);
		// return DateUtils.getISO8601Date(timestamp);
	}

	public String getName() {
		return name;
	}

	public String getNamespace() {
		return namespace;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getUnit() {
		return unit;
	}

	public Double getValue() {
		return value;
	}

	public void save(final Session session) {
		// dimensions.save(session);
		session.saveOrUpdate(this);
	}

	public void setDimensions(final Set<DimensionBean> dimensions) {
		this.dimensions = dimensions;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNamespace(final String namespace) {
		this.namespace = namespace;
	}

	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public void setUnit(final String unit) {
		this.unit = unit;
	}

	public void setValue(final Double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("Timestamp: " + timestamp + ", ");
		sb.append("Name: " + name + ", ");
		sb.append("Namespace: " + namespace + ", ");
		sb.append("Unit: " + unit + ", ");
		sb.append("Value: " + value + ", ");
		final String dims = "";
		// if (dimensions != null && dimensions.getDimensions() != null) {
		// for (final DimensionBean dim : dimensions.getDimensions()) {
		// dims += dim.getValue() + ":";
		// }
		// }
		sb.append("Dimensions: " + dims + ", ");
		sb.append("}");
		return sb.toString();
	}
}
