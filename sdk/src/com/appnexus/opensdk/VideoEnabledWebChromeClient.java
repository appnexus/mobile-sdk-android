package com.appnexus.opensdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.ViewUtil;

import java.util.LinkedList;

class VideoEnabledWebChromeClient extends BaseWebChromeClient {
    CustomViewCallback customViewCallback;
    FrameLayout frame;
    Activity context;
    AdView adView;
    private AdWebView adWebView;
    LinkedList<Pair<View, Integer>> views;

    public VideoEnabledWebChromeClient(Activity activity) {
        this.context = activity;
    }

    public VideoEnabledWebChromeClient(AdWebView adWebView) {
        this.context = (Activity) adWebView.getContext();
        this.adWebView = adWebView;
        this.adView = this.adWebView.adView;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        onShowCustomView(view, callback);
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        super.onShowCustomView(view, callback);

        if (context == null) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.fullscreen_video_show_error));
            return;
        }
        ViewGroup root = (ViewGroup) context.findViewById(android.R.id.content);
        if (root == null) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.fullscreen_video_show_error));
            return;
        }

        customViewCallback = callback;
        if (view instanceof FrameLayout) {
            frame = (FrameLayout) view;

            views = new LinkedList<Pair<View, Integer>>();
            // hide other children so that the only view shown is the custom view
            for (int i = 0; i < root.getChildCount(); i++) {
                View child = root.getChildAt(i);
                views.add(new Pair<View, Integer>(child, child.getVisibility()));
                child.setVisibility(View.GONE);
            }
            try {
                addCloseButton(frame);
                context.addContentView(frame,
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
            } catch (Exception e) {
                Clog.d(Clog.baseLogTag, e.toString());
            }
        } else
            frame = null;
    }

    @Override
    public void onHideCustomView() {
        super.onHideCustomView();

        if ((context == null) || (frame == null)) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.fullscreen_video_hide_error));
            return;
        }
        ViewGroup root = (ViewGroup) context.findViewById(android.R.id.content);
        if (root == null) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.fullscreen_video_hide_error));
            return;
        }

        root.removeView(frame);

        if (views != null) {
            // restore the views that were originally there
            for (Pair<View, Integer> child : views) {
                child.first.setVisibility(child.second);
            }
        }
        views = null;

        if (customViewCallback != null)
            customViewCallback.onCustomViewHidden();
    }

    private void addCloseButton(FrameLayout layout) {
        final ImageButton close = new ImageButton(context);
        close.setImageDrawable(context.getResources().getDrawable(
                android.R.drawable.ic_menu_close_clear_cancel));
        FrameLayout.LayoutParams blp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
                | Gravity.TOP);
        close.setLayoutParams(blp);
        close.setBackgroundColor(Color.TRANSPARENT);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onHideCustomView();
            }
        });
        layout.addView(close);
    }

    //HTML5 Location Callbacks
    @Override
    public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
        Context dialogContext = (adWebView != null) ? ViewUtil.getTopContext(adWebView) : context;
        AlertDialog.Builder adb = new AlertDialog.Builder(dialogContext);

        String title = String.format(this.context.getResources().getString(R.string.html5_geo_permission_prompt_title), origin);

        adb.setTitle(title);
        adb.setMessage(R.string.html5_geo_permission_prompt);

        adb.setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.invoke(origin, true, true);
            }
        });

        adb.setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.invoke(origin, false, false);
            }
        });

        adb.create().show();

        // We're presenting a modal dialog view, so this is equivalent to an expand
        // suppress if already expanded in MRAID
        if ((adView != null) && !adView.isInterstitial() && !adView.isMRAIDExpanded()) {
            this.adView.getAdDispatcher().onAdExpanded();
        }
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        if ((adView != null) && !adView.isInterstitial() && !adView.isMRAIDExpanded()) {
            this.adView.getAdDispatcher().onAdCollapsed();
        }
    }

}
