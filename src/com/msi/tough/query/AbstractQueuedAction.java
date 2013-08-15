/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.query;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.protobuf.Message;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.utils.MetricsUtil;
import com.msi.tough.workflow.core.Workflow;
import com.yammer.metrics.core.Meter;

/**
 * Action subclass to handle queued actions performed by a back end object.
 *
 * @author jgardner
 * @param <T>
 *            the type of request handled by this action
 * @param <V>
 *            the type of result returned by this action
 *
 */
public abstract class AbstractQueuedAction<T extends Message, V extends Message>
		implements QueuedAction {

	private static final Logger logger = Appctx
			.getLogger(AbstractQueuedAction.class.getName());

	public static Map<String, Meter> initMeter(String group, String name) {
		return MetricsUtil.initMeter(group, name);
	}

	private String action;
	private boolean useContextSession = true;

	@Resource
	private Workflow workflow;

	@Autowired
	private ActionHelper actionHelper;

	public abstract ServiceResponse buildResponse(ServiceResponse resp,
			V message);

	@Override
	public String getAction() {
		return action;
	}

	/**
	 * Getter to retrieve a required property from a message (via reflection
	 * since protobuf has no enforced interface/base class).
	 *
	 * @param message
	 * @param propertyName
	 * @param value
	 */
	protected <U extends Message> Object getRequiredMessageProperty(U message,
			String propertyName) {
		try {
			return PropertyUtils.getProperty(message, propertyName);
		} catch (Exception e) {
			// e may be: IllegalAccessException, InvocationTargetException,
			// NoSuchMethodException
			throw new IllegalArgumentException("Message must have "
					+ "required property " + propertyName);
		}
	}

	/**
	 * Obtain a session; either a new one (default), or one from context. The
	 * context session will be shared with other context aware beans, rather
	 * than being only accessible when passed everywhere explicitly.
	 *
	 * @return Session ready for use.
	 */
	public Session getSession() {
		if (useContextSession) {
			return HibernateUtil.getSession();
		}
		final Session s = HibernateUtil.newSession();
		return s;
	}

	@Override
	public Workflow getWorkflow() {
		return workflow;
	}

	public abstract T handleRequest(final ServiceRequest req,
			ServiceRequestContext context) throws ErrorResponse;

	@Override
	public boolean isUseContextSession() {
		return useContextSession;
	}

	protected void mark(Object ret, Exception e) {
		// do nothing
	}

	public void markStandard(Map<String, Meter> meters, Exception e) {
		MetricsUtil.markStandard(meters, e);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.msi.tough.query.QueuedAction#process(com.google.protobuf.Message,
	 * com.google.protobuf.Message)
	 */
	@Override
	public void process(ServiceRequest req, ServiceResponse resp)
			throws ErrorResponse {
		ServiceRequestContext context = new ServiceRequestContext();
		context.setRequestId(req.getRequestId());
		validate(req, context);
		T message = this.handleRequest(req, context);
		logger.debug("Built message: " + message.getClass().getName());
		message = ProtobufUtil.setRequiredField(message, "typeId", true);
		message = ProtobufUtil.setRequiredField(message, "requestId",
				context.getRequestId());
		message = ProtobufUtil.setRequiredField(message, "callerAccessKey",
				context.getAwsAccessKeyId());
		workflow.doWork(message, context);
	}

	public ServiceResponse respond(V message) throws ErrorResponse {
		ServiceResponse resp = null;
		String requestId = (String) getRequiredMessageProperty(message,
				"requestId");
		resp = new ServiceResponse(null, requestId);
		this.buildResponse(resp, message);
		return resp;
	}

	@Override
	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public void setUseContextSession(boolean useContextSession) {
		this.useContextSession = useContextSession;
	}

	@Override
	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	/**
	 * Validate that the request has minimal required information to be sane.
	 *
	 * @param req
	 */
	protected void validate(final ServiceRequest req,
			ServiceRequestContext context) {
		actionHelper.validate(req, context, this);
	}
}
