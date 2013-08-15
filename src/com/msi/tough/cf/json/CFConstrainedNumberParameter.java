package com.msi.tough.cf.json;

import org.codehaus.jackson.annotate.JsonProperty;

public class CFConstrainedNumberParameter extends CFDefaultParameter {

	private int minValue ;
	private int maxValue ;
	private String constraintDescription ;
 
	
	public CFConstrainedNumberParameter ( String description, int defaultValue, int minValue, int maxValue, String constraintDescription ) {
		super( "Number", description, Integer.toString(defaultValue));

		this.minValue = minValue ;
		this.maxValue = maxValue ;
		this.constraintDescription = constraintDescription ;
	}

	@JsonProperty("ConstraintDescription")
	public String getConstraintDescription() { return constraintDescription ; }
	
	@JsonProperty("MaxValue")
	public String getMaxValue() { return Integer.toString(maxValue); }
	
	@JsonProperty("MinValue")
	public String getMinValue() { return Integer.toString(minValue); }
	
}
