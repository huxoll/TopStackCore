/*
 * AvzoneBean.java
 *
 * MSI Eucalyptus LoadBalancer Project
 * Copyright (C) Momentum SI
 *
 */
package com.msi.tough.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model class for web service object AvailabilityZones. It implements hibernate
 * entity bean
 * <p>
 * Apart from the fields defined in the LoadBalancerDescription following extra
 * fields are maintained:
 * <li>id: database generated recored id</li>
 * </p>
 * 
 * @author raj
 * 
 */
@Entity
@Table(name = "version")
public class VersionBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "user_id")
	private long userId;

	@Column(name = "vesion")
	private String version;

	@Column(name = "application_name")
	private String applicationName;

	@Column(name = "description")
	private String desc;

	@Column(name = "auto_deploy")
	private Boolean autoDeploy;

	@Column(name = "s3bucket")
	private String s3bucket;

	@Column(name = "s3key")
	private String s3key;

	@Column(name = "created_time")
	private Date createdTime;

	@Column(name = "updated_time")
	private Date updatedTime;

	public String getApplicationName() {
		return applicationName;
	}

	public Boolean getAutoDeploy() {
		return autoDeploy;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public String getDesc() {
		return desc;
	}

	public long getId() {
		return id;
	}

	public String getS3bucket() {
		return s3bucket;
	}

	public String getS3key() {
		return s3key;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public long getUserId() {
		return userId;
	}

	public String getVersion() {
		return version;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public void setAutoDeploy(final Boolean autoDeploy) {
		this.autoDeploy = autoDeploy;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public void setDesc(final String desc) {
		this.desc = desc;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setS3bucket(final String s3bucket) {
		this.s3bucket = s3bucket;
	}

	public void setS3key(final String s3key) {
		this.s3key = s3key;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

}
