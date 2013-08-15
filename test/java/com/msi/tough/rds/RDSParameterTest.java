package com.msi.tough.rds;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.hibernate.Session;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.Assert;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.DescribeDBParametersRequest;
import com.amazonaws.services.rds.model.DescribeDBParametersResult;
import com.amazonaws.services.rds.model.Parameter;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbparameterGroup;
import com.msi.tough.model.rds.RdsParameter;

public class RDSParameterTest {

	private static final String PROPS_FILE = "testresources/test.properties";
	private static Properties props;

	private static String getAccessKey() {
		if (props == null) {
			System.out.println("props = null!");
		}

		return props.getProperty("accessKey");
	}

	private static String getSecretKey() {
		return props.getProperty("secretKey");
	}

	@Ignore
	@Test
	public void ReadAWSParameterGroup() {

		final boolean writeResultsToDatabase = true;

		props = new Properties();
		try {
			final FileInputStream fis = new FileInputStream(PROPS_FILE);
			props.load(fis);
			fis.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		System.out.println("props.size() = " + props.size());

		final String parameterGroupFamilyName = "mysql5.1";
		final String parameterGroupName = "default.mysql5.1";
		final String parameterGroupDescription = "Default parameter group for mysql5.1";

		final AWSCredentials creds = new BasicAWSCredentials(getAccessKey(),
				getSecretKey());
		final AmazonRDSClient amazonRDSClient = new AmazonRDSClient(creds);

		/*
		 * DescribeDBParameterGroupsRequest describeDBParameterGroupsRequest =
		 * new DescribeDBParameterGroupsRequest();
		 * describeDBParameterGroupsRequest
		 * .setDBParameterGroupName("default.mysql5.1");
		 *
		 * DescribeDBParameterGroupsResult describeDBParameterGroupsResponse =
		 * amazonRDSClient
		 * .describeDBParameterGroups(describeDBParameterGroupsRequest); for(
		 * DBParameterGroup group :
		 * describeDBParameterGroupsResponse.getDBParameterGroups()){
		 * System.out.print( group.getDBParameterGroupName() ) ; }
		 */

		final Session session = HibernateUtil.newSession();
		if (writeResultsToDatabase) {
			session.beginTransaction();
		}
		final AccountBean account = (AccountBean) session.get(
				AccountBean.class, 1L);

		final RdsDbparameterGroup rdsDbParameterGroup = new RdsDbparameterGroup(
				account, parameterGroupName, parameterGroupFamilyName,
				parameterGroupDescription);
		if (writeResultsToDatabase) {
			session.save(rdsDbParameterGroup);
		}

		String marker = "";
		do {
			final DescribeDBParametersRequest describeDBParametersRequest = new DescribeDBParametersRequest();
			describeDBParametersRequest
					.setDBParameterGroupName(parameterGroupName);
			describeDBParametersRequest.setMarker(marker);

			final DescribeDBParametersResult describeDBParametersResult = amazonRDSClient
					.describeDBParameters(describeDBParametersRequest);

			for (final Parameter parameter : describeDBParametersResult
					.getParameters()) {
				System.out.println(String.format(
						"Name:%s Value:%s Type:%s Apply:%s Allowed:%s",
						parameter.getParameterName(),
						parameter.getParameterValue(), parameter.getDataType(),
						parameter.getApplyMethod(),
						parameter.getAllowedValues()));

				if (writeResultsToDatabase) {
					saveParameter(session, rdsDbParameterGroup, parameter);
				}
			}
			marker = describeDBParametersResult.getMarker();

		} while (marker != null && marker.length() != 0);

		if (writeResultsToDatabase) {
			session.getTransaction().commit();
		}
		session.close();

		Assert.isTrue(true);
	}

	private void saveParameter(final Session session,
			final RdsDbparameterGroup rdsDbParameterGroup,
			final Parameter parameter) {

		final RdsParameter rdsParameter = new RdsParameter(rdsDbParameterGroup);
		rdsParameter.setAllowedValues(parameter.getAllowedValues());
		// rdsParameter.setApplyType(parameter.getApplyType().equalsIgnoreCase("dynamic"));
		rdsParameter.setDataType(parameter.getDataType());
		rdsParameter.setDescription(parameter.getDescription());
		rdsParameter.setIsModifiable(parameter.getIsModifiable());
		// Convert to relationship
		rdsParameter.setMinimumEngineVersion(parameter
				.getMinimumEngineVersion());
		rdsParameter.setParameterName(parameter.getParameterName());
		rdsParameter.setParameterValue(parameter.getParameterValue());
		rdsParameter.setSource(parameter.getSource());

		session.save(rdsParameter);
	}

}
