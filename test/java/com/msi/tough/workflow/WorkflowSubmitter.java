package com.msi.tough.workflow;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.amazonaws.services.simpledb.model.RequestTimeoutException;
import com.google.protobuf.Message;
import com.msi.tough.query.AsyncServiceImpl;
import com.msi.tough.query.AsyncServiceImpl.ServiceResponseListener;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.msi.tough.workflow.core.MuleIgniter;
import com.msi.tough.workflow.core.Workflow;

/**
 * Utility to submit jobs to workflow, await response.
 *
 * Useful for unit tests where we want to wait for a normally asynchronous
 * result.
 *
 * @author jgardner
 *
 */
@Component
public class WorkflowSubmitter {

    private static final int MAX_WAIT_SECS = 10;

    @Resource
    Workflow workflow = null;

    @Resource
    AsyncServiceImpl asyncService = null;

    @Resource
    MuleIgniter workflowIgniter = null;

    /**
     * Submit a workflow task and wait for response.
     *
     * @param groupName
     */
    public <T extends Message,V> V submitAndWait(T requestMessage) throws Exception {
        return submitAndWait(requestMessage, MAX_WAIT_SECS);
    }

    /**
     * Submit a workflow task and wait for response.
     *
     * @param requestMessage
     * @param timeout how long in secs to wait for a response (-1 for forever).
     */
    public <T extends Message,V> V submitAndWait(T requestMessage,
            int timeout) throws Exception {
        ServiceRequestContext context = new ServiceRequestContext();
        context.setAction("Unspecified");
        workflowIgniter.init(); // ensure workflow is initialized.
        ResponseListener listener = new ResponseListener(timeout);
        listener.registerForResponse(asyncService);
        workflow.doWork(requestMessage, context);
        @SuppressWarnings("unchecked")
        V ret = (V) listener.waitForResponse();
        listener.deregisterForResponse(asyncService);
        return ret;
    }

    public class ResponseListener implements ServiceResponseListener {

        private int maxWaitSecs = -1;
        private int waitMillis = 200;

        ServiceResponse response = null;
        ErrorResponse error = null;

        /**
         * @param timeout how long to wait (secs)
         */
        public ResponseListener(int timeout) {
            maxWaitSecs = timeout;
        }

        /* (non-Javadoc)
         * @see com.transcend.compute.servlet.AsyncServiceImpl.ServiceResponseListener#handleResponse(com.msi.tough.query.ServiceResponse)
         */
        @Override
        public void handleResponse(ServiceResponse response) {
            synchronized (this) {
                this.response = response;
            }
        }

        /* (non-Javadoc)
         * @see com.msi.tough.query.AsyncServiceImpl.ServiceResponseListener#handleError(com.msi.tough.query.ErrorResponse)
         */
        @Override
        public void handleError(ErrorResponse error) {
            synchronized (this) {
                this.error = error;
            }
        }

        public void registerForResponse(AsyncServiceImpl service) {
            service.addResponseListener(this);
        }

        public void deregisterForResponse(AsyncServiceImpl service) {
            service.removeResponseListener(this);
        }

        public Object waitForResponse() throws InterruptedException {
            for (int count = 0; count*waitMillis/1000 < maxWaitSecs ||
                    maxWaitSecs < 0; count+=1) {
                synchronized (this) {
                    if (response != null) {
                        Object returned = response.getPayload();
                        clear();
                        return returned;
                    }
                    if (error != null) {
                        ErrorResponse e = error;
                        clear();
                        throw e;
                    }
                }
                Thread.sleep(waitMillis);
            }
            throw new RequestTimeoutException("Request timed out.");
        }

        public void clear() {
            response = null;
            error = null;
        }
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }
}
