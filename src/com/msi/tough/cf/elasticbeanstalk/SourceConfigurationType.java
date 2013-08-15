package com.msi.tough.cf.elasticbeanstalk;

import com.msi.tough.cf.CFType;

public class SourceConfigurationType extends CFType {
	private String applicationTemplate;
	private String applicationStackName;

	public String getApplicationStackName() {
		return applicationStackName;
	}

	public String getApplicationTemplate() {
		return applicationTemplate;
	}

	public void setApplicationStackName(String applicationStackName) {
		this.applicationStackName = applicationStackName;
	}

	public void setApplicationTemplate(String applicationTemplate) {
		this.applicationTemplate = applicationTemplate;
	}

}
