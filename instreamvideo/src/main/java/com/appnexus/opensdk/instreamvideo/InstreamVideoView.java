/*
 *    Copyright 2016 APPNEXUS INC
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
package com.appnexus.opensdk.instreamvideo;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.appnexus.opensdk.SDKSettings;
import com.appnexus.opensdk.VideoOrientation;
import com.appnexus.opensdk.utils.AdvertisingIDUtil;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


class InstreamVideoView extends FrameLayout {
    public static final String TAG = InstreamVideoView.class.getName();
    private VideoWebView videoWebView;
    private ArrayList<WeakReference<View>> friendlyObstructionList = new ArrayList<>();

    /**
     * Begin Construction
     */
    InstreamVideoView(Context context) {
        this(context, null);
    }

    InstreamVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    InstreamVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(new MutableContextWrapper(context), attrs, defStyle);
        setup(context);
    }


    void setup(Context context) {

        SDKSettings.init(context, null);

        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.new_adview));

        // Store the AppID in the settings
        Settings.getSettings().app_id = context.getApplicationContext()
                .getPackageName();
        Clog.v(Clog.baseLogTag,
                Clog.getString(R.string.appid, Settings.getSettings().app_id));

        Clog.v(Clog.baseLogTag, Clog.getString(R.string.making_adman));

        // Some AdMob creatives won't load unless we set their parent's viewgroup's padding to 0-0-0-0
        setPadding(0, 0, 0, 0);
    }


    void setVideoWebView(VideoWebView adVideoView) {
        videoWebView = adVideoView;
    }

    VideoWebView getVideoWebView() {
        return videoWebView;
    }

    boolean playAd(ViewGroup layout) {
        Clog.d(Clog.videoLogTag, "PlayAd called");
        if (!(layout instanceof FrameLayout || layout instanceof RelativeLayout)) {
            Clog.e(Clog.videoLogTag, "Invalid container - a RelativeLayout or FrameLayout is required");
        }
        updateMutableContext(layout);
        setupLayout(layout);
        videoWebView.playAd();
        return true;
    }


    private void setupLayout(ViewGroup layout) {
        if (layout != null && layout.getWidth() > 0 && layout.getHeight() > 0) {
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.setLayoutParams(params);
            this.addView(videoWebView);
            layout.addView(this);
        } else {
            Clog.d("Failure", "Failed to set layout params");
        }
    }


    boolean clearSelf() {
        onDestroy();
        Clog.d(Clog.videoLogTag, "clearSelf");
        removeAllViews();
        ViewGroup parentViewGroup = (ViewGroup) getParent();
        if (parentViewGroup != null) {
            ((ViewGroup) getParent()).removeView(this);
        }
        return true;
    }


    public void onDestroy() {
        if (videoWebView != null) {
            videoWebView.destroy();
        }
    }

    public void onPause() {
        if (videoWebView != null) {
            videoWebView.onPause();
        }
    }

    public void onResume() {
        if (videoWebView != null) {
            videoWebView.onResume();
            videoWebView.resumeAd();
        }
    }


    private void updateMutableContext(ViewGroup layout) {
        // Update the MutableContext Wrapper. with the new activity context.
        if(this.getContext() instanceof MutableContextWrapper) {
            ((MutableContextWrapper)this.getContext()).setBaseContext(layout.getContext());
        }

        // Update the MutableContext Wrapper. with the new activity context.
        if(this.videoWebView.getContext() instanceof MutableContextWrapper) {
            ((MutableContextWrapper)this.videoWebView.getContext()).setBaseContext(layout.getContext());
        }
    }

    public void pauseAd(){
        if(this.videoWebView != null){
            this.videoWebView.pauseAd();
        }
    }

    public void resumeAd(){
        if(this.videoWebView != null){
            this.videoWebView.resumeAd();
        }
    }

    public String getCreativeURL () {
        if(this.videoWebView != null){
            return this.videoWebView.getCreativeUrl();
        }

        return "";
    }

    public String getVastURL () {
        if(this.videoWebView != null){
            return this.videoWebView.getVastURL();
        }

        return "";
    }

    public int getVideoAdDuration () {
        if(this.videoWebView != null){
            return this.videoWebView.getVideoDuration();
        }

        return 0;
    }

    public String getVastXML () {
        if(this.videoWebView != null){
            return this.videoWebView.getVastXML();
        }

        return "";
    }

    protected VideoOrientation getVideoOrientation() {
        if(this.videoWebView != null){
            return this.videoWebView.getVideoOrientation();
        }

        return VideoOrientation.UNKNOWN;
    }

    public void getAdPlayElapsedTime(ResultCallback resultCallback){
        if(this.videoWebView != null){
            this.videoWebView.getAdPlayElapsedTime(resultCallback);
        }
    }

    protected void addFriendlyObstruction(View view) {
        if (view == null) {
            Clog.e(Clog.baseLogTag, "Invalid Friendly Obstruction View. The friendly obstruction view cannot be null.");
            return;
        }
        if (!alreadyAddedToFriendlyObstruction(view)) {
            friendlyObstructionList.add(new WeakReference<View>(view));
        }
        if (videoWebView != null) {
            videoWebView.addFriendlyObstruction(view);
        }
    }

    protected void removeFriendlyObstruction(View view) {
        for (WeakReference<View> viewWeakReference : friendlyObstructionList) {
            if (viewWeakReference.get() != null && viewWeakReference.get() == view) {
                friendlyObstructionList.remove(viewWeakReference);
                break;
            }
        }
        if (videoWebView != null) {
            videoWebView.removeFriendlyObstruction(view);
        }
    }

    protected void removeAllFriendlyObstructions() {
        friendlyObstructionList.clear();
        if (videoWebView != null) {
            videoWebView.removeAllFriendlyObstructions();
        }
    }

    private boolean alreadyAddedToFriendlyObstruction(View view) {
        for (WeakReference<View> viewWeakReference: friendlyObstructionList) {
            if (viewWeakReference.get() != null && viewWeakReference.get() == view) {
                return true;
            }
        }
        return false;
    }

    protected ArrayList<WeakReference<View>> getFriendlyObstructionList() {
        return friendlyObstructionList;
    }
}
