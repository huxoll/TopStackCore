/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.scheduler;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.msi.tough.core.Appctx;

/**
 * Provides entry point to kick of scheduling, if enabled.
 *
 * @author jgardner
 *
 */
public class SchedulerInit implements ApplicationListener<ApplicationEvent> {
    private static Logger logger = Appctx
            .getLogger(SchedulerInit.class.getName());

    private boolean enabled = false;
    
    @Resource(name="schedFactoryBean")
    private Scheduler scheduler = null;

    private boolean started = false;
    
    /**
     * @return true if schedule is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the new value for enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Initializer; it's not actually safe for this to be PostConstruct,
     * because some beans downstream use Appctx.getBean, which will be trying
     * to call the context before it's complete.  See onApplicationEvent below.
     */
    public void init() throws SchedulerException {
        if (enabled) {
            logger.info("Starting Scheduler.");
            //final SchedulerFactory sf = new StdSchedulerFactory();
            try {
                if (! started) {
                    //scheduler = sf.getScheduler();
                    scheduler.start();
                    started = true;
                } else {
                    logger.error("Ignoring second init.");
                }
            } catch (SchedulerException e) {
                logger.error("Failed to create scheduler.", e);
            }
        }
    }

    @PreDestroy
    public synchronized void destroy() throws InterruptedException, SchedulerException {
        logger.info("Destroying Scheduler.");
        logger.info("Scheduler name: "+ scheduler.getSchedulerName());
        try {
        	boolean test = scheduler==null;
        	logger.info("scheduler is null?:" + test);
            if (scheduler != null) {
                scheduler.shutdown(true);
                //Sleep seems to be necessary to let threads terminate.  scheduler.isShutdown and scheduler.isStarted both dont' seem to be useful for polling for this.
                Thread.sleep(1000);
                scheduler = null;
            }
        } catch (SchedulerException e) {
            logger.error("Failed to shutdown scheduler.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            // Context has been completely loaded, it's now safe to
            // initialize old beans that use the getBean() anti-pattern
            try {
				init();
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}
