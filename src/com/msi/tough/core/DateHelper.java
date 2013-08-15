package com.msi.tough.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {
	/**
	 * Convert from a ISO8601 style date (YYYY-MM-DDThh:mm:ssZ) string to a
	 * Calendar Object.
	 * 
	 * @param timestamp
	 * @return
	 */
	public static Calendar getCalendarFromISO8601String(final String timestamp,
			final TimeZone tz) {
		final Calendar cal = Calendar.getInstance(tz);
		final SimpleDateFormat df = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");
		try {
			cal.setTime(df.parse(timestamp));
		} catch (final ParseException pe) {
			pe.printStackTrace();
			return null;
		}
		return cal;
	}

	public static long getCurrentUnixTimeStamp(final Date date) {
		return new Long(date.getTime() / 1000);
	}

	/**
	 * get an ISO 8601 Date Format
	 * 
	 * @param date
	 * @return
	 */
	public static String getISO8601Date(final Date date) {
		final SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");
		return sdf.format(date);
	}

	public static String getSQLDate(final Date date) {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	public static final Calendar toCalendar(final Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

}
