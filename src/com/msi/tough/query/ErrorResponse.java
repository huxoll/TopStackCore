package com.msi.tough.query;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.BaseException;
import com.msi.tough.message.CoreMessage.ErrorResult;

public class ErrorResponse extends BaseException {
	/**
	 *
	 */
	private static final long serialVersionUID = -567814666321583992L;

	public static final String TYPE_SENDER = "Sender";

	public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_INTERNAL_ERROR = 500;
    public static String xmlns;

    public static final String CODE_INVALID_DATA = "InvalidData";
    public static final String CODE_INVALID_PARAMETER_VALUE = "InvalidParameterValue";
    public static final String CODE_MISSING_PARAMETER = "MissingParameter";
    public static final String CODE_RESOURCE_NOT_FOUND = "ResourceNotFound";
    public static final String CODE_INTERNAL_FAILURE = "InternalFailure";
    public static final String CODE_INVALID_NEXT_TOKEN = "CODE_INVALID_NEXT_TOKEN";

	public static ErrorResponse invlidData(final String msg) {
		return new ErrorResponse(TYPE_SENDER, msg, CODE_INVALID_DATA);
	}

	public static ErrorResponse missingParameter() {
		return new ErrorResponse(
				TYPE_SENDER,
				"An input parameter that is mandatory for processing the request is not supplied.",
				CODE_MISSING_PARAMETER);
	}

	public static ErrorResponse InternalFailure(){
		return new ErrorResponse(TYPE_SENDER, "The request processing has failed due to some unknown error, exception or failure.",
		CODE_INTERNAL_FAILURE,
		STATUS_INTERNAL_ERROR);
	}

    public static ErrorResponse invalidNextToken() {
        return new ErrorResponse(ErrorResponse.TYPE_SENDER,
                CODE_INVALID_NEXT_TOKEN,
                "The NextToken value is invalid.");
    }

    public static ErrorResponse notFound() {
        return new ErrorResponse(ErrorResponse.TYPE_SENDER,
                CODE_RESOURCE_NOT_FOUND,
                "The named resource does not exist.",
                STATUS_NOT_FOUND);
    }

    public static ErrorResponse invalidParameterValue(String msg) {
        return new ErrorResponse(ErrorResponse.TYPE_SENDER,
                CODE_INVALID_PARAMETER_VALUE,
                msg != null? msg : "An invalid or out-of-range value was " +
                        "supplied for the input parameter.");
    }

    private String requestId;
	private final String type;

	private int statusCode = STATUS_BAD_REQUEST; // 400 as default

	public ErrorResponse(final String type, final String message,
			final String code) {
		super(message, code);
		this.type = type;
	}

	public ErrorResponse(final String type, final String message,
			final String code, final int statusCode) {
		this(type, message, code);
		this.statusCode = statusCode;
	}

	public ErrorResponse withXmlns(String xmlnsValue){
		xmlns = xmlnsValue;
		return this;
	}

	public String getError(final String requestId) {
		final XMLNode ner = new XMLNode("Response");
		if(xmlns != null){
			ner.addAttr("xmlns", xmlns);
		}
		final XMLNode errorList = QueryUtil
		        .addNode(ner, "Errors");
		final XMLNode ne = QueryUtil.addNode(errorList, "Error");
		QueryUtil.addNode(ne, "Type", type);
		QueryUtil.addNode(ne, "Message", getMessage());
		QueryUtil.addNode(ne, "Code", getCode());
		QueryUtil.addNode(ner, "RequestId", requestId);
		return ner.toString();
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getType() {
		return type;
	}

	public void setStatusCode(final int statusCode) {
		this.statusCode = statusCode;
	}

	public static String getXmlns() {
		return xmlns;
	}

	public static void setXmlns(String xmlns) {
		ErrorResponse.xmlns = xmlns;
	}

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Convenience method to build protobuf equivalent.
     * @return
     */
    public ErrorResult toErrorResult() {
        ErrorResult.Builder errorResult = ErrorResult.newBuilder();
        errorResult.setTypeId(true);
        errorResult.setRequestStatusEquivalent(getStatusCode());
        errorResult.setCallerAccessKey("Unknown");
        errorResult.setRequestId(getRequestId());
        errorResult.setErrorCode(getCode());
        errorResult.setErrorMessage(getMessage());
        errorResult.setErrorType(getType());
        return errorResult.build();
    }

}
