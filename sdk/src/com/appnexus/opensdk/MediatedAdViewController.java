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

import android.os.Handler;
import android.os.Message;

import com.appnexus.opensdk.ut.UTAdRequester;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;
import com.appnexus.opensdk.ut.adresponse.CSMSDKAdResponse;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
    protected MediaType mediaType;
    protected MediatedAdView mAV;
    private WeakReference<UTAdRequester> caller_requester;
    protected CSMSDKAdResponse currentAd;
    protected AdDispatcher listener;
    protected MediatedDisplayable mediatedDisplayable = new MediatedDisplayable(this);

    boolean hasFailed = false;
    boolean hasSucceeded = false;
    protected boolean destroyed = false;

    MediatedAdViewController(UTAdRequester requester, CSMSDKAdResponse currentAd, AdDispatcher listener, MediaType type) {
        this.caller_requester = new WeakReference<UTAdRequester>(requester);
        this.currentAd = currentAd;
        this.listener = listener;
        this.mediaType = type;

        ResultCode errorCode = null;

        if (currentAd == null) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_no_ads));
            errorCode = ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL);
        } else {
            boolean instantiateSuccessful = instantiateNewMediatedAd();
            if (!instantiateSuccessful)
                errorCode = ResultCode.getNewInstance(ResultCode.MEDIATED_SDK_UNAVAILABLE);
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
     * @return <code>true</code> if the controller is valid,
     * <code>false</code> otherwise.
     */
    @SuppressWarnings("rawtypes")
    boolean isValid(Class callerClass) {
        if (hasFailed) {
            return false;
        }
        if ((mAV == null) || (callerClass == null) || !callerClass.isInstance(mAV)) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.instance_exception,
                    callerClass != null ? callerClass.getCanonicalName() : "null"));
            onAdFailed(ResultCode.getNewInstance(ResultCode.MEDIATED_SDK_UNAVAILABLE));
            return false;
        }

        return true;
    }

    protected TargetingParameters getTargetingParameters() {
        UTAdRequester requester = this.caller_requester.get();
        TargetingParameters tp = null;
        if (requester != null && requester.getRequestParams() != null) {
            tp = requester.getRequestParams().getTargetingParameters();
        }
        if (tp == null) {
            tp = new TargetingParameters();
        }
        return tp;
    }

    /**
     * Attempts to instantiate the currentAd.
     *
     * @return <code>true</code> if instantiation was successful,
     * <code>false</code> otherwise.
     */
    private boolean instantiateNewMediatedAd() {
        Clog.d(Clog.mediationLogTag, Clog.getString(
                R.string.instantiating_class, currentAd.getClassName()));

        try {
            String className = currentAd.getClassName();
            String intermediaryAdaptorClassName = Settings.getSettings().externalMediationClasses.get(className);
            Class<?> c;

            if (StringUtil.isEmpty(intermediaryAdaptorClassName)) {
                c = Class.forName(className);
                mAV = (MediatedAdView) c.newInstance();
            } else {
                c = Class.forName(intermediaryAdaptorClassName);
                Constructor<?> constructor = c.getConstructor(String.class);
                mAV = (MediatedAdView) constructor.newInstance(className);
            }

            // exceptions will skip down to return false
            return true;
        } catch (ClassNotFoundException e) {
            // exception in Class.forName
            handleInstantiationFailure(e, currentAd.getClassName());
        } catch (LinkageError e) {
            // error in Class.forName
            // also catches subclass ExceptionInInitializerError
            handleInstantiationFailure(e, currentAd.getClassName());
        } catch (InstantiationException e) {
            // exception in Class.newInstance
            handleInstantiationFailure(e, currentAd.getClassName());
        } catch (IllegalAccessException e) {
            // exception in Class.newInstance
            handleInstantiationFailure(e, currentAd.getClassName());
        } catch (ClassCastException e) {
            // exception in object cast
            handleInstantiationFailure(e, currentAd.getClassName());
        } catch (NoSuchMethodException e) {
            // exception in Class.getConstructor
            // intermediary adaptor case
            handleInstantiationFailure(e, currentAd.getClassName());
        } catch (InvocationTargetException e) {
            // exception in Constructor.newInstance
            // intermediary adaptor case
            handleInstantiationFailure(e, currentAd.getClassName());
        }
        return false;
    }

    // Accepts both Exceptions and Errors
    private void handleInstantiationFailure(Throwable throwable, String className) {
        Clog.e(Clog.mediationLogTag,
                Clog.getString(R.string.mediation_instantiation_failure,
                        throwable.getClass().getSimpleName()));
        if (!StringUtil.isEmpty(className)) {
            Clog.w(Clog.mediationLogTag, String.format("Adding %s to invalid networks list", className));
            Settings.getSettings().addInvalidNetwork(mediaType, className);
        }
    }

    protected void finishController() {
        if (mAV != null) {
            mAV.destroy();
        }
        destroyed = true;
        mAV = null;
        Clog.d(Clog.mediationLogTag, Clog.getString(R.string.mediation_finish));
    }

    /**
     * Call this method to inform the AppNexus SDK that an ad from the
     * third-party SDK has successfully loaded.  This method should
     * only be called once per <code>requestAd</code> call (see the
     * implementations of <code>requestAd</code> for banners and
     * interstitials in {@link MediatedBannerAdView} and {@link
     * MediatedInterstitialAdView}).
     */
    public void onAdLoaded() {
        if (hasSucceeded || hasFailed || destroyed) return;
        markLatencyStop();
        cancelTimeout();
        hasSucceeded = true;
        fireResponseURL(currentAd.getResponseUrl(), ResultCode.getNewInstance(ResultCode.SUCCESS));
        UTAdRequester requester = this.caller_requester.get();
        if (requester != null) {
            requester.onReceiveAd(new AdResponse() {
                @Override
                public MediaType getMediaType() {
                    return mediaType;
                }

                @Override
                public boolean isMediated() {
                    return true;
                }

                @Override
                public Displayable getDisplayable() {
                    return mediatedDisplayable;
                }

                @Override
                public NativeAdResponse getNativeAdResponse() {
                    return null;
                }

                @Override
                public BaseAdResponse getResponseData() {
                    return currentAd;
                }

                @Override
                public void destroy() {
                    mediatedDisplayable.destroy();
                }
            });
        } else {
            mediatedDisplayable.destroy();
        }
    }

    abstract boolean isReady();

    abstract void show();

    /**
     * Call this method to inform the AppNexus SDK than an ad call
     * from the third-party SDK has failed to load an ad.  This method
     * should only be called once per <code>requestAd</code> call (see
     * the implementations of <code>requestAd</code> for banners and
     * interstitials in {@link MediatedBannerAdView} and {@link
     * MediatedInterstitialAdView}).
     *
     * @param reason The reason why the ad call from the third-party
     *               SDK failed.
     */
    public void onAdFailed(ResultCode reason) {
        if (hasSucceeded || hasFailed || destroyed) return;
        markLatencyStop();
        cancelTimeout();
        if (currentAd != null && currentAd.getResponseUrl() != null) {
            fireResponseURL(currentAd.getResponseUrl(), reason);
        }
        hasFailed = true;
        finishController();
        UTAdRequester requester = this.caller_requester.get();
        if (requester != null) {
            requester.continueWaterfall(reason);
        }
    }

    /**
     * Call this method to inform the AppNexus SDK that the ad has
     * expanded from its original size.  This is usually due to the
     * user interacting with an expanding
     * <a href="http://www.iab.net/mraid">MRAID</a> ad.
     */
    public void onAdExpanded() {
        if (hasFailed || destroyed) return;
        if (listener != null)
            listener.onAdExpanded();
    }

    /**
     * Call this method to inform the AppNexus SDK that a previously
     * expanded ad has now collapsed to its original size.
     */
    public void onAdCollapsed() {
        if (hasFailed || destroyed) return;
        if (listener != null)
            listener.onAdCollapsed();
    }

    /**
     * Call this method to inform the the AppNexus SDK that the user
     * is interacting with the ad (i.e., has clicked on it).
     */
    public void onAdClicked() {
        if (hasFailed || destroyed) return;
        if (listener != null)
            listener.onAdClicked();
    }

    /**
     * Call this method to inform the AppNexus SDK that
     *  ad impression has been observed.
     */
    public void onAdImpression() {
        if (hasFailed || destroyed) return;
        if (listener != null)
            listener.onAdImpression();
    }

    private void fireResponseURL(final String responseURL, final ResultCode result) {

        if ((responseURL == null) || StringUtil.isEmpty(responseURL)) {
            Clog.w(Clog.mediationLogTag, Clog.getString(R.string.fire_responseurl_null));
            return;
        }
        ResponseUrl responseUrl = new ResponseUrl.Builder(responseURL, result)
                .latency(getLatencyParam())
                .build();
        responseUrl.execute();
    }

    /*
     Timeout handler code
     */

    void startTimeout() {
        if (hasSucceeded || hasFailed) return;
        timeoutHandler.sendEmptyMessageDelayed(0, currentAd.getNetworkTimeout());
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
            try {
                avc.onAdFailed(ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR));
            } catch (IllegalArgumentException e) {
                // catch exception for unregisterReceiver() when destroying views
            } finally {
                avc.listener = null;
                avc.mAV = null;
                avc.currentAd = null;
            }
        }
    }

    // if the mediated network fails to call us within the timeout period, fail
    private final Handler timeoutHandler = new TimeoutHandler(this);

    /*
    Measuring Latency functions
     */

    // variables for measuring latency.
    private long latencyStart = -1, latencyStop = -1;

    /**
     * Should be called immediately after mediated SDK returns
     * from `requestAd` call.
     */
    protected void markLatencyStart() {
        latencyStart = System.currentTimeMillis();
    }

    /**
     * Should be called immediately after mediated SDK
     * calls either of `onAdLoaded` or `onAdFailed`.
     */
    protected void markLatencyStop() {
        latencyStop = System.currentTimeMillis();
    }

    /**
     * The latency of the call to the mediated SDK.
     *
     * @return the mediated SDK latency, -1 if `latencyStart`
     * or `latencyStop` not set.
     */
    private long getLatencyParam() {
        if ((latencyStart > 0) && (latencyStop > 0)) {
            return (latencyStop - latencyStart);
        }
        // return -1 if invalid.
        return -1;
    }

    //Forwarded from the activity holding the AdView
    abstract public void onDestroy();

    abstract public void onPause();

    abstract public void onResume();

    protected boolean hasCancelled = false;

    /**
     * Cancel mediated native ad request
     */
    protected void cancel(boolean hasCancelled) {
        this.hasCancelled = hasCancelled;
        if (hasCancelled) {
            finishController();
        }
    }
}
