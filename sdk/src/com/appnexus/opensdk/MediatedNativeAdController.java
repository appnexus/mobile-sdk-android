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

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.RejectedExecutionException;

public class MediatedNativeAdController {
    WeakReference<AdRequester> requester;
    MediatedAd currentAd;

    boolean hasSucceeded = false;
    boolean hasFailed = false;
    private boolean hasCancelled = false;

    ResultCode errorCode;


    public static MediatedNativeAdController create(MediatedAd currentAd, AdRequester requester){
           return new MediatedNativeAdController(currentAd, requester);
    }

    private MediatedNativeAdController(MediatedAd currentAd, AdRequester requester) {
        if (currentAd == null) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_no_ads));
            errorCode = ResultCode.UNABLE_TO_FILL;
        } else {
            Clog.d(Clog.mediationLogTag, Clog.getString(
                    R.string.instantiating_class, currentAd.getClassName()));

            this.requester = new WeakReference<AdRequester>(requester);
            this.currentAd = currentAd;

            startTimeout();
            markLatencyStart();

            try {
                Class<?> c = Class.forName(currentAd.getClassName());
                MediatedNativeAd ad = (MediatedNativeAd) c.newInstance();
                if (requester.getRequestParams() != null) {
                    ad.requestNativeAd(
                            requester.getRequestParams().getContext(),
                            currentAd.getId(), this,
                            requester.getRequestParams().getTargetingParameters());
                } else {
                    errorCode = ResultCode.INVALID_REQUEST;
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
        errorCode = ResultCode.MEDIATED_SDK_UNAVAILABLE;
    }

    protected void finishController() {
        currentAd = null;
        Clog.d(Clog.mediationLogTag, Clog.getString(R.string.mediation_finish));
    }

    /**
     * Call this method to inform the AppNexus SDK that an ad from the
     * third-party SDK has successfully loaded.  This method should
     * only be called once per <code>requestAd</code> call (see the
     * implementations of <code>requestAd</code> for native ad
     * in {@link MediatedNativeAd}.
     *
     */
    public void onAdLoaded(final NativeAdResponse response) {
        if (hasSucceeded || hasFailed) return;
        markLatencyStop();
        cancelTimeout();
        hasSucceeded = true;

        AdRequester requester = this.requester.get();
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
            });
        } else {
            Clog.d(Clog.mediationLogTag, "Request was cancelled, destroy mediated ad response");
            response.destroy();
        }
        currentAd = null;
        fireResultCB(ResultCode.SUCCESS);
    }

    /**
     * Call this method to inform the AppNexus SDK than an ad call
     * from the third-party SDK has failed to load an ad.  This method
     * should only be called once per <code>requestAd</code> call (see
     * the implementations of <code>requestAd</code> for native ad
     * in {@link MediatedNativeAd}.
     *
     * @param reason The reason why the ad call from the third-party
     * SDK failed.
     */
    public void onAdFailed(ResultCode reason) {
        if (hasSucceeded || hasFailed) return;
        markLatencyStop();
        cancelTimeout();

        // don't call the listener here. the requester will call the listener
        // at the end of the waterfall
        fireResultCB(reason);
        hasFailed = true;
        finishController();
    }


    /*
     Result CB Code
     */
    @SuppressLint({ "InlinedApi", "NewApi" }) /* suppress AsyncTask.THREAD_POOL_EXECUTOR warning for < HONEYCOMB */
    private void fireResultCB(final ResultCode result) {
        if (hasFailed) return;

        AdRequester requester = this.requester.get();
        // if resultCB is empty don't fire resultCB, and just continue to next ad
        if ((currentAd == null) || StringUtil.isEmpty(currentAd.getResultCB())) {
            if(result == ResultCode.SUCCESS) return;
            Clog.w(Clog.mediationLogTag, Clog.getString(R.string.fire_cb_result_null));
            // just making sure
            if (requester == null) {
                Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.fire_cb_requester_null));
                return;
            }
            requester.onReceiveServerResponse(null);
            return;
        }

        boolean ignoreResult = false; // default is to not ignore
        if ((requester != null)
                && (requester.getMediatedAds() != null)) {
            // ignore resultCB except on the last mediated ad
            ignoreResult = requester.getMediatedAds().size() > 0;
        }

        // ignore resultCB if succeeded already
        if (result == ResultCode.SUCCESS) {
            ignoreResult = true;
        }

        //fire call to result cb url
        ResultCBRequest cb = new ResultCBRequest(requester,
                currentAd.getResultCB(), result,
                currentAd.getExtras(), ignoreResult,
                getLatencyParam(), getTotalLatencyParam(requester));

        // Spawn GET call
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                cb.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                cb.execute();
            }
        } catch (RejectedExecutionException rejectedExecutionException) {
            Clog.e(Clog.baseLogTag, "Concurrent Thread Exception while firing ResultCB: "
                    + rejectedExecutionException.getMessage());
        } catch (Exception exception) {
            Clog.e(Clog.baseLogTag, "Exception while firing ResultCB: " + exception.getMessage());
        }

        // if currentAd failed and next ad is available, continue to next ad
        if (ignoreResult && result != ResultCode.SUCCESS) {
            if (requester != null) {
                requester.onReceiveServerResponse(null);
            }
        }

    }

    private class ResultCBRequest extends HTTPGet {
        WeakReference<AdRequester> requester;
        private final String resultCB;
        final ResultCode result;
        private final HashMap<String, Object> extras;
        private final boolean ignoreResult;
        private final long latency;
        private final long totalLatency;

        private ResultCBRequest(AdRequester requester, String resultCB, ResultCode result,
                                HashMap<String, Object> extras, boolean ignoreResult,
                                long latency, long totalLatency) {
            this.requester = new WeakReference<AdRequester>(requester);
            this.resultCB = resultCB;
            this.result = result;
            this.extras = extras;
            this.ignoreResult = ignoreResult;
            this.latency = latency;
            this.totalLatency = totalLatency;
        }

        @Override
        protected void onPostExecute(HTTPResponse httpResponse) {
            if (this.ignoreResult) {
                Clog.i(Clog.httpRespLogTag, Clog.getString(R.string.result_cb_ignored));
                return;
            }
            AdRequester requester = this.requester.get();
            if (requester == null) {
                Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.fire_cb_requester_null));
                return;
            }

            ServerResponse response = null;
            if ((httpResponse != null) && httpResponse.getSucceeded()) {
                response = new ServerResponse(httpResponse, MediaType.NATIVE);
                if (extras.containsKey(ServerResponse.EXTRAS_KEY_ORIENTATION)) {
                    response.addToExtras(ServerResponse.EXTRAS_KEY_ORIENTATION,
                            extras.get(ServerResponse.EXTRAS_KEY_ORIENTATION));
                }
            } else {
                Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.result_cb_bad_response));
            }

            requester.onReceiveServerResponse(response);
        }

        @Override
        protected String getUrl() {
            // create the resultCB request
            StringBuilder sb = new StringBuilder(this.resultCB);
            sb.append("&reason=").append(this.result.ordinal());
            // append the hashes of the device ID from settings
            if (!StringUtil.isEmpty(Settings.getSettings().aaid)) {
                sb.append("&aaid=").append(Uri.encode(Settings.getSettings().aaid));
            } else {
                sb.append("&md5udid=").append(Uri.encode(Settings.getSettings().hidmd5));
                sb.append("&sha1udid=").append(Uri.encode(Settings.getSettings().hidsha1));
            }

            if (latency > 0) {
                sb.append("&latency=").append(Uri.encode(String.valueOf(latency)));
            }
            if (totalLatency > 0) {
                sb.append("&total_latency=").append(Uri.encode(String.valueOf(totalLatency)));
            }

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
                nac.onAdFailed(ResultCode.INTERNAL_ERROR);
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
     * The running total latency of the ad call.
     * @return the running total latency, -1 if `latencyStop` not set.
     */
    private long getTotalLatencyParam(AdRequester requester) {
        if ((requester != null) && (latencyStop > 0)) {
            return requester.getLatency(latencyStop);
        }
        // return -1 if invalid.
        return -1;
    }

    /**
     * Cancel mediated native ad request
     */
    void cancel(boolean hasCancelled) {
        this.hasCancelled = hasCancelled;
        if (hasCancelled) {
            finishController();
        }
    }
}

