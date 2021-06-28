
/*
 *    Copyright 2017 APPNEXUS INC
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

package com.appnexus.opensdk.ut;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Pair;

import com.appnexus.opensdk.ANClickThroughAction;
import com.appnexus.opensdk.ANGDPRSettings;
import com.appnexus.opensdk.ANMultiAdRequest;
import com.appnexus.opensdk.ANExternalUserIdSource;
import com.appnexus.opensdk.ANUSPrivacySettings;
import com.appnexus.opensdk.Ad;
import com.appnexus.opensdk.AdSize;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.MediaType;
import com.appnexus.opensdk.R;
import com.appnexus.opensdk.SDKSettings;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.AdvertisingIDUtil;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import com.appnexus.opensdk.viewability.ANOmidViewabilty;

public class UTRequestParameters {

    private MediaType mediaType;
    private String placementID;
    @Deprecated
    private String externalUid;
    private int memberID;
    private int publisherId;
    private String invCode;
    private boolean doesLoadingInBackground = true;
    private AdSize primarySize;
    private ArrayList<AdSize> adSizes = new ArrayList<AdSize>();
    private boolean allowSmallerSizes = false;
    private boolean shouldServePSAs = false;
    private boolean isBannerVideoEnabled = false;
    private boolean isBannerEnabled = true;
    private boolean isBannerNativeEnabled = false;
    private String extInvCode;
    private float reserve = 0.00f;
    private String age;
    private String trafficSourceCode;

    private AdView.GENDER gender = AdView.GENDER.UNKNOWN;
    private ArrayList<Pair<String, String>> customKeywords = new ArrayList<Pair<String, String>>();
    private int videoAdMinDuration;
    private int videoAdMaxDuration;

    private int forceCreativeId = 0;
    private ANClickThroughAction clickThroughAction = ANClickThroughAction.OPEN_SDK_BROWSER;

    private String ANSDK = "ansdk";

    private static final String SIZE_WIDTH = "width";
    private static final String SIZE_HEIGHT = "height";
    private static final String TAGS = "tags";
    private static final String TAG_ID = "id";
    private static final String TAG_SIZES = "sizes";
    private static final String SDK = "sdk";
    private static final String TAG_PRIMARY_SIZE = "primary_size";
    private static final String TAG_ALLOW_SMALLER_SIZES = "allow_smaller_sizes";
    private static final String TAG_ALLOWED_AD_TYPES = "ad_types";
    private static final String TAG_ALLOWED_MEDIA_AD_TYPES = "allowed_media_types";
    private static final String TAG_DISABLE_PSA = "disable_psa";
    private static final String TAG_PREBID = "prebid";
    private static final String TAG_RESERVE = "reserve";
    private static final String TAG_ASSET_URL = "require_asset_url";
    private static final String TAG_NATIVE = "native";
    private static final String TAG_RENDERER_ID = "renderer_id";
    private static final boolean TAG_ASSET_URL_VALUE = false;
    private static final String TAG_CODE = "code";
    private static final String TAG_UUID = "uuid";
    private static final String EXTERNAL_IDS = "eids";
    private static final String EXTERNALID_SOURCE = "source";
    private static final String EXTERNALID_ID = "id";
    private static final String EXTERNALID_RTI_PARTNER = "rti_partner";
    private static final String EXTERNALID_RTI_PARTNER_UID2 = "UID2";
    private static final String EXTERNALID_RTI_PARTNER_TDID = "TDID";
    private static final String EXTERNALID_SOURCE_LIVERAMP = "liveramp.com";
    private static final String EXTERNALID_SOURCE_NETID = "netid.de";
    private static final String EXTERNALID_SOURCE_CRITEO = "criteo.com";
    private static final String EXTERNALID_SOURCE_THETRADEDESK = "adserver.org";
    private static final String EXTERNALID_SOURCE_UID2 = "uidapi.com";
    private static final String USER = "user";
    private static final String USER_AGE = "age";
    private static final String USER_GENDER = "gender";
    private static final String USER_EXTERNALUID = "external_uid"; // publisher first party id
    private static final String USER_LANGUAGE = "language";
    private static final String USER_DNT = "dnt";
    private static final String DEVICE = "device";
    private static final String GEO_OVERRIDE = "geoOverride";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String ZIP = "zip";
    private static final String AUCTION_TIMEOUT_MS = "auction_timeout_ms";
    private static final String DEVICE_USERAGENT = "useragent";
    private static final String DEVICE_GEO = "geo";
    private static final String GEO_LAT = "lat";
    private static final String GEO_LON = "lng";
    private static final String GEO_AGE = "loc_age";
    private static final String GEO_PREC = "loc_precision";
    private static final String DEVICE_MAKE = "make";
    private static final String DEVICE_MODEL = "model";
    private static final String DEVICE_OS = "os";
    private static final String DEVICE_CARRIER = "carrier";
    private static final String DEVICE_CONNECTIONTYPE = "connectiontype";
    private static final String DEVICE_MCC = "mcc";
    private static final String DEVICE_MNC = "mnc";
    private static final String DEVICE_LMT = "limit_ad_tracking";
    private static final String DEVICE_DEVICE_ID = "device_id";
    private static final String DEVICE_DEVTIME = "devtime";
    private static final String DEVICE_ID_AAID = "aaid";
    private static final String APP = "app";
    private static final String APP_ID = "appid";
    private static final String KEYWORDS = "keywords";
    private static final String MEMBER_ID = "member_id";
    private static final String PUBLISHER_ID = "publisher_id";
    private static final String KEYVAL_KEY = "key";
    private static final String KEYVAL_VALUE = "value";
    private static final String SDK_VERSION = "sdkver";
    private static final String SUPPLY_TYPE = "supply_type";
    private static final String SUPPLY_TYPE_CONTENT = "mobile_app";
    private static final String SOURCE = "source";
    private static final String VERSION = "version";
    private static final String TAG_VIDEO = "video";
    private static final String TAG_MINDURATION = "minduration";
    private static final String TAG_MAXDURATION = "maxduration";
    private static final String GDPR_CONSENT = "gdpr_consent";
    private static final String GDPR_CONSENT_STRING = "consent_string";
    private static final String GDPR_CONSENT_REQUIRED = "consent_required";
    private static final String GDPR_GOOGLE_ACM_ADDTL_CONSENT = "addtl_consent";
    private static final String FORCE_CREATIVE_ID = "force_creative_id";
    private static final String IAB_SUPPORT = "iab_support";
    private static final String OMIDPN = "omidpn";
    private static final String OMIDPV = "omidpv";
    private static final String BANNER_FRAMEWORKS = "banner_frameworks";
    private static final String VIDEO_FRAMEWORKS = "video_frameworks";
    private static final String NATIVE_FRAMEWORKS = "native_frameworks";
    private static final String OMID_FRAMEWORK_SIGNAL = "[6]";
    private static final String EXT_INV_CODE = "ext_inv_code";
    private static final String TRAFFICE_SOURCE_CODE = "traffic_source_code";

    private static final String US_PRIVACY = "us_privacy";


    private static final int ALLOWED_TYPE_BANNER = 1;
    private static final int ALLOWED_TYPE_INTERSTITIAL = 3;
    private static final int ALLOWED_TYPE_VIDEO = 4;
    private static final int ALLOWED_TYPE_NATIVE = 12;
    private static final String os = "android";

    private Context context;
    private int rendererId = 0;
    private ArrayList<WeakReference<Ad>> adUnitList;
    private String uuid;

    private WeakReference<ANMultiAdRequest> anMultiAdRequest;

    public static String FB_SETTINGS_CLASS = "com.appnexus.opensdk.csr.FBSettings";

    public UTRequestParameters(Context context) {
        this.context = context;
        adUnitList = new ArrayList<>();
        anMultiAdRequest = new WeakReference<>(null);
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

    public void setExtInvCode(String extInvCode) {
        this.extInvCode = extInvCode;
    }

    public String getExtInvCode() {
        return this.extInvCode;
    }

    public void setTrafficSourceCode(String trafficSourceCode) {
        this.trafficSourceCode = trafficSourceCode;
    }

    public String getTrafficSourceCode() {
        return this.trafficSourceCode;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
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

    public void setPrimarySize(AdSize primarySize) {
        this.primarySize = primarySize;
    }

    public AdSize getPrimarySize() {
        return this.primarySize;
    }

    public boolean getAllowSmallerSizes() {
        return this.allowSmallerSizes;
    }

    public void setAllowSmallerSizes(boolean allowSmallerSizes) {
        this.allowSmallerSizes = allowSmallerSizes;
    }

    public void setSizes(ArrayList<AdSize> adSizes) {
        this.adSizes = adSizes;
    }

    public ArrayList<AdSize> getSizes() {
        return adSizes;
    }

    /**
     * @deprecated Use setClickThroughAction instead
     * Refer {@link ANClickThroughAction}
     */
    public void setOpensNativeBrowser(boolean opensNativeBrowser) {
        setClickThroughAction(opensNativeBrowser ? ANClickThroughAction.OPEN_DEVICE_BROWSER : ANClickThroughAction.OPEN_SDK_BROWSER);
    }

    /**
     * @deprecated Use getClickThroughAction instead
     * Refer {@link ANClickThroughAction}
     */
    public boolean getOpensNativeBrowser() {
        return (getClickThroughAction() == ANClickThroughAction.OPEN_DEVICE_BROWSER);
    }


    public ANClickThroughAction getClickThroughAction() {
        return clickThroughAction;
    }

    public void setClickThroughAction(ANClickThroughAction clickThroughAction) {
        this.clickThroughAction = clickThroughAction;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setReserve(float reserve) {
        this.reserve = reserve;
    }

    public float getReserve() {
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

    @Deprecated
    public String getExternalUid() {
        return externalUid;
    }

    @Deprecated
    public void setExternalUid(String externalUid) {
        this.externalUid = externalUid;
    }

    public int getVideoAdMinDuration() {
        return videoAdMinDuration;
    }

    public void setVideoAdMinDuration(int minDuration) {
        this.videoAdMinDuration = minDuration;
    }

    public int getVideoAdMaxDuration() {
        return videoAdMaxDuration;
    }

    public void setVideoAdMaxDuration(int maxDuration) {
        this.videoAdMaxDuration = maxDuration;
    }

    public boolean getLoadsInBackground() {
        return doesLoadingInBackground;
    }

    public void setLoadsInBackground(boolean doesLoadingInBackground) {
        this.doesLoadingInBackground = doesLoadingInBackground;
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


    public boolean isBannerVideoEnabled() {
        return isBannerVideoEnabled;
    }

    public void setBannerVideoEnabled(boolean bannerVideoEnabled) {
        isBannerVideoEnabled = bannerVideoEnabled;
    }

    public void setBannerEnabled(boolean bannerEnabled) {
        isBannerEnabled = bannerEnabled;
    }

    public boolean isBannerEnabled() {
        return isBannerEnabled;
    }

    public boolean isBannerNativeEnabled() {
        return isBannerNativeEnabled;
    }

    public void setBannerNativeEnabled(boolean bannerNativeEnabled) {
        isBannerNativeEnabled = bannerNativeEnabled;
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
        if ((primarySize == null || (primarySize.width() <= 0 || primarySize.height() <= 0))) {
            Clog.e(Clog.baseLogTag, Clog.getString(R.string.no_size_info));
            return false;
        }
        return true;
    }

    /**
     * Generate targeting parameters for mediated networks
     *
     * @return Targeting Parameters
     */

    public TargetingParameters getTargetingParameters() {
        return new TargetingParameters(age, gender, customKeywords, SDKSettings.getLocation(), externalUid);
    }

    private String getFacebookBidderToken(Context context) {
        try {
            Class clazz = Class.forName(FB_SETTINGS_CLASS);
            Method method = clazz.getMethod("getBidderToken", Context.class);
            Object result = method.invoke(null, context);
            if (result instanceof String) {
                return (String) result;
            }
        } catch (NullPointerException e) {
            Clog.d(Clog.csrLogTag, e.getMessage());
        } catch (NoSuchMethodException e) {
            Clog.d(Clog.csrLogTag, e.getMessage());
        } catch (InvocationTargetException e) {
            Clog.d(Clog.csrLogTag, e.getMessage());
        } catch (IllegalAccessException e) {
            Clog.d(Clog.csrLogTag, e.getMessage());
        } catch (ClassNotFoundException e) {
            Clog.d(Clog.csrLogTag, e.getMessage());
        }
        return null;
    }

    private void appendFBToken(JSONObject postData, Context context) {
        String token = getFacebookBidderToken(context);
        if (token != null) {
            try {
                JSONObject fan = new JSONObject();
                fan.put("provider", "audienceNetwork");
                fan.put("user_id", token);
                JSONArray tpuids = new JSONArray();
                tpuids.put(fan);
                postData.put("tpuids", tpuids);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    // Package only for testing purpose
    String getPostData() {
        Context context = this.getContext();
        if (null == context) {
            Clog.e(Clog.baseLogTag, "UTRequestParameters.getPostData() -- context is NULL.");
            return "";
        }

        // Try to retrieve aaid and limitedAdTracking if they were not set
        AdvertisingIDUtil.retrieveAndSetAAID(context);

        orientation = context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE ? "h" : "v";
        JSONObject postData = new JSONObject();
        try {
            // add tags
            JSONArray tags = getTagsObject(postData);
            if (tags != null && tags.length() > 0) {
                postData.put(TAGS, tags);
            }
            // add user
            UTRequestParameters localParams = this;
            if (anMultiAdRequest.get() != null) {
                localParams = anMultiAdRequest.get().getRequestParameters();
            }
            JSONObject user = getUserObject(localParams);
            if (user != null && user.length() > 0) {
                postData.put(USER, user);
            }

            JSONArray eidArray = getExternalUserIdArray();
            if (eidArray != null && eidArray.length() > 0) {
                postData.put(EXTERNAL_IDS, eidArray);
            }

            // add device
            JSONObject device = getDeviceObject();
            if (device != null && device.length() > 0) {
                postData.put(DEVICE, device);
            }

            // add geoOverride
            JSONObject geoOverride = getGeoOverride();
            if (geoOverride != null && geoOverride.length() > 0) {
                postData.put(GEO_OVERRIDE, geoOverride);
            }

            // add auctionTimeout
            long auctionTimeout = SDKSettings.getAuctionTimeout();
            if (auctionTimeout > 0) {
                postData.put(AUCTION_TIMEOUT_MS, auctionTimeout);
            }


            // add app
            JSONObject app = getAppObject();
            if (device != null && device.length() > 0) {
                postData.put(APP, app);
            }

            postData.put(SDK_VERSION, ANSDK + Settings.getSettings().sdkVersion);

            // add sdk
            JSONObject sdk = getSDKObject();
            if (sdk != null && sdk.length() > 0) {
                postData.put(SDK, sdk);
            }

            postData.put(SUPPLY_TYPE, SUPPLY_TYPE_CONTENT);

            if (anMultiAdRequest.get() != null || (adUnitList != null && adUnitList.size() > 0)) {
                // add custom keywords
                ANMultiAdRequest multiAdRequest = anMultiAdRequest.get();
                ArrayList<Pair<String, String>> customKeywords = getCustomKeywords();
                int memberID = getMemberID();
                int publisherId = getPublisherId();
                if (multiAdRequest != null) {
                    customKeywords = multiAdRequest.getCustomKeywords();
                    memberID = multiAdRequest.getRequestParameters().getMemberID();
                    publisherId = multiAdRequest.getRequestParameters().getPublisherId();
                }
                JSONArray keywordsArray = getCustomKeywordsArray(customKeywords);
                if (keywordsArray != null && keywordsArray.length() > 0) {
                    postData.put(KEYWORDS, keywordsArray);
                }


                //add Member ID
                if (memberID > 0) {
                    postData.put(MEMBER_ID, memberID);
                }

                if (publisherId > 0) {
                    postData.put(PUBLISHER_ID, publisherId);
                }
            }

            // add GDPR Consent
            JSONObject gdprConsent = getGDPRConsentObject();
            if (gdprConsent != null && gdprConsent.length() > 0) {
                postData.put(GDPR_CONSENT, gdprConsent);
            }

            // add IAB Support Signal

            if (SDKSettings.getOMEnabled()) {
                JSONObject iabSupport = getIABSupportObject();
                if (iabSupport != null && iabSupport.length() > 0) {
                    postData.put(IAB_SUPPORT, iabSupport);
                }
            }
            // add Facebook bidder token if available
            appendFBToken(postData, context);

            // add USPrivacy String
            String privacyString = ANUSPrivacySettings.getUSPrivacyString(context);
            if (!privacyString.isEmpty()) {
                postData.put(US_PRIVACY, privacyString);
            }


        } catch (JSONException e) {
            Clog.e(Clog.httpReqLogTag, "JSONException: " + e.getMessage());
        }
        Clog.i(Clog.httpReqLogTag, "POST data: " + postData.toString());
        return postData.toString();
    }

    private JSONObject getGeoOverride() {
        try {
            JSONObject geoOverride = new JSONObject();
            if (!StringUtil.isEmpty(SDKSettings.getGeoOverrideCountryCode())) {
                geoOverride.put(COUNTRY_CODE, SDKSettings.getGeoOverrideCountryCode());
            }
            if (!StringUtil.isEmpty(SDKSettings.getGeoOverrideZipCode())) {
                geoOverride.put(ZIP, SDKSettings.getGeoOverrideZipCode());
            }
            return geoOverride;
        }catch (JSONException e) {
            Clog.e(Clog.httpReqLogTag, "JSONException: " + e.getMessage());
        }
        return null;
    }

    private String orientation;

    public String getOrientation() {
        return orientation;
    }

    private JSONArray getTagsObject(JSONObject postData) {
        JSONArray tags = new JSONArray();
        int limit = adUnitList != null && adUnitList.size() > 0 ? adUnitList.size() : 1;
        boolean isMultiAdList = (adUnitList != null && adUnitList.size() > 0);
        for (int count = 0; count < limit; count++) {
            String uuid = UUID.randomUUID().toString();
            UTRequestParameters utRequestParameters = this;
            if (isMultiAdList) {
                Ad ad = adUnitList.get(count).get();
                if (ad == null) {
                    continue;
                }
                ad.getRequestParameters().setUUID(uuid);
                utRequestParameters = ad.getRequestParameters();
            }
            JSONObject tag = new JSONObject();
            try {
                if (!StringUtil.isEmpty(utRequestParameters.getInvCode()) && utRequestParameters.getMemberID() > 0) {
                    tag.put(TAG_CODE, utRequestParameters.getInvCode());
                    postData.put(MEMBER_ID, utRequestParameters.getMemberID());
                } else if (!StringUtil.isEmpty(utRequestParameters.getPlacementID())) {
                    tag.put(TAG_ID, StringUtil.getIntegerValue(utRequestParameters.getPlacementID()));
                } else {
                    tag.put(TAG_ID, 0);
                }

                if (uuid != null) {
                    tag.put(TAG_UUID, uuid);
                }

                if ((anMultiAdRequest.get() == null) && utRequestParameters.getPublisherId() > 0) {
                    postData.put(PUBLISHER_ID, utRequestParameters.getPublisherId());
                }

                if (!StringUtil.isEmpty(getExtInvCode())) {
                    tag.put(EXT_INV_CODE, getExtInvCode());
                }
                if (!StringUtil.isEmpty(getTrafficSourceCode())) {
                    tag.put(TRAFFICE_SOURCE_CODE, getTrafficSourceCode());
                }

                JSONObject primesize = new JSONObject();
                primesize.put(SIZE_WIDTH, utRequestParameters.primarySize.width());
                primesize.put(SIZE_HEIGHT, utRequestParameters.primarySize.height());
                tag.put(TAG_PRIMARY_SIZE, primesize);

                if (utRequestParameters.forceCreativeId > 0) {
                    tag.put(FORCE_CREATIVE_ID, utRequestParameters.forceCreativeId);
                }

                // add custom parameters if there are any
                JSONArray keywordsArray = getCustomKeywordsArray(utRequestParameters.getCustomKeywords());
                if (keywordsArray != null && keywordsArray.length() > 0) {
                    tag.put(KEYWORDS, keywordsArray);
                }


                ArrayList<AdSize> sizesArray = utRequestParameters.getSizes();
                JSONArray sizes = new JSONArray();
                if (sizesArray != null && sizesArray.size() > 0) {
                    for (AdSize s : sizesArray) {
                        JSONObject size = new JSONObject();
                        size.put(SIZE_WIDTH, s.width());
                        size.put(SIZE_HEIGHT, s.height());
                        sizes.put(size);
                    }
                }
                tag.put(TAG_SIZES, sizes);

                tag.put(TAG_ALLOW_SMALLER_SIZES, getAllowSmallerSizes());

                JSONArray allowedMediaAdTypes = new JSONArray();
                JSONArray omidFrameworksValue = new JSONArray(OMID_FRAMEWORK_SIGNAL);

                if (utRequestParameters.getMediaType() == MediaType.BANNER) {
                    if (utRequestParameters.isBannerEnabled) {
                        allowedMediaAdTypes.put(ALLOWED_TYPE_BANNER);
                        if(SDKSettings.getOMEnabled()) {
                            tag.put(BANNER_FRAMEWORKS, omidFrameworksValue);
                        }
                    }
                    if (utRequestParameters.isBannerVideoEnabled) {
                        allowedMediaAdTypes.put(ALLOWED_TYPE_VIDEO);
                        if(SDKSettings.getOMEnabled()) {
                            tag.put(VIDEO_FRAMEWORKS, omidFrameworksValue);
                        }
                    }
                    if (utRequestParameters.isBannerNativeEnabled) {
                        allowedMediaAdTypes.put(ALLOWED_TYPE_NATIVE);
                        if(SDKSettings.getOMEnabled()) {
                            tag.put(NATIVE_FRAMEWORKS, omidFrameworksValue);
                        }
                    }
                } else if (utRequestParameters.getMediaType() == MediaType.INTERSTITIAL) {
                    allowedMediaAdTypes.put(ALLOWED_TYPE_BANNER);
                    allowedMediaAdTypes.put(ALLOWED_TYPE_INTERSTITIAL);
                    if(SDKSettings.getOMEnabled()) {
                        tag.put(BANNER_FRAMEWORKS, omidFrameworksValue);
                    }
                } else if (utRequestParameters.getMediaType() == MediaType.NATIVE) {
                    allowedMediaAdTypes.put(ALLOWED_TYPE_NATIVE);
                    if(SDKSettings.getOMEnabled()) {
                        tag.put(NATIVE_FRAMEWORKS, omidFrameworksValue);
                    }
                } else if (utRequestParameters.getMediaType() == MediaType.INSTREAM_VIDEO) {
                    allowedMediaAdTypes.put(ALLOWED_TYPE_VIDEO);
                    if(SDKSettings.getOMEnabled()) {
                        tag.put(VIDEO_FRAMEWORKS, omidFrameworksValue);
                    }
                }

                tag.put(TAG_ALLOWED_MEDIA_AD_TYPES, allowedMediaAdTypes);

                if (utRequestParameters.getMediaType() == MediaType.INSTREAM_VIDEO) {
                    JSONObject videoObject = utRequestParameters.getVideoObject();
                    if (videoObject.length() > 0) {
                        tag.put(TAG_VIDEO, videoObject);
                    }
                }


                tag.put(TAG_PREBID, false);
                if (utRequestParameters.getReserve() > 0) {
                    tag.put(TAG_RESERVE, utRequestParameters.getReserve());
                    tag.put(TAG_DISABLE_PSA, true);
                } else {
                    tag.put(TAG_DISABLE_PSA, !utRequestParameters.getShouldServePSAs());
                }
                tag.put(TAG_ASSET_URL, TAG_ASSET_URL_VALUE);

                if (utRequestParameters.getMediaType() == MediaType.NATIVE || (utRequestParameters.getMediaType() == MediaType.BANNER && isBannerNativeEnabled())) {
                    JSONObject nativeObject = getNativeRendererObject(utRequestParameters);
                    if (nativeObject != null) {
                        tag.put(TAG_NATIVE, nativeObject);
                    }
                }

            } catch (JSONException e) {
                Clog.e(Clog.baseLogTag, "Exception: " + e.getMessage());
            }
            if (tag.length() > 0) {
                tags.put(tag);
            }
        }
        return tags;
    }

    private void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getUUID() {
        return uuid;
    }

    private JSONObject getNativeRendererObject(UTRequestParameters utRequestParameters) {
        if (utRequestParameters.getRendererId() != 0) {
            try {
                JSONObject nativeObject = new JSONObject();
                nativeObject.put(TAG_RENDERER_ID, utRequestParameters.getRendererId());
                return nativeObject;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private JSONObject getUserObject(UTRequestParameters utRequestParameters) {
        JSONObject user = new JSONObject();
        try {
            if (StringUtil.getIntegerValue(utRequestParameters.getAge()) > 0) {
                user.put(USER_AGE, StringUtil.getIntegerValue(utRequestParameters.getAge()));
            }

            AdView.GENDER gender = utRequestParameters.getGender();
            int g = 0;
            switch (gender) {
                case FEMALE:
                    g = 2;
                    break;
                case MALE:
                    g = 1;
                    break;
                case UNKNOWN:
                    g = 0;
                    break;
            }
            user.put(USER_GENDER, g);
            if (!StringUtil.isEmpty(Settings.getSettings().language)) {
                user.put(USER_LANGUAGE, Settings.getSettings().language);
            }

            if(!StringUtil.isEmpty(Settings.getSettings().publisherUserId)){
                user.put(USER_EXTERNALUID, Settings.getSettings().publisherUserId);
            } else if (!StringUtil.isEmpty(utRequestParameters.getExternalUid())) {
                user.put(USER_EXTERNALUID, utRequestParameters.getExternalUid());
            }

            if(Settings.getSettings().doNotTrack){
                user.put(USER_DNT, true);
            }

        } catch (JSONException e) {
        }
        return user;
    }

    private JSONArray getExternalUserIdArray() {
        JSONArray idArray = new JSONArray();
        try {
            if (Settings.getSettings().externalUserIds != null) {
                for (Map.Entry<ANExternalUserIdSource, String> entry : Settings.getSettings().externalUserIds.entrySet()) {
                    JSONObject idObject = new JSONObject();
                    switch (entry.getKey()) {
                        case THE_TRADE_DESK:
                            idObject.put(EXTERNALID_SOURCE, EXTERNALID_SOURCE_THETRADEDESK);
                            idObject.put(EXTERNALID_ID, entry.getValue());
                            idObject.put(EXTERNALID_RTI_PARTNER, EXTERNALID_RTI_PARTNER_TDID);
                            break;
                        case CRITEO:
                            idObject.put(EXTERNALID_SOURCE, EXTERNALID_SOURCE_CRITEO);
                            idObject.put(EXTERNALID_ID, entry.getValue());
                            break;
                        case NETID:
                            idObject.put(EXTERNALID_SOURCE, EXTERNALID_SOURCE_NETID);
                            idObject.put(EXTERNALID_ID, entry.getValue());
                            break;
                        case LIVERAMP:
                            idObject.put(EXTERNALID_SOURCE, EXTERNALID_SOURCE_LIVERAMP);
                            idObject.put(EXTERNALID_ID, entry.getValue());
                            break;
                        case UID2:
                            idObject.put(EXTERNALID_SOURCE, EXTERNALID_SOURCE_UID2);
                            idObject.put(EXTERNALID_ID, entry.getValue());
                            idObject.put(EXTERNALID_RTI_PARTNER, EXTERNALID_RTI_PARTNER_UID2);
                            break;
                    }
                    idArray.put(idObject);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return idArray;
    }


    private JSONObject getDeviceObject() {
        JSONObject device = new JSONObject();
        try {
            // Device make
            if (!StringUtil.isEmpty(Settings.getSettings().deviceMake))
                device.put(DEVICE_MAKE, Settings.getSettings().deviceMake);
            // Device model
            if (!StringUtil.isEmpty(Settings.getSettings().deviceModel))
                device.put(DEVICE_MODEL, Settings.getSettings().deviceModel);
            // POST data that requires context
            Context context = this.getContext();
            if (context != null) {
                // Default User Agent
                if (!StringUtil.isEmpty(Settings.getSettings().ua)) {
                    device.put(DEVICE_USERAGENT, Settings.getSettings().ua);
                }

                TelephonyManager telephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                // Get mobile country codes

                if (Settings.getSettings().mcc == null || Settings.getSettings().mnc == null) {
                    String networkOperator = telephonyManager.getNetworkOperator();
                    if (!StringUtil.isEmpty(networkOperator)) {
                        try {
                            Settings.getSettings().mcc = networkOperator.substring(0, 3);
                            Settings.getSettings().mnc = networkOperator.substring(3);
                        } catch (Exception e) {
                            // Catches IndexOutOfBoundsException
                            Settings.getSettings().mcc = null;
                            Settings.getSettings().mnc = null;
                        }
                    }
                }
                if (Settings.getSettings().mcc != null && Settings.getSettings().mnc != null) {
                    device.put(DEVICE_MNC, StringUtil.getIntegerValue(Settings.getSettings().mnc));
                    device.put(DEVICE_MCC, StringUtil.getIntegerValue(Settings.getSettings().mcc));
                }

                // Get carrier
                if (Settings.getSettings().carrierName == null) {
                    try {
                        Settings.getSettings().carrierName = telephonyManager.getNetworkOperatorName();
                    } catch (SecurityException ex) {
                        // Some phones require READ_PHONE_STATE permission just ignore name
                        Settings.getSettings().carrierName = "";
                    }
                }
                if (!StringUtil.isEmpty(Settings.getSettings().carrierName))
                    device.put(DEVICE_CARRIER, Settings.getSettings().carrierName);

                // check connection type
                int connection_type = 0;
                ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnected()) {
                    NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (wifi != null) {
                        connection_type = wifi.isConnected() ? 1 : 2;
                    }
                }
                device.put(DEVICE_CONNECTIONTYPE, connection_type);
            }

            Double lat, lon;
            Integer locDataAge, locDataPrecision;
            Location lastLocation = null;
            Location appLocation = SDKSettings.getLocation();
            // Do we have access to location?
            if (SDKSettings.getLocationEnabled()) {

                // First priority is the app supplied location
                if (appLocation != null) {
                    lastLocation = appLocation;
                } else if (context != null
                        && (context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED
                        || context.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED)) {
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
                    Clog.w(Clog.baseLogTag, "Location permissions ACCESS_COARSE_LOCATION and/or ACCESS_FINE_LOCATION are not set in the host app. This may affect demand.");
                }
            }

            // Set the location info back to the application
            if (appLocation != lastLocation) {
                SDKSettings.setLocation(lastLocation);
            }

            if (lastLocation != null) {
                if (SDKSettings.getLocationDecimalDigits() <= -1) {
                    lat = lastLocation.getLatitude();
                    lon = lastLocation.getLongitude();
                } else {
                    lat = Double.parseDouble(String.format(Locale.ENGLISH, "%." + SDKSettings.getLocationDecimalDigits() + "f", lastLocation.getLatitude()));
                    lon = Double.parseDouble(String.format(Locale.ENGLISH, "%." + SDKSettings.getLocationDecimalDigits() + "f", lastLocation.getLongitude()));
                }
                locDataPrecision = Math.round(lastLocation.getAccuracy());
                //Don't report location data from the future
                locDataAge = (int) Math.max(0, (System.currentTimeMillis() - lastLocation.getTime()));
            } else {
                lat = null;
                lon = null;
                locDataAge = null;
                locDataPrecision = null;
            }
            JSONObject geo = new JSONObject();
            //noinspection ConstantConditions
            if (lat != null && lon != null) {
                geo.put(GEO_LAT, lat);
                geo.put(GEO_LON, lon);
                //noinspection ConstantConditions
                if (locDataAge != null) geo.put(GEO_AGE, locDataAge);
                //noinspection ConstantConditions
                if (locDataPrecision != null) geo.put(GEO_PREC, locDataPrecision);
            }
            if (geo.length() > 0) {
                device.put(DEVICE_GEO, geo);
            }

            // devtime
            long dev_time = System.currentTimeMillis();
            device.put(DEVICE_DEVTIME, dev_time);

            // limited ad tracking
            device.put(DEVICE_LMT, Settings.getSettings().limitTrackingEnabled);

            // Update the value of device access consent in setting each time a request is made.
            Settings.getSettings().deviceAccessAllowed = ANGDPRSettings.canIAccessDeviceData(context);

            if (Settings.getSettings().deviceAccessAllowed && !SDKSettings.isAAIDUsageDisabled() && !Settings.getSettings().doNotTrack) {
                if (!StringUtil.isEmpty(Settings.getSettings().aaid)) {
                    JSONObject device_id = new JSONObject();
                    device_id.put(DEVICE_ID_AAID, Settings.getSettings().aaid);
                    device.put(DEVICE_DEVICE_ID, device_id);
                }
            }
            // os
            device.put(DEVICE_OS, os);
        } catch (JSONException e) {
            Clog.e(Clog.baseLogTag, e.getMessage());
        }

        return device;
    }

    private JSONObject getAppObject() {
        if (StringUtil.isEmpty(Settings.getSettings().app_id)) {
            Context context = this.getContext();
            if (context != null) {
                Settings.getSettings().app_id = context.getApplicationContext().getPackageName();
            }
        }
        JSONObject app = new JSONObject();
        try {
            if (!StringUtil.isEmpty(Settings.getSettings().app_id)) {
                app.put(APP_ID, Settings.getSettings().app_id);
            }
        } catch (JSONException e) {
        }
        return app;
    }

    private JSONObject getVideoObject() {

        JSONObject videoDict = new JSONObject();
        try {
            if (this.videoAdMinDuration > 0) {
                videoDict.put(TAG_MINDURATION, this.videoAdMinDuration);
            }

            if (this.videoAdMaxDuration > 0) {
                videoDict.put(TAG_MAXDURATION, this.videoAdMaxDuration);
            }
        } catch (JSONException ex) {

        }
        return videoDict;
    }


    private JSONObject getSDKObject() {

        JSONObject sdk = new JSONObject();
        try {
            sdk.put(SOURCE, ANSDK);
            sdk.put(VERSION, Settings.getSettings().sdkVersion);

        } catch (JSONException e) {
        }
        return sdk;
    }

    private JSONObject getIABSupportObject() {
        JSONObject iabSupport = new JSONObject();
        try {
            iabSupport.put(OMIDPN, ANOmidViewabilty.OMID_PARTNER_NAME);
            iabSupport.put(OMIDPV, Settings.getSettings().sdkVersion);
        } catch (JSONException e) {
            Clog.e(Clog.httpReqLogTag, "IABSupportObject: " + e.getMessage());
        }
        return iabSupport;
    }


    private JSONObject getGDPRConsentObject() {
        JSONObject gdprConsent = new JSONObject();
        try {
            Context context = this.getContext();
            if (context != null) {
                Boolean consentRequired = ANGDPRSettings.getConsentRequired(context);
                // Only populate GDPR Consent object if Consent Required true/false. Otherwise treat it as unknown, impbus knows how to handle it.
                if (consentRequired != null) {
                    gdprConsent.put(GDPR_CONSENT_REQUIRED, consentRequired);
                    gdprConsent.put(GDPR_CONSENT_STRING, ANGDPRSettings.getConsentString(context));
                    JSONArray googleACMConsentStringJSONArray = ANGDPRSettings.getGoogleACMConsentStringJSONArray(context);
                    if(googleACMConsentStringJSONArray !=null){
                        gdprConsent.put(GDPR_GOOGLE_ACM_ADDTL_CONSENT, googleACMConsentStringJSONArray);
                    }
                }
            }
        } catch (JSONException e) {
        }
        return gdprConsent;
    }


    private JSONArray getCustomKeywordsArray
            (ArrayList<Pair<String, String>> customKeywords) {
        JSONArray keywords = new JSONArray();
        try {
            if (customKeywords != null) {
                for (Pair<String, String> pair : customKeywords) {
                    if (!StringUtil.isEmpty(pair.first) && !StringUtil.isEmpty(pair.second) && !updateIfKeyExists(pair.first, pair.second, keywords)) {
                        JSONObject key_val = new JSONObject();
                        key_val.put(KEYVAL_KEY, pair.first);
                        JSONArray val = new JSONArray();
                        val.put(pair.second);
                        key_val.put(KEYVAL_VALUE, val);
                        keywords.put(key_val);
                    }
                }
            }
        } catch (JSONException e) {
        }
        return keywords;
    }

    private boolean updateIfKeyExists(String key, String value, JSONArray keywords) throws
            JSONException {
        for (int i = 0; i < keywords.length(); i++) {
            JSONObject key_val = keywords.getJSONObject(i);
            if (key_val.getString(KEYVAL_KEY).equalsIgnoreCase(key)) {
                key_val.getJSONArray(KEYVAL_VALUE).put(value);
                return true;
            }
        }
        return false;
    }


    public void setForceCreativeId(int forceCreativeId) {
        this.forceCreativeId = forceCreativeId;
    }

    /**
     * @deprecated rendererId is not used anymore
     */
    public void setRendererId(int rendererId) {
        this.rendererId = rendererId;
    }

    /**
     * @deprecated rendererId is not used anymore
     */
    public int getRendererId() {
        return rendererId;
    }

    public void addAdUnit(WeakReference<Ad> adWeakReference) {
        adUnitList.add(adWeakReference);
    }

    public void removeAdUnit(Ad adUnit) {
        ArrayList<WeakReference<Ad>> tempAdUnitList = new ArrayList<>();
        tempAdUnitList.addAll(adUnitList);
        for (WeakReference<Ad> adWeakReference : tempAdUnitList) {
            if (adWeakReference.get() == adUnit) {
                adUnitList.remove(adWeakReference);
            }
        }
    }

    public ArrayList<WeakReference<Ad>> getAdUnitList() {
        Clog.e(Clog.SRMLogTag, "ADUNITLIST SIZE: " + adUnitList.size());
        return adUnitList;
    }

    public ANMultiAdRequest getMultiAdRequest() {
        return anMultiAdRequest.get();
    }

    public void associateWithMultiAdRequest(ANMultiAdRequest anMultiAdRequest) {
        this.anMultiAdRequest = new WeakReference<>(anMultiAdRequest);
    }

    public void disassociateFromMultiAdRequest() {
        this.anMultiAdRequest = new WeakReference<>(null);
    }

    public int getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }
}