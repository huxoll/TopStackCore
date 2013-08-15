package com.msi.tough.utils.rds;

import org.hibernate.Session;

import com.msi.tough.model.AccountBean;
import com.msi.tough.utils.rds.RDSUtilities.Quota;

public class QuotaEntity {
	public static boolean withinQuota(Session sess, Quota quotaSecgrp,
			AccountBean ac, int i) {
		return true;
	}

	public static boolean withinQuota(Session sess, Quota quotaAuthorization,
			long userID, int i) {
		return true;
	}
}
