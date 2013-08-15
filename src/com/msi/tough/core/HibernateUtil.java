/*
 * HibernateUtil.java
 *
 * MSI Eucalyptus LoadBalancer Project
 * Copyright (C) Momentum SI
 *
 */

package com.msi.tough.core;

import java.net.URL;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * Helper class for Hibernate
 *
 * @author raj
 *
 */
public class HibernateUtil {
	/**
	 * Helper interface to implement closure style of coding for working with
	 * session objects. It is used by method wtihSession.
	 *
	 * @author raj
	 *
	 * @param <R>
	 *            Generic type of object returned by the withSession method.
	 */
	public static interface Operation<R> {
		public R ex(Session session, Object... args) throws Exception;
	}

	private static SessionFactory sessionFactory = null;

	public static Session getSession() {
	    if (sessionFactory == null) {
	        final SessionFactory r = Appctx.getBean("sessionFactory");
	        return r.getCurrentSession();
	    } else {
	        return sessionFactory.getCurrentSession();
	    }
	}

	/**
	 * Get a session factory using Axis2 class loader and hibernate.cfg.xml file
	 * from classpath
	 *
	 * @return session factory
	 */
	public static SessionFactory getSessionFactory() {
		try {
			if (sessionFactory == null) {
				// Create the SessionFactory from hibernate.cfg.xml
				// return new Configuration().configure().buildSessionFactory();

				final AxisService axisService = MessageContext
						.getCurrentMessageContext().getAxisService();

				final ClassLoader serviceClassLoader = axisService
						.getClassLoader();

				final URL configURL = serviceClassLoader
						.getResource("hibernate.cfg.xml");

				// sessionFactory = new Configuration().configure(configURL)
				// .buildSessionFactory();
				sessionFactory = new AnnotationConfiguration().configure(
						configURL).buildSessionFactory();
			}
			return sessionFactory;

		} catch (final Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

    /**
     * Set the session factory to use, for e.g. unit tests.
     *
     * @pram session factory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        HibernateUtil.sessionFactory = sessionFactory;
    }

	public static Session newSession() {
		final SessionFactory r = Appctx.getBean("sessionFactory");
		return r.openSession();
	}

	public static <R> R withNewSession(final Operation<R> op,
			final Object... args) {
	    final Session session;
	    if (sessionFactory == null) {
	        final SessionFactory factory = Appctx.getBean("sessionFactory");
	        session = factory.openSession();
	    } else {
	        session = sessionFactory.openSession();
	    }
		session.beginTransaction();
		boolean commit = true;
		try {
			return op.ex(session, args);
		} catch (final Exception e) {
			e.printStackTrace();
			commit = false;
			return null;
		} finally {
			if (commit) {
				session.getTransaction().commit();
			} else {
				session.getTransaction().rollback();
			}
			try {
				session.close();
			} catch (final Exception e) {
			}
		}
	}

	/**
	 * Closure style method to work with transactions.
	 *
	 * @param <R>
	 *            Generic of type of object returned by closure
	 * @param op
	 *            closure to work with transactions
	 * @param args
	 *            parameters to be passed to the closure
	 * @return object returned by the closure
	 */
	public static <R> R withSession(final Operation<R> op, final Object... args) {
		final SessionFactory factory = Appctx.getBean("sessionFactory");
		final Session session = factory.getCurrentSession();
		session.beginTransaction();
		boolean commit = true;
		try {
			return op.ex(session, args);
		} catch (final Exception e) {
			e.printStackTrace();
			commit = false;
			return null;
		} finally {
			if (commit) {
				session.getTransaction().commit();
			} else {
				session.getTransaction().rollback();
			}
		}
	}
}
