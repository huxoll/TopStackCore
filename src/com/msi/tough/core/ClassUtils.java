package com.msi.tough.core;

public class ClassUtils {
	public static Class<?> forName(final Object obj, final String clazz)
			throws ClassNotFoundException {
		return obj.getClass().getClassLoader().loadClass(clazz);
	}

	@SuppressWarnings("unchecked")
	public static <R> R newInstance(final Object obj, final String clazz)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		final Class<?> c = obj.getClass().getClassLoader().loadClass(clazz);
		return (R) c.newInstance();
	}
}
