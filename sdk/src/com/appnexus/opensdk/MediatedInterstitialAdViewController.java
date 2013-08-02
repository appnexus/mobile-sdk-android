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
	
	public MediatedInterstitialAdViewController(AdView owner, AdResponse response) {
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
			mAV = (MediatedInterstitialAdView) c.newInstance();
		} catch (InstantiationException e) {
			Clog.e(Clog.mediationLogTag, Clog.getString(R.string.instantiation_exception));
			return;
		} catch (IllegalAccessException e) {
			Clog.e(Clog.mediationLogTag, Clog.getString(R.string.illegal_access_exception));
			return;
		}
	}

	@Override
	public View getView() {
		return mAV.requestAd((Activity)owner.getContext(), param, uid);
	}

	@Override
	public boolean failed() {
		// TODO Auto-generated method stub
		// Will spawn an ad request with the fail url and await further instruction
		return false;
	}

}
