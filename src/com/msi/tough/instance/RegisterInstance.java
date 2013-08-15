package com.msi.tough.instance;

import java.util.Map;

public interface RegisterInstance {
	public void registerInstance(Long acid, String loadBalancer,
			String[] newInstanceIds, String[] removeIds, Map<String, Object> map);
}
