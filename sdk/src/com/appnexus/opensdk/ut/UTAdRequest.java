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

import com.appnexus.opensdk.R;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.SharedNetworkManager;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.WebviewUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class UTAdRequest extends AsyncTask<Void, Integer, UTAdResponse> {


    private WeakReference<UTAdRequester> requester; // The instance of AdRequester which is filing this request.
    private UTRequestParameters requestParams;

    public UTAdRequest(UTAdRequester adRequester) {
        this.requester = new WeakReference<UTAdRequester>(adRequester);
        requestParams = adRequester.getRequestParams();
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
        UTAdRequester requester = this.requester.get();
        if (requester != null) {
            requester.failed(code);
        }
        Clog.clearLastResponse();
    }


    @Override
    protected UTAdResponse doInBackground(Void... params) {

        UTAdRequester requester = this.requester.get();
        if (requester != null) {
            try {

                String baseUrl = requester.isHttpsEnabled()?UTConstants.REQUEST_BASE_URL_UT_V2.replace("http:", "https:") : UTConstants.REQUEST_BASE_URL_UT_V2;
                URL url = new URL(baseUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("User-Agent", Settings.getSettings().ua);
                String cookieString = WebviewUtil.getCookie();
                if (!TextUtils.isEmpty(cookieString)) {
                    conn.setRequestProperty("Cookie",cookieString);
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
                    return new UTAdResponse(result, conn.getHeaderFields(), requestParams.getMediaType(),requestParams.getOrientation());
                } else {
                    Clog.d(Clog.httpRespLogTag, Clog.getString(R.string.http_bad_status, httpResult));
                    return new UTAdResponse(true);
                }

            } catch (SocketTimeoutException e) {
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

        UTAdRequester requester = this.requester.get();
        if (requester != null) {
            // add the orientation extra for interstitial ads
            requester.onReceiveUTResponse(result);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCancelled(UTAdResponse serverResponse) {
        super.onCancelled(serverResponse);
        Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.cancel_request));
    }
}
