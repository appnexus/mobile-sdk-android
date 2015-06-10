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

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.appnexus.opensdk.utils.Clog;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Application view Interactions with the Native SDK
 */
public class NativeAdSDK {
    /**
     * Only one of the registerTracking methods below should be called for any single
     * ad response, If you wish to reuse the view object you must call unRegisterTracking before
     * registering the view(s) with a new NativeAdResponse.
     */
    /**
     * Register the developer view that will track impressions and respond to clicks
     * for the native ad.
     *
     * @param response A NativeAdResponse
     * @param view     can be a single view, or container, or a view group
     * @param listener A NativeAdEventListener
     */
    public static void registerTracking(final NativeAdResponse response, final View view, final NativeAdEventListener listener) {
        if (isValid(response)) {
            if (view == null) {
                Clog.e(Clog.nativeLogTag, "View is not valid for registering");
                return;
            }
            // use a handler to always post register runnable to the main looper in the UI thread
            // should not use View.post() because the method posts runnables to different queues based on the view's attachment status
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (view.getTag(R.string.native_tag) != null) {
                        Clog.e(Clog.nativeLogTag, "View has already been registered, please unregister before reuse");
                        return;
                    }
                    if (response.registerView(view, listener)) {
                        WeakReference<NativeAdResponse> reference = new WeakReference<NativeAdResponse>(response);
                        view.setTag(R.string.native_tag, reference);
                    } else {
                        Clog.e(Clog.nativeLogTag, "failed at registering the View");
                    }
                }
            });
        }
    }

    /**
     * Register a list developer views that will track impressions and respond to clicks
     * for the native ad.
     *
     * @param container view/view group that will show native ad
     * @param response  that contains the meta data of native ad
     * @param views     a list of clickables
     * @param listener  called when Ad event happens, can be null
     */
    public static void registerTracking(final NativeAdResponse response, final View container, final List<View> views, final NativeAdEventListener listener) {
        if (isValid(response)) {
            if (container == null || views == null || views.isEmpty()) {
                Clog.e(Clog.nativeLogTag, "Views are not valid for registering");
                return;
            }
            // see comment above
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (container.getTag(R.string.native_tag) != null) {
                        Clog.e(Clog.nativeLogTag, "View has already been registered, please unregister before reuse");
                        return;
                    }
                    if (response.registerViewList(container, views, listener)) {
                        WeakReference<NativeAdResponse> reference = new WeakReference<NativeAdResponse>(response);
                        container.setTag(R.string.native_tag, reference);
                        Clog.d(Clog.nativeLogTag, "View has been registered.");
                    } else {
                        Clog.e(Clog.nativeLogTag, "failed at registering the View");
                    }
                }
            });
        }
    }

    /**
     * Called when you are finished with the views for the response or wish to reuse the
     * view object(s) for a new NativeAdResponse.
     * The old NativeAdResponse will be destroyed once it is unregistered.
     *
     * @param view can be a single view, or container, or a view group
     */
    public static void unRegisterTracking(final View view) {
        if (view == null) {
            return;
        }
        // see comment above
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (view.getTag(R.string.native_tag) != null) {
                    WeakReference reference = (WeakReference) view.getTag(R.string.native_tag);
                    NativeAdResponse response = (NativeAdResponse)reference.get();
                    if (response != null) {
                        Clog.d(Clog.nativeLogTag, "Unregister native ad response, assets will be destroyed.");
                        response.unregisterViews();
                    }
                    view.setTag(R.string.native_tag, null);
                }

            }
        });
    }

    static boolean isValid(NativeAdResponse response) {
        if (response != null && !response.hasExpired()) {
            return true;
        }
        Clog.d(Clog.nativeLogTag, "NativeAdResponse is not valid");
        return false;
    }
}
