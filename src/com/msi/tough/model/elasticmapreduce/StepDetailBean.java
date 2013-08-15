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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "BootstrapActionDetail")
public class StepDetailBean
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

    @Lob
    @Column(name = "LastStateChangeReason", length = 10280)
    private String lastStateChangeReason;

    @Column(name = "StartDateTime")
    private Date startDateTime;

    @Column(name = "State")
    private String state;

    @Column(name = "ActionOnFailure")
    private String actionOnFailure;

    @Lob
    @Column(name = "MainClass", length = 10280)
    private String mainClass;

    @Lob
    @Column(name = "Jar", length = 10280)
    private String jar;

    @Column(name = "Name")
    private String name;

    @OneToMany(mappedBy = "stepDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ArgsBean> args;

    @OneToMany(mappedBy = "stepDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<KeyValueBean> keyValues;

    public StepDetailBean()
    {
    }

    public StepDetailBean(Long id, JobFlowDetailBean jobFlowDetail)
    {
        this.id = id;
        this.jobFlowDetail = jobFlowDetail;
    }

    public StepDetailBean(Long id, JobFlowDetailBean jobFlowDetail,
        Date creationDateTime, Date endDateTime, String lastStateChangeReason,
        Date startDateTime, String state, String actionOnFailure,
        String mainClass, String jar, String name, Set<ArgsBean> args,
        Set<KeyValueBean> keyValues)
    {
        this.id = id;
        this.jobFlowDetail = jobFlowDetail;
        this.creationDateTime = creationDateTime;
        this.endDateTime = endDateTime;
        this.lastStateChangeReason = lastStateChangeReason;
        this.startDateTime = startDateTime;
        this.state = state;
        this.actionOnFailure = actionOnFailure;
        this.mainClass = mainClass;
        this.jar = jar;
        this.name = name;
        this.args = args;
        this.keyValues = keyValues;
    }

    public String getActionOnFailure()
    {
        return this.actionOnFailure;
    }

    public Set<ArgsBean> getArgs()
    {
        return this.args;
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

    public String getJar()
    {
        return this.jar;
    }

    public JobFlowDetailBean getJobFlowDetail()
    {
        return this.jobFlowDetail;
    }

    public Set<KeyValueBean> getKeyValues()
    {
        return this.keyValues;
    }

    public String getLastStateChangeReason()
    {
        return this.lastStateChangeReason;
    }

    public String getMainClass()
    {
        return this.mainClass;
    }

    public String getName()
    {
        return this.name;
    }

    public Date getStartDateTime()
    {
        return this.startDateTime;
    }

    public String getState()
    {
        return this.state;
    }

    public void setActionOnFailure(String actionOnFailure)
    {
        this.actionOnFailure = actionOnFailure;
    }

    public void setArgs(Set<ArgsBean> args)
    {
        this.args = args;
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

    public void setJar(String jar)
    {
        this.jar = jar;
    }

    public void setJobFlowDetail(JobFlowDetailBean jobFlowDetail)
    {
        this.jobFlowDetail = jobFlowDetail;
    }

    public void setKeyValues(Set<KeyValueBean> keyValues)
    {
        this.keyValues = keyValues;
    }

    public void setLastStateChangeReason(String lastStateChangeReason)
    {
        this.lastStateChangeReason = lastStateChangeReason;
    }

    public void setMainClass(String mainClass)
    {
        this.mainClass = mainClass;
    }

    public void setName(String name)
    {
        this.name = name;
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
