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

package com.appnexus.opensdkdemo.stdtests;

import android.util.Log;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.ClogListener;
import com.appnexus.opensdkdemo.util.TestUtil;
import junit.framework.TestCase;

public class TestClogListener extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // use Clog instead of Log during testing to prevent deadlock

    public void testFilters() throws Exception {
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
            filterCheckLevel(level);
        }

        @Override
        public void onReceiveMessage(LOG_LEVEL level, String LogTag, String message, Throwable tr) {
            filterCheckLevel(level);
        }

        @Override
        public LOG_LEVEL getLogLevel() {
            return level;
        }

        private void filterCheckLevel(ClogListener.LOG_LEVEL level) {
            if (level == ClogListener.LOG_LEVEL.V)
                this.didReceiveV = true;
            else if (level == ClogListener.LOG_LEVEL.D)
                this.didReceiveD = true;
            else if (level == ClogListener.LOG_LEVEL.I)
                this.didReceiveI = true;
            else if (level == ClogListener.LOG_LEVEL.W)
                this.didReceiveW = true;
            else if (level == ClogListener.LOG_LEVEL.E)
                this.didReceiveE = true;
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
