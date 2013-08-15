package com.msi.tough.utils;

import java.util.Arrays;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.msi.tough.cf.AccountType;

public class AWSUtil {
	public static AmazonEC2Client getClient(final AccountType ac,
			final String avZone) {
		try {
			final BasicAWSCredentials cred = new BasicAWSCredentials(
					ac.getAccessKey(), ac.getSecretKey());
			final String endpoint = (String) ConfigurationUtil
					.getConfiguration(Arrays.asList(new String[] { "EC2_URL",
							avZone }));
			final AmazonEC2Client ec2 = new AmazonEC2Client(cred);
			ec2.setEndpoint(endpoint);
			return ec2;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
