package com.msi.tough.workflow.core;

import java.util.Collection;

public interface ActivityWorker {
	public Object perform(Collection<ActivityTask> tasks);
}
