/*
 * RequestIdHelper.java.java
 *
 * MSI Eucalyptus LoadBalancer Project
 * Copyright (C) Momentum SI
 *
 */

package com.msi.tough.core;

import java.util.UUID;

/**
 * @author raj
 * 
 */
public class RequestIdHelper {
	public static String getRequestId() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
}
