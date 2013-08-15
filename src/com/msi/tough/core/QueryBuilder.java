/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.util.StringUtils;

/**
 * Builder to build up a SQL or HQL query.
 *
 * @author jgardner
 *
 */
public class QueryBuilder {

    private StringBuilder sb;

    private Map<String, Object> params = new HashMap<String, Object>();

    private boolean isWhereAppended = false;

    private static final String EQUALS = "=";
    private static final String IN = "in";
    private static final String LESS_OR_EQUAL = "<=";
    private static final String LESS = "<";
    private static final String GREATER = ">";
    private static final String GREATER_OR_EQUAL = ">=";
    private static final String LIKE = "%";

    public QueryBuilder() {
        super();
        sb = new StringBuilder();
    }

    public QueryBuilder(String s) {
        super();
        sb = new StringBuilder(s);
    }

    /**
     *
     * Append arbitrary text to the query adding in whitespace on either side
     * and a newline.
     *
     * @param sb
     * @param text
     */
    public QueryBuilder append(String text) {
        sb.append(" ");
        sb.append(text);
        sb.append(" \n");
        return this;
    }

    /**
     * Append another query builder to this one, including parameters. Useful
     * for sub-queries or delegating portions of queries to other code.
     *
     * @param qb
     * @return
     */
    public QueryBuilder append(QueryBuilder qb) {
        sb.append(qb.sb);
        for (Map.Entry<String, Object> entry : qb.params.entrySet()) {
            addParameter(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Append a name/value to the query. If the value is null or empty then
     * nothing is appended.
     *
     * @param name
     * @param param
     * @param value
     * @param allowNull
     */
    protected QueryBuilder appendEqualParam(String name, String param,
            Object value, boolean allowNull) {
        String operator = getEqualsOperator(value);
        if (operator != null) {
            // Assign an auto parameter name, if none given.
            param = (param == null ? addParameter(value) : param);
            if (allowNull) {
                isNullClause(name);
                sb.append(" or ");
                sb.append(name);
                sb.append(" ");
                sb.append(operator);
                sb.append(" (");
                sb.append(":");
                sb.append(param);
                sb.append(")");
                params.put(param, value);
                sb.append(") \n");
            } else {
                and(name, param, value, operator);
            }
        } else if (allowNull) {
            isNull(name);
        }
        return this;
    }

    /**
     * Append a name/value to the query. If the value is null or empty then
     * nothing is appended.
     *
     * @param name
     * @param param
     * @param value
     * @param allowNull
     */
    protected QueryBuilder appendOperatorParam(String name, String operator,
            String param,
            Object value) {
        // Assign an auto parameter name, if none given.
        param = (param == null ? addParameter(value) : param);
        and(name, param, value, operator);
        return this;
    }

    public QueryBuilder isNull(String name) {
        ensureWhere();
        sb.append(" and (");
        sb.append(name);
        sb.append(" is null) \n");
        return this;
    }

    public QueryBuilder isNotNull(String name) {
        ensureWhere();
        sb.append(" and not (");
        sb.append(name);
        sb.append(" is null) \n");
        return this;
    }

    private QueryBuilder isNullClause(String name) {
        sb.append(" (");
        sb.append(name);
        sb.append(" is null) \n");
        return this;
    }

    public QueryBuilder equals(String name, Object value,
            boolean allowNull) {
        ensureWhere();
        return appendEqualParam(name, null, value, allowNull);
    }

    /**
     * Add an "equals" clause for a single value or a collection ("in").
     * If the value is null or empty, nothing is added.
     *
     * @param name
     * @param value
     * @return
     */
    public QueryBuilder equals(String name, Object value) {
        ensureWhere();
        return equals(name, value, false);
    }

    /**
     * This method will append an equal param if the value is not null. If the
     * supplied value is null then the table value must be null
     *
     * @param name
     * @param value
     * @return
     */
    public QueryBuilder strictEquals(String name, Object value) {
        return equals(name, value, (value == null));
    }

    /**
     * This method will append a name and "like" value clause.
     *
     * @param name
     * @param likeValue
     * @return modified query
     */
    public QueryBuilder like(String name, String likeValue) {
        return appendOperatorParam(name, LIKE, null, likeValue);
    }

    public QueryBuilder lessThan(String name, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Null is not a valid value.");
        }
        and(name, addParameter(value), value, LESS);
        return this;
    }

    public QueryBuilder lessThanOrEqual(String name,
            Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Null is not a valid value.");
        }
        and(name, addParameter(value), value, LESS_OR_EQUAL);
        return this;
    }

    public QueryBuilder greaterThan(String name,
            Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Null is not a valid value.");
        }
        and(name, addParameter(value), value, GREATER);
        return this;
    }

