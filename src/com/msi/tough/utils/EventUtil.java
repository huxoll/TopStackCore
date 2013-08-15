package com.msi.tough.utils;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.model.EventLogBean;
import com.msi.tough.model.EventLogTagBean;

public class EventUtil {
	private static Logger logger = Appctx.getLogger(EventUtil.class.getName());

	public static void addEvent(Session sess, long userId, String message,
			String type, String[] tags) {
		EventLogBean ev = new EventLogBean();
		ev.setUserId(userId);
		ev.setType(type);
		ev.setMessage(message);
		ev.setCreatedTime(new Date());
		sess.save(ev);
		if (tags != null) {
			for (String t : tags) {
				EventLogTagBean tbn = new EventLogTagBean();
				tbn.setCreatedTime(ev.getCreatedTime());
				tbn.setEventId(ev.getId());
				tbn.setTag(t);
				tbn.setUserId(userId);
				sess.save(tbn);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List <EventLogBean> queryEventLogs(Session sess, long userID,
			String type, Date from, Date to, String marker, int maxRecords){
		// build the query strings
		String prdSql = "";
		if (from != null && to != null) {
			prdSql = " AND createdTime>=:start AND createdTime  <= :end";
		}
		else if (from != null && to == null){
			prdSql = " AND createdTime>=:start";
		}
		else if (from == null && to != null){
			prdSql = " AND createdTime<=:end";
		}
		// if marker is set select only records greater than marker
		String markerSql = "";
		if (marker != null && marker.length() != 0) {
			markerSql = " AND id>" + marker;
		}
		String typeSql = "";
		if (type != null && type.length() != 0) {
			markerSql = " AND type='" + type + "'";
		}
		String sql = "from EventLogBean where userId=" + userID + prdSql
		+ typeSql + markerSql + " ORDER BY id";
		
		logger.debug("Selecting EventLogs: HQL Query is " + sql);
		Query query = sess.createQuery(sql);
		if(from != null){
			query.setParameter("start", from);	
		}
		if(to != null){
			query.setParameter("end", to);
		}
		return query.list();
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<EventLogTagBean> queryEventLogTags(Session sess, long userID,
			String tag, Date from, Date to, String marker, int maxRecords) {
		// build the query strings
		String prdSql = "";
		if (from != null && to != null) {
			prdSql = " AND createdTime>=:start AND createdTime  <= :end";
		}
		else if (from != null && to == null){
			prdSql = " AND createdTime>=:start";
		}
		else if (from == null && to != null){
			prdSql = " AND createdTime<=:end";
		}
		// if marker is set select only records greater than marker
		String markerSql = "";
		if (marker != null && marker.length() != 0) {
			markerSql = " AND id>" + marker;
		}
		String tagSql = "";
		if (tag != null && tag.length() != 0) {
			markerSql = " AND tag='" + tag + "'";
		}
		// Select only those events associated with the named source
		String sql = "from EventLogTagBean where userId=" + userID + prdSql
				+ tagSql + markerSql + " ORDER BY id";

		logger.debug("Selecting EventLogTags: HQL Query is " + sql);
		Query query = sess.createQuery(sql);
		if(from != null){
			query.setParameter("start", from);	
		}
		if(to != null){
			query.setParameter("end", to);
		}
		return query.list();
	}
}
