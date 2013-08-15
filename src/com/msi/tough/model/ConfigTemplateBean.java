/*
 * AvzoneBean.java
 *
 * MSI Eucalyptus LoadBalancer Project
 * Copyright (C) Momentum SI
 *
 */
package com.msi.tough.model;

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
import javax.persistence.OneToMany;
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
@Table(name = "template")
public class ConfigTemplateBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "user_id")
	private long userId;

	@Column(name = "app_name")
	private String applicationName;
	
	@Column(name = "name")
	private String name;

	@Column(name = "stack")
	private String stack;

	@Column(name = "env_id")
	private String envId;

	@Column(name = "env_name")
	private String envName;
	
	@Column(name = "description")
	private String desc;

	//TODO many to one relationship with the parent (ApplicationBean) needed
	@Column(name = "src_app_name")
	private String srcAppName;

	@Column(name = "src_template")
	private String srcTemplate;

	@Column(name = "created_time")
	private Date createdTime;

	@Column(name = "updated_time")
	private Date updatedTime;

	private String deployStatus;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "template_configs", joinColumns = @JoinColumn(name = "template_id"), inverseJoinColumns = @JoinColumn(name = "config_id"))
	private Set<ConfigBean> configs;

	public Set<ConfigBean> getConfigs() {
		return configs;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public String getDeployStatus() {
		return deployStatus;
	}

	public String getDesc() {
		return desc;
	}

	public String getEnvId() {
		return envId;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSrcAppName() {
		return srcAppName;
	}

	public String getSrcTemplate() {
		return srcTemplate;
	}

	public String getStack() {
		return stack;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getEnvName() {
		return envName;
	}

	public void setEnvName(String envName) {
		this.envName = envName;
	}
	
	public void setConfigs(final Set<ConfigBean> configs) {
		this.configs = configs;
	}

	public void setCreatedTime(final Date createdTime) {
		this.createdTime = createdTime;
	}

	public void setDeployStatus(final String deployStatus) {
		this.deployStatus = deployStatus;
	}

	public void setDesc(final String desc) {
		this.desc = desc;
	}

	public void setEnvId(final String envId) {
		this.envId = envId;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setSrcAppName(final String srcAppName) {
		this.srcAppName = srcAppName;
	}

	public void setSrcTemplate(final String srcTemplate) {
		this.srcTemplate = srcTemplate;
	}

	public void setStack(final String stack) {
		this.stack = stack;
	}

	public void setUpdatedTime(final Date updatedTime) {
		this.updatedTime = updatedTime;
	}

}
