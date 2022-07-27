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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class VisibilityDetector {
    static final long VISIBILITY_THROTTLE_MILLIS = 250;
    private boolean scheduled = false;
    private List<WeakReference<View>> viewList;
    private Runnable visibilityCheck;
    private ScheduledExecutorService tasker;
    private static VisibilityDetector visibilityDetector;
    private Handler mHandler;

    static VisibilityDetector getInstance() {
        if (visibilityDetector == null) {
            visibilityDetector = new VisibilityDetector();
        }
        return visibilityDetector;
    }

    private VisibilityDetector() {
    }

    private boolean contains(List<WeakReference<View>> list, View reference)
    {
        if (list != null) {
            for (WeakReference<View> viewWeakReference : list) {
                if (viewWeakReference.get() == reference) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean remove(List<WeakReference<View>> list, View reference)
    {
        for (Iterator<WeakReference<View>> iterator = list.iterator(); iterator.hasNext(); ) {
            WeakReference<View> weakRef = iterator.next();
            if (weakRef.get() == reference) {
                if (reference != null && !(reference instanceof BannerAdView)) {
                    reference.setTag(R.string.native_view_tag, null);
                }
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    void addVisibilityListener(View view) {
        if (view == null) {
            Clog.d(Clog.nativeLogTag, "Unable to check visibility for null reference");
            return;
        }

        if (mHandler == null) {
            mHandler = new Handler();
        }
        if (viewList == null) {
            viewList = new ArrayList<>();
        }
        if (!contains(viewList, view)) {
            this.viewList.add(new WeakReference<View>(view));
            if (this.viewList.size() == 1) {
                scheduleVisibilityCheck();
            }
        }
    }

    void scheduleVisibilityCheck(){
        if(scheduled) return;
        scheduled = true;
        this.visibilityCheck = new Runnable() {
            @Override
            public void run() {
                ArrayList<WeakReference<View>> tempList = new ArrayList<>(viewList);
                for (WeakReference<View> viewWeakReference : tempList) {
                    VisibilityListener listener = getListener(viewWeakReference);
                    View view = viewWeakReference.get();
                    if (listener != null) {
                        listener.onVisibilityChanged(isVisible(view));
                    } else {
                        destroy(view);
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

    private VisibilityListener getListener(WeakReference<View> viewWeakReference) {
        View view = viewWeakReference.get();
        if (view != null) {
            if (view instanceof VisibilityListener) {
                return (VisibilityListener) view;
            } else {
                return (VisibilityListener) view.getTag(R.string.native_view_tag);
            }
        }
        return null;
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
        // Viewable Impression (Banner and Native)
        return visibleViewArea >= Settings.MIN_AREA_VIEWED_FOR_1PX;
    }

    void destroy(View view) {
        if (contains(viewList, view)) {
            remove(viewList, view);
        }
        if (viewList == null || viewList.size() == 0) {
            if (tasker != null) {
                tasker.shutdownNow();
            }
            scheduled = false;
            if (mHandler != null) {
                mHandler.removeCallbacks(visibilityCheck);
            }
        }
    }

    void pauseVisibilityDetector() {
        if (tasker != null) {
            tasker.shutdownNow();
        }
        scheduled = false;
        if (mHandler != null && visibilityCheck != null) {
            mHandler.removeCallbacks(visibilityCheck);
        }
    }

    void resumeVisibilityDetector() {
        if (viewList != null && viewList.size() > 0) {
            scheduleVisibilityCheck();
        }
    }

    interface VisibilityListener {
        void onVisibilityChanged(boolean visible);
    }

}