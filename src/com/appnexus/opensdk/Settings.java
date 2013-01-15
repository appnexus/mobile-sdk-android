package com.appnexus.opensdk;

import android.os.Build;

public class Settings {
	String hidmd5=null;
	String hidsha1=null;
	String carrierName=null;
	
	String deviceMake=Build.MANUFACTURER;
	String deviceModel=Build.MODEL;
	String app_id=null;

	boolean test_mode=false;
	String ua=null;
	boolean first_launch;
	final String sdkVersion = "1.0";
	//final String BASE_URL = "http://asweeney.adnxs.net:2048/mob?";
	final String BASE_URL = "http://shuf.ro/anmob?";
	
	private static Settings settings_instance=null;
	public static Settings getSettings(){
		if(settings_instance==null){
			Clog.d("OPENSDK", "The AppNexus OpenSDK is initializing");
			settings_instance=new Settings();
		}
		return settings_instance;
	}
}
