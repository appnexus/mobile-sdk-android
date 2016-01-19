package com.appnexus.opensdk.vastdata;

import java.util.ArrayList;

public class LinearAdModel {
	private String skipOffset;
	private String adParameters;
	private String xmlEncoded;
	private String duration;
	private ArrayList<MediaFileModel> mediaFilesArrayList;
	private ArrayList<TrackingModel> trackingEventArrayList;
	private ArrayList<VideoClickModel> videoClicksArrayList;
	private ArrayList<IconModel> iconsArrayList;
	
	
	/**
	 * @return the skipOffset
	 */
	public String getSkipOffset() {
		return skipOffset;
	}
	/**
	 * @param skipOffset the skipOffset to set
	 */
	public void setSkipOffset(String skipOffset) {
		this.skipOffset = skipOffset;
	}
	/**
	 * @return the adParameters
	 */
	public String getAdParameters() {
		return adParameters;
	}
	/**
	 * @param adParameters the adParameters to set
	 */
	public void setAdParameters(String adParameters) {
		this.adParameters = adParameters;
	}
	/**
	 * @return the xmlEncoded
	 */
	public String getXmlEncoded() {
		return xmlEncoded;
	}
	/**
	 * @param xmlEncoded the xmlEncoded to set
	 */
	public void setXmlEncoded(String xmlEncoded) {
		this.xmlEncoded = xmlEncoded;
	}
	/**
	 * @return the duration
	 */
	public String getDuration() {
		return duration;
	}
	/**
	 * @param duration the duration to set
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}
	/**
	 * @return the mediaFilesArrayList
	 */
	public ArrayList<MediaFileModel> getMediaFilesArrayList() {
		return mediaFilesArrayList;
	}
	/**
	 * @param mediaFilesArrayList the mediaFilesArrayList to set
	 */
	public void setMediaFilesArrayList(ArrayList<MediaFileModel> mediaFilesArrayList) {
		this.mediaFilesArrayList = mediaFilesArrayList;
	}
	/**
	 * @return the trackingEventArrayList
	 */
	public ArrayList<TrackingModel> getTrackingEventArrayList() {
		return trackingEventArrayList;
	}
	/**
	 * @param trackingEventArrayList the trackingEventArrayList to set
	 */
	public void setTrackingEventArrayList(ArrayList<TrackingModel> trackingEventArrayList) {
		this.trackingEventArrayList = trackingEventArrayList;
	}
	/**
	 * @return the videoClicksArrayList
	 */
	public ArrayList<VideoClickModel> getVideoClicksArrayList() {
		return videoClicksArrayList;
	}
	/**
	 * @param videoClicksArrayList the videoClicksArrayList to set
	 */
	public void setVideoClicksArrayList(ArrayList<VideoClickModel> videoClicksArrayList) {
		this.videoClicksArrayList = videoClicksArrayList;
	}
	/**
	 * @return the iconsArrayList
	 */
	public ArrayList<IconModel> getIconsArrayList() {
		return iconsArrayList;
	}
	/**
	 * @param iconsArrayList the iconsArrayList to set
	 */
	public void setIconsArrayList(ArrayList<IconModel> iconsArrayList) {
		this.iconsArrayList = iconsArrayList;
	}

	

}
