/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2013
 * All Rights Reserved.
 */
package com.msi.tough.workflow.core;

import org.mule.api.MuleContext;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.msi.tough.core.Appctx;

/**
 * Programmatic instantiation of mule (for non-web contexts).
 *
 * @author jgardner
 *
 */
public class MuleIgniter implements ApplicationContextAware {
    private final Logger logger = Appctx
            .getLogger(MuleIgniter.class.getName());

    private static MuleIgniter instance = null;
    private MuleContext muleContext = null;
    private ApplicationContext appContext;
    private String configFiles = null;
    private boolean initialized = false;

    public MuleIgniter() {
    }

    public static MuleIgniter getInstance() {
        if (instance == null) {
            instance = new MuleIgniter();
        }
        return instance;
    }

    public void setConfigFiles(String configFiles) {
        this.configFiles = configFiles;
    }

    public void init() throws Exception {
        if (!initialized) {
            SpringXmlConfigurationBuilder builder =
                    new SpringXmlConfigurationBuilder(configFiles);
            builder.setParentContext(appContext);

            muleContext =
                    new DefaultMuleContextFactory().createMuleContext(builder);
            muleContext.start();
            initialized = true;
            logger.debug("Started Mule.");
        }
    }

    public void destroy() throws Exception {
        if (initialized) {
            muleContext.dispose();
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.appContext = applicationContext;
    }
}
