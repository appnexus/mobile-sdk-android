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
		
		bav = new BannerAdView(activity);
		bav.setPlacementID("1281482");
		bav.setAdHeight(320);
		bav.setAdWidth(50);
	}

	public void testConstruction() {

		assertEquals("1281482", bav.getPlacementID());
		assertEquals(50, bav.getAdHeight());
		assertEquals(320, bav.getAdWidth());
		assertEquals(activity, bav.getContext());
	}


}
