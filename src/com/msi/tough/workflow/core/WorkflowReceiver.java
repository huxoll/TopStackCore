/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.workflow.core;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.zeromq.ZMQ;

import com.google.common.base.Charsets;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.ExecutorHelper;

public class WorkflowReceiver implements Runnable {

    private ZMQ.Context zmqContext = null;
    private ZMQ.Socket zmqRecvSocket = null;
    private ZMQ.Poller items = null;

    private final static Logger logger = Appctx
            .getLogger(WorkflowReceiver.class.getName());

    private AtomicBoolean done = new AtomicBoolean(false);

    @Resource
    /**
     * Should be ZMQ socket on which to listen.
     */
    private String recvEndpoint = "tcp://*:5555";

    @Resource
    /**
     * Workflow to process received jobs.
     */
    private Workflow workflow = null;

    private Future<?> executeResult = null;

    /**
     * Initialize receiver and begin listening for messages.
     *
     * @throws Exception
     */
    public void init() throws Exception {
        zmqContext = ZMQ.context(1);
        logger.debug("ZMQ initialized:" + ZMQ.getVersionString() +":"+ zmqContext.toString());

        logger.debug("Binding to ZMQ socket for receive:" +
                recvEndpoint);
        zmqRecvSocket = zmqContext.socket(ZMQ.PULL);
        zmqRecvSocket.bind(recvEndpoint);

        items = new ZMQ.Poller(1);
        items.register(zmqRecvSocket, ZMQ.Poller.POLLIN);
        executeResult = ExecutorHelper.execute(this);
    }

    /**
     * Shutdown receiver and clean up.
     *
     * @throws Exception
     */
    public void destroy() throws Exception {
        logger.debug("Destroying receiver.");
        done.set(true);
        logger.debug("Destroyed receiver, result:" + executeResult.get());
        if (zmqContext != null) {
            logger.debug("Terminating ZMQ.");
            zmqContext.term();
            zmqContext = null;
            logger.debug("Terminated ZMQ.");
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        while (!done.get() && !Thread.currentThread().isInterrupted()) {
            byte[] messageBytes, messageBytesPt2 = null;
            //logger.info("Polling."+new Date());
            items.poll(500);
            if (items.pollin(0)) {
                messageBytes = zmqRecvSocket.recv(0);
                logger.info("Got an item, len:"+messageBytes.length);
                try {
                    String returnAddress = null;
                    if (items.pollin(0)) {
                        messageBytesPt2 = zmqRecvSocket.recv(0);
                        returnAddress = new String(messageBytesPt2,
                                Charsets.UTF_8);
                        logger.info("Got request from:"+returnAddress);
                    }
                    Object payload = new Object[] { messageBytes, messageBytesPt2 };
                    workflow.doWorkRaw(payload);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        logger.debug("Exiting receive thread.");
        zmqRecvSocket.close();
    }
}
