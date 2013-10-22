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
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Pair;
import com.appnexus.opensdk.InterstitialAdView.Size;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HashingFunctions;
import com.appnexus.opensdk.utils.Settings;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author jacob
 */
public class AdRequest extends AsyncTask<Void, Integer, AdResponse> {

    AdView owner;
    AdRequester requester;
    AdListener adListener;
    Context context;
    String hidmd5;
    String hidsha1;
    String devMake;
    String devModel;
    String carrier;
    String firstlaunch;
    String lat;
    String lon;
    String locDataAge;
    String locDataPrecision;
    String ua;
    String orientation;
    String allowedSizes;
    String mcc;
    String mnc;
    String connection_type;
    String dev_time; // Set at the time of the request
    String dev_timezone;
    String os;
    String language;
    String placementId;
    String nativeBrowser;
    String psa;
    int width = -1;
    int height = -1;
    int maxWidth = -1;
    int maxHeight = -1;
    boolean shouldRetry = true; // true by default
    float reserve = 0.00f;
    String age;
    String gender;
    ArrayList<Pair<String, String>> customKeywords;

    private final Handler retryHandler = new Handler();

    int httpRetriesLeft = 0;
    int blankRetriesLeft = 0;

    private static final AdResponse HTTP_ERROR
            = new AdResponse(true, false, false);
    private static final AdResponse CONNECTIVITY_RETRY
            = new AdResponse(false, true, false);
    private static final AdResponse BLANK_RETRY
            = new AdResponse(false, false, true);

    /**
     * Creates a new AdRequest with the given parameters
     *
     * @param requester       The instance of AdRequester which is filing this request.
     * @param aid             The ANDROID_ID to hash and pass.
     * @param lat             The lattitude to pass.
     * @param lon             The longistude to pass.
     * @param placementId     The AppNexus placement id to use
     * @param orientation     The device orientation to pass, 'portrait' or 'landscape'
     * @param carrier         The carrier to pass, such as 'AT&T'
     * @param width           The width to request, in pixels. -1 for none.
     * @param height          The height to request, in pixels. -1 for none.
     * @param maxWidth        The maximum width, if no width is specified.
     * @param maxHeight       The maximum height, if no height is specified.
     * @param mcc             The MCC to pass.
     * @param mnc             The MNC to pass
     * @param connectionType  The type of connection, 'wifi' or 'wan'
     * @param isNativeBrowser Whether this ad space will open the landing page in the native
     *                        browser ('1') or the in-app browser ('0').
     * @param adListener      The instance of AdListener to use.
     * @param shouldServePSAs Whether this ad space accepts PSAs ('1') or only wants ads
     *                        ('0')
     */
    public AdRequest(AdRequester requester, String aid, String lat, String lon,
                     String placementId, String orientation, String carrier, int width,
                     int height, int maxWidth, int maxHeight, String mcc, String mnc,
                     String connectionType, boolean isNativeBrowser,
                     AdListener adListener, boolean shouldServePSAs, boolean shouldRetry) {
        this.adListener = adListener;
        this.requester = requester;
        this.httpRetriesLeft = Settings.getSettings().MAX_CONNECTIVITY_RETRIES;
        this.blankRetriesLeft = Settings.getSettings().MAX_BLANK_RETRIES;
        if (aid != null) {
            hidmd5 = HashingFunctions.md5(aid);
            hidsha1 = HashingFunctions.sha1(aid);
        }
        devMake = Settings.getSettings().deviceMake;
        devModel = Settings.getSettings().deviceModel;

        // Get firstlaunch and convert it to a string
        firstlaunch = "" + Settings.getSettings().first_launch;
        // Get ua, the user agent...
        ua = Settings.getSettings().ua;

        this.lat = lat;
        this.lon = lon;

        this.carrier = carrier;

        this.mnc = mnc;
        this.mcc = mcc;

        this.width = width;
        this.height = height;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;

        this.connection_type = connectionType;
        this.dev_time = "" + System.currentTimeMillis();

        this.dev_timezone = Settings.getSettings().dev_timezone;
        this.os = Settings.getSettings().os;
        this.language = Settings.getSettings().language;

        this.placementId = placementId;
        this.psa = shouldServePSAs ? "1" : "0";

        this.nativeBrowser = isNativeBrowser ? "1" : "0";

        this.shouldRetry = shouldRetry;
    }

    public AdRequest(AdRequester adRequester) {
        this(adRequester, Settings.getSettings().MAX_CONNECTIVITY_RETRIES, Settings.getSettings().MAX_BLANK_RETRIES);
    }

