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

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import com.appnexus.opensdk.utils.*;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * <p>
 * This is the base class for the AppNexus SDK's mediation
 * controllers.  It's used to implement external or third party SDK
 * mediation.  It determines where the AppNexus SDK calls into other
 * SDKs when commanded to do so via the AppNexus Network Manager.
 * </p>
 * <p>
 * The mediation adaptor receives an object of this class and uses it
 * to inform the AppNexus SDK of events from the third party SDK.
 * </p>
 */

public abstract class MediatedAdViewController {

	/**
	 * This enum contains the results from mediation calls sent
	 * back to the AppNexus SDK in the <code>onAdFailed</code>
	 * method.
	 */
    public static enum RESULT {
    	/**
    	 * Return this if the ad loaded successfully.
    	 */
        SUCCESS,
        /**
         * Return this if the ad request parameters or placement ID
         * were invalid.
         */
        INVALID_REQUEST,
        /**
         * Return this if the mediated network did not return an ad in
         * this call.
         */
        UNABLE_TO_FILL,
        /**
         * Return this if the third-party SDK is not available.
         */
        MEDIATED_SDK_UNAVAILABLE,
        /**
         * Return this if a network error caused no ad to be returned.
         */
        NETWORK_ERROR,
        /**
         * Return this if an Internal error is detected in the
         * third-party SDK.
         */
        INTERNAL_ERROR
    }


    protected MediatedAdView mAV;
    private AdRequester requester;
    protected MediatedAd currentAd;
    private AdViewListener listener;
    protected MediatedDisplayable mediatedDisplayable = new MediatedDisplayable(this);

    boolean hasFailed = false;
    boolean hasSucceeded = false;

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
     * Validates all the fields necessary for the controller to
     * function properly.
     *
     * @param callerClass The calling class that mAV (the
     *                    MediatedAdView) should be an instance of.
     *
     * @return <code>true</code> if the controller is valid,
     *         <code>false</code> otherwise.
     */
    @SuppressWarnings("rawtypes")
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
     * Attempts to instantiate the currentAd.
     *
     * @return <code>true</code> if instantiation was successful,
     *         <code>false</code> otherwise.
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

    protected void finishController() {
        mAV = null;
        requester = null;
        currentAd = null;
        listener = null;
        Clog.d(Clog.mediationLogTag, Clog.getString(R.string.mediation_finish));
    }

    /**
     * Call this method to inform the AppNexus SDK that an ad from the
     * third-party SDK has successfully loaded.  This method should
     * only be called once per <code>requestAd</code> call (see the
     * implementations of <code>requestAd</code> for banners and
     * interstitials in {@link MediatedBannerAdView} and {@link
     * MediatedInterstitialAdView}).
     *
     */
    public void onAdLoaded() {
        if (hasSucceeded || hasFailed) return;
        cancelTimeout();
        hasSucceeded = true;

        if (listener != null)
            listener.onAdLoaded(mediatedDisplayable);
        fireResultCB(RESULT.SUCCESS);
    }

    /**
     * Call this method to inform the AppNexus SDK than an ad call
     * from the third-party SDK has failed to load an ad.  This method
     * should only be called once per <code>requestAd</code> call (see
     * the implementations of <code>requestAd</code> for banners and
     * interstitials in {@link MediatedBannerAdView} and {@link
     * MediatedInterstitialAdView}).
     *
     * @param reason The reason why the ad call from the third-party
     * SDK failed.
     */
    public void onAdFailed(MediatedAdViewController.RESULT reason) {
        if (hasSucceeded || hasFailed) return;
        cancelTimeout();

        if (listener != null)
            listener.onAdFailed(false);
        fireResultCB(reason);
        finishController();
        hasFailed = true;
    }

    /**
     * Call this method to inform the AppNexus SDK that the ad has
     * expanded from its original size.  This is usually due to the
     * user interacting with an expanding
     * <a href="http://www.iab.net/mraid">MRAID</a> ad.
     */
    public void onAdExpanded() {
        if (hasFailed) return;
        if (listener != null)
            listener.onAdExpanded();
    }

    /**
     * Call this method to inform the AppNexus SDK that a previously
     * expanded ad has now collapsed to its original size.
     */
    public void onAdCollapsed() {
        if (hasFailed) return;
        if (listener != null)
            listener.onAdCollapsed();
    }

    /**
     * Call this method to inform the the AppNexus SDK that the user
     * is interacting with the ad (i.e., has clicked on it).
     */
    public void onAdClicked() {
        if (hasFailed) return;
        if (listener != null)
            listener.onAdClicked();
    }

    /*
     Result CB Code
     */
    @SuppressLint({ "InlinedApi", "NewApi" }) /* suppress AsyncTask.THREAD_POOL_EXECUTOR warning for < HONEYCOMB */
	private void fireResultCB(final RESULT result) {
        if (hasFailed) return;


        // if resultCB is empty don't fire resultCB, and just continue to next ad
        if ((currentAd == null) || StringUtil.isEmpty(currentAd.getResultCB())) {
            if(result == RESULT.SUCCESS) return;
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
        ResultCBRequest cb = new ResultCBRequest(requester, currentAd.getResultCB(), result, currentAd.getExtras());

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
        private final HashMap<String, Object> extras;

        private ResultCBRequest(AdRequester requester, String resultCB, RESULT result, HashMap<String, Object> extras) {
            this.requester = requester;
            this.resultCB = resultCB;
            this.result = result;
            this.extras = extras;
        }

        @Override
        protected void onPostExecute(HTTPResponse httpResponse) {
            // if this was the result of a successful ad, ignore the response and stop looking for more ads
            if (this.result == RESULT.SUCCESS)
                return;

            if (this.requester == null) {
                Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.fire_cb_requester_null));
                return;
            }
            AdResponse response = null;
            if ((httpResponse != null) && httpResponse.getSucceeded()) {
                response = new AdResponse(httpResponse);
                if (extras.containsKey(AdResponse.EXTRAS_KEY_ORIENTATION)) {
                    response.addToExtras(AdResponse.EXTRAS_KEY_ORIENTATION,
                            extras.get(AdResponse.EXTRAS_KEY_ORIENTATION));
                }
            } else {
                Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.result_cb_bad_response));
            }

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
        timeoutHandler.sendEmptyMessageDelayed(0, Settings.MEDIATED_NETWORK_TIMEOUT);
    }

    void cancelTimeout() {
        timeoutHandler.removeMessages(0);
    }

    static class TimeoutHandler extends Handler {
        WeakReference<MediatedAdViewController> mavc;
        
        public TimeoutHandler(MediatedAdViewController mavc) {
            this.mavc = new WeakReference<MediatedAdViewController>(mavc);
        }
        
        @Override
        public void handleMessage(Message msg) {
            MediatedAdViewController avc = mavc.get();
            
            if (avc == null || avc.hasFailed) return;
            Clog.w(Clog.mediationLogTag, Clog.getString(R.string.mediation_timeout));
            avc.onAdFailed(RESULT.INTERNAL_ERROR);
        }
    }
    // if the mediated network fails to call us within the timeout period, fail
    private final Handler timeoutHandler = new TimeoutHandler(this);

}
