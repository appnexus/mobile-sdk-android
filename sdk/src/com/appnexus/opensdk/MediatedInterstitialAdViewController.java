package com.appnexus.opensdk;

import com.appnexus.opensdk.utils.Clog;

import android.app.Activity;
import android.view.View;

public class MediatedInterstitialAdViewController implements Displayable {
    InterstitialAdView owner;
    int width;
    int height;
    boolean failed = false;
    String uid;
    String className;
    String param;

    Class<?> c;
    MediatedInterstitialAdView mAV;

    static public MediatedInterstitialAdViewController create(InterstitialAdView owner, AdResponse response) {
        MediatedInterstitialAdViewController out;
        try {
            out = new MediatedInterstitialAdViewController(owner, response);
        } catch (Exception e) {
            return null;
        }
        return out;

    }

    private MediatedInterstitialAdViewController(InterstitialAdView owner, AdResponse response) throws Exception {
        width = response.getWidth();
        height = response.getHeight();
        uid = response.getMediatedUID();
        className = response.getMediatedViewClassName();
        param = response.getParameter();
        this.owner = owner;

        try {
            c = Class.forName(className);

        } catch (ClassNotFoundException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.class_not_found_exception));
            throw e;
        }

        try {
            mAV = (MediatedInterstitialAdView) c.newInstance();
        } catch (InstantiationException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.instantiation_exception));
            failed = true;
            throw e;
        } catch (IllegalAccessException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.illegal_access_exception));
            failed = true;
            throw e;
        }
    }

    protected void show() {
        if (mAV != null) {
            mAV.show();
        }
    }

    @Override
    public View getView() {
        return mAV.requestAd(this, (Activity) owner.getContext(), param, uid);
    }

    public void onAdLoaded() {
        if (owner.getAdListener() != null) {
            owner.getAdListener().onAdLoaded(owner);
        }
    }

    public void onAdFailed() {
        if (owner.getAdListener() != null) {
            owner.getAdListener().onAdRequestFailed(owner);
        }
        this.failed = true;
    }

    public void onAdExpanded() {
        if (owner.getAdListener() != null) {
            owner.getAdListener().onAdExpanded(owner);
        }
    }

    public void onAdCollapsed() {
        if (owner.getAdListener() != null) {
            owner.getAdListener().onAdCollapsed(owner);
        }
    }

    public void onAdClicked() {
        if (owner.getAdListener() != null) {
            owner.getAdListener().onAdClicked(owner);
        }
    }

    @Override
    public boolean failed() {
        return failed;
    }

}
