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
package com.appnexus.picflip.tasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.appnexus.picflip.model.ImageCache;
import com.appnexus.picflip.util.Constants;
import com.appnexus.picflip.flickrapi.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.appnexus.picflip.util.LogUtils.makeLogTag;
import static com.appnexus.picflip.util.LogUtils.LOGD;

/**
 * The Flicker Image downloader
 */
public class FlickrImageTask extends AsyncTask {

    private static final String TAG = makeLogTag(FlickrImageTask.class);

    private static final int NTHREDS = 16;

    private final String apiKey = "5660c951d8cb5625007f363dae30d313";

    private final String commonsGroup       = "972605@N21";
    private final String publicDomainGroup  = "77356438@N00";
    private final String worldPhotography   = "557255@N22";
    private final String blackWhiteGroup    = "16978849@N00";
    private final String nasaPublicDomain   = "1238524@N23";
    private final String smthsonianAirSpace = "932662@N24";
    private final String publicHdr          = "38072161@N00";
    private final String earthNature        = "1227735@N25";
    private final String govPublicDomain    = "16523859@N00";
    private final String mountRainier       = "1949651@N21";


    private final String[] groups = {commonsGroup, publicDomainGroup, worldPhotography, blackWhiteGroup,
            nasaPublicDomain, smthsonianAirSpace, publicHdr, earthNature, govPublicDomain, mountRainier};

    private final String[] groupNames = {"Commons", "Public Domain", "World Photography", "Black and White",
            "Nasa Public Domain", "Smithsonian AirSpace", "Public", "Earth Nature", "Government Public Domain", "Mount Rainer"};


    private String jsonData = "";
    private JSONArray jsonWrapper = new JSONArray();
    private Handler mHandler;
    private Activity ctx;
    private ImageCache imgCache; // = ImageCache.getInstance();
    JSONObject jObj = null;


    private class GetFlickrImage implements Runnable {
        ImageCache mImgCache = null;

        public GetFlickrImage(ImageCache curImgCache) {
            mImgCache = curImgCache;
        }

        @Override
        public void run() {
            int numPerPage;
            int photoIdxToGet;
            boolean imgAddRetVal = false;
            Bitmap curPitureBm;
            JSONObject curPicture;
            Random rnd = new Random();

            try {
                JSONObject photoMetaData = jObj.getJSONObject("photos");
                JSONArray photoArray = photoMetaData.getJSONArray("photo");

                numPerPage = (Integer) photoMetaData.get("perpage");

                String curId;
                int cnt = 0;

                do {
                    photoIdxToGet = rnd.nextInt(photoArray.length()); //photoArraynumPerPage);

                    if (photoArray.length() < 16) {
                        LOGD(TAG, String.format("GetFlickrImage Group Len: %d Cnt: %d", photoArray.length(), ++cnt));
                        break;
                    }
                    curId = "";
                    curPicture = photoArray.optJSONObject(photoIdxToGet);
                    if (curPicture == null) {
                        LOGD(TAG, "GetFlickrImage - NULL Pointer getting curPicture");
                        continue;
                    }

                    curId = curPicture.getString("id");
                    LOGD(TAG, String.format("GetFlickrImage HaveID: %s Cnt: %d", curId, ++cnt));

                    curPitureBm = FlickrApi.retrievePhoto(curId,
                            curPicture.getString("farm"),
                            curPicture.getString("secret"),
                            curPicture.getString("server"));


                    if (curPitureBm != null) {
                        LOGD(TAG, String.format("GetFlickrImage adding image ID: %s to cache", curId));
                        imgAddRetVal = mImgCache.addImage(curPitureBm, curId);
                    }
                }
                while (!imgAddRetVal);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String doInBackground(Object... context) {
        int grp;
        ctx = (Activity) context[0];
        mHandler = (Handler) context[1];
        imgCache = (ImageCache) context[2];

        LOGD(TAG, "doInBackground started");
        grp = queryFlickr(ctx, imgCache);

        LOGD(TAG, String.format("doInBackground Finished - group: %s ImgCache: %s", groupNames[grp], imgCache.toString()));
        Message msg = mHandler.obtainMessage(Constants.MSG_REFRESH_GRID_VIEW, groupNames[grp]);
        msg.sendToTarget();
        return groupNames[grp];
    }

    private int queryFlickr(Context ctx, ImageCache curImgCache) {
        Random rnd = new Random();
        int groupIdx = rnd.nextInt(groups.length);
        jObj = FlickrApi.queryGroup(groups[groupIdx], apiKey);

        curImgCache.setGroupName(groupNames[groupIdx]);

        if (jObj != null) {
            ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
            for (int i = 0; i < 16; i++) {
                Runnable worker = new GetFlickrImage(curImgCache);
                executor.execute(worker);
            }
            executor.shutdown();

            // Wait until all threads are finish
            while (!executor.isTerminated()) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return groupIdx;
    }
}
