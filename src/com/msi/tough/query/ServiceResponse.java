/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.query;

import java.util.Map;

/**
 * Generic service response for TopStack services.
 *
 * Typically corresponds to a HTTP response content and meta-data.
 *
 * @author jgardner
 *
 */
public class ServiceResponse {

    private Map<String, String[]> responseMap;

    private Object payload;

    /** Content type of response payload. */
    private String contentType;

    /** Request ID to which this is a response. */
    private final String requestId;

    public ServiceResponse(final Object payload, final String requestId) {
        this.payload = payload;
        this.requestId = requestId;
        contentType = "xml";
    }

    /**
     * @return the payload
     */
    public Object getPayload() {
        return payload;
    }

    /**
     * @param payload the payload to set
     */
    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public String getContentType() {
        return contentType;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }
    /**
     * @return the responseMap
     */
    public Map<String, String[]> getResponseMap() {
        return responseMap;
    }

    /**
     * @param responseMap the responseMap to set
     */
    public void setResponseMap(Map<String, String[]> responseMap) {
        this.responseMap = responseMap;
    }
}
