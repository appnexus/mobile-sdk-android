package com.example.opensdkdemo;

import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.BannerAdView;
import com.appnexus.opensdk.InterstitialAdView;
import com.appnexus.opensdk.utils.Clog;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class MainTabFragment extends Fragment implements AdListener{
	private Button loadAdButton;
	private BannerAdView bannerAdView;
	private InterstitialAdView iav;
	private RadioGroup radioGroup;
	private EditText placementEditText;
	private boolean isInterstitial = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		final View out = inflater.inflate(R.layout.fragment_control, null);
		
		Spinner sizes = (Spinner ) out.findViewById(R.id.size_dropdown);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(container.getContext(), R.array.sizes, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		sizes.setAdapter(adapter);
		
		Spinner refresh = (Spinner ) out.findViewById(R.id.refresh_dropdown);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(container.getContext(), R.array.refresh, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		refresh.setAdapter(adapter2);
		
		// Locate member views
		loadAdButton = (Button) out.findViewById(R.id.loadad);
		loadAdButton.setOnClickListener(new LoadAdOnClickListener());
		
		bannerAdView = (BannerAdView) out.findViewById(R.id.banner);
		
		radioGroup = (RadioGroup) out.findViewById(R.id.radiogroup);
		radioGroup.check(R.id.radio_banner);
		radioGroup.setOnCheckedChangeListener(new RadioGroupListener());
		
		iav = new InterstitialAdView(out.getContext());
		//iav.setPlacementID("1281482");
		iav.setAdListener(this);
		
		sizes.setOnItemSelectedListener(new SizeSelectedListener());
		
		refresh.setOnItemSelectedListener(new RefreshSelectedListener());
		
		placementEditText = (EditText) out.findViewById(R.id.edit_text);
		placementEditText.addTextChangedListener(new PlacementTextWatcher());
		
		//Load default placement
		SharedPreferences sp = getActivity().getSharedPreferences("opensdkdemo", Activity.MODE_PRIVATE);
		String saved_placement=sp.getString("placement", "NO_PLACEMENT");
		if(!saved_placement.equals("NO_PLACEMENT")){
			placementEditText.setText(saved_placement);
		}else{
			placementEditText.setText("000000");
		}
		
		return out;
	}
	
	private class PlacementTextWatcher implements TextWatcher{

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
			
			SharedPreferences sp = getActivity().getSharedPreferences("opensdkdemo", Activity.MODE_PRIVATE);
			String saved_placement=sp.getString("placement", "NO_PLACEMENT");
			if(!saved_placement.equals(s.toString())){
				sp.edit().putString("placement", s.toString()).commit();
			}
		}
		
	}
	
	private class RefreshSelectedListener implements AdapterView.OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,
				long id) {
			String[] str_array=parent.getResources().getStringArray(R.array.refresh);
			if(position>=str_array.length) return;
			String setting = str_array[position];
			
			if(setting.equals("Off")){
				bannerAdView.setAutoRefresh(false);
				return;
			}
			int refresh;
			try{
				setting = setting.replace("s", "");
				refresh = Integer.parseInt(setting);
			}catch (NumberFormatException e){
				return;
			}
			bannerAdView.setAutoRefresh(true);
			bannerAdView.setAutoRefreshInterval(refresh*1000);
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	
	private class SizeSelectedListener implements AdapterView.OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,
				long id) {
			// Get size from array based on position parameter
			String[] str_array=parent.getResources().getStringArray(R.array.sizes);
			if(position>=str_array.length) return;
			String size_string = str_array[position];
			
			Log.d(Constants.logTag, "Size selected to: "+size_string);
			
			int width = Integer.parseInt(size_string.split("x")[0]);
			int height = Integer.parseInt(size_string.split("x")[1]);
			
			bannerAdView.setAdWidth(width);
			bannerAdView.setAdHeight(height);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class LoadAdOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			Log.d(Constants.logTag, "Load ad pressed.");
			if(!isInterstitial){
				bannerAdView.loadAd();
				return;
			}
			
			// Load and display an interstitial
			iav.loadAd();
		}
		
	}
	
	private class RadioGroupListener implements RadioGroup.OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch(checkedId){
				default:
					isInterstitial=false;
					break;
				case R.id.radio_interstitial:
					isInterstitial=true;
					Clog.d(Constants.logTag, "Set to load an interstitial");
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
		// TODO Auto-generated method stub
		
	}

}
