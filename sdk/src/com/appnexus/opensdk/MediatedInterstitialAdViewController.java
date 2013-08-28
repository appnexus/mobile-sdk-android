package com.appnexus.opensdk;

import com.appnexus.opensdk.utils.Clog;

import android.app.Activity;
import android.view.View;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.HTTPGet;

public class MediatedInterstitialAdViewController extends MediatedAdViewController implements Displayable {



    static public MediatedInterstitialAdViewController create(InterstitialAdView owner, AdResponse response) {
        MediatedInterstitialAdViewController out;
        try {
            out = new MediatedInterstitialAdViewController(owner, response);
        } catch (Exception e) {
            return null;
        }
        if(out.mAV == null || !(out.mAV instanceof MediatedInterstitialAdView)){
            return null;
        }
        return out;

    }

    protected MediatedInterstitialAdViewController(InterstitialAdView owner, AdResponse response) throws Exception {
        super(owner, response);
    }


    protected void show() {
        if (mAV != null) {
            ((MediatedInterstitialAdView)mAV).show();
        }
    }

    @Override
    public View getView() {
        Clog.d(Clog.mediationLogTag, Clog.getString(R.string.mediated_request));
        ((MediatedInterstitialAdView)mAV).requestAd(this, (Activity) owner.getContext(), param, uid);
        return null;
    }

}
