/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.query;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.msi.tough.core.CommaObject;

/**
 * Generic service request for TopStack services.
 *
 * Typically corresponds to a HTTP request body and parameters.
 *
 * @author jgardner
 *
 */
public class ServiceRequest {

    private String requestId;

    private Map<String, String[]> parameters;

    private Object payload;

    private final Map<String, Object> headers =
            new HashMap<String, Object>();

    /**
     *
     */
    public ServiceRequest() {
        super();
        this.requestId = UUID.randomUUID().toString();
    }

    /**
     * @return the parameterMap
     */
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    /**
     * @param parameterMap
     *            the parameterMap to set
     */
    public void setParameterMap(Map<String, String[]> parameterMap) {
        this.parameters = parameterMap;
    }

    /**
     * @return the payload
     */
    public Object getPayload() {
        return payload;
    }

    /**
     * @param payload
     *            the payload to set
     */
    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getParameter(String name) {
        String[] arr = (String[]) this.parameters.get(name);
        return (arr != null && arr.length > 0 ? arr[0] : null);
    }

    @SuppressWarnings("unchecked")
    public String getHeader(String name) {
        Object value = this.headers.get(name);
        if (value instanceof List) {
            return new CommaObject((List<String>) value).toString();
        }
        else if (value != null) {
            return value.toString();
        }
        else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Enumeration<String> getHeaders(String name) {
        Object value = this.headers.get(name);
        if (value instanceof List) {
            return Collections.enumeration((List<String>) value);
        }
        else if (value != null) {
            ArrayList<String> vector = new ArrayList<String>(1);
            vector.add(value.toString());
            return Collections.enumeration(vector);
        }
        else {
            return Collections.enumeration(Collections.EMPTY_SET);
        }
    }

    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(this.headers.keySet());
    }

    /**
     * Add a header entry for the given name.
     * <p>
     * If there was no entry for that header name before, the value will be used
     * as-is. In case of an existing entry, a String array will be created,
     * adding the given value (more specifically, its toString representation)
     * as further element.
     * <p>
     * Multiple values can only be stored as list of Strings, following the
     * Servlet spec (see <code>getHeaders</code> accessor). As alternative to
     * repeated <code>addHeader</code> calls for individual elements, you can
     * use a single call with an entire array or Collection of values as
     * parameter.
     *
     * @see #getHeaderNames
     * @see #getHeader
     * @see #getHeaders
     * @see #getDateHeader
     * @see #getIntHeader
     */
    public void addHeader(String name, Object value) {
        Object oldValue = this.headers.get(name);
        if (oldValue instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) oldValue;
            addHeaderValue(list, value);
        } else if (oldValue != null) {
            List<Object> list = new LinkedList<Object>();
            list.add(oldValue);
            addHeaderValue(list, value);
            this.headers.put(name, list);
        } else if (value instanceof Collection || value.getClass().isArray()) {
            List<Object> list = new LinkedList<Object>();
            addHeaderValue(list, value);
            this.headers.put(name, list);
        } else {
            this.headers.put(name, value);
        }
    }

    private void addHeaderValue(List<Object> list, Object value) {
        if (value instanceof Collection<?>) {
            Collection<?> valueColl = (Collection<?>) value;
            for (Iterator<?> it = valueColl.iterator(); it.hasNext();) {
                Object element = it.next();
                list.add(element.toString());
            }
        } else if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                Object element = Array.get(value, i);
                list.add(element.toString());
            }
        } else {
            list.add(value);
        }
    }
}
