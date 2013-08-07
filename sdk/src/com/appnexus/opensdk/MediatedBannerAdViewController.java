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

import android.app.Activity;
import android.view.View;

public class MediatedBannerAdViewController implements Displayable {
	AdView owner;
	int width;
	int height;
	String uid;
	String className;
	String param;
	boolean failed=false;

	Class<?> c;
	MediatedBannerAdView mAV;
	
	View placeableView;

	public MediatedBannerAdViewController(AdView owner, AdResponse response) {
		width = response.getWidth();
		height = response.getHeight();
		uid = response.getMediatedUID();
		className = response.getMediatedViewClassName();
		param = response.getParameter();

		try {
			c = Class.forName(className);

		} catch (ClassNotFoundException e) {
			Clog.e(Clog.mediationLogTag, Clog.getString(R.string.class_not_found_exception));
			return;
		}

		try {
			mAV = (MediatedBannerAdView) c.newInstance();
		} catch (InstantiationException e) {
			Clog.e(Clog.mediationLogTag, Clog.getString(R.string.instantiation_exception));
			return;
		} catch (IllegalAccessException e) {
			Clog.e(Clog.mediationLogTag, Clog.getString(R.string.illegal_access_exception));
			return;
		}
		placeableView = mAV.requestAd(this, (Activity)owner.getContext(), param, uid, width, height, owner);
	}

	@Override
	public View getView() {
		return placeableView;
	}

	@Override
	public boolean failed() {
		return failed;
	}

	public void onAdLoad() {
		if(owner.getAdListener()!=null){
			owner.getAdListener().onAdLoaded(owner);
		}
	}

	public void onAdFailed() {
		if(owner.getAdListener()!=null){
			owner.getAdListener().onAdRequestFailed(owner);
		}
		this.failed=true;
	}
}
