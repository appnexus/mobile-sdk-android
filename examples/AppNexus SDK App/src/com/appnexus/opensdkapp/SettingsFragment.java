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

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.appnexus.opensdk.utils.Clog;

public class SettingsFragment extends Fragment {
    private Button btnLoadAd;
    private View colorView;
    private Spinner dropSize, dropRefresh, dropCloseDelay;

    private Button btnAdTypeBanner, btnAdTypeInterstitial,
    btnPSAsYes, btnPSAsNo,
    btnBrowserInApp, btnBrowserNative;

    private TextView txtSize, txtRefresh,
            txtBackgroundColor, txtCloseDelay,
            txtMemberId, txtDongle;
    private EditText editPlacementId,
            editBackgroundColor,
            editMemberId, editDongle;

    private static final int SIZE_INDEX_320x480 = 3;
    private static final int REFRESH_INDEX_30s = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View out = inflater.inflate(R.layout.fragment_settings, null);

        // Locate member views
        btnAdTypeBanner = (Button) out.findViewById(R.id.btn_banner);
        btnAdTypeInterstitial = (Button) out.findViewById(R.id.btn_interstitial);
        btnPSAsYes = (Button) out.findViewById(R.id.btn_psa_yes);
        btnPSAsNo = (Button) out.findViewById(R.id.btn_psa_no);
        btnBrowserInApp = (Button) out.findViewById(R.id.btn_browser_inapp);
        btnBrowserNative = (Button) out.findViewById(R.id.btn_browser_native);

        txtSize = (TextView) out.findViewById(R.id.txt_size);
        txtRefresh = (TextView) out.findViewById(R.id.txt_refresh);
        txtBackgroundColor = (TextView) out.findViewById(R.id.txt_interstitial_color);
        txtCloseDelay = (TextView) out.findViewById(R.id.txt_close_delay);
        txtMemberId = (TextView) out.findViewById(R.id.txt_memberid);
        txtDongle = (TextView) out.findViewById(R.id.txt_dongle);

        colorView = out.findViewById(R.id.view_color);

        editPlacementId = (EditText) out.findViewById(R.id.edit_placementid);
        editBackgroundColor = (EditText) out.findViewById(R.id.edit_interstitial_color);
        editMemberId = (EditText) out.findViewById(R.id.edit_memberid);
        editDongle = (EditText) out.findViewById(R.id.edit_dongle);

        btnLoadAd = (Button) out.findViewById(R.id.btn_load_ad);

        // create dropdowns
        dropSize = initDropdown(out, container, R.id.dropdown_size, R.array.sizes);
        dropRefresh = initDropdown(out, container, R.id.dropdown_refresh, R.array.refresh);
        dropCloseDelay = initDropdown(out, container, R.id.dropdown_close_delay, R.array.close_delay);

        /*
         * SET LISTENERS
         */

        // listeners for buttons
        btnAdTypeBanner.setOnClickListener(new AdTypeListener(true));
        btnAdTypeInterstitial.setOnClickListener(new AdTypeListener(false));
        btnPSAsYes.setOnClickListener(new PSAListener(true));
        btnPSAsNo.setOnClickListener(new PSAListener(false));
        btnBrowserInApp.setOnClickListener(new BrowserListener(true));
        btnBrowserNative.setOnClickListener(new BrowserListener(false));

        btnLoadAd.setOnClickListener(new LoadAdOnClickListener());

        // listeners for dropdowns
        dropSize.setOnItemSelectedListener(new SizeSelectedListener(
                getResources().getStringArray(R.array.sizes)));
        dropRefresh.setOnItemSelectedListener(new RefreshSelectedListener(
                getResources().getStringArray(R.array.refresh)));
        dropCloseDelay.setOnItemSelectedListener(new CloseDelaySelectedListener(
                getResources().getStringArray(R.array.close_delay)));

