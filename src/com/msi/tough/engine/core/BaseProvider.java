package com.msi.tough.engine.core;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.BaseException;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.Constants;
import com.msi.tough.utils.LicenseException;

/**
 * Base provider call for all resource providers
 *
 * @author raj
 *
 */
public abstract class BaseProvider implements Provider, Constants {
	private static Logger logger = Appctx.getLogger(BaseProvider.class
			.getName());

	private Long resourceBeanId;

	@Override
	public CFType create(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		if (call.getName() == null && isResource()) {
			throw new BaseException("Name cannot be null for resource "
					+ call.getName());
		}
		final String stackId = call.getStackId();
		if (call.getProperty(Constants.AVAILABILITYZONE) != null) {
			call.setAvailabilityZone((String) call
					.getProperty(Constants.AVAILABILITYZONE));
		}

		resourceBeanId = HibernateUtil.withNewSession(new Operation<Long>() {

			@Override
			public Long ex(final Session s, final Object... args)
					throws Exception {
				if (isLicenseRequired()) {
				    throw new LicenseException();
				}
				if (isResource()) {
					if (stackId == null) {
						throw new BaseException(
								"Stackid cannot be null for resource "
										+ call.getName());
					}
					final Date dt = new Date();
					final ResourcesBean b = new ResourcesBean();
					b.setName(call.getName());
					b.setCreatedDate(dt);
					b.setDescription(call.getDescription());
					if (call.getPhysicalId() == null) {
						b.setPhysicalId(call.getName());
					} else {
						b.setPhysicalId(call.getPhysicalId());
					}
					b.setStackId(call.getStackId());
					b.setParentId(call.getParentId());
					b.setStatus("CREATE_STARTED");
					b.setType(call.getType());
					b.setAvailabilityZone(call.getAvailabilityZone());
					b.setUpdatedDate(dt);
					b.setUserId(ac.getId());
					b.setWaitHook(call.getWaitHookClass() != null ? call
							.getWaitHookClass() : waitHookClazz());
					b.setFailHook(failHookClazz());
					b.setResourceData(call.getResourceData());
					s.save(b);
					logger.info("resourceBean added " + b.getName());
					return b.getId();
				}
				return null;
			}
		});
		CFType ret = null;
		try {
			ret = create0(call);
		} catch (final Exception e) {
			e.printStackTrace();
			CFUtil.failStack(ac.getId(), stackId);
			return null;
		}

		if (isResource() && ret != null && resourceBeanId != null) {
			final String st = ret.getNoWait();

			HibernateUtil.withNewSession(new Operation<Object>() {

				@Override
				public Object ex(final Session s, final Object... args)
						throws Exception {
					final ResourcesBean b = getResourceBean(s);
					if (st == null || st.equals("0")) {
						b.setStatus("CREATE_COMPLETE");
						b.setNoWait(0);
					} else {
						b.setNoWait(Integer.parseInt(st));
					}
					s.save(b);
					logger.info("resourceBean updated " + b);
					return null;
				}
			});
		}
		return ret;
	}

	/**
	 * create method to be implemented by provider implementations
	 *
	 * @param s
	 *            database session
	 * @param call
	 *            call structure
	 * @return created resource
	 * @throws Exception
	 */
	public abstract CFType create0(CallStruct call) throws Exception;

	@Override
	public Resource delete(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		String name0 = call.getPhysicalId();
		if (name0 == null) {
			name0 = call.getName();
			call.setPhysicalId(name0);
		}
		final String name = name0;
		logger.debug("Deleting " + ac.getId() + " " + name);
		if (name == null) {
			throw new RuntimeException(
					"Name cannot be null for a resource deletion");
		}
		if (call.getResourcesBean() != null) {
			setResourceBeanId(call.getResourcesBean().getId());
		}
		CFUtil.deleteStackResources(ac, call.getStackId(), name, null);
		final Resource res = delete0(call);

		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {
				CFUtil.deleteResourceRecords(s, ac.getId(), call.getStackId(),
						name, null);
				CFUtil.deleteResourceRecords(s, ac.getId(), call.getStackId(),
						null, name);
				return null;
			}
		});
		logger.info("Deleted " + name);
		return res;
	}

	public Resource delete0(final CallStruct call) throws Exception {
		return null;
	}

	protected String failHookClazz() {
		return null;
	}

	@SuppressWarnings("unchecked")
	public ResourcesBean getResourceBean(final Session s) {
		final Query q = s.createQuery("from ResourcesBean where id="
				+ resourceBeanId);
		final List<ResourcesBean> l = q.list();
		if (l != null && l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public ResourcesBean getResourceBean(final Session s, final long acid,
			final String stackId, final String name) {
		final Query q = s.createQuery("from ResourcesBean where userId=" + acid
				+ " and stackId='" + stackId + "' and physicalId='" + name
				+ "'");
		final List<ResourcesBean> l = q.list();
		if (l != null && l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	public Long getResourceBeanId() {
		return resourceBeanId;
	}

	/**
	 * Test if a resource has been updated
	 *
	 * @param s
	 *            database session
	 * @param call
	 *            call structure
	 * @return true if resource definition has changed
	 * @throws Exception
	 */
	public boolean hasChanged(final Session s, final CallStruct call)
			throws Exception {
		return false;
	}

	protected boolean isLicenseRequired() {
		return false;
	}

	protected boolean isResource() {
		return true;
	}

	public void setResourceBeanId(final Long resourceBeanId) {
		this.resourceBeanId = resourceBeanId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CFType update(final CallStruct call) throws Exception {
		final AccountType ac = call.getAc();
		final Boolean hasCh = HibernateUtil
				.withNewSession(new Operation<Boolean>() {

					@Override
					public Boolean ex(final Session s, final Object... args)
							throws Exception {
						final Boolean hasCh = hasChanged(s, call);
						if (hasCh) {
							final ResourcesBean b = getResourceBean(s,
									ac.getId(), call.getStackId(),
									call.getName());
							if (isResource() && b != null) {
								b.setParentId(call.getParentId());
								b.setStatus("UPDATE_STARTED");
								b.setType(call.getType());
								b.setUpdatedDate(new Date());
								s.save(b);
								logger.info("resourceBean updated " + b);
							}
						}
						return hasCh;
					}
				});

		final CFType ret = update0(call);

		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {
				if (hasCh) {
					final Query q = s
							.createQuery("from ResourcesBean where userId="
									+ ac.getId() + " and stackId='"
									+ call.getStackId() + "' and physicalId='"
									+ call.getName() + "'");
					final List<ResourcesBean> l = q.list();
					ResourcesBean b = null;
					if (l != null && l.size() > 0) {
						b = l.get(0);
					}
					if (isResource() && b != null) {
						final String st = ret.getNoWait();
						if (st == null || st.equals("0")) {
							b.setStatus("UPDATE_COMPLETE");
							b.setNoWait(0);
						} else {
							b.setNoWait(Integer.parseInt(st));
						}
						s.save(b);
					}
				}
				return null;
			}
		});
		logger.info("resourceBean updated " + ret);
		return ret;
	}

	/**
	 * update method to be implemented by provider implementation
	 *
	 * @param s
	 *            database session
	 * @param call
	 *            call structure
	 * @return update resource
	 * @throws Exception
	 */
	public CFType update0(final CallStruct call) throws Exception {
		return null;
	}

	protected String waitHookClazz() {
		return null;
	}

}
