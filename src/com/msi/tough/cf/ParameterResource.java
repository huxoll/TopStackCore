package com.msi.tough.cf;

import org.codehaus.jackson.JsonNode;

import com.msi.tough.engine.resource.Resource;

public class ParameterResource implements Resource {
	private final String name;
	private final String type;
	private final String def;
	private final String description;
	private Object value;

	public ParameterResource(final String name, final JsonNode pm) {
		this.name = name;
		type = pm.get("Type").getTextValue();
		if (pm.get("Default") != null) {
			def = pm.get("Default").getTextValue();
		} else {
			def = null;
		}
		if (pm.get("Description") != null) {
			description = pm.get("Description").getTextValue();
		} else {
			description = null;
		}
	}

	public ParameterResource(final String name, final String value) {
		this.name = name;
		type = "String";
		this.value = value;
		def = null;
		description = null;
	}

	@Override
	public Object getAtt(final String key) {
		if (key.equals("Value")) {
			return getValue();
		}
		return null;
	}

	public String getDef() {
		return def;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String ref() {
		if (value == null) {
			return null;
		}
		return value.toString();
	}

	public void setValue(final Object value) {
		this.value = value;
	}

}
