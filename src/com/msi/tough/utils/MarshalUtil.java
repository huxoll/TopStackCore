/**
 *
 */
package com.msi.tough.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.DateHelper;
import com.msi.tough.query.ErrorResponse;

/**
 * @author tdhite
 */
public class MarshalUtil
{
    /**
     * @param nodeParent
     * @param nodeName
     * @param bool
     */
    public static void marshallBoolean(XMLNode nodeParent, String nodeName,
        Boolean bool)
    {
        MarshalUtil.marshallString(nodeParent, nodeName, bool.toString());
    }

    /**
     * @param nodeParent
     * @param nodeName
     * @param dval
     */
    public static void marshallDouble(XMLNode nodeParent, String nodeName,
        Double dval)
    {
        MarshalUtil.marshallString(nodeParent, nodeName, dval.toString());
    }

    /**
     * @param nodeParent
     * @param nodeName
     * @param ival
     */
    public static void marshallInteger(XMLNode nodeParent, String nodeName,
        Integer ival)
    {
        MarshalUtil.marshallString(nodeParent, nodeName, ival.toString());
    }

    /**
     * @param nodeParent
     * @param nodeName
     * @param string
     */
    public static void marshallString(XMLNode nodeParent, String nodeName,
        String string)
    {
        XMLNode nodeString = new XMLNode(nodeName);
        nodeParent.addNode(nodeString);
        if(string !=null){
	        XMLNode val = new XMLNode();
	        nodeString.addNode(val);
	        if(string !=null)
	        val.setPlaintext(string);
        }
    }

    /**
     * @param nodeParent
     * @param nodeName
     * @param list
     * @param stringNodeNames
     */
    public static void marshallStringList(XMLNode nodeParent, String nodeName,
        List<String> list, String stringNodeNames)
    {
        XMLNode nodeList = new XMLNode(nodeName);
        nodeParent.addNode(nodeList);
        for (String string : list)
        {
            MarshalUtil.marshallString(nodeList, stringNodeNames, string);
        }
    }

    /** Please use QueryUtil.addNode() instead of this routine
     * @param nodeParent
     * @param nodeAlarmconfigurationupdatedtimestamp
     * @param alarmConfigurationUpdatedTimestamp
     */
    public static void marshallTimestamp(XMLNode nodeParent, String nodeName,
        Date timeStamp)
    {
        XMLNode nodeTimestamp = new XMLNode(nodeName);
        nodeParent.addNode(nodeTimestamp);
        nodeParent.setPlaintext("" + timeStamp);
    }

    /*
     * (non-Javadoc)
     * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
     */
    /**
     * @param mapIn
     *            Map<String, String[]> of input key/value pairs.
     * @param key
     *            String key to lookup.
     * @param defaultValue
     *            String default value.
     * @param logger
     *            Logger to use for logging information or errors.
     * @return String value given the key, or defaultValue if no such key.
     */
    public static String unmarshallString(Map<String, String[]> mapIn,
        String key, String defaultValue, Logger logger)
    {
        return MarshalUtil.unmarshallString(mapIn, key, defaultValue, false, logger);
    }

    /**
     * @param mapIn
     *            Map<String, String[]> of input key/value pairs.
     * @param key
     *            String key to lookup.
     * @param defaultValue
     *            String default value.
     * @param required
     *            Raise an error if no value is present
     * @param logger
     *            Logger to use for logging information or errors.
     * @return String value given the key, or defaultValue if no such key.
     */
    public static String unmarshallString(Map<String, String[]> mapIn,
        String key, Logger logger)
    {
        return MarshalUtil.unmarshallString(mapIn, key, null, true, logger);
    }

    /**
     * @param mapIn
     *            Map<String, String[]> of input key/value pairs.
     * @param key
     *            String key to lookup.
     * @param defaultValue
     *            String default value.
     * @param required
     *            Raise an error if no value is present
     * @param logger
     *            Logger to use for logging information or errors.
     * @return String value given the key, or defaultValue if no such key.
     */
    private static String unmarshallString(Map<String, String[]> mapIn,
        String key, String defaultValue, boolean required, Logger logger)
    {
        String value = getSingleValue(mapIn, key, required, logger);

        return value == null? defaultValue : value;
    }

