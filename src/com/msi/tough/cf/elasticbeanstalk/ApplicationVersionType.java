package com.msi.tough.cf.elasticbeanstalk;

import com.msi.tough.cf.CFType;

public class ApplicationVersionType extends CFType {
	private String versionLabel;
	private SourceBundleType sourceBundle;

	public SourceBundleType getSourceBundle() {
		return sourceBundle;
	}

	public String getVersionLabel() {
		return versionLabel;
	}

	public void setSourceBundle(SourceBundleType sourceBundle) {
		this.sourceBundle = sourceBundle;
	}

	public void setVersionLabel(String versionLabel) {
		this.versionLabel = versionLabel;
	}

}
