package com.appnexus.opensdk.test;

import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.BannerAdView;
import com.appnexus.opensdkdemo.DemoMainActivity;
import com.appnexus.opensdkdemo.R;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

public class TestVisibility extends
		ActivityInstrumentationTestCase2<DemoMainActivity> implements AdListener{
	DemoMainActivity activity;
	boolean visible=false;
	
	public TestVisibility() {
		super(DemoMainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		activity=getActivity();
	}

	synchronized public void testVisibility() {
		BannerAdView bav = (BannerAdView) activity.findViewById(R.id.banner);
		bav.setPlacementID("1281482");
		bav.setAdListener(this);
		
		bav.loadAd();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(View.VISIBLE, bav.getVisibility());
	}

	@Override
	synchronized public void onAdLoaded(AdView adView) {
		notify();
		
	}

	@Override
	synchronized public void onAdRequestFailed(AdView adView) {
		assertEquals(false, true);
		notify();
	}

}
