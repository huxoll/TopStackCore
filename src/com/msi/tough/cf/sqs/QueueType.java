package com.msi.tough.cf.sqs;

import com.msi.tough.cf.CFType;

public class QueueType extends CFType {

	private String queueUrl;

	@Override
	public Object getAtt(final String key) {
		if (key.equals("QueueName")) {
			return getName();
		} else {
			return super.getAtt(key);
		}
	}

	public String getQueueUrl() {
		return queueUrl;
	}

	@Override
	public Object ref() {
		return getQueueUrl();
	}

	public void setQueueUrl(final String url) {
		queueUrl = url;
	}
}
