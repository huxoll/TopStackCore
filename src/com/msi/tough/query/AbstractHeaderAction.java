package com.msi.tough.query;

import java.util.Enumeration;
import java.util.List;
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

public abstract class AbstractHeaderAction<T> implements Action {
	private final static Logger logger = Appctx
			.getLogger(AbstractHeaderAction.class.getName());

	private final int successStatus = 200;
	private String signatureMethod;
	private String signedHeader;
	private String awsAccessKeyId;
	private String signature;

	private Long accountId;
	private AccountBean accountBean;
	private String requestId;
	private HttpServletRequest request;

	protected String exceptionMessage(final Exception e) {
		return null;
	}

	public AccountBean getAccountBean() {
		return this.accountBean;
	}

	public Long getAccountId() {
		return this.accountId;
	}

	public String getAwsAccessKeyId() {
		return this.awsAccessKeyId;
	}

	public String getHeader(final String header) {
		return request.getHeader(header);
	}

	public String getLocationHeader() {
		return null;
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

	public String getSignedHeader() {
		return this.signedHeader;
	}

	public int getSuccessStatus() {
		return this.successStatus;
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
		if (this.getLocationHeader() != null) {
			resp.setHeader("Location", this.getLocationHeader());
		}
		final Session s = HibernateUtil.newSession();
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
			if (map.get("ContentType") != null) {
				ms.setContentType(map.get("ContentType")[0]);
				resp.setContentType(map.get("ContentType")[0]);
			} else {
				ms.setContentType("xml");
				resp.setContentType("text/xml");
			}
			final String os = Constants.XML_VERSION + this.marshall(ms, resp);
			logger.debug("response: \n" + os);
			resp.setStatus(this.getSuccessStatus());
			resp.getOutputStream().write(os.replace("\n", "").getBytes());
		} catch (final ErrorResponse ae) {
			final String err = ae.getError(requestId);
			logger.error("ErrorResponse: \n" + err);
			resp.setStatus(ae.getStatusCode());
			resp.getOutputStream().write(err.getBytes());
			if (tx != null) {
			    tx.rollback();
			    tx = null;
			}
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
			final String err = internalFailure.toString();
			resp.setStatus(internalFailure.getStatusCode());
			resp.getOutputStream().write(err.getBytes());
			if (tx != null) {
			    tx.rollback();
			    tx = null;
			}
		} finally {
			try {
				s.close();
			} catch (final Exception e) {
			}
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

	public void setSignedHeader(final String signedHeader) {
		this.signedHeader = signedHeader;
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

		if (this.awsAccessKeyId == null) {
			final String auth = req.getHeader("authorization");
			if(auth != null){
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
		}

		String header = req.getHeader("X-Amzn-Authorization");
		logger.debug("HTTP X-Amzn-Authorization: " + header);

		int trimPosition = -1;

		if(header.subSequence(0, 5).equals("AWS3 ")){
			trimPosition = 5;
		}else if(header.subSequence(0, 10).equals("AWS3-HTTP ")){
			trimPosition = 10;
		}
		else if(trimPosition == -1){
			trimPosition = 11;
		}
		logger.debug("First word in header: " + header.substring(0, trimPosition));

		header = header != null ? header.substring(trimPosition, header.length()) : null;
		logger.debug("HTTP X-Amzn-Authorization after trim: " + header);

		final CommaObject co = new CommaObject(header);
		final List<String> secTokens = co.getList();
		final String awsAccessKeyIdToken = "AWSAccessKeyId=";
		final String algorithmToken = "Algorithm=";
		final String signatureToken = "Signature=";
		final String signedHeadersToken = "SignedHeaders=";

		for (final String token : secTokens) {
			logger.debug("Current token to process: " + token);
			if (token.substring(0, awsAccessKeyIdToken.length()).equals(
					awsAccessKeyIdToken)) {
				logger.debug("AWS_ACCESS_KEY: "
						+ token.substring(awsAccessKeyIdToken.length(),
								token.length()));
				this.awsAccessKeyId = token.substring(
						awsAccessKeyIdToken.length(), token.length());
			}
			if (token.substring(0, algorithmToken.length()).equals(
					algorithmToken)) {
				logger.debug("ALGORITHM: "
						+ token.substring(algorithmToken.length(),
								token.length()));
				this.signatureMethod = token.substring(algorithmToken.length(),
						token.length());
			}
			if (token.substring(0, signatureToken.length()).equals(
					signatureToken)) {
				logger.debug("SIGNATURE: "
						+ token.substring(signatureToken.length(),
								token.length()));
				this.signature = token.substring(signatureToken.length(),
						token.length());
			}
			if (token.substring(0, signedHeadersToken.length()).equals(
					signedHeadersToken)) {
				logger.debug("SIGNED_HEADER: "
						+ token.substring(signedHeadersToken.length(),
								token.length()));
				this.signedHeader = token.substring(
						signedHeadersToken.length(), token.length());
			}
		}
		logger.debug(this.awsAccessKeyId + "|" + this.signatureMethod + "|"
				+ this.signature + "|" + this.signedHeader);
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
