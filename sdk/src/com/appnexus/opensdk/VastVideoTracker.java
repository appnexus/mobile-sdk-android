package com.appnexus.opensdk;

import android.util.Log;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;

public class VastVideoTracker extends HTTPGet {
    private String url;

    VastVideoTracker(String url) {
        this.url = url;
    }

    @Override
    protected void onPostExecute(HTTPResponse response) {
        Log.d(Clog.httpRespLogTag, "Tracking response: "+response.getSucceeded());
        if (response != null && response.getSucceeded()) {
            Log.d(Clog.httpRespLogTag, "Tracking successful!");
        }
    }

    @Override
    protected String getUrl() {
        return url;
    }
}
