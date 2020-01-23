package com.appnexus.opensdk.instreamvideo;


import android.content.Context;

import com.appnexus.opensdk.AdFetcher;

public class MockVideoAdOwner extends VideoAd {

    AdFetcher adFetcher;

    public MockVideoAdOwner(Context context) {
        super(context, "PLACEMENT_ID");
        adFetcher = new AdFetcher(this);
    }

    @Override
    public boolean isReadyToStart() {
        return true;
    }


    public void startAdFetcher(){
        adFetcher.start();
    }
}
