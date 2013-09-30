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

package com.appnexus.opensdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.net.http.SslError;
import android.provider.CalendarContract;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.webkit.*;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.W3CEvent;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

@SuppressLint("InlinedApi")
public class MRAIDImplementation {
    MRAIDWebView owner;
    boolean readyFired = false;
    boolean expanded = false;
    boolean hidden = false;
    int default_width, default_height;

    public MRAIDImplementation(MRAIDWebView owner) {
        this.owner = owner;
    }

    // The webview about to load the ad, and the html ad content
    protected String onPreLoadContent(WebView wv, String html) {
        // Check to ensure <html> tags are present
        if (!html.contains("<html>")) {
            html = "<html><head></head><body style='padding:0;margin:0;'>"
                    + html + "</body></html>";
        } else if (!html.contains("<head>")) {
            // The <html> tags are present, but there is no <head> section to
            // inject the mraid js
            html = html.replace("<html>", "<html><head></head>");
        }

        // Insert mraid script source
        html = html.replace("<head>",
                "<head><script>" + getMraidDotJS(wv.getResources())
                        + "</script>");

        return html;
    }

    protected String getMraidDotJS(Resources r) {
        InputStream ins = r.openRawResource(R.raw.mraid);
        try {
            byte[] buffer = new byte[ins.available()];
            ins.read(buffer);
            return new String(buffer, "UTF-8");
        } catch (IOException e) {
            return null;
        }
    }

