package com.appnexus.opensdk.instreamvideo;


import android.content.Context;

public class MockVideoAdOwner extends VideoAd {

    VideoAdFetcher adFetcher;

    public MockVideoAdOwner(Context context) {
        super(context, "PLACEMENT_ID");
        adFetcher = new VideoAdFetcher(this);
    }

    @Override
    public boolean isReadyToStart() {
        return true;
    }


    public void startAdFetcher(){
        adFetcher.start();
    }
}
