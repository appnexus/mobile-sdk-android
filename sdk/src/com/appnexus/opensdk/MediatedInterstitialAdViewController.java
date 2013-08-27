package com.appnexus.opensdk;

import com.appnexus.opensdk.utils.Clog;

import android.app.Activity;
import android.view.View;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.HTTPGet;

public class MediatedInterstitialAdViewController implements Displayable {

    public static enum RESULT{
        SUCCESS,
        INVALID_REQUEST,
        UNABLE_TO_FILL,
        MEDIATED_SDK_UNAVAILABLE,
        NETWORK_ERROR,
        INTERNAL_ERROR
    }

    InterstitialAdView owner;
    int width;
    int height;
    boolean failed = false;
    String uid;
    String className;
    String param;
    String resultCB;
    protected boolean errorCBMade=false;

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
        resultCB = response.getMediatedResultCB();
        this.owner = owner;

        Clog.d(Clog.mediationLogTag, Clog.getString(R.string.instantiating_class, className));

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
        Clog.d(Clog.mediationLogTag, Clog.getString(R.string.mediated_request));
        mAV.requestAd(this, (Activity) owner.getContext(), param, uid);
        return null;
    }

    public void onAdLoaded() {
        if (owner.getAdListener() != null) {
            owner.getAdListener().onAdLoaded(owner);
        }

        fireResultCB(RESULT.SUCCESS);
    }

    public void onAdFailed(MediatedInterstitialAdViewController.RESULT reason) {
        if (owner.getAdListener() != null) {
            owner.getAdListener().onAdRequestFailed(owner);
        }
        this.failed = true;

        if(!errorCBMade){
            fireResultCB(reason);
            errorCBMade=true;
        }

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

    private void fireResultCB(final MediatedInterstitialAdViewController.RESULT result){

        //fire call to result cb url
        HTTPGet<Void, Void, HTTPResponse> cb = new HTTPGet<Void, Void, HTTPResponse>() {
            @Override
            protected void onPostExecute(HTTPResponse response) {
                AdFetcher f = MediatedInterstitialAdViewController.this.owner.mAdFetcher;
                f.dispatchResponse(new AdResponse(f, response.getResponseBody(), response.getHeaders()));
            }

            @Override
            protected String getUrl() {
                return resultCB+"&reason="+result;
            }
        };

        cb.execute();
    }

}
