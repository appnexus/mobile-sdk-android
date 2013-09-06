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

package com.appnexus.opensdkdemo.mediationtests;

import android.R;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.BannerAdView;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdkdemo.BlankActivity;
import com.appnexus.opensdkdemo.testviews.ThirdSuccessfulMediationView;
import com.appnexus.opensdkdemo.util.TestUtil;

public class TestActivityRefreshStdThenMediated extends ActivityInstrumentationTestCase2<BlankActivity> implements AdListener {

	BlankActivity activity;
	BannerAdView bav;

	public TestActivityRefreshStdThenMediated() {
		super(BlankActivity.class);
	}

//	public void testName() throws Exception {
//		assertEquals(true, true);
//
//	}
//
//	public void testActivity() throws Exception {
//		assertNotNull(getActivity());
////		assertNotNull(getActivity().getSupportFragmentManager());
//
//	}

	String old_base_url;
	boolean didPassStd = false;
	boolean didPassMed = false;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		old_base_url = Settings.getSettings().BASE_URL;
		Settings.getSettings().BASE_URL = TestUtil.MEDIATION_TEST_URL;
		Clog.d(TestUtil.testLogTag, "BASE_URL set to " + Settings.getSettings().BASE_URL);
		ThirdSuccessfulMediationView.didPass = false;
		didPassStd = false;
		didPassMed = false;

		setActivityInitialTouchMode(false);

		activity = getActivity();

		bav = new BannerAdView(activity);
		bav.setPlacementID("8a");
		bav.setAutoRefreshInterval(10);
		bav.setAdListener(this);

		bav.loadAd();

		final ViewGroup root = ((ViewGroup) activity.findViewById(R.id.content));
		if (root != null) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					LinearLayout l = new LinearLayout(activity);
					l.addView(bav);
					root.addView(l);
					Clog.w(TestUtil.testLogTag, "added views");
				}
			});
		}
		else
			Clog.w(TestUtil.testLogTag, "npe");

		Clog.w(TestUtil.testLogTag, "hi");
	}


	@Override
	protected void tearDown() {
		Clog.d(TestUtil.testLogTag, "tear down");
		Settings.getSettings().BASE_URL = old_base_url;
	}

	synchronized public void testRefresh() {
		Clog.w(TestUtil.testLogTag, "TEST REFRESH");

		bav.loadAd();

		try {
			wait(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Clog.w(TestUtil.testLogTag, "wait");

		bav.setPlacementID("8b");

		pause();

//		shouldWork.execute();
//		pause();
//		shouldWork.cancel(true);

		assertEquals(true, didPassStd);
//		assertEquals(true, didPassMed);
		assertEquals(true, ThirdSuccessfulMediationView.didPass);
	}

	synchronized private void pause() {
		Log.d(TestUtil.testLogTag, "pausing");
		try {
			synchronized (ThirdSuccessfulMediationView.lock) {
				ThirdSuccessfulMediationView.lock.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		Log.d(TestUtil.testLogTag, "unpausing");
	}

	@Override
	public void onAdLoaded(AdView adView) {
		didPassStd = true;
	}

	@Override
	public void onAdRequestFailed(AdView adView) {
		didPassStd = false;
	}

	@Override
	public void onAdExpanded(AdView adView) {
	}

	@Override
	public void onAdCollapsed(AdView adView) {
	}

	@Override
	public void onAdClicked(AdView adView) {
	}

}