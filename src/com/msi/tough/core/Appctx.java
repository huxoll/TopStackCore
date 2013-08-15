/*
 * Appctx.java
 *
 * MSI Eucalyptus LoadBalancer Project
 * Copyright (C) Momentum SI
 *
 */

package com.msi.tough.core;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.msi.tough.core.ExecutorHelper.Executable;

/**
 * Utility class to help integration of various java packages
 *
 * @author raj
 *
 */
public class Appctx {

	/**
	 * Singleton object
	 */
	private static Appctx singleton = null;
	private static Map<String, Object> config = null;
	private static ThreadLocal<Appctx> threadCtx = new ThreadLocal<Appctx>();
	private static ThreadLocal<Map<String, Object>> threadMap = new ThreadLocal<Map<String, Object>>();
	private static ExecutorService executorService;

	private static boolean loadDefault = true;

	private static void defLoad() {
		System.out.println("***** LOADING APPCTX Default ****");
		singleton = new Appctx();
		if (loadDefault) {
			singleton.appctx = new ClassPathXmlApplicationContext(
					"application-context.xml");
		}
		executorService = Executors.newCachedThreadPool();
	}

	/**
	 * Get a bean from Spring Application Context
	 *
	 * @param <R>
	 *            Generic Type of bean to get
	 * @param name
	 *            bean name to lookup in spring application context
	 * @return the bean
	 */
	@SuppressWarnings("unchecked")
	public static <R> R getBean(final String name) {
		return (R) instance().get(name);
	}

	public static Map<String, Object> getConfiguration() {
		if (config == null) {
			config = getBean("configuration");
			System.out.println("LOADING CONFIGURATION");
			SystemEnvUtils.expandMap(config);
		}
		return config;
	}

	@SuppressWarnings("unchecked")
	public static <R> R getConfigurationBean(final String name) {
		return (R) getConfiguration().get(name);
	}

	public static ConvertUtils getConverUtils() {
		return getBean("ConverUtils");
	}

	public static ExecutorService getExecutorService() {
	    if (executorService == null) {
	        executorService = Executors.newCachedThreadPool();
	    }
		return executorService;
	}

    public void setExecutorServiceInstance(ExecutorService executorService) {
        Appctx.executorService = executorService;
    }

	/**
	 * Obtain an instance of Appctx, uninitialized, suitable for dependency
	 * injection.
	 */
	public static Appctx getInstanceForInjection() {
		if (singleton == null) {
			singleton = new Appctx();
		}
		return singleton;
	}

	/**
	 * Get a logger for a name
	 *
	 * @param name
	 * @return logger
	 */
	public static Logger getLogger(final String name) {
		return LoggerFactory.getLogger(name);
	}

	public static Object getThreadMap(final String key) {
		final Map<String, Object> m = threadMap.get();
		if (m == null) {
			return null;
		}
		return m.get(key);
	}

	private static long getTime(final String filePath) {
		final File f = new File(filePath);
		return f.lastModified();
	}

	/**
	 * Return singleton object
	 *
	 * @return singleton
	 */
	public static Appctx instance() {
		if (threadCtx.get() != null) {
			return threadCtx.get();
		}
		if (singleton == null) {
			defLoad();
		}
		return singleton;
	}

    /**
     * Return singleton object
     *
     * @return singleton
     */
    public static Appctx instanceMaybe() {
        if (threadCtx.get() != null) {
            return threadCtx.get();
        }
        if (singleton == null) {
            return null;
        }
        return singleton;
    }

	public static boolean isLoadDefault() {
		return loadDefault;
	}

