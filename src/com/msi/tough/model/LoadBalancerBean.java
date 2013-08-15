/*
 * LoadBalancerDescriptionBean.java
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
 * Model class for web service object LoadBalancerDescription. It implements
 * CRUD methods and it also implements the hibernate entity bean.
 * <p>
 * Apart from the fields defined in the LoadBalancerDescription following extra
 * fields are maintained:
 * <li>id: database generated recored id</li>
 * <li>status: init->created. Init=created in DB. created=Eucalyptus instance is
 * up a and running and configured.</li>
 * <li>ipAddress: ipAddress of the load balancer instance</li>
 * <li>instanceId: Eucalyptus instanceId of the load balancer</li>
 * </p>
 * 
 * @author raj
 * 
 */

@Entity
@Table(name = "loadbalancer")
public class LoadBalancerBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "user_id")
	private long userId;

	@Column(name = "created_time")
	private Date createdTime;

	@Column(name = "lb_name")
	private String loadBalancerName;

	@Column(name = "hc_threshold")
	private long healthyThreshold;

	@Column(name = "hc_interval")
	private long interval;

	@Column(name = "hc_target")
	private String target;

	@Column(name = "uhc_threshold")
	private long unhealthyThreshold;

	@Column(name = "uhc_timeout")
	private long timeout;

	@Column(name = "inst_del")
	private long instDel;

	@Column(name = "lb_status")
	private String lbStatus;

	@Column(name = "instances")
	private String instances;

	@Column(name = "app_cookie")
	private String appCookieStickinessPolicy;

	@Column(name = "lb_cookie")
	private String lbCookieStickinessPolicy;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "lb_listener", joinColumns = @JoinColumn(name = "lb_id"), inverseJoinColumns = @JoinColumn(name = "listener_id"))
	private Set<ListenerBean> listeners;

	@Column(name = "avz")
	private String avzones;

	@Column(name = "lb_instances")
	private String lbInstances;

	@Column(name = "dns")
	private String dnsName;

	@Column(name = "physical_id")
	private String physicalId;

	@Column(name = "stack_id")
	private String stackId;

	@Column(name = "databag")
	private String databag;

	@Column(name = "ec2_sec_group")
	private String ec2SecGroup;

	@Column(name = "inconfig")
	private Boolean inconfig;

	@Column(name = "reconfig")
	private Boolean reconfig;

	@Column(name = "sg_name")
	private String sgName;

	@Column(name = "sg_id")
	private String sgId;

	public String getAppCookieStickinessPolicy() {
		return appCookieStickinessPolicy;
	}

	public String getAvzones() {
		return avzones;
	}

	/**
	 * get load balancer creation time
	 * 
	 * @return
	 */
	public Date getCreatedTime() {
		return createdTime;
	}

	public String getDatabag() {
		return databag;
	}

	public String getDnsName() {
		return dnsName;
	}

	public String getEc2SecGroup() {
		return ec2SecGroup;
	}

	/**
	 * get healthy theshhold
	 * 
	 * @return
	 */
	public long getHealthyThreshold() {
		return healthyThreshold;
	}

	/**
	 * get unique database id for load balancer
	 * 
	 * @return
	 */
	public long getId() {
		return id;
	}

	public Boolean getInconfig() {
		return inconfig;
	}

	public String getInstances() {
		return instances;
	}

	public long getInstDel() {
		return instDel;
	}

	/**
	 * get health check interval
	 * 
	 * @return
	 */
	public long getInterval() {
		return interval;
	}

	public String getLbCookieStickinessPolicy() {
		return lbCookieStickinessPolicy;
	}

	public String getLbInstances() {
		return lbInstances;
	}

	public String getLbStatus() {
		return lbStatus;
	}

	/**
	 * get listeners defined for the load balancer
	 * 
	 * @return
	 */
	public Set<ListenerBean> getListeners() {
		return listeners;
	}

	/**
	 * get load balancer name
	 * 
	 * @return
	 */
	public String getLoadBalancerName() {
		return loadBalancerName;
	}

	public String getPhysicalId() {
		return physicalId;
	}

	public Boolean getReconfig() {
		return reconfig;
	}

	public String getSgId() {
		return sgId;
	}

	public String getSgName() {
		return sgName;
	}

	public String getStackId() {
		return stackId;
	}

	/**
	 * get health check target
	 * 
	 * @return
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * get timeout
	 * 
	 * @return
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * get unhealthy threshhold
	 * 
	 * @return
	 */
	public long getUnhealthyThreshold() {
		return unhealthyThreshold;
	}

	public long getUserId() {
		return userId;
	}

	public void setAppCookieStickinessPolicy(
			final String appCookieStickinessPolicy) {
		this.appCookieStickinessPolicy = appCookieStickinessPolicy;
	}

	public void setAvzones(final String avzones) {
		this.avzones = avzones;
	}

	/**
	 * set load balancer creation time
	 * 
	 * @param cal
	 */
	public void setCreatedTime(final Date cal) {
		createdTime = cal;
	}

	public void setDatabag(final String databag) {
		this.databag = databag;
	}

	public void setDnsName(final String dnsName) {
		this.dnsName = dnsName;
	}

	public void setEc2SecGroup(final String securityGroup) {
		ec2SecGroup = securityGroup;
	}

	/**
	 * set health check helathy threshold
	 * 
	 * @param healthyThreshold
	 */
	public void setHealthyThreshold(final long healthyThreshold) {
		this.healthyThreshold = healthyThreshold;
	}

	/**
	 * set load balancer database id; used by hibernate to return the id created
	 * by database
	 * 
	 * @param id
	 */
	public void setId(final long id) {
		this.id = id;
	}

	public void setInconfig(final Boolean inconfig) {
		this.inconfig = inconfig;
	}

	public void setInstances(final String instances) {
		this.instances = instances;
	}

	public void setInstDel(final long instDel) {
		this.instDel = instDel;
	}

	/**
	 * set health check for interval
	 * 
	 * @param interval
	 */
	public void setInterval(final long interval) {
		this.interval = interval;
	}

	public void setLbCookieStickinessPolicy(
			final String lbCookieStickinessPolicy) {
		this.lbCookieStickinessPolicy = lbCookieStickinessPolicy;
	}

	public void setLbInstances(final String lbInstances) {
		this.lbInstances = lbInstances;
	}

	public void setLbStatus(final String lbStatus) {
		this.lbStatus = lbStatus;
	}

	/**
	 * set listeners
	 * 
	 * @param listeners
	 */
	public void setListeners(final Set<ListenerBean> listeners) {
		this.listeners = listeners;
	}

	/**
	 * set load balancer name
	 * 
	 * @param loadBalancerName
	 */
	public void setLoadBalancerName(final String loadBalancerName) {
		this.loadBalancerName = loadBalancerName;
	}

	public void setPhysicalId(final String physicalId) {
		this.physicalId = physicalId;
	}

	public void setReconfig(final Boolean reconfig) {
		this.reconfig = reconfig;
	}

	public void setSgId(final String sgId) {
		this.sgId = sgId;
	}

	public void setSgName(final String sgName) {
		this.sgName = sgName;
	}

	public void setStackId(final String stackId) {
		this.stackId = stackId;
	}

	/**
	 * set health check target
	 * 
	 * @param target
	 */
	public void setTarget(final String target) {
		this.target = target;
	}

	/**
	 * set health check timeout
	 * 
	 * @param timeout
	 */
	public void setTimeout(final long timeout) {
		this.timeout = timeout;
	}

	/**
	 * set unhealthy threshold
	 * 
	 * @param unhealthyThreshold
	 */
	public void setUnhealthyThreshold(final long unhealthyThreshold) {
		this.unhealthyThreshold = unhealthyThreshold;
	}

	public void setUserId(final long userId) {
		this.userId = userId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "'id':" + id + ", 'loadBalancerName':" + loadBalancerName
				+ ", 'healthyThreshold':" + healthyThreshold + ", 'interval':"
				+ interval + ", 'target':" + target + ", 'timeout':" + timeout
				+ ", 'unhealthyThreshold':" + unhealthyThreshold;

	}

}
