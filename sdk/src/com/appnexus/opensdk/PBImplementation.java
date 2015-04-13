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

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.view.View;
import com.appnexus.opensdk.utils.Clog;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class PBImplementation {
    private static final String HOST_WEB = "web";
    private static final String HOST_APP = "app";
    private static final String HOST_CAPTURE = "capture";

    private static final String ACTION_BROADCAST = "com.appnexus.opensdk.BROADCAST";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_AUCTIONINFO = "auction_info";
    private static final String KEY_AUCTIONID= "auction_id";

    private static final Uri URI_LAUNCH_APP = Uri.parse("appnexuspb://app?");
    private static final String URL_BROADCAST_PREFIX = "appnexuspb://app?auction_info=";

    private static LinkedHashMap<String, String> buffer
            = new LinkedHashMap<String, String>();

    private static final int PB_BUFFER_LIMIT = 10;
    private static final long PB_CAPTURE_DELAY_MS = 1000;

    static void handleUrl(AdWebView adWebView, String url) {
        if ((adWebView == null) || (adWebView.getContext() == null)) {
            return;
        }
        Context context = adWebView.getContext();
        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        if (HOST_WEB.equals(host)) {
            if (adWebView.getUserInteraction()) {
                launchApp(context);
            }
        } else if (HOST_APP.equals(host)) {
            String auctionInfo = uri.getQueryParameter(KEY_AUCTIONINFO);
            saveAuctionInfo(auctionInfo);

        } else if (HOST_CAPTURE.equals(host)) {
            String auctionID = uri.getQueryParameter(KEY_AUCTIONID);

            String auctionInfo = buffer.get(auctionID);
            if (auctionInfo == null) {
                return;
            }
            captureImage(context, adWebView, auctionInfo);
        }
    }

    // assume context is non-null
    private static void launchApp(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, URI_LAUNCH_APP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.opening_url_failed,
                    URI_LAUNCH_APP.toString()));
        }
    }

    private static void saveAuctionInfo(String auctionInfo) {
        try {
            JSONObject auctionJson = new JSONObject(auctionInfo);
            String auctionID = auctionJson.getString(KEY_AUCTIONID);
            trimBuffer();
            buffer.put(auctionID, auctionInfo);

        } catch (JSONException ignored) {
        }
    }

    // assume context is non-null
    private static void sendBroadcast(Context context, String auctionInfo, byte[] imageBytes) {
        String dataUrl = URL_BROADCAST_PREFIX + Uri.encode(auctionInfo);

        Intent intent = new Intent(ACTION_BROADCAST, Uri.parse(dataUrl));
        intent.putExtra(KEY_IMAGE, imageBytes);
        context.sendBroadcast(intent);
    }

    private static void trimBuffer() {
        if (buffer.size() > PB_BUFFER_LIMIT) {
            Set<String> keys = buffer.keySet();
            String key = keys.iterator().next();
            buffer.remove(key);
        }
    }

    private static void captureImage(final Context context, final View view, final String auctionInfo) {
        //Handler was blocking UI thread.
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                byte[] imageBytes = BitmapToByte(captureView(view));
                Clog.d(Clog.baseLogTag, "PITBULL image size: "+imageBytes.length+" bytes");
                sendBroadcast(context, auctionInfo, imageBytes);
            }
        }, PB_CAPTURE_DELAY_MS, TimeUnit.MILLISECONDS);
    }

    private static Bitmap captureView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        view.draw(c);
        return bitmap;
    }

    private static byte[] BitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)) {
            return stream.toByteArray();
        }
        return null;
    }
}
