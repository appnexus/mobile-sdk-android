package com.appnexus.example.simplevideo;

import android.app.Application;
import android.widget.Toast;

import com.appnexus.opensdk.InitListener;
import com.appnexus.opensdk.XandrAd;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XandrAd.init(10094, this, true, new InitListener() {
            @Override
            public void onInitFinished(boolean success) {
                Toast.makeText(getApplicationContext(), "Init Completed with " + success, Toast.LENGTH_SHORT).show();
            }
        });

    }
}