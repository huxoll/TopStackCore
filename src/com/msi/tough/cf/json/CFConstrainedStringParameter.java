package com.msi.tough.cf.json;

import org.codehaus.jackson.annotate.JsonProperty;

public class CFConstrainedStringParameter extends CFDefaultParameter {
	
	private String[] allowedValues = null ;
	private String constraintDescription ;


	CFConstrainedStringParameter( String description, String defaultValue, String constraintDescription, String [] allowedValues ){
		super("String", description, defaultValue );
		this.allowedValues = allowedValues ;
		this.constraintDescription = constraintDescription ;
	}

	@JsonProperty("AllowedValues")
	public String[] getAllowedValues() { return allowedValues ; }

	@JsonProperty("ConstraintDescription")
	public String getConstraintDescription() { return constraintDescription ; }

}
