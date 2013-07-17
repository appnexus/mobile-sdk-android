package com.appnexus.opensdk;

import android.app.Activity;
import android.view.View;

public class MediatedInterstitialAdViewController implements Displayable {
	AdView owner;
	int width;
	int height;
	String uid;
	String className;
	String param;

	Class<?> c;
	MediatedInterstitialAdView mAV;

	public MediatedInterstitialAdViewController(AdView owner, AdResponse response) {
		width = response.getWidth();
		height = response.getHeight();
		uid = response.getMediatedUID();
		className = response.getMediatedViewClassName();
		param = response.getParameter();
		
		try {
			c = Class.forName(className);

		} catch (ClassNotFoundException e) {
			// TODO error message
		}

		try {
			mAV = (MediatedInterstitialAdView) c.newInstance();
		} catch (InstantiationException e) {
			// TODO error message
			return;
		} catch (IllegalAccessException e) {
			// TODO error message
			return;
		}
		mAV.requestAd((Activity)owner.getContext(), param, uid, width, height);
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean failed() {
		// TODO Auto-generated method stub
		return false;
	}

}
