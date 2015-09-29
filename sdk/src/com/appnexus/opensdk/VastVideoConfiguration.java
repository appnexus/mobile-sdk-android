package com.appnexus.opensdk;

public class VastVideoConfiguration {
    private boolean openInNativeBrowser =false;
    
    enum SKIP_OFFSET_TYPE{
    	ABSOLUTE, RELATIVE
    }
    
    private int skipOffset = 0;
    private SKIP_OFFSET_TYPE skipOffsetType = SKIP_OFFSET_TYPE.ABSOLUTE;


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
	 * @param isRelative - absolute or relative
	 */
    protected void setSkipOffset(int skipOffset, boolean isRelative) {
		this.skipOffset = skipOffset;
        if(isRelative){
		    this.skipOffsetType = SKIP_OFFSET_TYPE.RELATIVE;
        }else{
            this.skipOffsetType = SKIP_OFFSET_TYPE.ABSOLUTE;
        }

	}

	/**
	 * @return the skipOffsetType
	 */
    protected SKIP_OFFSET_TYPE getSkipOffsetType() {
		return skipOffsetType;
	}
	
}
