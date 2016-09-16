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
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;

import com.appnexus.opensdk.utils.AdvertistingIDUtil;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.WebviewUtil;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

class AdRequest extends AsyncTask<Void, Integer, ServerResponse> {

    private WeakReference<AdRequester> requester; // The instance of AdRequester which is filing this request.

    private static final ServerResponse HTTP_ERROR
            = new ServerResponse(true);

    public AdRequest(AdRequester adRequester) {
        this.requester = new WeakReference<AdRequester>(adRequester);
        RequestParameters params = adRequester.getRequestParams();
        if (params != null) {
            AdvertistingIDUtil.retrieveAndSetAAID(params.getContext());

            SharedNetworkManager networkManager = SharedNetworkManager.getInstance(params.getContext());
            if (!networkManager.isConnected(params.getContext())) {
                fail(ResultCode.NETWORK_ERROR);
                this.cancel(true);
            }
        } else {
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
    protected ServerResponse doInBackground(Void... params) {

        AdRequester requester = this.requester.get();
        if (requester != null) {
            RequestParameters parameters = requester.getRequestParams();
            if (parameters != null) {
                try {
                    String query_string = parameters.getRequestUrl();

                    Clog.setLastRequest(query_string);

                    Clog.d(Clog.httpReqLogTag,
                            Clog.getString(R.string.fetch_url, query_string));

                    //  Create and connect to HTTP service
                    HttpURLConnection connection = createConnection(new URL(query_string));
                    setConnectionParams(connection);
                    connection.connect();


                    if (!httpShouldContinue(connection.getResponseCode())) {
                        return AdRequest.HTTP_ERROR;
                    }

                    //Response parsing
                    StringBuilder builder = new StringBuilder();
                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    reader.close();
                    is.close();
                    String out = builder.toString();
                    if (out.equals("")) {
                        // just log and return a valid AdResponse object so that it is
                        // marked as UNABLE_TO_FILL
                        Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.response_blank));
                    }

                    //Cookie Sync here.
                    WebviewUtil.cookieSync(connection.getHeaderFields());

                    return new ServerResponse(out, connection.getHeaderFields(), parameters.getMediaType());
                } catch (MalformedURLException e) {
                    Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_url_malformed));
                } catch (IOException e) {
                    Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_io));
                } catch (SecurityException se) {
                    Clog.e(Clog.httpReqLogTag,
                            Clog.getString(R.string.permissions_internet));
                } catch (IllegalArgumentException ie) {
                    Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_unknown));
                } catch (Exception e) {
                    e.printStackTrace();
                    Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.unknown_exception));
                }
            }
        }
        return null;
    }

    private boolean httpShouldContinue(int responseCode) {

        switch (responseCode) {
            default:
                Clog.d(Clog.httpRespLogTag,
                        Clog.getString(R.string.http_bad_status, responseCode));
                return false;
            case HttpURLConnection.HTTP_OK:
                return true;
        }

    }


    private HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(Settings.HTTP_CONNECTION_TIMEOUT);
        connection.setReadTimeout(Settings.HTTP_SOCKET_TIMEOUT);
        connection.setDoOutput(false);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("GET");
        return connection;
    }

    private void setConnectionParams(HttpURLConnection connection) throws ProtocolException{
        connection.setRequestProperty("User-Agent", Settings.getSettings().ua);
        String cookieString = WebviewUtil.getCookie();
        if (!TextUtils.isEmpty(cookieString)) {
            connection.setRequestProperty("Cookie",cookieString);
        }
    }

    @Override
    protected void onPostExecute(ServerResponse result) {
        // check for invalid responses
        if (result == null) {
            Clog.v(Clog.httpRespLogTag, Clog.getString(R.string.no_response));
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
            requester.onReceiveServerResponse(result);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCancelled(ServerResponse serverResponse) {
        super.onCancelled(serverResponse);
        Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.cancel_request));
    }
}
