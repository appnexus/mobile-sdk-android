/*
 *    Copyright 2015 APPNEXUS INC
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

package com.appnexus.opensdk;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.appnexus.opensdk.ut.UTConstants;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.JsonUtil;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.utils.ViewUtil;
import com.appnexus.opensdk.utils.WebviewUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ANNativeAdResponse extends BaseNativeAdResponse {
    private int memberId;
    private String title;
    private String description;
    private String imageUrl;
    private String iconUrl;
    private Bitmap image;
    private ImageSize imageSize = new ImageSize(-1, -1);
    private ImageSize iconSize = new ImageSize(-1, -1);
    private Bitmap icon;
    private Rating rating;
    private String clickUrl;
    private String clickFallBackUrl;
    private String callToAction;

    private HashMap<String, Object> nativeElements;
    private boolean expired = false;
    private ArrayList<String> imp_trackers;
    private ArrayList<String> click_trackers;
    private String sponsoredBy;
    private String additionalDescription;
    private Handler anNativeExpireHandler;
    private String creativeId = "";
    private String videoVastXML = "";
    private String privacyLink = "";
    private String rendererUrl = "";
    private JSONObject nativeRendererObject = null;
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "desc";
    private static final String KEY_MAIN_MEDIA = "main_img";
    private static final String KEY_URL = "url";
    private static final String KEY_IMAGE_WIDTH = "width";
    private static final String KEY_IMAGE_HEIGHT = "height";
    private static final String KEY_ICON = "icon";
    private static final String KEY_CTA = "ctatext";
    private static final String KEY_CLICK_TRACK = "click_trackers";
    private static final String KEY_IMP_TRACK = "impression_trackers";
    private static final String KEY_CLICK_FALLBACK_URL = "fallback_url";
    private static final String KEY_RATING = "rating";
    private static final String KEY_SPONSORED_BY = "sponsored";
    private static final String KEY_ADDITIONAL_DESCRIPTION = "desc2";
    private static final String KEY_LINK = "link";
    private static final String KEY_VIDEO = "video";
    private static final String KEY_VIDEO_CONTENT = "content";
    private static final String KEY_PRIVACY_LINK = "privacy_link";
    private static final String RENDERER_URL = "renderer_url";

    private Runnable expireRunnable = new Runnable() {
        @Override
        public void run() {
            if (listener != null) {
                listener.onAdExpired();
            }
            expired = true;
            registeredView = null;
            clickables = null;
            if (visibilityDetector != null) {
                visibilityDetector.destroy(viewWeakReference.get());
            }
            impressionTracker = null;
            listener = null;
            // free assets
            if (icon != null) {
                icon.recycle();
                icon = null;
            }
            if (image != null) {
                image.recycle();
                image = null;
            }
        }
    };

    private Runnable aboutToExpireRunnable = new Runnable() {
        @Override
        public void run() {
            if (listener != null) {
                listener.onAdAboutToExpire();
            }
            if (anNativeExpireHandler != null) {
                long interval = getExpiryInterval(UTConstants.RTB, memberId);
                anNativeExpireHandler.postDelayed(expireRunnable, interval);
                Clog.w(Clog.baseLogTag, "onAdExpired() will be called in " + interval + "ms.");
            }
        }
    };

    private View registeredView;
    private List<View> clickables;
    private NativeAdEventListener listener;
    private View.OnClickListener clickListener;
    private VisibilityDetector visibilityDetector;
    private ImpressionTracker impressionTracker;
    private ProgressDialog progressDialog;
    private ANClickThroughAction clickThroughAction = ANClickThroughAction.OPEN_SDK_BROWSER;
    private WeakReference<View> viewWeakReference;

    /**
     * Process the metadata of native response from ad server
     *
     * @param adObject JsonObject that contains info of native ad
     * @return ANNativeResponse if no issue happened during processing
     */
    public static ANNativeAdResponse create(JSONObject adObject) {
        if (adObject == null) {
            return null;
        }

        JSONObject rtbObject = JsonUtil.getJSONObject(adObject, UTConstants.RTB);
        JSONObject metaData = JsonUtil.getJSONObject(rtbObject, UTConstants.AD_TYPE_NATIVE);
        if (metaData == null) {
            return null;
        }

        JSONArray impTrackerJson = JsonUtil.getJSONArray(metaData, KEY_IMP_TRACK);
        ArrayList<String> imp_trackers = JsonUtil.getStringArrayList(impTrackerJson);
        if (imp_trackers == null) {
            return null;
        }
        ANNativeAdResponse response = new ANNativeAdResponse(adObject);
        response.imp_trackers = imp_trackers;
        response.rendererUrl = JsonUtil.getJSONString(adObject, RENDERER_URL);
        response.title = JsonUtil.getJSONString(metaData, KEY_TITLE);
        response.description = JsonUtil.getJSONString(metaData, KEY_DESCRIPTION);
        JSONObject media = JsonUtil.getJSONObject(metaData, KEY_MAIN_MEDIA);
        if (media != null) {
            response.imageUrl = JsonUtil.getJSONString(media, KEY_URL);
            response.imageSize = new ImageSize(
                    JsonUtil.getJSONInt(media, KEY_IMAGE_WIDTH),
                    JsonUtil.getJSONInt(media, KEY_IMAGE_HEIGHT)
            );
        }
        JSONObject icon = JsonUtil.getJSONObject(metaData, KEY_ICON);
        if (icon != null) {
            response.iconUrl = JsonUtil.getJSONString(icon, KEY_URL);
            response.iconSize = new ImageSize(JsonUtil.getJSONInt(icon, KEY_IMAGE_WIDTH),
                    JsonUtil.getJSONInt(icon, KEY_IMAGE_HEIGHT));
        }
        response.callToAction = JsonUtil.getJSONString(metaData, KEY_CTA);
        JSONObject link = JsonUtil.getJSONObject(metaData, KEY_LINK);
        response.clickUrl = JsonUtil.getJSONString(link, KEY_URL);
        response.clickFallBackUrl = JsonUtil.getJSONString(link, KEY_CLICK_FALLBACK_URL);

        response.sponsoredBy = JsonUtil.getJSONString(metaData, KEY_SPONSORED_BY);
        response.additionalDescription = JsonUtil.getJSONString(metaData, KEY_ADDITIONAL_DESCRIPTION);

        response.rating = new Rating(
                JsonUtil.getJSONDouble(metaData, KEY_RATING),
                -1);
        JSONArray clickTrackerJson = JsonUtil.getJSONArray(link, KEY_CLICK_TRACK);
        response.click_trackers = JsonUtil.getStringArrayList(clickTrackerJson);
        JSONObject video = JsonUtil.getJSONObject(metaData, KEY_VIDEO);
        response.videoVastXML = JsonUtil.getJSONString(video, KEY_VIDEO_CONTENT);
        response.privacyLink = JsonUtil.getJSONString(metaData, KEY_PRIVACY_LINK);

        JSONObject nativeRendererObject = metaData;
        nativeRendererObject.remove("impression_trackers");
        nativeRendererObject.remove("javascript_trackers");
        if (response.nativeElements == null) {
            response.nativeElements = new HashMap<>();
        }
        if (!StringUtil.isEmpty(response.rendererUrl)) {
            response.nativeRendererObject = nativeRendererObject;
        }
        JSONObject nativeObject = null;
        try {
            nativeObject = new JSONObject(nativeRendererObject.toString());
            if (nativeObject!= null && nativeObject.has(KEY_LINK)) {
                // Convert nativeLinkObject as JSONObject
                JSONObject nativeLinkObject = nativeObject.getJSONObject(KEY_LINK);
                if (nativeLinkObject != null && nativeLinkObject.has(KEY_CLICK_TRACK)) {
                    // Remove click Trackers from nativeAd's Link
                    nativeLinkObject.remove(KEY_CLICK_TRACK);
                }
                // Remove link from nativeObject
                nativeObject.remove(KEY_LINK);
                // Re-add nativeLinkObject into nativeObject without tracker
                nativeObject.put(KEY_LINK,nativeLinkObject);
            }
            response.nativeElements.put(NATIVE_ELEMENT_OBJECT, nativeObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create an OMID Related objects.
        response.setANVerificationScriptResources(adObject);

        return response;
    }

    private ANNativeAdResponse() {
    }

    private ANNativeAdResponse(JSONObject adObject) {
        memberId = JsonUtil.getJSONInt(adObject, "buyer_member_id");
        anNativeExpireHandler = new Handler(Looper.getMainLooper());
        anNativeExpireHandler.postDelayed(aboutToExpireRunnable, getAboutToExpireTime(UTConstants.RTB, memberId));
    }

    @Override
    public Network getNetworkIdentifier() {
        return Network.APPNEXUS;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public Bitmap getImage() {
        return image;
    }

    @Override
    public void setImage(Bitmap bitmap) {
        this.image = bitmap;
    }

    @Override
    public ImageSize getImageSize() {
        return this.imageSize;
    }

    @Override
    public String getAdditionalDescription() {
        return this.additionalDescription;
    }

    @Override
    public String getIconUrl() {
        return iconUrl;
    }

    @Override
    public Bitmap getIcon() {
        return icon;
    }

    @Override
    public void setIcon(Bitmap bitmap) {
        this.icon = bitmap;
    }

    @Override
    public String getCallToAction() {
        return callToAction;
    }

    @Override
    public HashMap<String, Object> getNativeElements() {
        return nativeElements;
    }

    @Override
    public String getSponsoredBy() {
        return sponsoredBy;
    }

    @Override
    public Rating getAdStarRating() {
        return rating;
    }

    @Override
    public boolean hasExpired() {
        return expired;
    }

    @Override
    protected boolean registerView(final View view, final NativeAdEventListener listener) {
        if (!expired && view != null) {
            this.listener = listener;
            viewWeakReference = new WeakReference<>(view);
            visibilityDetector = VisibilityDetector.getInstance();
            if (visibilityDetector == null) {
                return false;
            }

            impressionTracker = ImpressionTracker.create(viewWeakReference, imp_trackers, visibilityDetector, view.getContext(), anOmidAdSession, getImpressionType(), new ImpressionTrackerListener() {
                @Override
                public void onImpressionTrackerFired() {
                    if (listener != null) {
                        listener.onAdImpression();
                    }
                    if (anNativeExpireHandler != null) {
                        anNativeExpireHandler.removeCallbacks(expireRunnable);
                        anNativeExpireHandler.removeCallbacks(aboutToExpireRunnable);
                    }
                }
            });
            this.registeredView = view;
            setClickListener();
            view.setOnClickListener(clickListener);
            return true;
        }
        return false;
    }

    @Override
    protected boolean registerViewList(final View view, final List<View> clickables, NativeAdEventListener listener) {
        if (registerView(view, listener)) {
            view.setOnClickListener(null); // unset the click listener in registerView()
            for (View clickable : clickables) {
                clickable.setOnClickListener(clickListener);
            }
            this.clickables = clickables;
            return true;
        }
        return false;
    }

    @Override
    protected boolean registerNativeAdEventListener(NativeAdEventListener listener) {
        this.listener = listener;
        return true;
    }

    @Override
    protected void unregisterViews() {
        if (registeredView != null) {
            registeredView.setOnClickListener(null);
        }
        if (clickables != null && !clickables.isEmpty()) {
            for (View clickable : clickables) {
                clickable.setOnClickListener(null);
            }
        }
        destroy();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (anNativeExpireHandler != null) {
            anNativeExpireHandler.removeCallbacks(expireRunnable);
            anNativeExpireHandler.removeCallbacks(aboutToExpireRunnable);
            anNativeExpireHandler.post(expireRunnable);
        }
    }

    private boolean doesLoadingInBackground = true;

    public boolean getLoadsInBackground() {
        return doesLoadingInBackground;
    }

    void setLoadsInBackground(boolean doesLoadingInBackground) {
        this.doesLoadingInBackground = doesLoadingInBackground;
    }

    public ANClickThroughAction getClickThroughAction() {
        return clickThroughAction;
    }

    public void setClickThroughAction(ANClickThroughAction clickThroughAction) {
        this.clickThroughAction = clickThroughAction;
    }

    void setClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fire click tracker first
                if (click_trackers != null) {
                    for (String url : click_trackers) {
                        new ClickTracker(url).execute();
                    }
                }
                if (getClickThroughAction() == ANClickThroughAction.RETURN_URL) {
                    if (listener != null) {
                        listener.onAdWasClicked(clickUrl, clickFallBackUrl);
                    }
                } else {
                    if (listener != null) {
                        listener.onAdWasClicked();
                    }
                    if (!handleClick(clickUrl, v.getContext())) {
                        if (!handleClick(clickFallBackUrl, v.getContext())) {
                            Clog.d(Clog.nativeLogTag, "Unable to handle click.");
                        }
                    }

                }
            }
        };
    }

    @Override
    public String getPrivacyLink() {
        return privacyLink;
    }

    @Override
    public ImageSize getIconSize() {
        return iconSize;
    }

    @Override
    public String getVastXml() {
        return videoVastXML;
    }

    protected String getRendererUrl() {
        return rendererUrl;
    }

    protected JSONObject getNativeRendererObject() {
        return nativeRendererObject;
    }

    private class RedirectWebView extends WebView {

        @SuppressLint("SetJavaScriptEnabled")
        public RedirectWebView(final Context context) {
            super(new MutableContextWrapper(context));

            WebviewUtil.setWebViewSettings(this);
            this.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    Clog.v(Clog.browserLogTag, "Opening URL: " + url);
                    ViewUtil.removeChildFromParent(RedirectWebView.this);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    startBrowserActivity(context);
                }
            });
        }
    }

    private void startBrowserActivity(Context context) {
        Class<?> activity_clz = AdActivity.getActivityClass();
        try {
            Intent intent = new Intent(context, activity_clz);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(AdActivity.INTENT_KEY_ACTIVITY_TYPE, AdActivity.ACTIVITY_TYPE_BROWSER);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.adactivity_missing, activity_clz.getName()));
            BrowserAdActivity.BROWSER_QUEUE.remove();
        }
    }


    boolean handleClick(String clickUrl, Context context) {
        if (clickUrl == null || clickUrl.isEmpty()) {
            return false;
        }
        // if install, open store
        if (clickUrl.contains("://play.google.com") || clickUrl.contains("market://")) {
            Clog.d(Clog.nativeLogTag,
                    Clog.getString(R.string.opening_app_store));
            return openNativeIntent(clickUrl, context);
        }
        // open browser
        if (getClickThroughAction() == ANClickThroughAction.OPEN_DEVICE_BROWSER) {
            // if set to use native browser, open intent
            if (openNativeIntent(clickUrl, context)) {
                if (listener != null) {
                    listener.onAdWillLeaveApplication();
                }
                return true;
            }
            return false;
        } else {
            try {
                if (getLoadsInBackground()) {
                    final WebView out = new RedirectWebView(new MutableContextWrapper(context));
                    WebviewUtil.setWebViewSettings(out);
                    out.loadUrl(clickUrl);
                    BrowserAdActivity.BROWSER_QUEUE.add(out);
                    // Otherwise, create an invisible 1x1 webview to load the landing
                    // page and detect if we're redirecting to a market url
                    //Show a dialog box
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setCancelable(true);
                    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            out.stopLoading();
                        }
                    });
                    progressDialog.setMessage(context.getResources().getString(R.string.loading));
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();
                } else {
                    WebView out = new WebView(new MutableContextWrapper(context));
                    WebviewUtil.setWebViewSettings(out);
                    out.loadUrl(clickUrl);
                    BrowserAdActivity.BROWSER_QUEUE.add(out);
                    startBrowserActivity(context);
                }


                return true;
            } catch (Exception e) {
                // Catches PackageManager$NameNotFoundException for webview
                Clog.e(Clog.baseLogTag, "Exception initializing the redirect webview: " + e.getMessage());
                return false;
            }
        }
    }

    private boolean openNativeIntent(String url, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Clog.w(Clog.baseLogTag,
                    Clog.getString(R.string.opening_url_failed, url));
            return false;
        }
    }
}
