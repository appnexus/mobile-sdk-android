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

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;


class VastTracker extends HTTPGet{

    private String url;

    VastTracker(String url) {
        this.url = url;
    }

    @Override
    protected void onPostExecute(HTTPResponse response) {
        if (response != null && response.getSucceeded()) {
            Clog.i(Clog.vastLogTag, "VAST event has been tracked successfully!");
        }
    }

    @Override
    protected String getUrl() {
        return url;
    }

}
