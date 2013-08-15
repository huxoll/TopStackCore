package com.msi.tough.engine.core;

public class RefNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3340874235736079885L;

	public RefNotFoundException(final String ref) {
		super("not found " + ref);
	}

}
