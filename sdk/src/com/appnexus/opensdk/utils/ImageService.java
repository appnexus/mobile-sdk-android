/*
 *    Copyright 2014 APPNEXUS INC
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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;

import com.appnexus.opensdk.SDKSettings;
import com.appnexus.opensdk.tasksmanager.TasksManager;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class ImageService {
    HashMap<String, String> imageUrlMap = new HashMap<>();
    ImageServiceListener imageServiceListener;

    static final int TIMEOUT = 10000;
    private ImageReceiver imageReceiver;

    public void registerImageReceiver(ImageReceiver imageReceiver, HashMap<String, String> imageUrlMap) {
        if (imageReceiver != null && imageUrlMap != null && !imageUrlMap.isEmpty()) {
            this.imageReceiver = imageReceiver;
            this.imageUrlMap = imageUrlMap;
        }
    }

    public void registerNotification(ImageServiceListener imageServiceListener) {
        this.imageServiceListener = imageServiceListener;
    }

    public void finishDownload(String key) {
        if (imageUrlMap != null) {
            if (imageUrlMap.containsKey(key)) {
                imageUrlMap.remove(key);
                if (imageUrlMap.size() == 0) {
                    imageServiceListener.onAllImageDownloadsFinish();
                    Clog.d(Clog.baseLogTag, "Images downloading finished.");
                    finish();
                }
            }
        }
    }

    private void finish() {
        imageUrlMap = null;
        imageServiceListener = null;
    }


    public void execute() {
        if (imageServiceListener == null) {
            finish();
            return;
        }
        if (imageUrlMap != null && !imageUrlMap.isEmpty()) {
            HashMap<String, String> imageUrlMapCopy = new HashMap<>(imageUrlMap);
            for (Map.Entry pairs : imageUrlMapCopy.entrySet()) {
                ImageDownloader downloader = new ImageDownloader(imageReceiver, (String) pairs.getKey(), (String) pairs.getValue(), this);
                Clog.d(Clog.baseLogTag, "Downloading " + pairs.getKey() + " from url: " + pairs.getValue());
                downloader.execute();
            }
        } else {
            imageServiceListener.onAllImageDownloadsFinish();
            finish();
        }
    }

    private class ImageDownloader {
        private final String key;
        WeakReference<ImageService> caller;
        WeakReference<ImageReceiver> imageReceiver;
        String url;
        private boolean isCancelled;
        ImageDownloaderAsync downloaderAsync;

        ImageDownloader(ImageReceiver imageReceiver, String key, String url, ImageService caller) {
            this.caller = new WeakReference<ImageService>(caller);
            this.imageReceiver = new WeakReference<ImageReceiver>(imageReceiver);
            this.url = url;
            this.key = key;
        }

        public void execute() {
            if (SDKSettings.isBackgroundThreadingEnabled()) {
                TasksManager.getInstance().executeOnBackgroundThread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = getBitmap();
                        consumeBitmap(bitmap);
                    }
                });
            } else {
                downloaderAsync = new ImageDownloaderAsync();
                Clog.d(Clog.baseLogTag, "Downloading " + key + " from url: " + url);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    downloaderAsync.executeOnExecutor(SDKSettings.getExternalExecutor());
                } else {
                    downloaderAsync.execute();
                }
            }
        }

        private void cancel() {
            isCancelled = true;
            imageReceiver.clear();
            caller.clear();
            if (downloaderAsync != null) {
                downloaderAsync.onCancelled();
            }
        }

        private class ImageDownloaderAsync extends AsyncTask<Void, Void, Bitmap> {

            @Override
            protected void onCancelled() {
                super.onCancelled();
                imageReceiver.clear();
                caller.clear();
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                return getBitmap();
            }

            @Override
            protected void onPostExecute(Bitmap image) {
                consumeBitmap(image);
            }
        }

        private Bitmap getBitmap() {
            if (isCancelled || StringUtil.isEmpty(url)) {
                return null;
            }
            try {
                URLConnection connection = new URL(url).openConnection();
                connection.setReadTimeout(TIMEOUT);
                InputStream is = (InputStream) connection.getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                is.close();
                return bitmap;

            } catch (Exception ignore) {
            }
            return null;
        }

        private void consumeBitmap(Bitmap image) {
            ImageReceiver receiver = imageReceiver.get();
            ImageService service = caller.get();
            if (receiver != null) {
                if (image == null) {
                    receiver.onFail(url);
                } else {
                    receiver.onReceiveImage(key, image);
                }
            }
            if (service != null) {
                service.finishDownload(key);
            }
        }
    }

    public interface ImageReceiver {
        void onReceiveImage(String key, Bitmap image);

        void onFail(String url);
    }

    public interface ImageServiceListener {
        void onAllImageDownloadsFinish();
    }

}
