/*
 *    Copyright 2019 APPNEXUS INC
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

import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.viewability.ANOmidViewabilty;

import org.json.JSONException;
import org.json.JSONObject;


enum ANInitialAudioSetting {
    SOUND_ON,
    SOUND_OFF,
    DEFAULT
}

/*
 The video player for the AdUnit (Instream & Outstream) can be configured by the publisher
 The available options that the publishers can change are the
 1. ClickThrough Control changes
    1.1 Change the text for clickthrough control
    1.2 Hide the control if not needed
    1.3 Remove or Change the "Ad" text
 2. Show/Hide volume control
 3. Show/Hide fullscreen control for outstream adUnit
 4. Show/Hide the topBar
 */

public class ANVideoPlayerSettings {
    public static final String AN_NAME = "name";
    public static final String AN_VERSION = "version";
    public static final String AN_PARTNER = "partner";
    public static final String AN_ENTRY = "entryPoint";
    public static final String AN_INSTREAM_VIDEO = "INSTREAM_VIDEO";
    public static final String AN_BANNER = "BANNER";
    public static final String AN_AD_TEXT = "adText";
    public static final String AN_SEPARATOR = "separator";
    public static final String AN_ENABLED = "enabled";
    public static final String AN_TEXT = "text";
    public static final String AN_LEARN_MORE = "learnMore";
    public static final String AN_MUTE = "showMute";
    public static final String AN_ALLOW_FULLSCREEN = "allowFullscreen";
    public static final String AN_SHOW_FULLSCREEN = "showFullScreenButton";
    public static final String AN_DISABLE_TOPBAR = "disableTopBar";
    public static final String AN_VIDEO_OPTIONS = "videoOptions";
    public static final String AN_INITIAL_AUDIO = "initialAudio";
    public static final String AN_ON = "on";
    public static final String AN_OFF = "off";
    public static final String AN_SKIP = "skippable";
    public static final String AN_SKIP_DESCRIPTION = "skipText";
    public static final String AN_SKIP_LABEL_NAME = "skipButtonText";
    public static final String AN_SKIP_OFFSET= "videoOffset";

    //Show or Hide the ClickThrough control on the video player. Default is YES, setting it to NO will make the entire video clickable.
    private boolean showClickThroughControl = false;
    //Change the clickThru text on the video player
    private String clickThroughText = null;
    //Show or hide the volume control on the player
    private boolean showVolumeControl = false;
    //Show or hide fullscreen control on the player. This is applicable only for Banner Video
    private boolean showFullScreenControl = false;
    //Show or hide the top bar that has (ClickThrough & Skip control)
    private boolean showTopBar = false;
    //Show or hide the "Ad" text next to the ClickThrough control
    private boolean showAdText = false;
    //Change the ad text on the video player
    private String adText = null;
    //Decide how the ad video sound starts initally (sound on or off). By default its on for InstreamVideo and off for Banner Video
    private ANInitialAudioSetting initialAudio;
    //Show or hide the Skip control on the player
    private boolean showSkip = false;
    //Change the skip description on the video player
    private String skipDescription = null;
    //Change the skip button text on the video player
    private String skipLabelName = null;
    //Configure the skip offset on the video player
    private Integer skipOffset = 0;

    private JSONObject optionsMap;
    private static ANVideoPlayerSettings anVideoPlayerSettings;

    public boolean isClickThroughControlEnabled() {
        return showClickThroughControl;
    }
    public void shouldShowClickThroughControl(boolean showClickThroughControl) {
        this.showClickThroughControl = showClickThroughControl; }

    public String getClickThroughText() {
        return clickThroughText;
    }
    public void setClickThroughText(String clickThroughText) {
        this.clickThroughText = clickThroughText; }

    public boolean isVolumeControlEnabled() {
        return showVolumeControl;
    }
    public void shouldShowVolumeControl(boolean showVolumeControl) {
        this.showVolumeControl = showVolumeControl; }

    public boolean isFullScreenControlEnabled() {
        return showFullScreenControl;
    }
    public void shouldShowFullScreenControl(boolean showFullScreenControl) {
        this.showFullScreenControl = showFullScreenControl; }

    public boolean isTopBarEnabled() {
        return showTopBar;
    }
    public void shouldShowTopBar(boolean showTopBar) {
        this.showTopBar = showTopBar;
    }

    public boolean isAdTextEnabled() {
        return showAdText;
    }
    public void shouldShowAdText(boolean showAdText) {
        this.showAdText = showAdText;
    }

    public String getAdText() {
        return adText;
    }
    public void setAdText(String adText) {
        this.adText = adText;
    }

