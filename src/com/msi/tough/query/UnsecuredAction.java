package com.msi.tough.query;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.utils.AccountUtil;

public abstract class UnsecuredAction implements Action {

	private static Logger logger = Appctx.getLogger(UnsecuredAction.class
			.getName());

	private String awsAccessKeyId;
	private Long accountId;
	private AccountBean accountBean;
	/** Set to true to turn down logging, for actions called very often .*/
	private boolean lessVerbose = false;
	private boolean useContextSession = false;
	private boolean managedTx = false;

	public AccountBean getAccountBean() {
		return accountBean;
	}

	public Long getAccountId() {
		return accountId;
	}

	public String getAwsAccessKeyId() {
		return awsAccessKeyId;
	}

	@Override
	public void process(final HttpServletRequest req,
			final HttpServletResponse resp) throws Exception {
		final Map<String, String[]> map = req.getParameterMap();
		final StringBuilder sb = new StringBuilder();
		for (final Entry<String, String[]> en : map.entrySet()) {
			sb.append(en.getKey()).append("=").append(en.getValue()[0]);
		}
		if (logger.isDebugEnabled() && ! lessVerbose) {
		    logger.debug("requestmap " + sb.toString());
		}

		Session s = null;
		try {
		    s = getSession();
		} catch (Exception e) {
		    logger.error("Persistence Error:" + getClass().getName() + " : "
		            + e.getMessage());
		    e.printStackTrace();
		    return;
		}
		Transaction tx = null;
		try {
			//Added to eliminate bad integration with Javascript code.
			resp.addHeader("Access-Control-Allow-Origin", "*");
			if (!managedTx) {
			    tx = s.beginTransaction();
			}
			validate(s, req);
			final String ret = process0(s, req, resp, map);
			if (!managedTx) {
			    tx.commit();
			    tx = null;
			}
			if ((logger.isDebugEnabled() && ! lessVerbose) || !"DONE".equals(ret)) {
			    logger.debug("response " + ret);
			}
			if (ret != null) {
				resp.getOutputStream().write(ret.getBytes());
			}
		} catch (final Exception e) {
			logger.debug("Exception@" + getClass().getName() +" "+
			        e.getClass().getName() + " : "
					+ e.getMessage());
			if (tx != null) {
			    tx.rollback();
			    tx = null;
			}
			e.printStackTrace();
			throw e;
		} finally {
			try {
			    if (!managedTx) {
			        s.close();
			    }
			} catch (final Exception e) {
			}
		}
	}

	public abstract String process0(final Session session,
			final HttpServletRequest req, final HttpServletResponse resp,
			Map<String, String[]> map) throws Exception;

	public void setAccountBean(final AccountBean accountBean) {
		this.accountBean = accountBean;
	}

	public void setAccountId(final Long accountId) {
		this.accountId = accountId;
	}

	public void setAwsAccessKeyId(final String awsAccessKeyId) {
		this.awsAccessKeyId = awsAccessKeyId;
	}

	protected void validate(final Session s, final HttpServletRequest req) {
		final Map<String, String[]> map = req.getParameterMap();
		for (final Map.Entry<String, String[]> en : map.entrySet()) {
		    if (! lessVerbose) {
		        logger.debug(en.getKey() + ":" + en.getValue()[0]);
		    }
		}
		awsAccessKeyId = req.getParameter("AWSAccessKeyId");
		if (awsAccessKeyId != null) {
			accountBean = AccountUtil.readAccount(s, awsAccessKeyId);
			if (accountBean != null) {
				accountId = accountBean.getId();
			}
		}
	}

    public boolean isLessVerbose() {
        return lessVerbose;
    }

    public void setLessVerbose(boolean lessVerbose) {
        this.lessVerbose = lessVerbose;
    }

    protected void setContextSession(boolean useContextSession) {
        this.useContextSession = useContextSession;
    }

    protected void setManagedTx(boolean managedTx) {
        this.managedTx = managedTx;
    }

    /**
     * Obtain a session; either a new one (default), or one from context.
     * The context session will be shared with other context aware beans, rather
     * than being only accessible when passed everywhere explicitly.
     *
     * @return Session ready for use.
     */
    protected Session getSession() {
        if (useContextSession) {
            return HibernateUtil.getSession();
        }
        final Session s = HibernateUtil.newSession();
        return s;
    }
}
