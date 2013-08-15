package com.msi.tough.model.elasticmapreduce;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "JobFlowDetail")
public class JobFlowDetailBean
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "JobFlowId")
    private String jobFlowId;

    @Lob
    @Column(name = "LogUri", length = 10280)
    private String logUri;

    @Column(name = "Name")
    private String name;

    @Column(name = "CreationDateTime")
    private Date creationDateTime;

    @Column(name = "EndDateTime")
    private Date endDateTime;

    @Lob
    @Column(name = "LastStateChangeReason", length = 10280)
    private String lastStateChangeReason;

    @Column(name = "ReadyDateTime")
    private Date readyDateTime;

    @Column(name = "StartDateTime")
    private Date startDateTime;

    @Column(name = "State")
    private String state;

    @Column(name = "Ec2KeyName")
    private String ec2KeyName;

    @Column(name = "HadoopVersion")
    private String hadoopVersion;

    @Column(name = "InstanceCount")
    private Integer instanceCount;

    @Column(name = "KeepJobFlowAliveWhenNoSteps")
    private Boolean keepJobFlowAliveWhenNoSteps;

    @Column(name = "MasterInstanceId")
    private String masterInstanceId;

    @Column(name = "MasterInstanceType")
    private String masterInstanceType;

    @Lob
    @Column(name = "MasterPublicDnsName", length = 10280)
    private String masterPublicDnsName;

    @Column(name = "NormalizedInstanceHours")
    private Long normalizedInstanceHours;

    @Lob
    @Column(name = "Placement", length = 10280)
    private String placement;

    @Column(name = "SlaveInstanceType")
    private String slaveInstanceType;

    @OneToMany(mappedBy = "jobFlowDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BootstrapActionDetailBean> bootstrapActionDetails;

    @OneToMany(mappedBy = "jobFlowDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<StepDetailBean> stepDetails;

    @OneToMany(mappedBy = "jobFlowDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<InstanceGroupDetailBean> instanceGroupDetails;

    @Column(name = "Terminate")
    private Boolean terminate;

    public JobFlowDetailBean()
    {
    }

    public JobFlowDetailBean(Long id)
    {
        this.id = id;
    }

    public JobFlowDetailBean(Long id, String jobFlowId, String logUri,
        String name, Date creationDateTime, Date endDateTime,
        String lastStateChangeReason, Date readyDateTime, Date startDateTime,
        String state, String ec2KeyName, String hadoopVersion,
        Integer instanceCount, Boolean keepJobFlowAliveWhenNoSteps,
        String masterInstanceId, String masterInstanceType,
        String masterPublicDnsName, Long normalizedInstanceHours,
        String placement, String slaveInstanceType,
        Set<BootstrapActionDetailBean> bootstrapActionDetails,
        Set<StepDetailBean> stepDetails,
        Set<InstanceGroupDetailBean> instanceGroupDetails, Boolean terminate)
    {
        this.id = id;
        this.jobFlowId = jobFlowId;
        this.logUri = logUri;
        this.name = name;
        this.creationDateTime = creationDateTime;
        this.endDateTime = endDateTime;
        this.lastStateChangeReason = lastStateChangeReason;
        this.readyDateTime = readyDateTime;
        this.startDateTime = startDateTime;
        this.state = state;
        this.ec2KeyName = ec2KeyName;
        this.hadoopVersion = hadoopVersion;
        this.instanceCount = instanceCount;
        this.keepJobFlowAliveWhenNoSteps = keepJobFlowAliveWhenNoSteps;
        this.masterInstanceId = masterInstanceId;
        this.masterInstanceType = masterInstanceType;
        this.masterPublicDnsName = masterPublicDnsName;
        this.normalizedInstanceHours = normalizedInstanceHours;
        this.placement = placement;
        this.slaveInstanceType = slaveInstanceType;
        this.bootstrapActionDetails = bootstrapActionDetails;
        this.stepDetails = stepDetails;
        this.instanceGroupDetails = instanceGroupDetails;
        this.terminate = terminate;
    }

    public Set<BootstrapActionDetailBean> getBootstrapActionDetails()
    {
        return this.bootstrapActionDetails;
    }

    public Date getCreationDateTime()
    {
        return this.creationDateTime;
    }

    public String getEc2KeyName()
    {
        return this.ec2KeyName;
    }

    public Date getEndDateTime()
    {
        return this.endDateTime;
    }

    public String getHadoopVersion()
    {
        return this.hadoopVersion;
    }

    public Long getId()
    {
        return this.id;
    }

    public Integer getInstanceCount()
    {
        return this.instanceCount;
    }

    public Set<InstanceGroupDetailBean> getInstanceGroupDetails()
    {
        return this.instanceGroupDetails;
    }

    public String getJobFlowId()
    {
        return this.jobFlowId;
    }

    public Boolean getKeepJobFlowAliveWhenNoSteps()
    {
        return this.keepJobFlowAliveWhenNoSteps;
    }

    public String getLastStateChangeReason()
    {
        return this.lastStateChangeReason;
    }

    public String getLogUri()
    {
        return this.logUri;
    }

    public String getMasterInstanceId()
    {
        return this.masterInstanceId;
    }

    public String getMasterInstanceType()
    {
        return this.masterInstanceType;
    }

    public String getMasterPublicDnsName()
    {
        return this.masterPublicDnsName;
    }

    public String getName()
    {
        return this.name;
    }

    public Long getNormalizedInstanceHours()
    {
        return this.normalizedInstanceHours;
    }

    public String getPlacement()
    {
        return this.placement;
    }

    public Date getReadyDateTime()
    {
        return this.readyDateTime;
    }

    public String getSlaveInstanceType()
    {
        return this.slaveInstanceType;
    }

    public Date getStartDateTime()
    {
        return this.startDateTime;
    }

    public String getState()
    {
        return this.state;
    }

    public Set<StepDetailBean> getStepDetails()
    {
        return this.stepDetails;
    }

    public Boolean getTerminate()
    {
        return this.terminate;
    }

    public void setBootstrapActionDetails(
        Set<BootstrapActionDetailBean> bootstrapActionDetails)
    {
        this.bootstrapActionDetails = bootstrapActionDetails;
    }

    public void setCreationDateTime(Date creationDateTime)
    {
        this.creationDateTime = creationDateTime;
    }

    public void setEc2KeyName(String ec2KeyName)
    {
        this.ec2KeyName = ec2KeyName;
    }

    public void setEndDateTime(Date endDateTime)
    {
        this.endDateTime = endDateTime;
    }

    public void setHadoopVersion(String hadoopVersion)
    {
        this.hadoopVersion = hadoopVersion;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public void setInstanceCount(Integer instanceCount)
    {
        this.instanceCount = instanceCount;
    }

    public void setInstanceGroupDetails(
        Set<InstanceGroupDetailBean> instanceGroupDetails)
    {
        this.instanceGroupDetails = instanceGroupDetails;
    }

    public void setJobFlowId(String jobFlowId)
    {
        this.jobFlowId = jobFlowId;
    }

    public void setKeepJobFlowAliveWhenNoSteps(
        Boolean keepJobFlowAliveWhenNoSteps)
    {
        this.keepJobFlowAliveWhenNoSteps = keepJobFlowAliveWhenNoSteps;
    }

    public void setLastStateChangeReason(String lastStateChangeReason)
    {
        this.lastStateChangeReason = lastStateChangeReason;
    }

    public void setLogUri(String logUri)
    {
        this.logUri = logUri;
    }

    public void setMasterInstanceId(String masterInstanceId)
    {
        this.masterInstanceId = masterInstanceId;
    }

    public void setMasterInstanceType(String masterInstanceType)
    {
        this.masterInstanceType = masterInstanceType;
    }

    public void setMasterPublicDnsName(String masterPublicDnsName)
    {
        this.masterPublicDnsName = masterPublicDnsName;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setNormalizedInstanceHours(Long normalizedInstanceHours)
    {
        this.normalizedInstanceHours = normalizedInstanceHours;
    }

    public void setPlacement(String placement)
    {
        this.placement = placement;
    }

    public void setReadyDateTime(Date readyDateTime)
    {
        this.readyDateTime = readyDateTime;
    }

    public void setSlaveInstanceType(String slaveInstanceType)
    {
        this.slaveInstanceType = slaveInstanceType;
    }

    public void setStartDateTime(Date startDateTime)
    {
        this.startDateTime = startDateTime;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public void setStepDetails(Set<StepDetailBean> stepDetails)
    {
        this.stepDetails = stepDetails;
    }

    public void setTerminate(Boolean terminate)
    {
        this.terminate = terminate;
    }
}
