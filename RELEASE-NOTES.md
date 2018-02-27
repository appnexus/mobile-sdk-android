## 4.6
+ MS-3024 -- MultiFormat Banner + Video.
+ MS-3096 -- getCreativeId() new API for BannerAdView, InterstitialAdView, VideoAd and NativeAdResponse
+ MS-3179 -- Fixed NullPointerException in RequestManager

## 4.5
### Updates
+ MS-3087 -- Pre-Roll Placement Video Completion Tracker Not Firing Consistently
+ MS-3172 -- Typecasting issue in getAdPlayElapsedTime()

## 4.4
### Updates
+ MS-3097 -- Instream Video new API's to provide info on the video creatives loaded & playing

## 4.3
+ MS-3081 Fixed memory leak issue with Native SDK and Mediation Adapters
+ MS-3089 Fixed NullPointerException in continueWaterfall 
+ MS-3090 Fixed Millenial Media SDK crashes due to receiving callbacks in background thread
+ Update Facebook SDK to v4.26.1.

## 4.2
+ MS-3079 Milennial Media SDK causing app crashes in Android
+ MS-2862 Support newly added standard fields on the native creative object


## 4.2
+ MS-3079 Milennial Media SDK causing app crashes in Android
+ MS-2862 Support newly added standard fields on the native creative object

## 4.1
+ MS-3070 Upgraded AdMob and fixed Memory leak issue with ad expiry handler in admob mediation
+ Updated Rubicon to 6.3.2
+ Updated Facebook to 4.26.0

## 4.0
+ MS-2964 migrate from /mob to /ut/v2 endpoint in SDK 
+ MS-2898 Request with size bigger than the available space should be possible

## 3.5
+ Update AdColony SDK to v3.1.2
+ MS-3018 setResizeAdToFitContainer fails when BannerAdView width/height is 0

## 3.4
+ Update Facebook SDK to v4.24.0.

## 3.3
+ Support for VPAID.
+ MS-2986  Fix MRAID case where request for portrait orientation resulted in reverse portrait.
+ MS-3012  Crash in one-off cases of Android v4.3 where evaluateJavaScript() is not properly supported.
+ MS-3013  Crash where internal utility failed to return proper result.

## 3.2
+ MS-2999 Renamed mraid.js to avoid conflicts with other SDK's in the app.
+ MS-3011 Fixed Fatal crash IllegalArgumentException in android v4.x
+ Added debug options for Instream SDK
+ Updated Mediated SDKs
Rubicon SDK updated to 6.3.0

## 3.1
+ MS-2853 Updated ANJAM to update position on the fly
+ MS-2957 Fixed a crash with setOrientationProperties in MRAID
+ Fixed ut/v2 custom keywords
+ Updated Mediated SDKs
Facebook SDK update to 4.20.0

## 3.0
+ MS-2826 Mobile Instream SDK
+ MS-2914 Removed provider information from Admarvel mediation adapter AndroidManifest.xml
+ Updated Mediated SDKs
InMobi SDK update to 6.1.0

## 2.16
+ MS-2839 Fixed Anjam and sdk communication for creative served in iframe
+ MS-2841 Add a "ping" feature to the SDK without anjam.js injection
+ MS-2840 New mediation adapter, SmartAdServer Banner and Interstitial for Android
+ MS-2852 Added support for placements that accept both native and banner creatives 1x1 for Native.
+ Updated Mediated SDKs
MillennialMedia SDK update to 6.3.1
GooglePlay SDK update to 10.0.1

## RC2.15.1
+ MS-2789 Removed registerActivityLifecycleCallbacks from Google Banner Mediation.
+ MS-2794 Removed deprecated methods setAdHeight and setAdWidth from BannerAdView
+ MS-2819 Support multiple sizes on BannerAdView. New API introduced setAdSizes(ArrayList<AdSize> adSizes).
+ MS-2831 Fixed SDK crashes.

