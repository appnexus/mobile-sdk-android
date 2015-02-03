package com.appnexus.opensdk;

import android.content.Context;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;

class ClickTracker{

    private String url;
    private Context context;

    ClickTracker(String url, Context context) {
        this.url = url;
        this.context = context;
    }

    void fire() {
        SharedNetworkManager nm = SharedNetworkManager.getInstance(context);
        if( nm.isConnected(context)){
            new HTTPGet() {
                @Override
                protected void onPostExecute(HTTPResponse response) {
                    Clog.d(Clog.nativeLogTag, "Click tracked");
                }

                @Override
                protected String getUrl() {
                    return url;
                }
            }.execute();
        } else {
            nm.addURL(url, context);
        }
    }
}
