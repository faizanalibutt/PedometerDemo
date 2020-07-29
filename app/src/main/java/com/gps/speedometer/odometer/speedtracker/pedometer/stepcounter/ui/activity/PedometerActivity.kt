package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dev.bytes.adsmanager.*
import com.dev.bytes.adsmanager.billing.purchaseRemoveAds
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.R
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.app.App
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.ViewPagerAdapter
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.fragment.PedoMeterFragmentNew
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.fragment.ReportFragment
import kotlinx.android.synthetic.main.activity_pedometer.*
import kotlinx.android.synthetic.main.activity_pedometer.premium_services
import kotlinx.android.synthetic.main.fragment_map.view.*

class PedometerActivity : AppCompatActivity() {

    var startStopInterstitialAd: InterAdPair? = null
    var backInterstitialAd: InterAdPair? = null
    private var isStartStopShown: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedometer)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(PedoMeterFragmentNew(), "TODAY")
        adapter.addFragment(ReportFragment(), "REPORT")
        viewPager.adapter = adapter
        tabView.setupWithViewPager(viewPager)

        premium_services.setOnClickListener {
            App.bp?.purchaseRemoveAds(this)
        }

        nav_back.setOnClickListener {
            onBackPressed()
        }

        if (TinyDB.getInstance(this).getBoolean(getString(com.dev.bytes.R.string.is_premium)))
            premium_services.visibility = View.GONE

        loadInterstitialAd(
            ADUnitPlacements.PEDO_START_STOP_INTERSTITIAL,
            onLoaded = { startStopInterstitialAd = it },
            reloadOnClosed = true
        )
        loadInterstitialAd(
            ADUnitPlacements.PEDO_BACK_INTERSTITIAL,
            onLoaded = { backInterstitialAd = it })
    }

    fun showStartStopInter() {
        startStopInterstitialAd?.apply {
            if (this.isLoaded()) this.showAd(this@PedometerActivity, true)
                .let { isStartStopShown = it }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backInterstitialAd?.apply {
            if (this.isLoaded() && !isStartStopShown)
                this.showAd(this@PedometerActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!(App.bp!!.handleActivityResult(requestCode, resultCode, intent)))
            super.onActivityResult(requestCode, resultCode, data)
    }

}