## RC2.15
+ MS-2581 Added HTTPS support for SDK. New API introduced SDKSettings.useHttps(true).
+ MS-2708 Fixed bugs in MutableContextWrapper implementation.
+ MS-2710 Removed dependency on Legacy Apache library and moved to java.net.
+ MS-2730 Fixed context leak in loadAdOffscreen implementation.
+ Updated Mediated SDKs
    AdColony SDK update to 2.3.6
    Amazon SDK update to 5.8.1
    Chartboost SDK update to 6.5.1
    FaceBook SDK update to 4.15.0
    GooglePlay SDK update to 9.4.0
    InMobi SDK update to 5.3.1
    MillennialMedia SDK update to 6.3.0
    MoPub SDK update to 4.8.0
    Vungle SDK update to 4.0.2
    Yahoo Flurry SDK update to 6.4.2

## RC2.14
+ MS-2631 Support for new AdSizes in FaceBook banner mediation
+ MS-2586 WebView security improved.
+ MS-2539 Added onAdCollapse event for Ad dismiss using back button.
+ MS-2125 MoPub Native mediation has now been fixed.
+ MS-2017 ANJAM JS improved postMessage handling.
+ MS-2011 AdWebView context updated with MutableContextWrapper.
+ MS-1890 MRAID JS improved event firing.
+ Updated Mediated SDKs
    FaceBook SDK update to 4.14.1



## RC2.13
+ MS-2332 Fixed interstitial FullScreen videoplayer issue
+ MS-2383 MRAID CustomClose 
+ MS-2324 Prepend viewport to HTML ads 
+ MS-2382 MRAID number validation changes.
+ Thirdparty cookies are now enabled on AdWebview.
+ Updated Mediated SDKs
    FaceBook SDK update to 4.12.1
    

## RC2.12
+ MS-2319 Allowed creative to trigger MRAID open click through even when MRAID not enabled in SDK.
+ MS-2290 Handled VideoEnabledWebChromeClient crash
+ MS-2289 Fixed resizeToFitContainer expanding too much logic.

## RC2.11
+ MS-2242 Added Facebook AdChoices icon and url to the nativeElements
          Also added NativeAd object of Facebook,InMobi and Yahoo to the nativeElements object
+ Gradle Release Automation
+ MS-2307 Full Screen interstitial orientation support.
+ Updated Mediated SDKs
    FaceBook SDK update to 4.11.0
    GooglePlay SDK update to 9.0.0
    MillennialMedia SDK update to 6.1.0
    MoPub SDK update to 4.6.1
    Vungle SDK update to 3.3.5
    Yahoo Flurry SDK update to 6.3.1

## RC2.10
+ Fix gradle build settings
+ MS-2136 Dismiss interstitial on click
+ MS-2034 Resize Ad for BannerAdview

## RC2.9.1
+ MS-2101 Bug fix in AdWebView
+ MS-2016 Fixed the AdSize issue in onLayout method of BannerAdview

## RC2.9
+ MS-1813 AdMarvel Integration - Banner and Interstitial ads
+ MS-2030 AdMob Integration - Native ads
+ MS-1819 Rubicon Integration - Banner ads
+ MS-2046 Handle RejectedExecutionException to fix a one-off crash
+ MS-2047 Handle PackageManagerException when System Webview gets updated while using the app
+ Updated Mediated SDKs
    Facebook 4.9.0 - banner, interstitial, native
    InMobi 5.2.0 - banner, interstitial, native
    MoPub 4.3.0 - banner, interstitial (Removed the native mediation temporarily due to incompatibility issues)
    GooglePlay 8.4.0 - banner, interstitial, native


## RC2.8
+ MS-1702 Support for requesting ads with inventory code and member id
+ MS-1745 Update unit tests for inventory code and member id
+ Supported networks:
    AdColony 2.3.0, interstitial, native
    Amazon 5.6.20, banner, interstitial
    Chartboost 5.5.3, interstitial
    Facebook 4.7.0, banner, interstitial, native
    Google Play services 7.8.0, only depend on the ads library now, DFP/AdMob banner, interstitial
    InMobi 5.0.0, banner, interstitial, native
    MM Media 6.0.0, banner, interstitial
    MoPub 3.13.0, banner, interstitial, native
    Vdopia lw1.5, banner, interstitial
    Vungle 3.3.2, interstitials
    Yahoo Flurry 6.1.0 banner, interstitial, native

