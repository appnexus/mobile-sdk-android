package com.appnexus.opensdk.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Dips {
	
	/**
	 * Method to get pixel values in dip
	 * @param dips
	 * @param context
	 * @return
	 */
    public static float asFloatPixels(float dips, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, displayMetrics);
    }

    /**
     * Method to get dip values in pixel
     * @param dips
     * @param context
     * @return
     */
    public static int asIntPixels(float dips, Context context) {
        return (int) (asFloatPixels(dips, context) + 0.5f);
    }
}
