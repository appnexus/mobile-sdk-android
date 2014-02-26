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
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

public abstract class AndroidAdvertisingIDUtil {

    public abstract void onRetrievedID(String androidAdvertisingID, boolean isLimitAdTrackingEnabled);

    public abstract void onFailedToRetrieveID();

    // Do not call this function from the main thread. Otherwise,
    // an IllegalStateException will be thrown.
    public void getID(final Context context) {
        new Thread() {
            @Override
            public void run() {
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
                    onFailedToRetrieveID();
                    return;
                }

                onRetrievedID(adInfo.getId(), adInfo.isLimitAdTrackingEnabled());
            }
        }.start();
    }
}
