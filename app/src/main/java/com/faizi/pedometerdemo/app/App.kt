package com.faizi.pedometerdemo.app

import android.app.Application
import android.content.Context
import android.content.Intent
import com.anjlab.android.iab.v3.BillingProcessor
import com.dev.bytes.adsmanager.ADUnitPlacements
import com.dev.bytes.adsmanager.InterAdPair
import com.dev.bytes.adsmanager.billing.initBilling
import com.dev.bytes.adsmanager.billing.purchaseRemoveAds
import com.dev.bytes.adsmanager.billing.setPremium
import com.dev.bytes.adsmanager.loadInterstitialAd
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.ui.activity.MainActivity

class App : Application() {

    var splashInterstitial: InterAdPair? = null

    override fun onCreate() {
        super.onCreate()
        loadInterstitialAd(
            ADUnitPlacements.SPLASH_INTERSTITIAL,
            onLoaded = { splashInterstitial = it })
        initBP()
    }

    companion object {

        var bp: BillingProcessor? = null

        fun Context.initBP() {
            // TODO: 7/24/2020 in app purchase key put here
            bp = this.initBilling("", { onPurchased() }, {
                onPurchased()
            })
        }

        fun Context.onPurchased() {
            if (bp?.isPurchased(getString(R.string.remove_ads_key)) == true)
            {
                setPremium()
                // TODO: 7/24/2020 restart app
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }
}