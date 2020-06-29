package com.faizi.pedometerdemo.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.faizi.pedometerdemo.R
import com.google.android.material.navigation.NavigationView
import de.j4velin.pedometer.ui.Activity_Main
import kotlinx.android.synthetic.main.content_main.*

class MainActivity :
    AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

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
    }

    fun openSpeedo(view: View) {
        startActivity(Intent(this@MainActivity, MainSpeedometerActivity::class.java))
    }

    fun openPedoMeter(view: View) {
        startActivity(Intent(this@MainActivity, Activity_Main::class.java))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_pro -> {
                Toast.makeText(this, "Pro clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.nav_share -> {}
            R.id.nav_rate_us -> {}
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
            R.id.action_rate_us -> {
            }
            R.id.action_share -> {
            }
            R.id.action_pro -> {
            }
        }
    }

}