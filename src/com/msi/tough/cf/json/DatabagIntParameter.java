package com.msi.tough.cf.json;

import org.codehaus.jackson.annotate.JsonProperty;

public class DatabagIntParameter extends DatabagParameter {
	
	private int value ;
	
	public DatabagIntParameter( int value, boolean isModifiable ){
		super( isModifiable ) ;
		this.value = value ;
	}
	
	public DatabagIntParameter( int value, boolean isModifiable, String applyType ){
		super( isModifiable, applyType ) ;
		this.value = value ;
	}
	
	@JsonProperty("value")
	public int getValue(){
		return value ;
	}
}
