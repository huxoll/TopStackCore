package com.msi.tough.dasein;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nonnull;

import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.ProviderContext;

/**
 * Loads a properly configured Dasein Cloud
 * {@link org.dasein.cloud.CloudProvider}. This class looks for the following
 * properties:
 * <ul>
 * <li>DSN_PROVIDER_CLASS</li>
 * <li>DSN_ENDPOINT</li>
 * <li>DSN_REGION</li>
 * <li>DSN_ACCOUNT</li>
 * <li>DSN_API_SHARED</li>
 * <li>DSN_API_SECRET</li>
 * <li>DSN_API_VERSION</li>
 * <li>DSN_CLOUD_NAME</li>
 * <li>DSN_CLOUD_PROVIDER</li>
 * </ul>
 * The only required values are DSN_PROVIDER_CLASS, DSN_ENDPOINT,
 * DSN_PROVIDER_REGION, DSN_API_SHARED, and DSN_API_SECRET. The DSN_API_SHARED
 * will be used for DSN_ACCOUNT if DSN_ACCOUNT is omitted. In addition, you may
 * specify implementation-specific properties using the system property
 * DSN_CUSTOM_prop_name. For example, the custom property "domain" would be
 * specified through DSN_CUSTOM_domain.
 */
public class ProviderLoader {

	public @Nonnull
	CloudProvider getProvider(final Map<String, String> prop)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, UnsupportedEncodingException {
		final String cname = prop.get("DSN_PROVIDER_CLASS");

		final CloudProvider configuredProvider = (CloudProvider) Class.forName(
				cname).newInstance();

		final String endpoint = prop.get("DSN_ENDPOINT");
		final String regionId = prop.get("DSN_REGION");
		final String account = prop.get("DSN_ACCOUNT");
		final String shared = prop.get("DSN_API_SHARED");
		final String secret = prop.get("DSN_API_SECRET");
		final String version = prop.get("DSN_API_VERSION");
		final String cloudName = prop.get("DSN_CLOUD_NAME");
		final String providerName = prop.get("DSN_CLOUD_PROVIDER");

		final ProviderContext ctx = new ProviderContext();

		ctx.setEndpoint(endpoint);
		if (providerName != null) {
			ctx.setProviderName(providerName);
		}
		if (cloudName != null) {
			ctx.setCloudName(cloudName);
		}
		if (regionId != null) {
			ctx.setRegionId(regionId);
		}
		ctx.setAccountNumber(account);
		ctx.setAccessKeys(shared.getBytes("utf-8"), secret.getBytes("utf-8"));

		final Properties properties = new Properties();

		if (version != null) {
			properties.setProperty("apiVersion", version);
		}
		for (final String p : System.getProperties().stringPropertyNames()) {
			if (p.startsWith("DSN_CUSTOM_")) {
				properties
						.put(p.substring("DSN_CUSTOM_".length()), prop.get(p));
			}
		}
		ctx.setCustomProperties(properties);
		configuredProvider.connect(ctx);
		return configuredProvider;
	}
}
