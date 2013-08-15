package com.msi.tough.utils;

import org.codehaus.jackson.JsonNode;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.Assert;

import com.msi.tough.core.JsonUtil;

public class ChefUtilTest {

    @Ignore // Test isn't working currently
	@Test
	public void testClient() throws Exception {
		final String json = ChefUtil.createClient("testclient1");
		System.out.println(json);
		System.out.println(ChefUtil.deleteClient("testclient1"));
	}

    @Ignore // Test isn't working currently
	@Test
	public void testDatabag() throws Exception {
		System.out.println(ChefUtil.createDatabag("testbag1"));
		System.out.println(ChefUtil.createDatabagItem("testbag1", "item1"));
		final String databag = ChefUtil.getDatabag("testbag1");
		System.out.println(databag);
		final JsonNode json = JsonUtil.load(databag);
		System.out.println(ChefUtil.putDatabagItem("testbag1", "item1",
				"{\"id\":\"item1\",\"f\":\"fff\"}"));
		System.out.println(ChefUtil.deleteDatabagItem("testbag1", "item1"));
		System.out.println(ChefUtil.deleteDatabag("testbag1"));
	}

	/*
	 * // This test moving to ElasticacheCore
	 *
	 * @Test public void testGetDatabag() throws Exception { final ChefUtil
	 * chefUtil = new ChefUtil();
	 *
	 * final String dataBagName = "elasticache-12-testcluster"; final String
	 * dataBagItemName = "config"; final boolean cleanup = false;
	 *
	 * final String databag = chefUtil.getDatabag(dataBagName); final JsonNode
	 * json = JsonUtil.load(databag); final JsonNode errorNode =
	 * json.get("error");
	 *
	 * // Assert.isNull(errorNode) ; // Assert.notNull(errorNode); final
	 * ElasticacheConfigDatabagItem cc = new
	 * ElasticacheConfigDatabagItem(dataBagItemName, 256, 11211);
	 *
	 * String cccJson = cc.toJson() ;
	 * System.out.println(JsonUtil.toJsonPrettyPrintString(cc));
	 *
	 * if (errorNode != null) { chefUtil.createDatabag(dataBagName);
	 * chefUtil.createDatabagItem(dataBagName, dataBagItemName);
	 * chefUtil.putDatabagItem(dataBagName, dataBagItemName, cc.toJson()); }
	 *
	 * if (cleanup) { chefUtil.deleteDatabagItem(dataBagName, dataBagItemName);
	 * chefUtil.deleteDatabag(dataBagName); }
	 *
	 * //JsonUtil.toJsonPrettyPrintString(json); }
	 */
    @Ignore // Test isn't working currently
	@Test
	public void testGetNode() throws Exception {

		final ChefUtil chefUtil = new ChefUtil();

		final String nodeName = "jltest.momentumsoftware.com";

		final String node = ChefUtil.getNode(nodeName);

		final JsonNode json = JsonUtil.load(node);

		System.out.println(JsonUtil.toJsonPrettyPrintString(json));

		Assert.isTrue(true);
	}

    @Ignore // Test isn't working currently
	@Test
	public void testProcess() throws Exception {
		new ChefUtil();
		final String json = ChefUtil.executeJson("GET", "/clients", "");
		System.out.println(json);
	}

    @Ignore // Test isn't working currently
	@Test
	public void testSearchNode() throws Exception {

		// Attempt to call search (query string) always return security error
		final ChefUtil chefUtil = new ChefUtil();

		// final String nodes = chefUtil.searchNodes("name:c*") ;
		final String nodes = ChefUtil.searchNodes("name%3Ac*");

		final JsonNode jsonNodes = JsonUtil.load(nodes);

		System.out.println(JsonUtil.toJsonPrettyPrintString(jsonNodes));
	}
}
