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

import com.appnexus.opensdk.utils.JsonUtil;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.viewability.ANOmidViewabilty;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@Config(constants = BuildConfig.class, sdk = 21)
@RunWith(RobolectricTestRunner.class)
public class ANVideoPlayerSettingsTest {

    @Before
    public void setup() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testInstreamVideoPlayerSettings() {
       String json = ANVideoPlayerSettings.getVideoPlayerSettings().fetchInStreamVideoSettings();
       try{
           JSONObject jsonObject = new JSONObject(json);
           assertEquals(ANVideoPlayerSettings.AN_INSTREAM_VIDEO,JsonUtil.getJSONString(jsonObject,ANVideoPlayerSettings.AN_ENTRY));
           JSONObject omidOptions = JsonUtil.getJSONObject(jsonObject,ANVideoPlayerSettings.AN_PARTNER);
           assertEquals(ANOmidViewabilty.OMID_PARTNER_NAME,JsonUtil.getJSONString(omidOptions,ANVideoPlayerSettings.AN_NAME));
           assertEquals(Settings.getSettings().sdkVersion,JsonUtil.getJSONString(omidOptions,ANVideoPlayerSettings.AN_VERSION));
       }catch (JSONException e){
           e.printStackTrace();
       }
    }

    @Test
    public void testBannerVideoSettings() {
        String json = ANVideoPlayerSettings.getVideoPlayerSettings().fetchBannerSettings();
        try{
            JSONObject jsonObject = new JSONObject(json);
            assertEquals(ANVideoPlayerSettings.AN_BANNER,JsonUtil.getJSONString(jsonObject,ANVideoPlayerSettings.AN_ENTRY));
            JSONObject omidOptions = JsonUtil.getJSONObject(jsonObject,ANVideoPlayerSettings.AN_PARTNER);
            assertEquals(ANOmidViewabilty.OMID_PARTNER_NAME,JsonUtil.getJSONString(omidOptions,ANVideoPlayerSettings.AN_NAME));
            assertEquals(Settings.getSettings().sdkVersion,JsonUtil.getJSONString(omidOptions,ANVideoPlayerSettings.AN_VERSION));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testVideoPlayerClickThroughTrueSettings() {
        ANVideoPlayerSettings.getVideoPlayerSettings().setClickThroughText("SampleText");
        ANVideoPlayerSettings.getVideoPlayerSettings().shouldShowClickThroughControl(true);
        String json = ANVideoPlayerSettings.getVideoPlayerSettings().fetchInStreamVideoSettings();
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONObject videoOptions = JsonUtil.getJSONObject(jsonObject,ANVideoPlayerSettings.AN_VIDEO_OPTIONS);
            JSONObject learnMoreOptions = JsonUtil.getJSONObject(videoOptions,ANVideoPlayerSettings.AN_LEARN_MORE);
            assertTrue(JsonUtil.getJSONBoolean(learnMoreOptions,ANVideoPlayerSettings.AN_ENABLED));
            assertEquals("SampleText",JsonUtil.getJSONString(learnMoreOptions,ANVideoPlayerSettings.AN_TEXT));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testVideoPlayerClickThroughFalseSettings() {
        ANVideoPlayerSettings.getVideoPlayerSettings().setClickThroughText("SampleText");
        ANVideoPlayerSettings.getVideoPlayerSettings().shouldShowClickThroughControl(false);
        String json = ANVideoPlayerSettings.getVideoPlayerSettings().fetchInStreamVideoSettings();
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONObject videoOptions = JsonUtil.getJSONObject(jsonObject,ANVideoPlayerSettings.AN_VIDEO_OPTIONS);
            JSONObject learnMoreOptions = JsonUtil.getJSONObject(videoOptions,ANVideoPlayerSettings.AN_LEARN_MORE);
            assertFalse(JsonUtil.getJSONBoolean(learnMoreOptions,ANVideoPlayerSettings.AN_ENABLED));
            assertEquals("",JsonUtil.getJSONString(learnMoreOptions,ANVideoPlayerSettings.AN_TEXT));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testVideoPlayerShowAdTextTrueSettings() {
        ANVideoPlayerSettings.getVideoPlayerSettings().setAdText("Video Ad");
        ANVideoPlayerSettings.getVideoPlayerSettings().shouldShowAdText(true);
        String json = ANVideoPlayerSettings.getVideoPlayerSettings().fetchInStreamVideoSettings();
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONObject videoOptions = JsonUtil.getJSONObject(jsonObject,ANVideoPlayerSettings.AN_VIDEO_OPTIONS);
            assertEquals("Video Ad",JsonUtil.getJSONString(videoOptions,ANVideoPlayerSettings.AN_AD_TEXT));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testVideoPlayerShowAdTextFalseSettings() {
        ANVideoPlayerSettings.getVideoPlayerSettings().setAdText("Video Ad");
        ANVideoPlayerSettings.getVideoPlayerSettings().shouldShowAdText(false);
        String json = ANVideoPlayerSettings.getVideoPlayerSettings().fetchInStreamVideoSettings();
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONObject videoOptions = JsonUtil.getJSONObject(jsonObject,ANVideoPlayerSettings.AN_VIDEO_OPTIONS);
            assertEquals("",JsonUtil.getJSONString(videoOptions,ANVideoPlayerSettings.AN_TEXT));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testVideoPlayerVolumeFalseSettings() {
        ANVideoPlayerSettings.getVideoPlayerSettings().shouldShowVolumeControl(false);
        String json = ANVideoPlayerSettings.getVideoPlayerSettings().fetchInStreamVideoSettings();
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONObject videoOptions = JsonUtil.getJSONObject(jsonObject,ANVideoPlayerSettings.AN_VIDEO_OPTIONS);
            assertFalse(JsonUtil.getJSONBoolean(videoOptions,ANVideoPlayerSettings.AN_MUTE));
            assertFalse(JsonUtil.getJSONBoolean(videoOptions,ANVideoPlayerSettings.AN_VOLUME));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testVideoPlayerVolumeTrueSettings() {
        ANVideoPlayerSettings.getVideoPlayerSettings().shouldShowVolumeControl(true);
        String json = ANVideoPlayerSettings.getVideoPlayerSettings().fetchInStreamVideoSettings();
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONObject videoOptions = JsonUtil.getJSONObject(jsonObject,ANVideoPlayerSettings.AN_VIDEO_OPTIONS);
            assertTrue(JsonUtil.getJSONBoolean(videoOptions,ANVideoPlayerSettings.AN_MUTE));
            assertTrue(JsonUtil.getJSONBoolean(videoOptions,ANVideoPlayerSettings.AN_VOLUME));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testInstreamFullScreenFalseSettings() {
        ANVideoPlayerSettings.getVideoPlayerSettings().shouldShowFullScreenControl(false);
        String json = ANVideoPlayerSettings.getVideoPlayerSettings().fetchInStreamVideoSettings();
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONObject videoOptions = JsonUtil.getJSONObject(jsonObject,ANVideoPlayerSettings.AN_VIDEO_OPTIONS);
            assertFalse(JsonUtil.getJSONBoolean(videoOptions,ANVideoPlayerSettings.AN_ALLOW_FULLSCREEN));
            assertFalse(JsonUtil.getJSONBoolean(videoOptions,ANVideoPlayerSettings.AN_SHOW_FULLSCREEN));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testInstreamFullScreenTrueSettings() {
        ANVideoPlayerSettings.getVideoPlayerSettings().shouldShowFullScreenControl(true);
        String json = ANVideoPlayerSettings.getVideoPlayerSettings().fetchInStreamVideoSettings();
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONObject videoOptions = JsonUtil.getJSONObject(jsonObject,ANVideoPlayerSettings.AN_VIDEO_OPTIONS);
            assertFalse(videoOptions.has(ANVideoPlayerSettings.AN_ALLOW_FULLSCREEN));
            assertFalse(videoOptions.has(ANVideoPlayerSettings.AN_SHOW_FULLSCREEN));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testOutstreamFullScreenFalseSettings() {
        ANVideoPlayerSettings.getVideoPlayerSettings().shouldShowFullScreenControl(false);
        String json = ANVideoPlayerSettings.getVideoPlayerSettings().fetchBannerSettings();
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONObject videoOptions = JsonUtil.getJSONObject(jsonObject,ANVideoPlayerSettings.AN_VIDEO_OPTIONS);
            assertFalse(JsonUtil.getJSONBoolean(videoOptions,ANVideoPlayerSettings.AN_ALLOW_FULLSCREEN));
            assertFalse(JsonUtil.getJSONBoolean(videoOptions,ANVideoPlayerSettings.AN_SHOW_FULLSCREEN));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testOutstreamFullScreenTrueSettings() {
        ANVideoPlayerSettings.getVideoPlayerSettings().shouldShowFullScreenControl(true);
        String json = ANVideoPlayerSettings.getVideoPlayerSettings().fetchBannerSettings();
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONObject videoOptions = JsonUtil.getJSONObject(jsonObject,ANVideoPlayerSettings.AN_VIDEO_OPTIONS);
            assertTrue(JsonUtil.getJSONBoolean(videoOptions,ANVideoPlayerSettings.AN_ALLOW_FULLSCREEN));
            assertTrue(JsonUtil.getJSONBoolean(videoOptions,ANVideoPlayerSettings.AN_SHOW_FULLSCREEN));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testInitialAudioDefaultSettings() {
        ANVideoPlayerSettings.getVideoPlayerSettings().setInitialAudio(ANInitialAudioSetting.Default);
        String json = ANVideoPlayerSettings.getVideoPlayerSettings().fetchBannerSettings();
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONObject videoOptions = JsonUtil.getJSONObject(jsonObject,ANVideoPlayerSettings.AN_VIDEO_OPTIONS);
            assertEquals("",JsonUtil.getJSONString(videoOptions,ANVideoPlayerSettings.AN_INITIAL_AUDIO));

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testInitialAudioOffSettings() {
        ANVideoPlayerSettings.getVideoPlayerSettings().setInitialAudio(ANInitialAudioSetting.SoundOff);
        String json = ANVideoPlayerSettings.getVideoPlayerSettings().fetchBannerSettings();
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONObject videoOptions = JsonUtil.getJSONObject(jsonObject,ANVideoPlayerSettings.AN_VIDEO_OPTIONS);
            assertEquals(ANVideoPlayerSettings.AN_OFF,JsonUtil.getJSONString(videoOptions,ANVideoPlayerSettings.AN_INITIAL_AUDIO));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testInitialAudioOnSettings() {
        ANVideoPlayerSettings.getVideoPlayerSettings().setInitialAudio(ANInitialAudioSetting.SoundOn);
        String json = ANVideoPlayerSettings.getVideoPlayerSettings().fetchBannerSettings();
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONObject videoOptions = JsonUtil.getJSONObject(jsonObject,ANVideoPlayerSettings.AN_VIDEO_OPTIONS);
            assertEquals(ANVideoPlayerSettings.AN_ON,JsonUtil.getJSONString(videoOptions,ANVideoPlayerSettings.AN_INITIAL_AUDIO));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
