package com.msi.tough.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-toughcore-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class CommaObjectTest {


    @Test
    public void testToArray() throws Exception {
        CommaObject commas = new CommaObject("One,Two,Three,Four");
        String[] vals = commas.toArray();
        assertEquals("Expect parsed value.", "Three", vals[2]);
    }

    @Test
    public void testToArrayEmpty() throws Exception {
        CommaObject commas = new CommaObject("");
        String[] vals = commas.toArray();
        assertEquals("Expect no length.", vals.length, 0);
    }
}
