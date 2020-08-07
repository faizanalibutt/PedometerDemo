package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.dev.bytes.adsmanager.ADUnitPlacements
import com.dev.bytes.adsmanager.TinyDB.Companion.getInstance
import com.dev.bytes.adsmanager.billing.purchaseRemoveAds
import com.dev.bytes.adsmanager.loadNativeAd
import com.google.android.material.navigation.NavigationView
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.R
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.app.App
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.dialog.RemoveAdsDialog.Companion.show
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.AppUtils
import kotlinx.android.synthetic.main.content_main.*


class MainActivity :
    Activity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private var menuClicked: Boolean = false
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    private val OPEN_COUNT_FOR_PURCHASE_DIALOG = "open_count_for_remove_ads"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        if (getInstance(this).getBoolean(getString(com.dev.bytes.R.string.is_premium))) {
            nav_pro_group.visibility = View.GONE
            premium_services.visibility = View.GONE
            hideItem()
        }

        speedo_img.postDelayed({ setSpeedoRippleEffect() }, 200)

        showRemoveAdsDialogue()
        AppUtils.animateProButton(this, premium_services)

        navView.setNavigationItemSelectedListener(this)
        nav_menu.setOnClickListener(this)
        action_settings.setOnClickListener(this)
        action_rate_us.setOnClickListener(this)
        action_share.setOnClickListener(this)
        action_pro.setOnClickListener(this)
        premium_services.setOnClickListener(this)
        dialog = showRateExitDialogue(this@MainActivity, false)
        loadNativeAd(
            ad_container_main,
            R.layout.ad_unified_main,
            ADUnitPlacements.MAIN_MM_NATIVE_AD,
            true
        )
        (application as? App)?.splashInterstitial?.let { it1 ->
            if (it1.isLoaded())
                showMainLoadingAdDialog {
                    (application as? App)?.splashInterstitial?.let {
                        if (it.isLoaded()) it.showAd(
                            this
                        )
                    }
                }
        }

    }

    val xPivot : Float
        get() = speedo_view?.pivotX ?: 0f
    val yPivot : Float
        get() = (speedo_img?.pivotY ?: 0f) + resources.getDimension(R.dimen.dp_12)

    private fun setSpeedoRippleEffect() {

        speedo_view_effect.setOnRippleCompleteListener {
            speedo_view_effect.animateRipple(xPivot, yPivot)
        }
        speedo_view_effect.rippleColor = R.color.white
        speedo_view_effect.zoomScale = 2.0f
        speedo_view_effect.animateRipple(xPivot, yPivot)

        speedo_view_effect2.setOnRippleCompleteListener {
            speedo_view_effect2.animateRipple(xPivot, yPivot)
        }
        speedo_view_effect2.rippleColor = R.color.white_tab
        speedo_view_effect2.animateRipple(xPivot, yPivot)

        speedo_view_effect2.setOnClickListener {
            openSpeedo(it)
        }
    }

    private fun showRemoveAdsDialogue() {
        var openCount = getInstance(this).getInt(OPEN_COUNT_FOR_PURCHASE_DIALOG)
        getInstance(this).putInt(OPEN_COUNT_FOR_PURCHASE_DIALOG, ++openCount)

        if (!getInstance(this)
                .getBoolean(getString(R.string.is_premium)) && openCount % 2 != 0
        ) show(this)
    }

    private fun hideItem() {
        val nav_Menu: Menu = navView.menu
        nav_Menu.findItem(R.id.nav_pro).isVisible = false
    }

    fun openSpeedo(view: View) {
        startActivity(Intent(this@MainActivity, SpeedometerActivity::class.java))
    }

    fun openPedoMeter(view: View) {
        startActivity(Intent(this@MainActivity, PedometerActivity::class.java))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_pro -> App.bp?.purchaseRemoveAds(this)
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.nav_share -> shareIntent()
            R.id.nav_rate_us -> {
                dialog = null
                menuClicked = true
                dialog = showRateExitDialogue(this@MainActivity, true)
                dialog!!.show()
            }
            R.id.nav_privacy -> {
                val url = getString(R.string.privacy_policy_link_text)
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(url))
            }
            //R.id.nav_about -> {}
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!(App.bp!!.handleActivityResult(requestCode, resultCode, intent)))
            super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.nav_menu -> drawerLayout.openDrawer(GravityCompat.START, true)
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.action_rate_us -> {
                dialog = null
                menuClicked = true
                dialog = showRateExitDialogue(this@MainActivity, true)
                dialog!!.show()
            }
            R.id.action_share -> shareIntent()
            R.id.action_pro -> App.bp?.purchaseRemoveAds(this)
            R.id.premium_services -> App.bp?.purchaseRemoveAds(this)
        }
    }

    override fun onBackPressed() {
        if (dialog == null || menuClicked) {
            menuClicked = false
            dialog = showRateExitDialogue(this@MainActivity, false)
        }
        dialog?.show()
    }

    private fun shareIntent() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val shareSubText = this.resources.getString(R.string.great_app)
        // TODO: 7/24/2020 get app link ""DONE""
        val shareBodyText =
            this.resources.getString(R.string.use_one) + " https://play.google.com/store/apps/developer?id=" + packageName
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubText)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText)
        startActivity(
            Intent.createChooser(
                shareIntent,
                this.resources.getString(R.string.share_with)
            )
        )
    }

    fun Context.showMainLoadingAdDialog(loadingAdDialog: (() -> Unit)?) {
        val dialog: Dialog? = initCustomDialog(R.layout.splash_ad_loading_screen)
        dialog?.findViewById<View?>(R.id.main_loading_pb)?.visibility = View.VISIBLE
        dialog?.show()
        val handler = Handler()
        handler.postDelayed({
            try {
                dialog?.dismiss()
                loadingAdDialog?.invoke()
            } catch (e: Exception) {
            }
        }, 3000)
    }

    fun Context.initCustomDialog(layout: Int): Dialog? {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(layout)
        if (dialog.window != null) {
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            dialog.window!!.setBackgroundDrawable(
                resources.getDrawable(android.R.color.transparent)
            )
        }
        dialog.setCancelable(false)
        return dialog
    }


}