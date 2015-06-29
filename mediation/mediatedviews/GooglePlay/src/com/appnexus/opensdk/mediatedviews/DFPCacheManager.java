/*
 *    Copyright 2015 APPNEXUS INC
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

package com.appnexus.opensdk.mediatedviews;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.utils.StringUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * This class is to pre-cache DFP views.
 */
public class DFPCacheManager {
    private HashMap<CacheEntry, ViewEntry> cache;
    private WeakReference<Context> weakContext;

    private boolean cacheEnabled;

    private static final long EXPIRATION_MILLIS = 60 * 1000; // 60 seconds
    private static DFPCacheManager instance;

    private DFPCacheManager(Context context) {
        cacheEnabled = false;
        weakContext = new WeakReference<Context>(context);
    }

    // Global Targeting settings for pre-cache
    private static String age;
    private static AdView.GENDER gender = AdView.GENDER.UNKNOWN;
    private static Location location;
    private static ArrayList<Pair<String, String>> customKeywords = new ArrayList<Pair<String, String>>();

    /**
     * Get the age for targeting
     *
     * @return age
     */
    public static String getAge() {
        return age;
    }

    /**
     * Set the age for targeting
     *
     * @param age
     */
    public static void setAge(String age) {
        DFPCacheManager.age = age;
    }

    /**
     * Get the current user's gender, if it's available.  The default value is UNKNOWN.
     *
     * @return The user's gender.
     */
    public static AdView.GENDER getGender() {
        return gender;
    }

    /**
     * Set the user's gender.  This should be set if the user's gender is known, as it
     * can help make buying the ad space more attractive to advertisers.  The default
     * value is UNKNOWN.
     *
     * @param gender The user's gender.
     */
    public static void setGender(AdView.GENDER gender) {
        DFPCacheManager.gender = gender;
    }

    /**
     * Retrieve the location that's set by setLocation()
     *
     * @return null if location was not set
     */
    public static Location getLocation() {
        return location;
    }

    /**
     * Set the location for DFP to target
     *
     * @param location location of the user
     */
    public static void setLocation(Location location) {
        DFPCacheManager.location = location;
    }

    /**
     * Retrieve the array of custom keywords associated with the DFPCacheManager.
     *
     * @return The current list of key-value pairs of custom keywords.
     */
    public static ArrayList<Pair<String, String>> getCustomKeywords() {
        return customKeywords;
    }

