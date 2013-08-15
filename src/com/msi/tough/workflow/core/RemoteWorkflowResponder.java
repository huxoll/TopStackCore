/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2013
 * All Rights Reserved.
 */
package com.msi.tough.workflow.core;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.processor.MessageProcessor;
import org.slf4j.Logger;
import org.zeromq.ZMQ;

import com.google.common.base.Charsets;
import com.google.protobuf.Message;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;

/**
 * Direct a workflow response back to a requestor.
 *
 * This is likely to be used only for tests, where no persistent connection
 * is required.
 *
 * @author jgardner
 *
 */
public class RemoteWorkflowResponder implements MessageProcessor {
    private final Logger logger = Appctx
            .getLogger(RemoteWorkflowResponder.class.getName());

    private static RemoteWorkflowResponder instance = null;

    ZMQ.Context zmqContext = null;
    ZMQ.Socket zmqRecvSocket = null;
    ZMQ.Poller items = null;
    Thread recvThread = null;
    ServiceRequestContext context = null;

    /**
     * @param actionMap
     */
    private RemoteWorkflowResponder() {
        zmqContext = ZMQ.context(1);
    }

    public static RemoteWorkflowResponder getInstance() {
        if (instance == null) {
            instance = new RemoteWorkflowResponder();
        }
        return instance;
    }

    public void destroy() {
        logger.info("Destroying remote workflow responder.");
        // Terminate may only be called after all sockets are closed.
        //zmqContext.term();
    }

    /* (non-Javadoc)
     * @see org.mule.api.processor.MessageProcessor#process(org.mule.api.MuleEvent)
     */
    @Override
    public MuleEvent process(MuleEvent event) throws MuleException {

        byte[] bytes = event.getSession().getProperty("returnAddress");
        String returnAddress = new String(bytes, Charsets.UTF_8);
        ZMQ.Socket zmqSocket = zmqContext.socket(ZMQ.PUSH);
        try {
            zmqSocket.connect(returnAddress);
            Object payload = event.getMessage().getPayload();
            byte[] payloadData = new byte[] {};
            if (payload instanceof Message) {
                logger.info("Sending response of type: " +
                        payload.getClass().getSimpleName());
                payloadData = ((Message) payload).toByteArray();
            } else if (payload instanceof ErrorResponse) {
                ErrorResponse errorResp = (ErrorResponse) payload;
                payloadData = errorResp.toErrorResult().toByteArray();
                event = null;
            } else if (payload instanceof ServiceResponse) {
                payloadData = ((ServiceResponse) payload).getPayload().
                        toString().getBytes(Charsets.UTF_8);
            } else {
                logger.warn("Unhandled response object: " + payload.getClass());
                return event;
            }
            logger.info("Sending response to:"+returnAddress);
            boolean sent = zmqSocket.send(payloadData, 0);
            logger.info("Sending response: "+sent);
        } catch (RuntimeException e) {
            logger.warn("Error sending response.", e);
            throw e;
        } finally {
            zmqSocket.close();
        }
        return event;
    }
}
