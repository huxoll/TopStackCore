package com.msi.tough.utils;

import java.util.HashMap;
import java.util.Map;

import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.network.Firewall;
import org.dasein.cloud.network.FirewallSupport;
import org.dasein.cloud.network.NetworkServices;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.ec2.SecurityGroupType;
import com.msi.tough.dasein.DaseinHelper;
import com.msi.tough.engine.aws.ec2.DescribeSecurityGroup;
import com.msi.tough.engine.aws.ec2.SecurityGroup;
import com.msi.tough.engine.aws.ec2.SecurityGroupIngress;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.core.TemplateContext;

public class SecurityGroupUtils {

	public static void authorizeSecurityGroupIngress(final AccountType ac,
			final String secGrpName, final int port, final String stackId)
			throws Exception {

		final CallStruct c0 = new CallStruct();
		c0.setAc(ac);
		c0.setCtx(new TemplateContext(null));
		c0.setStackId(stackId);
		c0.setParentId(secGrpName);
		c0.setName(secGrpName + "_" + port);
		final Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(Constants.AVAILABILITYZONE, ac.getDefZone());
		properties.put(Constants.GROUPNAME, secGrpName);
		properties.put(Constants.FROMPORT, port);
		properties.put(Constants.TOPORT, port);
		properties.put(Constants.CIDRIP, "0.0.0.0/0");
		c0.setProperties(properties);
		c0.setType(SecurityGroupIngress.TYPE);
		final SecurityGroupIngress provider = new SecurityGroupIngress();
		provider.create(c0);
	}

	public static void authorizeSecurityGroupIngress(final AccountType ac,
			final String secGrpName, final int port, final String stackId,
			final String cidrip) throws Exception {

		final CallStruct c0 = new CallStruct();
		c0.setAc(ac);
		c0.setCtx(new TemplateContext(null));
		c0.setStackId(stackId);
		c0.setParentId(secGrpName);
		c0.setName(secGrpName);
		final Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(Constants.AVAILABILITYZONE, ac.getDefZone());
		properties.put(Constants.GROUPNAME, secGrpName);
		properties.put(Constants.FROMPORT, port);
		properties.put(Constants.TOPORT, port);
		properties.put(Constants.CIDRIP, cidrip);
		c0.setProperties(properties);
		c0.setType(SecurityGroupIngress.TYPE);
		final SecurityGroupIngress provider = new SecurityGroupIngress();
		provider.create(c0);
	}

	public static void authorizeSecurityGroupIngress(final AccountType ac,
			final String secGrpName, final String ec2SecGrpName,
			final String stackId, final String ec2SecGrpOwnerId,
			final int port, final String parentId) throws Exception {

		final CallStruct c0 = new CallStruct();
		c0.setAc(ac);
		c0.setCtx(new TemplateContext(null));
		c0.setStackId(stackId);
		c0.setParentId(parentId);
		c0.setName(parentId + "-" + ec2SecGrpName);
		final Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(Constants.AVAILABILITYZONE, ac.getDefZone());
		properties.put(Constants.GROUPNAME, secGrpName);
		properties.put(Constants.SOURCESECURITYGROUPNAME, ec2SecGrpName);
		properties.put(Constants.SOURCESECURITYGROUPOWNERID, ec2SecGrpOwnerId);
		properties.put(Constants.FROMPORT, port);
		properties.put(Constants.TOPORT, port);
		c0.setProperties(properties);
		c0.setType(SecurityGroupIngress.TYPE);
		final SecurityGroupIngress provider = new SecurityGroupIngress();
		provider.create(c0);
	}

	public static String createSecurityGroup(final AccountType ac,
			final TemplateContext ctx, final String parentId,
			final String stackId, final String availabilityZone,
			final String name, final String desc) throws Exception {
		return createSecurityGroup(ac, ctx, parentId, stackId,
				availabilityZone, name, desc, true);
	}

