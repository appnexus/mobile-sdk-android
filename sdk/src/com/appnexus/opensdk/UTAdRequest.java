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

package com.appnexus.opensdk;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Pair;

import com.appnexus.opensdk.utils.AdvertistingIDUtil;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.utils.WebviewUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

class UTAdRequest extends AsyncTask<Void, Integer, UTAdResponse> {

    public static final String SIZE_WIDTH = "width";
    public static final String SIZE_HEIGHT = "height";
    public static final String TAGS = "tags";
    public static final String TAG_ID = "id";
    public static final String TAG_SIZES = "sizes";
    public static final String TAG_ALLOW_SMALLER_SIZES = "allow_smaller_sizes";
    public static final String TAG_ALLOWED_MEDIA_AD_TYPES = "ad_types";
    public static final String TAG_DISABLE_PSA = "disable_psa";
    public static final String TAG_PREBID = "prebid";
    public static final String TAG_CODE = "code";
    public static final String USER = "user";
    public static final String USER_AGE = "age";
    public static final String USER_GENDER = "gender";
    public static final String USER_LANGUAGE = "language";
    public static final String DEVICE = "device";
    public static final String DEVICE_USERAGENT = "useragent";
    public static final String DEVICE_GEO = "geo";
    public static final String GEO_LAT = "lat";
    public static final String GEO_LON = "lng";
    public static final String GEO_AGE = "loc_age";
    public static final String GEO_PREC = "loc_precision";
    public static final String DEVICE_MAKE = "make";
    public static final String DEVICE_MODEL = "model";
    public static final String DEVICE_OS = "os";
    public static final String DEVICE_CARRIER = "carrier";
    public static final String DEVICE_CONNECTIONTYPE = "connectiontype";
    public static final String DEVICE_MCC = "mcc";
    public static final String DEVICE_MNC = "mnc";
    public static final String DEVICE_LMT = "limit_ad_tracking";
    public static final String DEVICE_DEVICE_ID = "device_id";
    public static final String DEVICE_DEVTIME = "devtime";
    public static final String DEVICE_ID_AAID = "aaid";
    public static final String APP = "app";
    public static final String APP_ID = "appid";
    public static final String KEYWORDS = "keywords";
    public static final String MEMBER_ID = "member_id";
    public static final String KEYVAL_KEY = "key";
    public static final String KEYVAL_VALUE = "value";
    public static final String ALLOWED_TYPE_BANNER = "banner";
    public static final String ALLOWED_TYPE_VIDEO = "video";


    private static ArrayList<Pair<String, String>> customKeywords = new ArrayList<Pair<String, String>>();
    private WeakReference<AdRequester> requester; // The instance of AdRequester which is filing this request.
    private RequestParameters params;

    private static final UTAdResponse HTTP_ERROR = new UTAdResponse(true);
    public static final String os = "android";

    public UTAdRequest(AdRequester adRequester) {
        this.requester = new WeakReference<AdRequester>(adRequester);
         params = adRequester.getRequestParams();
        if (params != null) {
            AdvertistingIDUtil.retrieveAndSetAAID(params.getContext());

            SharedNetworkManager networkManager = SharedNetworkManager.getInstance(params.getContext());
            if (!networkManager.isConnected(params.getContext())) {
                fail(ResultCode.NETWORK_ERROR);
                Clog.i(Clog.httpReqLogTag, "Connection Error");
                this.cancel(true);
            }
        } else {
            Clog.i(Clog.httpReqLogTag, "Internal Error");
            fail(ResultCode.INTERNAL_ERROR);
            this.cancel(true);
        }

    }

    private void fail(ResultCode code) {
        AdRequester requester = this.requester.get();
        if (requester != null) {
            requester.onReceiveUTResponse(null, code);
        }
        Clog.clearLastResponse();
    }


