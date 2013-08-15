package com.msi.tough.cf.elasticloadbalancing;

import java.util.List;
import java.util.Map;

import com.msi.tough.cf.CFType;
import com.msi.tough.core.MapUtil;

public class ListenerType extends CFType {
	private String instancePort;
	private String loadBalancerPort;
	private String protocol;
	private String SSLCertificateId;
	private List<PolicyNameType> policyNames;

	public String getInstancePort() {
		return instancePort;
	}

	public String getLoadBalancerPort() {
		return loadBalancerPort;
	}

	public List<PolicyNameType> getPolicyNames() {
		return policyNames;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getSSLCertificateId() {
		return SSLCertificateId;
	}

	public void setInstancePort(final String instancePort) {
		this.instancePort = instancePort;
	}

	public void setLoadBalancerPort(final String loadBalancerPort) {
		this.loadBalancerPort = loadBalancerPort;
	}

	public void setPolicyNames(final List<PolicyNameType> policyNames) {
		this.policyNames = policyNames;
	}

	public void setProtocol(final String protocol) {
		this.protocol = protocol;
	}

	public void setSSLCertificateId(final String sSLCertificateId) {
		SSLCertificateId = sSLCertificateId;
	}

	@Override
	public Map<String, Object> toMap() throws Exception {
		final Map<String, Object> map = MapUtil.create("InstancePort",
				instancePort, "LoadBalancerPort", loadBalancerPort, "Protocol",
				protocol);
		if (SSLCertificateId != null) {
			map.put("SSLCertificateId", SSLCertificateId);
		}
		// private final List<PolicyNameType> policyNames;
		return map;
	}
}
