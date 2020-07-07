package com.faizi.pedometerdemo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.ui.ViewPagerAdapter
import com.faizi.pedometerdemo.ui.fragment.DetailReportFragment
import kotlinx.android.synthetic.main.activity_pedometer.*

class SpeedoGraphActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speedo_graph)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(DetailReportFragment("today"), "TODAY")
        adapter.addFragment(DetailReportFragment("week"), "LAST WEEk")
        viewPager.adapter = adapter
        tabView.setupWithViewPager(viewPager)

        nav_back.setOnClickListener {
            finish()
        }

    }
}