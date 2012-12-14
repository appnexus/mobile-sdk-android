package com.appnexus.opensdk;

//Sample call http://mobile.adnxs.com/mob?id=${PLACEMENT_ID}&hidsha1=${DEVICE_ID_SHA1}&openudid=${DEVICE_ID_OPENUDID}&odin=${DEVICE_ID_ODIN}&ida=${DEVICE_ID_IDA}&optout=${OPTOUT}&devmake=${DEVICE_MAKE}&devmodel=${DEVICE_MODEL}&carrier=${CARIER}&appid=${APPLICATION_ID}&firstlaunch=${FIRST_LAUNCH}&lat=${LAT}&long=${LONG}&ip=${IP_ADDRESS}&ua=${USER_AGENT_ENC}&orientation=${ORIENTATION}&sdkver=${SDK_VERSION}
public class AdRequestParams {
	String placementID;
	String hidmd5;
	String hidsha1;
	String optOut;
	String devMake;
	String devModel;
	String carrier;
	String appID;
	String firstlaunch;
	String lat;
	String lon;
	String ua;
	String orientation;
	final String sdkVersion = "1.0";

	public AdRequestParams(String placementID, String hidmd5, String hidsha1,
			String optOut, String devMake, String devModel, String carrier,
			String appID, String firstlaunch, String lat, String lon,
			String ua, String orientation) {
		this.placementID = placementID;
		this.hidmd5 = hidmd5;
		this.hidsha1 = hidsha1;
		this.optOut = optOut;
		this.devMake = devMake;
		this.devModel = devModel;
		this.carrier = carrier;
		this.appID = appID;
		this.firstlaunch = firstlaunch;
		this.lat = lat;
		this.lon = lon;
		this.ua = ua;
		this.orientation = orientation;

	}

	public String toString(){
		return "http://mobile.adnxs.com/mob?" + placementID != null ? "id="+placementID : "" +
												hidmd5 != null? "hidmd5="+hidmd5 : "" +
												hidsha1 != null? "hidsha1="+hidsha1 : "" +
												optOut != null? "optout="+optOut : ""+
												devMake != null? "devmake="+devMake : ""+
												devModel != null? "devmodel="+devModel : ""+
												carrier != null? "carrier="+carrier : ""+
												appID != null? "appid="+appID : ""+
												firstlaunch !=null? "firstlaunch="+firstlaunch : ""+
												lat!=null? "lat="+lat : ""+
												lon!=null? "lon="+lon : ""+
												ua!=null? "ua="+ua : ""+
												orientation!=null?"orientation="+orientation:""+
												"sdkver="+sdkVersion;
	}
}
