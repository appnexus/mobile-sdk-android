package com.appnexus.opensdk;

import android.R;
import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.appnexus.opensdk.utils.Clog;

public class VideoEnabledWebChromeClient extends WebChromeClient {
    CustomViewCallback customViewCallback;
    FrameLayout frame;
    Activity context;


    public VideoEnabledWebChromeClient(Activity context) {
        this.context = context;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        super.onShowCustomView(view, callback);
        Clog.d(Clog.baseLogTag, "Entering onShowCustomView");

        if (context == null) {
            Clog.e(Clog.baseLogTag, "onShowCustomView: context was null");
            return;
        }

        customViewCallback = callback;
        if (view instanceof FrameLayout) {
            frame = (FrameLayout) view;

            ViewGroup root = (ViewGroup) context.findViewById(R.id.content);
            if (root == null) {
                Clog.e(Clog.baseLogTag, "onShowCustomView: could not find root view");
                return;
            }

            // hide other children so that the only view shown is the custom view
            for (int i = 0; i < root.getChildCount(); i++)
                root.getChildAt(i).setVisibility(View.GONE);

            try {
                addCloseButton(frame);
                context.addContentView(frame, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } catch (Exception e) {
                Clog.d(Clog.baseLogTag, e.toString());
            }
        }
        else
            frame = null;
    }

    @Override
    public void onHideCustomView() {
        super.onHideCustomView();
        Clog.d(Clog.baseLogTag, "Entering onHideCustomView");

        if (context == null) {
            Clog.e(Clog.baseLogTag, "onHideCustomView: context was null");
            return;
        }

        ViewGroup root = ((ViewGroup) context.findViewById(R.id.content));
        if (root == null) {
            Clog.e(Clog.baseLogTag, "onHideCustomView: could not find root view");
            return;
        }

        if (frame == null) {
            Clog.e(Clog.baseLogTag, "onHideCustomView: frame was null");
            return;
        }

        root.removeView(frame);

        // restore the views that were originally there
        for (int i = 0; i < root.getChildCount(); i++) {
            root.getChildAt(i).setVisibility(View.VISIBLE);
        }

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
//
//    @Override
//    public void onCompletion(MediaPlayer mediaPlayer) {
//        Clog.d(Clog.baseLogTag, "onCompletion");
////        onHideCustomView();
//    }
//
//    @Override
//    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
//        Clog.d(Clog.baseLogTag, "onError");
////        onHideCustomView();
//        return false;
//    }
}
