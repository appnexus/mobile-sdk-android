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

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.viewability.ANOmidAdSession;

import java.lang.ref.WeakReference;

class ImpressionTracker {
    private String url;
    private VisibilityDetector visibilityDetector;
    private boolean fired = false;
    private Context context;
    private ImpressionListener listener;
    private ANOmidAdSession anOmidAdSession;
    private ImpressionTrackerListener impressionTrackerListener;
    private WeakReference<View> viewWeakReference;

    static ImpressionTracker create(WeakReference<View> viewWeakReference, String url, VisibilityDetector visibilityDetector, Context context, ANOmidAdSession anOmidAdSession, ImpressionTrackerListener impressionTrackerListener) {
        if (visibilityDetector == null) {
            return null;
        } else {
            ImpressionTracker impressionTracker = new ImpressionTracker(viewWeakReference, url, visibilityDetector, context, anOmidAdSession, impressionTrackerListener);
            visibilityDetector.addVisibilityListener(viewWeakReference, impressionTracker.listener);
            return impressionTracker;
        }
    }

    private ImpressionTracker(WeakReference<View> viewWeakReference, String url, VisibilityDetector visibilityDetector, Context context, ANOmidAdSession anOmidAdSession, ImpressionTrackerListener impressionTrackerListener) {
        this.viewWeakReference = viewWeakReference;
        this.url = url;
        this.visibilityDetector = visibilityDetector;
        this.listener = new ImpressionListener();
        this.context = context;
        this.anOmidAdSession = anOmidAdSession;
        this.impressionTrackerListener = impressionTrackerListener;
    }

    private synchronized void fire() {
        // check if impression has already fired
        if (!fired) {
            SharedNetworkManager nm = SharedNetworkManager.getInstance(context);
            if (nm.isConnected(context)) {
                @SuppressLint("StaticFieldLeak") HTTPGet asyncTask = new HTTPGet() {
                    @Override
                    protected void onPostExecute(HTTPResponse response) {
                        Clog.d(Clog.nativeLogTag, "Impression tracked.");
                        if (impressionTrackerListener != null) {
                            impressionTrackerListener.onImpressionTrackerFired();
                        }
                    }

                    @Override
                    protected String getUrl() {
                        return url;
                    }
                };
                asyncTask.execute();
                visibilityDetector.destroy(viewWeakReference);
                listener = null;
            } else {
                nm.addURL(url, context, new ImpressionTrackerListener() {
                    @Override
                    public void onImpressionTrackerFired() {
                        if (impressionTrackerListener != null) {
                            impressionTrackerListener.onImpressionTrackerFired();
                        }
                    }
                });
            }
            if (anOmidAdSession != null) {
                anOmidAdSession.fireImpression();
            }
            fired = true;
        }
    }

    class ImpressionListener implements VisibilityDetector.VisibilityListener {
        long elapsedTime = 0;

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible && SDKSettings.getCountImpressionOn1pxRendering()) {
                ImpressionTracker.this.fire();
            } else {
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

}
