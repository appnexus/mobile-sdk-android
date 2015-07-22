package com.appnexus.opensdk.mediatedviews;

import android.app.Activity;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.ViewUtil;
import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdBanner;
import com.flurry.android.ads.FlurryAdBannerListener;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdTargeting;

import java.lang.ref.WeakReference;

public class YahooFlurryBanner implements MediatedBannerAdView, FlurryAdBannerListener {
    private WeakReference<MediatedBannerAdViewController> controller;
    private WeakReference<Activity> activityWeak;
    private RelativeLayout mBanner;
    private FlurryAdBanner flurryAd;

    @Override
    public void requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid, int width, int height, TargetingParameters tp) {
        if (mBC != null) {
            if (activity != null) {
                this.controller = new WeakReference<MediatedBannerAdViewController>(mBC);
                this.activityWeak = new WeakReference<Activity>(activity);
                mBanner = new RelativeLayout(activity);
                DisplayMetrics dp = activity.getResources().getDisplayMetrics();
                float density = dp.density;
                mBanner.setLayoutParams(new ViewGroup.LayoutParams((int) (width * density), (int) (height * density)));
                mBC.setView(mBanner);
                flurryAd = new FlurryAdBanner(activity, mBanner, uid);
                flurryAd.setTargeting(YahooFlurrySettings.getFlurryAdTargeting(tp));
                flurryAd.setListener(this);
                flurryAd.fetchAd();
            } else {
                mBC.onAdFailed(ResultCode.INTERNAL_ERROR);
            }

        }
    }

    @Override
    public void destroy() {
        if (flurryAd != null) {
            flurryAd.setListener(null);
            flurryAd.destroy();
            flurryAd = null;
            ViewUtil.removeChildFromParent(mBanner);
        }
    }

    @Override
    public void onPause() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Activity activity = this.activityWeak.get();
            if (activity != null) {
                FlurryAgent.onEndSession(activity);
            }
        }
    }

    @Override
    public void onResume() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Activity activity = this.activityWeak.get();
            if (activity != null) {
                FlurryAgent.onStartSession(activity);
            }
        }
    }

    @Override
    public void onDestroy() {
        destroy();
    }

    @Override
    public void onFetched(FlurryAdBanner flurryAdBanner) {
        if (flurryAd != null && flurryAd.isReady()) {
            flurryAd.displayAd();
        }
    }

    @Override
    public void onRendered(FlurryAdBanner flurryAdBanner) {
        MediatedBannerAdViewController controller = this.controller.get();
        if (controller != null) {
            controller.onAdLoaded();
        }
    }

    @Override
    public void onShowFullscreen(FlurryAdBanner flurryAdBanner) {
        MediatedBannerAdViewController controller = this.controller.get();
        if (controller != null) {
            controller.onAdExpanded();
        }

    }

    @Override
    public void onCloseFullscreen(FlurryAdBanner flurryAdBanner) {
        MediatedBannerAdViewController controller = this.controller.get();
        if (controller != null) {
            controller.onAdCollapsed();
        }
    }

    @Override
    public void onAppExit(FlurryAdBanner flurryAdBanner) {

    }

    @Override
    public void onClicked(FlurryAdBanner flurryAdBanner) {
        MediatedBannerAdViewController controller = this.controller.get();
        if (controller != null) {
            controller.onAdClicked();
        }
    }

    @Override
    public void onVideoCompleted(FlurryAdBanner flurryAdBanner) {

    }

    @Override
    public void onError(FlurryAdBanner flurryAdBanner, FlurryAdErrorType flurryAdErrorType, int i) {
        MediatedBannerAdViewController controller = this.controller.get();
        if (controller != null) {
            controller.onAdFailed(YahooFlurrySettings.errorCodeMapping(flurryAdErrorType, i));
        }
    }
}
