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
import com.appnexus.opensdk.utils.NewSettings;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;

import org.apache.http.StatusLine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

class NewAdRequest extends AsyncTask<Void, Integer, NewAdResponse> {

    private static ArrayList<Pair<String, String>> customKeywords = new ArrayList<Pair<String, String>>();
    private WeakReference<AdRequester> requester; // The instance of AdRequester which is filing this request.
    private RequestParameters params;

    private static final NewAdResponse HTTP_ERROR = new NewAdResponse(true);

    public NewAdRequest(AdRequester adRequester) {
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
            requester.failed(code);
        }
        Clog.clearLastResponse();
    }


    @Override
    protected NewAdResponse doInBackground(Void... params) {

        AdRequester requester = this.requester.get();
        if (requester != null) {
            RequestParameters parameters = requester.getRequestParams();
                try {

                    URL url = new URL(NewSettings.BASEURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
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
                    }
                    String result = builder.toString();

                    Clog.e(Clog.vastLogTag, "RESPONSE -- "+result);
                    /**
                     * TODO: Cookie sync needs to be implemented
                     */
//                    WebviewUtil.cookieSync(h.getCookieStore().getCookies());
                    if (result.equals("")) {
                        // just log and return a valid AdResponse object so that it is
                        // marked as UNABLE_TO_FILL
                        Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.response_blank));
                    }
                    return new NewAdResponse(result, parameters.getMediaType());
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
    protected void onPostExecute(NewAdResponse result) {
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
            if (requester.getRequestParams() != null) {
                result.addToExtras(ServerResponse.EXTRAS_KEY_ORIENTATION, requester.getRequestParams().getOrientation());
            }
            requester.onReceiveNewServerResponse(result);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCancelled(NewAdResponse serverResponse) {
        super.onCancelled(serverResponse);
        Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.cancel_request));
    }


    // Package only for testing purpose
    String getPostData() {
        Context context = NewSettings.getContext();
        if (context != null) {
            // Try to retrieve aaid and limitedAdTracking if they were not set
            AdvertistingIDUtil.retrieveAndSetAAID(context);
        }
        JSONObject postData = new JSONObject();
        try {
            // add tags
            JSONArray tags = getTagsObject();
            if (tags != null && tags.length() > 0) {
                postData.put(NewSettings.TAGS, tags);
            }
            // add user
            JSONObject user = getUserObject();
            if (user != null && user.length() > 0) {
                postData.put(NewSettings.USER, user);
            }
            // add device
            JSONObject device = getDeviceObject();
            if (device != null && device.length() > 0) {
                postData.put(NewSettings.DEVICE, device);
            }
            // add app
            JSONObject app = getAppObject();
            if (device != null && device.length() > 0) {
                postData.put(NewSettings.APP, app);
            }
            // add custom keywords
            JSONArray keywordsArray = getCustomKeywordsArray();
            if (keywordsArray != null && keywordsArray.length() > 0) {
                postData.put(NewSettings.KEYWORDS, keywordsArray);
            }
        } catch (JSONException e) {
            Clog.e(Clog.httpRespLogTag, "JSONException: "+e.getMessage());
        }
        Clog.e(Clog.httpRespLogTag, "POST data: " + postData.toString());
        return postData.toString();
    }

    private JSONArray getTagsObject() {
        JSONArray tags = new JSONArray();
        JSONObject tag = new JSONObject();
        try {
            /**
             * TODO: Add integer parse validation
             */
            tag.put(NewSettings.TAG_ID, Integer.parseInt(params.getPlacementID()));

//            if (params.getAdWidth() > 0 && params.getAdHeight() > 0) {
                JSONObject size = new JSONObject();
                size.put(NewSettings.SIZE_WIDTH, params.getAdWidth());
                size.put(NewSettings.SIZE_HEIGHT, params.getAdHeight());
                JSONArray sizes = new JSONArray();
                sizes.put(size);
                tag.put(NewSettings.TAG_SIZES, sizes);
                tag.put(NewSettings.TAG_ALLOW_SMALLER_SIZES, false);
//            }
            JSONArray allowedAdTypes = new JSONArray();
            allowedAdTypes.put(NewSettings.BANNER);
            allowedAdTypes.put(NewSettings.VAST);
            tag.put(NewSettings.TAG_ALLOWED_MEDIA_AD_TYPES, allowedAdTypes);
            tag.put(NewSettings.TAG_PREBID, false);
            tag.put(NewSettings.TAG_DISABLE_PSA, true);
        } catch (JSONException e) {
        }
        if (tag.length() > 0) {
            tags.put(tag);
        }
        return tags;
    }

    private JSONObject getUserObject() {
        JSONObject user = new JSONObject();
        try {
            if (StringUtil.getIntegerValue(params.getAge()) > 0) {
                user.put(NewSettings.USER_AGE, StringUtil.getIntegerValue(params.getAge()));
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
            user.put(NewSettings.USER_GENDER, g);
            if (!StringUtil.isEmpty(NewSettings.language)) {
                user.put(NewSettings.USER_LANGUAGE, NewSettings.language);
            }
        } catch (JSONException e) {
        }
        return user;
    }

    private JSONObject getDeviceObject() {
        JSONObject device = new JSONObject();
        try {
            // Device make
            if (!StringUtil.isEmpty(NewSettings.deviceMake))
                device.put(NewSettings.DEVICE_MAKE, NewSettings.deviceMake);
            // Device model
            if (!StringUtil.isEmpty(NewSettings.deviceModel))
                device.put(NewSettings.DEVICE_MODEL, NewSettings.deviceModel);
            // POST data that requires context
            Context context = NewSettings.getContext();
            if (context != null) {
                // Default User Agent
                if (!StringUtil.isEmpty(NewSettings.getUseragent())) {
                    device.put(NewSettings.DEVICE_USERAGENT, NewSettings.getUseragent());
                }

                TelephonyManager telephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                // Get mobile country codes
                if (NewSettings.getMCC() < 0 || NewSettings.getMNC() < 0) {
                    String networkOperator = telephonyManager.getNetworkOperator();
                    if (!StringUtil.isEmpty(networkOperator)) {
                        try {
                            NewSettings.setMCC(Integer.parseInt(networkOperator.substring(0, 3)));
                            NewSettings.setMNC(Integer.parseInt(networkOperator.substring(3)));
                        } catch (Exception e) {
                            // Catches NumberFormatException and StringIndexOutOfBoundsException
                            NewSettings.setMCC(-1);
                            NewSettings.setMNC(-1);
                        }
                    }
                }
                if (NewSettings.getMCC() > 0 && NewSettings.getMNC() > 0) {
                    device.put(NewSettings.DEVICE_MNC, NewSettings.getMNC());
                    device.put(NewSettings.DEVICE_MCC, NewSettings.getMCC());
                }

                // Get carrier
                if (NewSettings.getCarrierName() == null) {
                    try {
                        NewSettings.setCarrierName(telephonyManager.getNetworkOperatorName());
                    } catch (SecurityException ex) {
                        // Some phones require READ_PHONE_STATE permission just ignore name
                        NewSettings.setCarrierName("");
                    }
                }
                if (!StringUtil.isEmpty(NewSettings.getCarrierName()))
                    device.put(NewSettings.DEVICE_CARRIER, NewSettings.getCarrierName());

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
                device.put(NewSettings.DEVICE_CONNECTIONTYPE, connection_type);
            }
            // Location NewSettings
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
                geo.put(NewSettings.GEO_LAT, lat);
                geo.put(NewSettings.GEO_LON, lon);
                if (locDataAge != null) geo.put(NewSettings.GEO_AGE, locDataAge);
                if (locDataPrecision != null) geo.put(NewSettings.GEO_PREC, locDataPrecision);
            }
            if (geo.length() > 0) {
                device.put(NewSettings.DEVICE_GEO, geo);
            }

            // devtime
            long dev_time = System.currentTimeMillis();
            device.put(NewSettings.DEVICE_DEVTIME, dev_time);

            // limited ad tracking
            device.put(NewSettings.DEVICE_LMT, NewSettings.isLimitAdTracking());
            if (!NewSettings.isLimitAdTracking() && !StringUtil.isEmpty(NewSettings.getAAID())) {
                // device id
                JSONObject device_id = new JSONObject();
                device_id.put(NewSettings.DEVICE_ID_AAID, NewSettings.getAAID());
                device.put(NewSettings.DEVICE_DEVICE_ID, device_id);
            }

            // os
            device.put(NewSettings.DEVICE_OS, NewSettings.os);
        } catch (JSONException e) {
        }

        return device;
    }

    private JSONObject getAppObject() {
        if (StringUtil.isEmpty(NewSettings.getAppID())) {
            Context context = NewSettings.getContext();
            if (context != null) {
                NewSettings.setAppID(context.getApplicationContext()
                        .getPackageName());
            }
        }
        JSONObject app = new JSONObject();
        try {
            if (!StringUtil.isEmpty(NewSettings.getAppID())) {
                app.put(NewSettings.APP_ID, NewSettings.getAppID());
            }
        } catch (JSONException e) {
        }
        return app;
    }

    void clearCustomKeywords() {
        customKeywords.clear();
    }

    private JSONArray getCustomKeywordsArray() {
        JSONArray keywords = new JSONArray();
        try {
            // add custom parameters if there are any
            ArrayList<Pair<String, String>> customKeywords = getCustomKeywords();
            if (customKeywords != null) {
                for (Pair<String, String> pair : customKeywords) {
                    if (!StringUtil.isEmpty(pair.first) && !StringUtil.isEmpty(pair.second)) {
                        JSONObject key_val = new JSONObject();
                        key_val.put(NewSettings.KEYVAL_KEY, pair.first);
                        key_val.put(NewSettings.KEYVAL_VALUE, pair.second);
                        keywords.put(key_val);
                    }
                }
            }
        } catch (JSONException e) {
        }
        return keywords;
    }


    /**
     * Retrieve the array of custom keywords associated with the DFPCacheManager.
     *
     * @return The current list of key-value pairs of custom keywords.
     */
    public static ArrayList<Pair<String, String>> getCustomKeywords() {
        return customKeywords;
    }

    /**
     * Remove a custom keyword from the DFPCacheManager. Use this to remove a keyword
     * previously set using addCustomKeywords.
     *
     * @param key The key to remove; this should not be null or empty.
     */
    public static void removeCustomKeyword(String key) {
        if (StringUtil.isEmpty(key))
            return;
        for (int i = 0; i < customKeywords.size(); i++) {
            Pair<String, String> pair = customKeywords.get(i);
            if (pair.first.equals(key)) {
                customKeywords.remove(i);
                break;
            }
        }
    }

    /**
     * Add a custom keyword for customized targeting.
     *
     * @param key   The key to add; this cannot be null or empty.
     * @param value The value to add; this cannot be null or empty.
     */
    public static void addCustomKeyword(String key, String value) {
        if (StringUtil.isEmpty(key) || value == null)
            return;
        customKeywords.add(new Pair<String, String>(key, value));
    }
}
