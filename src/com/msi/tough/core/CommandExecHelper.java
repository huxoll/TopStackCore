package com.msi.tough.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;

public class CommandExecHelper {
	class StreamGobbler extends Thread {
		private final InputStream is;
		private final String type;
		private final StringBuilder sb;
		private final String file;
		private final String outbegin;
		private final String outend;

		StreamGobbler(final InputStream is, final String type,
				final StringBuilder sb, final String file,
				final String outbegin, final String outend) {
			this.is = is;
			this.type = type;
			this.sb = sb;
			this.file = file;
			this.outbegin = outbegin;
			this.outend = outend;
		}

		@Override
		public void run() {
			FileWriter fw = null;
			try {
				final InputStreamReader isr = new InputStreamReader(is);
				final BufferedReader br = new BufferedReader(isr);
				String line = null;
				boolean ok = true;
				if (outbegin != null) {
					ok = false;
				}
				if (file != null) {
					fw = new FileWriter(file);
				}
				while ((line = br.readLine()) != null) {
					logger.debug(type + ">" + line);
					if (!ok) {
						if (outbegin != null && line.startsWith(outbegin)) {
							ok = true;
						}
					}
					if (ok) {
						if (sb != null) {
							sb.append(line).append("\n");
						}
						if (fw != null) {
							fw.write(line + "\n");
						}
					}
					if (outend != null && line.startsWith(outend)) {
						ok = false;
					}
				}
			} catch (final IOException ioe) {
				ioe.printStackTrace();
			} finally {
				if (fw != null) {
					try {
						fw.close();
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static Logger logger = Appctx.getLogger(CommandExecHelper.class
			.getName());

	public Map<String, String> ex(final File dir, final String[] cmdarray,
			final StringBuilder sbout, final StringBuilder sberr,
			final String outfile, final String errfile, final String outbegin,
			final String outend) {
		logger.debug(Arrays.asList(cmdarray) + " " + sbout != null ? "sbout"
				: "" + " " + sberr != null ? "sberr" : "" + outfile + errfile);
		int i = 0;
		for (final String s : cmdarray) {
			if (s == null) {
				throw new RuntimeException("null entry " + i);
			}
			i++;
		}
		final BufferedReader stdout = null;
		final BufferedReader stderr = null;
		try {
			final Process p = Runtime.getRuntime().exec(cmdarray, null, dir);
			// any error message?
			final StreamGobbler errorGobbler = new StreamGobbler(
					p.getErrorStream(), "STDERR", sberr, errfile, null, null);

			// any output?
			final StreamGobbler outputGobbler = new StreamGobbler(
					p.getInputStream(), "STDOUT", sbout, outfile, outbegin,
					outend);

			// kick them off
			errorGobbler.start();
			outputGobbler.start();

			final int exitVal = p.waitFor();
			logger.debug("ExitValue: " + exitVal);
			return null;
		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (stdout != null) {
				try {
					stdout.close();
				} catch (final IOException e) {
				}
			}
			if (stderr != null) {
				try {
					stderr.close();
				} catch (final IOException e) {
				}
			}
		}
	}
}
