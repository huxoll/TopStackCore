package com.msi.tough.cf.json;

import org.codehaus.jackson.annotate.JsonProperty;

public class CFParameter {

	private String type ;
	private String description ;
	
	public CFParameter( String type, String description ){
		this.type = type ;
		this.description = description ;
	}
	

	@JsonProperty("Description")
	public String getDescription() { return description; }

	@JsonProperty("Type")
	public String getType() { return type ; }

}
