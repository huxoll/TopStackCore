package com.msi.tough.utils;

import java.io.File;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.DigestUtils;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.ec2.KeyPairType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.FileUtils;
import com.msi.tough.core.StringHelper;
import com.msi.tough.engine.aws.ec2.KeyPair;
import com.msi.tough.engine.aws.ec2.SecurityGroup;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.core.TemplateContext;

public class KeyPairUtils {

	// TODO: record user salt
	private static final String SALT = "d68aba4c2916cc6ffed43168ffb44419";

	public static KeyPairType createKeyPair(final AccountType ac,
			final TemplateContext ctx, final String parentId,
			final String stackId, final String availabilityZone,
			final String name) throws Exception {

		final CallStruct c = new CallStruct();
		c.setAc(ac);
		c.setCtx(ctx);
		c.setParentId(parentId);
		c.setStackId(stackId);
		c.setAvailabilityZone(availabilityZone);
		c.setName(name);
		final Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(Constants.AVAILABILITYZONE, ac.getDefZone());
		c.setProperties(properties);
		c.setType(SecurityGroup.TYPE);
		final KeyPair provider = new KeyPair();
		final KeyPairType kp = (KeyPairType) provider.create(c);
		if (kp != null) {
			final String kdir = Appctx.getConfigurationBean("KEYS_DIR");
			final String kf = kdir + "/" + name + ".pem";
			StringHelper.writeTofile(kp.getMaterial(), kf);
			FileUtils.chmod(kf, "0600");
		}
		return kp;
	}

	public static KeyPairType deleteKeyPair(final AccountType ac,
			final TemplateContext ctx, final String parentId,
			final String stackId, final String availabilityZone,
			final String name) throws Exception {

		final CallStruct c = new CallStruct();
		c.setAc(ac);
		c.setCtx(ctx);
		c.setParentId(parentId);
		c.setStackId(stackId);
		c.setAvailabilityZone(availabilityZone);
		c.setName(name);
		final Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(Constants.AVAILABILITYZONE, ac.getDefZone());
		c.setProperties(properties);
		c.setType(SecurityGroup.TYPE);
		final KeyPair provider = new KeyPair();
		final KeyPairType kp = (KeyPairType) provider.delete(c);

		final String kdir = Appctx.getConfigurationBean("KEYS_DIR");
		final String kf = kdir + "/" + name + ".pem";
		final File fl = new File(kf);
		fl.delete();
		return kp;
	}

	public static String getKeyName(final String uniqueValue) {
		String installId = Appctx.getConfigurationBean("INSTALL_ID");
		try {
			if (installId == null || "".equals(installId)
					|| installId.startsWith("${")) {
				installId = java.net.InetAddress.getLocalHost().getHostName();
			}
		} catch (final UnknownHostException e) {
			installId = "Unknown";
		}
		final String munged = DigestUtils
				.md5DigestAsHex((SALT + installId + uniqueValue).getBytes());
		return "__key_" + munged + "_transcend-key";
	}
}
