package com.msi.tough.instance;

import java.util.Map;

import com.msi.tough.core.SupportCallback;

public interface TrackInstance {
	public void trackInstance(SupportCallback callbackTo, String[] instanceIds,
			Map<String, Object> map);
}
