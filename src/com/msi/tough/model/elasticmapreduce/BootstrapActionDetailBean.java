package com.msi.tough.model.elasticmapreduce;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "BootStrapActionDetail")
public class BootstrapActionDetailBean
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "JobFlowDetail")
    private JobFlowDetailBean jobFlowDetail;

    @Column(name = "Name")
    private String name;

    @Column(name = "Path")
    private String path;

    @OneToMany(mappedBy = "bootstrapActionDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ArgsBean> args;

    public BootstrapActionDetailBean()
    {
    }

    public BootstrapActionDetailBean(Long id, JobFlowDetailBean jobFlowDetail)
    {
        this.id = id;
        this.jobFlowDetail = jobFlowDetail;
    }

    public BootstrapActionDetailBean(Long id, JobFlowDetailBean jobFlowDetail,
        String name, String path, Set<ArgsBean> args)
    {
        this.id = id;
        this.jobFlowDetail = jobFlowDetail;
        this.name = name;
        this.path = path;
        this.args = args;
    }

    public Set<ArgsBean> getArgs()
    {
        return this.args;
    }

    public Long getId()
    {
        return this.id;
    }

    public JobFlowDetailBean getJobFlowDetail()
    {
        return this.jobFlowDetail;
    }

    public String getName()
    {
        return this.name;
    }

    public String getPath()
    {
        return this.path;
    }

    public void setArgs(Set<ArgsBean> args)
    {
        this.args = args;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public void setJobFlowDetail(JobFlowDetailBean jobFlowDetail)
    {
        this.jobFlowDetail = jobFlowDetail;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

}
