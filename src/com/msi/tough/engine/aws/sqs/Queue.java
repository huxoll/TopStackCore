package com.msi.tough.engine.aws.sqs;

import java.util.Arrays;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.sqs.QueueType;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.core.MapUtil;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.utils.ConfigurationUtil;

public class Queue extends BaseProvider {
	final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@SuppressWarnings("unchecked")
	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final CreateQueueRequest req = new CreateQueueRequest();
		req.setQueueName(call.getName());
		req.setAttributes((Map<String, String>) call.getProperty("Attributes"));
		final BasicAWSCredentials awsCredentials = new BasicAWSCredentials(call
				.getAc().getAccessKey(), call.getAc().getSecretKey());
		final String endpoint = (String) ConfigurationUtil
				.getConfiguration(Arrays.asList(new String[] {
						"SQS_URL",
						call.getAvailabilityZone() == null ? call.getAc()
								.getDefZone() : call.getAvailabilityZone() }));
		final AmazonSQSClient client = new AmazonSQSClient(awsCredentials);
		client.setEndpoint(endpoint);
		final CreateQueueResult resp = client.createQueue(req);

		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {

				final ResourcesBean rb = getResourceBean(s);
				final Map<String, Object> map = MapUtil.create("Url",
						resp.getQueueUrl());
				final String js = JsonUtil.toJsonStringIgnoreNullValues(map);
				rb.setResourceData(js);
				s.save(rb);
				return null;
			}
		});

		final QueueType ret = new QueueType();
		ret.setName(call.getName());
		ret.setPhysicalId(resp.getQueueUrl());
		ret.setQueueUrl(resp.getQueueUrl());
		return ret;
	}

	@Override
	public Resource delete0(final CallStruct call) throws Exception {
		final ResourcesBean rb = call.getResourcesBean();
		Map<String, Object> map = null;
		if (rb.getResourceData() != null) {
			final JsonNode n = JsonUtil.load(rb.getResourceData());
			map = JsonUtil.toMap(n);
		}
		final DeleteQueueRequest req = new DeleteQueueRequest();
		if (map != null && map.containsKey("Url")) {
			req.setQueueUrl((String) map.get("Url"));
			final BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
					call.getAc().getAccessKey(), call.getAc().getSecretKey());
			final String endpoint = (String) ConfigurationUtil
					.getConfiguration(Arrays.asList(new String[] {
							"SQS_URL",
							call.getAvailabilityZone() == null ? call.getAc()
									.getDefZone() : call.getAvailabilityZone() }));
			final AmazonSQSClient client = new AmazonSQSClient(awsCredentials);
			client.setEndpoint(endpoint);
			client.deleteQueue(req);
		}
		return null;
	}

}
