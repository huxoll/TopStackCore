package com.msi.tough.utils;

import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;

public class RDSQueryFaults extends QueryFaults {

	public static ErrorResponse AuthorizationAlreadyExists() {
		return new ErrorResponse("Sender",
				"The specified CIDRIP or EC2 security group is already "
						+ "authorized for the specified DB security group.",
				"AuthorizationAlreadyExists");
	}

	public static ErrorResponse AuthorizationQuotaExceeded() {
		return new ErrorResponse(
				"Sender",
				"Database security group authorization quota has been reached.",
				"AuthorizationQuotaExceeded");
	}

	public static ErrorResponse DBInstanceAlreadyExists() {
		return new ErrorResponse("Sender",
				"User already has a DB Instance with the given identifier.",
				"DBInstanceAlreadyExists");
	}

	public static ErrorResponse DBInstanceAlreadyExists(final String msg) {
		return new ErrorResponse("Sender", msg, "DBInstanceAlreadyExists");
	}

	public static ErrorResponse DBInstanceNotFound() {
		return new ErrorResponse(
				"Sender",
				"DBInstanceIdentifier does not refer to an existing DB Instance.",
				"DBInstanceNotFound");
	}

	public static ErrorResponse DBInstanceNotFound(final String msg) {
		return new ErrorResponse("Sender", msg, "DBInstanceNotFound");
	}

	public static ErrorResponse DBParameterGroupAlreadyExists() {
		return new ErrorResponse("Sender",
				"DB Parameter Group already exists.",
				"DBParameterGroupAlreadyExists");
	}

	public static ErrorResponse DBParameterGroupNotFound() {
		return new ErrorResponse(
				"Sender",
				"DBParameterGroupName does not refer to an existing DB Parameter Group.",
				"DBParameterGroupNotFound", 404);
	}

	public static ErrorResponse DBParameterGroupNotFound(final String msg) {
		return new ErrorResponse("Sender", msg, "DBParameterGroupNotFound");
	}

	public static ErrorResponse DBParameterGroupQuotaExceeded() {
		return new ErrorResponse(
				"Sender",
				"Request would result in user exceeding the allowed number of DB Parameter Groups.",
				"DBParameterGroupQuotaExceeded");
	}

	public static ErrorResponse DBSecurityGroupAlreadyExist() {
		return new ErrorResponse(
				"Sender",
				"A database security group with the name specified in DBSecurityGroupName already exists.",
				"DBSecurityGroupAlreadyExist");
	}

	public static ErrorResponse DBSecurityGroupNotFound() {
		return new ErrorResponse(
				"Sender",
				"DBSecurityGroupName does not refer to an existing DB Security Group.",
				"DBSecurityGroupNotFound", 404);
	}

	public static ErrorResponse DBSecurityGroupQuotaExceeded() {
		return new ErrorResponse(
				"Sender",
				"Request would result in user exceeding the allowed number of DB Security Groups.",
				"DBSecurityGroupQuotaExceeded");
	}

	public static ErrorResponse DBSnapshotAlreadyExists() {
		return new ErrorResponse(
				"Sender",
				"DBSnapshotIdentifier is already used by an existing snapshot.",
				"DBSnapshotAlreadyExists");
	}

	public static ErrorResponse DBSnapshotNotFound() {
		return new ErrorResponse(
				"Sender",
				"DBSnapshotIdentifier does not refer to an existing DB Snapshot.",
				"DBSnapshotNotFound", 404);
	}

	public static ErrorResponse DBSnapshotNotFound(final String msg) {
		return new ErrorResponse("Sender", msg, "DBSnapshotNotFound", 404);
	}

	public static ErrorResponse IncompleteSignature() {
		return new ErrorResponse("Sender",
				"The request signature does not conform to AWS standards.",
				"IncompleteSignature");
	}

	public static ErrorResponse InstanceQuotaExceeded() {
		return new ErrorResponse(
				"Sender",
				"Request would result in user exceeding the allowed number of DB Instances.",
				"InstanceQuotaExceeded");
	}

	public static ErrorResponse InsufficientDBInstanceCapacity() {
		return new ErrorResponse(
				"Sender",
				"Specified DB Instance class is not available in the specified Availability Zone.",
				"InsufficientDBInstanceCapacity");
	}

	public static ErrorResponse InternalFailure() {
		return internalFailure();
	}

	public static ErrorResponse InternalFailure(final String msg) {
		return new ErrorResponse("Sender", msg, "InternalFailure", 500);
	}

	public static ErrorResponse InvalidAction() {
		return new ErrorResponse("Sender",
				"The action or operation requested is invalid.",
				"InvalidAction");
	}

