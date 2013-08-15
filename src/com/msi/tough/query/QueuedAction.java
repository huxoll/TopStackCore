package com.msi.tough.query;

import org.hibernate.Session;

import com.msi.tough.workflow.core.Workflow;

public interface QueuedAction {
    /**
     * Deposit the request for processing.
     * @param req
     * @param resp
     */
    public void process(ServiceRequest req, ServiceResponse resp);

    public String getAction();

    public void setAction(String action);

    public boolean isUseContextSession();

    public void setUseContextSession(boolean useContextSession);

    public Workflow getWorkflow();

    public void setWorkflow(Workflow workflow);

    public Session getSession();

}
