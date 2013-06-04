package com.appnexus.opensdk.utils;

import java.util.Locale;
import java.util.TimeZone;

import android.os.Build;

public class Settings {
	public String hidmd5 = null;
	public String hidsha1 = null;
	public String carrierName = null;

	public String deviceMake = Build.MANUFACTURER;
	public String deviceModel = Build.MODEL;
	public String app_id = null;

	public boolean test_mode = false;
	public String ua = null;
	public boolean first_launch;
	public final String sdkVersion = "1.0";

	public String mcc;
	public String mnc;
	public String dev_timezone = TimeZone.getDefault().getDisplayName();
	public String os = "Android " + Build.VERSION.RELEASE;
	public String language = Locale.getDefault().getLanguage();

	public final int HTTP_CONNECTION_TIMEOUT = 15000;
	public final int HTTP_SOCKET_TIMEOUT = 20000;
	
	public final int FETCH_THREAD_COUNT = 4;
	//public final String BASE_URL = "http://mobile-dev.adnxs.net/mob?";
	//public final String INSTALL_BASE_URL = "http://mobile-dev.adnxs.com/install?";

	public final String BASE_URL = "http://mobile.adnxs.com/mob?";
	public final String INSTALL_BASE_URL = "http://mobile.adnxs.com/install?";

	// final String BASE_URL = "http://asweeney.adnxs.net:2048/mob?";
	// final String BASE_URL = "http://shuf.ro/anmob/json/?";
	// final String BASE_URL = "http://shuf.ro/anmob/noads/?";
	// final String BASE_URL = "http://shuf.ro/anmob/error/?";
	// public final String BASE_URL = "http://shuf.ro/anmob/temp/?";

	// STATICS
	private static Settings settings_instance = null;

	public static Settings getSettings() {
		if (settings_instance == null) {
			settings_instance = new Settings();
			Clog.v(Clog.baseLogTag, "The AppNexus " + Clog.baseLogTag
					+ " is initializing.");
		}
		return settings_instance;
	}

	private Settings() {

	}

}
