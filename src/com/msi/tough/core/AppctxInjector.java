/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2013
 * All Rights Reserved.
 */

package com.msi.tough.core;

import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Glue code to inject application context into Appctx; avoid loading app
 * context explicitly and instead allow container to load it.
 *
 * This allows using Spring servlet context, unit tests, etc. to exert more
 * control over loading of contexts.
 *
 * @author jgardner
 *
 */
public class AppctxInjector implements ApplicationContextAware {

    @Resource
    Appctx appctx = null;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.context.ApplicationContextAware#setApplicationContext
     * (org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext appContext)
            throws BeansException {
        appctx.setAppctx(appContext);
    }

}
