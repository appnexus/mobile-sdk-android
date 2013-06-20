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

package com.appnexus.opensdk;

import com.appnexus.opensdk.utils.Clog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class AdActivity extends Activity {

	FrameLayout layout;
	long now;
	int orientation;

	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		layout = new FrameLayout(this);

		// Lock the orientation
		AdActivity.lockOrientation(this);

		setContentView(layout);

		setIAdView(InterstitialAdView.INTERSTITIALADVIEW_TO_USE);
		now = getIntent().getLongExtra("Time", System.currentTimeMillis());

		// Add a close button.
		addCloseButton(layout);

	}

	private void addCloseButton(FrameLayout layout) {
		final ImageButton close = new ImageButton(this);
		close.setImageDrawable(getResources().getDrawable(
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
				finish();

			}
		});
		layout.addView(close);
	}

	private void setIAdView(InterstitialAdView av) {
		if (layout != null) {
			layout.setBackgroundColor(av.getBackgroundColor());
			layout.removeAllViews();
			if (((ViewGroup) av.getParent()) != null) {
				((ViewGroup) av.getParent()).removeAllViews();
			}
			Pair<Long, Displayable> p = InterstitialAdView.q.poll();
			while (p != null && p.second != null
					&& now - p.first > InterstitialAdView.MAX_AGE) {
				Clog.w(Clog.baseLogTag, Clog.getString(R.string.too_old));
				p = InterstitialAdView.q.poll();
			}
			if (p == null)
				return;
			layout.addView(p.second.getView());
		}
	}

	@SuppressLint("InlinedApi")
	protected static void lockOrientation(Activity a) {
		Display d = ((WindowManager) a.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		final int orientation = a.getResources().getConfiguration().orientation;
		
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO) {
				a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else {
				int rotation = d.getRotation();
				if (rotation == android.view.Surface.ROTATION_90
						|| rotation == android.view.Surface.ROTATION_180) {
					a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
				} else {
					a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
			}
		}else if(orientation == Configuration.ORIENTATION_LANDSCAPE){
			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO) {
				a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			} else {
				int rotation = d.getRotation();
				if (rotation == android.view.Surface.ROTATION_0
						|| rotation == android.view.Surface.ROTATION_90) {
					a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				} else {
					a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
				}
			}
		}
	}

}
