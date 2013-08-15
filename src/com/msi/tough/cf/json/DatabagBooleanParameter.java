package com.msi.tough.cf.json;

import org.codehaus.jackson.annotate.JsonProperty;

public class DatabagBooleanParameter extends DatabagParameter {

	private boolean value ;
	
	public DatabagBooleanParameter( boolean value, boolean isModifiable ){
		super( isModifiable ) ;
		this.value = value ;
	}
	
	public DatabagBooleanParameter( boolean value, boolean isModifiable, String applyType ){
		super( isModifiable, applyType ) ;
		this.value = value ;
	}
	
	@JsonProperty("value")
	public boolean getValue(){
		return value ; //Boolean.toString(value) ;
	}
}
