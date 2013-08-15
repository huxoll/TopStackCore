package com.msi.tough.instance;

import org.hibernate.Session;

import com.msi.tough.model.InstanceBean;
import com.msi.tough.model.LaunchConfigBean;

public interface CreateInstance {
	public InstanceBean createInstace(final Session s, LaunchConfigBean launch,
			String avzones);
}
