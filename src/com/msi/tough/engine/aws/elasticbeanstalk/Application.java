package com.msi.tough.engine.aws.elasticbeanstalk;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.cf.CFType;
import com.msi.tough.cf.elasticbeanstalk.ApplicationType;
import com.msi.tough.cf.elasticbeanstalk.ApplicationVersionType;
import com.msi.tough.cf.elasticbeanstalk.SourceBundleType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.ApplicationBean;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.model.VersionBean;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.EventUtil;

public class Application extends BaseProvider {
	private static Logger logger = Appctx
			.getLogger(Application.class.getName());

	public static String TYPE = "AWS::ElasticBeanstalk::Application";

	@SuppressWarnings("unchecked")
	@Override
	public CFType create0(final CallStruct call) throws Exception {
		logger.debug("Creating a new application...");
		return HibernateUtil.withNewSession(new Operation<ApplicationType>() {

			@Override
			public ApplicationType ex(final Session s, final Object... args)
					throws Exception {
				final ResourcesBean resBean = getResourceBean(s);
				final AccountBean ac = AccountUtil.readAccount(s, call.getAc()
						.getId());
				final String appName = call.getName();
				ApplicationBean ap = AccountUtil
						.readApplication(s, ac, appName);
				boolean newRec = false;
				final String desc = (String) call.getProperty("Description");
				if (ap == null) {
					ap = new ApplicationBean();
					ap.setCreatedTime(new Date());
					newRec = true;
				}
				ap.setName(call.getName());
				ap.setUpdatedTime(new Date());
				ap.setDesc(desc);
				s.save(ap);
				if (newRec) {
					AccountUtil.addApplication(ac, ap);
					logger.debug("Creating a new event for this CreateApplication action...");
					EventUtil.addEvent(s, ac.getId(),
							"Created new Application: " + call.getName(),
							"EBSApplication", new String[] { call.getName() });
				}

				final ApplicationType app = new ApplicationType();
				app.setAcId(call.getAc().getId());
				app.setCreatedTime(ap.getCreatedTime());
				app.setUpdatedTime(ap.getUpdatedTime());
				app.setName(call.getName());
				app.setStackId(call.getStackId());
				app.setDescription(desc);

				// code below is never used if rest API is used to call
				// CreateApplicationVersion
				// CF template uses this portion of code below to create
				// ApplicationVersion
				final List<ApplicationVersionType> verTypes = new ArrayList<ApplicationVersionType>();
				app.setApplicationVersions(verTypes);
				final List<Map<String, Object>> applicationVersions = (List<Map<String, Object>>) call
						.getProperty("ApplicationVersions");
				if (applicationVersions != null) {
					for (final Map<String, Object> vm : applicationVersions) {
						final String label = (String) vm.get("VersionLabel");
						VersionBean vb = AccountUtil.getVersion(s, ap, label);
						if (vb == null) {
							vb = new VersionBean();
							vb.setUserId(ac.getId());
							vb.setApplicationName(call.getName());
							vb.setVersion(label);
							vb.setDesc((String) vm.get("Description"));
							final Map<String, Object> sourceBundle = (Map<String, Object>) vm
									.get("SourceBundle");
							vb.setS3bucket((String) sourceBundle
									.get("S3Bucket"));
							vb.setS3key((String) sourceBundle.get("S3Key"));
							vb.setCreatedTime(new Date());
							vb.setUpdatedTime(new Date());
							s.save(vb);
							AccountUtil.addVersion(ap, vb);
							s.save(ap);
						}
						final ApplicationVersionType vtype = new ApplicationVersionType();
						verTypes.add(vtype);
						vtype.setCreatedTime(vb.getCreatedTime());
						vtype.setUpdatedTime(vb.getUpdatedTime());
						vtype.setName(label);
						vtype.setVersionLabel(label);
						final SourceBundleType sb = new SourceBundleType();
						sb.setS3Bucket(vb.getS3bucket());
						sb.setS3Key(vb.getS3key());
						vtype.setSourceBundle(sb);
					}
				}

				// app.setConfigurationTemplates(configurationTemplates);
				return app;
			}
		});
	}
}
