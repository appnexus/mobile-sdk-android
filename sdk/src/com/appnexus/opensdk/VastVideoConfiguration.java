/*
 *    Copyright 2015 APPNEXUS INC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
