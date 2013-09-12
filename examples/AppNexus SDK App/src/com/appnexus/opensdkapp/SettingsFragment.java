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

public class SettingsFragment extends Fragment {
    private Button btnLoadAd;
    private boolean isInterstitial = false;
    private View colorView;
    private int color = 0xff000000;
    private Spinner dropSize, dropRefresh, dropCloseDelay;

    private Button btnAdTypeBanner, btnAdTypeInterstitial;
    private Button btnAdTypeBanner2, btnAdTypeInterstitial2;

    private TextView txtSize, txtRefresh,
            txtBackgroundColor, txtCloseDelay,
            txtMemberId, txtDongle;
    private EditText placementEditText,
            editBackgroundColor,
            editMemberId, editDongle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View out = inflater.inflate(R.layout.fragment_settings, null);

        dropSize = initSpinner(out, container, R.id.dropdown_size, R.array.sizes);
        dropRefresh = initSpinner(out, container, R.id.dropdown_refresh, R.array.refresh);
        dropCloseDelay = initSpinner(out, container, R.id.dropdown_close_delay, R.array.close_delay);

        // Locate member views
        btnLoadAd = (Button) out.findViewById(R.id.btn_load_ad);
        btnLoadAd.setOnClickListener(new LoadAdOnClickListener());

        btnAdTypeBanner = (Button) out.findViewById(R.id.btn_banner);
        btnAdTypeInterstitial = (Button) out.findViewById(R.id.btn_interstitial);

        btnAdTypeBanner.setOnClickListener(initBannerModeOnClickListener(true));

        btnAdTypeInterstitial.setOnClickListener(initBannerModeOnClickListener(false));

        btnAdTypeBanner2 = (Button) out.findViewById(R.id.btn_banner2);
        btnAdTypeInterstitial2 = (Button) out.findViewById(R.id.btn_interstitial2);

        btnAdTypeBanner2.setOnClickListener(initBannerModeOnClickListener(true));

        btnAdTypeInterstitial2.setOnClickListener(initBannerModeOnClickListener(false));

        txtSize = (TextView) out.findViewById(R.id.txt_size);
        txtRefresh = (TextView) out.findViewById(R.id.txt_refresh);
        txtBackgroundColor = (TextView) out.findViewById(R.id.txt_interstitial_color);
        txtCloseDelay = (TextView) out.findViewById(R.id.txt_close_delay);
        txtMemberId = (TextView) out.findViewById(R.id.txt_memberid);
        txtDongle = (TextView) out.findViewById(R.id.txt_dongle);

        placementEditText = (EditText) out.findViewById(R.id.edit_placementid);
        editBackgroundColor = (EditText) out.findViewById(R.id.edit_interstitial_color);
        editMemberId = (EditText) out.findViewById(R.id.edit_memberid);
        editDongle = (EditText) out.findViewById(R.id.edit_dongle);

        /*
         * SET LISTENERS
         */

        dropSize.setOnItemSelectedListener(new SizeSelectedListener(this));
        dropRefresh.setOnItemSelectedListener(new RefreshSelectedListener());

        placementEditText.addTextChangedListener(new PlacementTextWatcher());

        editBackgroundColor.addTextChangedListener(new BackgroundColorTextWatcher());

        /*
         * SET DEFAULTS
         */

        // Load default placement
        String savedPlacement = Prefs.getString(getActivity(), Prefs.KEY_PLACEMENT, Prefs.DEF_PLACEMENT);
        if (!savedPlacement.equals(Prefs.DEF_PLACEMENT))
            placementEditText.setText(savedPlacement);
        else
            placementEditText.setText(Prefs.DEF_PLACEMENT);

        // set default ad type to banner
        setBannerMode(true);

        // select 320x480
        dropSize.setSelection(3);
        // select 30 seconds
        dropRefresh.setSelection(1);

        return out;
    }

    private static Spinner initSpinner(View out, ViewGroup container, int resId, int stringsId) {
        Spinner dropdown = (Spinner) out.findViewById(resId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                container.getContext(), stringsId,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdown.setAdapter(adapter);
        return dropdown;
    }

    private View.OnClickListener initBannerModeOnClickListener(final boolean isBanner) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBannerMode(isBanner);
            }
        };
    }

    private void setBannerMode(boolean isBanner) {
        // ad type buttons
        btnAdTypeBanner.setEnabled(!isBanner);
        btnAdTypeInterstitial.setEnabled(isBanner);
        btnAdTypeBanner2.setEnabled(!isBanner);
        btnAdTypeInterstitial2.setEnabled(isBanner);

        //banner-only settings
        txtSize.setEnabled(isBanner);
        dropSize.setEnabled(isBanner);
        txtRefresh.setEnabled(isBanner);
        dropRefresh.setEnabled(isBanner);

        // interstitial-only settings
        txtBackgroundColor.setEnabled(!isBanner);
        editBackgroundColor.setEnabled(!isBanner);
        txtCloseDelay.setEnabled(!isBanner);
        dropCloseDelay.setEnabled(!isBanner);
    }

    private class PlacementTextWatcher implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            String savedPlacement = Prefs.getString(getActivity(),
                    Prefs.KEY_PLACEMENT, Prefs.DEF_PLACEMENT);
            if (!savedPlacement.equals(s.toString()))
                Prefs.writeString(getActivity(),
                        Prefs.KEY_PLACEMENT, savedPlacement);
        }

    }

    private class BackgroundColorTextWatcher implements TextWatcher {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            String savedColor = Prefs.getString(getActivity(),
                    Prefs.KEY_COLOR_HEX, Prefs.DEF_COLOR_HEX);
            if (!savedColor.equals(s.toString()))
                Prefs.writeString(getActivity(),
                        Prefs.KEY_COLOR_HEX, savedColor);
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
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
                    dropSize.setEnabled(true);
                    dropRefresh.setEnabled(true);
                    break;
                case R.id.radio_interstitial:
                    isInterstitial = true;
                    dropSize.setEnabled(false);
                    dropRefresh.setEnabled(false);
                    Log.d(Constants.LOG_TAG, "Set to load an interstitial");
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
