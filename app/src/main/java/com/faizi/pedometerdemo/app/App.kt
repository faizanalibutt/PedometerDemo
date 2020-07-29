package com.faizi.pedometerdemo.app

import android.app.Application
import android.content.Context
import android.content.Intent
import com.anjlab.android.iab.v3.BillingProcessor
import com.dev.bytes.adsmanager.ADUnitPlacements
import com.dev.bytes.adsmanager.InterAdPair
import com.dev.bytes.adsmanager.billing.initBilling
import com.dev.bytes.adsmanager.billing.productKey
import com.dev.bytes.adsmanager.loadInterstitialAd
import com.faizi.pedometerdemo.BuildConfig
import com.faizi.pedometerdemo.app.App.Companion.initBP
import com.faizi.pedometerdemo.ui.activity.MainActivity
import com.faizi.pedometerdemo.util.RemoteConfigUtils
import timber.log.Timber

class App : Application() {

    var splashInterstitial: InterAdPair? = null

    override fun onCreate() {
        super.onCreate()
        loadInterstitialAd(
            ADUnitPlacements.SPLASH_INTERSTITIAL,
            onLoaded = { splashInterstitial = it })
        initBP()

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

        RemoteConfigUtils.createConfigSettings().fetchAndActivate()
    }

    companion object {

        var bp: BillingProcessor? = null

        fun Context.initBP() {
            // TODO: 7/24/2020 in app purchase key put here
            bp = this.initBilling("brfbfghfghfuyr", { start(this) }, {
                onPurchased()
            })
        }

        fun Context.onPurchased() {
            val isPurchased = bp?.isPurchased(productKey) ?: false
            if (isPurchased) {
//                setPremium()
                // TODO: 7/24/2020 restart app
                start(this)
            }
        }

        fun start(context: Context) {
            val mainIntent = Intent(context, MainActivity::class.java)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(mainIntent)
        }
    }
}