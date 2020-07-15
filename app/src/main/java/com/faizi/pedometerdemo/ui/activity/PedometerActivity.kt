package com.faizi.pedometerdemo.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.ui.fragment.ReportFragment
import com.faizi.pedometerdemo.ui.ViewPagerAdapter
import com.faizi.pedometerdemo.ui.fragment.PedoMeterFragmentNew
import kotlinx.android.synthetic.main.activity_pedometer.*

class PedometerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedometer)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(PedoMeterFragmentNew(), "TODAY")
        //adapter.addFragment(ReportFragment(), "REPORT")
        viewPager.adapter = adapter
        tabView.setupWithViewPager(viewPager)

        nav_back.setOnClickListener {
            finish()
        }
    }

}