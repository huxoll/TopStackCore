package com.msi.tough.query;

public class QueryFaults {
	public static ErrorResponse AuthorizationNotFound() {
		return new ErrorResponse("Sender", "Authorization Not Found.",
				"AuthorizationNotFound");
	}

	public static ErrorResponse internalFailure() {
		return new ErrorResponse(
				"Provider",
				"The request processing has failed due to some unknown error, exception or failure.",
				"InternalFailure", 500);
	}

	public static ErrorResponse invalidAction() {
		return new ErrorResponse("Sender",
				"The action or operation requested is invalid.",
				"invalidAction");
	}

	public static ErrorResponse InvalidParameterCombination() {
		return new ErrorResponse(
				"Sender",
				"Parameters that must not be used together were used together.",
				"InvalidParameterCombination");
	}

	public static ErrorResponse InvalidParameterCombination(final String msg) {
		return new ErrorResponse("Sender", msg, "InvalidParameterCombination");
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

	public static ErrorResponse MissingParameter() {
		return new ErrorResponse(
				"Sender",
				"An input parameter that is mandatory for processing the request is not supplied.",
				"MissingParameter");
	}

	public static ErrorResponse MissingParameter(final String msg) {
		return new ErrorResponse("Sender", msg, "MissingParameter");
	}

	public static ErrorResponse notSupported() {
		return new ErrorResponse("Provider", "Operation not supported.",
				"NotSupported");
	}

	public static Exception quotaError(final String msg) {
		return new ErrorResponse("Sender", msg, "QuotaExceeded");
	}
}
