/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2013
 * All Rights Reserved.
 */
package com.msi.tough.workflow.core;

import javax.annotation.Resource;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.context.MuleContextAware;
import org.mule.module.client.MuleClient;
import org.slf4j.Logger;

import com.google.protobuf.Message;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequestContext;

/**
 * Initiator class, to fire off a Mule endpoint from our app code.
 *
 * @author jgardner
 *
 */
public class MuleWorkflow implements Workflow, MuleContextAware {
    private final Logger logger = Appctx
            .getLogger(MuleWorkflow.class.getName());

    private MuleContext muleContext;
    private static MuleWorkflow instance = null;
    @Resource
    private String endpoint = "ACTIONIn";
    @Resource
    private String rawEndpoint = "DirectToZMQEntry";

    public MuleWorkflow() {
    }

    public static MuleWorkflow getInstance() {
        if (instance == null) {
            instance = new MuleWorkflow();
        }
        return instance;
    }

    /* (non-Javadoc)
     * @see com.msi.tough.workflow.core.Workflow#doWork(com.google.protobuf.Message)
     */
    @Override
    public Message doWork(Message request, ServiceRequestContext context)
            throws ErrorResponse {
        try {
            MuleClient client = new MuleClient(muleContext);
            // Supply action name, if needed by endpoint.
            String workEndpoint  = endpoint.replaceFirst("ACTION",
                    context.getAction() != null? context.getAction() : "");
            client.dispatch(workEndpoint, request, null);
            // For now, use dispatch, for one-way.  Maybe support req/resp?
            //MuleMessage result = client.send("", request, null);
            //System.out.println("Got payload:"+result.getPayloadAsString());
        } catch (MuleException e) {
            logger.warn("Fatal exception dispatching to workflow", e);
            throw ErrorResponse.InternalFailure();
        } catch (Exception e) {
            logger.warn("Error extracting payload.", e);
            throw ErrorResponse.InternalFailure();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.mule.api.context.MuleContextAware#setMuleContext(org.mule.api.MuleContext)
     */
    @Override
    public void setMuleContext(MuleContext muleContext) {
        this.muleContext = muleContext;
    }

    /* (non-Javadoc)
     * @see com.msi.tough.workflow.core.Workflow#doWorkRaw(java.lang.Object)
     */
    @Override
    public void doWorkRaw(Object payload) {
        try {
            MuleClient client = new MuleClient(muleContext);
            // Supply action name, if needed by endpoint.
            String workEndpoint = rawEndpoint.replaceFirst("ACTION","");
            client.dispatch(workEndpoint, payload, null);
        } catch (MuleException e) {
            logger.warn("Fatal exception dispatching to workflow", e);
            throw ErrorResponse.InternalFailure();
        }
    }

}
