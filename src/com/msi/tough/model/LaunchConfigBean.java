package com.msi.tough.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "launch_config")
public class LaunchConfigBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "user_id")
	private long userId;

	@Column(name = "name")
	private String name;

	@Column(name = "created_time")
	private Date createdTime;

	@Column(name = "image_id")
	private String imageId;

	@Column(name = "inst_type")
	private String instType;

	@Column(name = "kernel")
	private String kernel;

	@Column(name = "ssh_key")
	private String key;

	@Column(name = "ramdisk")
	private String ramdisk;

	@Column(name = "sec_grps")
	private String secGrps;

	@Column(name = "blk_devs")
	private String blk_devs;

	@Column(name = "user_data")
	private String userData;

	@Column(name = "setup", length = 2048)
	private String setup;

	@Column(name = "shutdown", length = 2048)
	private String shutdown;

	@Column(name = "chef_roles", length = 100)
	private String chefRoles;

	@Column(name = "wait_hook", length = 100)
	private String waitHookClass;

	@Column(name = "databag", length = 100)
	private String databag;

	@Column(name = "mappings")
	@Lob
	private byte[] mappings;

	public String getBlk_devs() {
		return blk_devs;
	}

	public String getChefRoles() {
		return chefRoles;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public String getDatabag() {
		return databag;
	}

	public long getId() {
		return id;
	}

	public String getImageId() {
		return imageId;
	}

	public String getInstType() {
		return instType;
	}

	public String getKernel() {
		return kernel;
	}

	public String getKey() {
		return key;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Serializable> getMappingdsAsMap() {
		Map<String, Serializable> m = null;
		try {
			final ObjectInputStream is = new ObjectInputStream(
					new ByteArrayInputStream(getMappings()));
			m = (Map<String, Serializable>) is.readObject();
			is.close();
		} catch (final Exception e) {
			return new HashMap<String, Serializable>();
		}
		return m;
	}

	public byte[] getMappings() {
		return mappings;
	}

	public String getName() {
		return name;
	}

	public String getRamdisk() {
		return ramdisk;
	}

	public String getSecGrps() {
		return secGrps;
	}

	public String getSetup() {
		return setup;
	}

	public String getShutdown() {
		return shutdown;
	}

	public String getUserData() {
		return userData;
	}

	public long getUserId() {
		return userId;
	}

	public String getWaitHookClass() {
		return waitHookClass;
	}

	public void setBlk_devs(final String blkDevs) {
		blk_devs = blkDevs;
	}

	public void setChefRoles(final String chefRoles) {
		this.chefRoles = chefRoles;
	}

	public void setCreatedTime(final Date createdTime) {
		this.createdTime = createdTime;
	}

	public void setDatabag(final String databag) {
		this.databag = databag;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setImageId(final String imageId) {
		this.imageId = imageId;
	}

	public void setInstType(final String instType) {
		this.instType = instType;
	}

	public void setKernel(final String kernel) {
		this.kernel = kernel;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public void setMappings(final byte[] mappings) {
		this.mappings = mappings;
	}

	public void setMappings(final Map<String, Serializable> args) {
		try {
			final ByteArrayOutputStream bs = new ByteArrayOutputStream();
			final ObjectOutputStream os = new ObjectOutputStream(bs);
			os.writeObject(args);
			final byte[] ba = bs.toByteArray();
			os.close();
			setMappings(ba);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setRamdisk(final String ramdisk) {
		this.ramdisk = ramdisk;
	}

	public void setSecGrps(final String secGrps) {
		this.secGrps = secGrps;
	}

	public void setSetup(final String setup) {
		this.setup = setup;
	}

	public void setShutdown(final String shutdown) {
		this.shutdown = shutdown;
	}

	public void setUserData(final String userData) {
		this.userData = userData;
	}

	public void setUserId(final long userId) {
		this.userId = userId;
	}

	public void setWaitHookClass(final String waitHookClass) {
		this.waitHookClass = waitHookClass;
	}

}
