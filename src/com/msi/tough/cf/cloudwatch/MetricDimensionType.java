package com.msi.tough.cf.cloudwatch;

import java.util.Map;

import com.msi.tough.cf.CFType;

public class MetricDimensionType extends CFType {
	private String value;

	@Override
	public Object getAtt(final String key) {
		return super.getAtt(key);
	}

	public String getValue() {
		return value;
	}

	@Override
	public String ref() {
		return getName();
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public Map<String, Object> toMap() throws Exception {
		final Map<String, Object> map = super.toMap();
		return map;
	}

	@Override
	public String typeAsString() {
		return "AWS::CloudWatch::MetricDimension";
	}

}
