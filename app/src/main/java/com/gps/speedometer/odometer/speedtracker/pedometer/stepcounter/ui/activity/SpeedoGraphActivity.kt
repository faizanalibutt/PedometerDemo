package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dev.bytes.adsmanager.ADUnitPlacements
import com.dev.bytes.adsmanager.TinyDB
import com.dev.bytes.adsmanager.billing.purchaseRemoveAds
import com.dev.bytes.adsmanager.loadNativeAd
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.R
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.app.App
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.ViewPagerAdapter
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.fragment.DetailReportFragment
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.AppUtils
import kotlinx.android.synthetic.main.activity_pedometer.*
import kotlinx.android.synthetic.main.activity_pedometer.nav_back
import kotlinx.android.synthetic.main.activity_pedometer.tabView
import kotlinx.android.synthetic.main.activity_pedometer.viewPager
import kotlinx.android.synthetic.main.activity_speedo_graph.*
import kotlinx.android.synthetic.main.activity_speedo_graph.premium_services

class SpeedoGraphActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speedo_graph)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(DetailReportFragment("today"), "TODAY")
        adapter.addFragment(DetailReportFragment("week"), "LAST WEEk")
        viewPager.adapter = adapter
        tabView.setupWithViewPager(viewPager)

        nav_back.setOnClickListener {
            //startActivity(Intent(this, SpeedometerActivity::class.java))
            finish()
        }

        premium_services.setOnClickListener {
            App.bp?.purchaseRemoveAds(this)
        }

        if (TinyDB.getInstance(this).getBoolean(getString(com.dev.bytes.R.string.is_premium)))
            premium_services.visibility = View.GONE
        else
            AppUtils.animateProButton(this, premium_services)

        loadNativeAd(ad_container_graph, R.layout.ad_unified_common, ADUnitPlacements.COMMON_NATIVE_AD, true)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!(App.bp!!.handleActivityResult(requestCode, resultCode, intent)))
            super.onActivityResult(requestCode, resultCode, data)
    }
}