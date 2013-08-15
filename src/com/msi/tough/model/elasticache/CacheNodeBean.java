package com.msi.tough.model.elasticache;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "elasticache_node")
public class CacheNodeBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "cluster_id", nullable = false)
	private long cacheCluster;

	@Column(name = "created_time", nullable = false)
	private Date createdTime;

	@Column(name = "status", length = 32)
	private String nodeStatus;

	@Column(name = "parameter_group_status", length = 15)
	private String parameterGroupStatus;

	@Column(name = "elasticache_node_address", length = 256)
	private String address;

	@Column(name = "instance_id", length = 256)
	private String instaceId;

	public String getAddress() {
		return address;
	}

	public long getCacheCluster() {
		return cacheCluster;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public int getId() {
		return id;
	}

	public String getInstaceId() {
		return instaceId;
	}

	public String getNodeStatus() {
		return nodeStatus;
	}

	public String getParameterGroupStatus() {
		return parameterGroupStatus;
	}

	public void setAddress(final String address) {
		this.address = address;
	}

	public void setCacheCluster(final long cacheCluster) {
		this.cacheCluster = cacheCluster;
	}

	public void setCreatedTime(final Date createdTime) {
		this.createdTime = createdTime;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setInstaceId(final String instaceId) {
		this.instaceId = instaceId;
	}

	public void setNodeStatus(final String nodeStatus) {
		this.nodeStatus = nodeStatus;
	}

	public void setParameterGroupStatus(final String parameterGroupStatus) {
		this.parameterGroupStatus = parameterGroupStatus;
	}

}
