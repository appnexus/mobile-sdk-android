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

import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public abstract class HTTPGet<Params extends Void, Progress extends Void, Result extends HTTPResponse> extends AsyncTask<Params, Progress, Result> {


    public HTTPGet() {
        super();
    }

    @Override
    protected HTTPResponse doInBackground(Void... params) {
        HTTPResponse out = new HTTPResponse();

        //TODO: shouldn't this class have timeouts? @Jacob
//        HttpParams httpParams = new BasicHttpParams();
//        HttpConnectionParams.setConnectionTimeout(httpParams, Settings.getSettings().HTTP_CONNECTION_TIMEOUT);
//        HttpConnectionParams.setSoTimeout(httpParams, Settings.getSettings().HTTP_SOCKET_TIMEOUT);
//        HttpClient httpc = new DefaultHttpClient(httpParams);

        HttpClient httpc = new DefaultHttpClient();
        try {
            URI uri = new URI(getUrl());
            HttpGet request = new HttpGet();
            request.setURI(uri);
            HttpResponse r = httpc.execute(request);

            out.setHeaders(r.getAllHeaders());
            out.setResponseBody(EntityUtils.toString(r.getEntity()));
            out.setSucceeded(true);
        } catch (URISyntaxException e) {
            out.setSucceeded(false);
        } catch (ClientProtocolException e) {
            out.setSucceeded(false);
        } catch (IOException e) {
            out.setSucceeded(false);
        } finally {
            httpc.getConnectionManager().shutdown();
        }

        return out;
    }

    @Override
    abstract protected void onPostExecute(HTTPResponse response);

    @Override
    protected void onCancelled(HTTPResponse response) {
        super.onCancelled(null);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    protected abstract String getUrl();

}
