package com.appnexus.opensdk;

public class VastVideoConfiguration {
    private boolean openInNativeBrowser =false;

    public enum LABEL_POSITION{
    	TOP_RIGHT, TOP_LEFT, TOP_CENTER, BOTTOM_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER
    }
    
    public enum SKIP_OFFSET_TYPE{
    	ABSOLUTE, RELATIVE
    }
    
    private int skipOffset = 0;
    private SKIP_OFFSET_TYPE skipOffsetType = SKIP_OFFSET_TYPE.ABSOLUTE;
    private LABEL_POSITION countdownLabelPosition = LABEL_POSITION.TOP_RIGHT;


    /**
     * @return the openInNativeBrowser
     */
    public boolean openInNativeBrowser() {
        return openInNativeBrowser;
    }

    /**
     * @param openInBrowser the openInNativeBrowser to set
     */
    public void setOpenInNativeBrowser(boolean openInBrowser) {
        this.openInNativeBrowser = openInBrowser;
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
