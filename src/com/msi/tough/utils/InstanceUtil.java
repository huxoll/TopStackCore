package com.msi.tough.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.amazonaws.services.autoscaling.model.Instance;
import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.ec2.InstanceType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.BaseException;
import com.msi.tough.core.QueryBuilder;
import com.msi.tough.core.StringHelper;
import com.msi.tough.engine.aws.ec2.DescribeInstance;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.model.InstanceBean;

public class InstanceUtil implements Constants {

	public static InstanceBean createNewInstance(final Session s,
			final long acid, final String instanceId, final String avzone,
			final String logicalId, final String chefRoles) {
		return createNewInstance(s, acid, instanceId, avzone, logicalId,
				chefRoles, instanceId);
	}

	public static InstanceBean createNewInstance(final Session s,
			final long acid, final String instanceId, final String avzone,
			final String logicalId, final String chefRoles, final String uuid) {
		final InstanceBean b = new InstanceBean();
		b.setInstanceId(instanceId);
		b.setEc2Id(uuid);
		b.setUserId(acid);
		b.setPublicIp("0.0.0.0");
		b.setPrivateIp("0.0.0.0");
		b.setAvzone(avzone);
		b.setHostname(InstanceUtil.getHostName(logicalId));
		b.setChefRoles(chefRoles);
		b.setLogicalId(logicalId);
		b.setStatus("created");
		return b;
	}

	@SuppressWarnings("unchecked")
	public static String endpointBasedId(final Session session,
			final String instanceId) {
		final Map<String, Object> endpointOptions = (Map<String, Object>) Appctx
				.getThreadMap(Constants.ENDPOINT_OPTIONS);
		if (endpointOptions == null) {
			return instanceId;
		}
		final String idType = (String) endpointOptions.get("INSTANCE_ID_TYPE");
		String id = instanceId;
		if (idType != null && idType.equals("EC2")) {
			final InstanceBean ib = InstanceUtil.getInstance(session, id);
			id = ib.getEc2Id();
		}
		return id;
	}

	public static String[] essexEC2Id(final String instanceId, final String desc) {
		if (!instanceId.startsWith("i-")) {
			throw new BaseException("ID should start with i- found "
					+ instanceId);
		}
		final String s = instanceId.substring(2);
		final String[] ret = new String[3];
		ret[0] = "instance-" + s;
		String tkn = desc.trim();
		tkn = tkn.substring(1);
		tkn = tkn.substring(0, tkn.length() - 1).trim();
		final String[] parts = tkn.split(",");
		ret[1] = parts[0].trim();
		ret[2] = parts[1].trim();
		return ret;
	}

	public static String getHostName(final String avzone) {
		if (avzone == null) {
			return null;
		}
		return StringHelper.randomStringFromTime().toLowerCase()
				+ "."
				+ ConfigurationUtil.getConfiguration(Arrays
						.asList(avzone == null ? new String[] { "FQDN_DOMAIN",
								avzone }
								: new String[] { "FQDN_DOMAIN", avzone }));

	}
	public static String getHostName(final String avzone, final String installId, final String service) {
		if (avzone == null) {
			return null;
		}
		return  service
				+"-"
				+installId
				+ "-"
				+ StringHelper.randomStringFromTime().toLowerCase()
				+ "."
				+ ConfigurationUtil.getConfiguration(Arrays
						.asList(avzone == null ? new String[] { "FQDN_DOMAIN",
								avzone }
								: new String[] { "FQDN_DOMAIN", avzone }));

	}

	/**
	 * Read an instance from the database
	 *
	 * @param session
	 *            hibernate session to use
	 * @param name
	 *            name of the load balancer
	 * @return load balancer from database
	 */
	@SuppressWarnings("unchecked")
	public static InstanceBean getInstance(final Session session,
			final String instanceId) {
		final Query q = session
				.createQuery("from InstanceBean where instanceId='"
						+ instanceId + "'");
		final List<InstanceBean> l = q.list();
		if (l == null || l.size() == 0) {
			return null;
		}
		return l.get(0);
	}

	public static InstanceBean getInstanceByHostName(final Session session,
			final long acid, final String hostname) {
	    return getInstanceByHostName(session, acid, hostname, false);
	}

    public static InstanceBean getInstanceByHostName(final Session session,
            final long acid, final String hostname, boolean useCloud) {
        final Query q = session
                .createQuery("from InstanceBean where hostname='" + hostname
                        + "'");
        @SuppressWarnings("unchecked")
        final List<InstanceBean> l = q.list();
        if (l != null && l.size() > 0) {
            return l.get(0);
        }
        if (! useCloud) {
            return null;
        }
        //TODO: call describe instances by hostname to obtain instance.
        return null;
    }

	/**
	 * Read an instance definition from datastore.
	 *
	 * @param session
	 *            hibernate session to use
	 * @param id
	 *            unique ID for instance
	 * @return instance definition
	 */
	@SuppressWarnings("unchecked")
	public static InstanceBean getInstanceByUID(final Session session,
			final String id) {
		final Query q = session.createQuery("from InstanceBean where uuid='"
				+ id + "'");
		final List<InstanceBean> l = q.list();
		if (l == null || l.size() == 0) {
			return null;
		}
		return l.get(0);
	}

    /**
     * Read an instance definition from datastore.
     *
     * @param session
     *            hibernate session to use
     * @param id
     *            EC2-style identifier for instance
     * @return instance definition
     */
    @SuppressWarnings("unchecked")
    public static InstanceBean getInstanceByEc2Id(final Session session,
            final String id) {
        final Query q = session.createQuery("from InstanceBean where ec2Id='"
                + id + "'");
        final List<InstanceBean> l = q.list();
        if (l == null || l.size() == 0) {
            return null;
        }
        return l.get(0);
    }