    public AdRequest(AdRequester adRequester, int httpRetriesLeft, int blankRetriesLeft) {
        owner = adRequester.getOwner();
        this.requester = adRequester;
        this.httpRetriesLeft = httpRetriesLeft;
        this.blankRetriesLeft = blankRetriesLeft;
        this.placementId = owner.getPlacementID();
        context = owner.getContext();
        String aid = android.provider.Settings.Secure.getString(
                context.getContentResolver(), Secure.ANDROID_ID);

        // Do we have access to location?
        if (context
                .checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED
                || context
                .checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
            // Get lat, long from any GPS information that might be currently
            // available
            LocationManager lm = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);
            Location lastLocation = lm.getLastKnownLocation(lm.getBestProvider(
                    new Criteria(), false));
            if (lastLocation != null) {
                lat = "" + lastLocation.getLatitude();
                lon = "" + lastLocation.getLongitude();
                locDataAge = ""
                        + (System.currentTimeMillis() - lastLocation.getTime());
                locDataPrecision = "" + lastLocation.getAccuracy();
            }
        } else {
            Clog.w(Clog.baseLogTag,
                    Clog.getString(R.string.permissions_missing_location));
        }

        // Do we have permission ACCESS_NETWORK_STATE?
        if (context
                .checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") != PackageManager.PERMISSION_GRANTED) {
            Clog.e(Clog.baseLogTag,
                    Clog.getString(R.string.permissions_missing_network_state));
            fail();
            this.cancel(true);
            return;
        }

        // Get orientation, the current rotation of the device
        orientation = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? "landscape"
                : "portrait";
        // Get hidmd5, hidsha1, the device ID hashed
        if (Settings.getSettings().hidmd5 == null) {
            Settings.getSettings().hidmd5 = HashingFunctions.md5(aid);
        }
        hidmd5 = Settings.getSettings().hidmd5;
        if (Settings.getSettings().hidsha1 == null) {
            Settings.getSettings().hidsha1 = HashingFunctions.sha1(aid);
        }
        hidsha1 = Settings.getSettings().hidsha1;
        // Get devMake, devModel, the Make and Model of the current device
        devMake = Settings.getSettings().deviceMake;
        devModel = Settings.getSettings().deviceModel;
        // Get carrier
        if (Settings.getSettings().carrierName == null) {
            Settings.getSettings().carrierName = ((TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE))
                    .getNetworkOperatorName();
        }
        carrier = Settings.getSettings().carrierName;
        // Get firstlaunch and convert it to a string
        firstlaunch = "" + Settings.getSettings().first_launch;
        // Get ua, the user agent...
        ua = Settings.getSettings().ua;
        // Get wxh
        this.width = owner.getAdWidth();
        this.height = owner.getAdHeight();

        maxHeight = owner.getContainerHeight();
        maxWidth = owner.getContainerWidth();



        if (Settings.getSettings().mcc == null
                || Settings.getSettings().mnc == null) {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String networkOperator = tm.getNetworkOperator();
            if (networkOperator != null && networkOperator.length() >= 6) {
                Settings.getSettings().mcc = networkOperator.substring(0, 3);
                Settings.getSettings().mnc = networkOperator.substring(3);
            }
        }
        mcc = Settings.getSettings().mcc;
        mnc = Settings.getSettings().mnc;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        connection_type = wifi.isConnected() ? "wifi" : "wan";
        dev_time = "" + System.currentTimeMillis();

        if (owner instanceof InterstitialAdView) {
            // Make string for allowed_sizes
            allowedSizes = "";
            ArrayList<Size> sizes = ((InterstitialAdView) owner).getAllowedSizes();
            for (Size s : sizes) {
                allowedSizes += "" + s.width() + "x" + s.height();
                // If not last size, add a comma
                if (sizes.indexOf(s) != sizes.size() - 1)
                    allowedSizes += ",";
            }
        }

        nativeBrowser = owner.getOpensNativeBrowser() ? "1" : "0";

        //Reserve price
        reserve = owner.getReserve();
        if(reserve<=0){
            this.psa = owner.shouldServePSAs ? "1" : "0";
        }else{
            this.psa = "0";
        }

        age = owner.getAge();
        if (owner.getGender() != null) {
            if (owner.getGender() == AdView.GENDER.MALE)
                gender = "m";
            else if (owner.getGender() == AdView.GENDER.FEMALE)
                gender = "f";
            else
                gender = null;
        }
        customKeywords = owner.getCustomKeywords();

