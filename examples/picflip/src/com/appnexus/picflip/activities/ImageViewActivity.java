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

package com.appnexus.picflip.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.appnexus.picflip.R;

import static com.appnexus.picflip.util.LogUtils.makeLogTag;

/**
 * Created by eileen on 6/2/13.
 */
public class ImageViewActivity extends Activity {

    private static final String TAG = makeLogTag(ImageViewActivity.class);

    ImageView imgView = null;
    ImageButton mCloseButton;
    int mOverlayTextSize;

    View.OnClickListener onCloseButtonClicked = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bitmap bitmap = intent.getParcelableExtra("Bitmap");

        setContentView(R.layout.full_screen_image);

        setDisplayDensityVariables();

        imgView = (ImageView) findViewById(R.id.fullScreenImageView);
        mCloseButton = (ImageButton) findViewById(R.id.closeFullScreenButton);
        mCloseButton.setOnClickListener(onCloseButtonClicked);
        imgView.setImageBitmap(bitmap);
    }

    private void setDisplayDensityVariables() {


        DisplayMetrics mDispMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDispMetrics);

        int dispWidth = mDispMetrics.widthPixels;
        int dispHeight = mDispMetrics.heightPixels;
        int density = mDispMetrics.densityDpi;

        if (dispWidth > dispHeight) {
            if (dispWidth > 1000) {
                mOverlayTextSize = 30;
            } else if (dispWidth > 700) {
                mOverlayTextSize = 25;
            } else if (dispWidth > 400) {
                mOverlayTextSize = 20;
            } else {
                mOverlayTextSize = 15;
            }

        } else {
            if (dispHeight > 1000) {
                mOverlayTextSize = 30;
            } else if (dispHeight > 700) {
                mOverlayTextSize = 25;
            } else if (dispHeight > 400) {
                mOverlayTextSize = 20;
            } else {
                mOverlayTextSize = 15;
            }

        }
    }
}
