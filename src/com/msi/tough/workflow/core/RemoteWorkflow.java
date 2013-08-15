/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2013
 * All Rights Reserved.
 */
package com.msi.tough.workflow.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.zeromq.ZMQ;

import com.google.common.base.Charsets;
import com.google.protobuf.Message;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.AsyncServiceImpl;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ProtobufUtil;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;

/**
 * Deposit a task in a remote workflow engine and listen for a response.
 *
 * This is likely to be used only for tests, where no persistent connection
 * is required.
 *
 * @author jgardner
 *
 */
public class RemoteWorkflow implements Workflow, Runnable {
    private final Logger logger = Appctx
            .getLogger(RemoteWorkflow.class.getName());

    @Resource
    /**
     * Should be ZMQ socket on which a remote workflow is listening.
     */
    private String remoteEndpoint = "tcp://localhost:5560";

    @Resource
    /**
     * Should be ZMQ socket on which to listen for a response.
     */
    private String recvEndpoint = "tcp://localhost:5561";

    @Resource
    /**
     * Should be ZMQ socket to which responses will be sent.
     */
    private String returnAddress = "tcp://localhost:5561";

    @Resource
    AsyncServiceImpl asyncService = null;

    private boolean expectStrings = false;

    private ZMQ.Context zmqContext = null;
    private ZMQ.Socket zmqRecvSocket = null;
    private ZMQ.Poller items = null;
    private Thread recvThread = null;
    private ServiceRequestContext context = null;
    private List<Class<? extends Message>> possibleTypes = null;

    /**
     * @param actionMap
     */
    public RemoteWorkflow() {
    }

    public static RemoteWorkflow getInstance() {
        // Not actually a singleton, since we submit to multiple workflows.
        return new RemoteWorkflow();
    }

    public void setPossibleTypes(List<Class<? extends Message>> possibleTypes) {
        this.possibleTypes = possibleTypes;
    }

    /* (non-Javadoc)
     * @see com.msi.tough.workflow.core.Workflow#doWork(com.google.protobuf.Message)
     */
    @Override
    public Message doWork(Message request, ServiceRequestContext context)
            throws ErrorResponse {
        try {
            // Supply action name, if needed by endpoint.
            String workEndpoint  = remoteEndpoint.replaceFirst("ACTION",
                    context.getAction() != null? context.getAction() : "");
            zmqContext = ZMQ.context(1);

            synchronized (this) {
                if (zmqRecvSocket == null) {
                    logger.debug("Binding to ZMQ socket for receive:" +
                            recvEndpoint);
                    zmqRecvSocket = zmqContext.socket(ZMQ.PULL);
                    zmqRecvSocket.bind(recvEndpoint);
                    items = new ZMQ.Poller(1);
                    items.register(zmqRecvSocket, ZMQ.Poller.POLLIN);
                    recvThread = new Thread(this);
                    recvThread.start();
                }
            }

            this.context = context;
            ZMQ.Socket zmqSocket = zmqContext.socket(ZMQ.PUSH);
            logger.info("Connecting to remote ZMQ socket:" +
                    workEndpoint);
            zmqSocket.connect(workEndpoint);
            boolean sent = zmqSocket.send(request.toByteArray(), ZMQ.SNDMORE);
            // Send my return address as a second part.
            logger.info("Sending as callback ZMQ socket:" +
                    returnAddress);
            sent = zmqSocket.send(returnAddress.getBytes(), 0);
            Object action = ProtobufUtil.getOptionalField(request, "action");
            logger.debug("Sent message: "+
                    (action != null? action.toString() : sent) + " "+ request);
            zmqSocket.close();

        } catch (Exception e) {
            logger.warn("Error submitting to workflow.", e);
            throw ErrorResponse.InternalFailure();
        }
        return null;
    }

    public synchronized void destroy() {
        if (zmqRecvSocket != null) {
            recvThread.interrupt();
        }
        if (zmqContext != null) {
            //zmqContext.term();
        }
    }

    private Object deserializeFromByteArray(byte[] bytes) {
        if (bytes.length == 0) {
            return null;
        }
        if (expectStrings) {
            return new String(bytes, Charsets.UTF_8);
        }
        for (Class<? extends Message> type : possibleTypes) {
            Method parseFromMethod;
            try {
                parseFromMethod = type.getDeclaredMethod("parseFrom",
                        byte[].class);
            } catch (SecurityException e) {
                throw new IllegalStateException("Failed to call parseFrom", e);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("Missing parseFrom", e);
            }
            try {
                Object protobufResult = parseFromMethod.invoke(null,
                        bytes);
                return protobufResult;
            } catch (RuntimeException re) {
                // probably not correct type
                continue;
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Call to parseFrom failed", e);
            } catch (InvocationTargetException e) {
                continue;
            }
        }
        throw new UnexpectedTypeException();
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            byte[] messageBytes;
            items.poll();
            if (items.pollin(0)) {
                messageBytes = zmqRecvSocket.recv(0);
                try {
                    Object message = deserializeFromByteArray(messageBytes);
                    if (message == null) {
                        logger.error("Unable to deserialize response",
                                new String(messageBytes, "utf-8"));
                    } else {
                        logger.debug("Received response to:"+context.getRequestId());
                        ServiceResponse response = new ServiceResponse(message,
                                context.getRequestId());
                        asyncService.handleResponse(response);
                    }
                } catch (UnexpectedTypeException ute) {
                    try {
                        asyncService.handleError(ute);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        logger.debug("Exiting receive thread.");
        zmqRecvSocket.close();
    }

    /* (non-Javadoc)
     * @see com.msi.tough.workflow.core.Workflow#doWorkRaw(java.lang.Object)
     */
    @Override
    public void doWorkRaw(Object payload) {
        throw new IllegalArgumentException("Raw not supported for remote.");
    }

    public static class UnexpectedTypeException extends ErrorResponse {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        /**
         * @param type
         * @param message
         * @param code
         */
        public UnexpectedTypeException() {
            super(TYPE_SENDER,
                  "No matching type for wire message.",
                  CODE_INTERNAL_FAILURE);
        }

    }

    /**
     * @param remoteEndpoint the remoteEndpoint to set
     */
    public void setRemoteEndpoint(String remoteEndpoint) {
        this.remoteEndpoint = remoteEndpoint;
    }

    /**
     * @param recvEndpoint the recvEndpoint to set
     */
    public void setRecvEndpoint(String recvEndpoint) {
        this.recvEndpoint = recvEndpoint;
    }

    /**
     * @param returnAddress the returnAddress to set
     */
    public void setReturnAddress(String returnAddress) {
        this.returnAddress = returnAddress;
    }


}
