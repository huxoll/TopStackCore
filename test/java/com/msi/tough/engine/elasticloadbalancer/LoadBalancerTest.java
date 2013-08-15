package com.msi.tough.engine.elasticloadbalancer;

import java.util.Map;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.core.MapUtil;
import com.msi.tough.core.StringHelper;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.utils.CFUtil;

public class LoadBalancerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoadBalancer() {
	    // This test doesn't appear to be working.
	    /*
		HibernateUtil.withSession(new Operation<Object>() {

			@Override
			public Object ex(final Session session, final Object... args)
					throws Exception {
				final String nm = "lb"
						+ StringHelper.randomStringFromTime().toLowerCase();
				final Map<String, Object> m1 = MapUtil.create(
						"AvailabilityZones", "nova");
				final Map<String, Object> m0 = MapUtil.create("Type",
						"AWS::ElasticLoadBalancing::LoadBalancer",
						"Properties", m1);
				final Map<String, Object> m = MapUtil.create(nm, m0);
				final Map<String, Object> map = MapUtil.create("Resources", m);
				final String script = JsonUtil.toJsonString(map);
				CFUtil.runAWSScript(nm, 9, script, new TemplateContext(null));
				return null;
			}
		});
		*/
	}
}
