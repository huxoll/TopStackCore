package com.msi.tough.utils;

import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bouncycastle.openssl.PEMReader;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.JsonUtil;

/**
 * @author rarora
 *
 */
public class ChefUtil {
	private final static Logger logger = Appctx.getLogger(ChefUtil.class
			.getName());
	private static final String databagLockItem = "__databag__lock__";
	private static String databagLockedValue;

	/*
	 * @param databagName String name for the databag to lock
	 *
	 * @returns boolean True if worked, False otherwise.
	 */
	public static void beginDatabagUpdate(final String databagName)
			throws Exception {
		// So long as databag is update locked, the chef client
		// must NOT converge on the databag items
		ChefUtil.putDatabagItem(databagName, ChefUtil.databagLockedValue,
				Boolean.TRUE.toString());
	}

	public static String createDatabag(final String name) throws Exception {
		logger.info("createDatabag " + name);
		return ChefUtil.createDatabag(name, Boolean.FALSE);
	}

	public static String createDatabagItem(final String bag, final String item)
			throws Exception {
		logger.info("createDatabagItem " + bag + " " + item);
		return executeJson("POST", "/data/" + bag, "{\"id\":\"" + item + "\"}");
	}

	public static void createDatabagItem(final String databag,
			final String item, final String str) throws Exception {
		logger.info("createDatabagItem " + databag + " " + item + " " + str);
		createDatabag(databag);
		createDatabagItem(databag, item);
		putDatabagItem(databag, item, str);
	}

	public static String createStringToSign(final String request,
			final byte[] bs, final byte[] contentHash, final String timestamp,
			final String userId) {

		return new StringBuilder().append("Method:").append(request)
				.append("\n").append("Hashed Path:").append(new String(bs))
				.append("\n").append("X-Ops-Content-Hash:")
				.append(new String(contentHash)).append("\n")
				.append("X-Ops-Timestamp:").append(timestamp).append("\n")
				.append("X-Ops-UserId:").append(userId).toString();

	}

	public static String deleteClient(final String name) throws Exception {
		logger.info("deleteClient " + name);
		return executeJson("DELETE", "/clients/" + name, "");
	}

	public static String deleteDatabag(final String bag) throws Exception {
		logger.info("deleteDatabag " + bag);
		return executeJson("DELETE", "/data/" + bag, "");
	}

	public static String deleteDatabagItem(final String bag, final String item)
			throws Exception {
		logger.info("deleteDatabagItem " + bag + " " + item);
		return executeJson("DELETE", "/data/" + bag + "/" + item, "");
	}

	public static String deleteNode(final String name) throws Exception {
		logger.info("deleteNode " + name);
		return executeJson("DELETE", "/nodes/" + name, "");
	}

	public static String editDatabag(final String json) throws Exception {
		return executeJson("PUT", "/data", json);
	}

	public static void endDatabagUpdate(final String databagName)
			throws Exception {
		// So long as databag is update unlocked, the chef client
		// must converge on the databag items
		ChefUtil.putDatabagItem(databagName, ChefUtil.databagLockedValue,
				Boolean.FALSE.toString());
	}

	public static String executeJson(final String method,
			final String endpointPath, final String payload) throws Exception {
		final String userId = Appctx.getConfigurationBean("CHEF_USER_ID");
        assert(userId != null);
		final String privateKey = Appctx
				.getConfigurationBean("CHEF_PRIVATE_KEY");
		assert(privateKey != null);
		final String url = Appctx.getConfigurationBean("CHEF_API_URL");
        assert(url != null);
		final Map<String, String> headers = new HashMap<String, String>();
		final SimpleDateFormat iso8601DateParser = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss'Z'");
		iso8601DateParser.setTimeZone(new SimpleTimeZone(0, "UTC"));
		final String timestamp = iso8601DateParser.format(new Date());
		process(method, endpointPath, headers, payload, timestamp, userId,
				privateKey);
		final String uri = url + endpointPath;
		HttpUriRequest cmd = null;
		if (method.equals("GET")) {
			cmd = new HttpGet(uri);
		}
		if (method.equals("POST")) {
			final HttpPost post = new HttpPost(uri);
			final HttpEntity entity = new StringEntity(payload);
			post.setEntity(entity);
			cmd = post;
		}
		if (method.equals("PUT")) {
			final HttpPut put = new HttpPut(uri);
			final HttpEntity entity = new StringEntity(payload);
			put.setEntity(entity);
			cmd = put;
		}
		if (method.equals("DELETE")) {
			cmd = new HttpDelete(uri);
		}
		for (final Map.Entry<String, String> en : headers.entrySet()) {
			cmd.setHeader(en.getKey(), en.getValue());
		}
		final DefaultHttpClient cl = new DefaultHttpClient();
		final HttpResponse res = cl.execute(cmd);
		final HttpEntity resen = res.getEntity();
		final InputStream resin = resen.getContent();
		final StringBuilder sb = new StringBuilder();
		for (;;) {
			final byte[] bs = new byte[1000];
			final int i = resin.read(bs);
			if (i == -1) {
				break;
			}
			final String str = new String(Arrays.copyOf(bs, i));
			sb.append(str);
		}
		return sb.toString();
	}

