package com.appnexus.opensdk;

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
    CustomViewCallback c;
    View oldView;
    FrameLayout frame;
    View video;
    Activity context;


    public VideoEnabledWebChromeClient(Activity context) {
        this.context = context;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        c = callback;
        super.onShowCustomView(view, callback);
        if (view instanceof FrameLayout) {
            frame = (FrameLayout) view;
//				if (frame.getFocusedChild() instanceof VideoView) {
//					VideoView video = (VideoView) frame.getFocusedChild();
//					frame.removeView(video);
//					((Activity) AdWebView.this.destination.getContext())
//							.setContentView(video);
//					video.start();
//				}

            oldView = ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
            try {
                video = frame.getFocusedChild();
                addCloseButton(frame);
                context.setContentView(frame);
            } catch (Exception e) {
                Clog.d(Clog.baseLogTag, e.toString());
            }
        }
    }

    @Override
    public void onHideCustomView() {
        super.onHideCustomView();
//            video.stopPlayback();
        c.onCustomViewHidden();
        context.setContentView(oldView);
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
}
