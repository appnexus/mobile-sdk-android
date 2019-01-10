package com.appnexus.opensdk.mediatedviews;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.StringUtil;
import com.smartadserver.android.library.model.SASAdPlacement;
import com.smartadserver.android.library.util.SASConfiguration;

import org.json.JSONObject;

/**
 * Base class for adapter classes containing shared methods
 */
public class SmartAdServerBaseAdapter {

    // constant strings
    private static final String SITE_ID = "site_id";
    private static final String PAGE_ID = "page_id";
    private static final String FORMAT_ID = "format_id";

    //replace the SMART_BASE_URL with your own.
    public static String SMART_BASE_URL = "https://mobile.smartadserver.com";

    // Handler on main Thread to execute code on this thread
    protected static final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Returns a {@link SASAdPlacement} object from the passed parameters
     */
    protected SASAdPlacement configureSDKAndGetAdPlacement(Activity activity, String uid, TargetingParameters tp) {
        int site_id;
        String page_id;
        int format_id;
        try {
            if (!StringUtil.isEmpty(uid)) {
                JSONObject idObject = new JSONObject(uid);
                site_id = Integer.parseInt(idObject.getString(SITE_ID));
                page_id = idObject.getString(PAGE_ID);
                format_id = Integer.parseInt(idObject.getString(FORMAT_ID));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        if (!SASConfiguration.getSharedInstance().isConfigured()) {
            try {
                SASConfiguration.getSharedInstance().configure(activity, site_id, SMART_BASE_URL);
            } catch (SASConfiguration.ConfigurationException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (tp != null) {
            if (tp.getLocation() != null) {
                SASConfiguration.getSharedInstance().setForcedLocation(tp.getLocation());
            }
        }



        return new SASAdPlacement(site_id, page_id, format_id, null);
    }
}