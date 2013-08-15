package com.msi.tough.elasticbeanstalk.json;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;

import com.msi.tough.core.JsonUtil;

public class ElasticBeanStalkCloudFormation {
	
	@JsonProperty("AWSTemplateFormatVersion")
	private String awsTemplateFormatVersion;
	
	@JsonProperty("Description")
	private String description ;
	
	@JsonProperty("Resources")
	private LinkedHashMap<String,Object> resources = null ;
	
	@JsonProperty("Parameters")
	private LinkedHashMap<String,Object> parameters = null ;

	public ElasticBeanStalkCloudFormation(){
		this.awsTemplateFormatVersion = "2010-09-09";
		this.description = "AWS CloudFormation Sample Template ElasticBeanstalkSample: Configure and launch the AWS Elastic Beanstalk sample application. " +
				"Note, since AWS Elastic Beanstalk is only available in US-East-1, this template can only be used to create stacks in the US-East-1 region. " +
				"**WARNING** This template creates one or more Amazon EC2 instances. You will be billed for the AWS resources used if you create a stack from this template.";
		this.resources = new LinkedHashMap<String, Object>();
		this.parameters = new LinkedHashMap<String, Object>();
	}
	
	public void addResource(String key, Object value){
		this.resources.put(key, value);
	}
	
	public void addParameter(String key, Object value){
		this.parameters.put(key, value);
	}
	
	public String toJson() throws JsonGenerationException, JsonMappingException, IOException {
		return JsonUtil.toJsonString(this);
	}
}
