package com.faizi.pedometerdemo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dev.bytes.adsmanager.ADUnitPlacements
import com.dev.bytes.adsmanager.TinyDB
import com.dev.bytes.adsmanager.billing.purchaseRemoveAds
import com.dev.bytes.adsmanager.loadNativeAd
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.app.App
import com.faizi.pedometerdemo.ui.ViewPagerAdapter
import com.faizi.pedometerdemo.ui.fragment.DetailReportFragment
import kotlinx.android.synthetic.main.activity_pedometer.*
import kotlinx.android.synthetic.main.activity_pedometer.nav_back
import kotlinx.android.synthetic.main.activity_pedometer.tabView
import kotlinx.android.synthetic.main.activity_pedometer.viewPager
import kotlinx.android.synthetic.main.activity_speedo_graph.*
import kotlinx.android.synthetic.main.activity_speedo_graph.premium_services

class SpeedoGraphActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speedo_graph)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(DetailReportFragment("today"), "TODAY")
        adapter.addFragment(DetailReportFragment("week"), "LAST WEEk")
        viewPager.adapter = adapter
        tabView.setupWithViewPager(viewPager)

        nav_back.setOnClickListener {
            finish()
        }

        premium_services.setOnClickListener {
            App.bp?.purchaseRemoveAds(this)
        }

        if (TinyDB.getInstance(this).getBoolean(getString(com.dev.bytes.R.string.is_premium)))
            premium_services.visibility = View.GONE

        loadNativeAd(ad_container_graph, R.layout.ad_unified_common, ADUnitPlacements.COMMON_NATIVE_AD)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!(App.bp!!.handleActivityResult(requestCode, resultCode, intent)))
            super.onActivityResult(requestCode, resultCode, data)
    }
}