package com.msi.tough.model.elasticache;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//Valid AWS types as of 2011-11-06
//memcached1.4

@Entity
@Table(name = "elasticache_parameter_family")
public class CacheParameterGroupFamilyBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "family", nullable = false, length = 24)
	private String family;

	public CacheParameterGroupFamilyBean() {
	}

	public CacheParameterGroupFamilyBean(final String family) {
		this();
		this.family = family;
	}

	// Getters

	public String getFamily() {
		return family;
	}

	public int getId() {
		return id;
	}

}
