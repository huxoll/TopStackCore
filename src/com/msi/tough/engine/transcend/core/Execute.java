package com.msi.tough.engine.transcend.core;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;

import com.msi.tough.cf.CFType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.CommandExecHelper;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;

public class Execute extends BaseProvider {
	private static Logger logger = Appctx.getLogger(Execute.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final List<String> command = (List<String>) call
				.getRequiredProperty("Command");
		StringBuilder sbout = null;
		StringBuilder sberr = null;
		final String outfile = (String) call.getProperty("StdoutFile");
		final String outbegin = (String) call.getProperty("StdoutBegin");
		final String outend = (String) call.getProperty("StdoutEnd");
		final String errfile = (String) call.getProperty("StderrFile");
		final String dir = (String) call.getProperty("Directory");
		if (call.getProperty("Stdout") != null) {
			sbout = new StringBuilder();
		}
		if (call.getProperty("Stderr") != null) {
			sberr = new StringBuilder();
		}
		final File fdir = dir != null ? new File(dir) : null;
		logger.info("dir=" + fdir + " command=" + new CommaObject(command));
		new CommandExecHelper().ex(fdir, command.toArray(new String[1]), sbout,
				sberr, outfile, errfile, outbegin, outend);
		// final MapResource res = new MapResource();
		// if (sbout != null) {
		// res.put("Stdout", sbout.toString());
		// }
		// if (sberr != null) {
		// res.put("Stderr", sberr.toString());
		// }
		// if (task.getProperty("__NO_WAIT__") != null) {
		// res.put("__NO_WAIT__", task.getProperty("__NO_WAIT__"));
		// }
		// return Arrays.asList(new Resource[] { res });
		return null;
	}

	@Override
	protected boolean isResource() {
		return false;
	}
}
