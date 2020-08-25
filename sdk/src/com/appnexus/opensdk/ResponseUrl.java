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

package com.appnexus.opensdk;

import android.net.Uri;
import android.os.Build;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.StringUtil;

public final class ResponseUrl {


    private final String url; // required
    private final ResultCode resultCode; // required
    private final long latency; //optional
    private final long totalLatency; // optional


    private ResponseUrl(Builder builder) {
        this.url = builder.url;
        this.resultCode = builder.resultCode;
        this.latency = builder.latency;
        this.totalLatency = builder.totalLatency;
    }


    public static class Builder {

        private final String url;
        private final ResultCode resultCode;
        private long latency;
        private long totalLatency;


        public Builder(String url, ResultCode resultCode) {
            this.url = url;
            this.resultCode = resultCode;
        }

        public Builder latency(long latency) {
            this.latency = latency;
            return this;
        }

        public Builder totalLatency(long totalLatency) {
            this.totalLatency = totalLatency;
            return this;
        }

        public ResponseUrl build() {
            return new ResponseUrl(this);
        }
    }

    public void execute() {

        if ((url == null) || StringUtil.isEmpty(url)) {
            Clog.w(Clog.mediationLogTag, Clog.getString(R.string.fire_responseurl_null));
            return;
        }
        // create the responseurl request
        StringBuilder sb = new StringBuilder(url);
        sb.append("&reason=").append(resultCode.getCode());
        if (latency > 0) {
            sb.append("&latency=").append(Uri.encode(String.valueOf(latency)));
        }
        if (totalLatency > 0) {
            sb.append("&total_latency=").append(Uri.encode(String.valueOf(totalLatency)));
        }

        final String responseUrl = sb.toString();
        HTTPGet fire = new HTTPGet() {
            @Override
            protected HTTPResponse doInBackground(Void... params) {
                return super.doInBackground(params);
            }

            @Override
            protected void onPostExecute(HTTPResponse response) {
                if (response != null && response.getSucceeded()) {
                    Clog.i(Clog.mediationLogTag, "ResponseURL Fired Successfully");
                }
            }

            @Override
            protected String getUrl() {
                return responseUrl;
            }
        };
        fire.execute();
    }
}
