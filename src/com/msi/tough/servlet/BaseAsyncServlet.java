/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2013
 * All Rights Reserved.
 */
package com.msi.tough.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.ExecutorHelper;
import com.msi.tough.query.AsyncServiceImpl;
import com.msi.tough.query.AsyncServiceImpl.ServiceResponseListener;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceResponse;
import com.msi.tough.utils.ServiceMetadataUtil;

/**
 * Servlet to receive aync requests.
 */
@WebServlet(
// servlet name
name = "async", loadOnStartup = 1,
// servlet url pattern
value = { "/async/" },
// async support needed
asyncSupported = true)
public class BaseAsyncServlet extends HttpServlet implements
        ServiceResponseListener {

    private final Logger logger = Appctx.getLogger(BaseAsyncServlet.class
            .getName());

    private static final long serialVersionUID = 1L;

    public static final int CALLBACK_TIMEOUT = 60000;

    private final Map<String, AsyncContext> contexts = new HashMap<String, AsyncContext>();

    private final Set<AsyncServiceImpl> registeredActions = new HashSet<AsyncServiceImpl>();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public BaseAsyncServlet() {
        super();
    }

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);

        Boolean dnsRegister = Appctx.getBean("dnsRegister");
        if (dnsRegister) {
            final String serviceName = Appctx.getBean("serviceName");
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    try {
                        ServiceMetadataUtil metadataUtil =
                                Appctx.getBean("serviceMetadataUtil");

                        metadataUtil.populateServiceMetadata(config,
                                serviceName);
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            ExecutorHelper.execute(r);
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // create the async context, otherwise getAsyncContext() will be null
        final AsyncContext ctx = request.startAsync();

        // set the timeout
        ctx.setTimeout(CALLBACK_TIMEOUT);

        // attach listener to respond to lifecycle events of this AsyncContext
        ctx.addListener(new BackendAsyncListener());

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setParameterMap(request.getParameterMap());
        for (Enumeration<String> e = request.getHeaderNames(); e
                .hasMoreElements();) {
            String name = e.nextElement();
            serviceRequest.addHeader(name, request.getHeader(name));
        }
        ServiceResponse serviceResponse = new ServiceResponse(null,
                serviceRequest.getRequestId());
        // spawn a task to be run
        contexts.put(serviceRequest.getRequestId(), ctx);
        enqueLongRunningTask(serviceRequest, serviceResponse, ctx);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Send the request off for processing.
     * <p/>
     * if the {@link AsyncContext#getResponse()} is null, that means this
     * context has already timed-out (and context listener has been invoked).
     */
    protected void enqueLongRunningTask(final ServiceRequest request,
            ServiceResponse response, final AsyncContext ctx) {
        try {
            final AsyncServiceImpl impl = Appctx.getBean("myservice");
            impl.process(request, response);
        } catch (final ErrorResponse errorResponse) {
            errorResponse.setRequestId(request.getRequestId());
            handleError(errorResponse);
        } catch (final Exception e) {
            e.printStackTrace();
            ErrorResponse error = ErrorResponse.InternalFailure();
            error.setRequestId(request.getRequestId());
        }
    }

    protected void register(AsyncServiceImpl serviceImpl) {
        if (registeredActions.contains(serviceImpl)) {
            return;
        }
        serviceImpl.addResponseListener(this);
        registeredActions.add(serviceImpl);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AsyncServiceImpl.ServiceResponseListener#handleError
     * (com.msi.tough.query.ErrorResponse)
     */
    @Override
    public void handleError(ErrorResponse error) {
        String requestId = error.getRequestId();
        AsyncContext ctx = contexts.get(requestId);
        if (ctx == null) {
            logger.warn("No context found waiting for request: " + requestId);
            return;
        }
        ServletResponse response = ctx.getResponse();
        if (response != null) {
            try {
                final String errMsg = error.getError(requestId);
                logger.error("ErrorResponse " + errMsg);
                if (response instanceof HttpServletResponse) {
                    ((HttpServletResponse) response).setStatus(error
                            .getStatusCode());
                }
                response.getWriter().write(errMsg);
            } catch (IOException e) {
                logger.warn("Failed to write response to client.");
            }
            ctx.complete();
        } else {
            logger.warn("Response object from context is null, request timed out.");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.transcend.compute.servlet.AsyncServiceImpl.ServiceResponseListener
     * #handleResponse(com.msi.tough.query.ServiceResponse)
     */
    @Override
    public void handleResponse(ServiceResponse response) {
        AsyncContext ctx = contexts.get(response.getRequestId());
        if (ctx == null) {
            logger.warn("No context found waiting for request: "
                    + response.getRequestId());
            return;
        }
        generateResponse(ctx, response);
    }

    protected void generateResponse(final AsyncContext ctx,
            ServiceResponse serviceResponse) {
        Logger logger = Appctx.getLogger(getClass().getName());

        ServletResponse response = ctx.getResponse();
        if (response != null) {
            try {
                if (serviceResponse.getPayload() != null) {
                    response.getWriter().write(
                            serviceResponse.getPayload().toString());
                } else {
                    logger.warn("No payload returned for: "
                            + serviceResponse.getRequestId());
                    response.getWriter().write("OK");
                }
            } catch (IOException e) {
                logger.warn("Failed to write response to client.");
            }
            ctx.complete();
        } else {
            logger.warn("Response object from context is null, request timed out.");
        }
    }

    private class BackendAsyncListener implements AsyncListener {

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.AsyncListener#onComplete(javax.servlet.AsyncEvent)
         */
        @Override
        public void onComplete(AsyncEvent event) throws IOException {
            // Nothing to do, just completed.
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.AsyncListener#onError(javax.servlet.AsyncEvent)
         */
        @Override
        public void onError(AsyncEvent event) throws IOException {
            logger.warn("onError called");
            logger.warn(event.toString());
            event.getAsyncContext().getResponse().getWriter().write("ERROR");
            event.getAsyncContext().complete();
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * javax.servlet.AsyncListener#onStartAsync(javax.servlet.AsyncEvent)
         */
        @Override
        public void onStartAsync(AsyncEvent arg0) throws IOException {
            // Nothing to do, just started.
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.AsyncListener#onTimeout(javax.servlet.AsyncEvent)
         */
        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
            logger.warn("onTimeout called");
            logger.warn(event.toString());
            event.getAsyncContext().getResponse().getWriter().write("TIMEOUT");
            event.getAsyncContext().complete();
        }

    }
}
