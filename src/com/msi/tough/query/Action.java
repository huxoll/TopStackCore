package com.msi.tough.query;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Action {
	public void process(final HttpServletRequest req,
			final HttpServletResponse resp) throws Exception;
}