    @Override
    protected UTAdResponse doInBackground(Void... params) {

        AdRequester requester = this.requester.get();
        if (requester != null) {
            RequestParameters parameters = requester.getRequestParams();
                try {

                    String baseUrl = Settings.BASE_URL_UT;
                    if(Settings.useUniversalTagV2){
                        baseUrl = Settings.BASE_URL_UT_V2;
                    }
                    URL url = new URL(baseUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("User-Agent", Settings.getSettings().ua);
                    conn.setRequestMethod("POST");

                    conn.setConnectTimeout(Settings.HTTP_CONNECTION_TIMEOUT);
                    // Make post request
                    String postData = getPostData();
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(postData);
                    wr.flush();

                    // Start the connection
                    conn.connect();

                    // Read request response
                    int httpResult = conn.getResponseCode();
                    StringBuilder builder = new StringBuilder();
                    if (httpResult == HttpURLConnection.HTTP_OK) {
                        InputStream is = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                        reader.close();
                        is.close();
                    }else{
                        Clog.d(Clog.httpRespLogTag, Clog.getString(R.string.http_bad_status, httpResult));
                        return UTAdRequest.HTTP_ERROR;
                    }
                    String result = builder.toString();

                    Clog.i(Clog.httpRespLogTag, "RESPONSE - "+result);
                    CookieManager cookieManager = new CookieManager();
                    CookieHandler.setDefault(cookieManager);

                    WebviewUtil.httpCookieSync(cookieManager.getCookieStore().getCookies());
                    if (result.equals("")) {
                        // just log and return a valid AdResponse object so that it is
                        // marked as UNABLE_TO_FILL
                        Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.response_blank));
                    }
                    return new UTAdResponse(result);
                }  catch (SocketTimeoutException e) {
                    Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_timeout));
                } catch (IOException e) {
                    Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_io));
                } catch (SecurityException se) {
                    Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.permissions_internet));
                } catch (IllegalArgumentException ie) {
                    Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_unknown));
                } catch (Exception e) {
                    e.printStackTrace();
                    Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.unknown_exception));
                }
            }
        return null;
    }


    @Override
    protected void onPostExecute(UTAdResponse result) {
        // check for invalid responses
        if (result == null) {
            Clog.i(Clog.httpRespLogTag, Clog.getString(R.string.no_response));
            fail(ResultCode.NETWORK_ERROR);
            return; // http request failed
        }
        if (result.isHttpError()) {
            fail(ResultCode.NETWORK_ERROR);
            return;
        }

        AdRequester requester = this.requester.get();
        if (requester != null) {
            // add the orientation extra for interstitial ads
//            if (requester.getRequestParams() != null) {
//                result.addToExtras(ServerResponse.EXTRAS_KEY_ORIENTATION, requester.getRequestParams().getOrientation());
//            }
            requester.onReceiveUTResponse(result, ResultCode.SUCCESS);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCancelled(UTAdResponse serverResponse) {
        super.onCancelled(serverResponse);
        Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.cancel_request));
    }

    // Package only for testing purpose
    String getPostData() {
        Context context = params.getContext();
        if (context != null) {
            // Try to retrieve aaid and limitedAdTracking if they were not set
            AdvertistingIDUtil.retrieveAndSetAAID(context);
        }
        JSONObject postData = new JSONObject();
        try {
            // add tags
            JSONArray tags = getTagsObject(postData);
            if (tags != null && tags.length() > 0) {
                postData.put(TAGS, tags);
            }
            // add user
            JSONObject user = getUserObject();
            if (user != null && user.length() > 0) {
                postData.put(USER, user);
            }
            // add device
            JSONObject device = getDeviceObject();
            if (device != null && device.length() > 0) {
                postData.put(DEVICE, device);
            }
            // add app
            JSONObject app = getAppObject();
            if (device != null && device.length() > 0) {
                postData.put(APP, app);
            }
            // add custom keywords
            JSONArray keywordsArray = getCustomKeywordsArray();
            if (keywordsArray != null && keywordsArray.length() > 0) {
                postData.put(KEYWORDS, keywordsArray);
            }
        } catch (JSONException e) {
            Clog.e(Clog.httpRespLogTag, "JSONException: "+e.getMessage());
        }
        Clog.i(Clog.httpRespLogTag, "POST data: " + postData.toString());
        return postData.toString();
    }

    private JSONArray getTagsObject(JSONObject postData) {
        JSONArray tags = new JSONArray();
        JSONObject tag = new JSONObject();
        try {
            if(!StringUtil.isEmpty(params.getInvCode()) && params.getMemberID()>0){
                tag.put(TAG_CODE, params.getInvCode());
                postData.put(MEMBER_ID, params.getMemberID());
            }else if (!StringUtil.isEmpty(params.getPlacementID())) {
                tag.put(TAG_ID, StringUtil.getIntegerValue(params.getPlacementID()));
            }else{
                tag.put(TAG_ID, 0);
            }

            ArrayList<AdSize> allowedSizes = params.getAllowedSizes();

            JSONArray sizes = new JSONArray();
            if(allowedSizes != null && allowedSizes.size() > 0) {
                for (AdSize s : allowedSizes) {
                    JSONObject size = new JSONObject();
                    size.put(SIZE_WIDTH, s.width());
                    size.put(SIZE_HEIGHT, s.height());
                    sizes.put(size);
                }
            }
            addScreenSize(sizes);
            addOneByOneSize(sizes);

            tag.put(TAG_SIZES, sizes);

            tag.put(TAG_ALLOW_SMALLER_SIZES, false);

            JSONArray allowedAdTypes = new JSONArray();
            allowedAdTypes.put(ALLOWED_TYPE_BANNER);
            allowedAdTypes.put(ALLOWED_TYPE_VIDEO);

            tag.put(TAG_ALLOWED_MEDIA_AD_TYPES, allowedAdTypes);
            tag.put(TAG_PREBID, false);
            tag.put(TAG_DISABLE_PSA, !params.getShouldServePSAs());
        } catch (JSONException e) {
            Clog.e(Clog.baseLogTag, "Exception: "+e.getMessage());
        }
        if (tag.length() > 0) {
            tags.put(tag);
        }
        return tags;
    }


    private void addScreenSize(JSONArray sizes) throws JSONException {
        int maxHeight = params.getContainerHeight();
        int maxWidth = params.getContainerWidth();
        if (maxHeight > 0 && maxWidth > 0) {
            JSONObject size = new JSONObject();
            size.put(SIZE_WIDTH, maxWidth);
            size.put(SIZE_HEIGHT, maxHeight);
            sizes.put(size);
        }
    }


    private void addOneByOneSize(JSONArray sizes) throws JSONException {
        JSONObject size = new JSONObject();
        size.put(SIZE_WIDTH, 1);
        size.put(SIZE_HEIGHT, 1);
        sizes.put(size);
    }

    private JSONObject getUserObject() {
        JSONObject user = new JSONObject();
        try {
            if (StringUtil.getIntegerValue(params.getAge()) > 0) {
                user.put(USER_AGE, StringUtil.getIntegerValue(params.getAge()));
            }

            AdView.GENDER gender = params.getGender();
            int g = 0;
            switch (gender) {
                case FEMALE:
                    g = 2;
                    break;
                case MALE:
                    g = 1;
                    break;
                case UNKNOWN:
                    g = 0;
                    break;
            }
            user.put(USER_GENDER, g);
            if (!StringUtil.isEmpty(Settings.getSettings().language)) {
                user.put(USER_LANGUAGE, Settings.getSettings().language);
            }
        } catch (JSONException e) {
        }
        return user;
    }

    private JSONObject getDeviceObject() {
        JSONObject device = new JSONObject();
        try {
            // Device make
            if (!StringUtil.isEmpty(Settings.getSettings().deviceMake))
                device.put(DEVICE_MAKE, Settings.getSettings().deviceMake);
            // Device model
            if (!StringUtil.isEmpty(Settings.getSettings().deviceModel))
                device.put(DEVICE_MODEL, Settings.getSettings().deviceModel);
            // POST data that requires context
            Context context = params.getContext();
            if (context != null) {
                // Default User Agent
                if (!StringUtil.isEmpty(Settings.getSettings().ua)) {
                    device.put(DEVICE_USERAGENT, Settings.getSettings().ua);
                }

                TelephonyManager telephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                // Get mobile country codes

                if (Settings.getSettings().mcc == null || Settings.getSettings().mnc == null) {
                    String networkOperator = telephonyManager.getNetworkOperator();
                    if (!StringUtil.isEmpty(networkOperator)) {
                        try {
                            Settings.getSettings().mcc = networkOperator.substring(0, 3);
                            Settings.getSettings().mnc = networkOperator.substring(3);
                        } catch (Exception e) {
                            // Catches IndexOutOfBoundsException
                            Settings.getSettings().mcc = null;
                            Settings.getSettings().mnc = null;
                        }
                    }
                }
                if (Settings.getSettings().mcc != null && Settings.getSettings().mnc != null) {
                    device.put(DEVICE_MNC, StringUtil.getIntegerValue(Settings.getSettings().mnc));
                    device.put(DEVICE_MCC, StringUtil.getIntegerValue(Settings.getSettings().mcc));
                }

                // Get carrier
                if (Settings.getSettings().carrierName == null) {
                    try {
                        Settings.getSettings().carrierName = telephonyManager.getNetworkOperatorName();
                    } catch (SecurityException ex) {
                        // Some phones require READ_PHONE_STATE permission just ignore name
                        Settings.getSettings().carrierName = "";
                    }
                }
                if (!StringUtil.isEmpty(Settings.getSettings().carrierName))
                    device.put(DEVICE_CARRIER, Settings.getSettings().carrierName);

                // check connection type
                int connection_type = 0;
                ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnected()) {
                    NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (wifi != null) {
                        connection_type = wifi.isConnected() ? 1 : 2;
                    }
                }
                device.put(DEVICE_CONNECTIONTYPE, connection_type);
            }

            Double lat, lon;
            Integer locDataAge, locDataPrecision;
            Location lastLocation = null;
            Location appLocation = SDKSettings.getLocation();
            // Do we have access to location?
            if (SDKSettings.getLocationEnabled()) {

                // First priority is the app supplied location
                if (appLocation != null) {
                    lastLocation = appLocation;
                } else if (context != null
                        && (context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED
                        || context.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED)) {
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
                    Clog.w(Clog.baseLogTag, "Location permissions ACCESS_COARSE_LOCATION and/or ACCESS_FINE_LOCATION are not set in the host app. This may affect demand.");
                }
            }

            // Set the location info back to the application
            if (appLocation != lastLocation) {
                SDKSettings.setLocation(lastLocation);
            }

            if (lastLocation != null) {
                if (SDKSettings.getLocationDecimalDigits() <= -1) {
                    lat = lastLocation.getLatitude();
                    lon = lastLocation.getLongitude();
                } else {
                    lat = Double.parseDouble(String.format(Locale.ENGLISH, "%." + SDKSettings.getLocationDecimalDigits() + "f", lastLocation.getLatitude()));
                    lon = Double.parseDouble(String.format(Locale.ENGLISH, "%." + SDKSettings.getLocationDecimalDigits() + "f", lastLocation.getLongitude()));
                }
                locDataPrecision = Math.round(lastLocation.getAccuracy());
                //Don't report location data from the future
                locDataAge = (int) Math.max(0, (System.currentTimeMillis() - lastLocation.getTime()));
            } else {
                lat = null;
                lon = null;
                locDataAge = null;
                locDataPrecision = null;
            }
            JSONObject geo = new JSONObject();
            if (lat != null && lon != null) {
                geo.put(GEO_LAT, lat);
                geo.put(GEO_LON, lon);
                if (locDataAge != null) geo.put(GEO_AGE, locDataAge);
                if (locDataPrecision != null) geo.put(GEO_PREC, locDataPrecision);
            }
            if (geo.length() > 0) {
                device.put(DEVICE_GEO, geo);
            }

            // devtime
            long dev_time = System.currentTimeMillis();
            device.put(DEVICE_DEVTIME, dev_time);

            // limited ad tracking
            device.put(DEVICE_LMT, Settings.getSettings().limitTrackingEnabled);

            if (!StringUtil.isEmpty(Settings.getSettings().aaid)) {
                // device id
                JSONObject device_id = new JSONObject();
                device_id.put(DEVICE_ID_AAID, Settings.getSettings().aaid);
                device.put(DEVICE_DEVICE_ID, device_id);
            }

            // os
            device.put(DEVICE_OS, os);
        } catch (JSONException e) {
        }

        return device;
    }

    private JSONObject getAppObject() {
        if (StringUtil.isEmpty(Settings.getSettings().app_id)) {
            Context context = params.getContext();
            if (context != null) {
                Settings.getSettings().app_id = context.getApplicationContext().getPackageName();
            }
        }
        JSONObject app = new JSONObject();
        try {
            if (!StringUtil.isEmpty(Settings.getSettings().app_id)) {
                app.put(APP_ID, Settings.getSettings().app_id);
            }
        } catch (JSONException e) {
        }
        return app;
    }

    private JSONArray getCustomKeywordsArray() {
        JSONArray keywords = new JSONArray();
        try {
            // add custom parameters if there are any
            ArrayList<Pair<String, String>> customKeywords = params.getCustomKeywords();
            if (customKeywords != null) {
                for (Pair<String, String> pair : customKeywords) {
                    if (!StringUtil.isEmpty(pair.first) && !StringUtil.isEmpty(pair.second)) {
                        JSONObject key_val = new JSONObject();
                        key_val.put(KEYVAL_KEY, pair.first);
                        key_val.put(KEYVAL_VALUE, pair.second);
                        keywords.put(key_val);
                    }
                }
            }
        } catch (JSONException e) {
        }
        return keywords;
    }

}
