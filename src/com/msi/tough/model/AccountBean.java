package com.msi.tough.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.msi.tough.core.StringHelper;
import com.msi.tough.security.AESSecurity;

@Entity
@Table(name = "account")
public class AccountBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name")
	private String name;

	@Column(name = "access_key")
	private String accessKey;

	@Column(name = "secret_key")
	private String secretKey;

	@Column(name = "def_security_groups")
	private String defSecurityGroups;

	@Column(name = "def_key_name")
	private String defKeyName;

	@Column(name = "emails")
	private String emails;

	@Column(name = "def_zone")
	private String defZone;

	@Column(name = "tenant")
	private String tenant;

	@Column(name = "api_username")
	private String apiUsername;
	@Column(name = "api_password")
	private String apiPassword;

	@Column(name= "role_name")
	private String roleName;

	@Column(name="enabled")
	private boolean enabled;

	// @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @JoinTable(name = "ac_inst", joinColumns = @JoinColumn(name = "ac_id"),
	// inverseJoinColumns = @JoinColumn(name = "inst_id"))
	// private Set<InstanceBean> instances;

	// @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @JoinTable(name = "ac_volume", joinColumns = @JoinColumn(name = "ac_id"),
	// inverseJoinColumns = @JoinColumn(name = "volume_id"))
	// private Set<VolumeBean> volumes;

	// @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @JoinTable(name = "ac_lb", joinColumns = @JoinColumn(name = "ac_id"),
	// inverseJoinColumns = @JoinColumn(name = "lb_id"))
	// private Set<LoadBalancerBean> loadBalancers;

	// @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @JoinTable(name = "ac_user", joinColumns = @JoinColumn(name = "ac_id"),
	// inverseJoinColumns = @JoinColumn(name = "user_id"))
	// private Set<UserBean> users;
	//
	// @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @JoinTable(name = "ac_launch_config", joinColumns = @JoinColumn(name =
	// "ac_id"), inverseJoinColumns = @JoinColumn(name = "launch_config_id"))
	// private Set<LaunchConfigBean> launchConfigs;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "ac_applications", joinColumns = @JoinColumn(name = "ac_id"), inverseJoinColumns = @JoinColumn(name = "application_id"))
	private Set<ApplicationBean> applications;

	public String getAccessKey() {
		return accessKey;
	}

	public String getApiPassword() {
		//Possibly temporary code, many accounts in our instances currently don't have api_username or api_password defined.
		if(StringHelper.isBlank(apiPassword)){
			return apiPassword;
		}
		return AESSecurity.decrypt(apiPassword);
	}

	public String getApiPasswordRaw(){
		return apiPassword;
	}

	public String getApiUsername() {
		return apiUsername;
	}

	public Set<ApplicationBean> getApplications() {
		return applications;
	}

	public String getDefKeyName() {
		return defKeyName;
	}

	public String getDefSecurityGroups() {
		return defSecurityGroups;
	}

	public String getDefZone() {
		return defZone;
	}

	public String getEmails() {
		return emails;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSecretKey() {
		return AESSecurity.decrypt(secretKey);
	}
	public String getSecretKeyRaw(){
		return secretKey;
	}

	public String getTenant() {
		return tenant;
	}
	public String getRoleName(){
	    return roleName;
	}

	public boolean getEnabled(){
	    return enabled;
	}
	public void setEnabled(boolean enabled){
	    this.enabled=enabled;
	}
	public void setRoleName(String roleName){
	    this.roleName = roleName;
	}
	public void setAccessKey(final String accessKey) {
		this.accessKey = accessKey;
	}

	// public Set<InstanceBean> getInstances() {
	// return instances;
	// }

	public void setApiPassword(final String apiPassword) {
		this.apiPassword = AESSecurity.encrypt(apiPassword);
	}

	//
	// public Set<LoadBalancerBean> getLoadBalancers() {
	// return loadBalancers;
	// }

	public void setApiUsername(final String apiUsername) {
		this.apiUsername = apiUsername;
	}

	public void setApplications(final Set<ApplicationBean> applications) {
		this.applications = applications;
	}

	// public Set<UserBean> getUsers() {
	// return users;
	// }
	//
	// public Set<VolumeBean> getVolumes() {
	// return volumes;
	// }

	public void setDefKeyName(final String defKeyName) {
		this.defKeyName = defKeyName;
	}

	public void setDefSecurityGroups(final String defSecurityGroups) {
		this.defSecurityGroups = defSecurityGroups;
	}

	public void setDefZone(final String defZone) {
		this.defZone = defZone;
	}

	public void setEmails(final String emails) {
		this.emails = emails;
	}

	public void setId(final long id) {
		this.id = id;
	}

	// public void setInstances(final Set<InstanceBean> instances) {
	// this.instances = instances;
	// }

	public void setName(final String name) {
		this.name = name;
	}

	//
	// public void setLoadBalancers(final Set<LoadBalancerBean> loadBalancers) {
	// this.loadBalancers = loadBalancers;
	// }

	public void setSecretKey(final String secretKey) {
		this.secretKey = AESSecurity.encrypt(secretKey);
	}

	public void setTenant(final String tenant) {
		this.tenant = tenant;
	}

	// public void setUsers(final Set<UserBean> users) {
	// this.users = users;
	// }
	//
	// public void setVolumes(final Set<VolumeBean> volumes) {
	// this.volumes = volumes;
	// }

	@Override
	public String toString() {
		return "'id':" + id + ", 'name':" + name;

	}
}
