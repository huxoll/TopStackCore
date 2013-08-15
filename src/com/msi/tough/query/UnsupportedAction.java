/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.query;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.msi.tough.workflow.core.Workflow;

/**
 * Placeholder for actions not yet supported; calling an action that is mapped
 * to this will cause an error to be returned.
 *
 * @author jgardner
 *
 */
public class UnsupportedAction implements QueuedAction, Action {

    private String action = "";

    /**
     *
     */
    public UnsupportedAction() {
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.QueuedAction#process(com.msi.tough.query.ServiceRequest, com.msi.tough.query.ServiceResponse)
     */
    @Override
    public void process(ServiceRequest req, ServiceResponse resp) {
        throw QueryFaults.notSupported();
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.QueuedAction#getAction()
     */
    @Override
    public String getAction() {
        return action;
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.QueuedAction#setAction(java.lang.String)
     */
    @Override
    public void setAction(String action) {
        this.action = action;
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.QueuedAction#isUseContextSession()
     */
    @Override
    public boolean isUseContextSession() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.QueuedAction#setUseContextSession(boolean)
     */
    @Override
    public void setUseContextSession(boolean useContextSession) {
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.QueuedAction#getWorkflow()
     */
    @Override
    public Workflow getWorkflow() {
        // Should never be called.
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.QueuedAction#setWorkflow(com.msi.tough.workflow.core.Workflow)
     */
    @Override
    public void setWorkflow(Workflow workflow) {
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.Action#process(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void process(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        throw QueryFaults.notSupported();
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.QueuedAction#getSession()
     */
    @Override
    public Session getSession() {
        // Should never be called.
        throw new UnsupportedOperationException();
    }

}
