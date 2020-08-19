package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.app

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.anjlab.android.iab.v3.BillingProcessor
import com.dev.bytes.adsmanager.InterAdPair
import com.dev.bytes.adsmanager.billing.initBilling
import com.dev.bytes.adsmanager.billing.productKey
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.BuildConfig
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.activity.MainActivity
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.LocaleManagerX
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.RemoteConfigUtils
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.Utility
import timber.log.Timber


class App : Application() {

    var splashInterstitial: InterAdPair? = null

    override fun onCreate() {
        super.onCreate()
        /*loadInterstitialAd(
            ADUnitPlacements.SPLASH_INTERSTITIAL,
            onLoaded = { splashInterstitial = it })*/
        initBP()

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

        RemoteConfigUtils.createConfigSettings().fetchAndActivate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun attachBaseContext(base: Context?) {
        localeManager = LocaleManagerX(base)
        super.attachBaseContext(base)
        Utility.bypassHiddenApiRestrictions()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeManager!!.setLocale(this)
        Log.d("TAG", "onConfigurationChanged: " + newConfig.locale.getLanguage())
    }

    companion object {

        var bp: BillingProcessor? = null
        var localeManager: LocaleManagerX? = null

        fun Context.initBP() {
            // TODO: 7/24/2020 in app purchase key put here ""DONE""
            bp = this.initBilling(
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnFTfnOYXD0r0dGjsW7+GR7pUkw6Gz3mrm3hXi5p2Yw/yTh5TJym4GxjqG5YwOWxzHEneo/BATWzJt6NUo3ZFNdli6vPkhNi1+nc5D3xPkULUlHAtchtXw6KC2bwGlMQveY2jZ2ODaaiDfoGKusqU0Snpw+k6gWLT3YuQhctvA3H5ftxI8BIOkdvkQUvV8isHg7VCwUlG2L0z0ZB4JTwzrbnAUYtyF+RcYgOVoI9ljkqeMAEGLy5epK4552H2/zQax88S+MnDxZY97LAD8idkJmZVSsj6y6L4z6gfp122DgLE4UZtzYrA3MwGr8JQ+sr45gn7fu7oaB6i3KC88AIhQwIDAQAB",
                { start(this) },
                {
                    onPurchased()
                })
        }

        fun Context.onPurchased() {
            val isPurchased = bp?.isPurchased(productKey) ?: false
            if (isPurchased) {
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