	public static ErrorResponse InvalidAction(final String msg) {
		return new ErrorResponse("Sender", msg, "InvalidAction");
	}

	public static ErrorResponse InvalidClientTokenId() {
		return new ErrorResponse(
				"Sender",
				"The X.509 certificate or AWS Access Key ID provided does not exist in our records.",
				"InvalidClientTokenId", 403);
	}

	public static ErrorResponse InvalidClientTokenId(final String msg) {
		return new ErrorResponse("Sender", msg, "InvalidClientTokenId", 403);
	}

	public static ErrorResponse InvalidDBInstanceState() {
		return new ErrorResponse("Sender",
				"The specified DB Instance is not in the available state.",
				"InvalidDBInstanceState");
	}

	public static ErrorResponse InvalidDBInstanceState(final String msg) {
		return new ErrorResponse("Sender", msg, "InvalidDBInstanceState");
	}

	public static ErrorResponse InvalidDBParameterGroupState() {
		return new ErrorResponse(
				"Sender",
				"The DB Parameter Group cannot be deleted because it is in use.",
				"InvalidDBParameterGroupState");
	}

	public static ErrorResponse InvalidDBParameterGroupState(final String msg) {
		return new ErrorResponse("Sender", msg, "InvalidDBParameterGroupState");
	}

	public static ErrorResponse InvalidDBSecurityGroupState() {
		return new ErrorResponse(
				"Sender",
				"The state of the DBSecurityGroup does not permit this request.",
				"InvalidDBSecurityGroupState");
	}

	public static Exception InvalidDBSnapshotState() {
		return new ErrorResponse("Sender",
				"The state of DBSnapshot is not available.",
				"InvalidDBSnapshotState");
	}

	public static Exception InvalidDBSnapshotState(final String msg) {
		return new ErrorResponse("Sender", msg, "InvalidDBSnapshotState");
	}

	public static ErrorResponse InvalidParameterValue() {
		return new ErrorResponse(
				"Sender",
				"A bad or out-of-range value was supplied for the input parameter.",
				"InvalidParameterValue");
	}

	public static ErrorResponse InvalidParameterValue(final String msg) {
		return new ErrorResponse("Sender", msg, "InvalidParameterValue");
	}

	public static ErrorResponse InvalidQueryParameter() {
		return new ErrorResponse(
				"Sender",
				"AWS query string is malformed, does not adhere to AWS standards.",
				"InvalidQueryParameter");
	}

	public static ErrorResponse MalformedQueryString() {
		return new ErrorResponse("Sender", "The query string is malformed.",
				"MalformedQueryString", 404);
	}

	public static ErrorResponse MissingAction() {
		return new ErrorResponse("Sender",
				"The request is missing an action or operation parameter.",
				"MissingAction");
	}

	public static ErrorResponse MissingAuthenticationToken() {
		return new ErrorResponse(
				"Sender",
				"Request must contain either a valid (registered) AWS Access Key ID or X.509 certificate.",
				"MissingAuthenticationToken", 403);
	}

	public static ErrorResponse OptInRequired() {
		return new ErrorResponse("Sender",
				"The AWS Access Key ID needs a subscription for the service.",
				"OptInRequired", 403);
	}

	public static ErrorResponse ProvisionedIopsNotAvailableInAZ() {
		return new ErrorResponse("Sender", "ProvisionedIopsNotAvailableInAZ.",
				"Provisioned IOPS not available in the specified Availability Zone.");
	}

	public static ErrorResponse RequestExpired() {
		return new ErrorResponse(
				"Sender",
				"Request is past expires date or the request date (either with 15 minute padding),"
						+ " or the request date occurs more than 15 minutes in the future.",
				"RequestExpired");
	}

	public static ErrorResponse ServiceUnavailable() {
		return new ErrorResponse(
				"Sender",
				"The request has failed due to a temporary failure of the server.",
				"ServiceUnavailable", 503);
	}

	public static ErrorResponse SnapshotQuotaExceeded() {
		return new ErrorResponse(
				"Sender",
				"Request would result in user exceeding the allowed number of DB Snapshots.",
				"SnapshotQuotaExceeded");
	}

	public static ErrorResponse StorageQuotaExceeded() {
		return new ErrorResponse("Sender",
				"Request would result in user exceeding the allowed amount of"
						+ " storage available across all DB Instances.",
				"StorageQuotaExceeded");
	}

	public static ErrorResponse Throttling() {
		return new ErrorResponse("Sender",
				"Request was denied due to request throttling.", "Throttling");
	}
}
