/*
 *    Copyright 2018 APPNEXUS INC
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

import android.util.Base64;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


class VideoImplementation {

    private AdWebView adWebView;
    private String vastXML;
    private boolean videoComplete = false;
    private boolean adReady = false;


    public VideoImplementation(AdWebView adWebView) {
        this.adWebView = adWebView;
    }

    void webViewFinishedLoading() {
        createVastPlayerWithContent();
    }


    protected void dispatchNativeCallback(String url) {

        url = url.replaceFirst("video://", "");

        url = new String(Base64.decode(url, Base64.NO_WRAP));


        try {
            JSONObject videoObject = new JSONObject(url);

            String eventName = videoObject.getString("event");
            JSONObject paramsDictionary = videoObject.getJSONObject("params");
            if (eventName.equals("adReady")) {
                if (paramsDictionary.has("aspectRatio")) {
                    processAspectRatio(paramsDictionary.getString("aspectRatio"));
                }
                adWebView.success();
                adReady = true;
            } else if (eventName.equals("videoStart")) {
                //
            } else if (eventName.equals("video-error") || eventName.equals("Timed-out")) {
                handleVideoError();
            } else if (eventName.equals("video-complete")) {
                videoComplete = true;
                stopOMIDAdSession();
            } else {
                Clog.e(Clog.videoLogTag, "Error: Unhandled event::" + url);
                return;
            }
        } catch (JSONException ex) {
            Clog.e(Clog.videoLogTag, "Exception: JsonError::" + url);
            handleVideoError();
        } catch (Exception ex) {
            Clog.e(Clog.videoLogTag, "Exception caught::" + url);
            return;
        }
    }

    private void processAspectRatio(String fetchedAspectRatio) {
        if(adWebView.adView instanceof BannerAdView) {
            ((BannerAdView)adWebView.adView).setVideoOrientation(ViewUtil.getVideoOrientation(fetchedAspectRatio));
        }
    }

    private void handleVideoError() {
        if (adReady && !videoComplete) {
            //AdReady has been fired but video errored before Playback completion
            stopOMIDAdSession();
            adWebView.adView.getAdDispatcher().toggleAutoRefresh();
        } else {
            // AdReady has not been fired yet continue to do waterfall
            adWebView.fail();
        }
    }

    void stopOMIDAdSession(){
        adWebView.omidAdSession.stopAdSession();
    }

    protected void createVastPlayerWithContent() {
        try {
            //Encode videoXML to Base64String
            String encodedVastContent = Base64.encodeToString(vastXML.getBytes("UTF-8"), Base64.NO_WRAP);
            String options = Base64.encodeToString(ANVideoPlayerSettings.getVideoPlayerSettings().fetchBannerSettings().getBytes("UTF-8"), Base64.NO_WRAP);
            String inject = String.format("javascript:window.createVastPlayerWithContent('%s','%s')",
                    encodedVastContent, options);
            adWebView.injectJavaScript(inject);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    protected void fireViewableChangeEvent(){
        if (!adReady) return;
        boolean isCurrentlyViewable = adWebView.isVideoViewable();
        String inject = String.format("javascript:window.viewabilityUpdate('%s')",
                isCurrentlyViewable);
        adWebView.injectJavaScript(inject);
    }


    public void setVASTXML(String vastXML) {
        this.vastXML = vastXML;
    }
}