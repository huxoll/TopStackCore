package com.msi.tough.engine.core;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.hibernate.Session;

import com.msi.tough.engine.resource.Resource;

public interface Task {
	public List<Resource> execute(Session s, String parent, String name,
			String type, final Template template,
			Map<String, Object> properties, JsonNode metadata) throws Exception;

}
