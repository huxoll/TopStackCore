/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2013
 * All Rights Reserved.
 */
package com.msi.tough.workflow.core;

import com.google.protobuf.Message;
import com.msi.tough.query.ServiceRequestContext;

public interface Workflow {

    /**
     * Generic workflow processing step.
     *
     * @param request
     * @return
     */
    public Message doWork(Message request, ServiceRequestContext context);

    /**
     * Generic workflow processing step, for bare messages
     *
     * @param request
     * @return
     */
    public void doWorkRaw(Object payload);
}
