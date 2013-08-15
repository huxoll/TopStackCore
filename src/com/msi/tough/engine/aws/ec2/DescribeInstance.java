package com.msi.tough.engine.aws.ec2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.codehaus.jackson.node.ArrayNode;
import org.slf4j.Logger;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.ec2.InstanceType;
import com.msi.tough.core.Appctx;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.utils.InstanceUtils;
import com.msi.tough.utils.ConfigurationUtil;

public class DescribeInstance extends BaseProvider {
	private final static Logger logger = Appctx
			.getLogger(DescribeInstance.class.getName());
	public static String TYPE = "AWS::EC2::DescribeInstance";

	@SuppressWarnings("unchecked")
	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		final BasicAWSCredentials cred = new BasicAWSCredentials(
				ac.getAccessKey(), ac.getSecretKey());

		final InstanceType ins = new InstanceType();
		ins.setAvailabilityZone((String) call.getProperty("AvailabilityZone"));
		final AmazonEC2Client ec2 = new AmazonEC2Client(cred);
		final String endpoint = Appctx.getBean("COMPUTE_URL");
		ec2.setEndpoint(endpoint);
		final DescribeInstancesRequest req = new DescribeInstancesRequest();
		final Object ids = call.getProperty("InstanceIds");
		if (ids != null) {
			final Collection<String> instanceIds = new ArrayList<String>();
			if (ids instanceof ArrayNode) {
				final ArrayNode an = (ArrayNode) ids;
				for (int i = 0; i < an.size(); i++) {
					instanceIds.add(an.get(i).getTextValue());
				}
			} else if (ids instanceof List) {
				final List<String> an = (List<String>) ids;
				for (final String i : an) {
					instanceIds.add(i);
				}
			} else {
				instanceIds.add(ids.toString());
			}
			req.setInstanceIds(instanceIds);
		}
		final DescribeInstancesResult res = ec2.describeInstances(req);
		for (final Reservation i : res.getReservations()) {
			for (final Instance rins : i.getInstances()) {
				InstanceUtils.toResource(ins, rins);
			}
		}
		logger.debug("Instance " + ins.getInstanceId() + " "
				+ ins.getPublicIp());
		return ins;
	}

	@Override
	protected boolean isResource() {
		return false;
	}
}
