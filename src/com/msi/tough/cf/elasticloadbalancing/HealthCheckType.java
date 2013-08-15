package com.msi.tough.cf.elasticloadbalancing;

import java.util.Map;

import com.msi.tough.cf.CFType;

public class HealthCheckType extends CFType {
	private String healthyThreshold;
	private String interval;
	private String target;
	private String timeout;
	private String unhealthyThreshold;

	public String getHealthyThreshold() {
		return healthyThreshold;
	}

	public String getInterval() {
		return interval;
	}

	public String getTarget() {
		return target;
	}

	public String getTimeout() {
		return timeout;
	}

	public String getUnhealthyThreshold() {
		return unhealthyThreshold;
	}

	public void setHealthyThreshold(final String healthyThreshold) {
		this.healthyThreshold = healthyThreshold;
	}

	public void setInterval(final String interval) {
		this.interval = interval;
	}

	public void setTarget(final String target) {
		this.target = target;
	}

	public void setTimeout(final String timeout) {
		this.timeout = timeout;
	}

	public void setUnhealthyThreshold(final String unhealthyThreshold) {
		this.unhealthyThreshold = unhealthyThreshold;
	}

	@Override
	public Map<String, Object> toMap() throws Exception {
		final Map<String, Object> map = super.toMap();
		map.put("HealthyThreshold", healthyThreshold);
		map.put("Interval", interval);
		map.put("Target", target);
		map.put("Timeout", timeout);
		map.put("UnhealthyThreshold", unhealthyThreshold);
		return map;
	}

}
