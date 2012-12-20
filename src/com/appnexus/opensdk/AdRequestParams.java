package com.appnexus.opensdk;

import android.net.Uri;
import android.util.Log;

//Sample call http://mobile.adnxs.com/mob?id=${PLACEMENT_ID}&hidsha1=${DEVICE_ID_SHA1}&openudid=${DEVICE_ID_OPENUDID}&odin=${DEVICE_ID_ODIN}&ida=${DEVICE_ID_IDA}&optout=${OPTOUT}&devmake=${DEVICE_MAKE}&devmodel=${DEVICE_MODEL}&carrier=${CARIER}&appid=${APPLICATION_ID}&firstlaunch=${FIRST_LAUNCH}&lat=${LAT}&long=${LONG}&ip=${IP_ADDRESS}&ua=${USER_AGENT_ENC}&orientation=${ORIENTATION}&sdkver=${SDK_VERSION}
public class AdRequestParams {
	String hidmd5;
	String hidsha1;
	String optOut;
	String devMake;
	String devModel;
	String carrier;
	String firstlaunch;
	String lat;
	String lon;
	String ua;
	String orientation;
	final String sdkVersion = "1.0";

	public AdRequestParams(String lat, String lon, String orientation) {
		this.hidmd5 = Settings.getSettings().getHIDMD5();
		this.hidsha1 = Settings.getSettings().getHIDMD5();
		this.optOut = Settings.getSettings().optOut;
		this.devMake = Settings.getSettings().deviceMake;
		this.devModel = Settings.getSettings().deviceModel;
		this.carrier = Settings.getSettings().getCarrierName();
		this.firstlaunch = ""+Settings.getSettings().first_launch;
		this.lat = lat;
		this.lon = lon;
		this.ua = Settings.getSettings().ua;
		this.orientation = orientation;

	}

	@Override
	public String toString() {
		Log.d("OPENSDK", "Placement ID" + Settings.getSettings().placement_id);
		return Settings.getSettings().BASE_URL
				+ (Settings.getSettings().placement_id != null ? "id="
						+ Uri.encode(Settings.getSettings().placement_id)
						: "id=NO-PLACEMENT-ID")
				+ (hidmd5 != null ? "&hidmd5=" + Uri.encode(hidmd5) : "")
				+ (hidsha1 != null ? "&hidsha1=" + Uri.encode(hidsha1) : "")
				+ (optOut != null ? "&optout=" + optOut : "")
				+ (devMake != null ? "&devmake=" + Uri.encode(devMake) : "")
				+ (devModel != null ? "&devmodel=" + Uri.encode(devModel) : "")
				+ (carrier != null ? "&carrier=" + Uri.encode(carrier) : "")
				+ (Settings.getSettings().app_id != null ? "&appid="
						+ Uri.encode(Settings.getSettings().app_id)
						: "&appid=NO-APP-ID")
				+ (firstlaunch != null ? "&firstlaunch=" + firstlaunch : "")
				+ (lat != null ? "&lat=" + lat : "")
				+ (lon != null ? "&lon=" + lon : "")
				+ (Settings.getSettings().test_mode ? "&istest=true" : "")
				+ (ua != null ? "&ua=" + Uri.encode(ua) : "")
				+ (orientation != null ? "&orientation=" + orientation : "")
				+ "&sdkver=" + Uri.encode(sdkVersion);
	}
}
