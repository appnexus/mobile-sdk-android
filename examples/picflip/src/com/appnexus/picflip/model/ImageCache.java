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
package com.appnexus.picflip.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import static com.appnexus.picflip.util.LogUtils.makeLogTag;

public class ImageCache implements Parcelable {

    private static final String TAG = makeLogTag(ImageCache.class);

    public static int DEF_IMAGE_CACHE_SIZE = 16;

    private Bitmap mImageList[];
    private String mImageIds[];
    private String mGroupName;

    private static int curPos = 0;

    public ImageCache() {
        mImageList = new Bitmap[DEF_IMAGE_CACHE_SIZE];
        mImageIds = new String[DEF_IMAGE_CACHE_SIZE];
        mGroupName = "";
    }

    private ImageCache(Parcel in) {

        readFromParcel(in);
    }

    public boolean isEmpty() {
        return (mImageList[0] == null);
    }

    public synchronized boolean addImage(Bitmap image, String id) {
        boolean retval = false;

        if (!haveThisId(id)) {
            mImageList[curPos] = image;
            mImageIds[curPos] = id;

            if (++curPos >= DEF_IMAGE_CACHE_SIZE)
                curPos = 0;

            retval = true;
        }
        return retval;
    }

    public synchronized Bitmap getImage(int position) {
        Bitmap bm = null;

        if (mImageList.length > position) {
            bm = mImageList[position];
        }
        return bm;
    }

    public synchronized String getId(int position) {
        String idStr = null;

        if (mImageList.length > position) {
            idStr = mImageIds[position];
        }
        return idStr;
    }

    private boolean haveThisId(String curIdInt) {
        boolean retval = false;

        for (int i = 0; i < DEF_IMAGE_CACHE_SIZE && mImageIds[i] != null; i++) {
            if (curIdInt.equals(mImageIds[i])) {
                retval = true;
                break;
            }
        }
        return retval;
    }

    public void setGroupName(String group) {
        mGroupName = group;
    }


    public String getGroupName() {
        return mGroupName;
    }

    public void clear() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(mImageIds);
        parcel.writeArray(mImageList);
        parcel.writeString(mGroupName);
    }

    private void readFromParcel(Parcel in) {

        in.readStringArray(mImageIds);
        mImageList = (Bitmap[]) in.readArray(ImageCache.class.getClassLoader());
        mGroupName = in.readString();

    }

    public static final Creator CREATOR =
            new Creator() {
                public ImageCache createFromParcel(Parcel in) {
                    return new ImageCache(in);
                }

                public ImageCache[] newArray(int size) {
                    return new ImageCache[size];
                }
            };
}