	/*
	 * Load application context from a set of files. Multiple files
	 * functionality is provided to help scenarios where the later context
	 * objects require objects from earlier contexts; a typical example is
	 * looger object could be defined in the first context file and could be
	 * used on other context files when the other objects are being initialized.
	 * This way you do not have to DI logger inside every object and could be
	 * used directly from Appctx utility class.
	 */
	public static void load(final String... files) {
		if (singleton == null) {
			singleton = new Appctx();
		}
		for (final String f : files) {
			if (singleton.appctx == null) {
				singleton.appctx = new ClassPathXmlApplicationContext(f);
			} else {
				singleton.appctx = new ClassPathXmlApplicationContext(
						new String[] { f }, singleton.appctx);
			}
		}
		try {
			final Map<String, String> res = singleton.get("CONTEXT_RESOURCES");
			if (res != null && res.get("RELOAD_DIR") != null) {
				final Executable r = new ExecutorHelper.Executable(res, files) {
					@Override
					public void run() {
						@SuppressWarnings("unchecked")
						final Map<String, String> res = (Map<String, String>) getArgs()[0];
						final String[] files = (String[]) getArgs()[1];
						final String dir = res.get("RELOAD_DIR");
						final String reload[] = res.get("RELOAD_RESOURCES")
								.split(",");
						final Long times[] = new Long[reload.length];
						for (int i = 0; i < reload.length; i++) {
							reload[i] = dir + "/" + reload[i].trim();
							times[i] = getTime(reload[i]);
						}
						final int freq = Integer.parseInt(res
								.get("RELOAD_CHECK_FREQUENCY"));
						for (;;) {
							try {
								Thread.sleep(freq * 1000);
								for (int i = 0; i < reload.length; i++) {
									final long nt = getTime(reload[i]);
									if (nt != times[i]) {
										times[i] = nt;
										Appctx.reset();
										Appctx.load(files);
										break;
									}
								}
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}
					}
				};
				ExecutorHelper.execute(r);
			}
		} catch (final Exception e) {

		}
	}

	public static void refresh() {
		if (singleton == null) {
			defLoad();
		} else {
			final AbstractRefreshableApplicationContext ctx = (AbstractRefreshableApplicationContext) singleton.appctx;
			ctx.refresh();
		}
	}

	public static void removeThreadMap() {
		threadMap.remove();
	}

	public static void reset() {
		singleton = null;
	}

	public static void setExecutorService(final ExecutorService executorService) {
		Appctx.executorService = executorService;
	}

	public static void setLoadDefault(final boolean loadDefault) {
		Appctx.loadDefault = loadDefault;
	}

	public static void setThreadCtx(final Appctx tCtx) {
		threadCtx.set(tCtx);
	}

	public static void setThreadMap(final String key, final Object obj) {
		Map<String, Object> m = threadMap.get();
		if (m == null) {
			m = new HashMap<String, Object>();
			threadMap.set(m);
		}
		m.put(key, obj);

	}

	private ApplicationContext appctx;

	/**
	 * This method will destroy all the loaded beans. It should only be called
	 * as application is terminating.
	 */
	public synchronized void destroy() {
		final Logger logger = getLogger(Appctx.class.getName());

		if (executorService != null) {
			executorService.shutdown();
		}
		if (appctx != null) {
			// Sadly, have to cast back to impl to access destroy method.
			((ClassPathXmlApplicationContext) appctx).destroy();
		}
		if (executorService != null) {
			final List<Runnable> stillRunning = executorService.shutdownNow();
			if (stillRunning != null && stillRunning.size() > 0) {
				logger.debug("Killed " + stillRunning.size() + " tasks.");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <R> R get(final String name) {
		if (appctx == null) {
			final Logger logger = getLogger(Appctx.class.getName());
			logger.error("Appctx not initialized getting bean " + name);
			return null;
		}
		return (R) appctx.getBean(name);
	}

	/**
	 * Appctx constructor
	 *
	 * @return Appctx
	 */
	public ApplicationContext getAppctx() {
		return appctx;
	}

	/**
	 * Set appctx; only used for testing and debugging
	 *
	 * @param appctx
	 */
	public void setAppctx(final ApplicationContext appctx) {
		this.appctx = appctx;
	}

	/**
	 * Setter to allow injection of config, rather than use non-DI lookup.
	 *
	 * @param newConfig
	 */
	public void setConfiguration(final Map<String, Object> newConfig) {
		if (config == null) {
			config = newConfig;
			System.out.println("INJECTED CONFIGURATION");
			SystemEnvUtils.expandMap(config);
		}
	}

}
