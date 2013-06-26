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
package com.appnexus.picflip.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;

import static android.R.style.Theme_Black_NoTitleBar;
import static com.appnexus.picflip.util.LogUtils.makeLogTag;
import static com.appnexus.picflip.util.LogUtils.LOGD;


/* AppNexus imports */
import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.InterstitialAdView;

/* Google AdMob imports */
import com.google.ads.*;

/* Local imports */
import com.appnexus.picflip.R;
import com.appnexus.picflip.model.ImageCache;
import com.appnexus.picflip.tasks.FlickrImageTask;
import com.appnexus.picflip.util.Constants;

import java.lang.ref.WeakReference;

/**
 * The PicFlic Main activity
 * The PicFlip application is a sample application showing integration of the AppNexus Advertising SDK
 * and how to mediate the AppNexus SDK with Google's AdMob
 *
 * The PicFlip application is a simple picture viewer that will display sixteen random images
 from a random public domain Flickr group each time it is rotated. The images are picked
 from ten hard-coded public domain image groups on the Flickr site. The sixteen images
 are retrieved from the first page that is returned from the selected group. Clicking on a
 thumbnail image will launch a full-screen display of that image. An interstitial ad will be
 displayed every second time the user clicks on a thumbnail image after a rotation
 The app goes through nine basic states:
 1- Loading State
 2- Display State (AdMob Direct)
 3- Display State (OpenSDK Direct)
 4- Display State (OpenSDK Mediated)
 5- AdMob Direct Interstitial
 6- OpenSDK Direct Interstitial
 7- OpenSDK Mediated Interstitial
 8- OpenSDK Direct Interstitial in Internal WebView
 9- OpenSDK Direct Interstitial in external browser

 Note:
 You must set the ADMOB* and APPNEXUS* variables below to your placement to see ad's served.

 */
public class MainActivity extends Activity implements AdListener, com.google.ads.AdListener {

    private static final String TAG = makeLogTag(MainActivity.class);

    /* Ad Base and Mediation ID's */
    private AdView adMobAdView;
    private String ADMOB_AD_UNIT_ID = ""; /* This apps ID */
    private String ADMOB_INTERSTITIAL_UNIT_ID = "";

    /* AppNexus dirct placement ID */
    private String APPNEXUS_PLACEMENT_ID = "";

    /* Mediation Id's for AdMob mediation. These are passed to Google AdMob to route the
    * placement request to AppNexus OpenSDK*/
    private String ADMOB_BANNER_MEDIATION_ID = "";
    private String ADMOB_INTERSTITIAL_MEDIATION_ID = "";

    /* Mediation Id's for AppNexus Mediation. These are passed to AppNexus OpenSDK to route the
    * placement request to Google AdMob*/
    private String APPNEXUS_BANNER_MEDIATION_ID = "";

    private static final int REQUEST_PREFERENCE_DIALOG = 128;

    /* Grid View settings */
    private int ivWidth;
    private int imgPaddingX;
    private int imgPaddingY;
    private int ivHeight;

    /* Image cache */
    private ImageCache imgCacheOne;
    private ImageCache imgCacheTwo;
    private ImageCache curImgCacheInUse;
    private ImageCache bakImgCacheInUse;

    protected Dialog mSplashDialog;

    /* Widgets */
    private GridView gridView = null;
    private ImageAdapter gridViewAdapter;
    private TextView mCurrentGroupTV;
    private TextView mCurrentAdTypeTV;

    /**
     * Settings variables **
     */
    private boolean mShowGroupOverlay;
    private int mAdService;
    private boolean mShowSplash = true;
    private boolean mUseExternBrowserForInterstitial;
    private FlickrImageTask mFlickrImageTask = null;

    private String mAdServiceTypeId;
    private Typeface mOverlayTypeFace;
    private int mOverlayTextSize;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("IMAGE_CACHE_1", imgCacheOne);
        outState.putParcelable("IMAGE_CACHE_2", imgCacheTwo);
        outState.putParcelable("CUR_IMAGE_CACHE", curImgCacheInUse);
        outState.putBoolean("SHOW_SPLASH", mShowSplash);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        updatePreferences(sp);


