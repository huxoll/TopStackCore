package com.msi.tough.engine.aws.elasticache;

import java.util.LinkedHashMap;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class CacheSecurityGroupResource {
	private String type ;
	private LinkedHashMap<String,Object> properties = null ;
	private String name ;
	
	public CacheSecurityGroupResource( String name, String description ){
		properties = new LinkedHashMap<String, Object>() ;

		this.name = name ;
		this.type = "AWS::ElastiCache::SecurityGroup" ;

		properties.put("Description", description );
	}

	@JsonProperty("Type")
	public String getType() { return type ;} 

	@JsonProperty("Properties")
	public LinkedHashMap<String, Object> getProperties() { return properties; }

	@JsonIgnore
	public String getName() { return name ; }

	public void setType( String type ) { this.type = type ; }
	public void setProperties( LinkedHashMap<String,Object> properties) { this.properties = properties ; }
	public void setProperties( String description ){
		properties.put("Description", description ) ;
	}
	public void setName( String name ) { this.name = name ; }
}
