package com.msi.tough.elasticbeanstalk.json;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class ConfigurationTemplate {
	@JsonProperty("TemplateName")
	private String name;
	
	@JsonProperty("Description")
	private String description = "";
	
	@JsonProperty("OptionSettings")
	private List<OptionSetting> opSettings = null;
	
	@JsonProperty("OptionsToRemove")
	private List<OptionToRemove> opRemove = null;
	
	public ConfigurationTemplate(String name, String desc){
		this.name = name;
		this.opSettings = new ArrayList<OptionSetting>();
		this.opRemove = new ArrayList<OptionToRemove>();
		if(desc == null){
			this.description = "";
		}
	}
	
	public void addOptionSetting(OptionSetting os){
		this.opSettings.add(os);
	}
	
	public void addOptionToRemove(OptionToRemove otr){
		this.opRemove.add(otr);
	}
}
