package com.msi.tough.model.elasticmapreduce;

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
@Table(name = "KeyValue")
public class KeyValueBean
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "StepDetail")
    private StepDetailBean stepDetail;

    @Lob
    @Column(name = "KeyString", length = 10280)
    private String key;

    @Lob
    @Column(name = "Value", length = 10280)
    private String value;

    public KeyValueBean()
    {
    }

    public KeyValueBean(Long id, StepDetailBean stepDetail)
    {
        this.id = id;
        this.stepDetail = stepDetail;
    }

    public KeyValueBean(Long id, StepDetailBean stepDetail, String key,
        String value)
    {
        this.id = id;
        this.stepDetail = stepDetail;
        this.key = key;
        this.value = value;
    }

    public Long getId()
    {
        return this.id;
    }

    public String getKey()
    {
        return this.key;
    }

    public StepDetailBean getStepDetail()
    {
        return this.stepDetail;
    }

    public String getValue()
    {
        return this.value;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public void setStepDetail(StepDetailBean stepDetail)
    {
        this.stepDetail = stepDetail;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

}