## RC2.7.1
+ MS-1688 InMobi mediation adapters upgraded to support InMobi SDK 5.0.0
+ MS-1669 MoPub SDK update to 3.13.0 
+ MS-1424 Millennial Media mediation adapters upgraded to support MMSDK 6.0.0
+ Supported networks:
    AdColony 2.3.0, interstitial, native
    Amazon 5.6.20, banner, interstitial
    Chartboost 5.5.3, interstitial
    Facebook 4.7.0, banner, interstitial, native
    Google Play services 7.8.0, only depend on the ads library now, DFP/AdMob banner, interstitial
    InMobi 5.0.0, banner, interstitial, native
    MM Media 6.0.0, banner, interstitial
    MoPub 3.13.0, banner, interstitial, native
    Vdopia lw1.5, banner, interstitial
    Vungle 3.3.2, interstitials	
    Yahoo Flurry 6.1.0 banner, interstitial, native

## RC2.6
+ MS-1627 Bug fix for concurrency modification in RequestParams
+ MS-1629 Bug fix for using comma as decimal points when user sets certain languages
+ MS-1619 Bug fix for DFP banner impression logging
+ MS-1592 Bug fix for InterstitialAdView not passing back onAdCollapsed
+ Supported networks:
	AdColony 2.3.0, interstitial, native
	Amazon 5.6.20, banner, interstitial
        Chartboost 5.5.3, interstitial
	Facebook 4.7.0, banner, interstitial, native
	Google Play services 7.8.0, only depend on the ads library now, DFP/AdMob banner, interstitial
	InMobi 4.5.5, banner, interstitial, native
	MM Media 5.4.0, banner, interstitial
        MoPub 3.11.0, banner, interstitial, native
	Vdopia lw1.5, banner, interstitial
	Vungle 3.3.2, interstitials	
	Yahoo Flurry 6.1.0 banner, interstitial, native

## RC2.5
+ MS-1512 NPE fix in AdRequest
+ MS-1426 Change interstitial timeout from 60 seconds to 4.5 minutes
+ MS-1383/1494 Pro-guard settings fix
+ MS-1391 fix gradle build issue
+ Supported networks:
        AdColony 2.2.2, interstitial, native
        Amazon 5.6.20, banner, interstitial
        Chartboost 5.5.3, interstitial
        Facebook 4.5.1, banner, interstitial, native
        Google Play services 7.8.0, only depend on the ads library now, DFP/AdMob banner, interstitial
        InMobi 4.5.5, banner, interstitial, native
        MM Media 5.4.0, banner, interstitial
        MoPub 3.11.0, banner, interstitial, native
        Vdopia lw1.5, banner, interstitial
        Vungle 3.3.1, interstitials
        Yahoo Flurry 6.0.0 banner, interstitial, native

## RC 2.4
+ MS-1370 AdColony totation fix
+ MS-1307 AdFetcher refactor
+ MS-1326 AdWebView bug fix to support Android lollipop webview
+ MS-1320 Yahoo Flurry mediation, banner, interstitial, native, version 5.6.0
+ Supported networks:
        AdColony 2.2.2, interstitial, native
        Amazon 5.6.20, banner, interstitial
        Chartboost 5.3.0, interstitial
        Facebook 4.4.1, banner, interstitial, native
        Google Play services 7.5.0, only depend on the ads library now, DFP/AdMob banner, interstitial
        InMobi 4.5.5, banner, interstitial, native
        MM Media 5.4.0, banner, interstitial
        MoPub 3.9.0, banner, interstitial, native
        Vdopia lw1.5, banner, interstitial
        Vungle 3.3.1, interstitials
	Yahoo Flurry 5.6.0 banner, interstitial, native

## RC 2.3.1
+ MS-1350 fix native ad response expiration time

## RC 2.3
+ MS-1225 Legacy AdMob/DFP adapter update
+ MS-1135 AdColony In-feed video phase 1
+ Removed SDK unnecessary dependency on google play services
+ Supported networks:
	AdColony 2.2.2, interstitial, native
	Amazon 5.6.20, banner, interstitial
	Chartboost 5.4.1, interstitial
	Facebook 4.2.0, banner, interstitial, native
	Google Play services 7.5.0, only depend on the ads library now, DFP/AdMob banner, interstitial
	InMobi 4.5.5, banner, interstitial, native
	MM Media 5.4.0, banner, interstitial
	MoPub 3.8.0, banner, interstitial, native
	Vdopia lw1.5, banner, interstitial
	Vungle 3.3.0, interstitials

