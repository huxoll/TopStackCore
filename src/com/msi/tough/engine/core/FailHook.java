package com.msi.tough.engine.core;

/**
 * Hook to be called by PostWait event in ASInternal
 * 
 * @author raj
 * 
 */
public interface FailHook {
	public void endFail(long acid, String stackId, String physicalId,
			String parameter);

	/**
	 * This method to called when creation of a stack fails
	 * 
	 * @param acid
	 *            ID of the account which owns the resource
	 * @param stackId
	 *            ID of the stack to which this resource belongs
	 * @param physicalkId
	 *            ID of the of the resource failed
	 * @param parameter
	 *            Parameters passed back, they are defined at the time of
	 *            resource creation
	 */
	public void startFail(long acid, String stackId, String physicalId,
			String parameter);
}
