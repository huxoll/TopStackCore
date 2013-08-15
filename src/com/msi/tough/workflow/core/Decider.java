package com.msi.tough.workflow.core;

import java.util.Collection;

public interface Decider {
	public Collection<Activity> decide(Collection<DecisionTask> decisions);
}
