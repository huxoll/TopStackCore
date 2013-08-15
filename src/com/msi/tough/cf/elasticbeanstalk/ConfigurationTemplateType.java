package com.msi.tough.cf.elasticbeanstalk;

import java.util.List;

import com.msi.tough.cf.CFType;

public class ConfigurationTemplateType extends CFType {
	private String templateName;
	private List<OptionSettingsType> optionSettings;
	private String solutionStackName;
	private SourceConfigurationType sourceConfiguration;

	public List<OptionSettingsType> getOptionSettings() {
		return optionSettings;
	}

	public String getSolutionStackName() {
		return solutionStackName;
	}

	public SourceConfigurationType getSourceConfiguration() {
		return sourceConfiguration;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setOptionSettings(List<OptionSettingsType> optionSettings) {
		this.optionSettings = optionSettings;
	}

	public void setSolutionStackName(String solutionStackName) {
		this.solutionStackName = solutionStackName;
	}

	public void setSourceConfiguration(
			SourceConfigurationType sourceConfiguration) {
		this.sourceConfiguration = sourceConfiguration;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

}
