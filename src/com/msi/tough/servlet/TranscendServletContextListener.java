package com.msi.tough.servlet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.msi.tough.core.Appctx;
import com.msi.tough.utils.MetricsUtil;

public class TranscendServletContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(final ServletContextEvent ev) {
		// destroy meters
		MetricsUtil.stopAllMeters();

		final ExecutorService executor = Appctx.getExecutorService();
		if (executor != null) {
			executor.shutdownNow();
			final TimeUnit unit = TimeUnit.SECONDS;
			try {
				executor.awaitTermination(30, unit);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void contextInitialized(final ServletContextEvent ev) {
	}
}
