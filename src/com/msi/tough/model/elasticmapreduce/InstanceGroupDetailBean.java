package com.msi.tough.model.elasticmapreduce;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "InstanceGroupDetail")
public class InstanceGroupDetailBean
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "JobFlowDetail")
    private JobFlowDetailBean jobFlowDetail;

    @Column(name = "CreationDateTime")
    private Date creationDateTime;

    @Column(name = "EndDateTime")
    private Date endDateTime;

    @Column(name = "InstanceGroupId")
    private String instanceGroupId;

    @Column(name = "BidPrice")
    private String bidPrice;

    @Column(name = "InstanceRequestCount")
    private Integer instanceRequestCount;

    @Column(name = "InstanceRole")
    private String instanceRole;

    @Column(name = "InstanceRunningCount")
    private Integer instanceRunningCount;

    @Column(name = "InstanceType")
    private String instanceType;

    @Lob
    @Column(name = "LastStateChangeReason", length = 10280)
    private String lastStateChangeReason;

    @Column(name = "Name")
    private String name;

    @Column(name = "ReadyDateTime")
    private Date readyDateTime;

    @Column(name = "StartDateTime")
    private Date startDateTime;

    @Column(name = "State")
    private String state;

    public InstanceGroupDetailBean()
    {
    }

    public InstanceGroupDetailBean(Long id, JobFlowDetailBean jobFlowDetail)
    {
        this.id = id;
        this.jobFlowDetail = jobFlowDetail;
    }

    public InstanceGroupDetailBean(Long id, JobFlowDetailBean jobFlowDetail,
        Date creationDateTime, Date endDateTime, String instanceGroupId,
        String bidPrice, Integer instanceRequestCount, String instanceRole,
        Integer instanceRunningCount, String instanceType,
        String lastStateChangeReason, String name, Date readyDateTime,
        Date startDateTime, String state)
    {
        this.id = id;
        this.jobFlowDetail = jobFlowDetail;
        this.creationDateTime = creationDateTime;
        this.endDateTime = endDateTime;
        this.instanceGroupId = instanceGroupId;
        this.bidPrice = bidPrice;
        this.instanceRequestCount = instanceRequestCount;
        this.instanceRole = instanceRole;
        this.instanceRunningCount = instanceRunningCount;
        this.instanceType = instanceType;
        this.lastStateChangeReason = lastStateChangeReason;
        this.name = name;
        this.readyDateTime = readyDateTime;
        this.startDateTime = startDateTime;
        this.state = state;
    }

    public String getBidPrice()
    {
        return this.bidPrice;
    }

    public Date getCreationDateTime()
    {
        return this.creationDateTime;
    }

    public Date getEndDateTime()
    {
        return this.endDateTime;
    }

    public Long getId()
    {
        return this.id;
    }

    public String getInstanceGroupId()
    {
        return this.instanceGroupId;
    }

    public Integer getInstanceRequestCount()
    {
        return this.instanceRequestCount;
    }

    public String getInstanceRole()
    {
        return this.instanceRole;
    }

    public Integer getInstanceRunningCount()
    {
        return this.instanceRunningCount;
    }

    public String getInstanceType()
    {
        return this.instanceType;
    }

    public JobFlowDetailBean getJobFlowDetail()
    {
        return this.jobFlowDetail;
    }

    public String getLastStateChangeReason()
    {
        return this.lastStateChangeReason;
    }

    public String getName()
    {
        return this.name;
    }

    public Date getReadyDateTime()
    {
        return this.readyDateTime;
    }

    public Date getStartDateTime()
    {
        return this.startDateTime;
    }

    public String getState()
    {
        return this.state;
    }

    public void setBidPrice(String bidPrice)
    {
        this.bidPrice = bidPrice;
    }

    public void setCreationDateTime(Date creationDateTime)
    {
        this.creationDateTime = creationDateTime;
    }

    public void setEndDateTime(Date endDateTime)
    {
        this.endDateTime = endDateTime;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public void setInstanceGroupId(String instanceGroupId)
    {
        this.instanceGroupId = instanceGroupId;
    }

    public void setInstanceRequestCount(Integer instanceRequestCount)
    {
        this.instanceRequestCount = instanceRequestCount;
    }

    public void setInstanceRole(String instanceRole)
    {
        this.instanceRole = instanceRole;
    }

    public void setInstanceRunningCount(Integer instanceRunningCount)
    {
        this.instanceRunningCount = instanceRunningCount;
    }

    public void setInstanceType(String instanceType)
    {
        this.instanceType = instanceType;
    }

    public void setJobFlowDetail(JobFlowDetailBean jobFlowDetail)
    {
        this.jobFlowDetail = jobFlowDetail;
    }

    public void setLastStateChangeReason(String lastStateChangeReason)
    {
        this.lastStateChangeReason = lastStateChangeReason;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setReadyDateTime(Date readyDateTime)
    {
        this.readyDateTime = readyDateTime;
    }

    public void setStartDateTime(Date startDateTime)
    {
        this.startDateTime = startDateTime;
    }

    public void setState(String state)
    {
        this.state = state;
    }

}
