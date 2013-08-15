package com.msi.tough.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.ErrorResponse;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.MetricsRegistry;

public class MetricsUtil {
	public static Logger logger = Appctx.getLogger(MetricsUtil.class.getName());

	private static Map<String, Meter> allMeters = new HashMap<String, Meter>();
	private static MetricsRegistry metricsRegistry = null;

	public static Map<String, Meter> getAllMeters() {
		return allMeters;
	}

	public static MetricsRegistry getMetricsRegistry() {
		return metricsRegistry;
	}

	public static Map<String, Meter> initMeter(String group, String name) {
		logger.debug("initMeter " + group + " " + name);
		if (metricsRegistry == null) {
			metricsRegistry = new MetricsRegistry();
			logger.debug("metricsRegistry initialized");
		}
		Map<String, Meter> meters = new HashMap<String, Meter>();
		Meter nmt = metricsRegistry.newMeter(new MetricName(group, "ok-sec",
				name), "ok", TimeUnit.SECONDS);
		meters.put("ok-sec", nmt);
		allMeters.put(group + ":" + name + ":" + "ok-sec", nmt);

		nmt = metricsRegistry.newMeter(new MetricName(group, "ok-min", name),
				"ok", TimeUnit.MINUTES);
		meters.put("ok-min", nmt);
		allMeters.put(group + ":" + name + ":" + "ok-min", nmt);

		nmt = metricsRegistry.newMeter(
				new MetricName(group, "error-sec", name), "error",
				TimeUnit.SECONDS);
		meters.put("error-sec", nmt);
		allMeters.put(group + ":" + name + ":" + "error-sec", nmt);

		nmt = metricsRegistry.newMeter(
				new MetricName(group, "error-min", name), "error",
				TimeUnit.MINUTES);
		meters.put("error-min", nmt);
		allMeters.put(group + ":" + name + ":" + "error-min", nmt);

		nmt = metricsRegistry.newMeter(new MetricName(group, "exception-sec",
				name), "exception", TimeUnit.SECONDS);
		meters.put("exception-sec", nmt);
		allMeters.put(group + ":" + name + ":" + "exception-sec", nmt);

		nmt = metricsRegistry.newMeter(new MetricName(group, "exception-min",
				name), "exception", TimeUnit.MINUTES);
		meters.put("exception-min", nmt);
		allMeters.put(group + ":" + name + ":" + "exception-min", nmt);
		return meters;
	}

	public static void markStandard(Map<String, Meter> meters, Exception e) {
		if (e == null) {
			meters.get("ok-sec").mark();
			meters.get("ok-min").mark();
		}
		if (e != null) {
			if (e instanceof ErrorResponse) {
				meters.get("error-sec").mark();
				meters.get("error-min").mark();
			} else {
				meters.get("exception-sec").mark();
				meters.get("exception-min").mark();
			}
		}
	}

	public static void setAllMeters(Map<String, Meter> allMeters) {
		MetricsUtil.allMeters = allMeters;
	}

	public static void setMetricsRegistry(MetricsRegistry metricsRegistry) {
		MetricsUtil.metricsRegistry = metricsRegistry;
	}

	public static void stopAllMeters() {
		logger.debug("stopAllMeters");
		for (Entry<String, Meter> i : allMeters.entrySet()) {
			i.getValue().stop();
		}
		if (metricsRegistry != null) {
			metricsRegistry.shutdown();
		}
	}

}
