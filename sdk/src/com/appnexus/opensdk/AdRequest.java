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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Pair;

import com.appnexus.opensdk.utils.*;

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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;

class AdRequest extends AsyncTask<Void, Integer, AdResponse> {

    private WeakReference<AdRequester> requester; // The instance of AdRequester which is filing this request.
    private WeakReference<Context> owner_context;
    private MediaType media_type;
    private String hidmd5;
    private String hidsha1;
    private String aaid;
    private boolean limitTrackingEnabled;
    private String devMake;
    private String devModel;
    private String carrier; // The carrier to pass, such as 'AT&T'
    private boolean firstlaunch;
    private String lat; // The latitude to pass.
    private String lon; // the longitude to pass
    private String locDataAge;
    private String locDataPrecision;
    private String ua;
    private String orientation; // The device orientation to pass, 'vertical' or 'horizontal'
    private String allowedSizes;
    private String mcc; // The MCC to pass.
    private String mnc; // The MNC to pass.
    private String connection_type; // The type of connection, 'wifi' or 'wan'
    private String dev_time; // Set at the time of the request
    private String dev_timezone;
    private String language;
    private final String placementId; // The AppNexus placement id to use
    private String nativeBrowser; // Whether this ad space will open the landing page in the native
    // browser ('1') or the in-app browser ('0').
    private String psa;
    private int width = -1; // The width to request, in pixels. -1 for none.
    private int height = -1; // The height to request, in pixels. -1 for none.
    private int maxWidth = -1; // The maximum width, if no width is specified.
    private int maxHeight = -1; // The maximum height, if no height is specified.
    private float reserve = 0.00f;
    private boolean overrideMaxSize = false;
    private String age;
    private String gender;
    private ArrayList<Pair<String, String>> customKeywords;
    private String nonet;
    static HashSet<String> pNames = null;

    private static HashSet<String> getParamNames() {
        if (pNames == null) {
            pNames = new HashSet<String>();
            pNames.add("id");
            pNames.add("aaid");
            pNames.add("md5udid");
            pNames.add("sha1udid");
            pNames.add("devmake");
            pNames.add("devmodel");
            pNames.add("carrier");
            pNames.add("appid");
            pNames.add("firstlaunch");
            pNames.add("loc");
            pNames.add("loc_age");
            pNames.add("loc_prec");
            pNames.add("istest");
            pNames.add("ua");
            pNames.add("orientation");
            pNames.add("size");
            pNames.add("max_size");
            pNames.add("promo_sizes");
            pNames.add("mcc");
            pNames.add("mnc");
            pNames.add("language");
            pNames.add("devtz");
            pNames.add("devtime");
            pNames.add("connection_type");
            pNames.add("native_browser");
            pNames.add("psa");
            pNames.add("reserve");
            pNames.add("format");
            pNames.add("st");
            pNames.add("sdkver");

            return pNames;
        } else {
            return pNames;
        }
    }

    private static boolean stringNotInParamNames(String s) {
        return !getParamNames().contains(s);
    }

    private static final AdResponse HTTP_ERROR
            = new AdResponse(true);

