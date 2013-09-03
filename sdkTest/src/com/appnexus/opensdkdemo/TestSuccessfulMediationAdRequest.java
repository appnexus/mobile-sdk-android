///*
//*    Copyright 2013 APPNEXUS INC
//*
//*    Licensed under the Apache License, Version 2.0 (the "License");
//*    you may not use this file except in compliance with the License.
//*    You may obtain a copy of the License at
//*
//*        http://www.apache.org/licenses/LICENSE-2.0
//*
//*    Unless required by applicable law or agreed to in writing, software
//*    distributed under the License is distributed on an "AS IS" BASIS,
//*    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//*    See the License for the specific language governing permissions and
//*    limitations under the License.
//*/
//
//package com.appnexus.opensdkdemo;
//
//import com.appnexus.opensdk.AdListener;
//import com.appnexus.opensdk.AdView;
//import com.appnexus.opensdk.BannerAdView;
//import com.appnexus.opensdkdemo.DemoMainActivity;
//import com.appnexus.opensdkdemo.R;
//
//import android.test.ActivityInstrumentationTestCase2;
//import android.view.View;
//
//public class TestSuccessfulMediationAdRequest extends
//		ActivityInstrumentationTestCase2<DemoMainActivity> implements AdListener {
//
//	public TestVisibility() {
//		super(DemoMainActivity.class);
//	}
//
//	@Override
//	protected void setUp() throws Exception {
//		super.setUp();
//		setActivityInitialTouchMode(false);
//	}
//
//	synchronized public void testVisibility() {
//		BannerAdView bav = (BannerAdView) getActivity().findViewById(R.id.banner);
//		bav.setPlacementID("1281482");
//		bav.setAdListener(this);
//
//		bav.loadAd();
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		assertEquals(View.VISIBLE, bav.getVisibility());
//	}
//
//	@Override
//	synchronized public void onAdLoaded(AdView adView) {
//		notify();
//
//	}
//
//	@Override
//	synchronized public void onAdRequestFailed(AdView adView) {
//		assertEquals(false, true);
//		notify();
//	}
//
//	@Override
//	public void onAdExpanded(AdView adView) {
//	}
//
//	@Override
//	public void onAdCollapsed(AdView adView) {
//	}
//
//	@Override
//	public void onAdClicked(AdView adView) {
//	}
//
//}
//
