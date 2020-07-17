package com.faizi.pedometerdemo.app

import android.app.Application
import com.dev.bytes.adsmanager.ADUnitPlacements
import com.dev.bytes.adsmanager.InterAdPair
import com.dev.bytes.adsmanager.loadInterstitialAd

class App : Application() {

    var splashInterstitial: InterAdPair? = null

    override fun onCreate() {
        super.onCreate()
        loadInterstitialAd(
            ADUnitPlacements.SPLASH_INTERSTITIAL,
            onLoaded = { splashInterstitial = it })
    }

    companion object {
        val TAG = App::class.java.simpleName
    }
}