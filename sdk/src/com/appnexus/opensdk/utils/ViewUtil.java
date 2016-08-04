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
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class ViewUtil {
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
            if(view.getContext() instanceof MutableContextWrapper){
                return ((MutableContextWrapper)view.getContext()).getBaseContext();
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

    public static int getValueInPixel(Context context, int valueInDP) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((valueInDP * scale) + 0.5f);
    }
}
