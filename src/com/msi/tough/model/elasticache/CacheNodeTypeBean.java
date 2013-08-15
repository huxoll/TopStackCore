package com.msi.tough.model.elasticache;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

// Valid AWS types as of 2011-11-06
// cache.m1.small
// cache.m1.large
// cache.m1.xlarge
// cache.m2.xlarge
// cache.m2.2xlarge
// cache.m2.4xlarge
// cache.c1.xlarge

@Entity
@Table(name = "cache_node_type")
public class CacheNodeTypeBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	// This could be an enumeration if code recompiles are OK to add new types
	@Column(name = "node_type", nullable = false, length = 24)
	private String type;

	@Column(name = "description", length = 64)
	private String description;

	public CacheNodeTypeBean() {
	}

	public CacheNodeTypeBean(final int id, final String type) {
		this(type);
		this.id = id;
	}

	public CacheNodeTypeBean(final String type) {
		this();
		this.type = type;
	}

	// Getters

	public String getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}
}
