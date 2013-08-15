package com.msi.tough.elasticbeanstalk.json;

import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class OptionToRemove {
	@JsonProperty("Namespace")
	private String NameSpace;
	@JsonProperty("OptionName")
	private String OptionName;
	
	public OptionToRemove(String NameSpace, String OptionName){
		this.NameSpace = NameSpace;
		this.OptionName = OptionName;
	}	
	
	public String getNamespace(){
		return this.NameSpace;
	}

	public String getOptionName(){
		return this.OptionName;
	}
}
