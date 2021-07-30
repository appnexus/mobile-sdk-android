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

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.appnexus.opensdk.ut.UTAdRequester;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;
import com.appnexus.opensdk.ut.adresponse.CSMSDKAdResponse;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MediatedNativeAdController {
    NativeAdEventListener listener;
    WeakReference<UTAdRequester> requester;
    WeakReference<Context> contextWeakReference;
    private WeakReference<BaseNativeAdResponse> response;
    CSMSDKAdResponse currentAd;

    boolean hasSucceeded = false;
    boolean hasFailed = false;
    private boolean hasCancelled = false;

    ResultCode errorCode;


    public static MediatedNativeAdController create(CSMSDKAdResponse currentAd, UTAdRequester requester) {
        return new MediatedNativeAdController(currentAd, requester);
    }

    private MediatedNativeAdController(CSMSDKAdResponse currentAd, UTAdRequester requester) {
        if (currentAd == null) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_no_ads));
            errorCode = ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL);
        } else {
            Clog.d(Clog.mediationLogTag, Clog.getString(
                    R.string.instantiating_class, currentAd.getClassName()));

            this.requester = new WeakReference<UTAdRequester>(requester);
            this.currentAd = currentAd;
            this.contextWeakReference = new WeakReference<Context>(requester.getRequestParams().getContext());

            startTimeout();
            markLatencyStart();

            try {
                Class<?> c = Class.forName(currentAd.getClassName());
                MediatedNativeAd ad = (MediatedNativeAd) c.newInstance();
                if (requester.getRequestParams() != null) {
                    ad.requestNativeAd(
                            requester.getRequestParams().getContext(),
                            currentAd.getParam(),
                            currentAd.getId(), this,
                            requester.getRequestParams().getTargetingParameters());
                } else {
                    errorCode = ResultCode.getNewInstance(ResultCode.INVALID_REQUEST);
                }
            } catch (ClassNotFoundException e) {
                // exception in Class.forName
                handleInstantiationFailure(e, currentAd.getClassName());
            } catch (ClassCastException e) {
                // exception in casting instance to MediatedNativeAd
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
            } catch (Exception e) {
                Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_request_exception), e);
                errorCode = ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR);
            } catch (Error e) {
                // catch errors. exceptions will be caught above.
                Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_request_error), e);
                errorCode = ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR);
            }
        }
        if (errorCode != null) {
            onAdFailed(errorCode);
        }
    }

    private void handleInstantiationFailure(Throwable throwable, String className) {
        Clog.e(Clog.mediationLogTag,
                Clog.getString(R.string.mediation_instantiation_failure,
                        throwable.getClass().getSimpleName()));
        if (!StringUtil.isEmpty(className)) {
            Clog.w(Clog.mediationLogTag, String.format("Adding %s to invalid networks list", className));
            Settings.getSettings().addInvalidNetwork(MediaType.NATIVE, className);
        }
        errorCode = ResultCode.getNewInstance(ResultCode.MEDIATED_SDK_UNAVAILABLE);
    }

    /**
     * Call this method to inform the AppNexus SDK that an ad from the
     * third-party SDK has successfully loaded.  This method should
     * only be called once per <code>requestAd</code> call (see the
     * implementations of <code>requestAd</code> for native ad
     * in {@link MediatedNativeAd}.
     */
    public void onAdLoaded(final NativeAdResponse response) {
        if (hasSucceeded || hasFailed) return;
        markLatencyStop();
        cancelTimeout();
        hasSucceeded = true;
        fireResponseURL(currentAd.getResponseUrl(), ResultCode.getNewInstance(ResultCode.SUCCESS));
        UTAdRequester requester = this.requester.get();


        // Create an OMID Related objects.
        ((BaseNativeAdResponse) response).setANVerificationScriptResources(currentAd.getAdObject());
        this.response = new WeakReference<>((BaseNativeAdResponse) response);

        if (requester != null) {
            requester.onReceiveAd(new AdResponse() {
                @Override
                public MediaType getMediaType() {
                    return MediaType.NATIVE;
                }

                @Override
                public boolean isMediated() {
                    return true;
                }

                @Override
                public Displayable getDisplayable() {
                    return null;
                }

                @Override
                public NativeAdResponse getNativeAdResponse() {
                    return response;
                }

                @Override
                public void destroy() {
                    response.destroy();
                }

                @Override
                public BaseAdResponse getResponseData() {
                    return currentAd;
                }
            });
        } else {
            Clog.d(Clog.mediationLogTag, "Request was cancelled, destroy mediated ad response");
            response.destroy();
        }

    }

    /**
     * Call this method to inform the AppNexus SDK than an ad call
     * from the third-party SDK has failed to load an ad.  This method
     * should only be called once per <code>requestAd</code> call (see
     * the implementations of <code>requestAd</code> for native ad
     * in {@link MediatedNativeAd}.
     *
     * @param reason The reason why the ad call from the third-party
     *               SDK failed.
     */
    public void onAdFailed(ResultCode reason) {
        if (hasSucceeded || hasFailed) return;
        markLatencyStop();
        cancelTimeout();

        fireResponseURL(currentAd.getResponseUrl(), reason);
        hasFailed = true;

        // don't call the listener here. the requester will call the listener
        // at the end of the waterfall
        UTAdRequester requester = this.requester.get();
        if (requester != null) {
            requester.continueWaterfall(reason);
        }
    }

    /**
     * Call this method to inform the AppNexus SDK that an impression has been recorded on the Mediated Native Ad.
     * This is essential for AppNexus SDK to fire impression tracker correctly.
     */
    public void onAdImpression(NativeAdEventListener listener) {
        this.listener = listener;
        fireImpressionTracker();

        // Fire the impression event to OMID
        BaseNativeAdResponse response = this.response.get();
        if (response != null) {
            response.anOmidAdSession.fireImpression();
        }
    }


    private void fireResponseURL(final String responseURL, final ResultCode result) {
        final UTAdRequester requester = this.requester.get();
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
        WeakReference<MediatedNativeAdController> mnac;

        public TimeoutHandler(MediatedNativeAdController mnac) {
            this.mnac = new WeakReference<MediatedNativeAdController>(mnac);
        }

        @Override
        public void handleMessage(Message msg) {
            MediatedNativeAdController nac = mnac.get();

            if (nac == null || nac.hasFailed) return;
            Clog.w(Clog.mediationLogTag, Clog.getString(R.string.mediation_timeout));
            try {
                nac.onAdFailed(ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR));
            } catch (IllegalArgumentException e) {
                // catch exception for unregisterReceiver() of destroy() call
            } finally {
                nac.currentAd = null;
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

    /**
     * Cancel mediated native ad request
     */
    void cancel(boolean hasCancelled) {
        this.hasCancelled = hasCancelled;
        /*if (hasCancelled) {
            finishController();
        }*/
    }


    void fireImpressionTracker() {
        ArrayList<String> impressionTrackers = currentAd.getImpressionURLs();
        // Just to be fail safe since we are making it to null below to mark it as beign used.
        if (impressionTrackers != null) {
            Context context = this.contextWeakReference.get();
            // If not connected to network and impression trackerlist size is non zero queue them to be fired in future.
            if (context != null && !SharedNetworkManager.getInstance(context).isConnected(context) && impressionTrackers.size() > 0) {
                SharedNetworkManager nm = SharedNetworkManager.getInstance(context);
                for (String url : impressionTrackers) {
                    nm.addURL(url, context, new ImpressionTrackerListener() {
                        @Override
                        public void onImpressionTrackerFired() {
                            if (listener != null) {
                                listener.onAdImpression();
                            }
                        }
                    });
                }
            }
            // else for all other cases when impression trackerlist size in non zero fire the trackers
            else if (impressionTrackers.size() > 0) {
                for (String url : impressionTrackers) {
                    fireTracker(url);
                }
            }
            // Reset impression URL once we have either fired them all or added thme to SharedNewtorkManager Queue.
            currentAd.setImpressionURLs(null);
        }
    }


    void fireTracker(final String trackerUrl) {
        HTTPGet tracker = new HTTPGet() {
            @Override
            protected void onPostExecute(HTTPResponse response) {
                if (response != null && response.getSucceeded()) {
                    Clog.d(Clog.baseLogTag, "Mediated Native Impression Tracked successfully");
                    if (listener != null) {
                        listener.onAdImpression();
                    }
                }
            }

            @Override
            protected String getUrl() {
                return trackerUrl;
            }
        };
        tracker.execute();

    }

}
