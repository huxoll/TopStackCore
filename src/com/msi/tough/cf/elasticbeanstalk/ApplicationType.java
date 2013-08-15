package com.msi.tough.cf.elasticbeanstalk;

import java.util.List;

import com.msi.tough.cf.CFType;

public class ApplicationType extends CFType {
	private String description;
	private List<ApplicationVersionType> applicationVersions;
	private List<ConfigurationTemplateType> configurationTemplates;

	public List<ApplicationVersionType> getApplicationVersions() {
		return applicationVersions;
	}

	public List<ConfigurationTemplateType> getConfigurationTemplates() {
		return configurationTemplates;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public Object ref() {
		return getName();
	}

	public void setApplicationVersions(
			List<ApplicationVersionType> applicationVersions) {
		this.applicationVersions = applicationVersions;
	}

	public void setConfigurationTemplates(
			List<ConfigurationTemplateType> configurationTemplates) {
		this.configurationTemplates = configurationTemplates;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
