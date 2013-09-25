package com.appnexus.opensdk;

import android.app.Activity;
import android.view.View;
import com.appnexus.opensdk.utils.Clog;

public class MediatedInterstitialAdViewController extends MediatedAdViewController implements Displayable {

    static public MediatedInterstitialAdViewController create(InterstitialAdView owner, AdResponse response) {
        MediatedInterstitialAdViewController out;
        try {
            out = new MediatedInterstitialAdViewController(owner, response);
        } catch (Exception e) {
            return null;
        }

        return out;

    }

    protected MediatedInterstitialAdViewController(InterstitialAdView owner, AdResponse response) throws Exception {
        super(owner, response);

        if (this.mAV == null || !(this.mAV instanceof MediatedInterstitialAdView)) {
            throw new Exception(Clog.getString(R.string.instance_exception));
        }
    }


    protected void show() {
        if (mAV != null) {
            ((MediatedInterstitialAdView) mAV).show();
        }
    }

    //TODO: how come this is inconsistent with Banner controller? in banner controller we requestAd in the constructor, but for IADs we do that in getView
    //TODO: also we return null here; how to test if an interstitial returns an ad then?
    @Override
    public View getView() {
        Clog.d(Clog.mediationLogTag, Clog.getString(R.string.mediated_request));
        if (mAV == null) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_view_null));
            return null;
        }
        if (owner == null) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_owner_null));
            return null;
        }

        //TODO: refactor - this also depends on owner. what if owner is null? (for testing)
        try {
            ((MediatedInterstitialAdView) mAV).requestAd(this,
                    (Activity) owner.getContext(),
                    currentAd.getParam(),
                    currentAd.getId());
        } catch (Exception e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_request_exception), e);
            fail(RESULT.INVALID_REQUEST);
        } catch (Error e) {
            // catch errors. exceptions will be caught above.
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_request_error), e);
            fail(RESULT.MEDIATED_SDK_UNAVAILABLE);
        }

        return null;
    }

}
