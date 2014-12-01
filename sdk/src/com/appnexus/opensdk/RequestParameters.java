package com.appnexus.opensdk;

import android.content.Context;
import android.util.Pair;

import com.appnexus.opensdk.utils.StringUtil;

import java.util.ArrayList;

class RequestParameters {
    private MediaType mediaType;
    private String placementID;
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
    private ArrayList<InterstitialAdView.Size> allowedSizes;

    RequestParameters(Context context) {
        this.context = context;
    }

    String getPlacementID() {
        return placementID;
    }

    void setPlacementID(String placementID) {
        this.placementID = placementID;
    }

    Context getContext() {
        return this.context;
    }

    void setContext(Context context) {
        this.context = context;
    }

    void setAdWidth(int width) {
        this.width = width;
    }

    int getAdWidth() {
        if (mediaType == MediaType.BANNER) {
            return width;
        } else {
            return -1;
        }
    }

    void setAdHeight(int height) {
        this.height = height;
    }

    int getAdHeight() {
        if (mediaType == MediaType.BANNER) {
            return height;
        } else {
            return -1;
        }
    }

    void setOverrideMaxSize(boolean overrideMaxSize) {
        this.overrideMaxSize = overrideMaxSize;
    }

    boolean getOverrideMaxSize() {
        return mediaType == MediaType.BANNER && this.overrideMaxSize;
    }

    void setMaxSize(int maxW, int maxH) {
        this.maximumWidth = maxW;
        this.maximumHeight = maxH;
    }

    int getMaxWidth() {
        if (mediaType == MediaType.BANNER) {
            return this.maximumWidth;
        } else {
            return measuredWidth;
        }
    }

    int getMaxHeight() {
        if (mediaType == MediaType.BANNER) {
            return this.maximumHeight;
        } else {
            return measuredHeight;
        }
    }

    void setContainereWidth(int width) {
        this.measuredWidth = width;
    }

    int getContainerWidth() {
        return measuredWidth;
    }

    void setContainerHeight(int height) {
        this.measuredHeight = height;
    }

    int getContainerHeight() {
        return measuredHeight;
    }

    void setAllowedSizes(ArrayList<InterstitialAdView.Size> allowed_sizes) {
        this.allowedSizes = allowed_sizes;
    }

    ArrayList<InterstitialAdView.Size> getAllowedSizes() {
        if (mediaType == MediaType.INTERSTITIAL) {
            return allowedSizes;
        } else {
            return null;
        }
    }

    void setOpensNativeBrowser(boolean opensNativeBrowser) {
        this.opensNativeBrowser = opensNativeBrowser;
    }

    boolean getOpensNativeBrowser() {
        return opensNativeBrowser;
    }

    void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    MediaType getMediaType() {
        return mediaType;
    }

    void setReserve(float reserve) {
        this.reserve = reserve;
    }

    float getReserve() {
        return reserve;
    }

    void setAge(String age) {
        this.age = age;
    }

    String getAge() {
        return age;
    }

    void setGender(AdView.GENDER gender) {
        this.gender = gender;
    }

    AdView.GENDER getGender() {
        return gender;
    }

    void addCustomKeywords(String key, String value) {
        if (StringUtil.isEmpty(key) || (value == null)) {
            return;
        }
        this.customKeywords.add(new Pair<String, String>(key, value));
    }

    void removeCustomKeyword(String key) {
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

    void clearCustomKeywords(){
        customKeywords.clear();
    }

    ArrayList<Pair<String, String>> getCustomKeywords() {
        return customKeywords;
    }

    void setShouldServePSAs(boolean shouldServePSAs) {
        this.shouldServePSAs = shouldServePSAs;
    }

    public boolean getShouldServePSAs() {
        return shouldServePSAs;
    }

    TargetingParameters getTargetingParameters(){
        return new TargetingParameters(age, gender, customKeywords, SDKSettings.getLocation());
    }

}
