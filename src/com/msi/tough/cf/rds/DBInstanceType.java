package com.msi.tough.cf.rds;

import com.msi.tough.cf.CFType;

public class DBInstanceType extends CFType{
	private String dbName;
	private String allocatedStorage;
	private String dbInstanceClass;
	private String engine;
	private String engineVersion;
	private String masterUsername;
	private String masterUserPassword;
	private String dbSubnetGroupName;
	private String dbSecurityGroups;

	@Override
	public Object ref() {
		return getName();
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getAllocatedStorage() {
		return allocatedStorage;
	}

	public void setAllocatedStorage(String allocatedStorage) {
		this.allocatedStorage = allocatedStorage;
	}

	public String getDbInstanceClass() {
		return dbInstanceClass;
	}

	public void setDbInstanceClass(String dbInstanceClass) {
		this.dbInstanceClass = dbInstanceClass;
	}

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public String getEngineVersion() {
		return engineVersion;
	}

	public void setEngineVersion(String engineVersion) {
		this.engineVersion = engineVersion;
	}

	public String getMasterUsername() {
		return masterUsername;
	}

	public void setMasterUsername(String masterUsername) {
		this.masterUsername = masterUsername;
	}

	public String getMasterUserPassword() {
		return masterUserPassword;
	}

	public void setMasterUserPassword(String masterUserPassword) {
		this.masterUserPassword = masterUserPassword;
	}

	public String getDbSubnetGroupName() {
		return dbSubnetGroupName;
	}

	public void setDbSubnetGroupName(String dbSubnetGroupName) {
		this.dbSubnetGroupName = dbSubnetGroupName;
	}

	public String getDbSecurityGroups() {
		return dbSecurityGroups;
	}

	public void setDbSecurityGroups(String dbSecurityGroups) {
		this.dbSecurityGroups = dbSecurityGroups;
	}
	
	
}
