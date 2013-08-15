/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.query;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.protobuf.Message;
import com.msi.tough.model.AccountBean;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.workflow.core.AbstractWorker;

/**
 * Helper class for running action classes from tests, since there's web assets
 * that must be simulated.
 *
 * @author jgardner
 *
 */
@Component
public class ActionTestHelper {

	private static SessionFactory sessionFactory = null;

	@Resource(name = "accessKey")
	String accessKey = null;

	@Resource(name = "limitedQuotaAccessKey")
	String limitedQuotaAccessKey = null;

	@Resource(name = "limitedQuotaSecretKey")
	String limitedQuotaSecretKey = null;

	@Resource(name = "limitedQuotaZone")
	String limitedQuotaZone = null;

	@Resource(name = "limitedQuotaOwnerId")
	String limitedQuotaOwnerId = null;

	private AccountBean ensureLimitedQuotaAccount(String limitedQuotaAccessKey,
			String limitedQuotaSecretKey, String limitedQuotaOwner_id) {
		AccountBean ac = getAccountBeanByAccessKey(limitedQuotaAccessKey);
		if (ac != null) {
			return ac;
		}
		org.hibernate.classic.Session session = sessionFactory
				.getCurrentSession();
		ac = new AccountBean();
		ac.setAccessKey(limitedQuotaAccessKey);
		ac.setName(limitedQuotaAccessKey);
		ac.setApiUsername(limitedQuotaAccessKey);
		ac.setApiUsername(limitedQuotaSecretKey);
		ac.setDefZone(limitedQuotaZone);
		ac.setSecretKey(limitedQuotaSecretKey);
		ac.setTenant(limitedQuotaOwner_id);
		session.save(ac);
		return ac;
	}

	public AccountBean getAccountBean() {
		return getAccountBeanByAccessKey(accessKey);
	}

	@Transactional
	public AccountBean getAccountBeanByAccessKey(String accountAccessKey) {
		Session session = sessionFactory.getCurrentSession();
		AccountBean ac = AccountUtil.readAccount(session, accountAccessKey);
		if (ac == null) {
		    return null;
		}
		ac.getName();
		return ac;
	}

    @Transactional
    public <T extends Message, V extends Message> T
        invokeRequest(AbstractQueuedAction<T, V> action,
            final ActionRequest req) throws Exception {
        // simulate context set up by a real web request.
        Session session = sessionFactory.getCurrentSession();
        AccountBean account = AccountUtil.readAccount(session, accessKey);
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setParameterMap(req.getMap());
        ServiceRequestContext context = new ServiceRequestContext();
        context.setAccountId(account.getId());
        context.setAwsAccessKeyId(accessKey);
        context.setRequestId(serviceRequest.getRequestId());
        // Invoke the action request processing, return the request object.
        T ret = action.handleRequest(serviceRequest, context);
        ret = ProtobufUtil.setRequiredField(ret, "requestId",
                context.getRequestId());
        ret = ProtobufUtil.setRequiredField(ret, "callerAccessKey",
                context.getAwsAccessKeyId());
        ret = ProtobufUtil.setRequiredField(ret, "typeId", true);
        return ret;
    }

	@Transactional
	public <T> T invokeProcess(AbstractAction<T> action,
			final HttpServletRequest req, final HttpServletResponse resp,
			Map<String, String[]> map) throws Exception {

		Session session = sessionFactory.getCurrentSession();
		action.setAccountBean(getAccountBean());
		action.setAccountId(action.getAccountBean().getId());
		T ret = action.process0(session, req, resp, map);
		return ret;
	}

	@Transactional
	public String invokeProcess(UnsecuredAction action,
			final HttpServletRequest req, final HttpServletResponse resp,
			Map<String, String[]> map) throws Exception {

		Session session = sessionFactory.getCurrentSession();
		return action.process0(session, req, resp, map);
	}

	@Transactional
	public <T> T invokeProcessWithLimitedQuota(AbstractAction<T> action,
			final HttpServletRequest req, final HttpServletResponse resp,
			Map<String, String[]> map) throws Exception {

		AccountBean ac = ensureLimitedQuotaAccount(limitedQuotaAccessKey,
				limitedQuotaSecretKey, limitedQuotaOwnerId);
		if (ac != null) {
			Session session = sessionFactory.getCurrentSession();
			action.setAccountBean(getAccountBeanByAccessKey(limitedQuotaAccessKey));
			action.setAccountId(action.getAccountBean().getId());
			T ret = action.process0(session, req, resp, map);
			return ret;
		}
		return null;
	}

    @Transactional
    public <T extends Message, V extends Message> T
        invokeQueued(AbstractQueuedAction<T, V> action,
            final HttpServletRequest req, final HttpServletResponse resp,
            Map<String, String[]> map) throws Exception {

        //TODO: flesh this out for testing.
        return null;
    }

    @Transactional
    public <T extends Message, V extends Message> T
        invokeWorker(AbstractWorker<T, V> worker,
            final HttpServletRequest req, final HttpServletResponse resp,
            Map<String, String[]> map) throws Exception {

        //TODO: flesh this out for testing.
        return null;
    }
	@Autowired(required = true)
	public void setSessionFactory(SessionFactory sessionFactory) {
		ActionTestHelper.sessionFactory = sessionFactory;
	}

    public String getAccessKey() {
        return accessKey;
    }

    public String getLimitedQuotaAccessKey() {
        return limitedQuotaAccessKey;
    }

    public String getLimitedQuotaSecretKey() {
        return limitedQuotaSecretKey;
    }

    public String getLimitedQuotaZone() {
        return limitedQuotaZone;
    }

    public String getLimitedQuotaOwnerId() {
        return limitedQuotaOwnerId;
    }
}
