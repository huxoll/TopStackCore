package com.msi.tough.engine.aws.ec2;

import java.util.Arrays;

import org.slf4j.Logger;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AllocateAddressRequest;
import com.amazonaws.services.ec2.model.AllocateAddressResult;
import com.amazonaws.services.ec2.model.ReleaseAddressRequest;
import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.ec2.AllocateAddressType;
import com.msi.tough.core.Appctx;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.utils.ConfigurationUtil;

public class AllocateAddress extends BaseProvider {
	private static Logger logger = Appctx.getLogger(AllocateAddress.class
			.getName());
	public static String TYPE = "AWS::EC2::AllocateAddress";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final AllocateAddressType ret = new AllocateAddressType();
		final AccountType ac = call.getAc();
		final BasicAWSCredentials cred = new BasicAWSCredentials(
				ac.getAccessKey(), ac.getSecretKey());
		ret.setAvailabilityZone((String) call
				.getRequiredProperty("AvailabilityZone"));
		final AmazonEC2Client ec2 = new AmazonEC2Client(cred);
		final String endpoint = (String) ConfigurationUtil
				.getConfiguration(Arrays.asList(new String[] { "EC2_URL",
						ret.getAvailabilityZone() }));
		final String retry = (String) ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { "AWS::EC2::retryCount",
						"AWS::EC2::AllocateAddress" }));

		logger.debug("Call allocate IP " + endpoint);

		final int retrycnt = retry == null ? 1 : Integer.parseInt(retry);
		ec2.setEndpoint(endpoint);
		final AllocateAddressRequest req = new AllocateAddressRequest();
		AllocateAddressResult res = null;
		for (int i = 0; i < retrycnt; i++) {
			try {
				res = ec2.allocateAddress(req);
				final String addr = res.getPublicIp();
				if (addr.endsWith(".0")) {
					continue;
				}
				break;
			} catch (final Exception e) {
				e.printStackTrace();
				res = null;
				continue;
			}
		}
		ret.setPublicIp(res.getPublicIp());
		logger.info("IP allocated " + res.getPublicIp());
		return ret;
	}

	@Override
	public Resource delete(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		final BasicAWSCredentials cred = new BasicAWSCredentials(
				ac.getAccessKey(), ac.getSecretKey());
		final String id = call.getPhysicalId();

		final AmazonEC2Client ec2 = new AmazonEC2Client(cred);
		ec2.setEndpoint((String) ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { "EC2_URL", call.getAvailabilityZone() })));
		final ReleaseAddressRequest req = new ReleaseAddressRequest();
		final String ip = (String) call.getRequiredProperty("PublicIp");
		final String retry = (String) ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { "AWS::EC2::retryCount",
						"AWS::EC2::AllocateAddress" }));
		req.setPublicIp(ip);
		logger.debug("Release IP " + ip);
		final int retrycnt = retry == null ? 1 : Integer.parseInt(retry);
		for (int i = 0; i < retrycnt; i++) {
			try {
				ec2.releaseAddress(req);
				break;
			} catch (final Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		logger.info("IP released " + req.getPublicIp());
		return null;
	}

	@Override
	protected boolean isResource() {
		return false;
	}
}
