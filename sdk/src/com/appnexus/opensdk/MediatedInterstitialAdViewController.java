package com.appnexus.opensdk;

import com.appnexus.opensdk.utils.Clog;

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
	
	View placeableView;

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
			Clog.e(Clog.mediationLogTag, Clog.getString(R.string.instantiation_exception));
			return;
		} catch (IllegalAccessException e) {
			Clog.e(Clog.mediationLogTag, Clog.getString(R.string.illegal_access_exception));
			return;
		}
		placeableView=mAV.requestAd((Activity)owner.getContext(), param, uid, width, height);
	}

	@Override
	public View getView() {
		return placeableView;
	}

	@Override
	public boolean failed() {
		// TODO Auto-generated method stub
		return false;
	}

}
