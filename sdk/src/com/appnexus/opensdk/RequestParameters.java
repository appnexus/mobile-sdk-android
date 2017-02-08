/*
 *    Copyright 2014 APPNEXUS INC
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

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Pair;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class RequestParameters {
    private MediaType mediaType;
    private String placementID;
    private int memberID;
    private String invCode;
    private boolean opensNativeBrowser = false;
    private int width = -1;
    private int height = -1;
    private int measuredWidth = -1;
    private int measuredHeight = -1;
    private boolean shouldServePSAs = false;
    private float reserve = 0.00f;
    private String age;
    private AdView.GENDER gender = AdView.GENDER.UNKNOWN;
    private ArrayList<Pair<String, String>> customKeywords = new ArrayList<Pair<String, String>>();
    private Context context;
    private int maximumWidth = -1;
    private int maximumHeight = -1;
    private boolean overrideMaxSize = false;
    private ArrayList<AdSize> allowedSizes;

    public RequestParameters(Context context) {
        this.context = context;
    }

    public String getPlacementID() {
        return placementID;
    }

    public void setPlacementID(String placementID) {
        this.placementID = placementID;
    }

    public void setInventoryCodeAndMemberID(int memberId, String invCode) {
        this.memberID = memberId;
        this.invCode = invCode;
    }

    public int getMemberID() {
        return memberID;
    }

    public String getInvCode() {
        return invCode;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setAdWidth(int width) {
        this.width = width;
    }

    public int getAdWidth() {
        if (mediaType == MediaType.BANNER) {
            return width;
        } else {
            return -1;
        }
    }

    public void setAdHeight(int height) {
        this.height = height;
    }

    public int getAdHeight() {
        if (mediaType == MediaType.BANNER) {
            return height;
        } else {
            return -1;
        }
    }

    public void setOverrideMaxSize(boolean overrideMaxSize) {
        this.overrideMaxSize = overrideMaxSize;
    }

    public boolean getOverrideMaxSize() {
        return mediaType == MediaType.BANNER && this.overrideMaxSize;
    }

    public void setMaxSize(int maxW, int maxH) {
        this.maximumWidth = maxW;
        this.maximumHeight = maxH;
    }

    public int getMaxWidth() {
        if (mediaType == MediaType.BANNER) {
            return this.maximumWidth;
        } else {
            return measuredWidth;
        }
    }

    public int getMaxHeight() {
        if (mediaType == MediaType.BANNER) {
            return this.maximumHeight;
        } else {
            return measuredHeight;
        }
    }

    public void setContainerWidth(int width) {
        this.measuredWidth = width;
    }

    public int getContainerWidth() {
        return measuredWidth;
    }

    public void setContainerHeight(int height) {
        this.measuredHeight = height;
    }

    public int getContainerHeight() {
        return measuredHeight;
    }

    public void setAllowedSizes(ArrayList<AdSize> allowed_sizes) {
        this.allowedSizes = allowed_sizes;
    }

    public ArrayList<AdSize> getAllowedSizes() {
            return allowedSizes;
    }

    public void setOpensNativeBrowser(boolean opensNativeBrowser) {
        this.opensNativeBrowser = opensNativeBrowser;
    }

    public boolean getOpensNativeBrowser() {
        return opensNativeBrowser;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    void setReserve(float reserve) {
        this.reserve = reserve;
    }

    float getReserve() {
        return reserve;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAge() {
        return age;
    }

    public void setGender(AdView.GENDER gender) {
        this.gender = gender;
    }

    public AdView.GENDER getGender() {
        return gender;
    }

    public void addCustomKeywords(String key, String value) {
        if (StringUtil.isEmpty(key) || (value == null)) {
            return;
        }
        this.customKeywords.add(new Pair<String, String>(key, value));
    }

    public void removeCustomKeyword(String key) {
        if (StringUtil.isEmpty(key))
            return;

        for (int i = 0; i < customKeywords.size(); i++) {
            Pair<String, String> pair = customKeywords.get(i);
            if (pair.first.equals(key)) {
                customKeywords.remove(i);
                break;
            }
        }
    }

    public void clearCustomKeywords() {
        customKeywords.clear();
    }

    public ArrayList<Pair<String, String>> getCustomKeywords() {
        return customKeywords;
    }

    public void setShouldServePSAs(boolean shouldServePSAs) {
        this.shouldServePSAs = shouldServePSAs;
    }

    public boolean getShouldServePSAs() {
        return shouldServePSAs;
    }

    /**
     * Check if required parameters are set for a certain media type
     *
     * @return true if ready for request
     */
    public boolean isReadyForRequest() {
        if ((StringUtil.isEmpty(invCode) || memberID <= 0) && StringUtil.isEmpty(placementID)) {
            Clog.e(Clog.baseLogTag, Clog.getString(R.string.no_identification));
            return false;
        }
        if (!(mediaType.equals(MediaType.NATIVE) || mediaType.equals(MediaType.INSTREAM_VIDEO))) {
            // Size info must be presented for BannerAdView and InterstitialAdView
            int tempMaxWidth;
            int tempMaxHeight;
            if (overrideMaxSize) {
                tempMaxWidth = maximumWidth;
                tempMaxHeight = maximumHeight;
                if (tempMaxWidth <= 0 || tempMaxHeight <= 0) {
                    Clog.w(Clog.baseLogTag, Clog.getString(R.string.max_size_not_set));
                }
            } else {
                tempMaxWidth = measuredWidth;
                tempMaxHeight = measuredHeight;
            }
            if ((tempMaxHeight <= 0 || tempMaxWidth <= 0) &&
                    (width <= 0 || height <= 0)) {
                Clog.e(Clog.baseLogTag, Clog.getString(R.string.no_size_info));
                return false;
            }
        }
        return true;
    }

    /**
     * Generate targeting parameters for mediated networks
     *
     * @return Targeting Parameters
     */

    TargetingParameters getTargetingParameters() {
        return new TargetingParameters(age, gender, customKeywords, SDKSettings.getLocation());
    }

    /**
     * Generate request url for AdRequest to send to server
     *
     * @return request url
     */
    String getRequestUrl() {
        StringBuilder sb;
        Settings settings = Settings.getSettings();
        sb = new StringBuilder(Settings.getRequestBaseUrl());
        if (!StringUtil.isEmpty(invCode) && memberID > 0) {
            sb.append("member=").append(memberID);
            sb.append("&inv_code=").append(Uri.encode(invCode));
        } else if (!StringUtil.isEmpty(placementID)) {
            sb.append("id=").append(Uri.encode(placementID));
        } else {
            sb.append("id=").append("NO-PLACEMENT-ID");
        }
        if (!StringUtil.isEmpty(settings.hidmd5))
            sb.append("&md5udid=").append(Uri.encode(settings.hidmd5));
        if (!StringUtil.isEmpty(settings.hidsha1))
            sb.append("&sha1udid=").append(Uri.encode(settings.hidsha1));
        if (!StringUtil.isEmpty(settings.aaid)) {
            sb.append("&aaid=").append(Uri.encode(settings.aaid));
            sb.append(settings.limitTrackingEnabled ? "&LimitAdTrackingEnabled=1" : "&LimitAdTrackingEnabled=0");
        }
        if (!StringUtil.isEmpty(settings.deviceMake))
            sb.append("&devmake=").append(Uri.encode(settings.deviceMake));
        if (!StringUtil.isEmpty(settings.deviceModel))
            sb.append("&devmodel=").append(Uri.encode(settings.deviceModel));
        // Get carrier
        if (settings.carrierName == null) {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            try {
                settings.carrierName = telephonyManager.getNetworkOperatorName();
            } catch (SecurityException ex) {
                // Some phones require READ_PHONE_STATE permission just ignore name
                settings.carrierName = "";
            }
        }
        if (!StringUtil.isEmpty(settings.carrierName))
            sb.append("&carrier=").append(Uri.encode(settings.carrierName));
        sb.append("&appid=");
        if (!StringUtil.isEmpty(settings.app_id)) {
            sb.append(Uri.encode(settings.app_id));
        } else {
            sb.append("NO-APP-ID");
        }
        if (settings.first_launch) sb.append("&firstlaunch=true");

        // Location settings
        String lat, lon, locDataAge, locDataPrecision;
        Location lastLocation = null;
        Location appLocation = SDKSettings.getLocation();
        // Do we have access to location?
        if (SDKSettings.getLocationEnabled()) {

            // First priority is the app supplied location
            if (appLocation != null) {
                lastLocation = appLocation;
            } else if (context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED
                    || context.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
                // Get lat, long from any GPS information that might be currently
                // available
                LocationManager lm = (LocationManager) context
                        .getSystemService(Context.LOCATION_SERVICE);

                for (String provider_name : lm.getProviders(true)) {
                    Location l = lm.getLastKnownLocation(provider_name);
                    if (l == null) {
                        continue;
                    }

                    if (lastLocation == null) {
                        lastLocation = l;
                    } else {
                        if (l.getTime() > 0 && lastLocation.getTime() > 0) {
                            if (l.getTime() > lastLocation.getTime()) {
                                lastLocation = l;
                            }
                        }
                    }
                }
            } else {
                Clog.w(Clog.httpReqLogTag,
                        Clog.getString(R.string.permissions_missing_location));
            }
        }

        // Set the location info back to the application
        if (appLocation != lastLocation) {
            SDKSettings.setLocation(lastLocation);
        }

        if (lastLocation != null) {
            if (SDKSettings.getLocationDecimalDigits() <= -1) {
                lat = "" + lastLocation.getLatitude();
                lon = "" + lastLocation.getLongitude();
            } else {
                lat = String.format(Locale.ENGLISH, "%." + SDKSettings.getLocationDecimalDigits() + "f", lastLocation.getLatitude());
                lon = String.format(Locale.ENGLISH, "%." + SDKSettings.getLocationDecimalDigits() + "f", lastLocation.getLongitude());
            }
            locDataPrecision = "" + lastLocation.getAccuracy();
            //Don't report location data from the future
            locDataAge = "" + Math.max(0, (System.currentTimeMillis() - lastLocation.getTime()));
        } else {
            lat = "";
            lon = "";
            locDataAge = "";
            locDataPrecision = "";
        }
        if (!StringUtil.isEmpty(lat) && !StringUtil.isEmpty(lon))
            sb.append("&loc=").append(lat).append(",").append(lon);
        if (!StringUtil.isEmpty(locDataAge)) sb.append("&loc_age=").append(locDataAge);
        if (!StringUtil.isEmpty(locDataPrecision)) sb.append("&loc_prec=").append(locDataPrecision);
        if (settings.test_mode) sb.append("&istest=true");
        if (!StringUtil.isEmpty(settings.ua)) sb.append("&ua=").append(Uri.encode(settings.ua));

        // Get orientation, the current rotation of the device
        orientation = context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE ? "h" : "v";
        if (!StringUtil.isEmpty(orientation)) sb.append("&orientation=").append(orientation);
        if (width > 0 && height > 0) sb.append("&size=").append(width).append("x").append(height);

        // complicated, don't change
        int maxWidth;
        int maxHeight;
        if (this.overrideMaxSize) {
            maxHeight = getMaxHeight();
            maxWidth = getMaxWidth();
            if (maxWidth <= 0 || maxHeight <= 0) {
                Clog.w(Clog.httpReqLogTag, Clog.getString(R.string.max_size_not_set));
            }
        } else {
            maxHeight = getContainerHeight();
            maxWidth = getContainerWidth();
        }
        if (maxHeight > 0 && maxWidth > 0) {
            if ((!mediaType.equals(MediaType.INTERSTITIAL)) && (width < 0 || height < 0)) {
                sb.append("&max_size=").append(maxWidth).append("x").append(maxHeight);
            } else if (mediaType.equals(MediaType.INTERSTITIAL)) {
                sb.append("&size=").append(maxWidth).append("x").append(maxHeight);
            }
        }

        ArrayList<AdSize> sizes = getAllowedSizes();
        if (sizes != null) {
            // Make string for allowed_sizes
            String allowedSizesForInterstitial = "";
            for (AdSize s : sizes) {
                allowedSizesForInterstitial += "" + s.width() + "x" + s.height();
                // If not last size, add a comma
                if (sizes.indexOf(s) != sizes.size() - 1)
                    allowedSizesForInterstitial += ",";
            }
            if (!StringUtil.isEmpty(allowedSizesForInterstitial))
                sb.append("&promo_sizes=").append(allowedSizesForInterstitial);
        }
        if (mediaType.equals(MediaType.NATIVE)) {
            // passing 1x1 allows a placement id to be used for multiple media types in Console
            sb.append("&size=1x1");
        }

        if (!StringUtil.isEmpty(settings.mcc)) sb.append("&mcc=").append(Uri.encode(settings.mcc));
        if (!StringUtil.isEmpty(settings.mnc)) sb.append("&mnc=").append(Uri.encode(settings.mnc));
        if (!StringUtil.isEmpty(settings.language))
            sb.append("&language=").append(Uri.encode(settings.language));
        String dev_timezone = "" + Settings.getSettings().dev_timezone;
        if (!StringUtil.isEmpty(dev_timezone))
            sb.append("&devtz=").append(Uri.encode(dev_timezone));
        String dev_time = "" + System.currentTimeMillis();
        if (!StringUtil.isEmpty(dev_time)) sb.append("&devtime=").append(Uri.encode(dev_time));

        // check connection type
        String connection_type = null;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null) {
            connection_type = wifi.isConnected() ? "wifi" : "wan";
        }
        if (!StringUtil.isEmpty(connection_type))
            sb.append("&connection_type=").append(Uri.encode(connection_type));
        String nativeBrowser = opensNativeBrowser ? "1" : "0";
        if (!StringUtil.isEmpty(nativeBrowser)) sb.append("&native_browser=").append(nativeBrowser);

        String psa;
        if (reserve > 0) {
            sb.append("&reserve=").append(reserve);
            psa = "0";
        } else {
            psa = shouldServePSAs ? "1" : "0";
        }
        if (!StringUtil.isEmpty(psa)) sb.append("&psa=").append(psa);
        if (!StringUtil.isEmpty(age)) sb.append("&age=").append(Uri.encode(age));

        if (gender != null) {
            String g = null;
            switch (gender) {
                case UNKNOWN:
                    g = null;
                    break;
                case MALE:
                    g = "m";
                    break;
                case FEMALE:
                    g = "f";
                    break;
            }
            if (!StringUtil.isEmpty(g)) sb.append("&gender=").append(Uri.encode(g));
        }

        String nonet;
        StringBuilder nonetSB = new StringBuilder();

        for (String invalidNetwork : settings.getInvalidNetwork(mediaType)) {
            // only add commas when there are additional items
            if (nonetSB.length() > 0) {
                nonetSB.append(",");
            }
            nonetSB.append(invalidNetwork);
        }
        nonet = nonetSB.toString();

        if (!StringUtil.isEmpty(nonet)) sb.append("&nonet=").append(Uri.encode(nonet));
        sb.append("&format=json");
        sb.append("&st=mobile_app");
        sb.append("&sdkver=").append(Uri.encode(Settings.getSettings().sdkVersion));

        // add custom parameters if there are any
        if (customKeywords != null) {
            synchronized (customKeywords) {
                for (Pair<String, String> pair : customKeywords) {
                    if (!StringUtil.isEmpty(pair.first) && (pair.second != null)) {
                        if (stringNotInParamNames(pair.first)) {
                            sb.append("&")
                                    .append(pair.first)
                                    .append("=")
                                    .append(Uri.encode(pair.second));
                        } else {
                            Clog.w(Clog.httpReqLogTag, Clog.getString(R.string.request_parameter_override_attempt, pair.first));
                        }
                    }
                }
            }
        }

        return sb.toString();
    }

    private String orientation;

    String getOrientation() {
        return orientation;
    }

    static HashSet<String> pNames = null;

    private static HashSet<String> getParamNames() {
        if (pNames == null) {
            pNames = new HashSet<String>();
            pNames.add("id");
            pNames.add("aaid");
            pNames.add("md5udid");
            pNames.add("sha1udid");
            pNames.add("devmake");
            pNames.add("devmodel");
            pNames.add("carrier");
            pNames.add("appid");
            pNames.add("firstlaunch");
            pNames.add("loc");
            pNames.add("loc_age");
            pNames.add("loc_prec");
            pNames.add("istest");
            pNames.add("ua");
            pNames.add("orientation");
            pNames.add("size");
            pNames.add("max_size");
            pNames.add("promo_sizes");
            pNames.add("mcc");
            pNames.add("mnc");
            pNames.add("language");
            pNames.add("devtz");
            pNames.add("devtime");
            pNames.add("connection_type");
            pNames.add("native_browser");
            pNames.add("psa");
            pNames.add("reserve");
            pNames.add("format");
            pNames.add("st");
            pNames.add("sdkver");

            return pNames;
        } else {
            return pNames;
        }
    }

    private static boolean stringNotInParamNames(String s) {
        return !getParamNames().contains(s);
    }

}
