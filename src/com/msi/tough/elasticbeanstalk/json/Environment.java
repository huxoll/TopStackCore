package com.msi.tough.elasticbeanstalk.json;

import java.util.LinkedHashMap;

import org.codehaus.jackson.annotate.JsonProperty;

public class Environment {
	@JsonProperty("Type")
	private String type;
	
	@JsonProperty("Properties")
	private LinkedHashMap<String,Object> properties = null ;
	
	public Environment(){
		this.type = "AWS::ElasticBeanstalk::Environment";
		this.properties = new LinkedHashMap<String,Object>();
	}
	
	public void addProperty(String key, Object value){
		this.properties.put(key, value);
	}
}
