/*
 *    Copyright 2013 APPNEXUS INC
 *    
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
	public String dev_timezone = TimeZone.getDefault().getID();
	public String os = "Android " + Build.VERSION.RELEASE;
	public String language = Locale.getDefault().getLanguage();

	public final int HTTP_CONNECTION_TIMEOUT = 15000;
	public final int HTTP_SOCKET_TIMEOUT = 20000;

	public final int FETCH_THREAD_COUNT = 4;
	
	public final int MIN_REFRESH_MILLISECONDS = 15000;
	public final int DEFAULT_INTERSTITIAL_CLOSE_BUTTON_DELAY=10000;
	public final int DEFAULT_INTERSTITIAL_AUTOCLOSE_TIME=Integer.MAX_VALUE;
	public final long HTTP_RETRY_INTERVAL = 30000;

	public final int MAX_HTTP_RETRIES = 3;
	public final int MAX_FAILED_HTTP_RETRIES = 5;
	
	public final String BASE_URL = "http://mobile.adnxs.com/mob?";
	public final String INSTALL_BASE_URL = "http://mobile.adnxs.com/install?";
	
	

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
