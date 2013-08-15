/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.query;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Utility class for making requests against AbstractAction-derived classes.
 *
 * @author jgardner
 *
 */
public class ActionRequest {
    final Map<String, String[]> parameterMap =
            new HashMap<String, String[]>();
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    public void put(String key, String value) {
        parameterMap.put(key, new String[] {value});
    }

    public void putList(String key, String[] values) {
        for (int i = 0; i < values.length; i++) {
            put(key + ".member."+(i+1), values[i]);
        }
    }

    public void put(String key, Integer value) {
        this.put(key, value.toString());
    }

    public void put(String key, Double value) {
        this.put(key, value.toString());
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public Map<String, String[]> getMap() {
        return parameterMap;
    }

    /**
     * Reset request, to allow reuse.
     */
    public void reset() {
        parameterMap.clear();
        request.clearAttributes();
        response = new MockHttpServletResponse();
    }

}
