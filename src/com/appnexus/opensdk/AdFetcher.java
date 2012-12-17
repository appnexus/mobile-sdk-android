/**
 * 
 */
package com.appnexus.opensdk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * @author jacob
 *
 */
public class AdFetcher{
	//The period in milliseconds
	private int msPeriod=60*1000;
	private ScheduledExecutorService tasker;

	/**
	 * 
	 */
	public AdFetcher(Context context) {
		String aid = android.provider.Settings.Secure.getString(context.getContentResolver(), "android_id");
		String hidmd5 = md5(aid);
		String hidsha1 = sha1(aid);
		String optOut = "false";
		String devMake = Build.MANUFACTURER;
		String devModel = Build.MODEL;
		TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String carrierName = manager.getNetworkOperatorName();
		tasker=Executors.newScheduledThreadPool(1);
		String firstLaunch="false"; //TODO dynamicize
		String lat="0"; //TODO dynamicize
		String lon="0"; //TODO dynamicize
		String ua = ""; //TODO WebSettings instance .getUserAgent(context);
		String orientation=context.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE?"landscape":"portrait";
		tasker.scheduleAtFixedRate(new AdRequest(new AdRequestParams(Settings.getSettings().placement_id, hidmd5, hidsha1, optOut, devMake, devModel, carrierName, Settings.getSettings().app_id, firstLaunch, lat, lon, ua, orientation)), 0, msPeriod, TimeUnit.MILLISECONDS);
	}
	/**
	 * Sets the period. Default is one minute.
	 * @param period The period to refresh ads, in milliseconds.
	 */
	protected void setPeriod(int period){
		msPeriod=period;
	}
	
	protected int getPeriod(){
		return msPeriod;
	}
	
	private String md5(String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i=0; i<messageDigest.length; i++)
	            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
	}
	
	private String sha1(String s) {
	    try {
	        // Create SHA-1 Hash
	        MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuilder buf = new StringBuilder();
	        for (byte b : messageDigest) {
	            int halfbyte = (b >>> 4) & 0x0F;
	            int two_halfs = 0;
	            do {
	                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
	                halfbyte = b & 0x0F;
	            } while (two_halfs++ < 1);
	        }
	        return buf.toString();

	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
	}

}
