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
import com.appnexus.opensdk.utils.StringUtil;
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

class AdRequest extends AsyncTask<Void, Integer, AdResponse> {

    private AdView owner;
    private final AdRequester requester;
    private AdListener adListener;
    private Context context;
    private String hidmd5;
    private String hidsha1;
    private String devMake;
    private String devModel;
    private String carrier;
    private boolean firstlaunch;
    private String lat;
    private String lon;
    private String locDataAge;
    private String locDataPrecision;
    private String ua;
    private String orientation;
    private String allowedSizes;
    private String mcc;
    private String mnc;
    private String connection_type;
    private String dev_time; // Set at the time of the request
    private String dev_timezone;
    private String language;
    private final String placementId;
    private String nativeBrowser;
    private String psa;
    private int width = -1;
    private int height = -1;
    private int maxWidth = -1;
    private int maxHeight = -1;
    private float reserve = 0.00f;
    private String age;
    private String gender;
    private ArrayList<Pair<String, String>> customKeywords;

    private static final AdResponse HTTP_ERROR
            = new AdResponse(true);

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
        if (aid != null) {
            hidmd5 = HashingFunctions.md5(aid);
            hidsha1 = HashingFunctions.sha1(aid);
        }
        devMake = Settings.getSettings().deviceMake;
        devModel = Settings.getSettings().deviceModel;

        // Get firstlaunch and convert it to a string
        firstlaunch = Settings.getSettings().first_launch;
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
        this.language = Settings.getSettings().language;

        this.placementId = placementId;
        this.psa = shouldServePSAs ? "1" : "0";

