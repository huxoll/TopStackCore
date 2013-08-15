package com.msi.tough.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "as_activity")
public class ASActivityLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "user_id")
	private long userId;

	@Column(name = "grp_id")
	private long grpId;

	@Column(name = "cause")
	private String cause;

	@Column(name = "description")
	private String description;

	@Column(name = "details")
	private String details;

	@Column(name = "endtime")
	private Date endTime;

	@Column(name = "progress")
	private Integer progress;

	@Column(name = "starttime")
	private Date startTime;

	@Column(name = "statuscode")
	private String statusCode;

	@Column(name = "statusmsg")
	private String statusMsg;

	public String getCause() {
		return cause;
	}

	public String getDescription() {
		return description;
	}

	public String getDetails() {
		return details;
	}

	public Date getEndTime() {
		return endTime;
	}

	public long getGrpId() {
		return grpId;
	}

	public long getId() {
		return id;
	}

	public Integer getProgress() {
		return progress;
	}

	public Date getStartTime() {
		return startTime;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public String getStatusMsg() {
		return statusMsg;
	}

	public long getUserId() {
		return userId;
	}

	public void setCause(final String cause) {
		this.cause = cause;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setDetails(final String details) {
		this.details = details;
	}

	public void setEndTime(final Date endTime) {
		this.endTime = endTime;
	}

	public void setGrpId(final long grpId) {
		this.grpId = grpId;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setProgress(final Integer progress) {
		this.progress = progress;
	}

	public void setStartTime(final Date startTime) {
		this.startTime = startTime;
	}

	public void setStatusCode(final String statusCode) {
		this.statusCode = statusCode;
	}

	public void setStatusMsg(final String statusMsg) {
		this.statusMsg = statusMsg;
	}

	public void setUserId(final long userId) {
		this.userId = userId;
	}

}
