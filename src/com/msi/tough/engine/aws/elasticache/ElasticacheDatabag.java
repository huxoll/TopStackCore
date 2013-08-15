package com.msi.tough.engine.aws.elasticache;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;

import com.msi.tough.core.JsonUtil;

public class ElasticacheDatabag {
	
	private ElasticacheConfigDatabagItem config ;
	private ElasticacheParameterGroupDatabagItem parameterGroup;
	
	public ElasticacheDatabag( 
			ElasticacheConfigDatabagItem config, 
			ElasticacheParameterGroupDatabagItem parameterGroup ){
		this.config = config ;
		this.parameterGroup = parameterGroup ;
	}

	@JsonProperty("config")
	public ElasticacheConfigDatabagItem getConfig(){
		return config ;
	}

	@JsonProperty("parametergroup")
	public ElasticacheParameterGroupDatabagItem getParameterGroup(){
		return parameterGroup ;
	}

	public String toJson() throws JsonGenerationException, JsonMappingException, IOException {
		return JsonUtil.toJsonString(this);
	}

}
