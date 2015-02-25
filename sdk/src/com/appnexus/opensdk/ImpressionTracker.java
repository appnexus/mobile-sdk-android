/*
 *    Copyright 2014 APPNEXUS INC
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

import android.content.Context;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.Settings;

class ImpressionTracker extends HTTPGet {
    private String url;
    private VisibilityDetector visibilityDetector;
    private boolean fired = false;
    private Context context;
    private ImpressionListener listener;

    static ImpressionTracker create(String url, VisibilityDetector visibilityDetector, Context context) {
        if (visibilityDetector == null) {
            return null;
        } else {
            ImpressionTracker impressionTracker = new ImpressionTracker(url, visibilityDetector, context);
            visibilityDetector.addVisibilityListener(impressionTracker.listener);
            return impressionTracker;
        }
    }

    private ImpressionTracker(String url, VisibilityDetector visibilityDetector, Context context) {
        this.url = url;
        this.visibilityDetector = visibilityDetector;
        this.listener = new ImpressionListener();
        this.context = context;
    }

    private synchronized void fire() {
        // check if impression has already fired
        if (!fired) {
            SharedNetworkManager nm = SharedNetworkManager.getInstance(context);
            if (nm.isConnected(context)) {
                execute();
                visibilityDetector.removeVisibilityListener(listener);
                listener = null;
            } else {
                nm.addURL(url, context);
            }
            fired = true;
        }
    }

    @Override
    protected void onPostExecute(HTTPResponse response) {
        Clog.d(Clog.nativeLogTag, "Impression tracked.");
    }

    @Override
    protected String getUrl() {
        return url;
    }

    class ImpressionListener implements VisibilityDetector.VisibilityListener {
        long elapsedTime = 0;
        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                elapsedTime += VisibilityDetector.VISIBILITY_THROTTLE_MILLIS;
            } else {
                elapsedTime = 0;
            }
            if (elapsedTime >= Settings.NATIVE_AD_VISIBLE_PERIOD_MILLIS) {
                ImpressionTracker.this.fire();
            }
        }
    }

}