        if (savedInstanceState == null) {
            imgCacheOne = new ImageCache();
            imgCacheTwo = new ImageCache();
            curImgCacheInUse = null;
            mShowSplash = true;
        } else {
            imgCacheOne = savedInstanceState.getParcelable("IMAGE_CACHE_1");
            imgCacheTwo = savedInstanceState.getParcelable("IMAGE_CACHE_2");
            curImgCacheInUse = savedInstanceState.getParcelable("CUR_IMAGE_CACHE");
            mShowSplash = savedInstanceState.getBoolean("SHOW_SPLASH");
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View emptyView = inflater.inflate(R.layout.empty_waiting_for_sync,
                (ViewGroup) findViewById(android.R.id.empty), true);

        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.pictureGridView);
        gridViewAdapter = new ImageAdapter(this);
        gridView.setAdapter(gridViewAdapter);
        gridView.setEmptyView(emptyView);
        gridView.setOnItemClickListener(mImageOnGridItemClickListener);

        setDisplayDensityVariables();

        setShowOverlay(mShowGroupOverlay);

        gridViewAdapter.notifyDataSetChanged();
        gridView.invalidateViews();

        if (mShowSplash)
            showSplash();

        /* Set the mediation id or direct placement id */
        setupAdType();

        if (curImgCacheInUse == null) {
            curImgCacheInUse = imgCacheOne;
            polllFlickr(imgCacheOne);
            bakImgCacheInUse = imgCacheTwo;
        } else if (curImgCacheInUse == imgCacheOne) {
            curImgCacheInUse = imgCacheTwo;
            bakImgCacheInUse = imgCacheOne;
            polllFlickr(imgCacheOne);
        } else {
            curImgCacheInUse = imgCacheOne;
            bakImgCacheInUse = imgCacheTwo;
            polllFlickr(imgCacheTwo);
        }

        mCurrentGroupTV.setText(curImgCacheInUse.getGroupName());

