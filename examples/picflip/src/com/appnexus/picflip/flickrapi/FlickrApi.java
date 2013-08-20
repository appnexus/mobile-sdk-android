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

package com.appnexus.picflip.flickrapi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static com.appnexus.picflip.util.LogUtils.LOGD;
import static com.appnexus.picflip.util.LogUtils.makeLogTag;

public class FlickrApi {

    private static final String TAG = makeLogTag(FlickrApi.class);

    static public JSONObject queryGroup(String group, String apiKey) {
        String pictureUrlStr;
        String json;
        JSONObject jObj = null;
        InputStream in = null;
        OutputStream out = null;
        HttpURLConnection conn = null;
        URL syncUrl;

        try {
            pictureUrlStr = String.format(
                    "http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=%s&group_id=%s&format=json&nojsoncallback=1",
                    apiKey, group);
            syncUrl = new URL(pictureUrlStr);
            conn = (HttpURLConnection) syncUrl.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(20000 /* milliseconds */);
            conn.setRequestMethod("GET");

			/* get the data */
            conn.connect();

            in = conn.getInputStream();
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in, "iso-8859-1"), 80);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                json = sb.toString();

                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(json);
                } catch (JSONException e) {
                    LOGD(TAG, "Error parsing data " + e.toString());
                }

            } catch (Exception e) {
                LOGD(TAG, "Error converting result " + e.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jObj;
    }


    static public Bitmap retrievePhoto(String photoId,
                                       String farmNumber,
                                       String secret,
                                       String serverNumber
    ) {
        //http://farm + farmNumber + "static.flickr.com/" + serverNumber + "/" + photoId + "_" + secret + "_m.jpg"
        URL retrieveUrl;
        Bitmap imageBm = null;

        String urlString = String.format("http://farm%s.static.flickr.com/%s/%s_%s_m.jpg",
                farmNumber, serverNumber, photoId, secret);

        try {
            retrieveUrl = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) retrieveUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(20000 /* milliseconds */);
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            imageBm = BitmapFactory.decodeStream(inputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageBm;
    }
}
