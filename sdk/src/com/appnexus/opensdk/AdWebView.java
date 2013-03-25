package com.appnexus.opensdk;

import com.appnexus.opensdk.R;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;

/**
 * @author jshufro@appnexus.com
 * 
 */
@SuppressLint("ViewConstructor")
// This will only be constructed by AdFetcher.
public class AdWebView extends WebView implements Displayable {
	private boolean failed = false;
	private AdView destination;

	protected AdWebView(AdView owner) {
		super(owner.getContext());
		destination = owner;
		setup();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setup() {
		Settings.getSettings().ua = this.getSettings().getUserAgentString();
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		this.getSettings().setPluginState(WebSettings.PluginState.ON);
		this.getSettings().setBuiltInZoomControls(false);
		this.getSettings().setLightTouchEnabled(false);
		this.getSettings().setLoadsImagesAutomatically(true);
		this.getSettings().setSupportZoom(false);
		this.getSettings().setUseWideViewPort(true);
		this.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

		setHorizontalScrollbarOverlay(false);
		setHorizontalScrollBarEnabled(false);
		setVerticalScrollbarOverlay(false);
		setVerticalScrollBarEnabled(false);

		setBackgroundColor(Color.TRANSPARENT);
		setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);

		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return (event.getAction() == MotionEvent.ACTION_MOVE);
			}
		});

		setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingURL) {
				Clog.e(Clog.httpRespLogTag, Clog.getString(
						R.string.webclient_error, errorCode, description));
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				AdWebView.this.fail();
				Clog.e(Clog.httpRespLogTag,
						Clog.getString(R.string.webclient_error,
								error.getPrimaryError(), error.toString()));
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.startsWith("javascript:") || url.startsWith("mraid:")) return false;
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				getContext().startActivity(intent);
				return true;
			}

		});

	}

	protected void loadAd(AdResponse ad) {
		if (ad.getBody().equals("")) {
			fail();
			return;
		}

		String body = "<html><head /><body style='margin:0;padding:0;'>"
				+ ad.getBody() + "</body></html>";
		Clog.v(Clog.baseLogTag, Clog.getString(R.string.webview_loading, body));
		this.loadData(body, "text/html", "UTF-8");

		final float scale = destination.getContext().getResources()
				.getDisplayMetrics().density;
		int rheight = (int) (ad.getHeight() * scale + 0.5f);
		int rwidth = (int) (ad.getWidth() * scale + 0.5f);
		int rgravity = Gravity.CENTER;
		AdView.LayoutParams resize = new AdView.LayoutParams(rwidth, rheight,
				rgravity);
		this.setLayoutParams(resize);
	}

	@Override
	public View getView() {
		return this;
	}

	private void fail() {
		failed = true;
	}

	@Override
	public boolean failed() {
		return failed;
	}
}
