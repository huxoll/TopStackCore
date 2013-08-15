/*
 * ExecutorHelper.java.java
 *
 * MSI Eucalyptus LoadBalancer Project
 * Copyright (C) Momentum SI
 *
 */

package com.msi.tough.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author raj
 *
 */
public class ExecutorHelper {
	public static abstract class Executable implements Runnable {
		private final Object[] args;

		public Executable(final Object... args) {
			this.args = args;
		}

		public Object[] getArgs() {
			return args;
		}
	}

	public static Future<?> execute(final Runnable r) {
		final ExecutorService exsrv = Appctx.getExecutorService();
		return exsrv.submit(r);
	}
}
