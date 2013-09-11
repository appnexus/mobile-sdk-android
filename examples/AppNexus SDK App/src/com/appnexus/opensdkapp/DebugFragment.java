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

        //TODO: get these Strings from SharedPreferences/Settings screen once that is implemented
        webView = new DebugAuctionWebView("1281482", "958", "test", "300x50");

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
        private String placementId;
        private String memberId;
        private String dongle;
        private String size;

        public String getUrl() {
            StringBuilder params = new StringBuilder();
            params.append("&id=").append(placementId);
            params.append("&debug_member=").append(memberId);
            params.append("&dongle=").append(dongle);
            params.append("&size=").append(size);
            return Constants.DEBUG_AUCTION_URL + params.toString();
        }

        private DebugAuctionWebView(String placementId, String memberId, String dongle, String size) {
            super(getActivity().getApplicationContext());

            this.placementId = placementId;
            this.memberId = memberId;
            this.dongle = dongle;
            this.size = size;

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