	public static String createSecurityGroup(final AccountType ac,
			final TemplateContext ctx, final String parentId,
			final String stackId, final String availabilityZone,
			final String name, final String desc, final boolean setSsh)
			throws Exception {

		final CallStruct c = new CallStruct();
		String ret = null;
		{
			c.setAc(ac);
			c.setCtx(ctx == null ? new TemplateContext(null) : ctx);
			c.setParentId(parentId);
			c.setStackId(stackId);
			c.setAvailabilityZone(availabilityZone);
			c.setName(name);
			final Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(Constants.AVAILABILITYZONE, ac.getDefZone());
			properties.put("GroupDescription", desc);
			c.setProperties(properties);
			c.setType(SecurityGroup.TYPE);
			final SecurityGroup provider = new SecurityGroup();
			final SecurityGroupType res = (SecurityGroupType) provider
					.create(c);
			ret = res.getPhysicalId();
		}

		if (setSsh) {
			final CallStruct c0 = c.newCall(ret);
			c0.setAc(ac);
			c0.setCtx(new TemplateContext(null));
			c0.setName(ret + "_22");
			final Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(Constants.AVAILABILITYZONE, ac.getDefZone());
			properties.put(Constants.GROUPNAME, ret);
			properties.put(Constants.FROMPORT, 22);
			properties.put(Constants.TOPORT, 22);
			properties.put(Constants.CIDRIP, "0.0.0.0/0");
			c0.setProperties(properties);
			c0.setType(SecurityGroupIngress.TYPE);
			final SecurityGroupIngress provider = new SecurityGroupIngress();
			provider.create(c0);
		}
		return ret;
	}

	public static void deleteSecurityGroup(final AccountType ac,
			final String stackId, final String availabilityZone,
			final String name) throws Exception {

		final CallStruct c = new CallStruct();
		c.setAc(ac);
		c.setCtx(new TemplateContext(null));
		c.setStackId(stackId);
		c.setAvailabilityZone(availabilityZone);
		c.setName(name);
		final Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(Constants.AVAILABILITYZONE, availabilityZone);
		c.setProperties(properties);
		c.setType(SecurityGroup.TYPE);
		final SecurityGroup provider = new SecurityGroup();
		provider.delete(c);
	}

	public static SecurityGroupType describeSecurityGroup(final AccountType ac,
			final String availabilityZone, final String name) throws Exception {

		final CallStruct c = new CallStruct();
		c.setAc(ac);
		c.setAvailabilityZone(availabilityZone);
		c.setName(name);
		final Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(Constants.AVAILABILITYZONE, ac.getDefZone());
		c.setProperties(properties);
		c.setType(SecurityGroup.TYPE);
		final DescribeSecurityGroup provider = new DescribeSecurityGroup();
		return (SecurityGroupType) provider.create(c);
	}

	public static String getProviderId(final String name,
			final String availabilityZone, final AccountType ac)
			throws Exception {
		try {
			Integer.parseInt(name);
		} catch (final Exception e) {
			final CloudProvider provider = DaseinHelper.getProvider(
					availabilityZone, ac);
			final NetworkServices network = provider.getNetworkServices();
			final FirewallSupport firewall = network.getFirewallSupport();
			for (final Firewall f : firewall.list()) {
				if (f.getName().equals(name)) {
					return f.getProviderFirewallId();
				}
			}
		}
		return name;
	}

	public static void revokeSecurityGroupIngress(final AccountType ac,
			final String secGrpName, final int port, final String stackId,
			final String cidrip) throws Exception {

		final CallStruct c0 = new CallStruct();
		c0.setAc(ac);
		c0.setCtx(new TemplateContext(null));
		c0.setStackId(stackId);
		c0.setName(secGrpName + "_" + port);
		final Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(Constants.AVAILABILITYZONE, ac.getDefZone());
		properties.put(Constants.GROUPNAME, secGrpName);
		properties.put(Constants.FROMPORT, "" + port);
		properties.put(Constants.TOPORT, "" + port);
		properties.put(Constants.CIDRIP, cidrip);
		c0.setProperties(properties);
		c0.setType(SecurityGroupIngress.TYPE);
		final SecurityGroupIngress provider = new SecurityGroupIngress();
		provider.delete(c0);
	}

	public static void revokeSecurityGroupIngress(final AccountType ac,
			final String secGrpName, final String ec2SecGrpName,
			final String stackId, final String ec2SecGrpOwnerId, final int port)
			throws Exception {

		final CallStruct c0 = new CallStruct();
		c0.setAc(ac);
		c0.setCtx(new TemplateContext(null));
		c0.setStackId(stackId);
		c0.setName(secGrpName + "_");
		final Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(Constants.AVAILABILITYZONE, ac.getDefZone());
		properties.put(Constants.GROUPNAME, secGrpName);
		properties.put(Constants.SOURCESECURITYGROUPNAME, ec2SecGrpName);
		properties.put(Constants.SOURCESECURITYGROUPOWNERID, ec2SecGrpOwnerId);
		properties.put(Constants.FROMPORT, port);
		properties.put(Constants.TOPORT, port);
		c0.setProperties(properties);
		c0.setType(SecurityGroupIngress.TYPE);
		final SecurityGroupIngress provider = new SecurityGroupIngress();
		provider.delete(c0);
	}
}
