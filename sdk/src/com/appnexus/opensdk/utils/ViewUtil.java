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
import android.content.MutableContextWrapper;
import android.graphics.Point;
import android.os.Build;
import android.view.*;
import android.widget.FrameLayout;
import com.appnexus.opensdk.CircularProgressBar;
import com.appnexus.opensdk.VideoOrientation;

public class ViewUtil {

    public static final int CCD_MARGIN = 10;
    public static final int CCD_DIMENSIONS = 30;

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
            if (view.getContext() instanceof MutableContextWrapper) {
                return ((MutableContextWrapper) view.getContext()).getBaseContext();
            }
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

        return new int[]{screenWidth, screenHeight};
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

    public static int getValueInPixel(Context context, double valueInDP) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((valueInDP * scale) + 0.5f);
    }

    public static int getValueInDP(Activity activity, int pixels) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        return (int) ((pixels / scale) + 0.5f);
    }

    /**
     * Adds the CircularProgressBar to the layout when passed
     * Returns instance of created CircularProgressBar
     *
     * @param context - Context of the View/Activity currently running
     */
    public static CircularProgressBar createCircularProgressBar(Context context) {
        CircularProgressBar circularProgressBar = new CircularProgressBar(context, null, android.R.attr.indeterminateOnly);
        circularProgressBar.setId(android.R.id.closeButton);

        int size = getValueInPixel(context, CCD_DIMENSIONS);
        int margin = getValueInPixel(context, CCD_MARGIN);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                size, size, Gravity.END | Gravity.TOP);
        params.setMargins(0, margin, margin, 0);
        circularProgressBar.setVisibility(View.GONE);
        circularProgressBar.setLayoutParams(params);
        return circularProgressBar;
    }

    /**
     * Displays the Close Button when called
     *
     * @param circularProgressBar the instance of CircularProgressBar that is to be displayed
     * @param custom_close boolean value that states if the custom close is enable or disabled
     */
    public static void showCloseButton(CircularProgressBar circularProgressBar, boolean custom_close) {
        if (circularProgressBar != null) {
            circularProgressBar.setVisibility(View.VISIBLE);
            if (!custom_close) {
                circularProgressBar.setProgress(0);
                circularProgressBar.setTitle("X");
            } else {
                circularProgressBar.setTransparent();
            }
        }
    }

    public static VideoOrientation getVideoOrientation(String fetchedAspectRatio) {
        try {
            double aspectRatio = Double.parseDouble(fetchedAspectRatio);
            return aspectRatio == 0 ? VideoOrientation.UNKNOWN : (aspectRatio == 1) ? VideoOrientation.SQUARE : (aspectRatio > 1) ? VideoOrientation.LANDSCAPE : VideoOrientation.PORTRAIT;
        } catch (Exception ex) {
            return VideoOrientation.UNKNOWN;
        }
    }
}
