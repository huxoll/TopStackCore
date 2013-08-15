/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.query;

import java.util.Enumeration;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.Constants;

/**
 * Helper class for various mix-in utilities that are used by many actions.
 *
 * @author jgardner
 *
 */
@Component
public class ActionHelper {
    private final static Logger logger = Appctx.getLogger(ActionHelper.class
            .getName());

    @SuppressWarnings("unchecked")
    @Transactional
    public void validate(final ServiceRequest req,
            ServiceRequestContext context,
            QueuedAction action) {

        Session session = action.getSession();
        final Map<String, String[]> map = req.getParameterMap();
        for (final Map.Entry<String, String[]> en : map.entrySet()) {
            logger.debug(en.getKey() + "=>" + en.getValue()[0]);
        }

        final Enumeration<String> en = req.getHeaderNames();
        while (en.hasMoreElements()) {
            final String el = en.nextElement();
            final Enumeration<String> eln = req.getHeaders(el);
            final CommaObject co = new CommaObject();
            co.setSeparator("|");
            while (eln.hasMoreElements()) {
                co.add(eln.nextElement());
            }
            logger.debug("HEADER " + el + "=>" + co.toString());
        }

        context.setAction(req.getParameter("Action"));
        context.setSignatureVersion(req.getParameter("SignatureVersion"));
        context.setSignatureMethod(req.getParameter("SignatureMethod"));
        context.setTimestamp(req.getParameter("Timestamp"));
        context.setAwsAccessKeyId(req.getParameter("AWSAccessKeyId"));
        context.setSignature(req.getParameter("Signature"));

        if (context.getAwsAccessKeyId() == null) {
            final String auth = req.getHeader("authorization");
            if (auth == null) {
                throw QueryFaults.AuthorizationNotFound();
            }
            final String ts = "Credential=";
            final int iauth = auth.indexOf(ts);
            if (iauth != -1) {
                final int iauth2 = auth.indexOf("/", iauth);
                context.setAwsAccessKeyId(auth.substring(iauth + ts.length(), iauth2));
            }
            final int sign = auth.indexOf("Signature=");
            if (sign != -1) {
                context.setSignature(auth.substring(sign));
            }
        }

        final Map<String, Object> options = (Map<String, Object>) Appctx
                .getThreadMap(Constants.ENDPOINT_OPTIONS);
        boolean useApi = false;
        if (options != null) {
            final String opt = (String) options.get("AUTHN");
            if (opt != null && opt.equals("API")) {
                useApi = true;
            }
        }
        if (useApi) {
            context.setAccountBean(AccountUtil.readAccountApi(session, context.getAwsAccessKeyId()));
        } else {
            context.setAccountBean(AccountUtil.readAccount(session, context.getAwsAccessKeyId()));
        }

        if (context.getAccountBean() == null) {
            throw new ErrorResponse(
                    "Sender",
                    "Incorrect or invalid data is supplied for the security token.",
                    "InvalidSecurityToken");
        }
        context.setAccountId(context.getAccountBean().getId());
    }
}
