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
@Table(name = "application")
public class ApplicationBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "user_id")
	private long userId;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String desc;

	@Column(name = "created_time")
	private Date createdTime;

	@Column(name = "updated_time")
	private Date updatedTime;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "app_versions", joinColumns = @JoinColumn(name = "application_id"), inverseJoinColumns = @JoinColumn(name = "version_id"))
	private Set<VersionBean> versions;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "app_templates", joinColumns = @JoinColumn(name = "application_id"), inverseJoinColumns = @JoinColumn(name = "template_id"))
	private Set<ConfigTemplateBean> templates;

	public Date getCreatedTime() {
		return createdTime;
	}

	public String getDesc() {
		return desc;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Set<ConfigTemplateBean> getTemplates() {
		return templates;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public long getUserId() {
		return userId;
	}

	public Set<VersionBean> getVersions() {
		return versions;
	}

	public void setCreatedTime(final Date createdTime) {
		this.createdTime = createdTime;
	}

	public void setDesc(final String desc) {
		this.desc = desc;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setTemplates(final Set<ConfigTemplateBean> templates) {
		this.templates = templates;
	}

	public void setUpdatedTime(final Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setVersions(final Set<VersionBean> versions) {
		this.versions = versions;
	}

}
