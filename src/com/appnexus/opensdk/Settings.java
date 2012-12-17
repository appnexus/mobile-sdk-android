/**
 * 
 */
package com.appnexus.opensdk;

/**
 * @author jacob
 *
 */
public class Settings {
	
	String publisher_id="";
	String ad_id="";
	
	private static Settings settings_instance=null;
	
	static Settings getSettings(){
		if(settings_instance==null){
			settings_instance=new Settings();
		}
		return settings_instance;
	}

}
