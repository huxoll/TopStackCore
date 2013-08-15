/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.utils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * For testing, an ExecutorService that is synchronous (to avoid sleeping in
 * unit tests to verify functionality).
 * @author jgardner
 *
 */
public class SynchronousExecutor extends AbstractExecutorService {

    private boolean running = true;

    /**
     */
    public SynchronousExecutor() {
    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.ExecutorService#awaitTermination(long, java.util.concurrent.TimeUnit)
     */
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        running = false;
        return ! running;
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.ExecutorService#isShutdown()
     */
    @Override
    public boolean isShutdown() {
        return ! running;
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.ExecutorService#isTerminated()
     */
    @Override
    public boolean isTerminated() {
        return ! running;
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.ExecutorService#shutdown()
     */
    @Override
    public void shutdown() {
        running = false;
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.ExecutorService#shutdownNow()
     */
    @Override
    public List<Runnable> shutdownNow() {
        running = false;
        return Arrays.asList(new Runnable[] {});
    }
}
