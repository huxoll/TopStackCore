package com.msi.tough.instance;

import java.util.Map;

public class DeleteInstanceCLI implements DeleteInstance {
	private Map<String, Object> config;

	@Override
	public void deleteInstance(String[] instanceIds) {
	    throw new UnsupportedOperationException("No longer supported.");
	}

	public Map<String, Object> getConfig() {
		return config;
	}

	public void setConfig(Map<String, Object> config) {
		this.config = config;
	}

}