    public ANInitialAudioSetting getInitialAudio() {
        return initialAudio;
    }
    public void setInitialAudio(ANInitialAudioSetting initialAudio) {
        this.initialAudio = initialAudio; }

    public boolean isSkipEnabled() {
        return showSkip;
    }
    public void shouldShowSkip(boolean showSkip) {
        this.showSkip = showSkip; }

    public String getSkipDescription() {
        return skipDescription;
    }
    public void setSkipDescription(String skipDescription) {
        this.skipDescription = skipDescription;
    }

    public String getSkipLabelName() {
        return skipLabelName;
    }
    public void setSkipLabelName(String skipLabelName) {
        this.skipLabelName = skipLabelName;
    }

    public Integer getSkipOffset() {
        return skipOffset;
    }
    public void setSkipOffset(Integer skipOffset) {
        this.skipOffset = skipOffset;
    }

    private ANVideoPlayerSettings() {
        showClickThroughControl = true;
        showAdText = true;
        showVolumeControl = true;
        showFullScreenControl = true;
        showTopBar = true;
        showSkip = true;
        skipOffset = 5;
        initialAudio = ANInitialAudioSetting.DEFAULT;
        optionsMap = new JSONObject();
        try {
            JSONObject partner = new JSONObject();
            partner.put(AN_NAME, ANOmidViewabilty.OMID_PARTNER_NAME);
            partner.put(AN_VERSION, Settings.getSettings().sdkVersion);
            optionsMap.put(AN_PARTNER, partner);
            optionsMap.put(AN_ENTRY, AN_INSTREAM_VIDEO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ANVideoPlayerSettings getVideoPlayerSettings() {
        if (anVideoPlayerSettings == null) {
            anVideoPlayerSettings = new ANVideoPlayerSettings();
        }
        return anVideoPlayerSettings;
    }

    private String videoPlayerOptions() {

        JSONObject publisherOptions = new JSONObject();
        try {
            JSONObject clickthroughOptions = new JSONObject();
            if (showAdText && !StringUtil.isEmpty(adText)) {
                publisherOptions.put(AN_AD_TEXT, adText);
            } else if (!showAdText) {
                publisherOptions.put(AN_AD_TEXT, "");
                clickthroughOptions.put(AN_SEPARATOR, "");
            }

                clickthroughOptions.put(AN_ENABLED, showClickThroughControl);

            if (showClickThroughControl && !StringUtil.isEmpty(clickThroughText)) {
                clickthroughOptions.put(AN_TEXT, clickThroughText);
            }

            if (clickthroughOptions.length() != 0) {
                publisherOptions.put(AN_LEARN_MORE, clickthroughOptions);
            }

            if (optionsMap.getString(AN_ENTRY).equals(AN_INSTREAM_VIDEO)) {
                JSONObject skipOptions = new JSONObject();
                if (showSkip) {
                    skipOptions.put(AN_SKIP_DESCRIPTION, skipDescription);
                    skipOptions.put(AN_SKIP_LABEL_NAME, skipLabelName);
                    skipOptions.put(AN_SKIP_OFFSET, skipOffset);
                }
                skipOptions.put(AN_ENABLED, showSkip);
                publisherOptions.put(AN_SKIP,skipOptions);

            }

            publisherOptions.put(AN_MUTE, showVolumeControl);

            if (optionsMap.getString(AN_ENTRY).equals(AN_BANNER)) {
                publisherOptions.put(AN_ALLOW_FULLSCREEN, showFullScreenControl);
                publisherOptions.put(AN_SHOW_FULLSCREEN, showFullScreenControl);
            }

            if (initialAudio != ANInitialAudioSetting.DEFAULT) {
                if (initialAudio == ANInitialAudioSetting.SOUND_ON) {
                    publisherOptions.put(AN_INITIAL_AUDIO, AN_ON);
                } else {
                    publisherOptions.put(AN_INITIAL_AUDIO, AN_OFF);
                }
            }else{
                if (publisherOptions.has(AN_INITIAL_AUDIO)) {
                    publisherOptions.put(AN_INITIAL_AUDIO, null);

                }
            }

            if (!showTopBar) {
                publisherOptions.put(AN_DISABLE_TOPBAR, true);
            }

            if (publisherOptions.length() != 0) {
                optionsMap.put(AN_VIDEO_OPTIONS, publisherOptions);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return optionsMap.toString();
    }

    public String fetchInStreamVideoSettings() {
        try {
            optionsMap.put(AN_ENTRY, AN_INSTREAM_VIDEO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return videoPlayerOptions();
    }

    public String fetchBannerSettings() {
        try {
            optionsMap.put(AN_ENTRY, AN_BANNER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return videoPlayerOptions();
    }

}
