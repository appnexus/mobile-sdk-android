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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.*;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

public class DebugFragment extends Fragment {

    TextView txtRequest, txtResponse;
    EditText editMemberId, editDongle, editPlacementId;
    Button btnEmailServer, btnRunDebugAuction;
    DebugAuctionWebView webView;
    AlertDialog debugDialog;
    PullToRefreshScrollView pullToRefreshView;

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

        btnEmailServer.setOnClickListener(emailServerOnClickListener);

        webView = new DebugAuctionWebView();

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
        pullToRefreshView = (PullToRefreshScrollView) getActivity().getLayoutInflater().inflate(R.layout.dialog_debug, null, false);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                webView.runAuction();
            }
        });
        RelativeLayout frame  = (RelativeLayout) pullToRefreshView.findViewById(R.id.frame);
        View placeholderView = frame.findViewById(R.id.debug_auction_view);
        webView.setLayoutParams(placeholderView.getLayoutParams());
        // make sure the close button is on top of the webView
        frame.addView(webView, 1);

        debugDialog = new AlertDialog.Builder(getActivity())
                .setView(pullToRefreshView)
                .create();

        ImageButton close = (ImageButton) frame.findViewById(R.id.debug_btn_close);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (debugDialog != null)
                    debugDialog.dismiss();
            }
        });

        Button email = (Button) frame.findViewById(R.id.debug_btn_email);
        email.setOnClickListener(emailDebugAuctionOnClickListener);
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

    final private OnClickListener emailServerOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Request:\n" + Clog.getLastRequest() + "\n\n" + "Response:\n" + Clog.getLastResponse());

                startActivity(Intent.createChooser(emailIntent, "Select an app with which to send the debug information"));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), "No E-Mail App Installed!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    final private OnClickListener emailDebugAuctionOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_TEXT, webView.getResult());

                getActivity().startActivity(Intent.createChooser(emailIntent, "Select an app with which to send the debug information"));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), "No E-Mail App Installed!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private class DebugAuctionWebView extends WebView {

        String result;

        public String getUrl() {
            StringBuilder params = new StringBuilder();
            params.append("&id=").append(Prefs.getPlacementId(getActivity()));
            params.append("&debug_member=").append(Prefs.getMemberId(getActivity()));
            params.append("&dongle=").append(Prefs.getDongle(getActivity()));
            params.append("&size=").append(Prefs.getSize(getActivity()));
            return Constants.DEBUG_AUCTION_URL + params.toString();
        }

        private DebugAuctionWebView() {
            super(getActivity().getApplicationContext());

            setWebViewSettings();
        }

        private void setWebViewSettings() {
            // for scrolling
            setHorizontalScrollbarOverlay(true);
            setHorizontalScrollBarEnabled(true);
            setVerticalScrollbarOverlay(true);
            setVerticalScrollBarEnabled(true);

            WebSettings settings = getSettings();
            // for zooming
            settings.setBuiltInZoomControls(true);
            settings.setSupportZoom(true);
            settings.setUseWideViewPort(true);

            // for no reason
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);

            // for loading html
            settings.setDefaultTextEncodingName("utf-8");
        }

        public void runAuction() {
            final String debugAuctionUrl = getUrl();
            Clog.d(Constants.BASE_LOG_TAG, "Running a Debug Auction: " + debugAuctionUrl);
//            loadUrl(debugAuctionUrl);

            final HTTPGet<Void, Void, HTTPResponse> auctionGet = new HTTPGet<Void, Void, HTTPResponse>() {
                @Override
                protected void onPostExecute(HTTPResponse response) {
                    pullToRefreshView.onRefreshComplete();
                    String body = response.getResponseBody();
                    if (body != null) {
                        result = Html.fromHtml(body).toString();
                        Clog.d(Constants.BASE_LOG_TAG, result);
                        loadDataWithBaseURL(null, body, "text/html", "UTF-8", null);
                    }
                    else {
                        result = getString(R.string.debug_msg_debugauction_failed);
                        loadDataWithBaseURL(null, result, "text/html", "UTF-8", null);
                    }
                }

                @Override
                protected String getUrl() {
                    return debugAuctionUrl;
                }
            };

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                auctionGet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                auctionGet.execute();
            }
        }

        public String getResult() {
            return result;
        }

    }
}
