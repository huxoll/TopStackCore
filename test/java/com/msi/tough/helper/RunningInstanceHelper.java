/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.helper.AbstractHelper;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.workflow.WorkflowSubmitter;

/**
 * @author jgardner
 *
 */
public class RunningInstanceHelper extends AbstractHelper<String> {
    private final static Logger logger = Appctx
            .getLogger(RunningInstanceHelper.class.getName());

    private static final int MAX_WAIT_SECS = 60;
    private static final int WAIT_SECS = 2;

    @Resource(name = "accessKey")
    String accessKey = null;

    @Resource
    protected String defaultAvailabilityZone = null;

    @Resource
    private String defaultFlavor = null;

    @Resource
    protected String baseImageId = null;

    @Resource
    protected AmazonEC2Client computeClient = null;

    /**
     *
     */
    public RunningInstanceHelper() {
        super();
    }

    /**
     * Construct a minimal valid run instance request.
     *
     * @param lbName
     * @return
     */
    public RunInstancesRequest runInstancesRequest(String name) {
        final RunInstancesRequest request = new RunInstancesRequest();
        Placement place = new Placement();
        place.setAvailabilityZone(defaultAvailabilityZone);
        request.setPlacement(place);
        request.setImageId(baseImageId);
        request.setInstanceType(defaultFlavor);
        request.setMinCount(1);
        request.setMaxCount(1);
        // TODO: set hostname from user data, since that's an option on private.
        request.setUserData("hostname="+name);
        return request;
    }

    /**
     * Construct a minimal valid describe instance request.
     *
     * @param instanceId
     * @return
     */
    public DescribeInstancesRequest describeInstancesRequest(String instanceId) {
        final DescribeInstancesRequest describe = new DescribeInstancesRequest();
        {
            final List<String> instanceIds = new ArrayList<String>();
            instanceIds.add(instanceId);
            describe.setInstanceIds(instanceIds);
        }
        return describe;
    }

    /**
     * Describe an instance.
     *
     * @param instanceId
     */
    public Instance describeInstance(String instanceId) throws Exception {
        DescribeInstancesRequest request =
                describeInstancesRequest(instanceId);
        final DescribeInstancesResult result = computeClient.describeInstances(
                request);
        final Instance describedInstance = result.getReservations()
                .get(0).getInstances().get(0);
        return describedInstance;
    }

    /**
     * Run an instance.
     *
     * @param name
     */
    public String runInstance(String name) throws Exception {
        RunInstancesRequest request =
                runInstancesRequest(name);
        final RunInstancesResult runResult = computeClient.runInstances(
                request);
        Instance createdInstance = runResult.getReservation()
                .getInstances().get(0);
        addEntity(createdInstance.getInstanceId());
        return createdInstance.getInstanceId();
    }

    /**
     * Obtain or create a running instance (may be shared with other tests).
     *
     * @param suggestedName name to be used, if created.
     */
    public String getOrCreateInstance(String suggestedName) throws Exception {
        Collection<String> existing = getExistingEntities();
        if (! existing.isEmpty()) {
            String instanceId = existing.iterator().next();
            logger.debug("Found existing: " + instanceId);
            DescribeInstancesRequest request =
                    describeInstancesRequest(instanceId);
            try {
                // Describe it, to make sure it's still there.
                computeClient.describeInstances(request);
                return instanceId;
            } catch (AmazonServiceException ase) {
                if ("InvalidInstance.NotFound".equals(ase.getErrorCode())) {
                    logger.debug("Existing: " + instanceId +
                            " not found, create new.");
                }
            }
        }
        String instanceId = runInstance(suggestedName);
        return instanceId;
    }

    /**
     * Wait until instance is running (or some other state).
     *
     * @param instanceId
     * @param state desired run state
     */
    public String waitForState(String instanceId, String state) throws Exception {
        boolean done = false;
        int count;
        Instance describedInstance = null;
        for (count = 0; count < MAX_WAIT_SECS; count += WAIT_SECS) {
            try {
                describedInstance = describeInstance(instanceId);
            } catch (AmazonServiceException ase) {
                // if terminating, may not see "terminated" before gone.
                if ("terminated".equals(state) &&
                        "InvalidInstance.NotFound".equals(ase.getErrorCode())) {
                    return state;
                } else {
                    throw ase;
                }
            }
            logger.info("instance state:" + describedInstance.getState());
            if (describedInstance.getState().getName().equals(state)) {
                done = true;
                break;
            }
            Thread.sleep(1000 * WAIT_SECS);
        }
        if (!done) {
            return describedInstance.getState().getName();
        }
        logger.info("Instance is " + state + " after " + count + " seconds.");
        return state;
    }

    /**
     * Construct a terminate instance request.
     *
     * @param instanceId
     * @return
     */
    public TerminateInstancesRequest terminateInstanceRequest(String instanceId) {
        final TerminateInstancesRequest terminateRequest = new TerminateInstancesRequest();
        {
            final List<String> instanceIds = new ArrayList<String>();
            instanceIds.add(instanceId);
            terminateRequest.setInstanceIds(instanceIds);
        }
        return terminateRequest;
    }

    /**
     * Terminate an instance with the given id.
     *
     * @param instanceId
     */
    public void terminateInstance(String instanceId) throws Exception {
        TerminateInstancesRequest request = terminateInstanceRequest(instanceId);
        try {
            computeClient.terminateInstances(request);
        } catch (AmazonServiceException ase) {
            if ("InvalidInstance.NotFound".equals(ase.getErrorCode())) {
                logger.debug("Instance already gone: " + instanceId);
            } else {
                throw ase;
            }
        }
        removeEntity(instanceId);
    }

    /**
     * Delete all instances created by tests (for test-end cleanup).
     */
    public void deleteAllCreatedLoadBalancers() throws Exception {
        // defer deletion.
    }

    /* (non-Javadoc)
     * @see com.msi.tough.helper.AbstractHelper#entityName()
     */
    @Override
    public String entityName() {
        return "RunningInstance";
    }

    /* (non-Javadoc)
     * @see com.msi.tough.helper.AbstractHelper#create(java.io.Serializable)
     */
    @Override
    public void create(String identifier) throws Exception {
        runInstance(identifier);
    }

    /* (non-Javadoc)
     * @see com.msi.tough.helper.AbstractHelper#delete(java.io.Serializable)
     */
    @Override
    public void delete(String identifier) throws Exception {
        terminateInstance(identifier);
    }

    /* (non-Javadoc)
     * @see com.msi.tough.helper.AbstractHelper#setWorkflowSubmitter(com.msi.tough.workflow.WorkflowSubmitter)
     */
    @Override
    public void setWorkflowSubmitter(WorkflowSubmitter submitter) {
    }
}