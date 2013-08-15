package com.msi.tough.engine.resource;

/**
 * Interface to be implemented by all Resources
 * 
 * @author raj
 * 
 */
public interface Resource {
	/**
	 * Get value for attributes defined by the AWS CF for this resource
	 * 
	 * @param key
	 *            attribute name
	 * @return attribute value
	 */
	public Object getAtt(String key);

	/**
	 * Ref value for the resource as defined by AWS CF documentation
	 * 
	 * @return reference value
	 */
	public Object ref();
}
