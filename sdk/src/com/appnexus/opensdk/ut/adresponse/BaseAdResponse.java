/*
 *    Copyright 2017 APPNEXUS INC
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

package com.appnexus.opensdk.ut.adresponse;

import static com.appnexus.opensdk.utils.Settings.ImpressionType.*;

import com.appnexus.opensdk.ANAdResponseInfo;
import com.appnexus.opensdk.XandrAd;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.Settings.ImpressionType;

import java.util.ArrayList;
import java.util.HashMap;


public abstract class BaseAdResponse {
    private int width;
    private int height;

    private ANAdResponseInfo adResponseInfo;

    private String adType;
    private String contentSource;
    private String adContent;
    private ArrayList<String> impressionURLs = new ArrayList<String>();
    private ImpressionType impressionType = BEGIN_TO_RENDER;

    private HashMap<String, Object> extras = new HashMap<String, Object>();

    public BaseAdResponse(int width, int height, String adType, ArrayList<String> impressionURLs , ANAdResponseInfo adResponseInfo) {
        this.width = width;
        this.height = height;
        this.adType = adType;
        this.adResponseInfo = adResponseInfo;
        this.impressionURLs = impressionURLs;
        this.impressionType = XandrAd.isEligibleForViewableImpression(adResponseInfo.getBuyMemberId())? VIEWABLE_IMPRESSION: BEGIN_TO_RENDER;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public ArrayList<String> getImpressionURLs() {
        return impressionURLs;
    }

    public void setImpressionURLs(ArrayList<String> impressionURLs) {
        this.impressionURLs = impressionURLs;
    }

    public String getContentSource() {
        return contentSource;
    }

    public void setContentSource(String contentSource) {
        this.contentSource = contentSource;
    }

    public HashMap<String, Object> getExtras() {
        return extras;
    }

    public void addToExtras(String key, Object value) {
        extras.put(key, value);
    }


    public void setExtras(HashMap<String, Object> extras) {
        this.extras = extras;
    }

    public String getAdContent() {
        return adContent;
    }

    public void setAdContent(String adContent) {
        this.adContent = adContent;
    }

    public ANAdResponseInfo getAdResponseInfo() {
        return adResponseInfo;
    }

    public void setAdResponseInfo(ANAdResponseInfo adResponseInfo) {
        this.adResponseInfo = adResponseInfo;
    }

    public Settings.ImpressionType getImpressionType() {
        return impressionType;
    }
}
