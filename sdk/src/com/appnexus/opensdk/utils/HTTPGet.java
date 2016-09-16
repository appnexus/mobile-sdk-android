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

package com.appnexus.opensdk.utils;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;


import com.appnexus.opensdk.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public abstract class HTTPGet extends AsyncTask<Void, Void, HTTPResponse> {


    public HTTPGet() {
        super();
    }

    @Override
    protected HTTPResponse doInBackground(Void... params) {
        HTTPResponse out = new HTTPResponse();
        HttpURLConnection connection = null;
        try {
            URL reqUrl = new URL(getUrl());
            if (reqUrl.getHost() == null) {
                Clog.w(Clog.httpReqLogTag, "An HTTP request with an invalid URL was attempted.", new IllegalStateException("An HTTP request with an invalid URL was attempted."));
                out.setSucceeded(false);
                return out;
            }
            //  Create and connect to HTTP service
            connection = createConnection(reqUrl);
            setConnectionParams(connection);
            connection.connect();


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
            String responseString = builder.toString();


            out.setHeaders(connection.getHeaderFields());
            out.setResponseBody(responseString);
            boolean isStatusOK = (connection.getResponseCode()
                    == HttpURLConnection.HTTP_OK);
            out.setSucceeded(isStatusOK);

        }catch (MalformedURLException e) {
            out.setSucceeded(false);
            out.setErrorCode(HttpErrorCode.URI_SYNTAX_ERROR);
            Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_url_malformed));
        } catch (IOException e) {
            out.setSucceeded(false);
            out.setErrorCode(HttpErrorCode.TRANSPORT_ERROR);
            Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_io));
        } finally {
            if(connection!= null)
                connection.disconnect();
        }

        return out;
    }

    @Override
    abstract protected void onPostExecute(HTTPResponse response);

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCancelled(HTTPResponse response) {
        super.onCancelled(null);
    }


    protected abstract String getUrl();

    private HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(false);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("GET");
        return connection;
    }

    private void setConnectionParams(HttpURLConnection connection) throws ProtocolException {
        connection.setRequestProperty("User-Agent", Settings.getSettings().ua);
        String cookieString = WebviewUtil.getCookie();
        if (!TextUtils.isEmpty(cookieString)) {
            connection.setRequestProperty("Cookie",cookieString);
        }
    }

}