    public AdRequest(AdRequester adRequester) {
        this.requester = new WeakReference<AdRequester>(adRequester);
        RequestParameters params = adRequester.getRequestParams();
        this.placementId = params.getPlacementID();
        Context context = params.getContext();
        owner_context = new WeakReference<Context>(params.getContext());

        AdvertistingIDUtil.retrieveAndSetAAID(context);

        Location lastLocation = null;
        Location appLocation = SDKSettings.getLocation();
        // Do we have access to location?
        if (SDKSettings.getLocationEnabled()) {

            // First priority is the app supplied location
            if (appLocation != null) {
                lastLocation = appLocation;
            } else if (context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED
                    || context.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
                // Get lat, long from any GPS information that might be currently
                // available
                LocationManager lm = (LocationManager) context
                        .getSystemService(Context.LOCATION_SERVICE);

                for (String provider_name : lm.getProviders(true)) {
                    Location l = lm.getLastKnownLocation(provider_name);
                    if (l == null) {
                        continue;
                    }

                    if (lastLocation == null) {
                        lastLocation = l;
                    } else {
                        if (l.getTime() > 0 && lastLocation.getTime() > 0) {
                            if (l.getTime() > lastLocation.getTime()) {
                                lastLocation = l;
                            }
                        }
                    }
                }
            } else {
                Clog.w(Clog.httpReqLogTag,
                        Clog.getString(R.string.permissions_missing_location));
            }
        }

        // Set the location info back to the application
        if (appLocation != lastLocation) {
            SDKSettings.setLocation(lastLocation);
        }

        if (lastLocation != null) {
            if (SDKSettings.getLocationDecimalDigits() <= -1) {
                lat = "" + lastLocation.getLatitude();
                lon = "" + lastLocation.getLongitude();
            } else {
                lat = String.format("%." + SDKSettings.getLocationDecimalDigits() + "f", lastLocation.getLatitude());
                lon = String.format("%." + SDKSettings.getLocationDecimalDigits() + "f", lastLocation.getLongitude());
            }
            locDataPrecision = "" + lastLocation.getAccuracy();
            //Don't report location data from the future
            locDataAge = "" + Math.max(0, (System.currentTimeMillis() - lastLocation.getTime()));
        } else {
            lat = "";
            lon = "";
            locDataAge = "";
            locDataPrecision = "";
        }

        // Do we have permission ACCESS_NETWORK_STATE?
        if (context
                .checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") != PackageManager.PERMISSION_GRANTED) {
            Clog.e(Clog.httpReqLogTag,
                    Clog.getString(R.string.permissions_missing_network_state));
            fail();
            this.cancel(true);
            return;
        }

        Settings settings = Settings.getSettings();

        // Get orientation, the current rotation of the device
        orientation = context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE ? "h" : "v";

        aaid = settings.aaid;
        if (StringUtil.isEmpty(aaid)) {
            // Fall back on the hashed ANDROID_ID (device id) if no AAID found
            if (StringUtil.isEmpty(settings.hidmd5) || StringUtil.isEmpty(settings.hidsha1)) {
                String aid = android.provider.Settings.Secure.getString(
                        context.getContentResolver(), Secure.ANDROID_ID);
                if (!StringUtil.isEmpty(aid)) {
                    settings.hidmd5 = HashingFunctions.md5(aid);
                    settings.hidsha1 = HashingFunctions.sha1(aid);
                }
            }
            hidmd5 = settings.hidmd5;
            hidsha1 = settings.hidsha1;
        }

        // Get devMake, devModel, the Make and Model of the current device
        devMake = settings.deviceMake;
        devModel = settings.deviceModel;

        limitTrackingEnabled = settings.limitTrackingEnabled;
        // Get carrier
        if (settings.carrierName == null) {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            try {
                settings.carrierName = telephonyManager.getNetworkOperatorName();
            } catch (SecurityException ex) {
                // Some phones require READ_PHONE_STATE permission just ignore name
                settings.carrierName = "";
            }
        }
        carrier = settings.carrierName;
        // Get firstlaunch and convert it to a string
        firstlaunch = settings.first_launch;
        // Get ua, the user agent...
        ua = settings.ua;
        // Get wxh

        media_type = params.getMediaType();
        if (media_type == MediaType.BANNER) {
            this.width = params.getAdWidth();
            this.height = params.getAdHeight();
            this.overrideMaxSize = params.getOverrideMaxSize();
        }

        if (this.overrideMaxSize) {
            maxHeight = params.getMaxHeight();
            maxWidth = params.getMaxWidth();
            if (maxWidth <= 0 || maxHeight <= 0) {
                Clog.w(Clog.httpReqLogTag, Clog.getString(R.string.max_size_not_set));
            }
        } else {
            maxHeight = params.getContainerHeight();
            maxWidth = params.getContainerWidth();
        }


        if (settings.mcc == null
                || settings.mnc == null) {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String networkOperator = tm.getNetworkOperator();
            // CDMA devices need special treatment
            if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA &&
                    tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
                networkOperator = tm.getSimOperator();
            }
            if (networkOperator != null && networkOperator.length() >= 6) {
                settings.mcc = networkOperator.substring(0, 3);
                settings.mnc = networkOperator.substring(3);
            }
        }
        mcc = settings.mcc;
        mnc = settings.mnc;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null) {
            connection_type = wifi.isConnected() ? "wifi" : "wan";
        }

        dev_time = "" + System.currentTimeMillis();
        dev_timezone = "" + Settings.getSettings().dev_timezone;

        if (media_type == MediaType.INTERSTITIAL) {
            // Make string for allowed_sizes
            allowedSizes = "";
            ArrayList<AdSize> sizes = params.getAllowedSizes();
            for (AdSize s : sizes) {
                allowedSizes += "" + s.width() + "x" + s.height();
                // If not last size, add a comma
                if (sizes.indexOf(s) != sizes.size() - 1)
                    allowedSizes += ",";
            }
        }

        nativeBrowser = params.getOpensNativeBrowser() ? "1" : "0";

        //Reserve price
        reserve = params.getReserve();
        if (reserve <= 0) {
            this.psa = params.getShouldServePSAs() ? "1" : "0";
        } else {
            this.psa = "0";
        }

        age = params.getAge();
        if (params.getGender() != null) {
            if (params.getGender() == AdView.GENDER.MALE) {
                gender = "m";
            } else if (params.getGender() == AdView.GENDER.FEMALE) {
                gender = "f";
            } else {
                gender = null;
            }
        }
        customKeywords = params.getCustomKeywords();

        mcc = settings.mcc;
        mnc = settings.mnc;
        language = settings.language;

        StringBuilder nonetSB = new StringBuilder();

        for (String invalidNetwork : settings.getInvalidNetwork(media_type)) {
            // only add commas when there are additional items
            if (nonetSB.length() > 0) {
                nonetSB.append(",");
            }
            nonetSB.append(invalidNetwork);
        }
        nonet = nonetSB.toString();

        if (media_type != MediaType.NATIVE) {
            if ((maxHeight <= 0 || maxWidth <= 0) &&
                    (width <= 0 || height <= 0)) {
                Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.no_size_info));
                fail();
                this.cancel(true);
            }
        }
    }

    private void fail() {
        AdRequester requester = this.requester.get();
        if (requester != null) {
            requester.failed(this);
        }
        Clog.clearLastResponse();
    }

    String getRequestUrl() {
        StringBuilder sb;
        sb = new StringBuilder(Settings.REQUEST_BASE_URL);
        sb.append("id=");
        if (placementId != null) {
            sb.append(Uri.encode(placementId));
        } else {
            sb.append("NO-PLACEMENT-ID");
        }
        if (!StringUtil.isEmpty(hidmd5)) sb.append("&md5udid=").append(Uri.encode(hidmd5));
        if (!StringUtil.isEmpty(hidsha1)) sb.append("&sha1udid=").append(Uri.encode(hidsha1));
        if (!StringUtil.isEmpty(aaid)) {
            sb.append("&aaid=").append(Uri.encode(aaid));
            sb.append(limitTrackingEnabled ? "&LimitAdTrackingEnabled=1" : "&LimitAdTrackingEnabled=0");
        }
        if (!StringUtil.isEmpty(devMake)) sb.append("&devmake=").append(Uri.encode(devMake));
        if (!StringUtil.isEmpty(devModel)) sb.append("&devmodel=").append(Uri.encode(devModel));
        if (!StringUtil.isEmpty(carrier)) sb.append("&carrier=").append(Uri.encode(carrier));
        sb.append("&appid=");
        if (!StringUtil.isEmpty(Settings.getSettings().app_id)) {
            sb.append(Uri.encode(Settings.getSettings().app_id));
        } else {
            sb.append("NO-APP-ID");
        }
        if (firstlaunch) sb.append("&firstlaunch=true");
        if (!StringUtil.isEmpty(lat) && !StringUtil.isEmpty(lon))
            sb.append("&loc=").append(lat).append(",").append(lon);
        if (!StringUtil.isEmpty(locDataAge)) sb.append("&loc_age=").append(locDataAge);
        if (!StringUtil.isEmpty(locDataPrecision)) sb.append("&loc_prec=").append(locDataPrecision);
        if (Settings.getSettings().test_mode) sb.append("&istest=true");
        if (!StringUtil.isEmpty(ua)) sb.append("&ua=").append(Uri.encode(ua));
        if (!StringUtil.isEmpty(orientation)) sb.append("&orientation=").append(orientation);
        if (width > 0 && height > 0) sb.append("&size=").append(width).append("x").append(height);
        // complicated, don't change
        if (maxHeight > 0 && maxWidth > 0) {
            if (media_type != MediaType.INTERSTITIAL && (width < 0 || height < 0)) {
                sb.append("&max_size=").append(maxWidth).append("x").append(maxHeight);
            } else if (media_type == MediaType.INTERSTITIAL) {
                sb.append("&size=").append(maxWidth).append("x").append(maxHeight);
            }
        }

        if (!StringUtil.isEmpty(allowedSizes)) sb.append("&promo_sizes=").append(allowedSizes);
        if (!StringUtil.isEmpty(mcc)) sb.append("&mcc=").append(Uri.encode(mcc));
        if (!StringUtil.isEmpty(mnc)) sb.append("&mnc=").append(Uri.encode(mnc));
        if (!StringUtil.isEmpty(language)) sb.append("&language=").append(Uri.encode(language));
        if (!StringUtil.isEmpty(dev_timezone))
            sb.append("&devtz=").append(Uri.encode(dev_timezone));
        if (!StringUtil.isEmpty(dev_time)) sb.append("&devtime=").append(Uri.encode(dev_time));
        if (!StringUtil.isEmpty(connection_type))
            sb.append("&connection_type=").append(Uri.encode(connection_type));
        if (!StringUtil.isEmpty(nativeBrowser)) sb.append("&native_browser=").append(nativeBrowser);
        if (!StringUtil.isEmpty(psa)) sb.append("&psa=").append(psa);
        if (reserve > 0) sb.append("&reserve=").append(reserve);
        if (!StringUtil.isEmpty(age)) sb.append("&age=").append(Uri.encode(age));
        if (!StringUtil.isEmpty(gender)) sb.append("&gender=").append(Uri.encode(gender));
        if (!StringUtil.isEmpty(nonet)) sb.append("&nonet=").append(Uri.encode(nonet));
        sb.append("&format=json");
        sb.append("&st=mobile_app");
        sb.append("&sdkver=").append(Uri.encode(Settings.getSettings().sdkVersion));

        // add custom parameters if there are any
        if (customKeywords != null) {
            for (Pair<String, String> pair : customKeywords) {
                if (!StringUtil.isEmpty(pair.first) && (pair.second != null)) {
                    if (AdRequest.stringNotInParamNames(pair.first)) {
                        sb.append("&")
                                .append(pair.first)
                                .append("=")
                                .append(Uri.encode(pair.second));
                    } else {
                        Clog.w(Clog.httpReqLogTag, Clog.getString(R.string.request_parameter_override_attempt, pair.first));
                    }
                }
            }
        }

        return sb.toString();
    }

    @Override
    protected AdResponse doInBackground(Void... params) {

        if (!hasNetwork()) {
            Clog.e(Clog.httpReqLogTag,
                    Clog.getString(R.string.no_connectivity));
            return null;
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
                    Settings.HTTP_CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(p,
                    Settings.HTTP_SOCKET_TIMEOUT);
            HttpConnectionParams.setSocketBufferSize(p, 8192);
            DefaultHttpClient h = new DefaultHttpClient(p);

            HttpGet req = new HttpGet(query_string);
            req.setHeader("User-Agent", Settings.getSettings().ua);
            r = h.execute(req);
            if (!httpShouldContinue(r.getStatusLine())) {
                return AdRequest.HTTP_ERROR;
            }
            out = EntityUtils.toString(r.getEntity());
            WebviewUtil.cookieSync(h.getCookieStore().getCookies());
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
            Clog.e(Clog.httpReqLogTag,
                    Clog.getString(R.string.permissions_internet));
            return null;
        } catch (IllegalArgumentException ie) {
            Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_unknown));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.unknown_exception));
            return null;
        }

        if (out.equals("")) {
            // just log and return a valid AdResponse object so that it is
            // marked as UNABLE_TO_FILL
            Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.response_blank));
        }
        return new AdResponse(out, r.getAllHeaders(), media_type);
    }

    private boolean hasNetwork() {
        Context context = owner_context.get();
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

        // add the orientation extra for interstitial ads
        result.addToExtras(AdResponse.EXTRAS_KEY_ORIENTATION, orientation);

        AdRequester requester = this.requester.get();
        if (requester != null) {
            requester.onReceiveResponse(result);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCancelled(AdResponse adResponse) {
        super.onCancelled(adResponse);
        Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.cancel_request));
    }
}
