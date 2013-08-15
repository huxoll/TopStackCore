package com.msi.tough.cf.json;

import org.codehaus.jackson.annotate.JsonProperty;


public class CFDefaultParameter extends CFParameter {
	
	private String defaultValue ;
	
	public CFDefaultParameter( String type, String description, String defaultValue ){
		super( type, description ) ;
		
		this.defaultValue = defaultValue ;
	}
	
	@JsonProperty("Default")
	public String getDefault() { return defaultValue; }

}
