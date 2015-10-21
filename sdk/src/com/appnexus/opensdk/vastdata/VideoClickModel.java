package com.appnexus.opensdk.vastdata;

import java.util.ArrayList;

public class VideoClickModel {
	private String clickThroughURL;
	private ArrayList<ClickTrackingModel> clickTrackingArrayList = new ArrayList<ClickTrackingModel>();
	private ArrayList<CustomClickModel> customClickArrayList = new ArrayList<CustomClickModel>();
	
	/**
	 * @return the clickThroughURL
	 */
	public String getClickThroughURL() {
		return clickThroughURL;
	}
	/**
	 * @param clickThroughURL the clickThroughURL to set
	 */
	public void setClickThroughURL(String clickThroughURL) {
		this.clickThroughURL = clickThroughURL;
	}
	/**
	 * @return the clickTrackingArrayList
	 */
	public ArrayList<ClickTrackingModel> getClickTrackingArrayList() {
		return clickTrackingArrayList;
	}
	/**
	 * @param clickTrackingArrayList the clickTrackingArrayList to set
	 */
	public void setClickTrackingArrayList(
			ArrayList<ClickTrackingModel> clickTrackingArrayList) {
		this.clickTrackingArrayList = clickTrackingArrayList;
	}
	/**
	 * @return the customClickArrayList
	 */
	public ArrayList<CustomClickModel> getCustomClickArrayList() {
		return customClickArrayList;
	}
	/**
	 * @param customClickArrayList the customClickArrayList to set
	 */
	public void setCustomClickArrayList(ArrayList<CustomClickModel> customClickArrayList) {
		this.customClickArrayList = customClickArrayList;
	}

}
