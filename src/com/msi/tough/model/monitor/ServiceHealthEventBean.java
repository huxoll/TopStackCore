package com.msi.tough.model.monitor;

//import java.lang.annotation.Target;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
//import javax.persistence.Transient;
//import javax.persistence.UniqueConstraint;

import org.hibernate.Session;

import com.msi.tough.model.ServiceBean;
import com.msi.tough.model.monitor.enums.ServiceHealthStatus;

/**
 * Used to specify a descriptive string associated
 * with Service Health Metric Data
 * 
 * @author jlomax
 *
 */
@Entity
@Table
(
	name="service_health_event" /*, 
	uniqueConstraints = {@UniqueConstraint(columnNames={"service_health_event_description"})}*/
)
public class ServiceHealthEventBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="service_health_event_id")
    private long id;

    @ManyToOne
    private ServiceBean service ;

    @Column(name="service_health_event_description", nullable=false, length=128, unique=false )
    private String description;

    @Column(name="service_health_region", length=32)
    private String region;

    @Column(name="service_health_availability_zone", length=32)
    private String availabilityZone;

	@Column( name = "service_health_status", length=15)
	@Enumerated(EnumType.STRING)
    private ServiceHealthStatus status ;

    @Column(name = "created_time", nullable=false)
	private Date createdTime;

    public ServiceHealthEventBean( ) 
    {     
        this.setCreatedTime(new Date());
    }

    public ServiceHealthEventBean
    (
    	ServiceBean service,
    	String region,
    	String availabilityZone,
    	String serviceHealthEventDescription,
    	ServiceHealthStatus serviceHealthStatus
    )
    {
    	this();
    	this.service = service;
    	this.region = region;
    	this.availabilityZone = availabilityZone;
    	this.status = serviceHealthStatus ;
    	this.description = serviceHealthEventDescription;
    }


    public long getId() {
        return id;
    }

    public ServiceBean getService( ){
    	return this.service ;
    }
    
    public String getRegion() {
    	return region;
    }
    
    public String getAvailablityZone(){
    	return availabilityZone ;
    }
    
    public String getDescription() {
        return description;
    }
    
    public ServiceHealthStatus getStatus(){
    	return status ;
    }

    
    public void setId(long id) {
        this.id = id;
    }

    public void setService( ServiceBean service ) {
    	this.service = service ;
    }
    
    public void setDescription(String serviceHealthEventDescription) {
        this.description = serviceHealthEventDescription;
    }

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdDate) {
		createdTime = createdDate;
	}


    public void save(Session session)
    {
        session.saveOrUpdate(this);
    }
}