	private static String executeJsonGet(final String endpoint)
			throws Exception {
		return executeJson("GET", endpoint, "");
	}

	public static String getDatabag(final String name) throws Exception {
		return executeJson("GET", "/data/" + name, "");
	}

	public static String getDatabagItem(final String bag, final String item)
			throws Exception {
		return executeJson("GET", "/data/" + bag + "/" + item, "");
	}

	public static String getNode(final String name) throws Exception {
		return executeJsonGet("/nodes/" + name);
	}

	private static byte[] hashBody(final String payload) throws Exception {
		if (payload == null) {
			return null;
		}
		return Base64.encodeBase64(SHA1(payload));
	}

	private static byte[] hashPath(final String path) throws Exception {
		return Base64.encodeBase64(SHA1(path));
	}

	public static void process(final String method, final String endpointPath,
			final Map<String, String> headers, final String payload,
			final String timestamp, final String userId, final String privateKey)
			throws Exception {
		final byte[] contentHash = hashBody(payload);
		headers.put("X-Ops-Content-Hash", new String(contentHash));
		headers.put("X-Ops-Userid", userId);
		headers.put("X-Ops-Sign", "version=1.0");
		headers.put("X-Ops-Timestamp", timestamp);
		headers.put("X-Chef-Version", "0.8.16");
		headers.put("Accept", "application/json");
		headers.put("Content-Type", "application/json");
		final String toSign = createStringToSign(method,
				hashPath(endpointPath), contentHash, timestamp, userId);
		calculateAndReplaceAuthorizationHeaders(toSign, headers, privateKey);
	}

	public static PublicKey publicKeyFromFile(final String fileName)
			throws Exception {
		final Reader frdr = new FileReader(fileName);
		final PEMReader pem = new PEMReader(frdr);
		final KeyPair kp = (KeyPair) pem.readObject();
		frdr.close();
		return kp.getPublic();
	}

	public static String putClientAsAdmin(final String client) throws Exception {
		logger.info("putClientAsAdmin " + client);
		final String payload = "{\"admin\":true,\"name\":\"" + client + "\"}";
		return executeJson("PUT", "/clients/" + client, payload);
	}

	public static String putDatabagItem(final String bag,
			final String itemName, final String item) throws Exception {
		logger.info("putDatabagItem " + bag + " " + itemName);
		return executeJson("PUT", "/data/" + bag + "/" + itemName, item);
	}

	@SuppressWarnings("unchecked")
	public static String putNodeAttribute(final String node,
			final String attrib, final String value) throws Exception {
		logger.info("putNodeAttribute " + node + " " + attrib + " " + value);
		final String s = executeJsonGet("/nodes/" + node);
		final JsonNode n = JsonUtil.load(s);
		final Map<String, Object> m = JsonUtil.toMap(n);
		Object normal = m.get("normal");
		if (normal == null) {
			normal = new HashMap<String, Object>();
		}
		if (!(normal instanceof Map)) {
			if (normal instanceof JsonNode) {
				normal = JsonUtil.toMap((JsonNode) normal);
			} else {
				throw new Exception("normal is not Map or JsonNode");
			}
		}
		final Map<String, Object> nm = (Map<String, Object>) normal;
		nm.put(attrib, value);
		m.remove("normal");
		final String payload = JsonUtil.toJsonString(m).substring(1);
		final String nms = JsonUtil.toJsonString(nm);
		final String pay = "{ \"normal\":" + nms + "," + payload;
		return executeJson("PUT", "/nodes/" + node, pay);
	}

