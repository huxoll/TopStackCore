package com.msi.tough.engine.aws.ec2;

import java.util.Arrays;

import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.identity.IdentityServices;
import org.dasein.cloud.identity.SSHKeypair;
import org.dasein.cloud.identity.ShellKeySupport;
import org.slf4j.Logger;

import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.ec2.KeyPairType;
import com.msi.tough.core.Appctx;
import com.msi.tough.dasein.DaseinHelper;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.utils.ConfigurationUtil;
import com.msi.tough.utils.Constants;

public class KeyPair extends BaseProvider {
	private static Logger logger = Appctx.getLogger(KeyPair.class.getName());
	public static String TYPE = "AWS::EC2::KeyPair";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		String avZone = (String) call.getProperty(Constants.AVAILABILITYZONE);
		if (avZone == null) {
			avZone = ac.getDefZone();
		}
		final String name = call.getName();

		final String retry = (String) ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { "AWS::EC2::retryCount", TYPE }));
		final int retrycnt = retry == null ? 1 : Integer.parseInt(retry);

		final CloudProvider cloudProvider = DaseinHelper.getProvider(
		        avZone, ac.getTenant(),
		        ac.getAccessKey(), ac.getSecretKey());
		final IdentityServices identity = cloudProvider.getIdentityServices();
		final ShellKeySupport shellKeySupport = identity.getShellKeySupport();

		CreateKeyPairResult res = null;
		for (int i = 0; i < retrycnt; i++) {
			try {
			    SSHKeypair newKeyPair = shellKeySupport.createKeypair(name);
			    res = new CreateKeyPairResult();
			    com.amazonaws.services.ec2.model.KeyPair keyPair =
			            new com.amazonaws.services.ec2.model.KeyPair();
			    keyPair.setKeyName(name);
			    keyPair.setKeyFingerprint(newKeyPair.getFingerprint());
			    keyPair.setKeyMaterial(new String(newKeyPair.getPrivateKey()));
			    res.setKeyPair(keyPair);
				break;
			} catch (final Exception e) {
				Thread.sleep(10 * 1000);
				logger.warn("Failed to create keypair: " + e.getMessage());
				if (i == retrycnt - 1) {
				    logger.error("Failed to create keypair after " +
				            retrycnt + " attempt(s), failing.");
				    throw new RuntimeException(e.getMessage());
				}
				continue;
			}
		}
		final String key = res.getKeyPair().getKeyMaterial();
		final KeyPairType ret = new KeyPairType();
		ret.setName(name);
		ret.setMaterial(key);
		logger.debug("KeyPair Created " + name);
		return ret;
	}

	@Override
	public Resource delete(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		String avZone = (String) call.getProperty(Constants.AVAILABILITYZONE);
		if (avZone == null) {
			avZone = ac.getDefZone();
		}
		final String name = call.getName();

		final CloudProvider cloudProvider = DaseinHelper.getProvider(
		        avZone, ac.getTenant(),
		        ac.getAccessKey(), ac.getSecretKey());
		final IdentityServices identity = cloudProvider.getIdentityServices();
		final ShellKeySupport shellKeySupport = identity.getShellKeySupport();

		final String retry = (String) ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { "AWS::EC2::retryCount", TYPE }));
		final int retrycnt = retry == null ? 1 : Integer.parseInt(retry);

		final DeleteKeyPairRequest req = new DeleteKeyPairRequest();
		req.setKeyName(name);
		for (int i = 0; i < retrycnt; i++) {
			try {
			    shellKeySupport.deleteKeypair(name);
				break;
			} catch (final Exception e) {
				Thread.sleep(10 * 1000);
				continue;
			}
		}
		logger.debug("KeyPair Deleted " + name);
		return null;
	}

	@Override
	protected boolean isResource() {
		return false;
	}
}
