package com.msi.tough.cf.json;

import org.codehaus.jackson.annotate.JsonProperty;

public class DatabagStringParameter extends DatabagParameter {
	
	private String value ;
	
	public DatabagStringParameter( String value, boolean isModifiable ){
		super( isModifiable ) ;
		this.value = value ;
	}
	
	public DatabagStringParameter( String value, boolean isModifiable, String applyType ){
		super( isModifiable, applyType ) ;
		this.value = value;
	}

	@JsonProperty("value")
	public String getValue(){
		return value ;
	}
}
