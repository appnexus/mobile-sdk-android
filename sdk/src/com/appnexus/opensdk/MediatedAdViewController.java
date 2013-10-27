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

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.Settings;

public abstract class MediatedAdViewController implements Displayable {

    public static enum RESULT {
        SUCCESS,
        INVALID_REQUEST,
        UNABLE_TO_FILL,
        MEDIATED_SDK_UNAVAILABLE,
        NETWORK_ERROR,
        INTERNAL_ERROR
    }


    MediatedAdView mAV;
    private AdRequester requester;
    MediatedAd currentAd;
    private AdViewListener listener;

    private boolean hasFailed = false;
    private boolean hasSucceeded = false;

    MediatedAdViewController(AdRequester requester, MediatedAd currentAd, AdViewListener listener) {
        this.requester = requester;
        this.listener = listener;
        this.currentAd = currentAd;

        RESULT errorCode = null;

        if (currentAd == null) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_no_ads));
            errorCode = RESULT.UNABLE_TO_FILL;
        } else {
            boolean instantiateSuccessful = instantiateNewMediatedAd();
            if (!instantiateSuccessful)
                errorCode = RESULT.MEDIATED_SDK_UNAVAILABLE;
        }

        if (errorCode != null)
            onAdFailed(errorCode);
    }

    /*
    internal methods
     */

    /**
     * Validates all fields necessary for controller to function properly
     *
     * @param callerClass the calling class that mAV should be an instance of
     * @return true if the controller is valid, false if not.
     */
    boolean isValid(Class callerClass) {
        if (hasFailed) {
            return false;
        }
        if (currentAd == null) {
            onAdFailed(RESULT.UNABLE_TO_FILL);
            return false;
        }
        if ((mAV == null) || (callerClass == null) || !callerClass.isInstance(mAV)) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.instance_exception,
                    callerClass != null ? callerClass.getCanonicalName() : "null"));
            onAdFailed(RESULT.MEDIATED_SDK_UNAVAILABLE);
            return false;
        }

        return true;
    }

    /**
     * Attempts to instantiate currentAd
     *
     * @return true if instantiation was successful, false if not.
     */
    private boolean instantiateNewMediatedAd() {
        Clog.d(Clog.mediationLogTag, Clog.getString(
                R.string.instantiating_class, currentAd.getClassName()));

        try {
            Class<?> c = Class.forName(currentAd.getClassName());
            mAV = (MediatedAdView) c.newInstance();
            // exceptions will skip down to return false
            return true;
        } catch (ClassNotFoundException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.class_not_found_exception));
        } catch (InstantiationException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.instantiation_exception));
        } catch (IllegalAccessException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.illegal_access_exception));
        } catch (ClassCastException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.class_cast_exception));
        }
        return false;
    }

    private void finishController() {
        mAV = null;
        requester = null;
        currentAd = null;
        listener = null;
        Clog.d(Clog.mediationLogTag, Clog.getString(R.string.mediation_finish));
    }

    /*
    Public methods that the mediated network can call,
    which will alert the registered AdViewListener
     */

    public void onAdLoaded() {
        if (hasSucceeded || hasFailed) return;
        cancelTimeout();
        hasSucceeded = true;

        if (listener != null)
            listener.onAdLoaded(this);
        fireResultCB(RESULT.SUCCESS);
    }

    public void onAdFailed(MediatedAdViewController.RESULT reason) {
        if (hasSucceeded || hasFailed) return;
        cancelTimeout();

        if (listener != null)
            listener.onAdFailed(false);
        fireResultCB(reason);
        finishController();
        hasFailed = true;
    }

    public void onAdExpanded() {
        if (hasFailed) return;
        if (listener != null)
            listener.onAdExpanded();
    }

    public void onAdCollapsed() {
        if (hasFailed) return;
        if (listener != null)
            listener.onAdCollapsed();
    }

    public void onAdClicked() {
        if (hasFailed) return;
        if (listener != null)
            listener.onAdClicked();
    }

    /*
    Overridden Displayable methods
     */

    @Override
    public boolean failed() {
        return hasFailed;
    }

    @Override
    public void destroy() {
        finishController();
    }

    /*
     Result CB Code
     */

    private void fireResultCB(final RESULT result) {
        if (hasFailed) return;

        // if resultCB is empty don't fire resultCB, and just continue to next ad
        if ((currentAd == null) || (currentAd.getResultCB() == null) || currentAd.getResultCB().isEmpty()) {
            Clog.w(Clog.mediationLogTag, Clog.getString(R.string.fire_cb_result_null));

            // just making sure
            if (requester == null) {
                Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.fire_cb_requester_null));
                return;
            }

            requester.onReceiveResponse(null);
            return;
        }

        //fire call to result cb url
        ResultCBRequest cb = new ResultCBRequest(requester, currentAd.getResultCB(), result);

        // Spawn GET call
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            cb.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            cb.execute();
        }
    }

    private class ResultCBRequest extends HTTPGet<Void, Void, HTTPResponse> {
        final AdRequester requester;
        private final String resultCB;
        final RESULT result;

        private ResultCBRequest(AdRequester requester, String resultCB, RESULT result) {
            this.requester = requester;
            this.resultCB = resultCB;
            this.result = result;
        }

        @Override
        protected void onPostExecute(HTTPResponse httpResponse) {
            if (this.requester == null) {
                Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.fire_cb_requester_null));
                return;
            }
            AdResponse response = null;
            if ((httpResponse != null) && httpResponse.getSucceeded()) {
                response = new AdResponse(httpResponse.getResponseBody(), httpResponse.getHeaders());
            } else {
                Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.result_cb_bad_response));
            }

            // if this was the result of a successful ad, stop looking for more ads
            if (this.result == RESULT.SUCCESS)
                return;

            this.requester.onReceiveResponse(response);
        }

        @Override
        protected String getUrl() {
            // create the resultCB request
            StringBuilder sb = new StringBuilder(this.resultCB);
            sb.append("&reason=").append(this.result.ordinal());
            // append the hashes of the device ID from settings
            sb.append("&md5udid=").append(Uri.encode(Settings.getSettings().hidmd5));
            sb.append("&sha1udid=").append(Uri.encode(Settings.getSettings().hidsha1));
            return sb.toString();
        }
    }

    /*
     Timeout handler code
     */

    void startTimeout() {
        if (hasSucceeded || hasFailed) return;
        timeoutHandler.sendEmptyMessageDelayed(0, Settings.getSettings().MEDIATED_NETWORK_TIMEOUT);
    }

    void cancelTimeout() {
        timeoutHandler.removeMessages(0);
    }

    // if the mediated network fails to call us within the timeout period, fail
    private final Handler timeoutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (hasFailed) return;
            Clog.w(Clog.mediationLogTag, Clog.getString(R.string.mediation_timeout));
            onAdFailed(RESULT.INTERNAL_ERROR);
        }
    };

}