## RC 2.2
+ MS-1145 Separate invalid networks based on media type
+ MS-1110 Chartboost interstitial mediation
+ MS-953 InMobi native, banner, interstitial mediation
+ MS-952 Detect user interaction on webview
+ MS-1075 Vungle interstitial video mediation
+ MS-1074 AdColony interstial mediation
+ MS-1076 1080 Vdopia mediation, banner and interstitial

## RC 2.1
+ MS-857 MS-920 MS-866 AN native response, click handling, cookie management
+ MS-874 offline impression tracking
+ MS-914 MoPub native ad adapter
+ MS-915 update mediated SDKs, MoPub 3.2.0->3.3.0, Facebook 3.20->3.22, Amazon 5.4.192->5.4.325, GooglePlay->6.5.876
+ MS-890 Bug fix for Facebook native adapter
+ MS-927 Mraid compliance creative and bug fixes
+ MS-657 Clean up many lint warnings
+ MS-808 Remove runtime instance checks, overhaul old interstitial ad queue with new class-based model
+ MS-859 Release SDK on Maven
+ MS-782 closeButtonHandler made static to avoid potential memory leaks, cleaned up gradle lint
+ MS-763 Cleaner string building to minimize memory use
+ MS-826 If the impbus ever were to return malformed initcb/resultcb URLs, the SDK now fails gracefully on them
+ MS-790 Fix lint error in HTTPGet

## RC 2.0
+ MS-885 Facebook adapter exception handling 
+ MS-751 by default the android SDK now uses a modal dialog box loading animation during conversion loading. 
+ MS-846 Removed color attribute from XML interstitial setup because of conflicts with google lollipop libs
+ MS-855 refactor of AdFethcer and AdView 
+ MS-876 Add onActivityResume/Pause/Destroy to AdView, to be called by dev
+ MS-876 Fix DFP memory leak
+ MS-880 update mediated SDKs, Mopub 3.0.0->3.2.0, Facebook 3.18->3.20, Amazon 5.4.78->5.4.192, GooglePlay->6.1.11 
+ MS-842 native api public interface
+ MS-855 location_precision change

## RC 1.20



### New public APIs: 



+ `AdView.setLoadsInBackground(boolean)`: Controls the SDK's behavior when an ad is clicked. The default behavior (`true`) is to load the landing page in the background until the initial payload finishes loading and present a fully rendered page to the user. Setting this to `false` will cause the in-app browser to immediately become visible to display the un-rendered landing page. Note that setting to `false` when an ad redirects to the app store may cause the in-app browser to briefly flash on the screen.



+ `AdView.destroy()`: Whenever a view's activity or fragment is destroyed the developer must call the `destroy()` method of the view. This must be done in the UI thread.



+ `Settings.setLocationDecimalDigits(int digitsAfterDecimal)`: This method will ensure that any location information is rounded to the specified number of digits after the decimal.  The nominal resolution of digits after the decimal to distance is 2 digits to ~1 km , 3 digits to ~100m , 4 digits to ~10m.



+ `BannerView.setAdAlignment(enum AdAlignment)`: Override the alignment of the ad within its container. The default alignment is `CENTER`. Use this method to align to top left, top center, top right, center left, center, center right, bottom left, bottom center, or bottom right.



+ `BannerView.transitionAnimation()`: Add an optional animation for banner transitions. By default, the animation feature is disabled. The developer may choose from 4 transitions with optional animation direction and transition time.



### 3rd Party SDK updates:



+ Mopub 3.0.0

+ Amazon 5.3.22

+ Google Play 5.0.89

+ FB AudienceNetwork 3.18



### Bug Fixes:



+ Added proguard settings for Google Play, MoPub, Facebook and Millennial. These rules should be in the main applications `proguard-rules.txt` file.



+ Minimum API level bumped to 9.



+ Stability fixes to catch infrequent exceptions.



+ Handle a backwards/forward time synchronization gracefully.



### Notes:



To enable Amazon monetization your app must call, somewhere in its initialization path:



    import com.amazon.device.ads.AdRegistration;

    AdRegistration.setAppKey("YOUR_APP_KEY");
