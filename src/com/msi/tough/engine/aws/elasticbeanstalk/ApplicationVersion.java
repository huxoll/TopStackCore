package com.msi.tough.engine.aws.elasticbeanstalk;

import org.slf4j.Logger;

import com.msi.tough.cf.CFType;
import com.msi.tough.core.Appctx;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;

public class ApplicationVersion extends BaseProvider {

	private static Logger logger = Appctx.getLogger(ApplicationVersion.class
			.getName());

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		// final String name = (String) task.getRequiredProperty("__NAME__");
		// final MapResource res = new MapResource();
		// res.put("Id", name);
		// return Arrays.asList(new Resource[] { res });
		return null;
	}

	@Override
	protected boolean isResource() {
		return false;
	}
}
