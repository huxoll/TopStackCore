package com.msi.tough.model.elasticache;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "elasticache_parameter_group")
public class CacheParameterGroupBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	private long acid;

	@Column(name = "family_id")
	private long familyId;

	@Column(name = "name", nullable = false, length = 24)
	private String name;

	// Length is NOT from AWS documentation
	@Column(name = "description", nullable = false, length = 128)
	private String description;

	public long getAcid() {
		return acid;
	}

	public String getDescription() {
		return description;
	}

	public long getFamilyId() {
		return familyId;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setAcid(final long acid) {
		this.acid = acid;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setFamilyId(final long familyId) {
		this.familyId = familyId;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

}
