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

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.ViewUtil;
import com.appnexus.opensdk.vastdata.AdModel;

class VastVideoView extends VideoView implements OnErrorListener, Displayable {
	private String TAG = getClass().getSimpleName();
    private AdModel vastAd;

	/**
	 * Constructor to Initialize XVideoView.
	 * @param context
	 */
	public VastVideoView(Context context, AdModel vastAdResponse) {
		super(context);
        this.vastAd = vastAdResponse;
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		setLayoutParams(params);
        int videoViewId = VastVideoUtil.VIDEO_VIEW;
        setId(videoViewId);
		setOnErrorListener(this);
	}

    public AdModel getVastAd() {
        return vastAd;
    }

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		Log.e(TAG, "onError");
		return false;
	}

    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean failed() {
        return false;
    }

    @Override
    public void destroy() {
        try {
            vastAd = null;
            VastVideoView.this.stopPlayback();
            ViewUtil.removeChildFromParent(this);
        }catch (Exception e){
            Clog.e(Clog.baseLogTag, "Error destroying video view: " + e.getMessage());
        }

    }

    @Override
    public int getCreativeWidth() {
        return LayoutParams.MATCH_PARENT;
    }

    @Override
    public int getCreativeHeight() {
        return LayoutParams.WRAP_CONTENT;
    }

    @Override
    public void onPause() {
        Clog.i(Clog.baseLogTag, "VideoView onPause");
    }

    @Override
    public void onResume() {
        Clog.i(Clog.baseLogTag, "VideoView onResume");
    }

    @Override
    public void onDestroy() {
        Clog.i(Clog.baseLogTag, "VideoView onDestroy");
        destroy();
    }

}
