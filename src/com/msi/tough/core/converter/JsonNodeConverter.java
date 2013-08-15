package com.msi.tough.core.converter;

import org.apache.commons.beanutils.converters.AbstractConverter;
import org.codehaus.jackson.JsonNode;

import com.msi.tough.core.JsonUtil;
import com.msi.tough.core.StringHelper;

public class JsonNodeConverter extends AbstractConverter {

	@Override
	protected String convertToString(final Object value) {
		return JsonUtil.toJsonString(value);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Object convertToType(final Class type, final Object value) {
		if (value instanceof JsonNode) {
			return value;
		}
		if (type.equals(String.class)) {
			return convertToString(value);
		}
		if (value instanceof String) {
			return JsonUtil.load(StringHelper.toInputStream((String) value),
					true);
		}
		throw new IllegalArgumentException("Input value is not of correct type");
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getDefaultType() {
		return null;
	}
}
