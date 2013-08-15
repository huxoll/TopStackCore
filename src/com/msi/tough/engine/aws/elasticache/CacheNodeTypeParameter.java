package com.msi.tough.engine.aws.elasticache;

import org.codehaus.jackson.annotate.JsonProperty;

public class CacheNodeTypeParameter {

	private String defaultNodeType ;
	private String description ;
	private String type ;
	private String[] allowedValues = {
			"cache.m1.small", "cache.m1.large", "cache.m1.xlarge", "cache.m2.xlarge", "cache.m2.2xlarge", "cache.m2.4xlarge", "cache.c1.xlarge"
		};
	private String constraintDescription ;
	
	public CacheNodeTypeParameter(){
		defaultNodeType = "cache.m1.small" ;
		description = "The compute and memory capacity of the nodes in the Cache Cluster" ;
		type = "String" ;
		constraintDescription = "must select a valid Cache Node type." ;
	}
	
	@JsonProperty("Default")
	public String getDefault() { return defaultNodeType; }

	@JsonProperty("Description")
	public String getDescription() { return description; }

	@JsonProperty("Type")
	public String getType() { return type ; }

	@JsonProperty("AllowedValues")
	public String[] getAllowedValues() { return allowedValues ; }

	@JsonProperty("ConstraintDescription")
	public String getConstraintDescription() { return constraintDescription ; }
}
