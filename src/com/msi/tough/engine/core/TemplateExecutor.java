package com.msi.tough.engine.core;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.ParameterResource;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.ExecutorHelper;
import com.msi.tough.core.ExecutorHelper.Executable;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.utils.CFUtil;

/**
 * Template execution helper class
 * 
 * @author raj
 * 
 */
public class TemplateExecutor {
	private static Logger logger = Appctx.getLogger(TemplateExecutor.class
			.getName());

	public Map<String, Object> create(final Session s, final AccountType ac,
			final String stackId, final String region,
			final TemplateContext inputs, final Template template)
			throws Exception {
		return createOrUpdate(s, ac, stackId, region, inputs, template, false);
	}

	/**
	 * run the script in a new thread
	 * 
	 * @param ac
	 * @param stackId
	 * @param region
	 * @param script
	 * @throws Exception
	 */
	public void createNewThread(final AccountType ac, final String stackId,
			final String region, final String script) throws Exception {
		final Template tlt = new Template(new ByteArrayInputStream(
				script.getBytes()));
		createNewThread(ac, stackId, region,
				new TemplateContext(tlt.getMappings()), tlt);
	}

	/**
	 * run the template in a new thread
	 * 
	 * @param ac
	 * @param stackId
	 * @param region
	 * @param inputs
	 * @param template
	 */
	public void createNewThread(final AccountType ac, final String stackId,
			final String region, final TemplateContext inputs,
			final Template template) {
		final Executable r = new ExecutorHelper.Executable(stackId, region,
				inputs, template) {
			@Override
			public void run() {
				final Object[] args = getArgs();
				int a = 0;
				final String stackId = (String) args[a++];
				final String region = (String) args[a++];
				final TemplateContext inputs = (TemplateContext) args[a++];
				final Template template = (Template) args[a++];
				try {
					HibernateUtil.withSession(
							new HibernateUtil.Operation<Object>() {
								@Override
								public Object ex(final Session session,
										final Object... args) throws Exception {
									int a = 0;
									final String stackId = (String) args[a++];
									final String region = (String) args[a++];
									final TemplateContext inputs = (TemplateContext) args[a++];
									final Template template = (Template) args[a++];
									return create(session, ac, stackId, region,
											inputs, template);
								}
							}, stackId, region, inputs, template);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		};
		ExecutorHelper.execute(r);
	}

	/**
	 * Create resources based on a template
	 * 
	 * @param s
	 *            database session to use
	 * @param ac
	 *            caller account
	 * @param stackId
	 *            stack id to be used
	 * @param region
	 *            region for the template
	 * @param inputs
	 *            TemplateContext containing parameters to template
	 * @param template
	 *            template to be used
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> createOrUpdate(final Session s,
			final AccountType ac, final String stackId, final String region,
			final TemplateContext inputs, final Template template,
			final boolean updateOnly) throws Exception {

		// find out if the stack already exists, if yes then it is an update
		// call
		final String sql = "from ResourcesBean where userId=" + ac.getId()
				+ " and stackId='" + stackId + "'";
		final Query q = s.createQuery(sql);
		final List<ResourcesBean> l = q.list();
		boolean update = false;
		if (l != null && l.size() > 0) {
			update = true;
		}

		logger.debug("Update " + update + " " + template.getDescription()
				+ " version " + template.getVersion() + " inputs " + inputs);

		// set the default parameters
		inputs.put("AWS::Stack", new ParameterResource("AWS::Stack", stackId));
		inputs.put("AWS::Region", new ParameterResource("AWS::Region", region));

		// copy default value for parameters not defined
		for (final Entry<String, ParameterResource> en : template
				.getParameters().entrySet()) {
			final String k = en.getKey();
			final ParameterResource p = en.getValue();
			if (inputs.get(k) == null) {
				if (p.getDef() != null) {
					p.setValue(p.getDef());
					inputs.put(k, p);
				}
			}
		}

		// loop for resource creation
		final List<String> resourceCreated = new ArrayList<String>();
		for (;;) {
			int notfound = 0;
			int created = 0;
			// loop for resources defined
			for (final Map.Entry<String, JsonNode> res : template
					.getResources().entrySet()) {
				final String k = res.getKey();

				// if resource is already created go to next resource
				if (resourceCreated.contains(k)) {
					continue;
				}
				logger.debug("Resource " + k + " is not found.");
				notfound++;

				// if resource has DependOn and they are not created, loop back
				// till they are created
				final JsonNode r = res.getValue();
				final JsonNode jdep = r.get("DependsOn");
				if (jdep != null) {
					boolean skip = false;
					if (jdep.isArray()) {
						final Iterator<JsonNode> ir = jdep.getElements();
						while (ir.hasNext()) {
							final String f = ir.next().getTextValue();
							final Object val = inputs.get(f);
							if (val == null) {
								logger.debug(k + "DependsOn " + val);
								skip = true;
								continue;
							}
						}
					} else {
						final String f = jdep.getTextValue();
						final Object val = inputs.get(f);
						if (val == null) {
							logger.debug(k + "DependsOn " + val);
							continue;
						}
					}
					if (skip) {
						continue;
					}
				}

				// expand and calculate all the properties for this resource if
				// some property has a ref and the referenced resource is not
				// created, loop back and wait for that resource creation
				final Map<String, Object> properties = new HashMap<String, Object>();
				final JsonNode jp = r.get("Properties");
				if (jp != null) {
					boolean skip = false;
					final Iterator<String> ir = jp.getFieldNames();
					while (ir.hasNext()) {
						final String f = ir.next();
						final JsonNode pm = jp.get(f);
						try {
							final Object val = inputs.getValue(pm);
							properties.put(f, val);
						} catch (final RefNotFoundException e) {
							logger.debug("Skipping " + k + " ref " + f);
							skip = true;
							continue;
						} catch (final Exception ex) {
							logger.debug("not found " + f);
							throw ex;
						}

					}
					if (skip) {
						continue;
					}
				}

				// Create call structure and create resource
				final String type = r.get("Type").getTextValue();
				// final JsonNode metadata = r.get("Metadata");
				logger.debug("Executing Resource " + k + " type " + type
						+ " properties " + properties);
				final CallStruct call = new CallStruct();
				call.setAc(ac);
				call.setCtx(inputs);
				call.setName(k);
				call.setProperties(properties);
				call.setStackId(stackId);
				call.setType(type);
				Resource o = null;
				if (update || updateOnly) {
					o = CFUtil.updateResource(call);
				} else {
					o = CFUtil.createResource(call);
				}

				// if resource is not created then throw exception
				if (o == null) {
					logger.error("Resource not created for " + type
							+ " aborting");
					break;
				}

				// add to resources created
				resourceCreated.add(k);
				inputs.put(k, o);
				created++;
				logger.debug("Setting Resource " + k);
			}

			// if no resource is created
			if (created == 0) {
				// some of the resource couldn't be created as there may be a
				// ref loop
				if (notfound != 0) {
					logger.error("ERROR " + notfound + " resources not created");
				}
				break;
			}
		}

		// generate template output
		final Map<String, Object> outputs = new HashMap<String, Object>();
		for (final Entry<String, JsonNode> o : template.getOutputs().entrySet()) {
			final JsonNode on = o.getValue();
			final JsonNode vn = on.get("Value");
			if (vn != null) {
				outputs.put(o.getKey(), inputs.getValue(vn));
			}
		}
		return outputs;
	}

	public Map<String, Object> update(final Session s, final AccountType ac,
			final String stackId, final String region,
			final TemplateContext inputs, final Template template)
			throws Exception {
		return createOrUpdate(s, ac, stackId, region, inputs, template, true);
	}
}