    /**
     * Remove a custom keyword from the DFPCacheManager. Use this to remove a keyword
     * previously set using addCustomKeywords.
     *
     * @param key The key to remove; this should not be null or empty.
     */
    public static void removeCustomKeyword(String key) {
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

    /**
     * Add a custom keyword to the DFPCacheManager. This is used to set custom targeting
     * parameters for DFP.
     *
     * @param key   The key to add; this cannot be null or empty.
     * @param value The value to add; this cannot be null or empty.
     */
    public static void addCustomKeyword(String key, String value) {
        if (StringUtil.isEmpty(key) || value == null)
            return;
        customKeywords.add(new Pair<String, String>(key, value));
    }

    /**
     * Clear all custom keywords from the DFPCacheManager.
     */
    public static void clearCustomKeywords() {
        customKeywords.clear();
    }

    /**
     * Method to get the instance of the DFP cache manager
     *
     * @param context The context where the instance is asked
     * @return an instance of DFP cache manager
     */
    public static DFPCacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new DFPCacheManager(context);
        }
        return instance;
    }

    /**
     * Call this method to reset pre-cache manager for DFP.
     */
    public void resetCacheManager() {
        cacheEnabled = false;
        clearCache();
    }

    /**
     * Method to cache an ad for a particular width and height.
     *
     * @param ID DFP ad unit id
     * @param w  width of the cached ad view
     * @param h  height of the cached ad view
     */
    public void cacheBannerForSize(String ID, int w, int h) {
        CacheEntry entry = new CacheEntry(ID, w, h);
        cache(entry);
    }

    /**
     * Method to cache a smart banner for an ad unit id.
     *
     * @param ID DFP ad unit id
     */
    public void cacheSmartBanner(String ID) {
        CacheEntry entry = new CacheEntry(ID);
        cache(entry);
    }

    // Package only methods for adapter to use

    /**
     * For adapter to get cache for a particular id, width, height if there is one.
     *
     * @param ID DFP ad unit id
     * @param w  width of the cached ad view
     * @param h  height of the cached ad view
     * @return null if no available cached ad view
     */
    ViewEntry popBannerForSize(String ID, int w, int h) {
        CacheEntry entry = new CacheEntry(ID, w, h);
        return pop(entry);

    }

    /**
     * For adapter to get smart banner cache for an ad unit id.
     *
     * @param ID DFP ad unit id
     * @return null if no available cached ad view
     */
    ViewEntry popSmartBanner(String ID) {
        CacheEntry entry = new CacheEntry(ID);
        return pop(entry);
    }


    /**
     * For adapter to check if cache is enabled.
     *
     * @return true if cache is enabled
     */
    boolean isCacheEnabled() {
        return cacheEnabled;
    }

    // private helper methods for DFPCacheManager

    private void cache(CacheEntry entry) {
        cacheEnabled = true;
        if (cache == null) {
            createCache();
        }

        if (cache.containsKey(entry)) {
            ViewEntry viewEntry = (ViewEntry) cache.get(entry);
            if (viewEntry.expired()) {
                delete(entry);
                cache(entry);
            }
        } else {
            Context context = (Context) this.weakContext.get();
            if (context != null) {
                ViewEntry view = new ViewEntry(entry, context);
                view.load();
                cache.put(entry, view);
            }
        }
    }

    private ViewEntry pop(CacheEntry entry) {
        if (cacheEnabled && cache != null) {
            if (cache.containsKey(entry)) {
                ViewEntry viewEntry = cache.remove(entry);
                cache(entry);
                if (!viewEntry.expired()) {
                    return viewEntry;
                } else {
                    viewEntry.destroy();
                }
            }
        }
        return null;
    }

    private PublisherAdRequest buildRequest() {
        PublisherAdRequest.Builder builder = new PublisherAdRequest.Builder();

        if (gender != null) {
            switch (gender) {
                case UNKNOWN:
                    builder.setGender(PublisherAdRequest.GENDER_UNKNOWN);
                    break;
                case FEMALE:
                    builder.setGender(PublisherAdRequest.GENDER_FEMALE);
                    break;
                case MALE:
                    builder.setGender(PublisherAdRequest.GENDER_MALE);
                    break;
            }
        }

        Bundle bundle = new Bundle();

        if (!StringUtil.isEmpty(age)) {
            bundle.putString("Age", age);
        }
        if (location != null) {
            builder.setLocation(location);
        }

        if (customKeywords != null) {
            for (Pair<String, String> p : customKeywords) {
                bundle.putString(p.first, p.second);
            }
        }

        builder.addNetworkExtras(new AdMobExtras(bundle));

        builder.setManualImpressionsEnabled(true);

        return builder.build();
    }

    private void createCache() {
        cache = new HashMap<CacheEntry, ViewEntry>();
    }

    private void clearCache() {
        if (cache != null) {
            Set<CacheEntry> keys = cache.keySet();
            for (CacheEntry key : keys) {
                ViewEntry value = cache.get(key);
                if (value != null) {
                    value.destroy();
                }
            }
            cache.clear();
            cache = null;
        }
    }

    private void delete(CacheEntry entry) {
        if (cache != null) {
            ViewEntry value = cache.remove(entry);
            value.destroy();
        }
    }


    class CacheEntry {
        private String ID;
        private AdSize size;
        private boolean isSmartBanner;

        CacheEntry(String ID, int w, int h) {
            this.ID = ID;
            this.isSmartBanner = false;
            this.size = new AdSize(w, h);
        }

        CacheEntry(String ID) {
            this.ID = ID;
            this.isSmartBanner = true;
            this.size = AdSize.SMART_BANNER;
        }

        public String getID() {
            return ID;
        }

        public AdSize getSize() {
            return size;
        }

        @Override
        public boolean equals(Object o) {
            try {
                CacheEntry toBeCompared = (CacheEntry) o;
                if (this.isSmartBanner == toBeCompared.isSmartBanner) {
                    if (isSmartBanner) {
                        return this.ID.equals(toBeCompared.getID());
                    } else {
                        return (this.ID.equals(toBeCompared.getID())) &&
                                (this.size.getHeight() == toBeCompared.getSize().getHeight()) &&
                                (this.size.getWidth() == toBeCompared.getSize().getWidth());
                    }
                } else {
                    return false;
                }
            } catch (ClassCastException e) {
                return false;
            }
        }

        @Override
        public int hashCode() {
            String s;
            if (isSmartBanner) {
                s = ID + "true";
            } else {
                s = ID + size.getWidth() + size.getHeight() + "false";
            }
            return s.hashCode();
        }
    }

    class ViewEntry extends AdListener {
        private PublisherAdView adView;
        private CacheState state;
        private long timeCreated;
        private AdListener forwardingListener = null;

        ViewEntry(CacheEntry entry, Context context) {
            state = CacheState.Loading;
            adView = new PublisherAdView(context);
            adView.setAdUnitId(entry.getID());
            adView.setAdSizes(entry.getSize());
            adView.setAdListener(this);
            timeCreated = System.currentTimeMillis();
        }

        public void load() {
            adView.loadAd(buildRequest());
        }


        public void destroy() {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    adView.destroy();
                    adView = null;
                    forwardingListener = null;
                }
            });
        }

        public boolean expired() {
            return (System.currentTimeMillis() - timeCreated) > EXPIRATION_MILLIS;
        }

        public PublisherAdView getView() {
            return adView;
        }

        public CacheState getState() {
            return state;
        }

        public void addForwardingListener(AdListener listener) {
            forwardingListener = listener;
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            state = CacheState.Loaded;
            if (forwardingListener != null) {
                adView.recordManualImpression();
                forwardingListener.onAdLoaded();
            }
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            super.onAdFailedToLoad(errorCode);
            state = CacheState.Failed;
            if (forwardingListener != null) {
                forwardingListener.onAdFailedToLoad(errorCode);
            }
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            if (forwardingListener != null) {
                forwardingListener.onAdClosed();
            }
        }

        @Override
        public void onAdLeftApplication() {
            super.onAdLeftApplication();
            if (forwardingListener != null) {
                forwardingListener.onAdLeftApplication();
            }
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
            if (forwardingListener != null) {
                forwardingListener.onAdOpened();
            }
        }
    }

    enum CacheState {
        Loading,
        Loaded,
        Failed
    }

}
