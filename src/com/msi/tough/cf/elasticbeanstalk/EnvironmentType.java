package com.msi.tough.cf.elasticbeanstalk;

import java.util.List;

import com.msi.tough.cf.CFType;

public class EnvironmentType extends CFType {
	private String applicationName;
	private String cnamePrefix;
	private List<OptionSettingsType> optionSettings;
	private List<OptionSettingsType> optionsToRemove;
	private String solutionStackName;
	private String templateName;
	private String versionLabel;
	private String desc;

	public String getApplicationName() {
		return applicationName;
	}

	public String getCnamePrefix() {
		return cnamePrefix;
	}

	public String getDesc() {
		return desc;
	}

	public List<OptionSettingsType> getOptionSettings() {
		return optionSettings;
	}

	public List<OptionSettingsType> getOptionsToRemove() {
		return optionsToRemove;
	}

	public String getSolutionStackName() {
		return solutionStackName;
	}

	public String getTemplateName() {
		return templateName;
	}

	public String getVersionLabel() {
		return versionLabel;
	}

	@Override
	public Object ref() {
		return getName();
	}

	public void setApplicationName(final String applicationName) {
		this.applicationName = applicationName;
	}

	public void setCnamePrefix(final String cnamePrefix) {
		this.cnamePrefix = cnamePrefix;
	}

	public void setDesc(final String desc) {
		this.desc = desc;
	}

	public void setOptionSettings(final List<OptionSettingsType> optionSettings) {
		this.optionSettings = optionSettings;
	}

	public void setOptionsToRemove(
			final List<OptionSettingsType> optionsToRemove) {
		this.optionsToRemove = optionsToRemove;
	}

	public void setSolutionStackName(final String solutionStackName) {
		this.solutionStackName = solutionStackName;
	}

	public void setTemplateName(final String templateName) {
		this.templateName = templateName;
	}

	public void setVersionLabel(final String versionLabel) {
		this.versionLabel = versionLabel;
	}

}
