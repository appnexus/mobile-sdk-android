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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import android.view.ViewGroup;
import org.apache.http.message.BasicNameValuePair;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.provider.CalendarContract;
import android.util.Base64;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Hex;
import com.appnexus.opensdk.utils.W3CEvent;

@SuppressLint("InlinedApi")
class MRAIDImplementation {
    protected final MRAIDWebView owner;
    private boolean readyFired = false;
    boolean expanded = false;
    boolean resized = false;
    boolean hidden = false;
    int default_width, default_height;
    boolean supportsPictureAPI = false;
    boolean supportsCalendar = false;

    public MRAIDImplementation(MRAIDWebView owner) {
        this.owner = owner;
    }

    // The webview about to load the ad, and the html ad content
    String onPreLoadContent(WebView wv, String html) {
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

    String getMraidDotJS(Resources r) {
        InputStream ins = r.openRawResource(R.raw.mraid);
        try {
            byte[] buffer = new byte[ins.available()];
            if ( ins.read(buffer) > 0) {
                return new String(buffer, "UTF-8");
            }
        } catch (IOException e) {

        }
        return null;
    }

    protected void onReceivedError(WebView view, int errorCode, String desc,
                                   String failingUrl) {
        Clog.w(Clog.mraidLogTag, Clog.getString(
                R.string.webview_received_error, errorCode, desc, failingUrl));
    }

    WebViewClient getWebViewClient() {
        return new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.startsWith("mraid:") && !url.startsWith("javascript:")) {
                    Intent intent;
                    if (url.startsWith("sms:") || url.startsWith("tel:") || owner.owner.getOpensNativeBrowser()) {
                        intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(url));
                        try{
                            owner.getContext().startActivity(intent);
                            //Call onAdClicked
                            if(owner.owner.adListener!=null){
                                owner.owner.adListener.onAdClicked(owner.owner);
                            }
                        }catch (Exception e){
                            Toast.makeText(owner.getContext(),R.string.action_cant_be_completed, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        intent = new Intent(owner.getContext(),
                                BrowserActivity.class);
                        intent.putExtra("url", url);
                        try {
                            owner.getContext().startActivity(intent);
                        } catch(ActivityNotFoundException e) {
                            Clog.w(Clog.mraidLogTag, Clog.getString(R.string.opening_url_failed,url));
                        }
                        //Call onAdClicked
                        if(owner.owner.adListener!=null){
                            owner.owner.adListener.onAdClicked(owner.owner);
                        }
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
                    //Call onAdClicked
                    if(owner.owner.adListener!=null){
                        owner.owner.adListener.onAdClicked(owner.owner);
                    }
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
                    String adType = owner.owner.isBanner() ? "inline" : "interstitial";
                    view.loadUrl("javascript:window.mraid.util.setPlacementType('"
                            + adType + "')");
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
                view.loadUrl("javascript:window.mraid.util.setDefaultPosition(" + location[0] + ", " + location[1] + ", " + width + ", " + height + ")");
            }

            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            private void setMaxSize(WebView view) {
                if (owner.getContext() instanceof Activity) {
                    Activity a = ((Activity) owner.getContext());
                    Display d = a.getWindowManager().getDefaultDisplay();
                    Point p = new Point();
                    int width;
                    int height;
                    if(Build.VERSION.SDK_INT>=13){
                        d.getSize(p);
                        width = p.x;
                        height = p.y;
                    }else{
                        width = d.getWidth();
                        height = d.getHeight();
                    }

                    Rect r = new Rect();
                    a.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                    int contentViewTop = a.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
                    height -= contentViewTop;


                    view.loadUrl("javascript:window.mraid.util.setMaxSize(" + width + ", " + height + ")");
                }


            }

            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            private void setScreenSize(WebView view) {
                if (owner.getContext() instanceof Activity) {
                    Display d = ((Activity) owner.getContext()).getWindowManager().getDefaultDisplay();
                    Point p = new Point();
                    int width;
                    int height;
                    if(Build.VERSION.SDK_INT>=13){
                        d.getSize(p);
                        width = p.x;
                        height = p.y;
                    }else{
                        width = d.getWidth();
                        height = d.getHeight();
                    }

                    view.loadUrl("javascript:window.mraid.util.setScreenSize("+width + ", " + height + ")");
                }
            }

            @SuppressLint("NewApi")
            private void setSupportsValues(WebView view) {
                //SMS
                if (hasIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:5555555555")))) {
                    view.loadUrl("javascript:window.mraid.util.setSupportsSMS(true)");
                }

                //Tel
                if (hasIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("tel:5555555555")))) {
                    view.loadUrl("javascript:window.mraid.util.setSupportsTel(true)");
                }

                //Calendar
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                        && hasIntent(new Intent(Intent.ACTION_EDIT).setData(CalendarContract.Events.CONTENT_URI))) {
                    view.loadUrl("javascript:window.mraid.util.setSupportsCalendar(true)");
                    supportsCalendar = true;
                }else if(hasIntent(new Intent(Intent.ACTION_EDIT).setType("vnd.android.cursor.item/event"))){
                    view.loadUrl("javascript:window.mraid.util.setSupportsCalendar(true)");
                    supportsCalendar = true;
                    W3CEvent.useMIME = true;
                }

                //Store Picture only if on API 11 or above
                PackageManager pm = owner.getContext().getPackageManager();
                if(pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, owner.getContext().getPackageName()) == PackageManager.PERMISSION_GRANTED){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        view.loadUrl("javascript:window.mraid.util.setSupportsStorePicture(true)");
                        supportsPictureAPI = true;
                    }
                }

                //Video should always work inline.
                view.loadUrl("javascript:window.mraid.util.setSupportsInlineVideo(true)");

            }

            boolean hasIntent(Intent i) {
                PackageManager pm = owner.getContext().getPackageManager();
                return pm.queryIntentActivities(i, 0).size() > 0;
            }
        };
    }


	WebChromeClient getWebChromeClient() {
		return new VideoEnabledWebChromeClient((Activity) owner.getContext()){
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

        };
	}
    void onVisible() {
        if (readyFired)
            owner.loadUrl("javascript:window.mraid.util.setIsViewable(true)");
    }

    void onInvisible() {
        if (readyFired)
            owner.loadUrl("javascript:window.mraid.util.setIsViewable(false)");
    }

    protected void setCurrentPosition(int left, int top, int right, int bottom, WebView view) {
        int height = right-left;
        int width = bottom-top;

        owner.loadUrl("javascript:window.mraid.util.sizeChangeEvent(" + width + "," + height + ")");
        owner.loadUrl("javascript:window.mraid.util.setCurrentPosition(" + left + ", " + top + ", " + width + ", " + height + ")");
    }

    void close() {
        if (expanded || resized) {
            AdView.LayoutParams lp = new AdView.LayoutParams(
                    owner.getLayoutParams());
            lp.height = default_height;
            lp.width = default_width;
            lp.gravity = Gravity.CENTER;
            owner.setLayoutParams(lp);
            owner.close();
            owner.loadUrl("javascript:window.mraid.util.stateChangeEvent('default');");
            if (owner.owner!=null && owner.owner.adListener!=null) {
                owner.owner.adListener.onAdCollapsed(this.owner.owner);
            }

            // Allow orientation changes
            Activity a = ((Activity) this.owner.getContext());
            if (a != null)
                a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            expanded = false;
            resized = false;
        } else {
            // state must be default
            owner.hide();
            hidden = true;
        }
    }

    void expand(ArrayList<BasicNameValuePair> parameters) {
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

    void dispatch_mraid_call(String url) {
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
        } else if (func.equals("resize")) {
            resize(parameters);
        } else if (func.equals("setOrientationProperties")) {
            setOrientationProperties(parameters);
        } else if (supportsCalendar && func.equals("createCalendarEvent")) {
            createCalendarEvent(parameters);
        } else if (func.equals("playVideo")) {
            playVideo(parameters);
        } else if (supportsPictureAPI && func.equals("storePicture")) {
            storePicture(parameters);
        } else {
            Clog.d(Clog.mraidLogTag, Clog.getString(R.string.unsupported_mraid, func));

        }
    }

    private void storePicture(ArrayList<BasicNameValuePair> parameters) {
        String uri = null;
        for (BasicNameValuePair bnvp : parameters) {
            if (bnvp.getName().equals("uri")) {
                uri = bnvp.getValue();
            }
        }
        if (uri == null) {
            Clog.d(Clog.mraidLogTag, Clog.getString(R.string.store_picture_error));
            return;
        }

        final String uri_final = Uri.decode(uri);

        AlertDialog.Builder builder = new AlertDialog.Builder(owner.owner.getContext());
        builder.setTitle(R.string.store_picture_title);
        builder.setMessage(R.string.store_picture_message);
        builder.setPositiveButton(R.string.store_picture_accept, new DialogInterface.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Check URI scheme
                if (uri_final.startsWith("data:")) {
                    //Remove 'data:(//?)' and save
                    String ext = ".png";
                    boolean isBase64 = false;
                    //First, find file type:
                    if (uri_final.contains("image/gif")) {
                        ext = ".gif";
                    } else if (uri_final.contains("image/jpeg") || uri_final.contains("image/pjpeg")) {
                        ext = ".jpg";
                    } else if (uri_final.contains("image/png")) {
                        ext = ".png";
                    } else if (uri_final.contains("image/tiff")) {
                        ext = ".tif";
                    } else if (uri_final.contains("image/svg+xml")) {
                        ext = ".svg";
                    }
                    if (uri_final.contains("base64")) {
                        isBase64 = true;
                    }
                    File out = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ext);
                    FileOutputStream outstream=null;
                    try {
                        byte[] out_array;
                        outstream = new FileOutputStream(out);
                        if(out.canWrite()){
                            if (!isBase64) {
                                out_array = Hex.hexStringToByteArray(uri_final.substring(uri_final.lastIndexOf(",") + 1, uri_final.length()));
                            }else{
                                out_array = Base64.decode(uri_final.substring(uri_final.lastIndexOf(",") + 1, uri_final.length()), Base64.DEFAULT);
                            }

                            outstream.write(out_array);
                        }
                    } catch (FileNotFoundException e) {
                        Clog.d(Clog.mraidLogTag, Clog.getString(R.string.store_picture_error));
                    } catch (IOException e) {
                        Clog.d(Clog.mraidLogTag, Clog.getString(R.string.store_picture_error));
                    } catch (IllegalArgumentException e) {
                        Clog.d(Clog.mraidLogTag, Clog.getString(R.string.store_picture_error));
                    }
                    finally{
                        if(outstream!=null){
                            try {
                                outstream.close();
                            } catch (IOException e) {
                                Clog.d(Clog.mraidLogTag, Clog.getString(R.string.store_picture_error));
                            }
                        }
                    }

                } else {
                    //Use the download manager
                    final DownloadManager dm = (DownloadManager) owner.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request r = new DownloadManager.Request(Uri.parse(uri_final));

                    //Check if we're writing to internal or external
                    PackageManager pm = owner.getContext().getPackageManager();
                    if(pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, owner.getContext().getPackageName()) == PackageManager.PERMISSION_GRANTED){
                        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, uri_final.split("/")[uri_final.split("/").length-1]);
                        try {
                            r.allowScanningByMediaScanner();
                            r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            dm.enqueue(r);
                        } catch (IllegalStateException ex) {
                            Clog.d(Clog.mraidLogTag, Clog.getString(R.string.store_picture_error));
                        }
                    }else{
                        Clog.d(Clog.mraidLogTag, Clog.getString(R.string.store_picture_error));
                    }
                }
            }
        });

        builder.setNegativeButton(R.string.store_picture_decline, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Nothing needs to be done
            }
        });

        AlertDialog d = builder.create();
        d.show();


    }

    private void playVideo(ArrayList<BasicNameValuePair> parameters) {
        String uri = null;
        for (BasicNameValuePair bnvp : parameters) {
            if (bnvp.getName().equals("uri")) {
                uri = bnvp.getValue();
            }
        }
        if (uri == null) {
            Clog.d(Clog.mraidLogTag, Clog.getString(R.string.play_vide_no_uri));
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            i.setDataAndType(Uri.parse(URLDecoder.decode(uri, "UTF-8")), "video/mp4");
        } catch (UnsupportedEncodingException e) {
            Clog.d(Clog.mraidLogTag, Clog.getString(R.string.unsupported_encoding));
            return;
        }
        try{
            owner.getContext().startActivity(i);
            //Call onAdClicked
            if(owner.owner.adListener!=null){
                owner.owner.adListener.onAdClicked(owner.owner);
            }
        }catch(ActivityNotFoundException e){
            return;
        }
    }

    private void createCalendarEvent(ArrayList<BasicNameValuePair> parameters) {
        W3CEvent event = null;
        try {
            if(parameters != null && parameters.size()>0){
                event = W3CEvent.createFromJSON(URLDecoder.decode(parameters.get(0).getValue(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            return;
        }


        if (event != null) {
            try {
                Intent i = event.getInsertIntent();
                owner.getContext().startActivity(i);
                // Call onAdClicked
                if (owner.owner.adListener != null) {
                    owner.owner.adListener.onAdClicked(owner.owner);
                }
                Clog.d(Clog.mraidLogTag, Clog.getString(R.string.create_calendar_event));
            } catch (ActivityNotFoundException e) {

            }
        }

    }


    private void setOrientationProperties(ArrayList<BasicNameValuePair> parameters) {
        boolean allow_orientation_change = true;
        AdActivity.OrientationEnum orientation = AdActivity.OrientationEnum.none;

        for (BasicNameValuePair bnvp : parameters) {
            if (bnvp.getName().equals("allow_orientation_change")) {
                allow_orientation_change = Boolean.parseBoolean(bnvp.getValue());
            } else if (bnvp.getName().equals("force_orientation")) {
                String val = bnvp.getValue();
                if(val.equals("landscape")){
                    orientation = AdActivity.OrientationEnum.landscape;
                }else if(val.equals("portrait")){
                    orientation = AdActivity.OrientationEnum.portrait;
                }else{
                    orientation = AdActivity.OrientationEnum.none;
                }
            }
        }

        if (!allow_orientation_change) {
            AdActivity.setOrientation((Activity) owner.getContext(), AdActivity.OrientationEnum.none);
            AdActivity.setOrientation((Activity) owner.getContext(), orientation);
        } else {
            AdActivity.setOrientation((Activity) owner.getContext(), AdActivity.OrientationEnum.none);
        }

        Clog.d(Clog.mraidLogTag, Clog.getString(R.string.set_orientation_properties, allow_orientation_change, orientation.ordinal()));


    }

    public enum CUSTOM_CLOSE_POSITION {
        top_left,
        top_right,
        center,
        bottom_left,
        bottom_right,
        top_center,
        bottom_center
    }

    private void resize(ArrayList<BasicNameValuePair> parameters) {
        int w = -1;
        int h = -1;
        int offset_x = 0;
        int offset_y = 0;
        String custom_close_position = "top-right";
        boolean allow_offscrean = true;
        for (BasicNameValuePair bnvp : parameters) {
            try{
                if (bnvp.getName().equals("w")) {
                    w = Integer.parseInt(bnvp.getValue());
                } else if (bnvp.getName().equals("h")) {
                    h = Integer.parseInt(bnvp.getValue());
                } else if (bnvp.getName().equals("offset_x")) {
                    offset_x = Integer.parseInt(bnvp.getValue());
                } else if (bnvp.getName().equals("offset_y")) {
                    offset_y = Integer.parseInt(bnvp.getValue());
                } else if (bnvp.getName().equals("custom_close_position")) {
                    custom_close_position = bnvp.getValue();
                } else if (bnvp.getName().equals("allow_offscreen")) {
                    allow_offscrean = Boolean.parseBoolean(bnvp.getValue());
                }
            }catch(NumberFormatException e){
                Clog.d(Clog.mraidLogTag, Clog.getString(R.string.number_format));
                return;
            }
        }

        Clog.d(Clog.mraidLogTag, Clog.getString(R.string.resize, w, h, offset_x, offset_y, custom_close_position, allow_offscrean));
        this.owner.resize(w, h, offset_x, offset_y, CUSTOM_CLOSE_POSITION.valueOf(custom_close_position.replace('-', '_')), allow_offscrean);

        //Call onAdClicked
        if(owner.owner.adListener!=null){
            owner.owner.adListener.onAdExpanded(owner.owner);
        }

        this.owner
                .loadUrl("javascript:window.mraid.util.stateChangeEvent('resized');");
        resized = true;

    }
}
