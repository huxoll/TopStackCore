package com.msi.tough.utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WaitLock
{
	private static Lock lock;
	private static WaitLock instance;

	private WaitLock()
	{
		lock = new ReentrantLock();
	}

	public static WaitLock getInstance()
	{
		if(instance == null)
		{
			instance = new WaitLock();
		}
		return instance;
	}

	public void lock()
	{
		lock.lock();
	}

	public void unlock()
	{
		lock.unlock();
	}

	public boolean tryLock()
	{
		return lock.tryLock();
	}
}
