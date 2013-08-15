package com.msi.tough.engine.aws.ec2;

import java.util.Arrays;
import java.util.LinkedList;

import org.slf4j.Logger;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AssociateAddressRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DisassociateAddressRequest;
import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.ec2.AllocateAddressType;
import com.msi.tough.core.Appctx;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.utils.ConfigurationUtil;
import com.msi.tough.utils.Constants;

public class AssociateAddress extends BaseProvider {
	private static Logger logger = Appctx.getLogger(AssociateAddress.class
			.getName());
	public static String TYPE = "AWS::EC2::AssociateAddress";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final AllocateAddressType ret = new AllocateAddressType();
		final AccountType ac = call.getAc();
		final BasicAWSCredentials cred = new BasicAWSCredentials(
				ac.getAccessKey(), ac.getSecretKey());
		ret.setAvailabilityZone((String) call
				.getRequiredProperty(Constants.AVAILABILITYZONE));
		ret.setInstanceId((String) call
				.getRequiredProperty(Constants.INSTANCEID));
		ret.setPublicIp((String) call.getRequiredProperty(Constants.PUBLICIP));

		final AmazonEC2Client ec2 = new AmazonEC2Client(cred);
		final String endpoint = (String) ConfigurationUtil
				.getConfiguration(Arrays.asList(new String[] { "EC2_URL",
						ret.getAvailabilityZone() }));
		ec2.setEndpoint(endpoint);

		logger.debug("InstanceId "
				+ call.getRequiredProperty(Constants.INSTANCEID) + " address "
				+ call.getRequiredProperty(Constants.PUBLICIP));

		final DescribeInstancesRequest diReq = new DescribeInstancesRequest();
		final LinkedList<String> ids = new LinkedList<String>();
		ids.add((String) call.getRequiredProperty(Constants.INSTANCEID));
		diReq.setInstanceIds(ids);
		int available = 0;
		int cnt = 20;

		logger.debug("Waiting for the instance to be running before associating the address...");
		while (available == 0 && cnt > 0) {
			cnt--;
			final DescribeInstancesResult res = ec2.describeInstances(diReq);
			final String state = res.getReservations().get(0).getInstances()
					.get(0).getState().getName();

			ret.setStatus(state);
			if (state.equals("running")) {
				logger.debug("Current state of the ec2 instance is " + state);
				available = 1;
				break;
			}
			if (state.equals("error")) {
				logger.error("instance in error state "
						+ call.getRequiredProperty(Constants.INSTANCEID));
				available = -1;
				break;
			}
			Thread.sleep(3000);
		}

		ret.setAvailabilityZone((String) call
				.getRequiredProperty(Constants.AVAILABILITYZONE));
		ret.setInstanceId((String) call
				.getRequiredProperty(Constants.INSTANCEID));
		ret.setPublicIp((String) call.getRequiredProperty(Constants.PUBLICIP));

		if (available == 0) {
			logger.error("instance timedout"
					+ call.getRequiredProperty(Constants.INSTANCEID));
		}

		if (available == 1) {
			final AssociateAddressRequest req = new AssociateAddressRequest();
			req.setInstanceId(ret.getInstanceId());
			req.setPublicIp(ret.getPublicIp());
			ec2.associateAddress(req);
			logger.info("Address associated " + ret.getInstanceId() + " "
					+ ret.getPublicIp());
		}
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
		final DisassociateAddressRequest req = new DisassociateAddressRequest();
		req.setPublicIp((String) call.getRequiredProperty("PublicIp"));
		logger.debug("disassociate " + call.getRequiredProperty("PublicIp"));
		ec2.disassociateAddress(req);
		logger.info("Address disassociated " + req.getPublicIp());
		return null;
	}

	@Override
	protected boolean isResource() {
		return false;
	}
}
