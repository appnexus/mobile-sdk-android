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

import android.os.AsyncTask;
import android.os.Build;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;

public abstract class MediatedAdViewController implements Displayable {

    public static enum RESULT {
        SUCCESS,
        INVALID_REQUEST,
        UNABLE_TO_FILL,
        MEDIATED_SDK_UNAVAILABLE,
        NETWORK_ERROR,
        INTERNAL_ERROR
    }

    boolean failed = false;
    Class<?> c;
    MediatedAdView mAV;
    AdRequester requester;
    MediatedAd currentAd;
    AdViewListener listener;

    protected boolean errorCBMade = false;
    protected boolean successCBMade = false;

    //TODO: may be unnecessary
    private boolean noMoreAds = false;

    protected MediatedAdViewController(AdRequester requester, MediatedAd currentAd, AdViewListener listener) {
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

    /**
     * Validates all fields necessary for controller to function properly
     *
     * @param callerClass the calling class that mAV should be an instance of
     * @return true if the controller is valid, false if not.
     */
    protected boolean isValid(Class callerClass) {
        if (failed) {
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
     *  Attempts to instantiate currentAd
     *
     * @return true if instantiation was successful, false if not.
     */
    private boolean instantiateNewMediatedAd() {
        Clog.d(Clog.mediationLogTag, Clog.getString(
                R.string.instantiating_class, currentAd.getClassName()));

        try {
            c = Class.forName(currentAd.getClassName());
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

    public void onAdLoaded() {
        if (listener != null)
            listener.onAdLoaded(this);
        if (!successCBMade) {
            successCBMade = true;
            fireResultCB(RESULT.SUCCESS);
        }
    }

    public void onAdFailed(MediatedAdViewController.RESULT reason) {
        this.failed = true;
        if (listener != null)
            listener.onAdFailed(noMoreAds);

        if (!errorCBMade) {
            fireResultCB(reason);
            errorCBMade = true;
        }
    }

    public void onAdExpanded() {
        if (listener != null)
            listener.onAdExpanded();
    }

    public void onAdCollapsed() {
        if (listener != null)
            listener.onAdCollapsed();
    }

    public void onAdClicked() {
        if (listener != null)
            listener.onAdClicked();
    }

    @Override
    public boolean failed() {
        return failed;
    }

    private void fireResultCB(final RESULT result) {

        // if resultCB is empty don't fire resultCB, and just continue to next ad
        if ((currentAd == null) || (currentAd.getResultCB() == null) || currentAd.getResultCB().isEmpty()) {
            Clog.w(Clog.mediationLogTag, Clog.getString(R.string.fire_cb_result_null));

            // just making sure
            if (requester == null) {
                Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.fire_cb_requester_null));
                return;
            }

            requester.dispatchResponse(null);
            return;
        }
        final String resultCB = currentAd.getResultCB();

        //fire call to result cb url
        HTTPGet<Void, Void, HTTPResponse> cb = new HTTPGet<Void, Void, HTTPResponse>() {
            @Override
            protected void onPostExecute(HTTPResponse httpResponse) {
                if (requester == null) {
                    Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.fire_cb_requester_null));
                    return;
                }
                AdResponse response = null;
                if ((httpResponse != null) && httpResponse.getSucceeded()) {
                    response = new AdResponse(httpResponse.getResponseBody(), httpResponse.getHeaders());
                }
                else {
                    Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.result_cb_bad_response));
                }

                // if this was the result of a successful ad, stop looking for more ads
                if (successCBMade)
                    return;

                requester.dispatchResponse(response);
            }

            @Override
            protected String getUrl() {
                return resultCB + "&reason=" + result.ordinal();
            }
        };

        // Spawn GET call
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            cb.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            cb.execute();
        }
    }
}