	public static String putNodeRunlist(final String node, final String runList)
			throws Exception {
		logger.info("putNodeRunlist " + node + " " + runList);
		final String json = executeJsonGet("/nodes/" + node);
		final JsonNode jsonNode = JsonUtil.load(json);
		final Map<String, Object> nodeMap = JsonUtil.toMap(jsonNode);
		// final Object noderunList = nodeMap.get("run_list");
		nodeMap.remove("run_list");
		final String payload = JsonUtil.toJsonString(nodeMap).substring(1);
		final String pay = "{ \"run_list\":\"" + runList + "\"," + payload;
		return executeJson("PUT", "/nodes/" + node, pay);
	}

	public static PrivateKey readKeyFromFile(final String fileName)
			throws Exception {
		logger.debug("readKeyFromFile " + fileName);
		final Reader frdr = new FileReader(fileName);
		final PEMReader pem = new PEMReader(frdr);
		final KeyPair kp = (KeyPair) pem.readObject();
		frdr.close();
		return kp.getPrivate();
	}

	public static byte[] rsaDecrypt(final byte[] data, final PublicKey key)
			throws Exception {
		final Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);
		final byte[] cipherData = cipher.doFinal(data);
		return cipherData;
	}

	public static byte[] rsaEncrypt(final byte[] data, final PrivateKey pvtKey)
			throws Exception {
		final Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pvtKey);
		final byte[] cipherData = cipher.doFinal(data);
		return cipherData;
	}

	public static String searchNodes(final String search) throws Exception {
		return executeJsonGet("/search/node?q=" + search);
	}

	public static byte[] SHA1(final String text) throws Exception {
		final MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		final byte[] sha1hash = md.digest();
		return sha1hash;
	}

	public static byte[] sign(final String toSign, final String privateKey)
			throws Exception {
		final byte[] encrypted = rsaEncrypt(toSign.getBytes(),
				readKeyFromFile(privateKey));
		return Base64.encodeBase64(encrypted);
	}

	/**
	 * Use this string for the Chef Node Attribute that contains the databag
	 * name
	 */
	final public String TRANSCEND_NODE_DATABAG_ATTRIBUTE_NAME = "__TRANSCEND__DATABAG__";

	static {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	private static void calculateAndReplaceAuthorizationHeaders(
			final String toSign, final Map<String, String> headers,
			final String privateKey) throws Exception {
		final String signature = new String(sign(toSign, privateKey));
		final int len = signature.length();
		for (int i = 0;; i++) {
			if (i * 60 >= len) {
				break;
			}
			String line = null;
			if ((i + 1) * 60 <= len) {
				line = signature.substring(i * 60, (i + 1) * 60);
			} else {
				line = signature.substring(i * 60);
			}
			headers.put("X-Ops-Authorization-" + (i + 1), line);
		}
	}

	public static String canonicalPath(String path) {
		path = path.replaceAll("\\/+", "/");
		return path.endsWith("/") && path.length() > 1 ? path.substring(0,
				path.length() - 1) : path;
	}

	/**
	 * Creates a client and returns its private key
	 *
	 * @param name
	 * @return private key
	 * @throws Exception
	 */
	public static String createClient(final String name) throws Exception {
		logger.debug("createClient " + name);
		final String json = executeJson("POST", "/clients", "{\"name\":\""
				+ name + "\"}");
		final JsonNode node = JsonUtil.load(json);
		return node.get("private_key").getTextValue();
	}

	public static String createDatabag(final String name, final Boolean lockFlag)
			throws Exception {
		logger.debug("createDatabag " + name);
		final String dbag = executeJson("POST", "/data", "{\"name\":\"" + name
				+ "\"}");

		// we always must have a lock item to prevent partial convergence
		// while changes take place in bulk.
		ChefUtil.createDatabagItem(name, ChefUtil.databagLockItem);
		ChefUtil.putDatabagItem(name, ChefUtil.databagLockItem,
				lockFlag.toString());

		return dbag;
	}
}
