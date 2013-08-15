/*
 * InstanceBean.java.java
 *
 * MSI Eucalyptus LoadBalancer Project
 * Copyright (C) Momentum SI
 *
 */

package com.msi.tough.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.msi.tough.model.monitor.ServiceHealthEventBean;

/**
 * @author jlomax
 * 
 * NOTE:  This table is pre-created in ToughResources/scripts/configDB.SQL
 */
@Entity
@Table(name = "service")
public class ServiceBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="service_id")
	private int id;

	@Column(name="service_name", nullable=false, length=32, unique=true)
	private String serviceName;

	@Column(name="service_name_abbreviation", nullable=false, length=8, unique=true)
	private String serviceNameAbbreviation;

	// Hibernate creates the name "service_service_id" by default.
	// If you use a different name on the column below, it will create a
	// create that column (for no good reason) and your code will fail
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "service_service_id")
    private Set<ServiceHealthEventBean> serviceHealthEvents;
	
	
	// GETTERS

	public int getId() {
		return id;
	}

	public String getServiceName() {
		return serviceName;
	}
	
	public String getServiceNameAbbreviation() {
		return serviceNameAbbreviation ;
	}

	// SETTERS
	
	public void setId(int id) {
		this.id = id;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setServiceNameAbbreviation( String serviceNameAbbreviation ){
		this.serviceNameAbbreviation = serviceNameAbbreviation ;
	}

	public Set<ServiceHealthEventBean> getServiceHealthEvents() {
		return serviceHealthEvents;
	}

	public void setServiceHealthEvents(Set<ServiceHealthEventBean> serviceHealthEvents) {
		this.serviceHealthEvents = serviceHealthEvents;
	}

}
