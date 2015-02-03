package com.appnexus.opensdk;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

class SharedNetworkManager {

    private static SharedNetworkManager manager;

    static SharedNetworkManager getInstance(Context context) {
        if (manager == null) {
            manager = new SharedNetworkManager(context);
        }
        return manager;
    }


    private ArrayList<UrlObject> urls = new ArrayList<UrlObject>();
    private Timer retryTimer;
    private static final int TOTAL_RETRY_TIMES = 3;
    private static final int TOTAL_RETRY_WAIT_INTERVAL_MILLES = 1 * 10 * 1000;
    private static final String permission = "android.permission.ACCESS_NETWORK_STATE";
    private boolean permitted;

    private SharedNetworkManager(Context context) {
        int permissionStatus = context.getPackageManager().checkPermission(
                permission,
                context.getPackageName()
        );
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            permitted = true;
        } else {
            permitted = false;
        }
    }

    boolean isConnected(Context context) {
        if (permitted) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        } else {
            return true;
        }
    }

    synchronized void addURL(String url, Context context) {
        urls.add(new UrlObject(url));
        startTimer(context);
    }

    private void startTimer(Context context) {
        if (retryTimer == null) {
            // check Network Connectivity after a certain period
            final WeakReference<Context> weakContext = new WeakReference<Context>(context);
            retryTimer = new Timer();
            retryTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Context context = weakContext.get();
                    if (context != null) {
                        while (!urls.isEmpty()) {
                            if (isConnected(context)) {
                                final UrlObject urlObject = urls.remove(0);
                                if (urlObject.retryTimes < TOTAL_RETRY_TIMES) {
                                    new HTTPGet() {
                                        @Override
                                        protected void onPostExecute(HTTPResponse response) {}

                                        @Override
                                        protected String getUrl() {
                                            return urlObject.url;
                                        }
                                    }.execute();
                                }
                            } else {
                                break;
                            }
                        }
                        if (!urls.isEmpty()) {
                            for (UrlObject urlObject : urls) {
                                urlObject.retryTimes += 1;
                                if (urlObject.retryTimes >= TOTAL_RETRY_TIMES) {
                                    urls.remove(urlObject);
                                }
                            }
                        } else {
                            stopTimer();
                        }
                    } else {
                        stopTimer();
                    }
                }
            }, TOTAL_RETRY_WAIT_INTERVAL_MILLES, TOTAL_RETRY_WAIT_INTERVAL_MILLES);

        }
    }

    private void stopTimer() {
        if (retryTimer != null) {
            retryTimer.cancel();
            retryTimer = null;
        }
    }

    class UrlObject {
        String url;
        int retryTimes;

        UrlObject(String url) {
            this.url = url;
            this.retryTimes = 0;
        }
    }

}