        LOGD(TAG, String.format("onCreate - group: %s ImgCache: %s", curImgCacheInUse.getGroupName(), curImgCacheInUse.toString()));
    }

    private void setDisplayDensityVariables() {


        DisplayMetrics mDispMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDispMetrics);

        int dispWidth = mDispMetrics.widthPixels;
        int dispHeight = mDispMetrics.heightPixels;
        int density = mDispMetrics.densityDpi;

        ivWidth = dispWidth / 4;
        ivHeight = dispHeight / 5;


        if (dispWidth > dispHeight) {
            if (dispWidth > 1000) {
                mOverlayTextSize = 30;
                imgPaddingX = 15;
                imgPaddingY = 2;
            } else if (dispWidth > 700) {
                mOverlayTextSize = 25;
                imgPaddingX = 10;
                imgPaddingY = 1;
            } else if (dispWidth > 400) {
                mOverlayTextSize = 20;
                imgPaddingX = 2;
                imgPaddingY = 1;
            } else {
                mOverlayTextSize = 15;
                imgPaddingX = 1;
                imgPaddingY = 1;
            }

        } else {
            if (dispHeight > 1000) {
                mOverlayTextSize = 30;
                imgPaddingY = imgPaddingX = 5;
            } else if (dispHeight > 700) {
                mOverlayTextSize = 25;
                imgPaddingY = imgPaddingX = 3;
            } else if (dispHeight > 400) {
                mOverlayTextSize = 20;
                imgPaddingY = imgPaddingX = 2;
            } else {
                mOverlayTextSize = 15;
                imgPaddingY = imgPaddingX = 1;
            }

        }

        mOverlayTypeFace = Typeface.createFromAsset(getAssets(),
                "fonts/action_man.ttf");

        mCurrentGroupTV = (TextView) findViewById(R.id.textViewCurrentGroup);

        if (mCurrentGroupTV != null) {
            mCurrentGroupTV.setTypeface(mOverlayTypeFace);
            mCurrentGroupTV.setTextSize(mOverlayTextSize);
            mCurrentGroupTV.setTextColor(Color.WHITE);
        }

        mCurrentAdTypeTV = (TextView) findViewById(R.id.textViewAdType);

        if (mCurrentAdTypeTV != null) {
            mCurrentAdTypeTV.setTypeface(mOverlayTypeFace);
            mCurrentAdTypeTV.setTextSize(mOverlayTextSize);
            mCurrentAdTypeTV.setTextColor(Color.WHITE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void polllFlickr(final ImageCache curImgCache) {

        runOnUiThread(new Runnable() {
            public void run() {
                getFlickrImages(curImgCache);
            }
        });
    }


    public int getFlickrImages(ImageCache curImgCache) {
        int retval = 1;

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            mFlickrImageTask = new FlickrImageTask();

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                mFlickrImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this, mHandler, curImgCache);
            else
                mFlickrImageTask.execute(this, mHandler, curImgCache);

        } else {
            retval = -1;
        }
        return retval;
    }

    private final Handler mHandler = new AppHandler(this);

    private static class AppHandler extends Handler {

        private final WeakReference<MainActivity> mAct;

        public AppHandler(MainActivity act) {

            mAct = new WeakReference<MainActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            final MainActivity act = mAct.get();

            if (act != null) {
                switch (msg.what) {

                    case Constants.MSG_REFRESH_GRID_VIEW:

                        if (act.mCurrentGroupTV.length() == 0) {
                            act.mCurrentGroupTV.setText(act.curImgCacheInUse.getGroupName());
                        }

                        LOGD(TAG, String.format("handleMessage - Current - group: %s ImgCache: %s", act.curImgCacheInUse.getGroupName(), act.curImgCacheInUse.toString()));
                        act.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                Toast t = Toast.makeText(act.getApplicationContext(),
                                        "Image Cache Refresh", Toast.LENGTH_SHORT);
                                t.show();

                                if (act.mShowSplash) {
                                    act.removeSplashScreen();
                                    act.mShowSplash = false;
                                }
                            }
                        });

                        if (act.imgCacheTwo != null && act.imgCacheTwo.isEmpty()) {
                            act.polllFlickr(act.imgCacheTwo);
                        }

                        act.gridViewAdapter.notifyDataSetChanged();
                        act.gridView.invalidateViews();

                        break;
                }
            }
        }
    }

    private void setShowOverlay(boolean flag) {
        if (flag) {
            mCurrentGroupTV.setVisibility(View.VISIBLE);
            mCurrentAdTypeTV.setVisibility(View.VISIBLE);
        } else {
            mCurrentGroupTV.setVisibility(View.INVISIBLE);
            mCurrentAdTypeTV.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Setup the Ad Type. This can be either one of:
     * 1- Dirct to AdMob
     * 2- Direct to AppNexus
     * 3- Admob Mediation to AppNexus
     */
    private void setupAdType() {

        switch (mAdService) {
            case Constants.AD_DIRECT_ADMOB:
                mAdServiceTypeId = ADMOB_AD_UNIT_ID;
                setAdTypeOverlayText("AdMob Direct");
                setupAdMob();
                break;

            case Constants.AD_DIRECT_APPNEXUS:
                mAdServiceTypeId = APPNEXUS_PLACEMENT_ID;
                setAdTypeOverlayText("AppNexus Direct");
                setupDirectAppNexusBanner();
                break;

            case Constants.AD_MEDIATOR_ADMOB:
                mAdServiceTypeId = ADMOB_BANNER_MEDIATION_ID;
                setAdTypeOverlayText("AdMob Mediation");
                setupAdMob();
                break;

            default:
                mAdServiceTypeId = APPNEXUS_BANNER_MEDIATION_ID;
                break;
        }
    }

    private void setAdTypeOverlayText(String s) {
        if (mCurrentAdTypeTV != null) {
            mCurrentAdTypeTV.setText(s);
        }
    }

    /**
     * Set up AdMob service in either direct or mediation mode depending
     * on the AdServiceType that was configured.
     */
    private void setupAdMob() {
        // Create the Google AdMob adMobAdView
        adMobAdView = new AdView(this, AdSize.BANNER, mAdServiceTypeId);

        // Lookup your LinearLayout assuming it's been given
        // the attribute android:id="@+id/mainLayout"
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearlayoutAddMob);

        // Add the adMobAdView to it
        layout.addView(adMobAdView);

        // Initiate a generic request to load it with an ad
        AdRequest adRequest = new AdRequest();
//        adRequest.addTestDevice(AdRequest.TEST_EMULATOR);               // Emulator
//        adRequest.addTestDevice("567614884BD656A57C36D7199FC91787");    // Test Android Device
        adMobAdView.loadAd(adRequest);
    }

    /**
     * Setup direct AppNexus service
     */
    private void setupDirectAppNexusBanner() {
        com.appnexus.opensdk.AdView appNexusBannerAdView = new com.appnexus.opensdk.BannerAdView(this);
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearlayoutAddMob);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
        appNexusBannerAdView.setAdHeight(50);
        appNexusBannerAdView.setAdWidth(320);
        appNexusBannerAdView.setLayoutParams(lp);
        appNexusBannerAdView.setPlacementID(mAdServiceTypeId);
        layout.addView(appNexusBannerAdView);
        appNexusBannerAdView.loadAd();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent msi = new Intent(this, PrefActivity.class);
                startActivityForResult(msi, REQUEST_PREFERENCE_DIALOG);
                return true;

            case R.id.menu_about:
                onAbout(this);
                return true;
        }
        return false;
    }

    /**
     * Display the usual about dialog
     *
     * @param mainActivity
     */
    private void onAbout(MainActivity mainActivity) {

        PackageManager pm = getPackageManager();
        String packageName = getPackageName();
        String versionName;
        String VERSION_UNAVAILABLE = "N/A";
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = VERSION_UNAVAILABLE;
        }

        SpannableStringBuilder aboutBody = new SpannableStringBuilder();

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.about, null);
        aboutBody.append(Html.fromHtml(getString(R.string.copy_right,
                versionName)));
        TextView tv = (TextView) v.findViewById(R.id.copy_right_text);
        tv.setText(aboutBody);
        Linkify.addLinks(tv, Linkify.ALL);

        builder.setView(v).setPositiveButton(R.string.alert_ok_button, null)
                .setCancelable(false).setView(v).show();

        // Make the textview clickable. Must be called after show()
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Do any required actions after a launched activity has completed.
     *
     * @param req
     * @param result
     * @param data
     */
    @Override
    protected void onActivityResult(int req, int result, Intent data) {
        switch (req) {
            case REQUEST_PREFERENCE_DIALOG:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                updatePreferences(sp);
                break;
            default:
                super.onActivityResult(req, result, data);
                break;
        }
    }

    /**
     * Update the preferences. This is called anytime the user makes a change.
     *
     * @param sp
     */
    protected void updatePreferences(SharedPreferences sp) {
        String tmpStr;

        try {
            tmpStr = sp.getString("mediation_server", "");
            mAdService = Integer.parseInt(tmpStr);
            mShowGroupOverlay = sp.getBoolean("show_overlay", false);
            mUseExternBrowserForInterstitial = sp.getBoolean("interstitial_use_extern", false);

            if (gridView != null) {
                setShowOverlay(mShowGroupOverlay);

                gridViewAdapter.notifyDataSetChanged();
                gridView.invalidateViews();
            }

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    private int mIntersitialCount = 0;

    // AppNexus
    private InterstitialAdView iav;

    // AdMob
    private InterstitialAd adMobIav;
    private AdRequest adRequest;

    /**
     * OnClickListener that will bring up an Interstitial Ad on every other click on an image.
     * The Ad will be shown in either an external browser or an internal web view, depending on the
     * preferencee the user has set.
     */
    private GridView.OnItemClickListener mImageOnGridItemClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            LOGD(TAG, String.format("Grid Pos: %d ID: %d ImgID: %s", position, id, curImgCacheInUse.getId(position)));

            if ((++mIntersitialCount % 2) == 0) {
                Activity ctx = (Activity) view.getContext();

                switch (mAdService) {
                    case Constants.AD_DIRECT_ADMOB:
                        adMobIav = new InterstitialAd(ctx, ADMOB_INTERSTITIAL_UNIT_ID);
                        adRequest = new AdRequest();
                        adMobIav.loadAd(adRequest);
                        adMobIav.setAdListener((com.google.ads.AdListener) ctx);
                        break;

                    case Constants.AD_MEDIATOR_ADMOB:
                        adMobIav = new InterstitialAd(ctx, ADMOB_INTERSTITIAL_MEDIATION_ID);
                        adRequest = new AdRequest();
                        adMobIav.loadAd(adRequest);
                        adMobIav.setAdListener((com.google.ads.AdListener) ctx);
                        break;

                    case Constants.AD_DIRECT_APPNEXUS: // direct AppNexus Interstitial
                        iav = new InterstitialAdView(ctx);
                        iav.setAdListener((AdListener) ctx);
                        iav.setPlacementID(mAdServiceTypeId);
                        iav.setOpensNativeBrowser(mUseExternBrowserForInterstitial);
                        iav.loadAd();
                        break;

                }

            } else {
                Intent intent = new Intent(getApplicationContext(), ImageViewActivity.class);
                intent.putExtra("Bitmap", curImgCacheInUse.getImage(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        }
    };

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public int getCount() {
            return ImageCache.DEF_IMAGE_CACHE_SIZE;
        }

        public Object getItem(int position) {
            return curImgCacheInUse.getImage(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(ivWidth, ivHeight));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setPadding(imgPaddingX, imgPaddingY, imgPaddingX, imgPaddingY);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageBitmap(curImgCacheInUse.getImage(position));
            return imageView;
        }

    }

    protected void removeSplashScreen() {
        if (mSplashDialog != null) {
            mSplashDialog.dismiss();
            mSplashDialog = null;
        }
    }

    /**
     * Shows the splash screen over the full Activity
     */
    protected void showSplash() {
        mSplashDialog = new Dialog(this, Theme_Black_NoTitleBar); //R.style.SplashScreen);
        mSplashDialog.setContentView(R.layout.splash_layout);
        mSplashDialog.setCancelable(false);
        mSplashDialog.show();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                removeSplashScreen();
            }
        }, 30 * 1000);
    }

    /**
     * ******************************************
     * Lifecycle callbacks
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (mFlickrImageTask != null)
            mFlickrImageTask.cancel(true);
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (mFlickrImageTask != null)
            mFlickrImageTask.cancel(true);

        mHandler.removeMessages(Constants.MSG_REFRESH_GRID_VIEW);
        super.onDestroy();
    }

    /**
     * *************************************************************************
     * AppNexus OpenSDK Callbacks for Interstitial Ad's
     */
    @Override
    public void onAdLoaded(com.appnexus.opensdk.AdView adView) {
        Toast t = Toast.makeText(this,
                "AppNexus Ad Loaded", Toast.LENGTH_SHORT);
        t.show();

        iav.show();
    }

    @Override
    public void onAdRequestFailed(com.appnexus.opensdk.AdView adView) {
        Toast t = Toast.makeText(this,
                "AppNexus Ad Failed to Load", Toast.LENGTH_SHORT);
        t.show();
    }


    /**
     * ****************************************************************************
     * Google AdMod Callbacks for Interstitial Ad's
     */

    @Override
    public void onReceiveAd(Ad ad) {

        Toast t = Toast.makeText(this,
                "AdMob Received Ad", Toast.LENGTH_SHORT);
        t.show();

        if (ad == adMobIav) {
            adMobIav.show();
        }
    }

    @Override
    public void onFailedToReceiveAd(Ad ad, AdRequest.ErrorCode errorCode) {

        Toast t = Toast.makeText(this,
                "AdMob Failed to Received Ad", Toast.LENGTH_SHORT);
        t.show();
    }

    @Override
    public void onPresentScreen(Ad ad) {

    }

    @Override
    public void onDismissScreen(Ad ad) {

    }

    @Override
    public void onLeaveApplication(Ad ad) {

    }
}
