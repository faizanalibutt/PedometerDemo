package com.faizi.pedometerdemo.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.ui.ViewPagerAdapter
import com.faizi.pedometerdemo.ui.fragment.*
import kotlinx.android.synthetic.main.activity_pedometer.*
import kotlinx.android.synthetic.main.activity_pedometer.nav_back
import kotlinx.android.synthetic.main.activity_pedometer.tabView
import kotlinx.android.synthetic.main.activity_pedometer.viewPager
import kotlinx.android.synthetic.main.activity_speedometer.*

class SpeedometerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speedometer)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(AnalogFragment(this@SpeedometerActivity), "ANALOG")
        adapter.addFragment(DigitalFragment(), "DIGITAL")
        //adapter.addFragment(MapFragment(), "MAP")
        viewPager.adapter = adapter
        tabView.setupWithViewPager(viewPager)

        nav_back.setOnClickListener {
            finish()
        }

        speedo_graph.setOnClickListener {
            startActivity(Intent(this, SpeedoGraphActivity::class.java))
        }
    }

}