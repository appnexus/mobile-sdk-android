LazyLoad Demo 
==================

## How to Use the App?
### App Location: /app_mobile-sdk/apps/Android/mobile-sdk-android/examples/kotlin/LazyLoadDemo
### The Main screen of the App contains two options:
* Banner
* MultiAdRequest
    ##### Banner
    1. Click on Load Lazily
    2. loadAd() wil be triggered
    3. onLazyAdLoaded will be called from the SDK if the Ad has been loaded lazily
    4. Now, click on Load Webview button and see if the webview is loaded successfully. 
    5. The logs can be tracked using the Logcat. Type the search filter as LAZYLOAD and filter the errors. The sequence of action can be tracked from there.
    ##### MultiAdRequest
    1. Select the AdTypes to test and click on Add.
    2. Once all the AdTypes of your selection has been added along with Banner-LazyLoad. Click on LOAD button.
    3. The next screen will contain a list of Ads that are loaded.
    4. The LazyLoad Ad will contain the Activate button, using which the banner.loadLazyAd() can triggered.
    5. The logs can be tracked using the Logcat. Type the search filter as LAZYLOAD and filter the errors. The sequence of action can be tracked from there.