    public QueryBuilder greaterThanOrEqual(String name,
            Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Null is not a valid value.");
        }
        and(name, addParameter(value), value,
                GREATER_OR_EQUAL);
        return this;
    }

    public QueryBuilder between(String name,
            Object fromValue, Object toValue) {
        ensureWhere();
        if (fromValue != null && toValue != null) {

            sb.append(" and ");
            sb.append(name);
            sb.append(" between ");

            sb.append(":");
            sb.append(addParameter(fromValue));

            sb.append(" and ");

            sb.append(":");
            sb.append(addParameter(toValue));
        }
        return this;
    }

    public QueryBuilder in(String name, List<?> values) {
        ensureWhere();
        if (values != null) {

            sb.append(" and ");
            sb.append(name);
            sb.append(" in ");

            sb.append("(:");
            sb.append(addParameter(values));
            sb.append(")");
        }
        return this;
    }

    /**
     * Allow combining QueryBuilders (for subselects, unions).
     *
     * @param partners
     * @return
     */
    public QueryBuilder combineParams(QueryBuilder... partners) {
        for (QueryBuilder qb : partners) {
            for (Map.Entry<String, Object> entry : qb.params.entrySet()) {
                params.put(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public Query toQuery(Session session) {
        Query query = session.createQuery(toString());
        addParams(query);
        return query;
    }

    public SQLQuery toSqlQuery(Session session) {
        SQLQuery q = session.createSQLQuery(toString());
        addParams(q);
        return q;
    }

    public void ensureWhere() {
        if (!isWhereAppended) {
            sb.append(" where 1 = 1 ");
            isWhereAppended = true;
        }
    }

    @Override
    public String toString() {
        return sb.toString();
    }

    /**
     * Dump the query, with placeholders resolved to values (for debugging).
     * @return
     */
    public String toStringResolved() {
        StringBuilder sbResolved = new StringBuilder(sb);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String param = ":" + entry.getKey();
            int startIndex = sbResolved.indexOf(param);
            while (startIndex > -1) {
                int endIndex = startIndex + param.length();
                String value = resolveParam(entry.getValue());
                if (value == null) {
                    throw new IllegalStateException("Value '"
                            + entry.getValue()
                            + "' could not be resolved for param '" + param
                            + "'");
                }
                sbResolved.replace(startIndex, endIndex, value);
                startIndex = sbResolved.indexOf(param, startIndex);
            }
        }
        return sbResolved.toString();
    }

    protected void addParams(Query q) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof Collection<?>) {
                q.setParameterList(entry.getKey(), (Collection<?>) entry.getValue());
            } else {
                q.setParameter(entry.getKey(), entry.getValue());
            }
        }
    }

    private void and(String name, String param, Object value,
            String operator) {
        ensureWhere();
        sb.append(" and ");
        sb.append(name);
        sb.append(" ");
        sb.append(operator);
        sb.append(" (");
        sb.append(":");
        sb.append(param);
        sb.append(") \n");
        params.put(param, value);
    }

    private String resolveParam(Object value) {
        String paramValue = null;

        if (value instanceof Collection) {
            Collection<?> newCollection = CollectionUtils.collect(
                    (Collection<?>) value, new Transformer() {
                        @Override
                        public Object transform(Object value) {
                            return resolveSingleParam(value);
                        }
                    });
            paramValue = StringUtils
                    .collectionToCommaDelimitedString(newCollection);
        } else {
            paramValue = resolveSingleParam(value);
        }

        return paramValue;
    }

    private String resolveSingleParam(Object value) {
        String paramValue = null;
        if (value instanceof String) {
            paramValue = "'" + value + "'";
        } else if (value != null) {
            paramValue = value.toString();
        }

        return paramValue;
    }

    /**
     * Add an anonymous parameter to the query.
     * @param value
     * @return
     */
    protected String addParameter(Object value) {
        if (value != null) {
            String param = "param" + params.size();
            addParameter(param, value);
            return param;
        }
        return null;
    }

    /**
     * Add an named parameter to the query.
     * @param name
     * @param value
     */
    public void addParameter(String name, Object value) {
        if (params.put(name, value) != null) {
            throw new IllegalStateException("Parameter already added: " + name);
        }
    }

    /**
     * Method returns the appropriate equals operator given the supplied params.
     *
     * @param name
     * @param param
     * @param value
     * @return
     */
    private String getEqualsOperator(Object value) {
        if ((value instanceof String && StringUtils.hasText(value.toString()))
                || (!(value instanceof String) && value != null)) {
            if (value instanceof Collection) {
                if (((Collection<?>) value).size() > 0) {
                    return IN;
                }
            } else {
                return EQUALS;
            }
        }
        return null;
    }
}
