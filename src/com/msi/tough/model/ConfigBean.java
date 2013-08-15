/*
 * AvzoneBean.java
 *
 * MSI Eucalyptus LoadBalancer Project
 * Copyright (C) Momentum SI
 *
 */
package com.msi.tough.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model class for web service object AvailabilityZones. It implements hibernate
 * entity bean
 * <p>
 * Apart from the fields defined in the LoadBalancerDescription following extra
 * fields are maintained:
 * <li>id: database generated recored id</li>
 * </p>
 * 
 * @author raj
 * 
 */
@Entity
@Table(name = "config")
public class ConfigBean {
	//TODO add parent relationship back to ConfigTemplateBean
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name_space")
	private String nameSpace;

	@Column(name = "opt")
	private String option;

	@Column(name = "val")
	private String value;

	public long getId() {
		return id;
	}

	public String getNameSpace() {
		return nameSpace;
	}

	public String getOption() {
		return option;
	}

	public String getValue() {
		return value;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setNameSpace(final String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public void setOption(final String option) {
		this.option = option;
	}

	public void setValue(final String value) {
		this.value = value;
	}

}