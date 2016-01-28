package com.appnexus.opensdk.mediatedviews;

import com.appnexus.opensdk.MediatedAdViewController;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMAdViewListener;
import com.rfm.sdk.RFMInterstitialAdViewListener;

/**
 * Created by ramit on 28/01/16.
 */
public class RubiconListener implements RFMAdViewListener, RFMInterstitialAdViewListener{

    private final MediatedAdViewController mediatedAdViewController;
    private final String className;

    public RubiconListener(MediatedBannerAdViewController mediatedAdViewController, String className) {
        this.mediatedAdViewController = mediatedAdViewController;
        this.className = className;
    }

    @Override
    public void onAdRequested(RFMAdView rfmAdView, String s, boolean b) {
        if(this.mediatedAdViewController != null){
        }
    }

    @Override
    public void onAdReceived(RFMAdView rfmAdView) {
        if(this.mediatedAdViewController != null){
            this.mediatedAdViewController.onAdLoaded();
        }
    }

    @Override
    public void onAdFailed(RFMAdView rfmAdView) {
        if(this.mediatedAdViewController != null){
            this.mediatedAdViewController.onAdFailed(ResultCode.UNABLE_TO_FILL);
        }
    }

    @Override
    public void onAdStateChangeEvent(RFMAdView rfmAdView, RFMAdViewEvent rfmAdViewEvent) {
        switch(rfmAdViewEvent){
            case FULL_SCREEN_AD_DISPLAYED:
                if(this.mediatedAdViewController != null){
                    this.mediatedAdViewController.onAdExpanded();
                }
                break;
            case FULL_SCREEN_AD_DISMISSED:
                if(this.mediatedAdViewController != null){
                    this.mediatedAdViewController.onAdCollapsed();
                }
                break;
        }

    }

    @Override
    public void onAdResized(RFMAdView rfmAdView, int i, int i1) {
        if(this.mediatedAdViewController != null){

        }
    }

    @Override
    public void didDisplayAd(RFMAdView rfmAdView) {
        if(this.mediatedAdViewController != null){

        }
    }

    @Override
    public void didFailedToDisplayAd(RFMAdView rfmAdView, String s) {
        if(this.mediatedAdViewController != null){

        }
    }

    @Override
    public void onInterstitialAdWillDismiss(RFMAdView rfmAdView) {
        if(this.mediatedAdViewController != null){

        }
    }

    @Override
    public void onInterstitialAdDismissed(RFMAdView rfmAdView) {
        if(this.mediatedAdViewController != null){
            this.mediatedAdViewController.onDestroy();
        }
    }
}
