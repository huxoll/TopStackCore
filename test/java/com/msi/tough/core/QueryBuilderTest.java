package com.msi.tough.core;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-toughcore-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class QueryBuilderTest {


    @Test
    public void testBasicConstruction() throws Exception {
        final Session session = HibernateUtil.newSession();
        QueryBuilder builder = new QueryBuilder("select 1 from dual");
        builder.equals("1", "1");
        Query query = builder.toSqlQuery(session);
        List<?> ret = query.list();
        assertEquals("Keyname should be same for same input.", "1", ret.get(0).toString());
    }
}
