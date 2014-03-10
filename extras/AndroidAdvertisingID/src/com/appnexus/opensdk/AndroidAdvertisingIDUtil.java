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

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Pair;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * This class contains facilities for retrieving the Android Advertising ID
 * Users should call getID(), which, once complete, will call either
 * onRetrievedID() or onFailedToRetrieveID(), for success/failure respectively,
 * on the UIThread.
 *
 * Example code usage to put into your Activity class:
 *
 * {@code
 *
 * private void getAAID() {
 *      AndroidAdvertisingIDUtil util = new AndroidAdvertisingIDUtil() {
 *          @Override
 *          public void onRetrievedID(String androidAdvertisingID, boolean isLimitAdTrackingEnabled) {
 *              Log.d("OPENSDK-AAID', "Setting aaid: " + androidAdvertisingID + " " + isLimitAdTrackingEnabled);
 *              Settings.setAAID(androidAdvertisingID, isLimitAdTrackingEnabled);
 *          }
 *
 *          @Override
 *          public void onFailedToRetrieveID() {
 *              Log.d("OPENSDK-AAID', "Failed to retrieve aaid");
 *              Settings.setAAID(null, false);
 *          }
 *      };
 *      util.getID(this);
 *  }
 *
 */
public abstract class AndroidAdvertisingIDUtil {

    /**
     * Called when the Android Advertising ID has been retrieved successfully
     *
     * @param androidAdvertisingID The Android Advertising ID
     * @param isLimitAdTrackingEnabled whether the user has limit ad tracking enabled or not.
     */
    public abstract void onRetrievedID(String androidAdvertisingID, boolean isLimitAdTrackingEnabled);

    /**
     * Called when the Android Advertising ID was not retrieved successfully
     */
    public abstract void onFailedToRetrieveID();

    /**
     * executes an AsyncTask to retrieve the Android Advertising ID
     *
     * @param context context through which to retrieve the AdvertisingIdInfo
     */
    public void getID(Context context) {
        AAIDTask aaidTask = new AAIDTask(this, context);

        if (Build.VERSION.SDK_INT > 10) {
            aaidTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            aaidTask.execute();
        }
    }

    private class AAIDTask extends AsyncTask<Void, Void, Pair<String, Boolean>> {
        WeakReference<AndroidAdvertisingIDUtil> aaidUtil;
        Context context;

        private AAIDTask(AndroidAdvertisingIDUtil aaidUtil, Context context) {
            this.aaidUtil = new WeakReference<AndroidAdvertisingIDUtil>(aaidUtil);
            this.context = context;
        }

        @Override
        protected Pair<String, Boolean> doInBackground(Void... params) {
            AdvertisingIdClient.Info adInfo = null;
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
            } catch (IOException e) {
                // Unrecoverable error connecting to Google Play services (e.g.,
                // the old version of the service doesn't support getting AdvertisingId).
            } catch (IllegalStateException e) {
                e.printStackTrace();
                // cannot be called on main thread
            } catch (GooglePlayServicesNotAvailableException e) {
                // Google Play services is not available entirely.
            } catch (GooglePlayServicesRepairableException e) {
                // Encountered a recoverable error connecting to Google Play services.
            }

            if (adInfo == null) {
                return null;
            }

            return new Pair<String, Boolean>(adInfo.getId(), adInfo.isLimitAdTrackingEnabled());
        }

        @Override
        protected void onPostExecute(Pair<String, Boolean> aaidPair) {
            super.onPostExecute(aaidPair);

            AndroidAdvertisingIDUtil aaidUtil = this.aaidUtil.get();
            if (aaidUtil != null) {
                if (aaidPair != null) {
                    aaidUtil.onRetrievedID(aaidPair.first, aaidPair.second);
                } else {
                    aaidUtil.onFailedToRetrieveID();
                }
            }
        }
    }
}
