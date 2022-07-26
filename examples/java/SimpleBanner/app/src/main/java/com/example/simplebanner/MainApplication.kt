package com.example.simplebanner

import android.app.Application
import android.widget.Toast
import com.appnexus.opensdk.XandrAd

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        XandrAd.init(10094, this, true) {
            Toast.makeText(this, "Init Completed", Toast.LENGTH_SHORT).show()
        }
    }
}