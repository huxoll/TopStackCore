/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2013
 * All Rights Reserved.
 */
package com.msi.tough.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;

import com.google.protobuf.Message;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.QueuedAction;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceResponse;

/**
 * @author jgardner
 */
public class AsyncServiceImpl
{
    private final static Logger logger = Appctx.getLogger(AsyncServiceImpl.class
        .getName());

    private final Map<String, QueuedAction> actionMap;
    private List<ServiceResponseListener> listeners =
            new ArrayList<ServiceResponseListener>();

    public AsyncServiceImpl(final Map<String, QueuedAction> actionMap)
    {
        this.actionMap = actionMap;
    }

    public void process(final ServiceRequest req,
        final ServiceResponse resp) throws Exception
    {
        final QueuedAction a = this.actionMap.get(req.getParameter("Action"));
        if (a == null)
        {
            logger.debug("No action exists for " + req.getParameter("Action"));
            logger.debug("Those that exist are:");
            for (Entry<String, QueuedAction> item : this.actionMap.entrySet())
            {
                logger.error("\"" + item.getKey() + "\"");
            }
            throw QueryFaults.notSupported();
        }
        else
        {
            logger.debug("calling action " + a);
            try {
                logger.debug("Starting to process " + req.getParameter("Action") + " request.");
                a.process(req, resp);
            }
            catch (ErrorResponse er) {
                er.setRequestId(req.getRequestId());
                handleError(er);
            }
            catch (Exception e) {
                e.printStackTrace();
                ErrorResponse error = ErrorResponse.InternalFailure();
                error.setRequestId(req.getRequestId());
                handleError(error);
            }
        }
    }

    public void handleResponse(final ServiceResponse resp) throws Exception {
        for (ServiceResponseListener listener : listeners) {
            listener.handleResponse(resp);
        }
    }

    public void handleError(final ErrorResponse resp) throws Exception {
        for (ServiceResponseListener listener : listeners) {
            listener.handleError(resp);
        }
    }

    public void handleMessage(final Message generalMessage) throws Exception {
        // Nothing to do; this message should already have been sent to a
        // requestor.
    }

    public void addResponseListener(ServiceResponseListener listener) {
        listeners.add(listener);
    }

    public void removeResponseListener(ServiceResponseListener listener) {
        listeners.remove(listener);
    }

    public static interface ServiceResponseListener {
        public void handleResponse(final ServiceResponse response);
        public void handleError(final ErrorResponse error);
    }
}
