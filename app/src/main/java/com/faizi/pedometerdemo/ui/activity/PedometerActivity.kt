package com.faizi.pedometerdemo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dev.bytes.adsmanager.*
import com.dev.bytes.adsmanager.billing.purchaseRemoveAds
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.app.App
import com.faizi.pedometerdemo.ui.ViewPagerAdapter
import com.faizi.pedometerdemo.ui.fragment.PedoMeterFragmentNew
import com.faizi.pedometerdemo.ui.fragment.ReportFragment
import kotlinx.android.synthetic.main.activity_pedometer.*
import kotlinx.android.synthetic.main.activity_pedometer.premium_services
import kotlinx.android.synthetic.main.content_main.*

class PedometerActivity : AppCompatActivity() {

    private var commonInterstitialAd: InterAdPair? = null

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
            finish()
            commonInterstitialAd?.apply {
                if (this.isLoaded()) this.showAd(this@PedometerActivity)
            }
        }

        if (TinyDB.getInstance(this).getBoolean(getString(com.dev.bytes.R.string.is_premium)))
            premium_services.visibility = View.GONE

        loadNativeAd(
            ad_container_pedo,
            R.layout.ad_unified_common,
            ADUnitPlacements.COMMON_NATIVE_AD
        )
        loadInterstitialAd(
            ADUnitPlacements.COMMON_INTERSTITIAL,
            onLoaded = { commonInterstitialAd = it })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        commonInterstitialAd?.apply {
            if (this.isLoaded()) this.showAd(this@PedometerActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!(App.bp!!.handleActivityResult(requestCode, resultCode, intent)))
            super.onActivityResult(requestCode, resultCode, data)
    }

}