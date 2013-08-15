package com.msi.tough.model.elasticache;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "elasticache_security_group")
public class CacheSecurityGroupBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	private long acid;

	@Column(name = "name", nullable = false, length = 24)
	private String name;

	@Column(name = "provider_id")
	private String providerId;

	@Column(name = "provider_name")
	private String providerName;

	// Length is NOT from AWS documentation
	@Column(name = "description", nullable = false, length = 128)
	private String description;

	@Column(name = "stack_id", nullable = false, length = 128)
	private String stackId;

	public long getAcid() {
		return acid;
	}

	public String getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getProviderId() {
		return providerId;
	}

	public String getProviderName() {
		return providerName;
	}

	public String getStackId() {
		return stackId;
	}

	public void setAcid(final long acid) {
		this.acid = acid;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setProviderId(final String providerId) {
		this.providerId = providerId;
	}

	public void setProviderName(final String providerName) {
		this.providerName = providerName;
	}

	public void setStackId(final String stackId) {
		this.stackId = stackId;
	}

}
