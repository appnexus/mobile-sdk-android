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

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public abstract class HTTPGet extends AsyncTask<Void, Void, HTTPResponse> {


    public HTTPGet() {
        super();
    }

	@Override
    protected HTTPResponse doInBackground(Void... params) {
        HTTPResponse out = new HTTPResponse();

        HttpClient httpc = new DefaultHttpClient();
        try {
            URI uri = new URI(getUrl());
            if(uri.getHost()==null){
                 Clog.w(Clog.httpReqLogTag, "An HTTP request with an invalid URL was attempted.", new IllegalStateException("An HTTP request with an invalid URL was attempted."));
                out.setSucceeded(false);
                return out;
            }
            HttpGet request = new HttpGet();
            request.setHeader("User-Agent", Settings.getSettings().ua);
            request.setURI(uri);
            request.addHeader("Cookie", WebviewUtil.getCookie());
            HttpResponse r = httpc.execute(request);

            out.setHeaders(r.getAllHeaders());
            if (r.getEntity() != null) {
                out.setResponseBody(EntityUtils.toString(r.getEntity()));
            }
            boolean isStatusOK = (r.getStatusLine() != null)
                    && (r.getStatusLine().getStatusCode()
                    == 200);
            out.setSucceeded(isStatusOK);
        } catch (URISyntaxException e) {
            out.setSucceeded(false);
            out.setErrorCode(HttpErrorCode.URI_SYNTAX_ERROR);
        } catch (ClientProtocolException e) {
            out.setSucceeded(false);
            out.setErrorCode(HttpErrorCode.HTTP_PROTOCOL_ERROR);
        } catch (ConnectionPoolTimeoutException e) {
            out.setSucceeded(false);
            out.setErrorCode(HttpErrorCode.CONNECTION_FAILURE);
        } catch (ConnectTimeoutException e) {
            out.setSucceeded(false);
            out.setErrorCode(HttpErrorCode.CONNECTION_FAILURE);
        } catch (IOException e) {
            out.setSucceeded(false);
            out.setErrorCode(HttpErrorCode.TRANSPORT_ERROR);
        } finally {
            httpc.getConnectionManager().shutdown();
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

}
