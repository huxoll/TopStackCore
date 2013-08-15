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
@Table(name = "environment")
public class EnvironmentBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "user_id")
	private long userId;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String desc;

	@Column(name = "dns_prefix")
	private String dnsPrefix;

	@Column(name = "url")
	private String url;

	@Column(name = "stack")
	private String stack;

	@Column(name = "template")
	private String template;

	// TODO add one-to-one relationship back to ApplicationBean
	@Column(name = "application_name")
	private String applicationName;

	@Column(name = "version")
	private String version;

	@Column(name = "created_time")
	private Date createdTime;

	@Column(name = "updated_time")
	private Date updatedTime;

	@Column(name = "status")
	private String status;

	@Column(name = "health")
	private String health;

	@Column(name = "env_id")
	private String envId;

	@Column(name = "resources_stack")
	private String resourcesStack;

	@Column(name = "databag")
	private String databag;

	@Column(name = "as_group")
	private String asGroup;

	@Column(name = "launch_config")
	private String launchConfig;

	@Column(name = "loadbalancer")
	private String loadBalancer;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "env_configs", joinColumns = @JoinColumn(name = "env_id"), inverseJoinColumns = @JoinColumn(name = "config_id"))
	private Set<ConfigBean> configs;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "env_remove_configs", joinColumns = @JoinColumn(name = "env_id"), inverseJoinColumns = @JoinColumn(name = "config_id"))
	private Set<ConfigBean> removeConfigs;

	public String getApplicationName() {
		return applicationName;
	}

	public String getAsGroup() {
		return asGroup;
	}

	public Set<ConfigBean> getConfigs() {
		return configs;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public String getDatabag() {
		return databag;
	}

	public String getDesc() {
		return desc;
	}

	public String getDnsPrefix() {
		return dnsPrefix;
	}

	public String getEnvId() {
		return envId;
	}

	public String getHealth() {
		return health;
	}

	public long getId() {
		return id;
	}

	public String getLaunchConfig() {
		return launchConfig;
	}

	public String getLoadBalancer() {
		return loadBalancer;
	}

	public String getName() {
		return name;
	}

	public Set<ConfigBean> getRemoveConfigs() {
		return removeConfigs;
	}

	public String getResourcesStack() {
		return resourcesStack;
	}

	public String getStack() {
		return stack;
	}

	public String getStatus() {
		return status;
	}

	public String getTemplate() {
		return template;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public String getUrl() {
		return url;
	}

	public long getUserId() {
		return userId;
	}

	public String getVersion() {
		return version;
	}

	public void setApplicationName(final String applicationName) {
		this.applicationName = applicationName;
	}

	public void setAsGroup(final String asGroup) {
		this.asGroup = asGroup;
	}

	public void setConfigs(final Set<ConfigBean> configs) {
		this.configs = configs;
	}

	public void setCreatedTime(final Date createdTime) {
		this.createdTime = createdTime;
	}

	public void setDatabag(final String databag) {
		this.databag = databag;
	}

	public void setDesc(final String desc) {
		this.desc = desc;
	}

	public void setDnsPrefix(final String dnsPrefix) {
		this.dnsPrefix = dnsPrefix;
	}

	public void setEnvId(final String envId) {
		this.envId = envId;
	}

	public void setHealth(final String health) {
		this.health = health;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setLaunchConfig(final String launchConfig) {
		this.launchConfig = launchConfig;
	}

	public void setLoadBalancer(final String loadBalancer) {
		this.loadBalancer = loadBalancer;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setRemoveConfigs(final Set<ConfigBean> removeConfigs) {
		this.removeConfigs = removeConfigs;
	}

	public void setResourcesStack(final String resourcesStack) {
		this.resourcesStack = resourcesStack;
	}

	public void setStack(final String stack) {
		this.stack = stack;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public void setTemplate(final String template) {
		this.template = template;
	}

	public void setUpdatedTime(final Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public void setUserId(final long userId) {
		this.userId = userId;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

}
