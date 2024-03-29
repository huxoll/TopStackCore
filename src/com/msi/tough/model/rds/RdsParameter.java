package com.msi.tough.model.rds;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * RdsParameter generated by hbm2java
 */

@Entity
@Table(name = "rds_parameter")
public class RdsParameter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="rds_parameter_id")
	private long id;
	
	@ManyToOne
	@JoinColumn(name="rds_parameter_group_id", nullable=false)
	private RdsDbparameterGroup rdsParamGroup;
	
	@Column(name="parameter_name")
	private String parameterName;
	
	// AWS 5.1 parameter 271 chars long
	@Column(name="parameter_value", length=320)
	private String parameterValue;
	
	@Column(name="description")
	private String description;
	
	@Column(name="source")
	private String source;
	
	@Column(name="data_type")
	private String dataType;
	
	@Column(name="allowed_values", length=512)
	private String allowedValues;
	
	@Column(name="applied_method")
	private String applyMethod;
	
	@Column(name="apply_type")
	private String applyType;
	
	@Column(name="is_modifiable")
	private Boolean isModifiable;
	
	@Column(name="minimum_engine_version")
	private String minimumEngineVersion;
	
	public RdsParameter(){}
	
	public RdsDbparameterGroup getRdsParamGroup() {
		return rdsParamGroup;
	}

	public void setRdsParamGroup(RdsDbparameterGroup rdsParamGroup) {
		this.rdsParamGroup = rdsParamGroup;
	}

	public RdsParameter (RdsDbparameterGroup rdsParamGroup){
		this.rdsParamGroup = rdsParamGroup;
	}

	public String getAllowedValues() {
		return allowedValues;
	}

	public String getApplyMethod() {
		return applyMethod;
	}

	public void setApplyMethod(String applyMethod) {
		this.applyMethod = applyMethod;
	}

	public String getApplyType() {
		return applyType;
	}

	public String getDataType() {
		return dataType;
	}

	public RdsDbparameterGroup getParamGroup() {
		return rdsParamGroup;
	}

	public String getDescription() {
		return description;
	}

	public long getId() {
		return id;
	}

	public Boolean getIsModifiable() {
		return isModifiable;
	}

	public String getMinimumEngineVersion() {
		return minimumEngineVersion;
	}

	public String getParameterName() {
		return parameterName;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public String getSource() {
		return source;
	}

	public void setAllowedValues(String allowedValues) {
		this.allowedValues = allowedValues;
	}

	/*public void setApplyMethod(String applyMethod) {
		this.applyMethod = applyMethod;
	}*/

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setIsModifiable(Boolean isModifiable) {
		this.isModifiable = isModifiable;
	}

	public void setMinimumEngineVersion(String minimumEngineVersion) {
		this.minimumEngineVersion = minimumEngineVersion;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	public String toString(){
		return parameterName + ": (value = " + this.parameterValue + "), (description = " + this.description 
		+ "), (source = " + this.source + "), (dataType = " + this.dataType + "), (allowedValues = " + this.allowedValues 
		+ "), (applyType = " + this.applyType + "), (isModifiable = " + this.isModifiable + "), (minimumEngineVersion = " + this.minimumEngineVersion
		+ ")";
	}
}
