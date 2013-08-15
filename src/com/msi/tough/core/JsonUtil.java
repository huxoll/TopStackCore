package com.msi.tough.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.node.TextNode;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.msi.tough.model.AccountBean;

public class JsonUtil {
	private static final MappingJsonFactory fac = new MappingJsonFactory();

	public static JsonNode load(final InputStream in, final boolean closeOnRead) {
		try {
			final JsonParser parser = fac.createJsonParser(in);
			parser.configure(Feature.ALLOW_COMMENTS, true);
			parser.configure(Feature.ALLOW_SINGLE_QUOTES, true);
			parser.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			final JsonNode node = parser.readValueAsTree();
			return node;
		} catch (final JsonProcessingException e) {
			// return null if Json was invalid
			e.printStackTrace();
			return null;
		} catch (final Exception e) {
			throw new BaseException(e);
		} finally {
			if (closeOnRead) {
				try {
					in.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public static JsonNode load(final String in) {
		final ByteArrayInputStream as = new ByteArrayInputStream(in.getBytes());
		return load(as, true);
	}

	public static String toJsonPrettyPrintString(final Object in)
			throws IOException {
		final StringWriter sw = new StringWriter();
		final ObjectMapper mapper = new ObjectMapper();
		final JsonGenerator jsonGenerator = mapper.getJsonFactory()
				.createJsonGenerator(sw);
		jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
		mapper.writeValue(jsonGenerator, in);
		return sw.toString();
	}

	public static String toJsonString(final Object in) {
		final ObjectMapper mapper = new ObjectMapper();
		final StringWriter sw = new StringWriter();
		try {
			mapper.writeValue(sw, in);
			final String s = sw.toString();
			sw.close();
			return s;
		} catch (final Exception e) {
			throw new BaseException(e);
		}
	}

	public static String toJsonStringIgnoreNullValues(final Object in) {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.getSerializationConfig().setSerializationInclusion(
				Inclusion.NON_NULL);
		final StringWriter sw = new StringWriter();
		try {
			mapper.writeValue(sw, in);
			final String s = sw.toString();
			sw.close();
			return s;
		} catch (final Exception e) {
			throw new BaseException(e);
		}
	}

	public static List<Object> toList(final JsonNode node) {
		if (!node.isArray()) {
			return null;
		}
		final List<Object> list = new LinkedList<Object>();
		final Iterator<JsonNode> it = node.getElements();
		while (it.hasNext()) {
			final JsonNode s = it.next();
			final Map<String, Object> temp = toMap(s);
			list.add(temp);
		}
		return list;
	}

	public static Map<String, Object> toMap(final JsonNode node) {
		if (!node.isContainerNode()) {
			return null;
		}
		final Map<String, Object> map = new HashMap<String, Object>();
		final Iterator<String> fs = node.getFieldNames();
		while (fs.hasNext()) {
			final String f = fs.next();
			final JsonNode vn = node.get(f);
			if (vn instanceof TextNode) {
				map.put(f, vn.getTextValue());
			} else {
				map.put(f, vn);
			}
		}
		return map;
	}

	public static LinkedHashMap<String, Object> toSingleHash(final String s,
			final Object o) {
		final LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
		m.put(s, o);
		return m;
	}

	public static List<String> toStringList(final JsonNode node) {
		if (!node.isArray()) {
			return null;
		}
		final List<String> list = new LinkedList<String>();
		final Iterator<JsonNode> it = node.getElements();
		while (it.hasNext()) {
			final JsonNode s = it.next();
			final String temp = s.getTextValue();
			list.add(temp);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<String> toStringList(final Object rsecurityGroups) {
		if (rsecurityGroups == null) {
			return null;
		}
		final List<String> ret = new ArrayList<String>();
		if (!(rsecurityGroups instanceof List)) {
			throw new RuntimeException("Should be a list type "
					+ rsecurityGroups.toString());
		}
		final List<Object> l = (List) rsecurityGroups;
		for (final Object o : l) {
			if (o instanceof JsonNode) {
				final JsonNode j = (JsonNode) o;
				ret.add(j.getTextValue());
			} else {
				ret.add(o.toString());
			}
		}
		return ret;
	}
	//take a list of accounts and convert to a json output
	public static String accBeansToJsonList(final List<AccountBean> l) throws JsonGenerationException, IOException{
		final StringWriter sw = new StringWriter();
		final JsonFactory jfactory = new JsonFactory();
		final JsonGenerator json = jfactory.createJsonGenerator(sw);
		json.setPrettyPrinter(new DefaultPrettyPrinter());
		
		json.writeStartArray();
		for(AccountBean ac: l){
			//A bit weak, but if access key is null or blank, we assume it's the system-account, and skip it;
			if(StringHelper.isBlank(ac.getAccessKey())){
				continue;
			}
			accBeanToJsonObjectHelper(json, ac);
		}
		json.writeEndArray();
		json.close();
		return sw.toString();
	}
	//Used for converting an account bean to a JSON object
    public static String accBeanToJsonObject(final AccountBean ac) throws IOException{
        final StringWriter sw = new StringWriter();
        final JsonFactory jfactory = new JsonFactory();
        final JsonGenerator json = jfactory.createJsonGenerator(sw);
        json.setPrettyPrinter(new DefaultPrettyPrinter());
        accBeanToJsonObjectHelper(json, ac);
        json.close();
        return sw.toString();
    }

    public static void accBeanToJsonObjectHelper(final JsonGenerator json, final AccountBean ac ) throws JsonGenerationException, IOException{
		json.writeStartObject();
		
		json.writeStringField("Id", "" + ac.getId());
		json.writeStringField("UserName", ac.getName());
		if(ac.getSecretKeyRaw()==null || ac.getSecretKeyRaw().equals(""))
		    json.writeStringField("SecretKey", "");
		else
		    json.writeStringField("SecretKey", "*****");
		json.writeStringField("AccessKey", ac.getAccessKey());
		json.writeStringField("APIUsername", ac.getApiUsername());
		if(ac.getApiPasswordRaw()==null || ac.getApiPasswordRaw().equals(""))
		    json.writeStringField("APIPassword", "");
		else
		    json.writeStringField("APIPassword", "*****");
		json.writeStringField("Email", ac.getEmails());
		json.writeStringField("APITenant", ac.getTenant());
		json.writeStringField("KeyName", ac.getDefKeyName());
		json.writeStringField("CloudName", ac.getDefZone());
		json.writeStringField("RoleName", ac.getRoleName());
		json.writeStringField("Enabled", ac.getEnabled()+"");
		
		json.writeEndObject();
	}
    public static boolean isValidJSON(String result){
        try{
            if(result.charAt(0)=='['){
                new JSONArray(result);
            }else
                new JSONObject(result);
            return true;
        }
        catch(JSONException ex){
            return false;
        }
    }
    public static String getValueFromJSON(String json, String key) throws JSONException{
        final JSONObject jsonObj = new JSONObject(json);
        return (String) jsonObj.get(key);
    }
	
	
}
