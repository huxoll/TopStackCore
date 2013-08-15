package com.msi.tough.model.elasticache;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author jlomax
 * 
 */
@Entity
@Table(name = "elasticache_parameter")
public class ParameterBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "group_id")
	private long groupId;

	@Column(name = "node_specific", nullable = false)
	private boolean nodeSpecific;

	@Column(name = "allowed_values", nullable = true, length = 256)
	private String allowedValues;

	// Length is NOT from AWS documentation
	@Column(name = "data_type", nullable = true, length = 20)
	private String dataType;

	// Length is NOT from AWS documentation
	@Column(name = "description", nullable = true, length = 256)
	private String description;

	@Column(name = "is_modifiable", nullable = false)
	private boolean isModifiable;

	// Length is NOT from AWS documentation
	@Column(name = "name", nullable = true, length = 256)
	private String name;

	// Length is NOT from AWS documentation
	@Column(name = "parameter_value", nullable = true, length = 256)
	private String parameterValue;

	// Length is NOT from AWS documentation
	@Column(name = "source", nullable = true, length = 20)
	private String source;

	@Column(name = "minimum_engine_version", nullable = false)
	private int minimumEngineVersion;

	public String getAllowedValues() {
		return allowedValues;
	}

	public String getDataType() {
		return dataType;
	}

	public String getDescription() {
		return description;
	}

	public long getGroupId() {
		return groupId;
	}

	public int getId() {
		return id;
	}

	public int getMinimumEngineVersion() {
		return minimumEngineVersion;
	}

	public String getName() {
		return name;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public String getSource() {
		return source;
	}

	public boolean isModifiable() {
		return isModifiable;
	}

	public boolean isNodeSpecific() {
		return nodeSpecific;
	}

	public void setAllowedValues(final String allowedValues) {
		this.allowedValues = allowedValues;
	}

	public void setDataType(final String dataType) {
		this.dataType = dataType;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setGroupId(final long groupId) {
		this.groupId = groupId;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setMinimumEngineVersion(final int minimumEngineVersion) {
		this.minimumEngineVersion = minimumEngineVersion;
	}

	public void setModifiable(final boolean isModifiable) {
		this.isModifiable = isModifiable;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNodeSpecific(final boolean nodeSpecific) {
		this.nodeSpecific = nodeSpecific;
	}

	public void setParameterValue(final String parameterValue) {
		this.parameterValue = parameterValue;
	}

	public void setSource(final String source) {
		this.source = source;
	}

}
