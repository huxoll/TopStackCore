package com.msi.tough.client;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.msi.tough.core.StringHelper;
 

public abstract class AbstractBase {
	private final Options options;
	private final ClientHelper clientHelper;
	protected boolean showHelp = false;
	protected boolean error = false;
	private final GnuParser parser;
	protected String msg = "";
	protected CommandLine cmd;
	protected boolean debug;
	private String accessKey;
	private String secretKey;
	private AWSCredentials credentials;
	private String endpoint;

	public AbstractBase(ClientHelper clientHelper) {
		this.clientHelper = clientHelper;
		options = new Options();
		parser = new GnuParser();
		globalOptions();
	}

	public String getAccessKey() {
		return accessKey;
	}

	public ClientHelper getClientHelper() {
		return clientHelper;
	}

	public CommandLine getCmd() {
		return cmd;
	}

	public AWSCredentials getCredentials() {
		return credentials;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public String getFooter() {
		return clientHelper.getFooter();
	}

	public String getHeader() {
		return clientHelper.getHeader();
	}

	public String getMsg() {
		return msg;
	}

	public String getSecretKey() {
		return secretKey;
	}

	protected abstract String getUrlEnvVar();

	@SuppressWarnings("static-access")
	protected void globalOptions() {
		final Option opt = new Option(
				null,
				"aws-credential-file",
				true,
				"Location of the file with your AWS credentials. This value can be set by using the environment variable 'AWS_CREDENTIAL_FILE'.");
		opt.setArgName("file");
		options.addOption(opt);

		options
				.addOption(OptionBuilder
						.hasArg()
						.withArgName("VALUE")
						.withLongOpt("url")
						.withDescription(
								"This option will override the URL for the service call with VALUE. This value can be set by using the environment variable '"
										+ getUrlEnvVar() + "'.").create("U"));

		options
				.addOption(OptionBuilder
						.hasArg()
						.withArgName("VALUE")
						.withLongOpt("access-key-id")
						.withDescription(
								"Specify VALUE as the AWS Access Id to use. This value can be set by using the environment variable 'EC2_ACCESS_KEY'.")
						.create("I"));

		options
				.addOption(OptionBuilder
						.hasArg()
						.withArgName("VALUE")
						.withLongOpt("secret-key")
						.withDescription(
								"Specify VALUE as the AWS Secret Key to use. This value can be set by using the environment variable 'EC2_SECRET_KEY'.")
						.create("S"));

		options.addOption(OptionBuilder.withLongOpt("debug").withDescription(
				"Debug Client").create());
	}

	protected void help(final Options options, final String msg) {
		final HelpFormatter hf = new HelpFormatter();
		hf.setWidth(100);
		System.out.println("error: " + msg);
		hf.printHelp(getHeader(), null, options, getFooter());
	}

	public final void process(final String[] args) {
		try {
			setOptions(options);

			cmd = parser.parse(options, args);

			// set debug
			if (cmd.hasOption("debug")) {
				debug = true;
			}
			if (debug) {
				System.out.println("INPUT ARGS "
						+ StringHelper.concat(args, " "));
			}
			accessKey = cmd.getOptionValue('I');
			if (accessKey == null) {
				accessKey = System.getenv("EC2_ACCESS_KEY");
			}
			secretKey = cmd.getOptionValue('S');
			if (secretKey == null) {
				secretKey = System.getenv("EC2_SECRET_KEY");
			}
			if (accessKey != null && secretKey != null) {
				credentials = new BasicAWSCredentials(getAccessKey(),
						getSecretKey());
			}
			process0();
		} catch (final ParseException e) {
			showHelp = true;
			msg = e.getMessage();
		} catch (final AxisFault e) {
			if (debug) {
				e.printStackTrace();
			}
			System.out.println(e.getFaultCode().getLocalPart() + ": "
					+ e.getMessage());
		} catch (final RemoteException e) {
			e.printStackTrace();
		} catch (final ArgException e) {
			showHelp = true;
			msg = e.getMessage();
		} catch (final Exception e) {
			e.printStackTrace();
			showHelp = true;
		}
		if (showHelp) {
			help(options, msg);
		}
	}

	protected abstract void process0() throws Exception;

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public void setCredentials(AWSCredentials credentials) {
		this.credentials = credentials;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setOptions(Options options) {
		clientHelper.setOptions(options);
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
}
// -C, --ec2-cert-file-path VALUE
// Location of the file with your EC2 X509 certificate. This value can be
// set by using the environment variable 'EC2_CERT'.
//
// --connection-timeout VALUE
// Specify a connection timeout VALUE (in seconds). The default value is
// '30'.
//
// --delimiter VALUE
// What delimiter to use when displaying delimited (long) results.
//
// --headers
// If you are displaying tabular or delimited results, it includes the
// column headers. If you are showing xml results, it returns the HTTP
// headers from the service request, if applicable. This is off by default.
//
// -K, --ec2-private-key-file-path VALUE
// Location of the file with your EC2 private key. This value can be set by
// using the environment variable 'EC2_PRIVATE_KEY'.
//
// --region VALUE
// Specify region VALUE as the web service region to use. This value can be
// set by using the environment variable 'EC2_REGION'.
//
// --show-empty-fields
// Show empty fields and rows, using a "(nil)" value. The default is to not
// show empty fields or columns.
//
// --show-request
// Displays the URL the tools used to call the AWS Service. The default
// value is 'false'.
//
// --show-table, --show-long, --show-xml, --quiet
// Specify how the results are displayed: tabular, delimited (long), xml, or
// no output (quiet). Tabular shows a subset of the data in fixed
// column-width form, while long shows all of the returned values delimited
// by a character. The xml is the raw return from the service, while quiet
// suppresses all standard output. The default is tabular, or 'show-table'.
