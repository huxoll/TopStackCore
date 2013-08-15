package com.msi.tough.engine.aws.ec2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.IntNode;
import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.network.Direction;
import org.dasein.cloud.network.FirewallSupport;
import org.dasein.cloud.network.NetworkServices;
import org.dasein.cloud.network.Protocol;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.ec2.AuthorizeSecurityGroupIngressType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.utils.ConfigurationUtil;
import com.msi.tough.utils.Constants;
import com.msi.tough.utils.SecurityGroupUtils;

public class SecurityGroupIngress extends BaseProvider {
	private static Logger logger = Appctx.getLogger(SecurityGroupIngress.class
			.getName());
	public static String TYPE = "AWS::EC2::SecurityGroupIngress";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		String avz = (String) call.getProperty(Constants.AVAILABILITYZONE);
		if (avz == null) {
			avz = ac.getDefZone();
		}
		String groupName = (String) call.getProperty(Constants.GROUPNAME);
		if (groupName == null) {
			groupName = (String) call.getProperty(Constants.GROUPID);
		}
		if (groupName == null) {
			throw new RuntimeException("SecurityGroupName cannot be blank");
		}
		groupName = SecurityGroupUtils.getProviderId(groupName, avz,
				call.getAc());

		String ipProtocol = (String) call.getProperty(Constants.IPPROTOCOL);
		if (ipProtocol == null) {
			ipProtocol = "tcp";
		}
		final String cidrIp = (String) call.getProperty(Constants.CIDRIP);
		final String sourceSecurityGroupName = (String) call
				.getProperty(Constants.SOURCESECURITYGROUPNAME);
		final String sourceSecurityGroupOwnerId = (String) call
				.getProperty(Constants.SOURCESECURITYGROUPOWNERID);
		if (cidrIp == null && sourceSecurityGroupName == null) {
			throw new RuntimeException(
					"CidrIP and SourceSecurityGroupName both cannot be blank");
		}
		if (cidrIp != null && sourceSecurityGroupName != null) {
			throw new RuntimeException(
					"CidrIP and SourceSecurityGroupName both cannot be provided");
		}

