package com.msi.tough.engine.core;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

import com.msi.tough.cf.ParameterResource;
import com.msi.tough.core.Appctx;

/**
 * AWS CF template implementation
 * 
 * @author raj
 * 
 */
public class Template {
	private static final Logger logger = Appctx.getLogger(Template.class
			.getName());

	public static Logger getLogger() {
		return logger;
	}

	/**
	 * template script version as defined by AWS CF
	 */
	private String version;

	/**
	 * template description as defined by AWS CF
	 * 
	 */
	private String description;

	/**
	 * paramaters passed to the temaplate
	 * 
	 */
	private final Map<String, ParameterResource> parameters;

	/**
	 * mappings in the template
	 */
	private final Map<String, JsonNode> mappings;

	/**
	 * Map containing resources defined in the template
	 */
	private final Map<String, JsonNode> resources;

	/**
	 * output from the template
	 * 
	 */
	private final Map<String, JsonNode> outputs;

	public Template(final InputStream in) throws Exception {
		parameters = new HashMap<String, ParameterResource>();
		mappings = new HashMap<String, JsonNode>();
		resources = new HashMap<String, JsonNode>();
		outputs = new HashMap<String, JsonNode>();
		load(in);
	}

	public String getDescription() {
		return description;
	}

	public Map<String, JsonNode> getMappings() {
		return mappings;
	}

	public Map<String, JsonNode> getOutputs() {
		return outputs;
	}

	public Map<String, ParameterResource> getParameters() {
		return parameters;
	}

	public Map<String, JsonNode> getResources() {
		return resources;
	}

	public String getVersion() {
		return version;
	}

	/**
	 * Load a template
	 * 
	 * @param in
	 *            input stream to load template from
	 * @throws Exception
	 */
	public void load(final InputStream in) throws Exception {
		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode root = mapper.readValue(in, JsonNode.class);
		final JsonNode jv = root.findValue("ToughTemplateFormatVersion");
		if (jv != null) {
			version = jv.getTextValue();
		}

		final JsonNode jd = root.findValue("Description");
		if (jd != null) {
			description = jd.getTextValue();
		}

		final JsonNode p = root.findValue("Parameters");
		if (p != null) {
			final Iterator<String> it = p.getFieldNames();
			while (it.hasNext()) {
				final String f = it.next();
				final JsonNode pm = p.get(f);
				parameters.put(f, new ParameterResource(f, pm));
			}
		}

		final JsonNode m = root.findValue("Mappings");
		if (m != null) {
			final Iterator<String> im = m.getFieldNames();
			while (im.hasNext()) {
				final String f = im.next();
				final JsonNode pm = m.get(f);
				mappings.put(f, pm);
			}
		}

		final JsonNode r = root.findValue("Resources");
		if (r != null) {
			final Iterator<String> ir = r.getFieldNames();
			while (ir.hasNext()) {
				final String f = ir.next();
				final JsonNode pm = r.get(f);
				resources.put(f, pm);
			}
		}

		final JsonNode op = root.findValue("Outputs");
		if (op != null) {
			final Iterator<String> ip = op.getFieldNames();
			while (ip.hasNext()) {
				final String f = ip.next();
				final JsonNode pm = op.get(f);
				outputs.put(f, pm);
			}
		}
	}

	@Override
	public String toString() {
		return "version : " + version + ", description : " + description
				+ ", parameters : " + parameters;
	}
}
