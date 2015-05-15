/*
 *    Copyright 2015 APPNEXUS INC
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

package com.appnexus.opensdk.mediatedviews;

import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.utils.Clog;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Model.CBError;

import java.util.HashMap;

/**
 * Class to bridge singleton Chartboost delegate to multiple listeners
 */
class ChartboostDelegateBridge extends ChartboostDelegate {
    private static ChartboostDelegateBridge bridge;
    private HashMap<String, ChartboostListener> listenerMap;

    private ChartboostDelegateBridge() {
        listenerMap = new HashMap<String, ChartboostListener>();
    }

    static ChartboostDelegateBridge getInstance() {
        if (bridge == null) {
            bridge = new ChartboostDelegateBridge();
        }
        return bridge;
    }

    void cacheInterstitialWithListener(String location, ChartboostListener listener) {
        if (listener == null) {
            Clog.w(Clog.mediationLogTag, "Not caching for location " + location + " because no listener was passed in.");
            return;
        }
        if (listenerMap.get(location) != null) {
            Clog.d(Clog.mediationLogTag, "An interstitial ad has already been cached for location " + location
                    + ".Only one interstitial per location.");
            listener.didFailToLoadInterstitial(location, ResultCode.UNABLE_TO_FILL);
        } else {
            listenerMap.put(location, listener);
            Chartboost.cacheInterstitial(location);
        }

    }

    void remove(String location, ChartboostListener listener) {
        if (listenerMap.get(location) == listener) {
            listenerMap.remove(location);
        }
    }

    @Override
    public void didCacheInterstitial(String location) {
        super.didCacheInterstitial(location);
        ChartboostListener l = listenerMap.get(location);
        if (l != null) {
            l.didCacheInterstitial(location);
        }
    }

    @Override
    public void didFailToLoadInterstitial(String location, CBError.CBImpressionError error) {
        super.didFailToLoadInterstitial(location, error);
        ResultCode code = ResultCode.INTERNAL_ERROR;
        switch (error) {
            case INTERNET_UNAVAILABLE:
            case TOO_MANY_CONNECTIONS:
            case NETWORK_FAILURE:
                code = ResultCode.NETWORK_ERROR;
                break;
            case NO_AD_FOUND:
            case VIDEO_UNAVAILABLE:
                code = ResultCode.UNABLE_TO_FILL;
                break;
            case INVALID_LOCATION:
                code = ResultCode.INVALID_REQUEST;
                break;
            case INTERNAL:
            case FIRST_SESSION_INTERSTITIALS_DISABLED:
            case WRONG_ORIENTATION:
            case SESSION_NOT_STARTED:
            case IMPRESSION_ALREADY_VISIBLE:
            case NO_HOST_ACTIVITY:
            case USER_CANCELLATION:
            case VIDEO_ID_MISSING:
            case ERROR_PLAYING_VIDEO:
            case INVALID_RESPONSE:
            case ASSETS_DOWNLOAD_FAILURE:
            case ERROR_CREATING_VIEW:
            case ERROR_DISPLAYING_VIEW:
            case INCOMPATIBLE_API_VERSION:
                code = ResultCode.INTERNAL_ERROR;
                break;
        }
        ChartboostListener l = listenerMap.get(location);
        if (l != null) {
            l.didFailToLoadInterstitial(location, code);
        }
    }

    @Override
    public void didDismissInterstitial(String location) {
        super.didDismissInterstitial(location);
        ChartboostListener l = listenerMap.get(location);
        if (l != null) {
            l.didDismissInterstitial(location);
        }
    }

    @Override
    public void didCloseInterstitial(String location) {
        super.didCloseInterstitial(location);
        ChartboostListener l = listenerMap.get(location);
        if (l != null) {
            l.didCloseInterstitial(location);
        }
    }

    @Override
    public void didClickInterstitial(String location) {
        super.didClickInterstitial(location);
        ChartboostListener l = listenerMap.get(location);
        if (l != null) {
            l.didClickInterstitial(location);
        }

    }

    public interface ChartboostListener {
        public void didCacheInterstitial(String location);

        public void didFailToLoadInterstitial(String location, ResultCode errorCode);

        public void didDismissInterstitial(String location);

        public void didCloseInterstitial(String location);

        public void didClickInterstitial(String location);
    }
}
