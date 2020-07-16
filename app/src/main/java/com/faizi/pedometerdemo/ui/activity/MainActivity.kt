package com.faizi.pedometerdemo.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.dev.bytes.adsmanager.ADUnitPlacements
import com.dev.bytes.adsmanager.loadNativeAd
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.dialog.ExitDialogue
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

        navView.setNavigationItemSelectedListener(this)
        nav_menu.setOnClickListener(this)
        action_settings.setOnClickListener(this)
        action_rate_us.setOnClickListener(this)
        action_share.setOnClickListener(this)
        action_pro.setOnClickListener(this)
        dialog = showRateExitDialogue(this@MainActivity, false)
        loadNativeAd(ad_container_main, R.layout.ad_unified_main, ADUnitPlacements.MAIN_MM_NATIVE_AD)
    }

    fun openSpeedo(view: View) {
        startActivity(Intent(this@MainActivity, SpeedometerActivity::class.java))
    }

    fun openPedoMeter(view: View) {
        startActivity(Intent(this@MainActivity, PedometerActivity::class.java))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_pro -> {
                Toast.makeText(this, "Pro clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.nav_share -> {}
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.nav_menu -> drawerLayout.openDrawer(GravityCompat.START, true)
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.action_rate_us -> {}
            R.id.action_share -> {}
            R.id.action_pro -> {}
        }
    }

    override fun onBackPressed() {
        if (dialog == null || menuClicked) {
            menuClicked = false
            dialog = showRateExitDialogue(this@MainActivity, false)
        }
        dialog?.show()
    }

}