    /**
     * Extract an integer from the request, with a default value.
     *
     * @param mapIn
     *            Map<String, String[]> of input key/value pairs.
     * @param key
     *            String key to lookup.
     * @param defaultValue
     *            Integer default value.
     * @param logger
     *            Logger to use for logging information or errors.
     * @return Integer value given the key, or defaultValue if no such key.
     */
    public static Integer unmarshallInteger(Map<String, String[]> mapIn,
        String key, Integer defaultValue, Logger logger)
    {
        return MarshalUtil.unmarshallInteger(mapIn, key, defaultValue, false, logger);
    }

    /**
     * Extract an integer from the request (value is required).
     *
     * @param mapIn
     *            Map<String, String[]> of input key/value pairs.
     * @param key
     *            String key to lookup.
     * @param required
     *            Raise an error if no value is present
     * @param logger
     *            Logger to use for logging information or errors.
     * @return Integer value given the key, or defaultValue if no such key.
     */
    public static Integer unmarshallInteger(Map<String, String[]> mapIn,
        String key, Logger logger)
    {
        return MarshalUtil.unmarshallInteger(mapIn, key, null, true, logger);
    }

    /*
     * (non-Javadoc)
     * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
     */
    /**
     * Extract an integer from the request.
     * @param mapIn
     *            Map<String, String[]> of input key/value pairs.
     * @param key
     *            String key to lookup.
     * @param defaultValue
     *            Integer default value.
     * @param required
     *            Raise an error if no value is present
     * @param logger
     *            Logger to use for logging information or errors.
     * @return Integer value given the key, or defaultValue if no such key.
     */
    private static Integer unmarshallInteger(Map<String, String[]> mapIn,
        String key, Integer defaultValue, boolean required, Logger logger)
    {
        String value = getSingleValue(mapIn, key, required, logger);

        return value == null? defaultValue : Integer.valueOf(value);
    }

    /**
     * Extract an integer from the request, with a default value.
     *
     * @param mapIn
     *            Map<String, String[]> of input key/value pairs.
     * @param key
     *            String key to lookup.
     * @param defaultValue
     *            Double default value.
     * @param logger
     *            Logger to use for logging information or errors.
     * @return Double value given the key, or defaultValue if no such key.
     */
    public static Double unmarshallDouble(Map<String, String[]> mapIn,
        String key, Double defaultValue, Logger logger)
    {
        return MarshalUtil.unmarshallDouble(mapIn, key, defaultValue, false, logger);
    }

    /**
     * Extract an integer from the request (value is required).
     *
     * @param mapIn
     *            Map<String, String[]> of input key/value pairs.
     * @param key
     *            String key to lookup.
     * @param required
     *            Raise an error if no value is present
     * @param logger
     *            Logger to use for logging information or errors.
     * @return Double value given the key, or defaultValue if no such key.
     */
    public static Double unmarshallDouble(Map<String, String[]> mapIn,
        String key, Logger logger)
    {
        return MarshalUtil.unmarshallDouble(mapIn, key, null, true, logger);
    }

    /*
     * (non-Javadoc)
     * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
     */
    /**
     * Extract an integer from the request.
     * @param mapIn
     *            Map<String, String[]> of input key/value pairs.
     * @param key
     *            String key to lookup.
     * @param defaultValue
     *            Double default value.
     * @param required
     *            Raise an error if no value is present
     * @param logger
     *            Logger to use for logging information or errors.
     * @return Double value given the key, or defaultValue if no such key.
     */
    private static Double unmarshallDouble(Map<String, String[]> mapIn,
        String key, Double defaultValue, boolean required, Logger logger)
    {
        String value = getSingleValue(mapIn, key, required, logger);

        return value == null? defaultValue : Double.valueOf(value);
    }

    /**
     * Extract a date from the request (value is required).
     *
     * @param mapIn
     *            Map<String, String[]> of input key/value pairs.
     * @param key
     *            String key to lookup.
     * @param logger
     *            Logger to use for logging information or errors.
     * @return Date value given the key, return failure.
     */
    public static Date unmarshallDate(Map<String, String[]> mapIn,
        String key, Date defaultValue, Logger logger)
    {
        return MarshalUtil.unmarshallDate(mapIn, key, defaultValue, false, logger);
    }

