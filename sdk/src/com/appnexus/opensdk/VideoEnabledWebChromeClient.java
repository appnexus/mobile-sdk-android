package com.appnexus.opensdk;

import android.*;
import android.R;
import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.VideoView;
import com.appnexus.opensdk.utils.Clog;

import java.util.ArrayList;

public class VideoEnabledWebChromeClient extends WebChromeClient {
    CustomViewCallback c;
    FrameLayout frame;
    Activity context;


    public VideoEnabledWebChromeClient(Activity context) {
        this.context = context;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        super.onShowCustomView(view, callback);
        c = callback;
//        Clog.d(Clog.baseLogTag, "Showing custom view");
        if (view instanceof FrameLayout) {
            frame = (FrameLayout) view;

            ViewGroup root = (ViewGroup) context.findViewById(R.id.content);

            for (int i = 0; i < root.getChildCount(); i++) {
                root.getChildAt(i).setVisibility(View.GONE);
            }

            try {
                addCloseButton(frame);
                context.addContentView(frame, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } catch (Exception e) {
                Clog.d(Clog.baseLogTag, e.toString());
            }

//            if (frame.getFocusedChild() instanceof VideoView) {
//                videoView = (VideoView) frame.getFocusedChild();
//                videoView.start();
////                videoView.setOnCompletionListener(this);
////                videoView.setOnErrorListener(this);
//            }
        }
    }

    @Override
    public void onHideCustomView() {
        super.onHideCustomView();
//        Clog.d(Clog.baseLogTag, "Hiding custom view");
//        if (videoView != null) videoView.stopPlayback();
        ViewGroup root = ((ViewGroup) context.findViewById(R.id.content));
        root.removeView(frame);

        for (int i = 0; i < root.getChildCount(); i++) {
            root.getChildAt(i).setVisibility(View.VISIBLE);
        }

        c.onCustomViewHidden();
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
