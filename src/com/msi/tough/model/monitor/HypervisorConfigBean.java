/**
 * 
 */
package com.msi.tough.model.monitor;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This is all the necessary information to connect to a hypervisor. mduong:
 * added hibernate annotations
 * 
 * @author heathm mduong
 * 
 */

@Entity
@Table(name = "hypervisors")
public class HypervisorConfigBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "username")
	private String username;

	@Column(name = "type")
	private String type;

	@Column(name = "protocol")
	private String proto;

	@Column(name = "password")
	private String password; // TODO: make encrypted

	@Column(name = "host")
	private String host;

	@Column(name = "enable")
	private String enable;

	@Column(name = "account_id")
	private Integer accountId;

	@ElementCollection
	@Column(name = "options")
	private Map<String, String> options = new HashMap<String, String>();

	public HypervisorConfigBean() {

	}

	public HypervisorConfigBean(final String given_username,
			final String given_type, final String given_proto,
			final String given_password, final String given_host,
			final Map<String, String> given_options) {
		username = given_username;
		type = given_type;
		proto = given_proto;
		password = given_password;
		host = given_host;
		options = given_options;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public String getEnable() {
		return enable;
	}

	public String getHost() {
		return host;
	}

	public long getId() {
		return id;
	}

	public String getOption(final String name) {
		return options.get(name);
	}

	public Map<String, String> getOptions() {
		return options;
	}

	public String getPassword() {
		return password;
	}

	public String getProto() {
		return proto;
	}

	public String getType() {
		return type;
	}

	public String getUsername() {
		return username;
	}

	public void setAccountId(final Integer accountId) {
		this.accountId = accountId;
	}

	public void setEnable(final String enable) {
		this.enable = enable;
	}

	public void setHost(final String host) {
		this.host = host;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setOptions(final Map<String, String> options) {
		this.options = options;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public void setProto(final String proto) {
		this.proto = proto;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

}
