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
    protected boolean openInNativeBrowser() {
        return openInNativeBrowser;
    }

    /**
     * @param openInBrowser the openInNativeBrowser to set
     */
    protected void setOpenInNativeBrowser(boolean openInBrowser) {
        this.openInNativeBrowser = openInBrowser;
    }

	/**
	 * Returns the skip countdown position 
	 * @return countdownLabelPosition
	 */
    protected LABEL_POSITION getCountdownLabelPosition() {
		return countdownLabelPosition;
	}

	/**
	 * Sets the countdown timer label position
	 * @param countdownLabelPosition - position of countdown timer label
	 */
    protected void setCountdownLabelPosition(LABEL_POSITION countdownLabelPosition) {
		this.countdownLabelPosition = countdownLabelPosition;
	}


	/**
	 * @return the skipOffset
	 */
    protected int getSkipOffset() {
		return skipOffset;
	}

	/**
	 * Sets the Skip offset value either in seconds or in percentage of ad's total duration, 
	 * depending upon the value of skipOffsetType.
	 * 
	 * @param skipOffset - in seconds or in percentage of ad's total duration
	 * @param skipOffsetType - absolute or relative
	 */
    protected void setSkipOffset(int skipOffset, SKIP_OFFSET_TYPE skipOffsetType) {
		this.skipOffset = skipOffset;
		this.skipOffsetType = skipOffsetType;
	}

	/**
	 * @return the skipOffsetType
	 */
    protected SKIP_OFFSET_TYPE getSkipOffsetType() {
		return skipOffsetType;
	}
	
}
