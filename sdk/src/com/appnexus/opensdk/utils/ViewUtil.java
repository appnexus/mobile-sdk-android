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

package com.appnexus.opensdk.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.appnexus.opensdk.CircularProgressBar;
import com.appnexus.opensdk.R;

public class ViewUtil {

    public static final int MUTE_BUTTON_SIZE = 32;
    public static final int MUTE_BUTTON_MARGIN = 10;
    public static final int CCD_MARGIN = 10;
    public static final int CCD_DIMENSIONS = 30;

    public static ImageButton createCloseButton(Context context, boolean custom_close) {
        final ImageButton close = new ImageButton(context);
        if (!custom_close){
            close.setImageDrawable(context.getResources().getDrawable(
                    android.R.drawable.ic_menu_close_clear_cancel));
        }
        FrameLayout.LayoutParams blp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
                | Gravity.TOP);
        close.setLayoutParams(blp);
        close.setBackgroundColor(Color.TRANSPARENT);
        return close;
    }

    public static ImageView createMuteButtonInRelativeLayout(Context context, boolean isMuted) {
        final ImageView muteButton = new ImageView(context);
        muteButton.setScaleType(ImageView.ScaleType.FIT_XY);
        if(isMuted) {
            muteButton.setImageResource(R.drawable.mute);
        }else{
            muteButton.setImageResource(R.drawable.unmute);
        }
        int sizeInDP = getSizeInDP(context, MUTE_BUTTON_SIZE);
        int marginInDp = getSizeInDP(context, MUTE_BUTTON_MARGIN);

        RelativeLayout.LayoutParams blp = new RelativeLayout.LayoutParams(
                sizeInDP, sizeInDP);

        blp.setMargins(0, 0, marginInDp, marginInDp);
        blp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        blp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        muteButton.setLayoutParams(blp);

        return muteButton;
    }


    public static void removeChildFromParent(View view) {
        if ((view != null) && (view.getParent() != null)) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    public static Context getTopContext(View view) {
        if (view == null) {
            return null;
        }
        ViewParent parent = view.getParent();

        if ((parent == null) || !(parent instanceof View)) {
            return view.getContext();
        }

        //noinspection ConstantConditions
        while ((parent.getParent() != null)
                && (parent.getParent() instanceof View)) {
            parent = parent.getParent();
        }

        return ((View) parent).getContext();
    }

    // returns screen size as { width, height } in pixels
    @SuppressWarnings("deprecation")
    public static int[] getScreenSizeAsPixels(Activity activity) {
        int screenWidth;
        int screenHeight;
        Display d = activity.getWindowManager().getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= 13) {
            Point p = new Point();
            d.getSize(p);
            screenWidth = p.x;
            screenHeight = p.y;
        } else {
            screenWidth = d.getWidth();
            screenHeight = d.getHeight();
        }

        return new int[] { screenWidth, screenHeight };
    }

    // returns screen size as { width, height } in DP
    public static int[] getScreenSizeAsDP(Activity activity) {
        int[] screenSize = getScreenSizeAsPixels(activity);
        convertFromPixelsToDP(activity, screenSize);
        return screenSize;
    }

    public static void convertFromPixelsToDP(Activity activity, int[] pixels) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = (int) ((pixels[i] / scale) + 0.5f);
        }
    }

    public static void convertFromDPToPixels(Activity activity, int[] pixels) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = (int) ((pixels[i] * scale) + 0.5f);
        }
    }


    public static CircularProgressBar addCountdownWidget(Activity activity, RelativeLayout layout) {
        CircularProgressBar countdownWidget = (CircularProgressBar) activity.getLayoutInflater().inflate(R.layout.countdown_widget, null);
        int size = ViewUtil.getSizeInDP(activity, CCD_DIMENSIONS);
        int margin = ViewUtil.getSizeInDP(activity, CCD_MARGIN);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
        params.setMargins(0, margin, margin, 0);
        countdownWidget.setVisibility(View.GONE);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        countdownWidget.setLayoutParams(params);
        layout.addView(countdownWidget);
        return countdownWidget;
    }

    public static CircularProgressBar addCountdownWidget(Activity activity, FrameLayout layout) {
        CircularProgressBar countdownWidget = (CircularProgressBar) activity.getLayoutInflater().inflate(R.layout.countdown_widget, null);
        int size = ViewUtil.getSizeInDP(activity, CCD_DIMENSIONS);
        int margin = ViewUtil.getSizeInDP(activity, CCD_MARGIN);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                size, size, Gravity.END | Gravity.TOP);
        params.setMargins(0, margin, margin, 0);
        countdownWidget.setVisibility(View.GONE);
        countdownWidget.setLayoutParams(params);
        layout.addView(countdownWidget);
        return countdownWidget;
    }

 /**
     * Returns the value according to device's density pixels.
     *
     * @param context
     * @param pixelSize
     * @return
     */
    public static int getSizeInDP(Context context, double pixelSize) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int sizeInDP = (int) (pixelSize * scale);
        return sizeInDP;
    }
}
