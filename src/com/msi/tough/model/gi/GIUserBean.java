package com.msi.tough.model.gi;

import javax.persistence.*;

import org.hibernate.annotations.Type;

@Entity
@Table( name = "gi_user")
public class GIUserBean {
	
	@Id
	@Column( name = "id")
	@GeneratedValue (strategy = GenerationType.IDENTITY)	
	private Long id;
	
	@Column( name = "user_name")	
	private String username;
	
	@Column( name = "keypair")	
	private String keypair;

	@Column( name = "metadata")	
	private String metadata;

	@Column( name = "user_id")
	private String userid;

	@Column( name = "admin_status")
	@Type( type = "boolean")
	private boolean adminstatus;
	
	public GIUserBean(){
		
	}
		
	public GIUserBean(String name, String key, boolean status){
		username = name;
		keypair = key;
		adminstatus = status;
	}
	
	public long getId(){
		return id;
	}
	
	public void setId(long givenId){
		id = givenId;
	}
	
	public String getUsername(){
		return username;
	}
	
	public void setUsername(String givenName){
		username = givenName;
	}
	
	public String getKeypair(){
		return keypair;
	}
	
	public void setKeypair(String givenKey){
		keypair = givenKey;
	}
	
	public String getMetadata(){
		return metadata;
	}

	public void setMetadata(String recipe){
		metadata = recipe;
	}
	
	public String getUserid(){
		return userid;
	}
	
	public void setUserid(String i){
		userid = i; 
	}
	
	public boolean getAdminstatus(){
		return adminstatus;
	}
	
	public void setAdminstatus(boolean status){
		adminstatus = status;
	}
	
}