    /**
     * Extract a date from the request.
     *
     * @param mapIn
     *            Map<String, String[]> of input key/value pairs.
     * @param key
     *            String key to lookup.
     * @param logger
     *            Logger to use for logging information or errors.
     * @return Date value given the key, or defaultValue if no such key.
     */
    public static Date unmarshallDate(Map<String, String[]> mapIn,
        String key, Logger logger)
    {
        return MarshalUtil.unmarshallDate(mapIn, key, null, true, logger);
    }

    /**
     * Extract a date from the request.
     * @param mapIn
     *            Map<String, String[]> of input key/value pairs.
     * @param key
     *            String key to lookup.
     * @param defaultValue
     *            Date default value.
     * @param required
     *            Raise an error if no value is present
     * @param logger
     *            Logger to use for logging information or errors.
     * @return Date value given the key, or defaultValue if no such key.
     */
    private static Date unmarshallDate(Map<String, String[]> mapIn,
        String key, Date defaultValue, boolean required, Logger logger)
    {
        String value = getSingleValue(mapIn, key, required, logger);

        if (value != null)
        {
            return DateHelper.getCalendarFromISO8601String(
                    value, TimeZone.getTimeZone("GMT")).getTime();
        }
        return defaultValue;
    }

    /**
     * Extract a boolean from the request (with default if not supplied).
     *
     * @param mapIn
     *            Map<String, String[]> of input key/value pairs.
     * @param key
     *            String key to lookup.
     * @param defaultValue
     *            Boolean default value.
     * @param logger
     *            Logger to use for logging information or errors.
     * @return Boolean value given the key, or defaultValue if no such key.
     */
    public static Boolean unmarshallBoolean(Map<String, String[]> mapIn,
        String key, Boolean defaultValue, Logger logger)
    {
        return MarshalUtil.unmarshallBoolean(mapIn, key, defaultValue, false, logger);
    }

    /**
     * Extract a date from the request (value is required).
     *
     * @param mapIn
     *            Map<String, String[]> of input key/value pairs.
     * @param key
     *            String key to lookup.
     * @param defaultValue
     *            Boolean default value.
     * @param required
     *            Raise an error if no value is present
     * @param logger
     *            Logger to use for logging information or errors.
     * @return Boolean value given the key, or defaultValue if no such key.
     */
    public static Boolean unmarshallBoolean(Map<String, String[]> mapIn,
        String key, Logger logger)
    {
        return MarshalUtil.unmarshallBoolean(mapIn, key, null, true, logger);
    }

    /**
     * Extract a date from the request.
     *
     * @param mapIn
     *            Map<String, String[]> of input key/value pairs.
     * @param key
     *            String key to lookup.
     * @param defaultValue
     *            Boolean default value.
     * @param required
     *            Raise an error if no value is present
     * @param logger
     *            Logger to use for logging information or errors.
     * @return Boolean value given the key, or defaultValue if no such key.
     */
    private static Boolean unmarshallBoolean(Map<String, String[]> mapIn,
        String key, Boolean defaultValue, boolean required, Logger logger)
    {
        String value = getSingleValue(mapIn, key, required, logger);

        return value == null? defaultValue : value.equalsIgnoreCase("true");
    }

    /**
     * Internal method to get a value from the request map.
     *
     * @param mapIn
     *            Map<String, String[]> of input key/value pairs.
     * @param key
     *            String key to lookup.
     * @param defaultValue
     *            String default value.
     * @param required
     *            Raise an error if no value is present
     * @param logger
     *            Logger to use for logging information or errors.
     * @return String value given the key, or defaultValue if no such key.
     */
    private static String getSingleValue(Map<String, String[]> mapIn,
        String key, boolean required, Logger logger)
    {
        String value[] = mapIn.get(key);

        if (value == null)
        {
            if (required) {
                logger.warn("No key \"" + key + "\" exists in the request.");
                throw ErrorResponse.missingParameter();
            }
            logger.debug("No key \"" + key + "\" exists in the request.");
            return null;
        }
        return value[0];
    }

    /**
     * @param in
     * @param string
     *            String prefix for each member (e.g., "AlarmNames.member."
     * @return
     */
    public static Collection<String> unmarshallStrings(
        Map<String, String[]> in, String prefix)
    {
        List<String> strings = new ArrayList<String>();

        for (int i = 1; true; i++)
        {
            final String names[] = in.get(prefix + i);
            if (names != null)
            {
                strings.add(names[0]);
            }
            else
            {
                break;
            }
        }

        return strings;
    }
}
