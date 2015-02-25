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
