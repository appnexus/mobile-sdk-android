package com.appnexus.opensdk;

public class VastVideoConfiguration {
    private boolean openInExternalBrowser=false;

    public enum LABEL_POSITION{
    	TOP_RIGHT, TOP_LEFT, TOP_CENTER, BOTTOM_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER
    }
    
    public enum SKIP_OFFSET_TYPE{
    	ABSOLUTE, RELATIVE
    }
    
    private int skipOffset = 0;
    private SKIP_OFFSET_TYPE skipOffsetType = SKIP_OFFSET_TYPE.ABSOLUTE;
    private LABEL_POSITION countdownLabelPosition = LABEL_POSITION.TOP_RIGHT;
    private boolean dismissVideoAdOnClick;
  

    /**
     * @return the openInExternalBrowser
     */
    public boolean isOpenInExternalBrowser() {
        return openInExternalBrowser;
    }

    /**
     * @param openInBrowser the openInExternalBrowser to set
     */
    public void setOpenInExternalBrowser(boolean openInBrowser) {
        this.openInExternalBrowser = openInBrowser;
    }

	/**
	 * Returns the skip countdown position 
	 * @return
	 */
	public LABEL_POSITION getCountdownLabelPosition() {
		return countdownLabelPosition;
	}

	/**
	 * Sets the countdown timer label position
	 * @param countdownLabelPosition
	 */
	public void setCountdownLabelPosition(LABEL_POSITION countdownLabelPosition) {
		this.countdownLabelPosition = countdownLabelPosition;
	}

	
	public boolean shouldDismissVideoAdOnClick() {
		return dismissVideoAdOnClick;
	}

	/**
	 * Sets whether to dismiss or pause the pre-roll ad on click through. 
	 * As a default behavior SDK would pause the ad whenever the browser is opened.
	 * @param dismissVideoAdOnClick
	 */
	public void setDismissVideoAdOnClick(boolean dismissVideoAdOnClick) {
		this.dismissVideoAdOnClick = dismissVideoAdOnClick;
	}

	/**
	 * @return the skipOffset
	 */
	public int getSkipOffset() {
		return skipOffset;
	}

	/**
	 * Sets the Skip offset value either in seconds or in percentage of ad's total duration, 
	 * depending upon the value of skipOffsetType.
	 * 
	 * @param skipOffset - in seconds or in percentage of ad's total duration
	 * @param skipOffsetType - absolute or relative
	 */
	public void setSkipOffset(int skipOffset, SKIP_OFFSET_TYPE skipOffsetType) {
		this.skipOffset = skipOffset;
		this.skipOffsetType = skipOffsetType;
	}

	/**
	 * @return the skipOffsetType
	 */
	public SKIP_OFFSET_TYPE getSkipOffsetType() {
		return skipOffsetType;
	}
	
}
