package com.msi.tough.instance;

import java.util.Map;

import org.hibernate.Session;

import com.msi.tough.model.InstanceBean;
import com.msi.tough.model.LaunchConfigBean;

public class CreateInstanceCLI implements CreateInstance {
	private Map<String, Object> config;

	@Override
	public InstanceBean createInstace(final Session s,
			final LaunchConfigBean launch, String avzones) {
        throw new UnsupportedOperationException("No longer supported.");
	}

	public Map<String, Object> getConfig() {
		return config;
	}

	public void setConfig(final Map<String, Object> config) {
		this.config = config;
	}
}