    /**
     * Look up an instance by ID, cache in DB, collect DNS information.
     * @param session
     * @param ac
     * @param id
     * @return
     * @throws Exception
     */
	public static String getInstanceDns(final Session session,
			final AccountType ac, final String id) throws Exception {
		final InstanceBean ib = getInstance(session, id);
		if (ib != null && ib.getPublicIpId() != null) {
			return ib.getPublicIpId();
		}
		final CallStruct c = new CallStruct();
		c.setAc(ac);
		c.setAvailabilityZone(ac.getDefZone());
		final Map<String, Object> m = new HashMap<String, Object>();
		m.put(AVAILABILITYZONE, ac.getDefZone());
		m.put(INSTANCEIDS, id);
		c.setProperties(m);
		c.setType(DescribeInstance.TYPE);
		final DescribeInstance provider = new DescribeInstance();
		final InstanceType l = (InstanceType) provider.create(c);
		if (l == null || l.getPublicIp() == null) {
			return null;
		}
		if (ib != null) {
			ib.setPublicIpId(l.getPublicIp());
			ib.setPrivateIp(l.getPrivateIpAddress());
			session.save(ib);
		}
		return l.getPublicIp();
	}

	public static String getIP(final String dns, final String ip,
			final String avz) {
		final String useIpforInstances = (String) ConfigurationUtil
				.getConfiguration(Arrays.asList(new String[] {
						"useIpforInstances", avz }));
		if (useIpforInstances == null ||
		        useIpforInstances.toLowerCase().startsWith("y")) {
			return ip;
		} else {
			return dns;
		}
	}

	public static String idType(final String id) {
		if (id.startsWith("i-")) {
			return "EC2";
		}
		if (id.startsWith("instance-")) {
			return "libvert";
		}
		return "hypervisor";
	}

	public static void putDefInstanceValues(final Map<String, Object> prop,
			final AccountType ac) {
		final String avz = (String) prop.get(AVAILABILITYZONE);
		final String installId= Appctx.getBean("INSTALL_ID");
		final String service = (String) prop.get(SERVICE);
		for (final String str : new String[] { IMAGEID, INSTANCETYPE, KERNELID,
				RAMDISKID }) {
			if (!prop.containsKey(str)) {
				final Object c = ConfigurationUtil.getConfiguration(str, avz,
						"ElasticLoadBalancing");
				if (c != null) {
					prop.put(str, c);
				}
			}
		}
		prop.put(KEYNAME, ac.getDefKeyName());
		prop.put(HOSTNAME, InstanceUtil.getHostName(avz, installId, service));
		prop.put(ACID, "" + ac.getId());

	}

	public static Instance toInstance(final InstanceBean i) {
		final Instance b = new Instance();
		b.setAvailabilityZone(i.getAvzone());
		b.setInstanceId(i.getInstanceId());
		b.setLifecycleState(i.getStatus());
		b.setHealthStatus(i.getHealth());
		return b;
	}

	public static InstanceType toInstanceType(final InstanceBean i) {
		final InstanceType b = new InstanceType();
		b.setAvailabilityZone(i.getAvzone());
		b.setInstanceId(i.getInstanceId());
		b.setAcId(i.getId());
		b.setChefRoles(i.getChefRoles());
		b.setDatabag(i.getDatabag());
		b.setHostname(i.getHostname());
		b.setPublicIp(i.getPublicIpId());
		b.setPrivateIpAddress(i.getPrivateIp());
		return b;
	}

	public static String UUIDtoEc2(final Session session, final long acid,
			final String instId) throws Exception {
		final InstanceBean ib = InstanceUtil.getInstance(session, instId);
		if (ib != null && ib.getEc2Id() != null && ib.getEc2Id().length() > 0) {
			return ib.getEc2Id();
		}
		// final AccountBean ac = AccountUtil.readAccount(session, acid);
		// final String ec2Id = OpenstackUtil.UUIDtoEc2(session, ac, instId,
		// ac.getDefZone());
		// if (ec2Id == null) {
		// return null;
		// }
		// ib = InstanceUtil.getInstance(session, ec2Id);
		// if (ib == null) {
		// ib = new InstanceBean();
		// ib.setUserId(acid);
		// ib.setInstanceId(ec2Id);
		// }
		// ib.setUuid(instId);
		// session.save(ib);
		// getInstanceDns(session, AccountUtil.toAccount(ac),
		// ib.getInstanceId());
		// return ec2Id;
		return null;
	}

	/**
	 * Convert a non-standard instance ID into a cloud-normal instance ID.
	 * In some circumstances (e.g. essex instance data) an EC2 style ID
	 * (i-XXXXXX) is returned rather than a native ID.  This converts to native
	 * by reading from instance table.
	 * @param session
	 * @param instId
	 * @return
	 * @throws Exception
	 */
    public static String normalizeInstanceId(final Session session,
            final String instanceId) {
        if (instanceId == null || !instanceId.startsWith("i-")) {
            return instanceId;
        }
        final Query q = new QueryBuilder("from InstanceBean")
        .equals("ec2Id", instanceId)
        .toQuery(session);
        InstanceBean ib = (InstanceBean) q.uniqueResult();
        if (ib != null && !StringHelper.isBlank(ib.getInstanceId()) ) {
            return ib.getInstanceId();
        }
        return instanceId;
    }
}
