package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import com.dev.bytes.adsmanager.ADUnitPlacements
import com.dev.bytes.adsmanager.InterAdPair
import com.dev.bytes.adsmanager.TinyDB
import com.dev.bytes.adsmanager.billing.purchaseRemoveAds
import com.dev.bytes.adsmanager.loadInterstitialAd
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.R
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.app.App
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.ViewPagerAdapter
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.fragment.PedoMeterFragmentNew
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.fragment.ReportFragment
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.AppUtils
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.activity_pedometer.*
import java.util.ArrayList

class PedometerActivity : Activity() {

    private var pedoMeterWorking: Boolean = false
    var startStopInterstitialAd: InterAdPair? = null
    var backInterstitialAd: InterAdPair? = null
    private var isStartStopShown: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedometer)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(PedoMeterFragmentNew(), getString(R.string.text_today))
        adapter.addFragment(ReportFragment(), getString(R.string.text_report))
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
        else
            AppUtils.animateProButton(this, premium_services)

        loadInterstitialAd(
            ADUnitPlacements.PEDO_START_STOP_INTERSTITIAL,
            onLoaded = { startStopInterstitialAd = it },
            reloadOnClosed = true
        )
        loadInterstitialAd(
            ADUnitPlacements.PEDO_BACK_INTERSTITIAL,
            onLoaded = { backInterstitialAd = it })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val permissions = arrayOf(Manifest.permission.ACTIVITY_RECOGNITION)
            val rationale =
                getString(R.string.text_recognition_permission)
            val options = Permissions.Options().setRationaleDialogTitle("Info")
                .setSettingsDialogTitle(getString(R.string.text_warning))

            Permissions.check(
                this,
                permissions,
                rationale,
                options,
                object : PermissionHandler() {
                    override fun onGranted() {
                        pedoMeterWorking = true
                    }

                    override fun onDenied(
                        context: Context?,
                        deniedPermissions: ArrayList<String>?
                    ) {
                        super.onDenied(context, deniedPermissions)
                        finish()
                    }
                })
        }
        textView.isSelected = true
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
        if ((App.bp?.handleActivityResult(requestCode, resultCode, intent))  != true)
        super.onActivityResult(requestCode, resultCode, data)
    }

}