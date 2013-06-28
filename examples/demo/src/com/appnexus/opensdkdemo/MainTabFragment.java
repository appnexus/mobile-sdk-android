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

package com.appnexus.opensdkdemo;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;

import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.BannerAdView;
import com.appnexus.opensdk.InterstitialAdView;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdkdemo.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainTabFragment extends Fragment implements AdListener {
	private Button loadAdButton;
	//private Button pasteAdButton;
	private BannerAdView bannerAdView;
	private InterstitialAdView iav;
	private RadioGroup radioGroup;
	private RadioGroup radioGroup2;
	private TextView bannerText;
	private EditText placementEditText;
	private boolean isInterstitial = false;
	private Button colorButton;
	private View colorView;
	private int color = 0xff000000;
	private Spinner sizes;
	private Spinner refresh;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View out = inflater.inflate(R.layout.fragment_control, null);

		sizes = (Spinner) out.findViewById(R.id.size_dropdown);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				container.getContext(), R.array.sizes,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		sizes.setAdapter(adapter);

		refresh = (Spinner) out.findViewById(R.id.refresh_dropdown);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
				container.getContext(), R.array.refresh,
				android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		refresh.setAdapter(adapter2);

		// Locate member views
		loadAdButton = (Button) out.findViewById(R.id.loadad);
		loadAdButton.setOnClickListener(new LoadAdOnClickListener());

		//pasteAdButton = (Button) out.findViewById(R.id.pastead);
		//pasteAdButton.setOnClickListener(new PasteAdOnClickListener());

		bannerAdView = (BannerAdView) out.findViewById(R.id.banner);

		bannerAdView.setAdListener(new AdListener() {

			@Override
			public void onAdRequestFailed(AdView adView) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAdLoaded(AdView adView) {
				View v = getView();
				if(v==null) return;
				FrameLayout adframe = (FrameLayout) v.findViewById(
						R.id.adframe);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						adframe.getLayoutParams());
				if(lp!=null && adframe!=null){
					lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
					adframe.setLayoutParams(lp);
				}
				bannerText.setVisibility(TextView.INVISIBLE);

			}

			@Override
			public void onAdExpanded(AdView adView) {
				Toast.makeText(MainTabFragment.this.getActivity(), "Ad Expanded", Toast.LENGTH_SHORT).show();
				
			}

			@Override
			public void onAdCollapsed(AdView adView) {
				Toast.makeText(MainTabFragment.this.getActivity(), "Ad Collapsed", Toast.LENGTH_SHORT).show();
				
			}

			@Override
			public void onAdClicked(AdView adView) {
				Toast.makeText(MainTabFragment.this.getActivity(), "Opening Browser", Toast.LENGTH_SHORT).show();
				
			}
		});

		bannerText = (TextView) out.findViewById(R.id.bannertext);

		radioGroup = (RadioGroup) out.findViewById(R.id.radiogroup);
		radioGroup.check(R.id.radio_banner);
		radioGroup.setOnCheckedChangeListener(new RadioGroupListener());

		radioGroup2 = (RadioGroup) out.findViewById(R.id.radiogroup2);
		radioGroup2.check(R.id.radio_inapp);
		radioGroup2.setOnCheckedChangeListener(new RadioGroup2Listener());

		iav = new InterstitialAdView(out.getContext());
		// iav.setPlacementID("1281482");
		iav.setAdListener(this);

		sizes.setOnItemSelectedListener(new SizeSelectedListener(this));

		refresh.setOnItemSelectedListener(new RefreshSelectedListener());

		placementEditText = (EditText) out.findViewById(R.id.edit_text);
		placementEditText.addTextChangedListener(new PlacementTextWatcher());

		colorButton = (Button) out.findViewById(R.id.color_button);
		colorView = (View) out.findViewById(R.id.color);
		colorButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AmbilWarnaDialog d = new AmbilWarnaDialog(out.getContext(),
						color, new OnAmbilWarnaListener() {

							@Override
							public void onOk(AmbilWarnaDialog dialog, int color) {
								MainTabFragment.this.color = color;
								MainTabFragment.this.colorView
										.setBackgroundColor(color);
								MainTabFragment.this.iav
										.setBackgroundColor(color);

							}

							@Override
							public void onCancel(AmbilWarnaDialog dialog) {
								// TODO Auto-generated method stub

							}
						});
				d.show();
			}

		});

		// Load default placement
		SharedPreferences sp = getActivity().getSharedPreferences(
				"opensdkdemo", Activity.MODE_PRIVATE);
		String saved_placement = sp.getString("placement", "NO_PLACEMENT");
		if (!saved_placement.equals("NO_PLACEMENT")) {
			placementEditText.setText(saved_placement);
		} else {
			placementEditText.setText("000000");
		}
		sizes.setEnabled(true);
		refresh.setEnabled(true);
		colorButton.setEnabled(false);
		return out;
	}

	private class PlacementTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			bannerAdView.setPlacementID(s.toString());
			iav.setPlacementID(s.toString());

			SharedPreferences sp = getActivity().getSharedPreferences(
					"opensdkdemo", Activity.MODE_PRIVATE);
			String saved_placement = sp.getString("placement", "NO_PLACEMENT");
			if (!saved_placement.equals(s.toString())) {
				sp.edit().putString("placement", s.toString()).commit();
			}
		}

	}

	private class RefreshSelectedListener implements
			AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			String[] str_array = parent.getResources().getStringArray(
					R.array.refresh);
			if (position >= str_array.length)
				return;
			String setting = str_array[position];

			if (setting.equals("Off")) {
				bannerAdView.setAutoRefreshInterval(0);
				return;
			}
			int refresh;
			try {
				setting = setting.replace("s", "");
				refresh = Integer.parseInt(setting);
			} catch (NumberFormatException e) {
				return;
			}
			bannerAdView.setAutoRefreshInterval(refresh * 1000);

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	}

	private class SizeSelectedListener implements
			AdapterView.OnItemSelectedListener {
		MainTabFragment p;
		
		public SizeSelectedListener(MainTabFragment parent){
			p=parent;
		}
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// Get size from array based on position parameter
			String[] str_array = parent.getResources().getStringArray(
					R.array.sizes);
			if (position >= str_array.length)
				return;
			String size_string = str_array[position];

			bannerAdView.setAdWidth(getSizeFromPosition(position)[0]);
			bannerAdView.setAdHeight(getSizeFromPosition(position)[1]);
			
			DisplayMetrics m = new DisplayMetrics();
			MainTabFragment.this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(m);
			float d = m.density;
			
			
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(bannerAdView.getLayoutParams());
			if(lp.width!=-1) lp.width = (int) (bannerAdView.getAdWidth()*d+0.5f);
			if(lp.height!=-1) lp.height = (int) (bannerAdView.getAdHeight()*d+0.5f);
			bannerAdView.setLayoutParams(lp);
			
			Log.d(Constants.logTag, "Size selected to: " + size_string);

		}
		
		int[] getSizeFromString(String size_string){
			int out[] = new int[2];
			out[0]=Integer.parseInt(size_string.split("x")[0]);
			out[1]=Integer.parseInt(size_string.split("x")[1]);
			
			return out;
		}
		
		int[] getSizeFromPosition(int position){
			String[] str_array = p.getResources().getStringArray(
					R.array.sizes);
			
			String size_str = str_array[position];

			
			return getSizeFromString(size_str);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	}

	private class LoadAdOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(Constants.logTag, "Load ad pressed.");

			if (!isInterstitial) {
				bannerAdView.loadAd();
				return;
			}

			// Load and display an interstitial
			iav.loadAd();
		}

	}

	/*private class PasteAdOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(Constants.logTag, "Paste ad pressed.");

			// Set up an alert
			AlertDialog.Builder alert = new AlertDialog.Builder(getView()
					.getContext());

			alert.setTitle("Enter HTML");
			alert.setMessage("Paste or enter HTML ad tag here: ");

			final LinearLayout view = new LinearLayout(getView().getContext());
			view.setOrientation(LinearLayout.VERTICAL);
			final EditText input = new EditText(getView().getContext());
			final EditText width = new EditText(getView().getContext());
			final EditText height = new EditText(getView().getContext());
			TextView h = new TextView(getView().getContext());
			TextView w = new TextView(getView().getContext());

			h.setText("H: ");
			w.setText("W: ");
			LinearLayout hll = new LinearLayout(getView().getContext());
			LinearLayout wll = new LinearLayout(getView().getContext());
			hll.addView(h);
			hll.addView(height);
			wll.addView(w);
			wll.addView(width);
			width.setInputType(InputType.TYPE_CLASS_NUMBER);
			height.setInputType(InputType.TYPE_CLASS_NUMBER);

			view.addView(input);
			view.addView(wll);
			view.addView(hll);
			alert.setView(view);

			alert.setPositiveButton("Load", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String value = input.getText().toString();

					DisplayMetrics metrics = new DisplayMetrics();
					MainTabFragment.this.getActivity().getWindowManager()
							.getDefaultDisplay().getMetrics(metrics);
					float d = metrics.density;
					int h = -1;
					int w = -1;
					try {
						h = (int) ((Integer.parseInt(height.getText()
								.toString()) - 0.5f) / (d));
						w = (int) ((Integer
								.parseInt(width.getText().toString()) - 0.5f) / (d));
					} catch (NumberFormatException e) {
						Toast.makeText(getActivity(),
								"Invalid Number in Width/Height",
								Toast.LENGTH_SHORT).show();
						return;
					}
					AdResponse fakeResponse = new AdResponse(null, null, null);
					fakeResponse.body = value;
					fakeResponse.height = h;
					fakeResponse.width = w;
					if (isInterstitial) {
						AdWebView awv = new AdWebView(iav);
						awv.loadAd(fakeResponse);
						iav.display(awv);
					} else {
						AdWebView awv = new AdWebView(bannerAdView);
						awv.loadAd(fakeResponse);
						bannerAdView.setAutoRefresh(false);
						bannerAdView.setShouldReloadOnResume(false);
						bannerAdView.removeAllViews();
						bannerAdView.display(awv);
					}
				}

			});

			alert.setNegativeButton("Cancel", null);

			alert.show();

		}

	}*/

	private class RadioGroupListener implements
			RadioGroup.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			default:
				isInterstitial = false;
				sizes.setEnabled(true);
				refresh.setEnabled(true);
				colorButton.setEnabled(false);
				break;
			case R.id.radio_interstitial:
				isInterstitial = true;
				sizes.setEnabled(false);
				refresh.setEnabled(false);
				colorButton.setEnabled(true);
				Clog.d(Constants.logTag, "Set to load an interstitial");
				break;
			}
		}

	}

	private class RadioGroup2Listener implements
			RadioGroup.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			default:
				bannerAdView.setOpensNativeBrowser(false);
				iav.setOpensNativeBrowser(false);
				break;
			case R.id.radio_native:
				bannerAdView.setOpensNativeBrowser(true);
				iav.setOpensNativeBrowser(true);
				break;
			}

		}

	}

	@Override
	public void onAdLoaded(AdView adView) {
		iav.show();

	}

	@Override
	public void onAdRequestFailed(AdView adView) {
		Toast.makeText(this.getActivity(), "Ad request failed", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onAdExpanded(AdView adView) {
		Toast.makeText(this.getActivity(), "Ad expanded", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onAdCollapsed(AdView adView) {
		Toast.makeText(this.getActivity(), "Ad collapsed", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onAdClicked(AdView adView) {
		Toast.makeText(this.getActivity(), "Opening browser", Toast.LENGTH_SHORT).show();
		
	}

}
