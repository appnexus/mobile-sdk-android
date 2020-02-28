/*
 *    Copyright 2017 APPNEXUS INC
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

package com.appnexus.opensdk.ut;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;

import com.appnexus.opensdk.ANMultiAdRequest;
import com.appnexus.opensdk.Ad;
import com.appnexus.opensdk.AdViewRequestManager;
import com.appnexus.opensdk.R;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.SDKSettings;
import com.appnexus.opensdk.SharedNetworkManager;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.JsonUtil;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.WebviewUtil;

import org.json.JSONArray;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UTAdRequest extends AsyncTask<Void, Integer, HashMap<String, UTAdResponse>> {


    private WeakReference<UTAdRequester> requester; // The instance of AdRequester which is filing this request.
    private UTRequestParameters requestParams;
    private ANMultiAdRequest anMultiAdRequest;

    public UTAdRequest(UTAdRequester adRequester) {
        this.requester = new WeakReference<UTAdRequester>(adRequester);
        anMultiAdRequest = adRequester instanceof AdViewRequestManager ? ((AdViewRequestManager) adRequester).getMultiAdRequest() : null;
        requestParams = anMultiAdRequest == null ? adRequester.getRequestParams() : anMultiAdRequest.getRequestParameters();
        if (requestParams != null) {
            SharedNetworkManager networkManager = SharedNetworkManager.getInstance(requestParams.getContext());
            if (!networkManager.isConnected(requestParams.getContext())) {
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
        if (anMultiAdRequest != null && anMultiAdRequest.isMARRequestInProgress()) {
            anMultiAdRequest.onRequestFailed(code);
            ArrayList<WeakReference<Ad>> adUnitList = anMultiAdRequest.getAdUnitList();
            for (WeakReference<Ad> adWeakReference : adUnitList) {
                Ad ad = adWeakReference.get();
                if (ad != null) {
                    UTAdRequester requester = new AdViewRequestManager(ad);
                    if (requester != null) {
                        requester.failed(code, null);
                    }
                }
            }
            return;
        }
        UTAdRequester requester = this.requester.get();
        if (requester != null) {
            requester.failed(code, null);
        }
        Clog.clearLastResponse();
    }


    @Override
    protected HashMap<String, UTAdResponse> doInBackground(Void... params) {

//        UTAdRequester requester = this.requester.get();
//        if (requester != null) {
        try {

            String baseUrl = SDKSettings.isHttpsEnabled() ? UTConstants.REQUEST_BASE_URL_UT.replace("http:", "https:") : UTConstants.REQUEST_BASE_URL_UT;
            URL url = new URL(baseUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", Settings.getSettings().ua);
            String cookieString = WebviewUtil.getCookie();
            if (!TextUtils.isEmpty(cookieString)) {
                conn.setRequestProperty("Cookie", cookieString);
            }
            conn.setRequestMethod("POST");

            conn.setConnectTimeout(Settings.HTTP_CONNECTION_TIMEOUT);
            conn.setReadTimeout(Settings.HTTP_SOCKET_TIMEOUT);

            // Make post request
            String postData = requestParams.getPostData();
            Clog.setLastRequest(postData);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(postData);
            wr.flush();

            // Start the connection
            conn.connect();

            // Read request response
            int httpResult = conn.getResponseCode();

            HashMap<String, UTAdResponse> adResponseMap = new HashMap<>();
            if (httpResult == HttpURLConnection.HTTP_OK) {
                StringBuilder builder = new StringBuilder();
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();
                is.close();

                String result = builder.toString();

                Clog.i(Clog.httpRespLogTag, "RESPONSE - " + result);
                Map<String, List<String>> headers = conn.getHeaderFields();
                WebviewUtil.cookieSync(headers);
                if (anMultiAdRequest == null) {
                    adResponseMap.put(requestParams.getUUID(), new UTAdResponse(result, conn.getHeaderFields(), requestParams.getMediaType(), requestParams.getOrientation()));
                } else {
                    LinkedHashMap<String, JSONObject> jsonMap = new LinkedHashMap<>();
                    JSONObject response = new JSONObject(result);
                    JSONArray tagsArray = JsonUtil.getJSONArray(response, "tags");
                    for (int i = 0; i < tagsArray.length(); i++) {
                        JSONObject tag = tagsArray.getJSONObject(i);
                        jsonMap.put(JsonUtil.getJSONString(tag, "uuid"), tag);
                    }
                    ArrayList<WeakReference<Ad>> adUnitList = anMultiAdRequest.getAdUnitList();
                    for (WeakReference<Ad> adWeakReference : adUnitList) {
                        Ad ad = adWeakReference.get();
                        if (ad != null) {
                            UTRequestParameters requestParameters = ad.getRequestParameters();
                            adResponseMap.put(ad.getRequestParameters().getUUID(), new UTAdResponse(result, jsonMap.get(ad.getRequestParameters().getUUID()), conn.getHeaderFields(), requestParameters.getMediaType(), requestParameters.getOrientation()));
                        }
                    }
                }
            } else {
                Clog.d(Clog.httpRespLogTag, Clog.getString(R.string.http_bad_status, httpResult));
                adResponseMap.put(requestParams.getUUID(), new UTAdResponse(true));
            }
            return adResponseMap;

        } catch (SocketTimeoutException e) {
            Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_timeout));
        } catch (IOException e) {
            Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_io));
        } catch (SecurityException se) {
            Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.permissions_internet));
        } catch (IllegalArgumentException ie) {
            Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_unknown));
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_ooo));
        } catch (Exception e) {
            e.printStackTrace();
            Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.unknown_exception));
        }
        return null;
    }


    @Override
    protected void onPostExecute(HashMap<String, UTAdResponse> adResponseMap) {
        // check for invalid responses
        if (anMultiAdRequest == null) {

            if (adResponseMap == null) {
                Clog.i(Clog.httpRespLogTag, Clog.getString(R.string.no_response));
                fail(ResultCode.INVALID_REQUEST);
                return;
            }

            if (adResponseMap.size() == 1) {
                UTAdResponse result = adResponseMap.get(requestParams.getUUID());
                if (result == null) {
                    Clog.i(Clog.httpRespLogTag, Clog.getString(R.string.no_response));
                    fail(ResultCode.NETWORK_ERROR);
                    return; // http request failed
                }
                if (result.isHttpError()) {
                    fail(ResultCode.NETWORK_ERROR);
                    return;
                }

                UTAdRequester requester = this.requester.get();
                if (requester != null) {
                    // add the orientation extra for interstitial ads
                    requester.onReceiveUTResponse(result);
                }
            }
        } else if (anMultiAdRequest != null) {
            if (adResponseMap == null) {
                Clog.e(Clog.SRMLogTag, "FAILED: " + ResultCode.INVALID_REQUEST);
                anMultiAdRequest.onRequestFailed(ResultCode.INVALID_REQUEST);
            } else {
                anMultiAdRequest.onMARLoadCompleted();
            }
            ArrayList<WeakReference<Ad>> adUnitList = anMultiAdRequest.getAdUnitList();
            for (WeakReference<Ad> adWeakReference : adUnitList) {
                Ad ad = adWeakReference.get();
                Clog.d(Clog.SRMLogTag, "RECIEVED: " + ad);
                if (ad != null) {
                    UTAdRequester requester = new AdViewRequestManager(ad);
                    ad.getMultiAd().setRequestManager(requester);
                    if (adResponseMap == null) {
                        Clog.e(Clog.SRMLogTag, "FAILED: " + ResultCode.INVALID_REQUEST);
                        requester.failed(ResultCode.INVALID_REQUEST, null);
                        continue;
                    }
                    UTAdResponse result = adResponseMap.get(ad.getRequestParameters().getUUID());
                    Clog.d(Clog.SRMLogTag, "RECIEVED: RESPONSE: " + result);

                    if (requester != null) {
                        if (result == null) {
                            Clog.e(Clog.SRMLogTag, "FAILED: " + ResultCode.NETWORK_ERROR);
                            requester.failed(ResultCode.NETWORK_ERROR, null);
                            continue;
                        }
                        if (result.isHttpError()) {
                            Clog.e(Clog.SRMLogTag, "FAILED: " + ResultCode.NETWORK_ERROR);
                            requester.failed(ResultCode.NETWORK_ERROR, null);
                            continue;
                        }
                        Clog.e(Clog.SRMLogTag, "SUCCESS: " + ad);
                        requester.onReceiveUTResponse(result);
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCancelled(HashMap<String, UTAdResponse> serverResponse) {
        super.onCancelled(serverResponse);
        Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.cancel_request));
    }
}
