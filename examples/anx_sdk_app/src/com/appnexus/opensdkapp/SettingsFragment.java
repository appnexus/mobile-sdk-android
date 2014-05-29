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

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

import android.support.v4.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SettingsFragment extends Fragment {
    private GradientDrawable colorViewBackground;
    private Spinner dropSize, dropRefresh, dropGender, dropIBURL;

    private Button btnAdTypeBanner, btnAdTypeInterstitial,
            btnPSAsYes, btnPSAsNo,
            btnBrowserInApp, btnBrowserNative, btnShowAdvanced, btnHideAdvanced, btnEditCustomKeywords;

    private TextView txtSize, txtRefresh,
            txtBackgroundColor;
    private EditText editPlacementId,
            editBackgroundColor,
            editMemberId, editDongle, editAge, editZip;
    private String currentValidColor;

    private OnLoadAdClickedListener callback;

    // keep saved settings in memory in order to optimize writes
    boolean savedAdType, savedPSAs, savedBrowser;
    String savedPlacement, savedSize, savedRefresh, savedColor, savedMemberId, savedDongle, savedAge, savedZip;
    AdView.GENDER savedGender;
    HashMap<String, String> customKeywords;

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
        btnShowAdvanced = (Button) out.findViewById(R.id.btn_advanced_yes);
        btnHideAdvanced = (Button) out.findViewById(R.id.btn_advanced_no);
        btnEditCustomKeywords = (Button) out.findViewById(R.id.edit_custom_keywords);

        txtSize = (TextView) out.findViewById(R.id.txt_size);
        txtRefresh = (TextView) out.findViewById(R.id.txt_refresh);
        txtBackgroundColor = (TextView) out.findViewById(R.id.txt_interstitial_color);

        colorViewBackground = (GradientDrawable) out.findViewById(R.id.view_color).getBackground();

        editPlacementId = (EditText) out.findViewById(R.id.edit_placementid);
        editBackgroundColor = (EditText) out.findViewById(R.id.edit_interstitial_color);
        editMemberId = (EditText) out.findViewById(R.id.edit_memberid);
        editDongle = (EditText) out.findViewById(R.id.edit_dongle);
        editAge = (EditText) out.findViewById(R.id.edit_age);
        editZip = (EditText) out.findViewById(R.id.edit_zip);

        Button btnLoadAd = (Button) out.findViewById(R.id.btn_load_ad);

        // create dropdowns
        dropSize = initDropdown(out, container, R.id.dropdown_size, R.array.sizes);
        dropRefresh = initDropdown(out, container, R.id.dropdown_refresh, R.array.refresh);
        dropGender = initDropdown(out, container, R.id.dropdown_gender, R.array.gender);
        dropIBURL = initDropdown(out, container, R.id.spinner_ib_url, R.array.ib_url);


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
        btnShowAdvanced.setOnClickListener(new ShowAdvancedListener(true, this));
        btnHideAdvanced.setOnClickListener(new ShowAdvancedListener(false, this));
        btnEditCustomKeywords.setOnClickListener(new CustomKeywordsListener(this));

        btnLoadAd.setOnClickListener(new LoadAdOnClickListener());

        // listeners for dropdowns
        dropSize.setOnItemSelectedListener(new SizeSelectedListener(
                getResources().getStringArray(R.array.sizes)));
        dropRefresh.setOnItemSelectedListener(new RefreshSelectedListener(
                getResources().getStringArray(R.array.refresh)));
        dropGender.setOnItemSelectedListener(new GenderSelectedListener(getResources().getStringArray(R.array.gender)));
        dropIBURL.setOnItemSelectedListener(new IBURLSelectedListener(getResources().getStringArray(R.array.ib_url)));

        // listeners for editText
        editBackgroundColor.addTextChangedListener(new BackgroundColorTextWatcher());
        editAge.addTextChangedListener(new AgeTextWatcher());
        editZip.addTextChangedListener(new ZipTextWatcher());

        // load saved or default settings
        loadSettings();

        return out;
    }

    /*
     * Load saved settings from Prefs
     */

    private void loadSettings() {
        // Buttons: Banner mode, Allow PSAs, and In-App Browser
        savedAdType = Prefs.getAdType(getActivity());
        savedPSAs = Prefs.getAllowPSAs(getActivity());
        savedBrowser = Prefs.getBrowserInApp(getActivity());

        if (savedAdType)
            btnAdTypeBanner.performClick();
        else
            btnAdTypeInterstitial.performClick();

        if (savedPSAs)
            btnPSAsYes.performClick();
        else
            btnPSAsNo.performClick();

        if (savedBrowser)
            btnBrowserInApp.performClick();
        else
            btnBrowserNative.performClick();

        // Load default placement id
        savedPlacement = Prefs.getPlacementId(getActivity());
        editPlacementId.setText(savedPlacement);

        // Load size
        savedSize = Prefs.getSize(getActivity());

        String[] sizeStrings = getResources().getStringArray(R.array.sizes);
        for (int i = 0; i < sizeStrings.length; i++) {
            if (sizeStrings[i].equals(savedSize)) {
                dropSize.setSelection(i);
            }
        }

        // Load refresh
        savedRefresh = Prefs.getRefresh(getActivity());

        String[] refreshStrings = getResources().getStringArray(R.array.refresh);
        for (int i = 0; i < refreshStrings.length; i++) {
            if (refreshStrings[i].equals(savedRefresh)) {
                dropRefresh.setSelection(i);
            }
        }

        // Load background color
        savedColor = Prefs.getColor(getActivity());
        editBackgroundColor.setText(savedColor);

        // Load member id
        savedMemberId = Prefs.getMemberId(getActivity());
        editMemberId.setText(savedMemberId);

        // Load dongle
        savedDongle = Prefs.getDongle(getActivity());
        editDongle.setText(savedDongle);

        //Load Gender
        savedGender = AdView.GENDER.values()[Prefs.getGender(getActivity())];
        dropGender.setSelection(savedGender.ordinal());

        //Load Age
        savedAge = Prefs.getAge(getActivity());
        editAge.setText(savedAge);

        //Load Zip
        savedZip = Prefs.getZip(getActivity());
        editZip.setText(savedZip);

        //Load Keywords
        customKeywords = Prefs.getCustomKeywords(getActivity());


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

            Clog.d(Constants.BASE_LOG_TAG, "Size set to: " + setting);
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

            Clog.d(Constants.BASE_LOG_TAG, "Refresh set to: " + refreshStrings[position]);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private class IBURLSelectedListener implements AdapterView.OnItemSelectedListener{
        String[] ibNames;

        private IBURLSelectedListener(String[]ibNames){this.ibNames=ibNames;}

        @Override
        public void onItemSelected(AdapterView<?> parents, View view, int position, long id){
            if(position > ibNames.length) return;

            if(position==1) {
                Settings.BASE_URL = "http://ib.client-testing.adnxs.net/";
                Settings.COOKIE_DOMAIN = "http://ib.client-testing.adnxs.net/";
                Settings.REQUEST_BASE_URL = "http://ib.client-testing.adnxs.net/mob?";
                Settings.INSTALL_BASE_URL = "http://ib.client-testing.adnxs.net/install?";
            }else{
                Settings.BASE_URL = "http://mediation.adnxs.com/";
                Settings.COOKIE_DOMAIN = "http://mediation.adnxs.com";
                Settings.REQUEST_BASE_URL = "http://mediation.adnxs.com/mob?";
                Settings.INSTALL_BASE_URL = "http://mediation.adnxs.com/install?";
            }

            Clog.d(Constants.BASE_LOG_TAG, "IB URL Set to "+ibNames[position]);
        }


        @Override
        public void onNothingSelected(AdapterView<?> viewOfAdapter){

        }
    }

    private class GenderSelectedListener implements AdapterView.OnItemSelectedListener{
        String[] genderStrings;

        private GenderSelectedListener(String[]genderStrings){
            this.genderStrings=genderStrings;
        }

        @Override
        public void onItemSelected(AdapterView<?> parents, View view, int position, long id){
            if(position > genderStrings.length) return;

            Clog.d(Constants.BASE_LOG_TAG, "Gender set to: " + genderStrings[position]);
        }

        @Override
        public void onNothingSelected(AdapterView<?> viewOfAdapter){

        }
    }

    public interface OnLoadAdClickedListener {
        public void onLoadAdClicked();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (OnLoadAdClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadAdClickedListener");
        }
    }

    private class LoadAdOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Clog.d(Constants.BASE_LOG_TAG, "Load Ad button pressed.");

            // only write if something has changed
            Prefs prefs = new Prefs(getActivity());

            // counterintuitive, but if banner mode is selected, the button will be DISabled.
            // but we persist the field as "isBanner"
            if (savedAdType == btnAdTypeBanner.isEnabled()) {
                savedAdType = !btnAdTypeBanner.isEnabled();
                prefs.writeBoolean(Prefs.KEY_ADTYPE_IS_BANNER, savedAdType);
            }
            if (savedPSAs == btnPSAsYes.isEnabled()) {
                savedPSAs = !btnPSAsYes.isEnabled();
                prefs.writeBoolean(Prefs.KEY_ALLOW_PSAS, savedPSAs);
            }
            if (savedBrowser == btnBrowserInApp.isEnabled()) {
                savedBrowser = !btnBrowserInApp.isEnabled();
                prefs.writeBoolean(Prefs.KEY_BROWSER_IS_INAPP, savedBrowser);
            }
            if (!savedPlacement.equals(editPlacementId.getText().toString())) {
                savedPlacement = editPlacementId.getText().toString();
                prefs.writeString(Prefs.KEY_PLACEMENT, savedPlacement);
            }

            if (!savedSize.equals(dropSize.getSelectedItem())) {
                savedSize = (String) dropSize.getSelectedItem();
                prefs.writeString(Prefs.KEY_SIZE, savedSize);
            }
            if (!savedRefresh.equals(dropRefresh.getSelectedItem())) {
                savedRefresh = (String) dropRefresh.getSelectedItem();
                prefs.writeString(Prefs.KEY_REFRESH, savedRefresh);
            }

            if (!savedColor.equals(currentValidColor)) {
                savedColor = currentValidColor;
                prefs.writeString(Prefs.KEY_COLOR_HEX, savedColor);
            }

            if (!savedMemberId.equals(editMemberId.getText().toString())) {
                savedMemberId = editMemberId.getText().toString();
                prefs.writeString(Prefs.KEY_MEMBERID, savedMemberId);
            }
            if (!savedDongle.equals(editDongle.getText().toString())) {
                savedDongle = editDongle.getText().toString();
                prefs.writeString(Prefs.KEY_DONGLE, savedDongle);
            }

            if(savedGender != AdView.GENDER.values()[dropGender.getSelectedItemPosition()]){
                savedGender = AdView.GENDER.values()[dropGender.getSelectedItemPosition()];
                prefs.writeInt(Prefs.KEY_GENDER, savedGender.ordinal());
            }

            if(!savedAge.equals(editAge.getText().toString())){
                savedAge = editAge.getText().toString();
                prefs.writeString(Prefs.KEY_AGE, savedAge);
            }

            if(!savedZip.equals(editZip.getText().toString())){
                savedZip = editZip.getText().toString();
                prefs.writeString(Prefs.KEY_ZIP, savedZip);
            }


            prefs.writeHashMap(Prefs.KEY_CUSTOM_KEYWORDS, customKeywords);

            prefs.applyChanges();

            if (callback != null)
                callback.onLoadAdClicked();

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
            Clog.d(Constants.BASE_LOG_TAG, "AdType set isBanner to: " + isBanner);

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
            Clog.d(Constants.BASE_LOG_TAG, "PSAs set isAllowed to: " + isAllowed);

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
            Clog.d(Constants.BASE_LOG_TAG, "Browser type set isInApp to: " + isInApp);

            // Browser buttons. disable selected option
            btnBrowserInApp.setEnabled(!isInApp);
            btnBrowserNative.setEnabled(isInApp);
        }
    }

    private class ShowAdvancedListener implements View.OnClickListener{
        boolean show;
        Fragment f;
        private ShowAdvancedListener(boolean show, Fragment f){
            this.show=show;
            this.f=f;
        }

        @Override
        public void onClick(View v){
            ViewGroup viewGroup = (ViewGroup) f.getActivity().findViewById(R.id.advanced_block);
            if(show){
                viewGroup.setVisibility(View.VISIBLE);
            }else{
                viewGroup.setVisibility(View.GONE);
            }
            btnHideAdvanced.setEnabled(show);
            btnShowAdvanced.setEnabled(!show);
        }
    }

    private class CustomKeywordsListener implements View.OnClickListener{
        Fragment f;
        private CustomKeywordsListener(Fragment f){
            this.f=f;
        }

        @Override
        public void onClick(View v){
            CustomKeywordsDialog customKeywordsDialog = new CustomKeywordsDialog();
            customKeywordsDialog.show(f.getFragmentManager(), "ckd");
        }
    }

    private class CustomKeywordsDialog extends DialogFragment{

        HashMap<String, String> currentKeywords = new HashMap<String, String>();
        ArrayList<Pair<EditText, EditText>> pairs = new ArrayList<Pair<EditText, EditText>>();

        public CustomKeywordsDialog(){

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.dialog_fragment_custom_keywords, container);
            getDialog().setTitle("Edit Custom Keywords");

            final LinearLayout keywords_ll = (LinearLayout) view.findViewById(R.id.keyword_layout);
            Button done = (Button) view.findViewById(R.id.done_editing);
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentKeywords.clear();
                    for(Pair<EditText, EditText> p : pairs){
                        currentKeywords.put(p.first.getText().toString(), p.second.getText().toString());
                    }
                    SettingsFragment.this.customKeywords = currentKeywords;
                    getDialog().dismiss();
                }
            });

            //Add current keywords
            for(String s : customKeywords.keySet()){
                addKeyword(keywords_ll, s, customKeywords.get(s));
            }

            Button add = (Button) view.findViewById(R.id.add_keyword);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addKeyword(keywords_ll, "KEY", "VALUE");
                }
            });
            return view;
        }

        public void addKeyword(final LinearLayout l, String k, String v){
            Context context = CustomKeywordsDialog.this.getActivity();
            final LinearLayout new_keyword = new LinearLayout(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            new_keyword.setLayoutParams(lp);

            Button minus = new Button(context);
            final EditText key = new EditText(context);
            EditText value = new EditText(context);
            minus.setText("-");
            minus.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    l.removeView(new_keyword);
                    for(int j=0; j<pairs.size();j++){
                        if(pairs.get(j).first == key){
                            pairs.remove(j);
                        }
                    }
                }
            });
            new_keyword.addView(minus);


            key.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            key.setText(k);
            new_keyword.addView(key);


            value.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            value.setText(v);
            new_keyword.addView(value);

            pairs.add(new Pair<EditText, EditText>(key, value));

            l.addView(new_keyword);
        }

    }

    /**
     * TextWatchers for EditTexts
     */

    private class BackgroundColorTextWatcher implements TextWatcher {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (s.length() == 8) {
                try {
                    colorViewBackground.setColor(Color.parseColor("#" + s.toString()));
                    // only set this if the color is valid
                    currentValidColor = s.toString();
                } catch (IllegalArgumentException e) {
                    Clog.d(Constants.BASE_LOG_TAG, "Invalid hex color");
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

    private class AgeTextWatcher implements TextWatcher{
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){
            Clog.d(Constants.BASE_LOG_TAG, "Age set to: "+s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    private class ZipTextWatcher implements TextWatcher{
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){
            Clog.d(Constants.BASE_LOG_TAG, "Zip set to: "+s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }
}
