/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.Logger;

import com.google.protobuf.Message;
import com.msi.tough.core.Appctx;
import com.msi.tough.workflow.WorkflowSubmitter;

/**
 * Abstract base for all helpers.  Maintain some state across tests, so that we
 * can re-use instances and other assets across tests.
 *
 * @author jgardner
 *
 */
public abstract class AbstractHelper<K extends Serializable> {

    private final static Logger logger = Appctx
            .getLogger(AbstractHelper.class.getName());

    // Set to true while debugging, so breakpoints won't cause timeouts
    // Can be set to true by setting system property TopStackDebug to true.
    // E.g., set that on IDE JDK and debugging will never time out.
    private static boolean DEBUG = false;

    static {
        String debugFlag = System.getProperty("TopStackDebug", null);
        if (debugFlag != null && !debugFlag.equalsIgnoreCase("false")) {
            DEBUG = true;
        }
    }
    /**
     * Set of unique identifiers for known entities
     */
    private Set<K> existingEntities = null;

    protected WorkflowSubmitter workflowSubmitter = null;

    /**
     *
     */
    public AbstractHelper() {
        super();
        refreshExistingEntities();
    }

    /**
     * Entity name, for separating the entity managed by this helper from other
     * types.
     * @return
     */
    public abstract String entityName();

    /**
     * General method to create a thing by ID.
     * @param identifier
     * @throws Exception
     */
    public abstract void create(K identifier) throws Exception;

    /**
     * General method to destroy a thing by ID.
     * @param identifier
     * @throws Exception
     */
    public abstract void delete(K identifier) throws Exception;

    public void refreshExistingEntities() {
        try
        {
           FileInputStream fileIn =
                   new FileInputStream("test"+entityName()+".ser");
           ObjectInputStream in =
                              new ObjectInputStream(fileIn);
           Object o = in.readObject();
           if (o instanceof ConcurrentSkipListSet<?>) {
               @SuppressWarnings("unchecked")
               ConcurrentSkipListSet<K> concurrentSkipListSet =
               (ConcurrentSkipListSet<K>) o;
               existingEntities = concurrentSkipListSet;
           }
           in.close();
           fileIn.close();
        } catch(FileNotFoundException ioe) {
            // Ignore, probably a new run.
        } catch(IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException("Failed to refresh existing.", ioe);
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            throw new RuntimeException("Failed to refresh existing.", cnfe);
        }
        if (existingEntities == null) {
            existingEntities =
                new ConcurrentSkipListSet<K>();
        }
    }

    /**
     * Write out current known entities to file, to be used by following tests.
     */
    public void flushExistingEntities() {
        try
        {
           FileOutputStream fileOut =
                   new FileOutputStream("test"+entityName()+".ser");
           ObjectOutputStream out =
                              new ObjectOutputStream(fileOut);
           out.writeObject(existingEntities);
           out.close();
           fileOut.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException("Failed to flush existing.", ioe);
        }
    }

    protected void addEntity(K identifier) {
        existingEntities.add(identifier);
        flushExistingEntities();
    }

    protected void removeEntity(K identifier) {
        existingEntities.remove(identifier);
        flushExistingEntities();
    }

    public Collection<K> getExistingEntities() {
        return existingEntities;
    }

    public void finalDestroy() {
        performFinalDestroy();
        File file = new File("test"+entityName()+".ser");
        file.delete();
    }

    /**
     * Set the submitter to be used by subclasses (for remote versus in-VM).
     * @param submitter
     */
    public abstract void setWorkflowSubmitter(WorkflowSubmitter submitter);

    /**
     * To be implemented by ultimate helper; delete all instances.  This should
     * only be run after all tests have completed.
     */
    public void performFinalDestroy() {
        for (K ident : getExistingEntities()) {
            try {
                logger.info("Final destroy: destroying "+ident);
                delete(ident);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Pass through, for submitting test-built requests to the deployed webapp.
     * @param requestMessage
     * @return
     * @throws Exception
     */
    public <T extends Message, V> V submitAndWait(T requestMessage)
            throws Exception {
        if (DEBUG) {
            return workflowSubmitter.submitAndWait(requestMessage, -1);
        } else {
            return workflowSubmitter.submitAndWait(requestMessage);
        }
    }

}