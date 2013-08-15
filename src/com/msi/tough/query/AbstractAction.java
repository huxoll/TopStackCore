package com.msi.tough.query;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.Constants;
import com.msi.tough.utils.MetricsUtil;
import com.yammer.metrics.core.Meter;

/**
 * Abstract class for Servlet actions
 *
 * @author raj
 *
 * @param <T>
 */
public abstract class AbstractAction<T> implements Action {
	private final static Logger logger = Appctx.getLogger(AbstractAction.class
			.getName());

	public static Map<String, Meter> initMeter(String group, String name) {
		return MetricsUtil.initMeter(group, name);
	}

	private String action;

	private String signatureVersion;
	private String signatureMethod;
	private String timestamp;
	private String awsAccessKeyId;
	private String signature;
	private Long accountId;
	private AccountBean accountBean;
	private String requestId;
	private HttpServletRequest request;
	private final Map<String, Object> otherData;
	private boolean useContextSession = false;

	public AbstractAction() {
		otherData = new HashMap<String, Object>();
	}

	protected String exceptionMessage(final Exception e) {
		return null;
	}

	public AccountBean getAccountBean() {
		return this.accountBean;
	}

	public Long getAccountId() {
		return this.accountId;
	}

	public String getAction() {
		return this.action;
	}

	public String getAwsAccessKeyId() {
		return this.awsAccessKeyId;
	}

	public String getHeader(final String header) {
		return request.getHeader(header);
	}

	public Map<String, Object> getOtherData() {
		return otherData;
	}

	public String getRequestId() {
		return this.requestId;
	}

	/**
	 * Obtain a session; either a new one (default), or one from context. The
	 * context session will be shared with other context aware beans, rather
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

	public String getSignature() {
		return this.signature;
	}

	public String getSignatureMethod() {
		return this.signatureMethod;
	}

	public String getSignatureVersion() {
		return this.signatureVersion;
	}

	public String getTimestamp() {
		return this.timestamp;
	}

	protected void mark(T ret, Exception e) {
		// do nothing
	}

	public void markStandard(Map<String, Meter> meters, Exception e) {
		MetricsUtil.markStandard(meters, e);
	}

	public abstract String marshall(final MarshallStruct<T> input,
			final HttpServletResponse resp) throws Exception;

	@Override
	public void process(final HttpServletRequest req,
			final HttpServletResponse resp) throws Exception {
		request = req;
		final Map<String, String[]> map = req.getParameterMap();
		logger.debug("requestmap " + map);
		this.requestId = UUID.randomUUID().toString();
		logger.debug("requestId " + this.requestId);
		resp.setHeader("x-amzn-RequestId", this.requestId);
		final Session s = getSession();
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			// validate request
			this.validate(s, req);
			final T ret = this.process0(s, req, resp, map);
			tx.commit();
			tx = null;
			final MarshallStruct<T> ms = new MarshallStruct<T>(ret,
					this.getRequestId());
			ms.setOtherData(otherData);
			if (map.get("ContentType") != null) {
				ms.setContentType(map.get("ContentType")[0]);
				resp.setContentType(map.get("ContentType")[0]);
			} else {
				ms.setContentType("xml");
				resp.setContentType("text/xml");
			}
			final String os = this.marshall(ms, resp);
			logger.debug("response " + os);
			resp.getOutputStream().write(os.replace("\n", "").getBytes());
			mark(ret, null);
		} catch (final ErrorResponse ae) {
			final String err = ae.getError(requestId);
			logger.error("ErrorResponse " + err);
			resp.setStatus(ae.getStatusCode());
			resp.getOutputStream().write(err.getBytes());
			if (tx != null) {
				tx.rollback();
				tx = null;
			}
			mark(null, ae);
		} catch (final Exception e) {
			e.printStackTrace();
			logger.error("Exception = " + e.getClass().getName() + " : "
					+ e.getMessage());
			final String msg = exceptionMessage(e);
			if (msg != null) {
				logger.error(msg);
			}
			final ErrorResponse internalFailure = ErrorResponse
					.InternalFailure();
			final String err = internalFailure.getError(requestId);
			resp.setStatus(internalFailure.getStatusCode());
			logger.error("ErrorResponse " + err);
			resp.getOutputStream().write(err.getBytes());
			if (tx != null) {
				tx.rollback();
				tx = null;
			}
			mark(null, e);
		} finally {
			try {
				s.close();
			} catch (final Exception e) {
			}
			Appctx.removeThreadMap();
		}
	}

	public abstract T process0(final Session session,
			final HttpServletRequest req, final HttpServletResponse resp,
			Map<String, String[]> map) throws Exception;

	public void setAccountBean(final AccountBean accountBean) {
		this.accountBean = accountBean;
	}

	public void setAccountId(final Long accountId) {
		this.accountId = accountId;
	}

	public void setAction(final String action) {
		this.action = action;
	}

	public void setAwsAccessKeyId(final String awsAccessKeyId) {
		this.awsAccessKeyId = awsAccessKeyId;
	}

	protected void setContextSession(boolean useContextSession) {
		this.useContextSession = useContextSession;
	}

	public void setRequestId(final String requestId) {
		this.requestId = requestId;
	}

	public void setSignature(final String signature) {
		this.signature = signature;
	}

	public void setSignatureMethod(final String signatureMethod) {
		this.signatureMethod = signatureMethod;
	}

	public void setSignatureVersion(final String signatureVersion) {
		this.signatureVersion = signatureVersion;
	}

	public void setTimestamp(final String timestamp) {
		this.timestamp = timestamp;
	}

	@SuppressWarnings("unchecked")
	protected void validate(final Session s, final HttpServletRequest req) {
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

		this.action = req.getParameter("Action");
		this.signatureVersion = req.getParameter("SignatureVersion");
		this.signatureMethod = req.getParameter("SignatureMethod");
		this.timestamp = req.getParameter("Timestamp");
		this.awsAccessKeyId = req.getParameter("AWSAccessKeyId");
		this.signature = req.getParameter("Signature");

		if (this.awsAccessKeyId == null) {
			final String auth = req.getHeader("authorization");
			final String ts = "Credential=";
			final int iauth = auth != null? auth.indexOf(ts) : -1;
			if (iauth != -1) {
				final int iauth2 = auth.indexOf("/", iauth);
				awsAccessKeyId = auth.substring(iauth + ts.length(), iauth2);
			}
			final int sign = auth != null? auth.indexOf("Signature=") : -1;
			if (sign != -1) {
				signature = auth.substring(sign);
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
			accountBean = AccountUtil.readAccountApi(s, this.awsAccessKeyId);
		} else {
			accountBean = AccountUtil.readAccount(s, this.awsAccessKeyId);
		}

		if (this.accountBean == null) {
			throw new ErrorResponse(
					"Sender",
					"Incorrect or invalid data is supplied for the security token.",
					"InvalidSecurityToken");
		}
		this.accountId = this.accountBean.getId();
	}
}
