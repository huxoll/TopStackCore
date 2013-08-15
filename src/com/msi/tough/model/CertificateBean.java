package com.msi.tough.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "certificate")
public class CertificateBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private long accountId;

	private String name;

	private String path;

	private Date uploadDate;

	@Column(length = 10240)
	private String body;

	@Column(length = 10240)
	private String chain;

	@Column(length = 10240)
	private String pvtkey;

	public long getAccountId() {
		return accountId;
	}

	public String getBody() {
		return body;
	}

	public String getChain() {
		return chain;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public String getPvtkey() {
		return pvtkey;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setAccountId(final long accountId) {
		this.accountId = accountId;
	}

	public void setBody(final String body) {
		this.body = body;
	}

	public void setChain(final String chain) {
		this.chain = chain;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	public void setPvtkey(final String pvtkey) {
		this.pvtkey = pvtkey;
	}

	public void setUploadDate(final Date uploadDate) {
		this.uploadDate = uploadDate;
	}

}
