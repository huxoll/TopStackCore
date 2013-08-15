package com.msi.tough.security;

import static org.junit.Assert.*;
import javax.crypto.SecretKey;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
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
public class AESSecurityTest {


	static SecretKey sk;
	static String encryptedString;
	//access key from database
	final static String awsSecretKey = "testing";
	//Install password
	final static String password = "transcend";

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEncrypt(){
		try{
			encryptedString = AESSecurity.encrypt(awsSecretKey);
			System.out.println("Encrypted Secret Key: " + encryptedString);
			assertFalse(encryptedString.equals(awsSecretKey));
		}
		catch(Exception e){
			e.printStackTrace();
			Assert.fail("Exception " + e.toString() + " thrown.  Testing the encryption failed!");
		}
	}

	@Test
	public void testDecrypt() {
		try{
			String decryptedString = AESSecurity.decrypt(encryptedString);
			assert(decryptedString.equals(awsSecretKey));
		}
		catch(Exception e){
			e.printStackTrace();
			Assert.fail("Exception " + e.toString() + " thrown.  Testing the decryption failed!");
		}
	}



}