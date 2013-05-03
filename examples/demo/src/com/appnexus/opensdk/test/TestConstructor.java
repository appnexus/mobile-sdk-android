package com.appnexus.opensdk.test;

import com.appnexus.opensdk.BannerAdView;
import com.appnexus.opensdkdemo.DemoMainActivity;
import android.test.ActivityInstrumentationTestCase2;

public class TestConstructor extends
		ActivityInstrumentationTestCase2<DemoMainActivity> {
	DemoMainActivity activity;
	BannerAdView bav;
	
	public TestConstructor() {
		super(DemoMainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		activity=getActivity();
		
		bav = new BannerAdView(activity, "1281482", 320, 50);
	}

	public void testConstruction() {

		assertEquals("1281482", bav.getPlacementID());
		assertEquals(50, bav.getAdHeight());
		assertEquals(320, bav.getAdWidth());
		assertEquals(activity, bav.getContext());
	}


}