        // listeners for editText
        editPlacementId.addTextChangedListener(new SaveToPrefsTextWatcher(Prefs.KEY_PLACEMENT, Prefs.DEF_PLACEMENT));
        editBackgroundColor.addTextChangedListener(new BackgroundColorTextWatcher());
        editMemberId.addTextChangedListener(new SaveToPrefsTextWatcher(Prefs.KEY_MEMBERID, Prefs.DEF_MEMBERID));
        editDongle.addTextChangedListener(new SaveToPrefsTextWatcher(Prefs.KEY_DONGLE, Prefs.DEF_DONGLE));

        // set defaults
        setDefaultSettings();

        return out;
    }

    /*
     * SET DEFAULTS
     */

    private void setDefaultSettings() {
        // Load default placement id
        String savedPlacement = Prefs.getString(getActivity(), Prefs.KEY_PLACEMENT, Prefs.DEF_PLACEMENT);
        if (!savedPlacement.equals(Prefs.DEF_PLACEMENT))
            editPlacementId.setText(savedPlacement);
        else
            editPlacementId.setText(Prefs.DEF_PLACEMENT);

        // Load default background color
        String savedColor = Prefs.getString(getActivity(), Prefs.KEY_COLOR_HEX, Prefs.DEF_COLOR_HEX);
        Clog.d(Constants.LOG_TAG, "saved c: " + savedColor);
        editBackgroundColor.setText(savedColor);

        // Buttons: Banner mode, Allow PSAs, and In-App Browser
        btnAdTypeBanner.performClick();
        btnPSAsYes.performClick();
        btnBrowserInApp.performClick();

        // Size: select 320x480
        dropSize.setSelection(SIZE_INDEX_320x480);
        // Refresh: select 30 seconds
        dropRefresh.setSelection(REFRESH_INDEX_30s);
    }

    // generic function to create dropdown views
    private static Spinner initDropdown(View out, ViewGroup container, int resId, int stringsId) {
        Spinner dropdown = (Spinner) out.findViewById(resId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                container.getContext(), stringsId,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdown.setAdapter(adapter);
        return dropdown;
    }

    /**
     * Dropdown item listeners (OnItemSelectedListener)
     */
    private class SizeSelectedListener implements
            AdapterView.OnItemSelectedListener {
        String[] sizeStrings;

        private SizeSelectedListener(String[] sizeStrings) {
            this.sizeStrings = sizeStrings;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            // Get size from array based on position parameter
            if (position >= sizeStrings.length)
                return;
            String setting = sizeStrings[position];

            //TODO: parse and persist

//			bannerAdView.setAdWidth(getSizeFromPosition(position)[0]);
//			bannerAdView.setAdHeight(getSizeFromPosition(position)[1]);

//            DisplayMetrics m = new DisplayMetrics();
//            SettingsFragment.this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(m);
//            float d = m.density;


//			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(bannerAdView.getLayoutParams());
//			if(lp.width!=-1) lp.width = (int) (bannerAdView.getAdWidth()*d+0.5f);
//			if(lp.height!=-1) lp.height = (int) (bannerAdView.getAdHeight()*d+0.5f);
//			bannerAdView.setLayoutParams(lp);

            Clog.d(Constants.LOG_TAG, "Size selected to: " + setting);

        }

        int[] getSizeFromString(String size_string) {
            int out[] = new int[2];
            out[0] = Integer.parseInt(size_string.split("x")[0]);
            out[1] = Integer.parseInt(size_string.split("x")[1]);

            return out;
        }

        int[] getSizeFromPosition(int position) {
            String size_str = sizeStrings[position];


            return getSizeFromString(size_str);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private class RefreshSelectedListener implements
            AdapterView.OnItemSelectedListener {
        String[] refreshStrings;

        private RefreshSelectedListener(String[] refreshStrings) {
            this.refreshStrings = refreshStrings;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (position >= refreshStrings.length)
                return;
            String setting = refreshStrings[position];

            //TODO: parse and persist
//            if (setting.equals("Off")) {
//                return;
//            }
//            int refresh;
//            try {
//                setting = setting.replace(" seconds", "");
//                refresh = Integer.parseInt(setting);
//            } catch (NumberFormatException ignored) {
//            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private class CloseDelaySelectedListener implements
            AdapterView.OnItemSelectedListener {
        String[] closeDelayStrings;

        private CloseDelaySelectedListener(String[] closeDelayStrings) {
            this.closeDelayStrings = closeDelayStrings;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (position >= closeDelayStrings.length)
                return;
            String setting = closeDelayStrings[position];

            //TODO: parse and persist
//            if (setting.equals("Off")) {
//                return;
//            }
//            int closeDelay;
//            try {
//                setting = setting.replace(" seconds", "");
//                closeDelay = Integer.parseInt(setting);
//            } catch (NumberFormatException ignored) {
//            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private class LoadAdOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Clog.d(Constants.LOG_TAG, "Load ad pressed.");

            // in banner mode, the button will be disabled
            if (!btnAdTypeBanner.isEnabled()) {
//                bannerAdView.loadAd();
                return;
            }

            // Load and display an interstitial
//			iav.loadAd();

            ((MainActivity) getActivity()).onPageSelected(MainActivity.TABS.PREVIEW.ordinal());
        }

    }

    /**
     * OnClickListeners
     */

    private class AdTypeListener implements View.OnClickListener {
        boolean isBanner;

        private AdTypeListener(boolean isBanner) {
            this.isBanner = isBanner;
        }

        @Override
        public void onClick(View view) {
            handleAdType(isBanner);
        }

        private void handleAdType(boolean isBanner) {
            Clog.d(Constants.LOG_TAG, "Set to load an interstitial");
            // ad type buttons - opposite because we disable the option
            // that is selected
            btnAdTypeBanner.setEnabled(!isBanner);
            btnAdTypeInterstitial.setEnabled(isBanner);

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
    }

    private class PSAListener implements View.OnClickListener {
        boolean isAllowed;

        private PSAListener(boolean isAllowed) {
            this.isAllowed = isAllowed;
        }

        @Override
        public void onClick(View view) {
            handlePSAs(isAllowed);
        }

        private void handlePSAs(boolean isAllowed) {
            // PSA buttons. disable selected option
            btnPSAsYes.setEnabled(!isAllowed);
            btnPSAsNo.setEnabled(isAllowed);
        }
    }

    private class BrowserListener implements View.OnClickListener {
        boolean isInApp;

        private BrowserListener(boolean isInApp) {
            this.isInApp = isInApp;
        }

        @Override
        public void onClick(View view) {
            handleBrowser(isInApp);
        }

        private void handleBrowser(boolean isInApp) {
            // Browser buttons. disable selected option
            btnBrowserInApp.setEnabled(!isInApp);
            btnBrowserNative.setEnabled(isInApp);
        }
    }

    /**
     * TextWatchers for EditTexts
     */

    private class BackgroundColorTextWatcher implements TextWatcher {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            String savedColor = Prefs.getString(getActivity(),
                    Prefs.KEY_COLOR_HEX, Prefs.DEF_COLOR_HEX);
            if (!savedColor.equals(s.toString()))
                Prefs.writeString(getActivity(),
                        Prefs.KEY_COLOR_HEX, s.toString());

            if (s.length() == 8) {
                try {
                    colorView.setBackgroundColor(Color.parseColor("#" + s.toString()));
                } catch (IllegalArgumentException e) {
                    Clog.d(Constants.LOG_TAG, "Invalid hex color");
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    private class SaveToPrefsTextWatcher implements TextWatcher {

        String key;
        String defValue;

        private SaveToPrefsTextWatcher(String key, String defValue) {
            this.key = key;
            this.defValue = defValue;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            String savedValue = Prefs.getString(getActivity(), key, defValue);
            if (!savedValue.equals(s.toString()))
                Prefs.writeString(getActivity(), key, s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }
    }
}
