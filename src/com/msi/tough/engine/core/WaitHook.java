package com.msi.tough.engine.core;

import java.util.Map;

import org.hibernate.Session;

/**
 * Hook to be called by PostWait event in ASInternal
 * 
 * @author raj
 * 
 */
public interface WaitHook {
	/**
	 * This method to called by ASInternal
	 * 
	 * @param s
	 *            Database Session to use
	 * @param success
	 *            whether chef role implementation was success or failure
	 * @param acid
	 *            ID of the account which owns the resource
	 * @param stackId
	 *            ID of the stack to which this resource belongs
	 * @param parameter
	 *            Parameters passed back, they are defined at the time of
	 *            resource creation
	 */
	public void postWait(final Session s, boolean success, long acid,
			String stackId, String physicalId, String resourceData,
			Map<String, String[]> map);
}
