package com.msi.tough.elasticbeanstalk.json;

import org.codehaus.jackson.annotate.JsonProperty;

public class OptionSetting{
	
	private String NameSpace;
	
	private String OptionName;
	
	private String Value;

	public OptionSetting(String NameSpace, String OptionName, String Value){
		this.NameSpace = NameSpace;
		this.OptionName = OptionName;
		this.Value = Value;
	}

	@JsonProperty("Namespace")
	public String getNamespace(){
		return this.NameSpace;
	}

	@JsonProperty("OptionName")
	public String getOptionName(){
		return this.OptionName;
	}

	@JsonProperty("Value")
	public String getOptionValue(){
		return this.Value;
	}
}