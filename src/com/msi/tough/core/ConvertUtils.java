package com.msi.tough.core;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.codehaus.jackson.JsonNode;

import com.msi.tough.core.converter.JsonNodeConverter;

public class ConvertUtils extends ConvertUtilsBean {
	public ConvertUtils() {
		register(new JsonNodeConverter(), JsonNode.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object convert(Object value, Class targetType) {
		if (value.getClass() == targetType) {
			return value;
		}
		return super.convert(value, targetType);
	}
}
