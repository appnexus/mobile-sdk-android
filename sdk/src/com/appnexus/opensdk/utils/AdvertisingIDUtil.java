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

package com.appnexus.opensdk.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.appnexus.opensdk.InitListener;
import com.appnexus.opensdk.SDKSettings;
import com.appnexus.opensdk.tasksmanager.TasksManager;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.RejectedExecutionException;

/**
 * Utility class for retrieving and setting
 * the Google Play Services Advertising ID asynchronously.
 */
public class AdvertisingIDUtil {

    /**
     * Starts an AsyncTask or a BG Thread to retrieve and set the AAID.
     * Does nothing if Settings.aaid is already set for the SDK.
     *
     * @param context context to retrieve the AAID on.
     */
    public static void retrieveAndSetAAID(Context context) {
        retrieveAndSetAAID(context, null);
    }

    private static final String cAdvertisingIdClientName
            = "com.google.android.gms.ads.identifier.AdvertisingIdClient";
    private static final String cAdvertisingIdClientInfoName
            = "com.google.android.gms.ads.identifier.AdvertisingIdClient$Info";

    public static void retrieveAAIDBGTask(Context context) {
        if (!StringUtil.isEmpty(SDKSettings.getAAID())) {
            return;
        }
        Pair<String, Boolean> pair = getAAID(context);
        SDKSettings.setAAID(pair.first, pair.second);
    }

    private static Pair<String, Boolean> getAAID(Context context) {
        String aaid = null;
        Boolean limited = false;

        // attempt to retrieve AAID from GooglePlayServices via reflection

        try {
            if (context != null) {
                // NPE catches null objects
                Class<?> cInfo = Class.forName(cAdvertisingIdClientInfoName);
                Class<?> cClient = Class.forName(cAdvertisingIdClientName);

                Method mGetAdvertisingIdInfo = cClient.getMethod("getAdvertisingIdInfo", Context.class);
                Method mGetId = cInfo.getMethod("getId");
                Method mIsLimitAdTrackingEnabled = cInfo.getMethod("isLimitAdTrackingEnabled");

                Object adInfoObject = cInfo.cast(mGetAdvertisingIdInfo.invoke(null, context));

                aaid = (String) mGetId.invoke(adInfoObject);
                limited = (Boolean) mIsLimitAdTrackingEnabled.invoke(adInfoObject);
            }
        } catch (ClassNotFoundException ignored) {
        } catch (InvocationTargetException ignored) {
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (ClassCastException ignored) {
        } catch (NullPointerException ignored) {
        } catch (Exception ignored) {
            // catches GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException
        }

        // set or clear the AAID depending on success/failure
        return new Pair<String, Boolean>(aaid, limited);
    }

    public static void retrieveAndSetAAID(final Context context, final InitListener listener) {
        // skip if AAID is already available
        if (!StringUtil.isEmpty(SDKSettings.getAAID())) {
            return;
        }
        if (!SDKSettings.isBackgroundThreadingEnabled()) {
            AAIDTask getAAIDTask = new AAIDTask(context, listener);
            try {
                getAAIDTask.executeOnExecutor(SDKSettings.getExternalExecutor());
            } catch (RejectedExecutionException rejectedExecutionException) {
                Clog.e(Clog.baseLogTag, "Concurrent Thread Exception while fetching the AAID: "
                        + rejectedExecutionException.getMessage());
            } catch (Exception exception) {
                Clog.e(Clog.baseLogTag, "Exception while fetching the AAID: " + exception.getMessage());
            }
        } else {
            TasksManager.getInstance().executeOnBackgroundThread(new Runnable() {
                @Override
                public void run() {
                    retrieveAAIDBGTask(context);
                    TasksManager.getInstance().executeOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onInitFinished(true);
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * Retrieves AAID from GooglePlayServices via reflection
     * Sets the SDK's aaid value to the result if successful,
     * or null if failed.
     */
    private static class AAIDTask extends AsyncTask<Void, Void, Pair<String, Boolean>> {


        private WeakReference<Context> context;
        InitListener listener;

        private AAIDTask(Context context, InitListener listener) {
            this.context = new WeakReference<Context>(context);
            this.listener = listener;
        }

        @Override
        protected Pair<String, Boolean> doInBackground(Void... params) {
            Context callcontext = context.get();
            return getAAID(callcontext);
        }

        @Override
        protected void onPostExecute(Pair<String, Boolean> pair) {
            super.onPostExecute(pair);
            SDKSettings.setAAID(pair.first, pair.second);
            if (listener != null) {
                listener.onInitFinished(true);
            }
        }
    }
}
