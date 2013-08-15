package com.msi.tough.cf.json;

import org.codehaus.jackson.annotate.JsonProperty;

public class CFBooleanParameter extends CFDefaultParameter {

	private String[] allowedValues = { "true", "false" };
	private String constraintDescription = "must be either true or false" ;

	public CFBooleanParameter(String description, String defaultValue){
		super("String", description, defaultValue) ;
	}

	@JsonProperty("AllowedValues")
	public String[] getAllowedValues() { return allowedValues ; }

	@JsonProperty("ConstraintDescription")
	public String getConstraintDescription() { return constraintDescription ; }

}
