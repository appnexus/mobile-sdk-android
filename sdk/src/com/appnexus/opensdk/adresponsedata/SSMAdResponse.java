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

package com.appnexus.opensdk.adresponsedata;

import java.util.ArrayList;


public class SSMAdResponse extends BaseAdResponse {
    private String adUrl;
    private int ssmTimeout;

    private ArrayList<String> errorURLs = new ArrayList<String>();
    private ArrayList<String> videoClickURLs = new ArrayList<String>();
    private ArrayList<String> start = new ArrayList<String>();
    private ArrayList<String> skip  = new ArrayList<String>();
    private ArrayList<String> firstQuartile = new ArrayList<String>();
    private ArrayList<String> midpoint = new ArrayList<String>();
    private ArrayList<String> thirdQuartile  = new ArrayList<String>();
    private ArrayList<String> complete = new ArrayList<String>();

    public SSMAdResponse(int width, int height, String adType, String notifyUrl, ArrayList<String> impressionURLs) {
        super(width, height, adType, notifyUrl, impressionURLs);
    }

    public String getAdUrl() {
        return adUrl;
    }

    public void setAdUrl(String adUrl) {
        this.adUrl = adUrl;
    }

    public int getSsmTimeout() {
        return ssmTimeout;
    }

    public void setSsmTimeout(int ssmTimeout) {
        this.ssmTimeout = ssmTimeout;
    }


    public ArrayList<String> getErrorURLs() {
        return errorURLs;
    }

    public void setErrorURLs(ArrayList<String> errorURLs) {
        if(errorURLs == null) return;
        this.errorURLs = errorURLs;
    }

    public ArrayList<String> getStart() {
        return start;
    }

    public void setStart(ArrayList<String> start) {
        if(start == null) return;
        this.start = start;
    }

    public ArrayList<String> getSkip() {
        return skip;
    }

    public void setSkip(ArrayList<String> skip) {
        if(skip == null) return;
        this.skip = skip;
    }

    public ArrayList<String> getFirstQuartile() {
        return firstQuartile;
    }

    public void setFirstQuartile(ArrayList<String> firstQuartile) {
        if(firstQuartile == null) return;
        this.firstQuartile = firstQuartile;
    }

    public ArrayList<String> getMidpoint() {
        return midpoint;
    }

    public void setMidpoint(ArrayList<String> midpoint) {
        if(midpoint == null) return;
        this.midpoint = midpoint;
    }

    public ArrayList<String> getThirdQuartile() {
        return thirdQuartile;
    }

    public void setThirdQuartile(ArrayList<String> thirdQuartile) {
        if(thirdQuartile == null) return;
        this.thirdQuartile = thirdQuartile;
    }

    public ArrayList<String> getComplete() {
        return complete;
    }

    public void setComplete(ArrayList<String> complete) {
        if(complete == null) return;
        this.complete = complete;
    }

    public ArrayList<String> getVideoClickURLs() {
        return videoClickURLs;
    }

    public void setVideoClickURLs(ArrayList<String> videoClickURLs) {
        if(videoClickURLs == null) return;
        this.videoClickURLs = videoClickURLs;
    }
}
