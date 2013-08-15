package com.msi.tough.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "resources")
public class ResourcesBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private long userId;
	private String name;
	private String availabilityZone;
	private String physicalId;
	private String description;
	private String status;
	private String statusReason;
	private String type;
	private String stackId;
	private String parentId;
	private Date createdDate;
	private Date updatedDate;
	private Integer noWait;
	private String waitHook;
	private String resourceData;
	private String failHook;

	public String getAvailabilityZone() {
		return availabilityZone;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getDescription() {
		return description;
	}

	public String getFailHook() {
		return failHook;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Integer getNoWait() {
		return noWait;
	}

	public String getParentId() {
		return parentId;
	}

	public String getPhysicalId() {
		return physicalId;
	}

	public String getResourceData() {
		return resourceData;
	}

	public String getStackId() {
		return stackId;
	}

	public String getStatus() {
		return status;
	}

	public String getStatusReason() {
		return statusReason;
	}

	public String getType() {
		return type;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public long getUserId() {
		return userId;
	}

	public String getWaitHook() {
		return waitHook;
	}

	public void setAvailabilityZone(final String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setFailHook(final String failHook) {
		this.failHook = failHook;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNoWait(final Integer noWait) {
		this.noWait = noWait;
	}

	public void setParentId(final String parentId) {
		this.parentId = parentId;
	}

	public void setPhysicalId(final String physicalId) {
		this.physicalId = physicalId;
	}

	public void setResourceData(final String resourceData) {
		this.resourceData = resourceData;
	}

	public void setStackId(final String stackId) {
		this.stackId = stackId;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public void setStatusReason(final String statusReason) {
		this.statusReason = statusReason;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public void setUpdatedDate(final Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public void setUserId(final long userId) {
		this.userId = userId;
	}

	public void setWaitHook(final String waitHook) {
		this.waitHook = waitHook;
	}

}
