package com.msi.tough.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "as_group")
public class ASGroupBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "user_id")
	private long userId;

	@Column(name = "name")
	private String name;

	@Column(name = "created_time")
	private Date createdTime;

	@Column(name = "cooldown")
	private long cooldown;

	@Column(name = "launchConfig")
	private String launchConfig;

	@Column(name = "max_sz")
	private long maxSz;

	@Column(name = "min_sz")
	private long minSz;

	@Column(name = "capacity")
	private long capacity;

	@Column(name = "inst_del")
	private long instDel;

	private String avzones;

	private String loadBalancers;

	private String terminationPolicies;

	private String instances;

	@Column(name = "stack_id")
	private String stackId;

	@Column(name = "cooldown_time")
	private Date cooldownTime;

	@Column(name = "suspend")
	private String suspend;

	@Column(name = "arn")
	private String arn;

	@Column(name = "terminate_instance")
	private String terminateInstance;

	@Column(name = "reduce_capacity")
	private Boolean reduceCapacity;

	@Column(name = "health_check_grace_period")
	private Integer healthCheckGracePeriod;

	@Column(name = "health_check_type")
	private String healthCheckType;

	public String getArn() {
		return arn;
	}

	public String getAvzones() {
		return avzones;
	}

	public long getCapacity() {
		return capacity;
	}

	public long getCooldown() {
		return cooldown;
	}

	public Date getCooldownTime() {
		return cooldownTime;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public Integer getHealthCheckGracePeriod() {
		return healthCheckGracePeriod;
	}

	public String getHealthCheckType() {
		return healthCheckType;
	}

	public long getId() {
		return id;
	}

	public String getInstances() {
		return instances;
	}

	public long getInstDel() {
		return instDel;
	}

	public String getLaunchConfig() {
		return launchConfig;
	}

	public String getLoadBalancers() {
		return loadBalancers;
	}

	public long getMaxSz() {
		return maxSz;
	}

	public long getMinSz() {
		return minSz;
	}

	public String getName() {
		return name;
	}

	public Boolean getReduceCapacity() {
		return reduceCapacity;
	}

	public String getStackId() {
		return stackId;
	}

	public String getSuspend() {
		return suspend;
	}

	public String getTerminateInstance() {
		return terminateInstance;
	}

	public String getTerminationPolicies() {
		return terminationPolicies;
	}

	public long getUserId() {
		return userId;
	}

	public void setArn(final String arn) {
		this.arn = arn;
	}

	public void setAvzones(final String avzones) {
		this.avzones = avzones;
	}

	public void setCapacity(final long capacity) {
		this.capacity = capacity;
	}

	public void setCooldown(final long cooldown) {
		this.cooldown = cooldown;
	}

	public void setCooldownTime(final Date cooldownTime) {
		this.cooldownTime = cooldownTime;
	}

	public void setCreatedTime(final Date createdTime) {
		this.createdTime = createdTime;
	}

	public void setHealthCheckGracePeriod(final Integer healthCheckGracePeriod) {
		this.healthCheckGracePeriod = healthCheckGracePeriod;
	}

	public void setHealthCheckType(final String healthCheckType) {
		this.healthCheckType = healthCheckType;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setInstances(final String instances) {
		this.instances = instances;
	}

	public void setInstDel(final long instDel) {
		this.instDel = instDel;
	}

	public void setLaunchConfig(final String launchConfig) {
		this.launchConfig = launchConfig;
	}

	public void setLoadBalancers(final String loadBalancers) {
		this.loadBalancers = loadBalancers;
	}

	public void setMaxSz(final long maxSz) {
		this.maxSz = maxSz;
	}

	public void setMinSz(final long minSz) {
		this.minSz = minSz;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setReduceCapacity(final Boolean reduceCapacity) {
		this.reduceCapacity = reduceCapacity;
	}

	public void setStackId(final String stackId) {
		this.stackId = stackId;
	}

	public void setSuspend(final String suspend) {
		this.suspend = suspend;
	}

	public void setTerminateInstance(final String terminateInstance) {
		this.terminateInstance = terminateInstance;
	}

	public void setTerminationPolicies(final String terminationPolicies) {
		this.terminationPolicies = terminationPolicies;
	}

	public void setUserId(final long userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "'id':" + id + ", 'name':" + name + ", 'createdTime':"
				+ createdTime + ", 'cooldown':" + cooldown
				+ ", 'launchConfig':" + launchConfig + ", 'maxSz':" + maxSz
				+ ", 'minSz':" + minSz + ", 'capacity':" + capacity;
	}

}
