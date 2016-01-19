package com.appnexus.opensdk;

interface HibernationListener {
	/**
	 * Gets called when screen comes back from hibernation mode.
	 */
	public void onScreenDisplayOn();
	
	/**
	 * Gets called when screen goes to hibernation mode.
	 */
	public void onScreenDisplayOff();
}
