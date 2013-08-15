package com.msi.tough.cf.autoscaling;

import java.util.Map;

import com.msi.tough.cf.CFType;

public class ScalingPolicyType extends CFType {
	private String adjustmentType;
	private String autoScalingGroupName;
	private String cooldown;
	private String scalingAdjustment;

	public String getAdjustmentType() {
		return adjustmentType;
	}

	@Override
	public Object getAtt(final String key) {
		return super.getAtt(key);
	}

	public String getAutoScalingGroupName() {
		return autoScalingGroupName;
	}

	public String getCooldown() {
		return cooldown;
	}

	public String getScalingAdjustment() {
		return scalingAdjustment;
	}

	@Override
	public String ref() {
		return getName();
	}

	public void setAdjustmentType(final String adjustmentType) {
		this.adjustmentType = adjustmentType;
	}

	public void setAutoScalingGroupName(final String autoScalingGroupName) {
		this.autoScalingGroupName = autoScalingGroupName;
	}

	public void setCooldown(final String cooldown) {
		this.cooldown = cooldown;
	}

	public void setScalingAdjustment(final String scalingAdjustment) {
		this.scalingAdjustment = scalingAdjustment;
	}

	@Override
	public Map<String, Object> toMap() throws Exception {
		final Map<String, Object> map = super.toMap();
		return map;
	}

	@Override
	public String typeAsString() {
		return "AWS::AutoScaling::ScalingPolicy";
	}

}
