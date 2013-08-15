package com.msi.tough.cf.elasticloadbalancing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.msi.tough.cf.CFType;
import com.msi.tough.cf.ec2.InstanceType;

/**
 * Loadbalancer resource
 * 
 * @author raj
 * 
 */
/**
 * @author raj
 * 
 */
public class LoadBalancerType extends CFType {
	private String availabilityZones;
	private HealthCheckType healthCheck;
	private List<String> instances;
	private List<ListenerType> listeners;
	private List<AppCookieStickinessPolicyType> appCookieStickinessPolicy;
	private List<LBCookieStickinessPolicyType> lbCookieStickinessPolicy;
	private List<String> securityGroups;
	private List<String> subnets;
	private List<InstanceType> instanceData;
	private String dnsName;
	private String ec2SecGroup;
	private String ec2SecGroupId;
	private String certBody;
	private String certChain;
	private String certPvtKey;

	public List<AppCookieStickinessPolicyType> getAppCookieStickinessPolicy() {
		return appCookieStickinessPolicy;
	}

	@Override
	public Object getAtt(final String key) {
		// CanonicalHostedZoneName
		// CanonicalHostedZoneNameID
		// SourceSecurityGroup.GroupName
		// SourceSecurityGroup.OwnerAlias
		if (key.equals("DNSName")) {
			return dnsName;
		}
		return super.getAtt(key);
	}

	public String getAvailabilityZones() {
		return availabilityZones;
	}

	public String getCertBody() {
		return certBody;
	}

	public String getCertChain() {
		return certChain;
	}

	public String getCertPvtKey() {
		return certPvtKey;
	}

	public String getDnsName() {
		return dnsName;
	}

	public String getEc2SecGroup() {
		return ec2SecGroup;
	}

	public String getEc2SecGroupId() {
		return ec2SecGroupId;
	}

	public HealthCheckType getHealthCheck() {
		return healthCheck;
	}

	public List<InstanceType> getInstanceData() {
		return instanceData;
	}

	public List<String> getInstances() {
		return instances;
	}

	public List<LBCookieStickinessPolicyType> getLbCookieStickinessPolicy() {
		return lbCookieStickinessPolicy;
	}

	public List<ListenerType> getListeners() {
		return listeners;
	}

	public List<String> getSecurityGroups() {
		return securityGroups;
	}

	public List<String> getSubnets() {
		return subnets;
	}

	@Override
	public String ref() {
		return getName();
	}

	public void setAppCookieStickinessPolicy(
			final List<AppCookieStickinessPolicyType> appCookieStickinessPolicy) {
		this.appCookieStickinessPolicy = appCookieStickinessPolicy;
	}

	public void setAvailabilityZones(final String availabilityZones) {
		this.availabilityZones = availabilityZones;
	}

	public void setCertBody(final String certBody) {
		this.certBody = certBody;
	}

	public void setCertChain(final String certChain) {
		this.certChain = certChain;
	}

	public void setCertPvtKey(final String certPvtKey) {
		this.certPvtKey = certPvtKey;
	}

	public void setDnsName(final String dnsName) {
		this.dnsName = dnsName;
	}

	public void setEc2SecGroup(final String securityGroup) {
		ec2SecGroup = securityGroup;
	}

	public void setEc2SecGroupId(final String ec2SecGroupId) {
		this.ec2SecGroupId = ec2SecGroupId;
	}

	public void setHealthCheck(final HealthCheckType healthCheck) {
		this.healthCheck = healthCheck;
	}

	public void setInstanceData(final List<InstanceType> instanceData) {
		this.instanceData = instanceData;
	}

	public void setInstances(final List<String> instances) {
		this.instances = instances;
	}

	public void setLbCookieStickinessPolicy(
			final List<LBCookieStickinessPolicyType> lbCookieStickinessPolicy) {
		this.lbCookieStickinessPolicy = lbCookieStickinessPolicy;
	}

	public void setListeners(final List<ListenerType> listeners) {
		this.listeners = listeners;
	}

	public void setSecurityGroups(final List<String> securityGroups) {
		this.securityGroups = securityGroups;
	}

	public void setSubnets(final List<String> subnets) {
		this.subnets = subnets;
	}

	@Override
	public Map<String, Object> toMap() throws Exception {
		final Map<String, Object> map = super.toMap();
		map.put("AvailabilityZones", availabilityZones);
		if (healthCheck != null) {
			map.put("HealthCheck", healthCheck.toMap());
		}
		if (instances != null) {
			map.put("Instances", instances);
		}
		if (instanceData != null) {
			map.put("InstanceData", instanceData);
		}
		if (listeners != null) {
			final List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
			for (final ListenerType o : listeners) {
				l.add(o.toMap());
			}
			map.put("Listeners", l);
		}

		if (appCookieStickinessPolicy != null) {
			final List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
			for (final AppCookieStickinessPolicyType o : appCookieStickinessPolicy) {
				l.add(o.toMap());
			}
			map.put("AppCookieStickinessPolicy", l);
		}
		if (lbCookieStickinessPolicy != null) {
			final List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
			for (final LBCookieStickinessPolicyType o : lbCookieStickinessPolicy) {
				l.add(o.toMap());
			}
			map.put("LBCookieStickinessPolicy", l);
		}
		if (certBody != null) {
			map.put("CertBody", certBody);
		}
		if (certChain != null) {
			map.put("CertChain", certChain);
		}
		if (certPvtKey != null) {
			map.put("CertPvtKey", certPvtKey);
		}
		return map;
	}

	@Override
	public String typeAsString() {
		return "AWS::ElasticLoadBalancing::LoadBalancer";
	}

}
