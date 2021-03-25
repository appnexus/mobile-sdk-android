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

import android.graphics.Rect;
import android.os.Handler;
import android.view.View;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class VisibilityDetector {
    static final long VISIBILITY_THROTTLE_MILLIS = 250;
    private boolean scheduled = false;
    private HashMap<WeakReference<View>, ArrayList<VisibilityListener>> viewListenerMap;
    private Runnable visibilityCheck;
    private ScheduledExecutorService tasker;
    private static VisibilityDetector visibilityDetector;
    private Handler mHandler;

    static VisibilityDetector create(WeakReference<View> view) {
        if (view == null) {
            Clog.d(Clog.nativeLogTag, "Unable to check visibility");
            return null;
        }

        if (visibilityDetector == null) {
            visibilityDetector = new VisibilityDetector();
        }

        visibilityDetector.addViewForVisibility(view);
        return visibilityDetector;
    }

    private VisibilityDetector() {
    }

    private void addViewForVisibility(WeakReference<View> view) {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        if (viewListenerMap == null) {
            viewListenerMap = new HashMap();
        }
        if (!viewListenerMap.containsKey(view)) {
            this.viewListenerMap.put(view, new ArrayList<VisibilityListener>());
            if (this.viewListenerMap.size() == 1) {
                scheduleVisibilityCheck();
            }
        }
    }

    void addVisibilityListener(WeakReference<View> viewWeakReference, VisibilityListener listener) {
        if (listener != null && viewListenerMap.containsKey(viewWeakReference) && !viewListenerMap.get(viewWeakReference).contains(listener)) {
            viewListenerMap.get(viewWeakReference).add(listener);
        }
    }

    boolean removeVisibilityListener(WeakReference<View> viewWeakReference) {
        return viewListenerMap.remove(viewWeakReference) != null;
    }

    void scheduleVisibilityCheck(){
        if(scheduled) return;
        scheduled = true;
        this.visibilityCheck = new Runnable() {
            @Override
            public void run() {
                HashMap<WeakReference<View>, ArrayList<VisibilityListener>> tempMap = new HashMap<>(viewListenerMap);
                for (WeakReference<View> viewWeakReference : tempMap.keySet()) {
                    ArrayList<VisibilityListener> listeners = tempMap.get(viewWeakReference);
                    View view = viewWeakReference.get();
                    if (listeners != null && listeners.size() > 0 && view != null) {
                        // copy listeners to a new array to avoid concurrentmodificationexception
                        ArrayList<VisibilityListener> tempList = new ArrayList<VisibilityListener>();
                        for (VisibilityListener listener : listeners) {
                            tempList.add(listener);
                        }
                        if (isVisible(view)) {
                            for (VisibilityListener listener : tempList) {
                                listener.onVisibilityChanged(true);
                            }
                        } else {
                            for (VisibilityListener listener : tempList) {
                                listener.onVisibilityChanged(false);
                            }
                        }
                    } else {
                        viewListenerMap.remove(viewWeakReference);
                        if (viewListenerMap.size() == 0) {

                        }
                    }
                }
            }
        };
        tasker = Executors.newSingleThreadScheduledExecutor();
        tasker.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mHandler.post(visibilityCheck);
            }
        }, 0, VISIBILITY_THROTTLE_MILLIS, TimeUnit.MILLISECONDS);
    }

    boolean isVisible(View view) {
        if (view == null || view.getVisibility() != View.VISIBLE || view.getParent() == null) {
            return false;
        }

        // holds the visible part of a view
        Rect clippedArea = new Rect();

        if (!view.getGlobalVisibleRect(clippedArea)) {
            return false;
        }

        final int visibleViewArea = clippedArea.height() * clippedArea.width();
        final int totalArea = view.getHeight() * view.getWidth();

        if (totalArea <= 0) {
            return false;
        }
        if (SDKSettings.getCountImpressionOn1pxRendering()) {
            return visibleViewArea >= Settings.MIN_AREA_VIEWED_FOR_1PX;
        } else {
            return 100 * visibleViewArea >= Settings.MIN_PERCENTAGE_VIEWED * totalArea;
        }
    }

    void destroy(WeakReference<View> view) {
        if (viewListenerMap.containsKey(view)) {
            removeVisibilityListener(view);
        }
        if (viewListenerMap.size() == 0) {
            if (tasker != null) {
                tasker.shutdownNow();
            }
            scheduled = false;
            mHandler.removeCallbacks(visibilityCheck);
        }
    }

    interface VisibilityListener {
        void onVisibilityChanged(boolean visible);
    }

}