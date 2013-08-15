package com.msi.tough.engine.aws.elasticache;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.msi.tough.core.converter.ToJson;

// This class contains parameters that are not in a parameter group
public class ElasticacheConfigDatabagItem implements ToJson {

	private final String Id;
	private int memory;
	private int port;
	private String user;
	private String listen;
	private String serviceUrl;
	private String accessKey;
	private long acid;
	private String stackId;

	public ElasticacheConfigDatabagItem(final String Id) {
		this.Id = Id;
		user = "nobody";
		listen = "0.0.0.0";
	}

	public ElasticacheConfigDatabagItem(final String Id, final int memory,
			final int port, final String serviceUrl, final String accessKey,
			final long acid, final String stackId) {
		this(Id);
		this.memory = memory;
		this.port = port;
		this.serviceUrl = serviceUrl;
		this.accessKey = accessKey;
		this.acid = acid;
		this.stackId = stackId;
	}

	// public ElasticacheConfigDatabagItem( String Id, int memory, String
	// accessKey ){
	// this(Id, memory, 11211, accessKey );
	// }

	@JsonProperty("accesskey")
	public String getAccessKey() {
		return accessKey;
	}

	@JsonProperty("AcId")
	public long getAcid() {
		return acid;
	}

	@JsonProperty("serviceurl")
	public String getHostname() {
		return serviceUrl;
	}

	@JsonProperty("id")
	public String getId() {
		return Id;
	}

	@JsonProperty("listen")
	public String getListen() {
		return listen;
	}

	@JsonProperty("memory")
	public int getMemory() {
		return memory;
	}

	@JsonProperty("port")
	public int getPort() {
		return port;
	}

	@JsonProperty("StackId")
	public String getStackId() {
		return stackId;
	}

	@JsonProperty("user")
	public String getUser() {
		return user;
	}

	public void setListen(final String listen) {
		this.listen = listen;
	}

	public void setMemory(final int memory) {
		this.memory = memory;
	}

	public void setPort(final int port) {
		this.port = port;
	}

	public void setUser(final String user) {
		this.user = user;
	}

	@Override
	public String toJson() throws JsonGenerationException,
			JsonMappingException, IOException {
		final ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
}
