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

package com.appnexus.opensdk.instreamvideo.adresponsedata;

import java.util.ArrayList;
import java.util.HashMap;


public abstract class BaseAdResponse {
    private int width;
    private int height;
    private String adType;
    private String notifyUrl;
    private String contentSource;
    private String adContent;
    private ArrayList<String> impressionURLs = new ArrayList<String>();

    private HashMap<String, Object> extras = new HashMap<String, Object>();

    public BaseAdResponse(int width, int height, String adType, String notifyUrl, ArrayList<String> impressionURLs) {
        this.width = width;
        this.height = height;
        this.adType = adType;
        this.notifyUrl = notifyUrl;
        this.impressionURLs = impressionURLs;
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

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
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

}
