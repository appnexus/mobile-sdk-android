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

package com.appnexus.opensdkapp;

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
import android.widget.*;
import com.appnexus.opensdk.utils.Clog;

public class SettingsFragment extends Fragment {
    private Button loadAdButton;
    //private Button pasteAdButton;
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
        final View out = inflater.inflate(R.layout.fragment_settings, null);

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

        radioGroup = (RadioGroup) out.findViewById(R.id.radiogroup);
        radioGroup.check(R.id.radio_banner);
        radioGroup.setOnCheckedChangeListener(new RadioGroupListener());

        radioGroup2 = (RadioGroup) out.findViewById(R.id.radiogroup2);
        radioGroup2.check(R.id.radio_inapp);
        radioGroup2.setOnCheckedChangeListener(new RadioGroup2Listener());

        sizes.setOnItemSelectedListener(new SizeSelectedListener(this));

        refresh.setOnItemSelectedListener(new RefreshSelectedListener());

        placementEditText = (EditText) out.findViewById(R.id.edit_text);
        placementEditText.addTextChangedListener(new PlacementTextWatcher());

        colorButton = (Button) out.findViewById(R.id.color_button);
        colorView = (View) out.findViewById(R.id.color);
        colorButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//				AmbilWarnaDialog d = new AmbilWarnaDialog(out.getContext(),
//						color, new OnAmbilWarnaListener() {
//
//					@Override
//					public void onOk(AmbilWarnaDialog dialog, int color) {
//						SettingsFragment.this.color = color;
//						SettingsFragment.this.colorView
//								.setBackgroundColor(color);
//						SettingsFragment.this.iav
//								.setBackgroundColor(color);
//
//					}
//
//					@Override
//					public void onCancel(AmbilWarnaDialog dialog) {
//						// TODO Auto-generated method stub
//
//					}
//				});
//				d.show();
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
                return;
            }
            int refresh;
            try {
                setting = setting.replace("s", "");
                refresh = Integer.parseInt(setting);
            } catch (NumberFormatException e) {
                return;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }

    }

    private class SizeSelectedListener implements
            AdapterView.OnItemSelectedListener {
        SettingsFragment p;

        public SizeSelectedListener(SettingsFragment parent) {
            p = parent;
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

//			bannerAdView.setAdWidth(getSizeFromPosition(position)[0]);
//			bannerAdView.setAdHeight(getSizeFromPosition(position)[1]);

            DisplayMetrics m = new DisplayMetrics();
            SettingsFragment.this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(m);
            float d = m.density;


//			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(bannerAdView.getLayoutParams());
//			if(lp.width!=-1) lp.width = (int) (bannerAdView.getAdWidth()*d+0.5f);
//			if(lp.height!=-1) lp.height = (int) (bannerAdView.getAdHeight()*d+0.5f);
//			bannerAdView.setLayoutParams(lp);

            Log.d(Constants.LOG_TAG, "Size selected to: " + size_string);

        }

        int[] getSizeFromString(String size_string) {
            int out[] = new int[2];
            out[0] = Integer.parseInt(size_string.split("x")[0]);
            out[1] = Integer.parseInt(size_string.split("x")[1]);

            return out;
        }

        int[] getSizeFromPosition(int position) {
            String[] str_array = p.getResources().getStringArray(
                    R.array.sizes);

            String size_str = str_array[position];


            return getSizeFromString(size_str);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }

    }

    private class LoadAdOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.d(Constants.LOG_TAG, "Load ad pressed.");

            if (!isInterstitial) {
//				bannerAdView.loadAd();
                return;
            }

            // Load and display an interstitial
//			iav.loadAd();
        }

    }

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
                    Clog.d(Constants.LOG_TAG, "Set to load an interstitial");
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
//					bannerAdView.setOpensNativeBrowser(false);
//					iav.setOpensNativeBrowser(false);
                    break;
                case R.id.radio_native:
//					bannerAdView.setOpensNativeBrowser(true);
//					iav.setOpensNativeBrowser(true);
                    break;
            }

        }

    }
}
