package com.msi.tough.core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;

public class HttpUtils {
	private static Logger logger = Appctx.getLogger(HttpUtils.class.getName());

	/**
	 * Pipes everything from the reader to the writer via a buffer
	 *
	 * @return
	 */
	private final void pipe(final Reader reader, final Writer writer)
			throws IOException {
		final char[] buf = new char[1024];
		int read = 0;
		while ((read = reader.read(buf)) >= 0) {
			writer.write(buf, 0, read);
		}
		writer.flush();
	}

	/**
	 * Reads data from the data reader and posts it to a server via POST
	 * request. data - The data you want to send endpoint - The server's address
	 * output - writes the server's response to output
	 *
	 * @throws Exception
	 */
	public final void postData(final Reader data, final URL endpoint,
			final Writer output) throws Exception {
		HttpURLConnection urlc = null;
		try {
			urlc = (HttpURLConnection) endpoint.openConnection();
			try {
				urlc.setRequestMethod("POST");
			} catch (final ProtocolException e) {
				throw new Exception(
						"Shouldn't happen: HttpURLConnection doesn't support POST??",
						e);
			}
			urlc.setDoOutput(true);
			urlc.setDoInput(true);
			urlc.setUseCaches(false);
			urlc.setAllowUserInteraction(false);
			urlc.setRequestProperty("Content-type", "text/xml; charset="
					+ "UTF-8");

			final OutputStream out = urlc.getOutputStream();

			try {
				final Writer writer = new OutputStreamWriter(out, "UTF-8");
				pipe(data, writer);
				writer.close();
			} catch (final IOException e) {
				throw new Exception("IOException while posting data", e);
			} finally {
				if (out != null) {
					out.close();
				}
			}

			final InputStream in = urlc.getInputStream();
			try {
				final Reader reader = new InputStreamReader(in);
				pipe(reader, output);
				reader.close();
			} catch (final Exception e) {
				throw new Exception("IOException while reading response", e);
			} finally {
				if (in != null) {
					in.close();
				}
			}

		} catch (final Exception e) {
			throw new Exception("Connection error (is server running at "
					+ endpoint + " ?): " + e);
		} finally {
			if (urlc != null) {
				urlc.disconnect();
			}
		}
	}

	public final String sendGetRequest(final String endpoint,
			final String requestParameters) {
		String result = null;

		String urlStr = endpoint;
		if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
			// Send a GET request to the servlet
			try {

				// Send data
				if (requestParameters != null && requestParameters.length() > 0) {
					urlStr += "?" + requestParameters;
				}

				final URL url = new URL(urlStr);
				final URLConnection conn = url.openConnection();

				// Get the response
				final BufferedReader rd = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));
				final StringBuffer sb = new StringBuffer();
				String line;
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				rd.close();
				result = sb.toString();
			} catch (final Exception e) {
			    logger.error("Failed to issue GET request to:"+urlStr, e);
			}
		}
		return result;
	}

    public final String sendGetRequest(final String endpoint,
            final Map<String, String> requestParameters) {
        return sendGetRequest(endpoint, generateParams(requestParameters));
    }
    
    public final String sendPostRequest(final String endpoint, final Map<String, String> requestParameters) {
        final String paramData = generateParams(requestParameters);
        String result = "";
        try{
            final URL url = new URL(endpoint);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", "" + Integer.toString(paramData.getBytes().length));
            
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(paramData);
            wr.flush();
            wr.close();
            // Get the response
            final BufferedReader rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            final StringBuffer sb = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            result = sb.toString();
            conn.disconnect();
        } catch (final Exception e) {
            logger.error("Failed to issue POST request to:" + endpoint, e);
        }
        return result;
        
    }
    public final String generateParams(final Map<String, String> requestParameters){
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        for (final Entry<String, String> entry : requestParameters.entrySet()) {
            if (i++ != 0) {
                sb.append('&');
            }
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(String.valueOf(entry.getValue()));
        }
        return sb.toString();
    }
}