    protected WebViewClient getWebViewClient() {

        return new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.startsWith("mraid:") && !url.startsWith("javascript:")) {
                    if (owner.owner.getOpensNativeBrowser()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(url));
                        owner.getContext().startActivity(intent);
                    } else {
                        Intent intent = new Intent(owner.getContext(),
                                BrowserActivity.class);
                        intent.putExtra("url", url);
                        owner.getContext().startActivity(intent);
                    }
                    return true;
                } else if (url.startsWith("mraid://")) {
                    MRAIDImplementation.this.dispatch_mraid_call(url);

                    return true;
                }

                // See if any native activities can handle the Url
                try {
                    owner.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    // If it's an IAV, prevent it from closing
                    if (owner.owner instanceof InterstitialAdView) {
                        ((InterstitialAdView) (owner.owner)).interacted();
                    }
                    return true;
                } catch (ActivityNotFoundException e) {
                    return false;
                }
            }

            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                Clog.e(Clog.httpRespLogTag,
                        Clog.getString(R.string.webclient_error,
                                error.getPrimaryError(), error.toString()));
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingURL) {
                Clog.e(Clog.httpRespLogTag, Clog.getString(
                        R.string.webclient_error, errorCode, description));
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Fire the ready event only once
                if (!readyFired) {
                    view.loadUrl("javascript:window.mraid.util.setPlacementType('"
                            + owner.owner.getMRAIDAdType() + "')");
                    view.loadUrl("javascript:window.mraid.util.setIsViewable(true)");

                    setSupportsValues(view);
                    setScreenSize(view);
                    setMaxSize(view);
                    setDefaultPosition(view);

                    view.loadUrl("javascript:window.mraid.util.stateChangeEvent('default')");
                    view.loadUrl("javascript:window.mraid.util.readyEvent();");

                    // Store width and height for close()
                    default_width = owner.getLayoutParams().width;
                    default_height = owner.getLayoutParams().height;

                    readyFired = true;
                }
            }

            private void setDefaultPosition(WebView view) {
                int[] location = new int[2];
                owner.getLocationOnScreen(location);

                int height = owner.getMeasuredHeight();
                int width = owner.getMeasuredWidth();
                view.loadUrl("javascript:window.mraid.util.setDefaultPosition(x:"+location[0]+", y:"+location[1]+", width:"+width+", height:"+height+")");
            }

            private void setMaxSize(WebView view) {
                if(owner.getContext() instanceof Activity){
                    Activity a = ((Activity) owner.getContext());
                    Display d = a.getWindowManager().getDefaultDisplay();
                    Point p = new Point();
                    d.getSize(p);
                    int width = p.x;
                    int height = p.y;

                    Rect r = new Rect();
                    a.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                    int contentViewTop = a.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
                    height-=contentViewTop;


                    view.loadUrl("javascript:window.mraid.util.setMaxSize({width: "+width+", height:"+height+")");
                }



            }

            private void setScreenSize(WebView view) {
                if(owner.getContext() instanceof Activity){
                    Display d = ((Activity) owner.getContext()).getWindowManager().getDefaultDisplay();
                    Point p = new Point();
                    d.getSize(p);
                    int width = p.x;
                    int height = p.y;

                    view.loadUrl("javascript:window.mraid.util.setScreenSize({width: "+width+", height:"+height+")");
                }
            }

            private void setSupportsValues(WebView view) {
                //SMS
                //TODO Check for permissions
                if(hasIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:5555555555")))){
                    view.loadUrl("javascript:window.mraid.util.setSupportsSMS(true)");
                }

                //Tel
                //TODO Check for permissions
                if(hasIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("tel:5555555555")))){
                    view.loadUrl("javascript:window.mraid.util.setSupportsTel(true)");
                }

                //Calendar
                //TODO Check for permissions
                Intent i;
                i = new Intent(Intent.ACTION_EDIT);
                i.setType("vnd.android.cursor.item/event");
                if(hasIntent(i)){
                    view.loadUrl("javascript:window.mraid.util.setSupportsCalendar(true)");
                }
                i=null;

                //Store Picture
                //TODO Check for permissions
                //TODO: This isn't done by an intent. Do we want to make a custom dialog box for this, or just not support it?

                //Video should always work inline.
                view.loadUrl("javascript:window.mraid.util.setSupportsInlineVideo(true)");

            }

            boolean hasIntent(Intent i){
                PackageManager pm = owner.getContext().getPackageManager();
                return pm.queryIntentActivities(i, 0).size()>0;
            }
        };
    }


    protected WebChromeClient getWebChromeClient() {
		return new MRAIDWebChromeClient((Activity) owner.getContext());
	}

	class MRAIDWebChromeClient extends VideoEnabledWebChromeClient {

        public MRAIDWebChromeClient(Activity context) {
            super(context);
        }

        @Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
			// super.onConsoleMessage(consoleMessage);
			Clog.w(Clog.mraidLogTag,
					Clog.getString(R.string.console_message,
							consoleMessage.message(),
							consoleMessage.lineNumber(),
							consoleMessage.sourceId()));
			return true;
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			// /super.onJsAlert(view, url, message, result);
			Clog.w(Clog.mraidLogTag,
					Clog.getString(R.string.js_alert, message, url));
			result.confirm();
			return true;
		}
    }

    protected void onVisible() {
        if (readyFired)
            owner.loadUrl("javascript:window.mraid.util.setIsViewable(true)");

    }

    protected void onInvisible() {
        if (readyFired)
            owner.loadUrl("javascript:window.mraid.util.setIsViewable(false)");
    }

    protected void setCurrentPosition(WebView view){ //TODO find somewhere convenient to call this
        int[] location = new int[2];
        owner.getLocationOnScreen(location);

        int height = owner.getMeasuredHeight();
        int width = owner.getMeasuredWidth();

        view.loadUrl("javascript:window.mraid.util.setCurrentPosition(x:"+location[0]+", y:"+location[1]+", width:"+width+", height:"+height+")");
    }

    protected void close() {
        if (expanded) {
            AdView.LayoutParams lp = new AdView.LayoutParams(
                    owner.getLayoutParams());
            lp.height = default_height;
            lp.width = default_width;
            lp.gravity = Gravity.CENTER;
            owner.setLayoutParams(lp);
            owner.close();
            owner.loadUrl("javascript:window.mraid.util.sizeChangeEvent(" + default_width + "," + default_height + ")");
            this.owner
                    .loadUrl("javascript:window.mraid.util.stateChangeEvent('default');");
            this.owner.owner.adListener.onAdCollapsed(this.owner.owner);

            // Allow orientation changes
            Activity a = ((Activity) this.owner.getContext());
            if (a != null)
                a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            expanded = false;
        } else {
            // state must be default
            owner.hide();
            hidden = true;
        }
    }

    protected void expand(ArrayList<BasicNameValuePair> parameters) {
        if (!hidden) {
            int width = owner.getLayoutParams().width;// Use current height and
            // width as expansion
            // defaults.
            int height = owner.getLayoutParams().height;
            boolean useCustomClose = false;
            for (BasicNameValuePair bnvp : parameters) {
                if (bnvp.getName().equals("w"))
                    try {
                        width = Integer.parseInt(bnvp.getValue());
                    } catch (NumberFormatException e) {
                        // Do nothing
                    }
                else if (bnvp.getName().equals("h"))
                    try {
                        height = Integer.parseInt(bnvp.getValue());
                    } catch (NumberFormatException e) {
                        // Do nothing
                    }
                else if (bnvp.getName().equals("useCustomClose"))
                    useCustomClose = Boolean.parseBoolean(bnvp.getValue());
            }

            owner.expand(width, height, useCustomClose, this);
            // Fire the stateChange to MRAID
            owner.loadUrl("javascript:window.mraid.util.sizeChangeEvent(" + width + "," + height + ")");
            this.owner
                    .loadUrl("javascript:window.mraid.util.stateChangeEvent('expanded');");
            expanded = true;

            // Fire the AdListener event
            if (this.owner.owner.adListener != null) {
                this.owner.owner.adListener.onAdExpanded(this.owner.owner);
            }

            // Lock the orientation
            AdActivity.lockOrientation((Activity) this.owner.getContext());

        } else {
            owner.show();
            hidden = false;
        }
    }

    protected void dispatch_mraid_call(String url) {
        // Remove the fake protocol
        url = url.replaceFirst("mraid://", "");

        // Separate the function from the parameters
        String[] qMarkSplit = url.split("\\?");
        String func = qMarkSplit[0].replaceAll("/", "");
        String params;
        ArrayList<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
        if (qMarkSplit.length > 1) {
            params = url.substring(url.indexOf("?") + 1);

            for (String s : params.split("&")) {
                if (s.split("=").length < 2) {
                    continue;
                }
                parameters.add(new BasicNameValuePair(s.split("=")[0], s
                        .split("=")[1]));
            }
        }

        if (func.equals("expand")) {
            expand(parameters);
        } else if (func.equals("close")) {
            close();
        } else if (func.equals("resize")){
            resize(parameters);
        } else if (func.equals("setOrientationProperties")){
            setOrientationProperties(parameters);
        } else if (func.equals("createCalendarEvent")){
            createCalendarEvent(parameters);
        } else if (func.equals("playVideo")){
            playVideo(parameters);
        } else if (func.equals("storePicture")){
            storePicture(parameters);
        }
    }

    private void storePicture(ArrayList<BasicNameValuePair> parameters) {
        //TODO: Make our own dialog box? shit, tom is gone
    }

    private void playVideo(ArrayList<BasicNameValuePair> parameters) {
        String uri = null;
        for (BasicNameValuePair bnvp : parameters) {
            if(bnvp.getName().equals("uri")){
                uri = bnvp.getValue();
            }
        }
        if(uri==null){
            //TODO: Clogging, error here.
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            i.setDataAndType(Uri.parse(URLDecoder.decode(uri, "UTF-8")), "video/mp4");
        } catch (UnsupportedEncodingException e) {
            //TODO: Clogging, error here.
        }
        owner.getContext().startActivity(i);
    }

    private void createCalendarEvent(ArrayList<BasicNameValuePair> parameters) {
        W3CEvent event = W3CEvent.createFromJSON(parameters.get(0).getValue());
        Intent i = event.getInsertIntent();
        owner.getContext().startActivity(i);

        //TODO: Clogging
    }



    private void setOrientationProperties(ArrayList<BasicNameValuePair> parameters) {
        boolean allow_orientation_change=true;
        AdActivity.OrientationEnum orientation=AdActivity.OrientationEnum.none;

        for (BasicNameValuePair bnvp : parameters) {
            if(bnvp.getName().equals("allow_orientation_change")){
                allow_orientation_change = Boolean.parseBoolean(bnvp.getValue());
            }else if(bnvp.getName().equals("force_orientation")){
                orientation=AdActivity.OrientationEnum.valueOf(bnvp.getValue());
            }
        }

        if(allow_orientation_change == false){
            AdActivity.setOrientation((Activity)owner.getContext(), orientation);
        }else{
            AdActivity.setOrientation((Activity)owner.getContext(), AdActivity.OrientationEnum.none);
        }

        //TODO: Clogging



    }

    private void resize(ArrayList<BasicNameValuePair> parameters) {
        int w;
        int h;
        int offset_x;
        int offset_y;
        String custom_close_position;
        boolean allow_offscrean;
        for (BasicNameValuePair bnvp : parameters) {
            if(bnvp.getName().equals("w")){
                w = Integer.parseInt(bnvp.getValue());
            }else if(bnvp.getName().equals("h")){
                h = Integer.parseInt(bnvp.getValue());
            }else if(bnvp.getName().equals("offset_x")){
                offset_x = Integer.parseInt(bnvp.getValue());
            }else if(bnvp.getName().equals("offset_y")){
                offset_y = Integer.parseInt(bnvp.getValue());
            }else if(bnvp.getName().equals("custom_close_position")){
                custom_close_position = bnvp.getValue();
            }else if(bnvp.getName().equals("allow_offscreen")){
                allow_offscrean = Boolean.parseBoolean(bnvp.getValue());
            }
        }
    }
}
