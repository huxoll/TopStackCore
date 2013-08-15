package com.msi.tough.engine.aws.ec2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.network.Firewall;
import org.dasein.cloud.network.FirewallRule;
import org.dasein.cloud.network.FirewallSupport;
import org.dasein.cloud.network.NetworkServices;
import org.slf4j.Logger;

import com.msi.tough.cf.CFType;
import com.msi.tough.cf.ec2.AuthorizeSecurityGroupIngressType;
import com.msi.tough.cf.ec2.SecurityGroupType;
import com.msi.tough.core.Appctx;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;

public class DescribeSecurityGroup extends BaseProvider {
	private static Logger logger = Appctx.getLogger(DescribeSecurityGroup.class
			.getName());
	public static String TYPE = "AWS::EC2::DescribeSecurityGroup";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final SecurityGroupType ret = new SecurityGroupType();
		final String name = call.getName();
		// final AccountType ac = call.getAc();
		// ret.setName(call.getName());
		// String avZone = (String)
		// call.getProperty(Constants.AVAILABILITYZONE);
		// if (avZone == null) {
		// avZone = ac.getDefZone();
		// }
		// final BasicAWSCredentials cred = new BasicAWSCredentials(
		// ac.getAccessKey(), ac.getSecretKey());
		// final AmazonEC2Client ec2 = new AmazonEC2Client(cred);
		// final String endpoint = (String) ConfigurationUtil
		// .getConfiguration(Arrays.asList(new String[] { "EC2_URL",
		// avZone }));
		// ec2.setEndpoint(endpoint);
		//
		// final DescribeSecurityGroupsRequest req = new
		// DescribeSecurityGroupsRequest();
		// req.setGroupNames(Arrays.asList(name));
		// final DescribeSecurityGroupsResult res = ec2
		// .describeSecurityGroups(req);
		// if (res.getSecurityGroups().size() > 0) {
		// final SecurityGroup g = res.getSecurityGroups().get(0);
		final CloudProvider cloudProvider = call.getCloudProvider();
		final NetworkServices network = cloudProvider.getNetworkServices();
		final FirewallSupport firewall = network.getFirewallSupport();
		final Firewall g = firewall.getFirewall(name);
		ret.setGroupName(name);
		ret.setGroupDescription(g.getDescription());
		final List<AuthorizeSecurityGroupIngressType> l = new ArrayList<AuthorizeSecurityGroupIngressType>();
		final Collection<FirewallRule> rules = firewall.getRules(name);
		for (final FirewallRule p : rules) {
			final AuthorizeSecurityGroupIngressType ing = new AuthorizeSecurityGroupIngressType();
			final String ruleType = p.getSourceEndpoint().getRuleTargetType().toString();
			ing.setFromPort("" + p.getStartPort());
			ing.setIpProtocol(p.getProtocol().name());
			ing.setToPort("" + p.getEndPort());
			if(ruleType.equals("GLOBAL")){
				final String groupId = p.getSourceEndpoint().getProviderFirewallId();
				ing.setSourceSecurityGroupId(groupId);
				ing.setSourceSecurityGroupName(firewall.getFirewall(groupId).getName());
			}
			else if(ruleType.equals("CIDR")){
				ing.setCidrIp(p.getSourceEndpoint().getCidr());
			}
			l.add(ing);
		}
		// for (final UserIdGroupPair ec2grp : p.getUserIdGroupPairs()) {
		// final AuthorizeSecurityGroupIngressType ing = new
		// AuthorizeSecurityGroupIngressType();
		// ing.setFromPort("" + p.getFromPort());
		// ing.setIpProtocol(p.getIpProtocol());
		// ing.setToPort("" + p.getToPort());
		// ing.setSourceSecurityGroupName(ec2grp.getGroupName());
		// ing.setSourceSecurityGroupOwnerId(ec2grp.getUserId());
		// l.add(ing);
		// }
		// }
		logger.debug("Security Group " + name);
		ret.setSecurityGroupIngress(l);
		return ret;
	}

	@Override
	protected boolean isResource() {
		return false;
	}
}
