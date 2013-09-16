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

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.*;
import com.appnexus.opensdk.utils.Clog;

public class DebugFragment extends Fragment {

    TextView txtRequest, txtResponse;
    EditText editMemberId, editDongle, editPlacementId;
    Button btnEmailServer, btnRunDebugAuction;
    DebugAuctionWebView webView;
    AlertDialog debugDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View out = inflater.inflate(R.layout.fragment_debug, null);

        Clog.d(Constants.LOG_TAG, "Switched to DebugFragment tab");

        txtRequest = (TextView) out.findViewById(R.id.request_text);
        txtResponse = (TextView) out.findViewById(R.id.response_text);
        editMemberId = (EditText) out.findViewById(R.id.memberid_edit);
        editDongle = (EditText) out.findViewById(R.id.dongle_edit);
        editPlacementId = (EditText) out.findViewById(R.id.placementid_edit);
        btnEmailServer = (Button) out.findViewById(R.id.btn_email_server);
        btnRunDebugAuction = (Button) out.findViewById(R.id.btn_run_debug);

        btnEmailServer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("message/rfc822");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Request:\n" + Clog.getLastRequest() + "\n\n" + "Response:\n" + Clog.getLastResponse());


                    startActivity(Intent.createChooser(emailIntent, "Select an app with which to send the debug information"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(out.getContext(), "No E-Mail App Installed!", Toast.LENGTH_SHORT).show();
                }
            }

        });

        webView = new DebugAuctionWebView(SettingsWrapper.getSettingsWrapperFromPrefs(getActivity()));

        createDebugAuctionDialog();

        btnRunDebugAuction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.runAuction();
                if (debugDialog != null)
                    debugDialog.show();
            }
        });

        return out;
    }

    private void createDebugAuctionDialog() {
        // hacked to be fullscreen with minHeight. see xml
        FrameLayout frame  = (FrameLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_debug, null, false);
        View placeholderView = frame.findViewById(R.id.debug_auction_view);
        webView.setLayoutParams(placeholderView.getLayoutParams());
        // make sure the close button is on top of the webView
        frame.addView(webView, 1);

        debugDialog = new AlertDialog.Builder(getActivity())
                .setView(frame)
                .create();

        ImageButton close = (ImageButton) frame.findViewById(R.id.btn_close);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (debugDialog != null)
                    debugDialog.dismiss();
            }
        });

    }

    protected void refresh() {
        if (txtRequest != null) txtRequest.setText(Clog.getLastRequest());
        if (txtResponse != null) txtResponse.setText(Clog.getLastResponse());
        if (editMemberId != null) editMemberId.setText(Prefs.getMemberId(getActivity()));
        if (editDongle != null) editDongle.setText(Prefs.getDongle(getActivity()));
        if (editPlacementId != null) editPlacementId.setText(Prefs.getPlacementId(getActivity()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null)
            webView.destroy();
        if (debugDialog != null)
            debugDialog.dismiss();

    }

    private class DebugAuctionWebView extends WebView {
        SettingsWrapper settingsWrapper;

        public String getUrl() {
            StringBuilder params = new StringBuilder();
            params.append("&id=").append(settingsWrapper.getPlacementId());
            params.append("&debug_member=").append(settingsWrapper.getMemberId());
            params.append("&dongle=").append(settingsWrapper.getDongle());
            params.append("&size=").append(settingsWrapper.getSize());
            return Constants.DEBUG_AUCTION_URL + params.toString();
        }

        private DebugAuctionWebView(SettingsWrapper settingsWrapper) {
            super(getActivity().getApplicationContext());

            this.settingsWrapper = settingsWrapper;
            setWebViewSettings();
        }

        private void setWebViewSettings() {
            // for scrolling
            setHorizontalScrollbarOverlay(true);
            setHorizontalScrollBarEnabled(true);
            setVerticalScrollbarOverlay(true);
            setVerticalScrollBarEnabled(true);

            // for zooming
            getSettings().setBuiltInZoomControls(true);
            getSettings().setSupportZoom(true);
            getSettings().setUseWideViewPort(true);

            // for no reason
            getSettings().setJavaScriptEnabled(true);
            getSettings().setDomStorageEnabled(true);
        }

        public void runAuction() {
            loadUrl(getUrl());
        }

    }
}
