package com.msi.tough.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.msi.tough.model.monitor.AlarmActionBean;


@Entity
@DiscriminatorValue("InsufficientAlarmAction")
public class InsufficientAlarmActionBean extends AlarmActionBean {

}
