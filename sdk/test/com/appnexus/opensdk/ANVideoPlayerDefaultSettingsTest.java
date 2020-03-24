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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@Config(sdk = 21)
@RunWith(RobolectricTestRunner.class)
public class ANVideoPlayerDefaultSettingsTest {

    @Before
    public void setup() {
        SDKSettings.setExternalExecutor(null);
        try {
            Class videoPlayerSettings = ANVideoPlayerSettings.getVideoPlayerSettings().getClass();
            Field instance = videoPlayerSettings.getDeclaredField("anVideoPlayerSettings");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testVideoPlayerDefaultSettings() {
        ANVideoPlayerSettings videoSettings = ANVideoPlayerSettings.getVideoPlayerSettings();
        assertTrue(videoSettings.isClickThroughControlEnabled());
        assertTrue(videoSettings.isFullScreenControlEnabled());
        assertTrue(videoSettings.isAdTextEnabled());
        assertTrue(videoSettings.isVolumeControlEnabled());
        assertTrue(videoSettings.isTopBarEnabled());
        assertTrue(videoSettings.isSkipEnabled());
        assertEquals(videoSettings.getInitialAudio(), ANInitialAudioSetting.DEFAULT);
        assertEquals(videoSettings.getSkipOffset().intValue(),5);
    }
}
