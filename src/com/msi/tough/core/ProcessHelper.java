package com.msi.tough.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProcessHelper {
	public static String[] exec(String cmd) throws Exception {
		Process p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p
				.getInputStream()));
		String s = null;
		StringBuilder sbout = new StringBuilder();
		while ((s = stdInput.readLine()) != null) {
			sbout.append(s);
		}
		StringBuilder sberr = new StringBuilder();
		BufferedReader stdErrIn = new BufferedReader(new InputStreamReader(p
				.getErrorStream()));
		while ((s = stdErrIn.readLine()) != null) {
			sberr.append(s);
		}
		String[] ret = new String[2];
		ret[0] = sbout.toString();
		ret[1] = sberr.toString();
		return ret;
	}
}
