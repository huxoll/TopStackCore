package com.msi.tough.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author raj
 * 
 */
@Entity
@Table(name = "volume")
public class VolumeBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long userId;
	private String volumeId;
	private long size;
	private String status;
	private String availabilityZone;
	private boolean deleteWithInstance;
	private String instanceId;

	public String getAvailabilityZone() {
		return availabilityZone;
	}

	public long getId() {
		return id;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public long getSize() {
		return size;
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

	public boolean isDeleteWithInstance() {
		return deleteWithInstance;
	}

	public void setAvailabilityZone(final String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}

	public void setDeleteWithInstance(final boolean deleteWithInstance) {
		this.deleteWithInstance = deleteWithInstance;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setInstanceId(final String instanceId) {
		this.instanceId = instanceId;
	}

	public void setSize(final long size) {
		this.size = size;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public void setUserId(final long userId) {
		this.userId = userId;
	}

	public void setVolumeId(final String volumeId) {
		this.volumeId = volumeId;
	}

}
