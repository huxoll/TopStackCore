/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.workflow.core;

import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;

import com.google.protobuf.Message;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ProtobufUtil;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.MetricsUtil;
import com.yammer.metrics.core.Meter;

/**
 * Worker to process a single phase of work on an object.
 *
 * Compare to AbstractWorker, which assumes it generates the final response.
 *
 * @author jgardner
 * @param <T> the type of object handled by this worker
 */
public abstract class AbstractPhaseWorker<T extends Message> {
    private final static Logger logger = Appctx
            .getLogger(AbstractPhaseWorker.class.getName());

    @Resource
    private SessionFactory sessionFactory = null;

    /**
     * Generic template method for performing work.  Delegates implementation
     * to doWork0.
     * @param req
     * @throws Exception
     */
    protected void doWork(T req, Session session)
            throws Exception {
        String requestId = null;
        requestId = (String) ProtobufUtil.getRequiredField(req, "requestId");
        assert(requestId != null);
        try {
            doWork(req, session, requestId);
        }
        catch (ErrorResponse e) {
            mark(null, e);
            e.setRequestId(requestId);
            throw e;
        }
        catch (Exception e) {
            mark(null, e);
            logger.error("Failed to perform work.", e);
            ErrorResponse er = ErrorResponse.InternalFailure();
            er.setRequestId(requestId);
            throw er;
        }
    }

    /**
     * Generic template method for performing work.  Delegates implementation
     * to doWork0.
     * @param req
     * @throws Exception
     */
    protected T doWork(T req, Session session, String requestId)
            throws Exception {
        String accessKey = null;
        AccountBean account = null;
        accessKey = (String) ProtobufUtil.getRequiredField(req, "callerAccessKey");
        assert(accessKey != null);
        requestId = (String) ProtobufUtil.getRequiredField(req, "requestId");
        assert(requestId != null);
        try {
            account = AccountUtil.readAccount(session, accessKey);
        }
        catch (Exception e) {
            mark(null, e);
            logger.error("Failed to load account.", e);
            ErrorResponse er = ErrorResponse.InternalFailure();
            er.setRequestId(requestId);
            throw er;
        }
        ServiceRequestContext context = new ServiceRequestContext();
        context.setAccountBean(account);
        context.setAccountId(account.getId());
        context.setAwsAccessKeyId(accessKey);
        try
        {
            T result = doWork0(req, context);
            result = ProtobufUtil.setRequiredField(result, "requestId", requestId);
            result = ProtobufUtil.setRequiredField(result, "typeId", true);
            mark(result, null);
            return result;
        }
        catch (ErrorResponse e) {
            mark(null, e);
            e.setRequestId(requestId);
            throw e;
        }
        catch (Exception e) {
            mark(null, e);
            logger.error("Failed to perform work.", e);
            ErrorResponse er = ErrorResponse.InternalFailure();
            er.setRequestId(requestId);
            throw er;
        }
    }

    /**
     * Do work for this workflow step.
     * @param req
     */
    protected abstract T doWork0(T req, ServiceRequestContext context)
            throws Exception;

    /**
     * Obtain a session; either a new one, or one from context (default).
     * The context session will be shared with other context aware beans, rather
     * than being only accessible when passed everywhere explicitly.
     *
     * @return Session ready for use.
     */
    protected Session getSession() {
        return HibernateUtil.getSession();
    }

    /**
     * Static helper to initialize standard metrics for a worker.
     * @param group name of metric group (service, usually)
     * @param name specific metric name
     * @return
     */
    public static Map<String, Meter> initMeter(String group, String name) {
        return MetricsUtil.initMeter(group, name);
    }

    /**
     * Perform the standark mark metric; add response to meters.
     * @param meters
     * @param e
     */
    public void markStandard(Map<String, Meter> meters, Exception e) {
        MetricsUtil.markStandard(meters, e);
    }

    /**
     * Accumulate an event into the metrics for a service; either a good
     * response or an exception.
     * @param ret
     * @param e
     */
    protected void mark(T ret, Exception e)
    {
        // do nothing by default; descendant must override
    }



}