        this.nativeBrowser = isNativeBrowser ? "1" : "0";
    }

    public AdRequest(AdRequester adRequester) {
        owner = adRequester.getOwner();
        this.requester = adRequester;
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
        orientation = context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE ? "h" : "v";
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
        firstlaunch = Settings.getSettings().first_launch;
        // Get ua, the user agent...
        ua = Settings.getSettings().ua;
        // Get wxh

        if(owner.isBanner()){
            this.width = ((BannerAdView)owner).getAdWidth();
            this.height = ((BannerAdView)owner).getAdHeight();
        }

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
            if (owner.getGender() == AdView.GENDER.MALE) {
                gender = "m";
            }
            else if (owner.getGender() == AdView.GENDER.FEMALE) {
                gender = "f";
            }
            else {
                gender = null;
            }
        }
        customKeywords = owner.getCustomKeywords();

        mcc = Settings.getSettings().mcc;
        mnc = Settings.getSettings().mnc;
        language = Settings.getSettings().language;
    }

    private void fail() {
        if (requester != null)
            requester.failed(this);
        if (adListener != null)
            adListener.onAdRequestFailed(this.owner);
        Clog.clearLastResponse();
    }

    String getRequestUrl() {
        StringBuilder sb;
        sb = new StringBuilder(Settings.getSettings().BASE_URL);
        sb.append("id=");
        if (placementId != null) {
            sb.append(Uri.encode(placementId));
        } else {
            sb.append("NO-PLACEMENT-ID");
        }
        if (!StringUtil.isEmpty(hidmd5)) sb.append("&md5udid=").append(Uri.encode(hidmd5));
        if (!StringUtil.isEmpty(hidsha1)) sb.append("&sha1udid=").append(Uri.encode(hidsha1));
        if (!StringUtil.isEmpty(devMake)) sb.append("&devmake=").append(Uri.encode(devMake));
        if (!StringUtil.isEmpty(devModel)) sb.append("&devmodel=").append(Uri.encode(devModel));
        if (!StringUtil.isEmpty(carrier)) sb.append( "&carrier=").append(Uri.encode(carrier));
        sb.append("&appid=");
        if (!StringUtil.isEmpty(Settings.getSettings().app_id)) {
            sb.append(Uri.encode(Settings.getSettings().app_id));
        } else {
            sb.append("NO-APP-ID");
        }
        if (firstlaunch) sb.append("&firstlaunch=true");
        if (!StringUtil.isEmpty(lat) && !StringUtil.isEmpty(lon)) sb.append("&loc=").append(lat).append(",").append(lon);
        if (!StringUtil.isEmpty(locDataAge)) sb.append("&loc_age=").append(locDataAge);
        if (!StringUtil.isEmpty(locDataPrecision)) sb.append("&loc_prec=").append(locDataPrecision);
        if (Settings.getSettings().test_mode) sb.append("&istest=true");
        if (!StringUtil.isEmpty(ua)) sb.append("&ua=").append(Uri.encode(ua));
        if (!StringUtil.isEmpty(orientation)) sb.append("&orientation=").append(orientation);
        if (width > 0 && height > 0) sb.append("&size=").append(width).append("x").append(height);
        // complicated, don't change
        if (owner != null) {
            if (maxHeight > 0 && maxWidth > 0) {
                if (!(owner instanceof InterstitialAdView)
                        && (width < 0 || height < 0)) {
                    sb.append("&max_size=").append(maxWidth).append("x").append(maxHeight);
                } else if (owner instanceof InterstitialAdView) {
                    sb.append("&size=").append(maxWidth).append("x").append(maxHeight);
                }
            }
        }
        if (!StringUtil.isEmpty(allowedSizes)) sb.append("&promo_sizes=").append(allowedSizes);
        if (!StringUtil.isEmpty(mcc)) sb.append("&mcc=").append(Uri.encode(mcc));
        if (!StringUtil.isEmpty(mnc)) sb.append("&mnc=").append(Uri.encode(mnc));
        if (!StringUtil.isEmpty(language)) sb.append("&language=").append(Uri.encode(language));
        if (!StringUtil.isEmpty(dev_timezone)) sb.append("&devtz=").append(Uri.encode(dev_timezone));
        if (!StringUtil.isEmpty(dev_time)) sb.append("&devtime=").append(Uri.encode(dev_time));
        if (!StringUtil.isEmpty(connection_type)) sb.append("&connection_type=").append( Uri.encode(connection_type));
        if (!StringUtil.isEmpty(nativeBrowser)) sb.append("&native_browser=").append(nativeBrowser);
        if (!StringUtil.isEmpty(psa)) sb.append( "&psa=").append(psa);
        if (reserve>0) sb.append("&reserve=").append(reserve);
        if (!StringUtil.isEmpty(age)) sb.append("&age=").append(Uri.encode(age));
        if (!StringUtil.isEmpty(gender)) sb.append("&gender=").append(Uri.encode(gender));
        sb.append("&format=json");
        sb.append("&st=mobile_app");
        sb.append("&sdkver=").append(Uri.encode(Settings.getSettings().sdkVersion));

        // add custom parameters if there are any
        if (customKeywords != null) {
            for (Pair<String, String> pair : customKeywords) {
                if (!StringUtil.isEmpty(pair.first) && (pair.second != null)) {
                    sb.append("&")
                            .append(pair.first)
                            .append("=")
                            .append(Uri.encode(pair.second));
                }
            }
        }

        return sb.toString();
    }

    @Override
    protected AdResponse doInBackground(Void... params) {
        if (!hasNetwork(context)) {
            Clog.e(Clog.httpReqLogTag,
                    Clog.getString(R.string.no_connectivity));
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
            return null;
        } catch (ConnectTimeoutException e) {
            Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_timeout));
            return null;
        } catch (HttpHostConnectException he) {
            Clog.e(Clog.httpReqLogTag, Clog.getString(
                    R.string.http_unreachable, he.getHost().getHostName(), he
                    .getHost().getPort()));
            return null;
        } catch (IOException e) {
            Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_io));
            return null;
        } catch (SecurityException se) {
            Clog.e(Clog.baseLogTag,
                    Clog.getString(R.string.permissions_internet));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Clog.e(Clog.baseLogTag, Clog.getString(R.string.unknown_exception));
            return null;
        }
        if (out.equals("")) {
            Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.response_blank));
            return null;
        }
        return new AdResponse(out, r.getAllHeaders());
    }

    private boolean hasNetwork(Context context) {
        if (context != null) {
            NetworkInfo ninfo = ((ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            if (ninfo != null && ninfo.isConnectedOrConnecting()) {
                return true;
            }
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
        // check for invalid responses
        if (result == null) {
            Clog.v(Clog.httpRespLogTag, Clog.getString(R.string.no_response));
            fail();
            return; // http request failed
        }
        if (result.isHttpError()) {
            fail();
            return;
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
    }

//   // Uncomment for unit tests
//   public void setContext(Context context) {
//       this.context = context;
//   }
}
