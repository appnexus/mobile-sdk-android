package com.xandr.lazyloaddemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)
    }

    fun openBanner(view: View) {
        startActivity(Intent(this, BannerLazyLoadActivity::class.java))
    }

    fun openMAR(view: View) {
        startActivity(Intent(this, MARSettingsActivity::class.java))
    }
}