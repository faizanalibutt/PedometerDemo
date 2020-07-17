package com.faizi.pedometerdemo.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dev.bytes.adsmanager.ADUnitPlacements
import com.dev.bytes.adsmanager.InterAdPair
import com.dev.bytes.adsmanager.loadInterstitialAd
import com.dev.bytes.adsmanager.loadNativeAd
import com.faizi.pedometerdemo.ui.fragment.ReportFragment
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.ui.ViewPagerAdapter
import com.faizi.pedometerdemo.ui.fragment.PedoMeterFragmentNew
import kotlinx.android.synthetic.main.activity_pedometer.*

class PedometerActivity : AppCompatActivity() {

    var commonInterstitialAd: InterAdPair? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedometer)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(PedoMeterFragmentNew(), "TODAY")
        adapter.addFragment(ReportFragment(), "REPORT")
        viewPager.adapter = adapter
        tabView.setupWithViewPager(viewPager)

        nav_back.setOnClickListener {
            finish()
        }

        loadNativeAd(ad_container_pedo, R.layout.ad_unified_common, ADUnitPlacements.COMMON_NATIVE_AD)
        loadInterstitialAd(ADUnitPlacements.COMMON_INTERSTITIAL, onLoaded = { commonInterstitialAd = it } )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        commonInterstitialAd?.apply {
            if (this.isLoaded()) this.showAd(this@PedometerActivity)
        }
    }

}