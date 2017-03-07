/*
 *    Copyright 2013 APPNEXUS INC
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

import android.util.Log;

import com.appnexus.opensdk.util.RoboelectricTestRunnerWithResources;
import com.appnexus.opensdk.util.TestUtil;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.ClogListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@Config(constants = BuildConfig.class, sdk = 21)
@RunWith(RoboelectricTestRunnerWithResources.class)
public class ClogListenerTest {

    boolean didReceiveMessage;

    @Before
    public void setup() {
        Clog.clogged = false;
    }

    @After
    public void tearDown() {
        Clog.unregisterAllListeners();
    }

    // use Log instead of Clog during testing to prevent deadlock

    @Test
    public void test1Register() throws Exception {
        didReceiveMessage = false;

        Clog.registerListener(new ClogListener() {
            @Override
            public void onReceiveMessage(LOG_LEVEL level, String LogTag, String message) {
                didReceiveMessage = true;
            }

            @Override
            public void onReceiveMessage(LOG_LEVEL level, String LogTag, String message, Throwable tr) {
            }

            @Override
            public LOG_LEVEL getLogLevel() {
                return LOG_LEVEL.V;
            }
        });

        clogStuff();

        assertTrue(didReceiveMessage);
    }

    @Test
    public void test2Unregister() throws Exception {
        didReceiveMessage = false;

        ClogListener listener = new ClogListener() {
            @Override
            public void onReceiveMessage(LOG_LEVEL level, String LogTag, String message) {
                didReceiveMessage = true;
            }

            @Override
            public void onReceiveMessage(LOG_LEVEL level, String LogTag, String message, Throwable tr) {
            }

            @Override
            public LOG_LEVEL getLogLevel() {
                return LOG_LEVEL.V;
            }
        };

        Clog.registerListener(listener);
        Clog.unregisterListener(listener);

        clogStuff();

        assertFalse(didReceiveMessage);
    }

    @Test
    public void test3Filters() throws Exception {
        FilterClogListener vLevel = new FilterClogListener(ClogListener.LOG_LEVEL.V);
        FilterClogListener dLevel = new FilterClogListener(ClogListener.LOG_LEVEL.D);
        FilterClogListener iLevel = new FilterClogListener(ClogListener.LOG_LEVEL.I);
        FilterClogListener wLevel = new FilterClogListener(ClogListener.LOG_LEVEL.W);
        FilterClogListener eLevel = new FilterClogListener(ClogListener.LOG_LEVEL.E);
        Clog.registerListener(vLevel);
        Clog.registerListener(dLevel);
        Clog.registerListener(iLevel);
        Clog.registerListener(wLevel);
        Clog.registerListener(eLevel);

        clogStuff();

        Thread.sleep(1000);

        Log.d(TestUtil.testLogTag, vLevel.toString());
        Log.d(TestUtil.testLogTag, dLevel.toString());
        Log.d(TestUtil.testLogTag, iLevel.toString());
        Log.d(TestUtil.testLogTag, wLevel.toString());
        Log.d(TestUtil.testLogTag, eLevel.toString());

        // verbose
        assertEquals(true, vLevel.didReceiveV);
        assertEquals(true, vLevel.didReceiveD);
        assertEquals(true, vLevel.didReceiveI);
        assertEquals(true, vLevel.didReceiveW);
        assertEquals(true, vLevel.didReceiveE);

        // debug
        assertEquals(false, dLevel.didReceiveV);
        assertEquals(true, dLevel.didReceiveD);
        assertEquals(true, dLevel.didReceiveI);
        assertEquals(true, dLevel.didReceiveW);
        assertEquals(true, dLevel.didReceiveE);

        // info
        assertEquals(false, iLevel.didReceiveV);
        assertEquals(false, iLevel.didReceiveD);
        assertEquals(true, iLevel.didReceiveI);
        assertEquals(true, iLevel.didReceiveW);
        assertEquals(true, iLevel.didReceiveE);

        // warning
        assertEquals(false, wLevel.didReceiveV);
        assertEquals(false, wLevel.didReceiveD);
        assertEquals(false, wLevel.didReceiveI);
        assertEquals(true, wLevel.didReceiveW);
        assertEquals(true, wLevel.didReceiveE);

        // error
        assertEquals(false, eLevel.didReceiveV);
        assertEquals(false, eLevel.didReceiveD);
        assertEquals(false, eLevel.didReceiveI);
        assertEquals(false, eLevel.didReceiveW);
        assertEquals(true, eLevel.didReceiveE);
    }

    @Test
    public void test4HandleNullListeners() throws Exception {
        Clog.registerListener(null);
        Clog.unregisterListener(null);

        clogStuff();

        assertTrue(true);
    }

    @Test
    public void testThrowable() throws Exception {
        didReceiveMessage = false;

        ClogListener listener = new ClogListener() {
            @Override
            public void onReceiveMessage(LOG_LEVEL level, String LogTag, String message) {
                // should not reach this callback
                assertTrue(false);
            }

            @Override
            public void onReceiveMessage(LOG_LEVEL level, String LogTag, String message, Throwable tr) {
                didReceiveMessage = true;
            }

            @Override
            public LOG_LEVEL getLogLevel() {
                return LOG_LEVEL.V;
            }
        };

        Clog.registerListener(listener);

        Throwable tr = new Throwable("test");
        Clog.e(TestUtil.testLogTag, "test throwable", tr);

        assertTrue(didReceiveMessage);
    }

    private void clogStuff() {
        Clog.v(TestUtil.testLogTag, "verbose");
        Clog.d(TestUtil.testLogTag, "debug");
        Clog.i(TestUtil.testLogTag, "info");
        Clog.w(TestUtil.testLogTag, "warning");
        Clog.e(TestUtil.testLogTag, "error");
    }

    private class FilterClogListener extends ClogListener {

        public LOG_LEVEL level;
        public boolean didReceiveV, didReceiveD, didReceiveI,
                didReceiveW, didReceiveE;


        private FilterClogListener(LOG_LEVEL level) {
            this.level = level;
        }

        @Override
        public void onReceiveMessage(LOG_LEVEL level, String LogTag, String message) {
            assertNotNull(level);
            assertNotNull(LogTag);
            assertNotNull(message);
            filterCheckLevel(level);
        }

        @Override
        public void onReceiveMessage(LOG_LEVEL level, String LogTag, String message, Throwable tr) {
            assertNotNull(level);
            assertNotNull(LogTag);
            assertNotNull(message);
            assertNotNull(tr);
            filterCheckLevel(level);
        }

        @Override
        public LOG_LEVEL getLogLevel() {
            return level;
        }

        private void filterCheckLevel(LOG_LEVEL level) {
            if (level == LOG_LEVEL.V)
                this.didReceiveV = true;
            else if (level == LOG_LEVEL.D)
                this.didReceiveD = true;
            else if (level == LOG_LEVEL.I)
                this.didReceiveI = true;
            else if (level == LOG_LEVEL.W)
                this.didReceiveW = true;
            else if (level == LOG_LEVEL.E)
                this.didReceiveE = true;
            else
                assertTrue(false);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("FilterClogListener{");
            sb.append("level=").append(level);
            sb.append(", didReceiveV=").append(didReceiveV);
            sb.append(", didReceiveD=").append(didReceiveD);
            sb.append(", didReceiveI=").append(didReceiveI);
            sb.append(", didReceiveW=").append(didReceiveW);
            sb.append(", didReceiveE=").append(didReceiveE);
            sb.append('}');
            return sb.toString();
        }
    }
}
