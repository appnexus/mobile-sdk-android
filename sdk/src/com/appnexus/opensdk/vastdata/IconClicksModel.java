package com.appnexus.opensdk.vastdata;

import java.util.ArrayList;

public class IconClicksModel {
	private String iconClickThrough;
	private ArrayList<IconClickTrackingModel> iconClickTrackingArrayList;
	/**
	 * @return the iconClickThrough
	 */
	public String getIconClickThrough() {
		return iconClickThrough;
	}
	/**
	 * @param iconClickThrough the iconClickThrough to set
	 */
	public void setIconClickThrough(String iconClickThrough) {
		this.iconClickThrough = iconClickThrough;
	}
	/**
	 * @return the iconClickTrackingArrayList
	 */
	public ArrayList<IconClickTrackingModel> getIconClickTrackingArrayList() {
		return iconClickTrackingArrayList;
	}
	/**
	 * @param iconClickTrackingArrayList the iconClickTrackingArrayList to set
	 */
	public void setIconClickTrackingArrayList(
			ArrayList<IconClickTrackingModel> iconClickTrackingArrayList) {
		this.iconClickTrackingArrayList = iconClickTrackingArrayList;
	}
}
