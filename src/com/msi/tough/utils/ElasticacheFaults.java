package com.msi.tough.utils;

import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;

public class ElasticacheFaults extends QueryFaults {
	public static ErrorResponse CacheClusterAlreadyExists() {
		return new ErrorResponse("Sender",
				"User already has a Cache Cluster with the given identifier.",
				"CacheClusterAlreadyExists");
	}

	public static ErrorResponse CacheClusterNotFound() {
		return new ErrorResponse("Sender",
				"CacheClusterId does not refer to an existing Cache Cluster.",
				"CacheClusterNotFound", 404);
	}

	public static ErrorResponse CacheParameterGroupAlreadyExists() {
		return new ErrorResponse(
				"Sender",
				"A Cache Parameter Group with the name specified in CacheParameterGroupName already exists.",
				"CacheParameterGroupAlreadyExists");
	}

	public static ErrorResponse CacheParameterGroupNotFound() {
		return new ErrorResponse(
				"Sender",
				"CacheParameterGroupName does not refer to an existing Cache Parameter Group.",
				"CacheParameterGroupNotFound", 404);
	}

	public static ErrorResponse CacheParameterGroupQuotaExceeded() {
		return new ErrorResponse(
				"Sender",
				"Request would result in user exceeding the allowed number of Cache Parameter Groups.",
				"CacheParameterGroupQuotaExceeded");
	}

	public static ErrorResponse CacheSecurityGroupAlreadyExists() {
		return new ErrorResponse(
				"Sender",
				"A Cache Security Group with the name specified in CacheSecurityGroupName already exists.",
				"CacheSecurityGroupAlreadyExists", 400);
	}

	public static ErrorResponse CacheSecurityGroupNotFound() {
		return new ErrorResponse(
				"Sender",
				"CacheSecurityGroupName does not refer to an existing Cache Security Group.",
				"CacheSecurityGroupNotFound", 404);
	}

	public static ErrorResponse ClusterQuotaForCustomerExceeded() {
		return new ErrorResponse(
				"Sender",
				"Request would result in user exceeding the allowed number of Cache Clusters per customer.",
				"ClusterQuotaForCustomerExceeded");
	}

	public static ErrorResponse InsufficientCacheClusterCapacity() {
		return new ErrorResponse(
				"Sender",
				"Specified Cache node type is not available in the specified Availability Zone.",
				"InsufficientCacheClusterCapacity");
	}

	public static ErrorResponse InvalidCacheClusterState() {
		return new ErrorResponse("Sender",
				"The specified Cache Cluster is not in the available state.",
				"InvalidCacheClusterState", 400);
	}

	public static ErrorResponse InvalidCacheParameterGroupState() {
		return new ErrorResponse(
				"Sender",
				"The state of the Cache Parameter Group does not allow for the requested action to occur.",
				"InvalidCacheParameterGroupState");
	}

	public static ErrorResponse InvalidCacheSecurityGroupState() {
		return new ErrorResponse(
				"Sender",
				"The state of the Cache Security Group does not allow deletion.",
				"InvalidCacheSecurityGroupState", 400);
	}

	public static ErrorResponse NodeQuotaForClusterExceeded() {
		return new ErrorResponse(
				"Sender",
				"Request would result in user exceeding the allowed number of Cache Nodes in a single Cache Cluster.",
				"NodeQuotaForClusterExceeded");
	}

	// AWS Standard ?

	public static ErrorResponse NodeQuotaForCustomerExceeded() {
		return new ErrorResponse(
				"Sender",
				"Request would result in user exceeding the allowed number of Cache Nodes per customer.",
				"NodeQuotaForCustomerExceeded");
	}

	public static ErrorResponse ResourceInUse(final String requestId,
			final String message) {
		return new ErrorResponse("Sender", message, "ResourceInUse", 400);
	}

}
