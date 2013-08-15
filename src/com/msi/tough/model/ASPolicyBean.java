package com.msi.tough.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "as_policy")
public class ASPolicyBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "user_id")
	private long userId;

	@Column(name = "name")
	private String Name;

	@Column(name = "arn")
	private String arn;

	@Column(name = "grp_name")
	private String grpName;

	@Column(name = "adj_type")
	private String adjustmentType;

	@Column(name = "cooldown")
	private int cooldown;

	@Column(name = "scaling_adjustment")
	private int scalingAdjustment;

	@Column(name = "min_adjustment")
	private int minAdjustmentStep;

	@Column(name = "created_date")
	private Date createdDate;

	public String getAdjustmentType() {
		return adjustmentType;
	}

	public String getArn() {
		return arn;
	}

	public int getCooldown() {
		return cooldown;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getGrpName() {
		return grpName;
	}

	public long getId() {
		return id;
	}

	public int getMinAdjustmentStep() {
		return minAdjustmentStep;
	}

	public String getName() {
		return Name;
	}

	public int getScalingAdjustment() {
		return scalingAdjustment;
	}

	public long getUserId() {
		return userId;
	}

	public void setAdjustmentType(final String adjustmentType) {
		this.adjustmentType = adjustmentType;
	}

	public void setArn(final String arn) {
		this.arn = arn;
	}

	public void setCooldown(final int cooldown) {
		this.cooldown = cooldown;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setGrpName(final String grpName) {
		this.grpName = grpName;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setMinAdjustmentStep(final int minAdjustmentStep) {
		this.minAdjustmentStep = minAdjustmentStep;
	}

	public void setName(final String name) {
		Name = name;
	}

	public void setScalingAdjustment(final int scalingAdjustment) {
		this.scalingAdjustment = scalingAdjustment;
	}

	public void setUserId(final long userId) {
		this.userId = userId;
	}

}