		final AuthorizeSecurityGroupIngressType ret = new AuthorizeSecurityGroupIngressType();
		// final BasicAWSCredentials cred = new BasicAWSCredentials(
		// ac.getAccessKey(), ac.getSecretKey());
		// final AmazonEC2Client ec2 = new AmazonEC2Client(cred);
		// final String endpoint = (String) ConfigurationUtil
		// .getConfiguration(Arrays
		// .asList(new String[] { "EC2_URL", avz }));
		final String retry = (String) ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { "AWS::EC2::retryCount",
						"AWS::EC2::AuthorizeSecurityGroupIngress" }));
		final int retrycnt = retry == null ? 1 : Integer.parseInt(retry);
		// ec2.setEndpoint(endpoint);
		// final AuthorizeSecurityGroupIngressRequest req = new
		// AuthorizeSecurityGroupIngressRequest();
		// req.setCidrIp(cidrIp);
		// req.setFromPort(fromPort);
		// req.setToPort(toPort);
		// req.setGroupName(groupName);
		// req.setIpProtocol(ipProtocol);
		// req.setSourceSecurityGroupName(sourceSecurityGroupName);
		// req.setSourceSecurityGroupOwnerId(sourceSecurityGroupOwnerId);
		call.setAvailabilityZone(avz);
		final CloudProvider cloudProvider = call.getCloudProvider();
		final NetworkServices network = cloudProvider.getNetworkServices();
		final FirewallSupport firewall = network.getFirewallSupport();
		final int fromPort = Integer.parseInt(""
				+ call.getRequiredProperty(Constants.FROMPORT));
		final int toPort = Integer.parseInt(""
				+ call.getRequiredProperty(Constants.TOPORT));

		for (int i = 0; i < retrycnt; i++) {
			try {
				// ec2.authorizeSecurityGroupIngress(req);
				if (cidrIp != null) {
					firewall.authorize(groupName, Direction.INGRESS, cidrIp,
							Protocol.TCP, fromPort, toPort);
				} else {
					firewall.authorize(groupName, Direction.INGRESS, sourceSecurityGroupName,
							Protocol.TCP, fromPort, toPort);
				}
				break;
			} catch (final Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		logger.info("Ingress set ");

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(Constants.GROUPNAME, groupName);
		params.put(Constants.IPPROTOCOL, ipProtocol);
		if (cidrIp != null) {
			params.put(Constants.CIDRIP, cidrIp);
		}
		if (sourceSecurityGroupName != null) {
			params.put(Constants.SOURCESECURITYGROUPNAME,
					sourceSecurityGroupName);
			params.put(Constants.SOURCESECURITYGROUPOWNERID,
					sourceSecurityGroupOwnerId);
		}
		params.put(Constants.FROMPORT, fromPort);
		params.put(Constants.TOPORT, toPort);

		HibernateUtil.withNewSession(new Operation<Object>() {
			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {

				final ResourcesBean resBean = getResourceBean(s);
				resBean.setResourceData(JsonUtil.toJsonString(params));
				s.save(resBean);
				return null;
			}
		});
		return ret;
	}

	@Override
	public Resource delete0(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		String avz = (String) call.getProperty(Constants.AVAILABILITYZONE);
		if (avz == null) {
			avz = ac.getDefZone();
		}
		final ResourcesBean rbean = call.getResourcesBean();
		Map<String, Object> resourceMap = null;
		if (rbean != null) {
			final String hp = rbean.getResourceData();
			if (hp != null) {
				final JsonNode jn = JsonUtil.load(hp);
				resourceMap = JsonUtil.toMap(jn);
			}
		}

		String groupName = (String) call.getProperty(Constants.GROUPNAME);
		if (groupName == null && resourceMap != null) {
			final Object node = resourceMap.get(Constants.GROUPNAME);
			if (node != null) {
				if (node instanceof String) {
					groupName = (String) node;
				}
				if (node instanceof JsonNode) {
					groupName = ((JsonNode) node).getTextValue();
				}
			}
		}

		String ipProtocol = (String) call.getProperty(Constants.IPPROTOCOL);
		if (ipProtocol == null && resourceMap != null) {
			final Object node = resourceMap.get(Constants.IPPROTOCOL);
			if (node != null) {
				if (node instanceof String) {
					ipProtocol = (String) node;
				}
				if (node instanceof JsonNode) {
					ipProtocol = ((JsonNode) node).getTextValue();
				}
			}
		}
		if (ipProtocol == null) {
			ipProtocol = "TCP";
		}

		String cidrIp = (String) call.getProperty(Constants.CIDRIP);
		if (cidrIp == null && resourceMap != null) {
			final Object node = resourceMap.get(Constants.CIDRIP);
			if (node != null) {
				if (node instanceof String) {
					cidrIp = (String) node;
				}
				if (node instanceof JsonNode) {
					cidrIp = ((JsonNode) node).getTextValue();
				}
			}
		}

		String sourceSecurityGroupName = (String) call
				.getProperty(Constants.SOURCESECURITYGROUPNAME);
		if (sourceSecurityGroupName == null && resourceMap != null) {
			final Object node = resourceMap
					.get(Constants.SOURCESECURITYGROUPNAME);
			if (node != null) {
				if (node instanceof String) {
					sourceSecurityGroupName = (String) node;
				}
				if (node instanceof JsonNode) {
					sourceSecurityGroupName = ((JsonNode) node).getTextValue();
				}
			}
		}

		String sourceSecurityGroupOwnerId = (String) call
				.getProperty(Constants.SOURCESECURITYGROUPOWNERID);
		if (sourceSecurityGroupOwnerId == null && resourceMap != null) {
			final Object node = resourceMap
					.get(Constants.SOURCESECURITYGROUPOWNERID);
			if (node != null) {
				if (node instanceof String) {
					sourceSecurityGroupOwnerId = (String) node;
				}
				if (node instanceof JsonNode) {
					sourceSecurityGroupOwnerId = ((JsonNode) node)
							.getTextValue();
				}
			}
		}

		String fromPort = null;
		String toPort = null;
		if (fromPort == null && resourceMap != null) {
			final Object node = resourceMap.get(Constants.FROMPORT);
			if (node != null) {
				if (node instanceof String) {
					fromPort = (String) node;
				}
				if (node instanceof IntNode) {
					fromPort = ((IntNode) node).getValueAsText();
				}
			}
		}
		if (toPort == null && resourceMap != null) {
			final Object node = resourceMap.get(Constants.TOPORT);
			if (node != null) {
				if (node instanceof String) {
					toPort = (String) node;
				}
				if (node instanceof IntNode) {
					toPort = ((IntNode) node).getValueAsText();
				}
			}
		}
		// if (call.getProperty(Constants.FROMPORT) instanceof Integer
		// || call.getProperty(Constants.FROMPORT) instanceof String) {
		// fromPort = Integer.parseInt(call.getProperty(Constants.FROMPORT)
		// .toString());
		// }
		// if (call.getProperty(Constants.TOPORT) instanceof Integer
		// || call.getProperty(Constants.TOPORT) instanceof String) {
		// toPort = Integer.parseInt(call.getProperty(Constants.TOPORT)
		// .toString());
		// }
		//
		// if (resourceMap != null && fromPort == -1) {
		// final JsonNode node = (JsonNode) resourceMap
		// .get(Constants.FROMPORT);
		// if (node != null) {
		// final String s = node.getTextValue();
		// fromPort = Integer.parseInt(s);
		// }
		// }
		// if (resourceMap != null && toPort == -1) {
		// final JsonNode node = (JsonNode) resourceMap.get(Constants.TOPORT);
		// if (node != null) {
		// final String s = node.getTextValue();
		// toPort = Integer.parseInt(s);
		// }
		// }
		if (fromPort == null || toPort == null) {
			return null;
		}
		if (cidrIp == null && sourceSecurityGroupName == null
				&& resourceMap != null) {
			final JsonNode node = (JsonNode) resourceMap.get(Constants.CIDRIP);
			if (node != null) {
				cidrIp = node.getTextValue();
			}
			final JsonNode node1 = (JsonNode) resourceMap
					.get(Constants.SOURCESECURITYGROUPNAME);
			if (node1 != null) {
				sourceSecurityGroupName = node1.getTextValue();
			}
			final JsonNode node2 = (JsonNode) resourceMap
					.get(Constants.SOURCESECURITYGROUPOWNERID);
			if (node2 != null) {
				sourceSecurityGroupOwnerId = node2.getTextValue();
			}
		}

		final CloudProvider cloudProvider = call.getCloudProvider();
		final NetworkServices network = cloudProvider.getNetworkServices();
		final FirewallSupport firewall = network.getFirewallSupport();

		final String retry = (String) ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { "AWS::EC2::retryCount",
						"AWS::EC2::AuthorizeSecurityGroupIngress" }));
		final int retrycnt = retry == null ? 1 : Integer.parseInt(retry);
		for (int i = 0; i < retrycnt; i++) {
			try {
				if (cidrIp != null) {
					firewall.revoke(groupName, Direction.INGRESS, cidrIp,
							Protocol.TCP, Integer.parseInt(fromPort),
							Integer.parseInt(toPort));
				} else {
					firewall.revoke(groupName, sourceSecurityGroupName,
							Protocol.TCP, Integer.parseInt(fromPort),
							Integer.parseInt(toPort));
				}
				break;
			} catch (final Exception e) {
				e.printStackTrace();
				continue;
			}
		}

		logger.info("Ingress revoked " + groupName + " " + fromPort + " "
				+ cidrIp);
		return null;
	}

	@Override
	protected boolean isResource() {
		return true;
	}
}
