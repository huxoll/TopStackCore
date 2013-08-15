package com.msi.tough.engine.core;

import com.msi.tough.cf.CFType;
import com.msi.tough.engine.resource.Resource;

/**
 * A provider implements resource creation, update and deletion logic.
 * 
 * @author raj
 * 
 */
public interface Provider {

	public CFType create(CallStruct call) throws Exception;

	public Resource delete(CallStruct call) throws Exception;

	public CFType update(CallStruct call) throws Exception;

}
