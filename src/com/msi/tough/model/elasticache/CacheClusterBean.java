package com.msi.tough.model.elasticache;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author raj
 * 
 */
@Entity
@Table(name = "elasticache_cluster")
public class CacheClusterBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "acid", nullable = false)
	private long acid;

	@Column(name = "name", length = 20, nullable = false)
	String name;

	@Column(name = "node_count", nullable = false)
	private Integer nodeCount;

	@Column(name = "new_node_count")
	private Integer newNodeCount;

	@Column(name = "auto_version_upgrade", nullable = false)
	private Boolean autoMinorVersionUpgrade = true;

	@Column(name = "status", length = 30)
	private String cacheClusterStatus;

	@Column(name = "engine", length = 12)
	private String engine;

	@Column(name = "engine_version", length = 12)
	private String engineVersion;

	@Column(name = "new_engine_version", length = 12)
	private String newEngineVersion;

	@Column(name = "notification_topic_arn", length = 256, nullable = true)
	private String notificationTopicArn;

	@Column(name = "notification_topic_status", length = 8, nullable = true)
	private String notificationTopicStatus;

	@Column(name = "port", nullable = true)
	private Integer port;

	@Column(name = "preferred_availability_zone", nullable = true)
	private String preferredAvailabilityZone;

	@Column(name = "preferred_maintenance_window", nullable = true)
	private String preferredMaintenanceWindow;

	@Column(name = "stack_id", nullable = false)
	private String stackId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_time", nullable = false)
	private Date createdTime;

	@Column(name = "node_type_id", nullable = false)
	private Long nodeTypeId;

	@Column(name = "parameter_group_id", nullable = false)
	private Long parameterGroupId;

	@Column(name = "parameter_group_status")
	private String parameterGroupStatus;

	@Column(name = "security_groups", nullable = false)
	private String securityGroups;

	public long getAcid() {
		return acid;
	}

	public Boolean getAutoMinorVersionUpgrade() {
		return autoMinorVersionUpgrade;
	}

	public String getCacheClusterStatus() {
		return cacheClusterStatus;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public String getEngine() {
		return engine;
	}

	public String getEngineVersion() {
		return engineVersion;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getNewEngineVersion() {
		return newEngineVersion;
	}

	public Integer getNewNodeCount() {
		return newNodeCount;
	}

	public Integer getNodeCount() {
		return nodeCount;
	}

	public Long getNodeTypeId() {
		return nodeTypeId;
	}

	public String getNotificationTopicArn() {
		return notificationTopicArn;
	}

	public String getNotificationTopicStatus() {
		return notificationTopicStatus;
	}

	public Long getParameterGroupId() {
		return parameterGroupId;
	}

	public String getParameterGroupStatus() {
		return parameterGroupStatus;
	}

	public Integer getPort() {
		return port;
	}

	public String getPreferredAvailabilityZone() {
		return preferredAvailabilityZone;
	}

	public String getPreferredMaintenanceWindow() {
		return preferredMaintenanceWindow;
	}

	public String getSecurityGroups() {
		return securityGroups;
	}

	public String getStackId() {
		return stackId;
	}

	public void setAcid(final long acid) {
		this.acid = acid;
	}

	public void setAutoMinorVersionUpgrade(final Boolean autoMinorVersionUpgrade) {
		this.autoMinorVersionUpgrade = autoMinorVersionUpgrade;
	}

	public void setCacheClusterStatus(final String cacheClusterStatus) {
		this.cacheClusterStatus = cacheClusterStatus;
	}

	public void setCreatedTime(final Date createdTime) {
		this.createdTime = createdTime;
	}

	public void setEngine(final String engine) {
		this.engine = engine;
	}

	public void setEngineVersion(final String engineVersion) {
		this.engineVersion = engineVersion;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNewEngineVersion(final String newEngineVersion) {
		this.newEngineVersion = newEngineVersion;
	}

	public void setNewNodeCount(final Integer newNodeCount) {
		this.newNodeCount = newNodeCount;
	}

	public void setNodeCount(final Integer nodeCount) {
		this.nodeCount = nodeCount;
	}

	public void setNodeTypeId(final Long nodeTypeId) {
		this.nodeTypeId = nodeTypeId;
	}

	public void setNotificationTopicArn(final String notificationTopicArn) {
		this.notificationTopicArn = notificationTopicArn;
	}

	public void setNotificationTopicStatus(final String notificationTopicStatus) {
		this.notificationTopicStatus = notificationTopicStatus;
	}

	public void setParameterGroupId(final Long parameterGroupId) {
		this.parameterGroupId = parameterGroupId;
	}

	public void setParameterGroupStatus(final String parameterGroupStatus) {
		this.parameterGroupStatus = parameterGroupStatus;
	}

	public void setPort(final Integer port) {
		this.port = port;
	}

	public void setPreferredAvailabilityZone(
			final String preferredAvailabilityZone) {
		this.preferredAvailabilityZone = preferredAvailabilityZone;
	}

	public void setPreferredMaintenanceWindow(
			final String preferredMaintenanceWindow) {
		this.preferredMaintenanceWindow = preferredMaintenanceWindow;
	}

	public void setSecurityGroups(final String securityGroups) {
		this.securityGroups = securityGroups;
	}

	public void setStackId(final String stackId) {
		this.stackId = stackId;
	}

}
