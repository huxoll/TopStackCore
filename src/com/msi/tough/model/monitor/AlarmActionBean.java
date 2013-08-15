package com.msi.tough.model.monitor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="action") 
public class AlarmActionBean {
	@Id 
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	@Column(nullable = false)
	private int position;
	@Column(nullable = false)
	private String name;
	@ManyToOne
	private AlarmBean parent;
	

	public AlarmBean getParent() {
		return parent;
	}
	public void setParent(AlarmBean parent) {
		this.parent = parent;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}

	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
