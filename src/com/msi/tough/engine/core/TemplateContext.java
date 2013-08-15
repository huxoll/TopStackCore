package com.msi.tough.engine.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.ConvertUtils;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.utils.ConfigurationUtil;

/**
 * Execution context for the templates. All resources created in the template
 * are stored in this context.
 *
 * @author raj
 *
 */
public class TemplateContext extends HashMap<String, Object> {
	/**
	 *
	 */
	private static final long serialVersionUID = -1222178364785522321L;
	private static Logger logger = Appctx.getLogger(TemplateContext.class
			.getName());

	private Map<String, JsonNode> mappings;

	public TemplateContext() {
	}

	public TemplateContext(final Map<String, JsonNode> mappings) {
		this.mappings = mappings;
	}

	public Map<String, JsonNode> getMappings() {
		return mappings;
	}

	/**
	 * Get string value for a json node
	 *
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public String getStringValue(final JsonNode node) throws Exception {
		final Object obj = getValue(node);
		if (obj instanceof String) {
			return (String) obj;
		}
		if (obj instanceof JsonNode) {
			return ((JsonNode) obj).getTextValue();
		}
		final ConvertUtils convutils = Appctx.getBean("ConvertUtils");
		return (String) convutils.convert(obj, String.class);
	}

	/**
	 * get object value for a json node. This applies formula, reference and
	 * other JSON functions if defined
	 *
	 * @param node
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getValue(final JsonNode node) throws Exception {
		if (node == null) {
			return null;
		}
		if (!node.isContainerNode()) {
			return node.getTextValue();
		}
		if (node instanceof ArrayNode) {
			final ArrayNode an = (ArrayNode) node;
			final Iterator<JsonNode> it = an.getElements();
			final List<Object> l = new ArrayList<Object>();
			while (it.hasNext()) {
				l.add(getValue(it.next()));
			}
			return l;
		}
		final Iterator<String> it = node.getFieldNames();
		final Map<String, Object> outnode = new HashMap<String, Object>();
		while (it.hasNext()) {
			final String f = it.next();
			final JsonNode n = node.get(f);
			logger.debug("Now processing " + n.getTextValue());
			if (f.equals("Ref")) {
				final String onm = n.getTextValue();
				final Object o = get(onm);
				if (o == null) {
					logger.debug("Ref not found " + onm);
					throw new RefNotFoundException(onm);
				} else if (o instanceof Boolean) {
					return o.toString();
				} else if (o instanceof List) {
					return ((Resource) ((List) o).get(0)).ref();
				} else if (o instanceof String) {
					return o;
				} else {
					return ((Resource) o).ref();
				}
			}
			if (f.startsWith("Fn::")) {
				final String fun = f.substring("Fn::".length());
				if (fun.equals("FindInMap")) {
					final String map = getStringValue(n.get(0));
					final String map1 = getStringValue(n.get(1));
					final String map2 = getStringValue(n.get(2));
					final JsonNode mapping = mappings.get(map);
					if (mapping == null) {
						logger.debug("mapping not found " + map + " " + map1
								+ " " + map2);
					}
					final JsonNode m2 = mapping.get(map1);
					if (m2 == null) {
						logger.debug("m2 not found " + map + " " + map1 + " "
								+ map2);
					}
					return getStringValue(m2.get(map2));
				}
				if (fun.equals("Concat")) {
					final StringBuilder sb = new StringBuilder();
					final Iterator<JsonNode> itr = n.getElements();
					while (itr.hasNext()) {
						final String s = (String) getValue(itr.next());
						sb.append(s);
					}
					return sb.toString();
				}
				if (fun.equals("Join")) {
					final StringBuilder sb = new StringBuilder();
					final String sep = getStringValue(n.get(0));
					boolean addsep = false;
					final Iterator<JsonNode> itr = n.get(1).getElements();
					while (itr.hasNext()) {
						if (addsep) {
							sb.append(sep);
						}
						addsep = true;
						final String s = getStringValue(itr.next());
						sb.append(s);
					}
					return sb.toString();
				}
				if (fun.equals("Configuration")) {
					final List<String> list = new ArrayList<String>();
					final Iterator<JsonNode> itr = n.getElements();
					while (itr.hasNext()) {
						final String s = getStringValue(itr.next());
						list.add(s);
					}
					return ConfigurationUtil.getConfiguration(list);
				}
				if (fun.equals("Base64")) {
					return getStringValue(n);
				}
				if (fun.equals("GetAtt")) {
					final String id = (String) getValue(n.get(0));
					final String att = (String) getValue(n.get(1));
					final Object obj = get(id);
					if (obj == null) {
						logger.debug("Object not found " + id);
					}
					if (obj instanceof List) {
						return ((List<Resource>) obj).get(0).getAtt(att);
					}
					return ((Resource) obj).getAtt(att);
				}
				if (fun.equals("GetVal")) {
					final String id = (String) getValue(n.get(0));
					final String att = (String) getValue(n.get(1));
					final Map<String, Object> obj = (Map<String, Object>) get(id);
					return obj.get(att);
				}
				throw new Exception("Function not recoginzed " + fun);
			}
			outnode.put(f, getValue(n));
		}
		return JsonUtil.load(JsonUtil.toJsonString(outnode));
	}

	public Object ref(final String key) {
		return ((Resource) get(key)).ref();

	}

	public void setMappings(final Map<String, JsonNode> mappings) {
		this.mappings = mappings;
	}

}
