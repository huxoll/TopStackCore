package com.msi.tough.cf.elasticbeanstalk;

import com.msi.tough.cf.CFType;

public class OptionSettingsType extends CFType {
	private String nameSpace;
	private String optionName;
	private String value;

	public String getNameSpace() {
		return nameSpace;
	}

	public String getOptionName() {
		return optionName;
	}

	public String getValue() {
		return value;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
