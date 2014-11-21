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

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class ImageService {
    HashMap<ImageReceiver, String> imageUrls = new HashMap<ImageReceiver, String>();
    ImageServiceListener imageServiceListener;
    static final int TIMEOUT = 10000;

    public void registerImageReceiver(ImageReceiver imageReceiver, String url) {
        if (!StringUtil.isEmpty(url) && imageReceiver != null) {
            imageUrls.put(imageReceiver, url);
        }
    }

    public void registerNotification(ImageServiceListener imageServiceListener) {
        this.imageServiceListener = imageServiceListener;
    }

    public void finishDownload(ImageReceiver imageReceiver) {
        if (imageUrls != null) {
            if (imageUrls.containsKey(imageReceiver)) {
                imageUrls.remove(imageReceiver);
                if (imageUrls.size() == 0) {
                    imageServiceListener.onAllImageDownloadsFinish();
                    Clog.d(Clog.baseLogTag, "Images downloading finished.");
                    finish();
                }
            }
        }
    }

    private void finish() {
        imageUrls = null;
        imageServiceListener = null;
    }


    public void execute() {
        if (imageServiceListener == null) {
            finish();
            return;
        }
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for(Map.Entry pairs : imageUrls.entrySet()) {
                ImageDownloader downloader = new ImageDownloader((ImageReceiver) pairs.getKey(), (String) pairs.getValue(), this);
                Clog.d(Clog.baseLogTag, "Downloading image from url: " + pairs.getValue());
                downloader.execute();
            }
        } else {
            imageServiceListener.onAllImageDownloadsFinish();
            finish();
        }
    }

    class ImageDownloader extends AsyncTask<Void, Void, Bitmap> {
        WeakReference<ImageService> caller;
        WeakReference<ImageReceiver> imageReceiver;
        String url;

        ImageDownloader(ImageReceiver imageReceiver, String url, ImageService caller) {
            this.caller = new WeakReference<ImageService>(caller);
            this.imageReceiver = new WeakReference<ImageReceiver>(imageReceiver);
            this.url = url;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            imageReceiver.clear();
            caller.clear();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            if (isCancelled()) {
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

        @Override
        protected void onPostExecute(Bitmap image) {
            ImageReceiver receiver = imageReceiver.get();
            ImageService service = caller.get();
            if (receiver != null) {
                if (image == null) {
                    receiver.onFail();
                } else {
                    receiver.onReceiveImage(image);
                }
            }
            if (service != null) {
                service.finishDownload(receiver);
            }
        }
    }

    public interface ImageReceiver {
        public void onReceiveImage(Bitmap image);
        public void onFail();
    }

    public interface ImageServiceListener {
        public void onAllImageDownloadsFinish();
    }

}
