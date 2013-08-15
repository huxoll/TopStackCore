package com.msi.tough.client;

import org.apache.commons.cli.Options;

public interface ClientHelper {
	public String getFooter();

	public String getHeader();

	public void setOptions(Options options);

}
