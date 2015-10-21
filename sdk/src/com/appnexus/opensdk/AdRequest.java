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

import com.appnexus.opensdk.utils.AdvertistingIDUtil;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.WebviewUtil;

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

                    Clog.e(Clog.vastLogTag, "URL -- "+query_string);

                    Clog.setLastRequest(query_string);

                    Clog.d(Clog.httpReqLogTag,
                            Clog.getString(R.string.fetch_url, query_string));

                    HttpParams p = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(p,
                            Settings.HTTP_CONNECTION_TIMEOUT);
                    HttpConnectionParams.setSoTimeout(p,
                            Settings.HTTP_SOCKET_TIMEOUT);
                    HttpConnectionParams.setSocketBufferSize(p, 8192);
                    DefaultHttpClient h = new DefaultHttpClient(p);

                    HttpGet req = new HttpGet(query_string);
                    req.setHeader("User-Agent", Settings.getSettings().ua);
                    HttpResponse r = h.execute(req);
                    if (!httpShouldContinue(r.getStatusLine())) {
                        return AdRequest.HTTP_ERROR;
                    }
                    String out = EntityUtils.toString(r.getEntity());


                    Clog.e(Clog.vastLogTag, "RESPONSE -- "+out);
                    WebviewUtil.cookieSync(h.getCookieStore().getCookies());
                    if (out.equals("")) {
                        // just log and return a valid AdResponse object so that it is
                        // marked as UNABLE_TO_FILL
                        Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.response_blank));
                    }
                    return new ServerResponse(out, r.getAllHeaders(), parameters.getMediaType());
                } catch (ClientProtocolException e) {
                    Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_unknown));
                } catch (ConnectTimeoutException e) {
                    Clog.e(Clog.httpReqLogTag, Clog.getString(R.string.http_timeout));
                } catch (HttpHostConnectException he) {
                    Clog.e(Clog.httpReqLogTag, Clog.getString(
                            R.string.http_unreachable, he.getHost().getHostName(), he
                                    .getHost().getPort()));
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
