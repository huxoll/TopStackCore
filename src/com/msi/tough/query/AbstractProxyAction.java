package com.msi.tough.query;

import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.model.AccountBean;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.Constants;
import com.msi.tough.utils.MetricsUtil;
import com.yammer.metrics.core.Meter;

public abstract class AbstractProxyAction<T> implements Action {

	private final static Logger logger = Appctx
			.getLogger(AbstractProxyAction.class.getName());

	public static Map<String, Meter> initMeter(String group, String name) {
		return MetricsUtil.initMeter(group, name);
	}

	private String action;

	private String signatureVersion;
	private String signatureMethod;
	private String timestamp;
	private String awsAccessKeyId;
	private String signature;
	private String requestId;
	private AccountBean accountBean;

	public AccountBean getAccountBean() {
		return accountBean;
	}

	public String getAction() {
		return this.action;
	}

	public String getAwsAccessKeyId() {
		return this.awsAccessKeyId;
	}

	public String getRequestId() {
		return this.requestId;
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
		final Map<String, String[]> map = req.getParameterMap();
		logger.debug("requestmap " + map);
		this.requestId = UUID.randomUUID().toString();
		logger.debug("requestId " + this.requestId);
		resp.setHeader("x-amzn-RequestId", this.requestId);
		try {
			// valiate request
			this.validate(req);
			final T ret = this.process0(req, resp, map);
			final MarshallStruct<T> ms = new MarshallStruct<T>(ret,
					this.getRequestId());
			if (map.get("ContentType") != null) {
				ms.setContentType(map.get("ContentType")[0]);
			}
			final String os = this.marshall(ms, resp);
			logger.debug("response " + os);
			resp.getOutputStream().write(os.getBytes());
		} catch (final ErrorResponse ae) {
			final String err = ae.getError(requestId);
			logger.debug("ErrorResponse " + err);
			resp.setStatus(ae.getStatusCode());
			resp.getOutputStream().write(err.getBytes());
		} catch (final Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public abstract T process0(final HttpServletRequest req,
			final HttpServletResponse resp, Map<String, String[]> map)
			throws Exception;

	public void setAccountBean(AccountBean accountBean) {
		this.accountBean = accountBean;
	}

	public void setAction(final String action) {
		this.action = action;
	}

	public void setAwsAccessKeyId(final String awsAccessKeyId) {
		this.awsAccessKeyId = awsAccessKeyId;
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
	protected void validate(final HttpServletRequest req) {
		boolean failFlag = false;
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

		if (this.action == null) {
			logger.error("ERROR: No Action provided.");
			failFlag = true;
		}

		if (this.awsAccessKeyId == null) {
			final String auth = req.getHeader("authorization");
			final String ts = "Credential=";
			final int iauth = auth.indexOf(ts);
			if (iauth != -1) {
				final int iauth2 = auth.indexOf("/", iauth);
				awsAccessKeyId = auth.substring(iauth + ts.length(), iauth2);
			}
			final int sign = auth.indexOf("Signature=");
			if (sign != -1) {
				signature = auth.substring(sign);
			}
		}
		//
		// if (this.signatureVersion == null) {
		// logger.error("ERROR: No SignatureVertion provided.");
		// failFlag = true;
		// }
		//
		// if (this.signatureMethod == null) {
		// logger.error("ERROR: No SignatureMethod provided.");
		// failFlag = true;
		// }
		//
		// if (this.timestamp == null) {
		// logger.error("ERROR: No Timestamp provided.");
		// failFlag = true;
		// }

		if (this.awsAccessKeyId == null) {
			logger.error("ERROR: No AWSAccessKeyId provided.");
			failFlag = true;
		}

		if (this.signature == null) {
			logger.error("ERROR: No Signature provided.");
			failFlag = true;
		}

		final Map<String, Object> options = (Map<String, Object>) Appctx
				.getThreadMap(Constants.ENDPOINT_OPTIONS);

		if (failFlag) {
			throw new ErrorResponse(
					"Sender",
					"Incorrect or invalid data supplied for the security token.",
					"InvalidSecurityToken");
		}

		final AccountBean ac = HibernateUtil
				.withNewSession(new Operation<AccountBean>() {
					@Override
					public AccountBean ex(final Session session,
							final Object... args) throws Exception {
						boolean useApi = false;
						if (options != null) {
							final String opt = (String) options.get("AUTHN");
							if (opt != null && opt.equals("API")) {
								useApi = true;
							}
						}
						if (useApi) {
							return AccountUtil.readAccountApi(session,
									awsAccessKeyId);
						} else {
							return AccountUtil.readAccount(session,
									awsAccessKeyId);
						}
					}
				});
		if (ac == null) {
			throw QueryFaults.AuthorizationNotFound();
		}
		accountBean = ac;

		logger.debug("Validation of AWS required parameters complete.");
	}
}
