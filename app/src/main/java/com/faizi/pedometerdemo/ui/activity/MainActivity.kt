package com.faizi.pedometerdemo.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.dev.bytes.adsmanager.ADUnitPlacements
import com.dev.bytes.adsmanager.TinyDB
import com.dev.bytes.adsmanager.billing.purchaseRemoveAds
import com.dev.bytes.adsmanager.loadNativeAd
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.app.App
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_main.*


class MainActivity :
    Activity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private var menuClicked: Boolean = false
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        if (TinyDB.getInstance(this).getBoolean(getString(com.dev.bytes.R.string.is_premium))) {
            nav_pro_group.visibility = View.GONE
            premium_services.visibility = View.GONE
            hideItem()
        }

        navView.setNavigationItemSelectedListener(this)
        nav_menu.setOnClickListener(this)
        action_settings.setOnClickListener(this)
        action_rate_us.setOnClickListener(this)
        action_share.setOnClickListener(this)
        action_pro.setOnClickListener(this)
        premium_services.setOnClickListener(this)
        dialog = showRateExitDialogue(this@MainActivity, false)
        loadNativeAd(ad_container_main, R.layout.ad_unified_main, ADUnitPlacements.MAIN_MM_NATIVE_AD)
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
                val url = "https://www.freeprivacypolicy.com/privacy/view/50c5621471755f1548917ebbe5e90160"
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(url))
            }
            R.id.nav_about -> {}
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
        // TODO: 7/24/2020 get app link
        val shareBodyText = this.resources.getString(R.string.use_one) + "app_link" + packageName
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubText)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText)
        startActivity(Intent.createChooser(shareIntent, this.resources.getString(R.string.share_with)))
    }

}