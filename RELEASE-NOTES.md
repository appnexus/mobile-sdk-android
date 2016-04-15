## RC2.10
+ Fix gradle build settings
+ MS-2136 Dismiss interstitial on click

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
