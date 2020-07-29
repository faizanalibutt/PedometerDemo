package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.dev.bytes.adsmanager.ADUnitPlacements
import com.dev.bytes.adsmanager.loadInterstitialAd
import com.dev.bytes.adsmanager.loadNativeAd
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.R
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.app.App
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.AppUtils
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.NetworkUtils
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.layout_gdp_view.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        getSplashViews()

        loadInterstitialAd(
            ADUnitPlacements.SPLASH_INTERSTITIAL,
            onLoaded = { (application as? App)?.splashInterstitial = it })
    }

    private fun getSplashViews() {
        val isFirstLaunch =
            AppUtils.getDefaultPreferences(this@SplashActivity)
                .getBoolean("is_First_Launch", false)
        if (!isFirstLaunch) {
            showGdpView()
        } else {
            showSplashView()
        }
    }

    private fun showGdpView() {

        appIconsplash.visibility = View.GONE
        progressBar.visibility = View.GONE
        layout_gdp.visibility = View.VISIBLE

        @Suppress("DEPRECATION")
        //this.tv_privacy.text = Html.fromHtml("<u>"+resources.getString(R.string.privacy_policy_translate)+"</u>")

        val colorPrimary = ContextCompat.getColor(this@SplashActivity, R.color.black)
        val spanString: SpannableString? = SpannableString(appname.text)
        val appColor = ForegroundColorSpan(colorPrimary)
        spanString!!.setSpan(appColor, 6, appname.text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        appname.text = spanString

        val text = SpannableString(getString(R.string.privacy_policy_desc))
        text.setSpan(UnderlineSpan(), 27, privacytext.length(), 0)
        text.setSpan(appColor, 27, privacytext.length(), 0)
        privacytext.text = text

        privacytext.setOnClickListener {
            val url =
                getString(R.string.privacy_policy_link_text)
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse(url))
        }

        AppUtils.getDefaultPreferences(this@SplashActivity).edit().putBoolean(
            "show_rating_dialog", true
        ).apply()

        startbutton.setOnClickListener {
            AppUtils.getDefaultPreferences(this@SplashActivity).edit().putBoolean(
                "is_First_Launch", true
            ).apply()
            showSplashView()
        }
    }

    private fun showSplashView() {

        appIconsplash.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        layout_gdp.visibility = View.GONE
        loadNativeAd(
            ad_container_splash,
            R.layout.ad_unified_splash,
            ADUnitPlacements.SPLASH_NATIVE_AD,
            true
        )

        val isOnline = NetworkUtils.isOnline(this@SplashActivity)
        val stuckLimit: Long = if (isOnline) 8000 else 3000
        var skip = 8
        Thread {
            for ((progress, _) in (1..if (isOnline) 8 else 3).withIndex()) {
                try {
                    Thread.sleep(1000)
                } catch (e: Exception) {
                }
                runOnUiThread {
                    progressBar.max = if (isOnline) 8 else 3
                    progressBar.progress = progress + 1
                    skip -= 1
                    skipText.text = String.format("Skip $skip")
                }
            }
        }.start()

        if (isOnline)
            showMain(stuckLimit)
        else
            showMain(stuckLimit)
    }

    private fun showMain(stuckLimit: Long) {
        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
//            (application as App).splashInterstitial?.apply {
//                if (this.isLoaded()) this.showAd(this@SplashActivity)
//            }
        }, stuckLimit)
    }

}
