/**
 * 
 */
package com.appnexus.opensdk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * @author jacob
 *
 */
public class Settings {
	
	Context context;
	private String aid=null;//
	private String hidmd5=null;
	private String hidsha1=null;
	private String carrierName=null;
	
	String deviceMake=Build.MANUFACTURER;
	String deviceModel=Build.MODEL;
	String optOut="false"; //TODO
	String app_id=null;
	String placement_id=null;
	int refresh_rate_ms=-1;
	boolean test_mode=false;
	String ua=null;
	boolean first_launch;
	final String BASE_URL = "http://asweeney.adnxs.net:2048/mob?";
	
	public String getCarrierName(){
		if(carrierName==null)
			carrierName=((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName();
		return carrierName;
	}
	
	public String getHIDMD5(){
		if(aid==null)
			aid=android.provider.Settings.Secure.getString(context.getContentResolver(), "android_id");
		if(hidmd5==null)
			hidmd5=md5(aid);
		return hidmd5;
	}
	
	public String getHIDSHA1(){
		if(aid==null)
			aid=android.provider.Settings.Secure.getString(context.getContentResolver(), "android_id");
		if(hidmd5==null)
			hidsha1=sha1(aid);
		return hidsha1;
	}
	
	private static Settings settings_instance=null;
	public static Settings getSettings(){
		if(settings_instance==null){
			settings_instance=new Settings();
		}
		return settings_instance;
	}
	private String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
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
			MessageDigest digest = java.security.MessageDigest
					.getInstance("SHA-1");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuilder buf = new StringBuilder();
			for (byte b : messageDigest) {
				int halfbyte = (b >>> 4) & 0x0F;
				int two_halfs = 0;
				do {
					buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte)
							: (char) ('a' + (halfbyte - 10)));
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
