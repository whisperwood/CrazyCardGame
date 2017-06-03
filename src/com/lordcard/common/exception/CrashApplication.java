package com.lordcard.common.exception;

import com.lordcard.ui.view.GoogleAdsHelper;

import android.app.Application;

/**
 * common.exception.CrashApplication
 * @author Administrator <br/>
 *         create at 2012 2012-12-15 下午12:07:05
 */
public class CrashApplication extends Application {
	private static CrashApplication Instance;

	public static CrashApplication getInstance() {
		return Instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		CrashExceptionHandler crashExceptionHandler = CrashExceptionHandler.getInstance();
		crashExceptionHandler.init(getApplicationContext());
		Instance = this;
		GoogleAdsHelper.initConext(this);
	}
}