        mcc = Settings.getSettings().mcc;
        mnc = Settings.getSettings().mnc;
        os = Settings.getSettings().os;
        language = Settings.getSettings().language;
    }

    private void fail() {
        if (requester != null)
            requester.failed(this);
        if (adListener != null)
            adListener.onAdRequestFailed(this.owner);
        Clog.clearLastResponse();
    }

    public String getRequestUrl() {
        StringBuilder sb;
        sb = new StringBuilder(Settings.getSettings().BASE_URL);
        sb.append((placementId != null ? "id=" + Uri.encode(placementId)
                : "id=NO-PLACEMENT-ID"));
        sb.append((!isEmpty(hidmd5) ? "&md5udid=" + Uri.encode(hidmd5) : ""));
        sb.append((!isEmpty(hidsha1) ? "&sha1udid=" + Uri.encode(hidsha1) : ""));
        sb.append((!isEmpty(devMake) ? "&devmake=" + Uri.encode(devMake) : ""));
        sb.append((!isEmpty(devModel) ? "&devmodel=" + Uri.encode(devModel)
                : ""));
        sb.append((!isEmpty(carrier) ? "&carrier=" + Uri.encode(carrier) : ""));
        sb.append((!isEmpty(Settings.getSettings().app_id) ? "&appid="
                + Uri.encode(Settings.getSettings().app_id)
                : "&appid=NO-APP-ID"));
        sb.append((!isEmpty(firstlaunch) ? "&firstlaunch=" + firstlaunch : ""));
        sb.append(!isEmpty(lat) && !isEmpty(lon) ? "&loc=" + lat + "," + lon
                : "");
        sb.append((!isEmpty(locDataAge) ? "&" + "loc_age=" + locDataAge : ""));
        sb.append((!isEmpty(locDataPrecision) ? "&loc_prec=" + locDataPrecision
                : ""));
        sb.append((Settings.getSettings().test_mode ? "&istest=true" : ""));
        sb.append((!isEmpty(ua) ? "&ua=" + Uri.encode(ua) : ""));
        sb.append((!isEmpty(orientation) ? "&orientation=" + orientation : ""));
        sb.append(((width > 0 && height > 0) ? "&size=" + width + "x" + height
                : ""));
        // complicated, don't change
        if (owner != null) {
            if (maxHeight > 0 && maxWidth > 0) {
                if (!(owner instanceof InterstitialAdView)
                        && (width < 0 && height < 0)) {
                    sb.append("&max_size=" + maxWidth + "x" + maxHeight);
                } else if (owner instanceof InterstitialAdView) {
                    sb.append("&size=" + maxWidth + "x" + maxHeight);
                }
            }
        }
        sb.append((!isEmpty(allowedSizes) ? "&promo_sizes=" + allowedSizes : ""));
        sb.append((!isEmpty(mcc) ? "&mcc=" + Uri.encode(mcc) : ""));
        sb.append((!isEmpty(mnc) ? "&mnc=" + Uri.encode(mnc) : ""));
        sb.append((!isEmpty(os) ? "&os=" + Uri.encode(os) : ""));
        sb.append((!isEmpty(language) ? "&language=" + Uri.encode(language)
                : ""));
        sb.append((!isEmpty(dev_timezone) ? "&devtz="
                + Uri.encode(dev_timezone) : ""));
        sb.append((!isEmpty(dev_time) ? "&devtime=" + Uri.encode(dev_time) : ""));
        sb.append((!isEmpty(connection_type) ? "&connection_type="
                + Uri.encode(connection_type) : ""));
        sb.append((!isEmpty(nativeBrowser) ? "&native_browser=" + nativeBrowser
                : ""));
        sb.append((!isEmpty(psa) ? "&psa=" + psa : ""));
        sb.append("&reserve=" + (reserve>0 ? Uri.encode(reserve+""):""));
        if (!isEmpty(age)) sb.append("&age=").append(Uri.encode(age));
        if (!isEmpty(gender)) sb.append("&gender=").append(Uri.encode(gender));
        // add custom paramters if there are any
        for (Pair<String, String> pair : customKeywords) {
            if (!isEmpty(pair.first) && (pair.second != null)) {
                sb.append("&")
                        .append(pair.first)
                        .append("=")
                        .append(Uri.encode(pair.second));
            }
        }
        sb.append("&format=json");
        sb.append("&sdkver=" + Uri.encode(Settings.getSettings().sdkVersion));

        return sb.toString();
    }

    @Override
    protected AdResponse doInBackground(Void... params) {
        if (!hasNetwork(context)) {
            Clog.e(Clog.httpReqLogTag,
                    Clog.getString(R.string.no_connectivity));
            if (!shouldRetry)
                return doRequest();
            return AdRequest.CONNECTIVITY_RETRY;
        }

        return doRequest();

    }

    private AdResponse doRequest() {
        String query_string = getRequestUrl();

        Clog.setLastRequest(query_string);

        Clog.d(Clog.httpReqLogTag,
                Clog.getString(R.string.fetch_url, query_string));

        HttpResponse r = null;
        String out = null;
        try {
            HttpParams p = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(p,
                    Settings.getSettings().HTTP_CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(p,
                    Settings.getSettings().HTTP_SOCKET_TIMEOUT);
            HttpConnectionParams.setSocketBufferSize(p, 8192);
            DefaultHttpClient h = new DefaultHttpClient(p);
            r = h.execute(new HttpGet(query_string));
            if (!httpShouldContinue(r.getStatusLine())) {
                return AdRequest.HTTP_ERROR;
            }
            out = EntityUtils.toString(r.getEntity());
        } catch (ClientProtocolException e) {
            Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_unknown));
            return AdRequest.CONNECTIVITY_RETRY;
        } catch (ConnectTimeoutException e) {
            Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_timeout));
            return AdRequest.CONNECTIVITY_RETRY;
        } catch (HttpHostConnectException he) {
            Clog.e(Clog.httpReqLogTag, Clog.getString(
                    R.string.http_unreachable, he.getHost().getHostName(), he
                    .getHost().getPort()));
            return AdRequest.CONNECTIVITY_RETRY;
        } catch (IOException e) {
            Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_io));
            return AdRequest.CONNECTIVITY_RETRY;
        } catch (SecurityException se) {
            Clog.e(Clog.baseLogTag,
                    Clog.getString(R.string.permissions_internet));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Clog.e(Clog.baseLogTag, Clog.getString(R.string.unknown_exception));
            return AdRequest.CONNECTIVITY_RETRY;
        }
        if (out.equals("")) {
            Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.response_blank));
            return AdRequest.BLANK_RETRY;
        }
        return new AdResponse(out, r.getAllHeaders());
    }

    private boolean hasNetwork(Context context) {
        if (context != null) {
            NetworkInfo ninfo = ((ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            if (ninfo == null || !ninfo.isConnectedOrConnecting()) {
                return false;
            }
            return true;
        }
        return false;
    }


    private boolean httpShouldContinue(StatusLine statusLine) {
        if (statusLine == null)
            return false;

        int http_error_code = statusLine.getStatusCode();
        switch (http_error_code) {
            default:
                Clog.d(Clog.httpRespLogTag,
                        Clog.getString(R.string.http_bad_status, http_error_code));
                return false;
            case 200:
                return true;
        }

    }

    @Override
    protected void onPostExecute(AdResponse result) {
        if (requester != null)
            requester.setAdRequest(null);
        if (result == null) {
            Clog.v(Clog.httpRespLogTag, Clog.getString(R.string.no_response));
            fail();
            // Don't call fail again!
            return; // http request failed
        }
        if (result.isHttpError()) {
            fail();
            return;
        }

        if (shouldRetry) {
            if ((httpRetriesLeft < 1) || (blankRetriesLeft < 1)) {
                // return if we have exceeded the max number of tries
                fail();
                return;
            }
            boolean resultIsRetry = false;

            if (result.isConnectivityRetry()) {
                httpRetriesLeft--;
                resultIsRetry = true;
            } else if (result.isBlankRetry()) {
                blankRetriesLeft--;
                resultIsRetry = true;
            }

            if (resultIsRetry) {
                // don't fail, but clear the last response
                Clog.clearLastResponse();
                final AdRequest retry = new AdRequest(requester, httpRetriesLeft, blankRetriesLeft);
                if (requester != null)
                    requester.setAdRequest(retry);
                retry.retryHandler.postDelayed(new RetryRunnable(retry), Settings.getSettings().HTTP_RETRY_INTERVAL);
                return; // The request failed and should be retried.
            }
            // else let it continue to process the valid result
        }
        if (requester != null)
            requester.onReceiveResponse(result);
        // for unit testing
        if (adListener != null)
            adListener.onAdLoaded(owner);
    }

    @Override
    protected void onCancelled(AdResponse adResponse) {
        super.onCancelled(adResponse);
        Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.cancel_request));
        if (requester != null)
            requester.setAdRequest(null);
        // remove pending retry requests if the requester cancels the ad request
        retryHandler.removeCallbacksAndMessages(null);
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean isEmpty(String str) {
        if (str == null)
            return true;
        if (str.equals(""))
            return true;
        return false;
    }

    class RetryRunnable implements Runnable {
        AdRequest retry;

        RetryRunnable(AdRequest retry) {
            this.retry = retry;
        }

        @Override
        public void run() {
            if (retry.isCancelled()) {
                Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.retry_already_cancelled));
                return;
            }

            // Spawn an AdRequest
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                retry.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                retry.execute();
            }
        }
    }

//   // Uncomment for unit tests
//   public void setContext(Context context) {
//       this.context = context;
//   }
}
