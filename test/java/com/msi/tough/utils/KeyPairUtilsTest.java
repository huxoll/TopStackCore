package com.msi.tough.utils;

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
public class KeyPairUtilsTest {

    @Test
    public void testKeyName() throws Exception {
        String keyName1 = KeyPairUtils.getKeyName("bob");
        String keyName2 = KeyPairUtils.getKeyName("bob");
        String keyName3 = KeyPairUtils.getKeyName("bob2");
        assertEquals("Keyname should be same for same input.", keyName1, keyName2);
        assertFalse("Keyname should be != for != input.", keyName1.equals(keyName3));
    }
}
