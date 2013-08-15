/**
 *
 */
package com.msi.tough.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.utils.MetricsUtil;

/**
 * @author jgardner
 */
public class StandardContextListener implements ServletContextListener {
	private static final Logger logger = Appctx
			.getLogger(StandardContextListener.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(final ServletContextEvent event) {
		// destroy meters
		logger.debug("destroy meters");
		MetricsUtil.stopAllMeters();

		logger.debug("Context is destroyed.");
		Appctx ctx = Appctx.instanceMaybe();
		if (ctx != null) {
			ctx.destroy();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet
	 * .ServletContextEvent)
	 */
	@Override
	public void contextInitialized(final ServletContextEvent event) {
		logger.debug("Context is initialized.");
	}
}
