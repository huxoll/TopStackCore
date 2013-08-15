package com.msi.tough.elasticbeanstalk.json;

import java.util.LinkedHashMap;

import org.codehaus.jackson.annotate.JsonProperty;

import com.msi.tough.core.JsonUtil;

public class Application {
	
	@JsonProperty("Type")
	private String type;
	
	@JsonProperty("Properties")
	private LinkedHashMap<String,Object> properties = null ;
	
	public Application(){
		this.type = "AWS::ElasticBeanstalk::Application";
		this.properties = new LinkedHashMap<String, Object>();
	}
	
	public void addProperty(String key, Object value){
		this.properties.put(key, value);
	}
	
	/*public void addRefParameter( LinkedHashMap<String,Object> properties, String parameter ){
		properties.put(parameter, JsonUtil.toSingleHash("Ref", parameter)) ;
	}*/
	
	
}
