package com.empsoft.opine;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class OpineApplication extends Application{


	public final static String TAG = "com.empsoft.opine";
	public final static boolean DEBUG_MODE = false;

	public static CookieManager cookieManager;
	static {
		disableConnectionReuseIfNecessary();
		enableCookieManagement();
	}

	private static void disableConnectionReuseIfNecessary(){
		// Http connection reuse which was buggy pre-froyo
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}

	private static void enableCookieManagement(){
		cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Parse.initialize(this, "62IWkJFCtqyCkyRajF25oAK8ocitulaV2jjpCzZY",
				"AmKpgc2r0KHmochB1sEsW0IbELp7cC32HKZxDPAF");
		PushService.setDefaultPushCallback(this, MainActivity.class);
		Log.i(TAG, "Parse Push notification service initialized");

		ParseInstallation.getCurrentInstallation().saveInBackground();
	}

}