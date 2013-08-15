package com.msi.tough.model.rds;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * RdsSnapshot generated by hbm2java
 */
@Entity
@Table(name = "rds_snapshot")
public class RdsSnapshot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String dbsnapshotId;
	private long userId;
	private String dbinstanceId;
	private Date snapshotCreateTime;
	private String status;
	private String logPointer;
	private String volumeId;
	private Integer allocatedStorage;
	private String dbinstanceClass;
	private String engine;
	private String engineVersion;
	private String masterUsername;
	private String masterPasswd;
	private String dbparameterGroup;
	private String availabilityZone;
	private Date instanceCreatedTime;
	private String licenseModel;
	private Integer port;
	private String snapshotType;
	

	public String getSnapshotType() {
		return snapshotType;
	}

	public void setSnapshotType(String snapshotType) {
		this.snapshotType = snapshotType;
	}

	public String getEngineVersion() {
		return engineVersion;
	}

	public void setEngineVersion(String engineVersion) {
		this.engineVersion = engineVersion;
	}

	public Date getInstanceCreatedTime() {
		return instanceCreatedTime;
	}

	public void setInstanceCreatedTime(Date instanceCreatedTime) {
		this.instanceCreatedTime = instanceCreatedTime;
	}

	public String getLicenseModel() {
		return licenseModel;
	}

	public void setLicenseModel(String licenseModel) {
		this.licenseModel = licenseModel;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getAvailabilityZone() {
		return availabilityZone;
	}

	public void setAvailabilityZone(String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}

	public String getDbparameterGroup() {
		return dbparameterGroup;
	}

	public void setDbparameterGroup(String dbparameterGroup) {
		this.dbparameterGroup = dbparameterGroup;
	}

	public Integer getAllocatedStorage() {
		return allocatedStorage;
	}

	public void setAllocatedStorage(Integer allocatedStorage) {
		this.allocatedStorage = allocatedStorage;
	}

	public String getDbinstanceClass() {
		return dbinstanceClass;
	}

	public void setDbinstanceClass(String dbinstanceClass) {
		this.dbinstanceClass = dbinstanceClass;
	}

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public String getMasterUsername() {
		return masterUsername;
	}

	public void setMasterUsername(String masterUsername) {
		this.masterUsername = masterUsername;
	}

	public String getMasterPasswd() {
		return masterPasswd;
	}

	public void setMasterPasswd(String masterPasswd) {
		this.masterPasswd = masterPasswd;
	}

	public String getDbinstanceId() {
		return dbinstanceId;
	}

	public String getDbsnapshotId() {
		return dbsnapshotId;
	}

	public long getId() {
		return id;
	}

	public String getLogPointer() {
		return logPointer;
	}

	public Date getSnapshotCreateTime() {
		return snapshotCreateTime;
	}

	public String getStatus() {
		return status;
	}

	public long getUserId() {
		return userId;
	}
	
	public String getVolumeId() {
		return volumeId;
	}

	public void setDbinstanceId(String dbinstanceId) {
		this.dbinstanceId = dbinstanceId;
	}

	public void setDbsnapshotId(String dbsnapshotId) {
		this.dbsnapshotId = dbsnapshotId;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setLogPointer(String logPointer) {
		this.logPointer = logPointer;
	}

	public void setSnapshotCreateTime(Date snapshotCreateTime) {
		this.snapshotCreateTime = snapshotCreateTime;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public void setVolumeId(String vId) {
		this.volumeId = vId;
	}

}