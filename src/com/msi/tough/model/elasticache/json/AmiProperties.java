package com.msi.tough.model.elasticache.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class AmiProperties {
	
	private static String MSIChefRoles = "__MSI_Chef_Roles__" ;
	private static String MSIChefRole = "__MSI_Chef_Role__" ;
	
	private String type ;
	private LinkedHashMap<String,Object> Properties = null ;

	public AmiProperties(){
		this.Properties = new LinkedHashMap<String, Object>();
	}
	public AmiProperties( String type ){
		this();
		this.type = type;
	}
	
	@JsonProperty("Type")
	public String getType() { return type ;} 
	@JsonProperty("Properties")
	public LinkedHashMap<String,Object> getProperties() { return Properties; }

	public void setType( String type ) { this.type = type ; }
	public void setProperties( LinkedHashMap<String,Object> properties) { this.Properties = properties ; }

	public void setProperties( 
			//String imageId, 
			//String kernelId, 
			//String ramdiskId, 
			int numCacheNodes,
			String instanceType, 
			String availabilityZone,
			String [] securityGroups,
			String keyName)
	{
		//Properties.put("ImageId", imageId) ;
		//Properties.put("KernelId", kernelId) ;
		//Properties.put("RamdiskId", ramdiskId) ;
		Properties.put("NumCacheNodes", numCacheNodes);
		Properties.put("InstanceType", instanceType) ;
		Properties.put("AvailabilityZone", availabilityZone) ;

		Properties.put("SecurityGroups", securityGroups) ;
		Properties.put("KeyName", keyName);
	}
	
	public void setChefRoles( String [] roleList ){
		
		ArrayList<Map<String,String>> roles = new ArrayList<Map<String,String>>() ;
		for( String roleName : roleList){
			Map<String,String> role = new HashMap<String,String>() ;
			role.put(MSIChefRole, roleName);
			roles.add(role);
		}
		Properties.put(MSIChefRoles, roles) ;
	}

}
