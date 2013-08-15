package com.msi.tough.cf.json;

import org.codehaus.jackson.annotate.JsonProperty;

public class DatabagFloatParameter extends DatabagParameter {

	private float value ;
	
	public DatabagFloatParameter( float value, boolean isModifiable ){
		super( isModifiable ) ;
		this.value = value ;
	}
	
	public DatabagFloatParameter( float value, boolean isModifiable, String applyType ){
		super( isModifiable, applyType ) ;
		this.value = value ;
	}

	@JsonProperty("value")
	public float getValue(){
		return value ;
	}
}
