/**
 * 
 */
package com.appnexus.opensdk;

/**
 * @author jacob
 *
 */
public class Settings {
	
	String app_id=null;
	String placement_id=null;
	int refresh_rate_ms=-1;
	boolean test_mode=false;
	String ua=null;
	boolean first_launch;
	final String BASE_URL = "http://asweeney.adnxs.net:2048/mob?";
	
	private static Settings settings_instance=null;
	
	public static Settings getSettings(){
		if(settings_instance==null){
			settings_instance=new Settings();
		}
		return settings_instance;
	}

}
