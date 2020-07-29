package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import com.dev.bytes.adsmanager.BannerPlacements
import com.dev.bytes.adsmanager.TinyDB
import com.dev.bytes.adsmanager.billing.purchaseRemoveAds
import com.dev.bytes.adsmanager.loadBannerAd
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.R
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.SensorListener
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.app.App
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.AppUtils
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.nav_back
import kotlinx.android.synthetic.main.activity_settings.premium_services

class SettingsActivity : Activity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        nav_back.setOnClickListener {
          finish()
        }

        auto_count_switch.isChecked = AppUtils.getDefaultPreferences(this).getString("pedo_state", null) == "stop"

        auto_count_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppUtils.getDefaultPreferences(this).edit().putString("pedo_state", "stop").apply()
                startService(Intent(this, SensorListener::class.java))
            } else {
                AppUtils.getDefaultPreferences(this).edit().putString("pedo_state", "resume").apply()
                stopService(Intent(this, SensorListener::class.java))
            }
        }

        change_language.setOnClickListener(this)
        privacy_policy.setOnClickListener(this)
        rate_us.setOnClickListener(this)
        share.setOnClickListener(this)
        premium.setOnClickListener(this)
        premium_services.setOnClickListener(this)

        ad_container_settings.loadBannerAd(BannerPlacements.BANNER_AD)

        if (TinyDB.getInstance(this).getBoolean(getString(com.dev.bytes.R.string.is_premium)))
        {
            premium_services.visibility = View.GONE
            premium_group.visibility = View.GONE
        }

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.change_language -> {}
            R.id.privacy_policy -> {
                val url = getString(R.string.privacy_policy_link_text)
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(url))
            }
            R.id.rate_us -> {
                dialog = null
                dialog = showRateExitDialogue(this@SettingsActivity, true)
                dialog!!.show()
            }
            R.id.premium -> App.bp?.purchaseRemoveAds(this)
            R.id.share -> {
                shareIntent()
            }
            R.id.premium_services -> App.bp?.purchaseRemoveAds(this)
        }
    }

    private fun shareIntent() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val shareSubText = this.resources.getString(R.string.great_app)
        // TODO: 7/24/2020 get app link ""DONE""
        val shareBodyText = this.resources.getString(R.string.use_one) + "https://play.google.com/store/apps/developer?id=" + packageName
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubText)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText)
        startActivity(Intent.createChooser(shareIntent, this.resources.getString(R.string.share_with)))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!(App.bp!!.handleActivityResult(requestCode, resultCode, intent)))
            super.onActivityResult(requestCode, resultCode, data)
    }
}