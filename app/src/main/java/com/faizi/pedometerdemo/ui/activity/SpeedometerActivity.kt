package com.faizi.pedometerdemo.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.dev.bytes.adsmanager.*
import com.faizi.pedometerdemo.Database
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.callback.Callback
import com.faizi.pedometerdemo.model.Distance
import com.faizi.pedometerdemo.ui.ViewPagerAdapter
import com.faizi.pedometerdemo.ui.fragment.AnalogFragment
import com.faizi.pedometerdemo.ui.fragment.DigitalFragment
import com.faizi.pedometerdemo.ui.fragment.MapFragment
import com.faizi.pedometerdemo.ui.vm.SpeedViewModel
import com.faizi.pedometerdemo.util.AppUtils
import com.faizi.pedometerdemo.util.CurrentLocation
import com.faizi.pedometerdemo.util.TimeUtils
import com.google.android.material.tabs.TabLayout
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.activity_speedometer.*
import kotlinx.android.synthetic.main.fragment_analog.view.*
import java.util.*
import java.util.concurrent.TimeUnit

class SpeedometerActivity : AppCompatActivity(), CurrentLocation.LocationResultListener {

    private var speed: Double = 0.0
    private var handler: Handler? = null
    private var updateTimerThread: Runnable? = null
    var totalTime: Long = 0
    var startTime: Long = 0
    var endTime: Long = 0
    var paused = false
    private var distance: Double = 0.0
    private var maxSpeed: Double = 0.0
    private var avgSpeed: Double = 0.0
    private var isStop: Boolean = false

    private var currentLocation: CurrentLocation? = null
    private var mCurrentLocation: Location? = null
    var lStart: Location? = null
    var lEnd: Location? = null
    var commonInterstitialAd: InterAdPair? = null
    var unitType = "km"

    var mViewModel: SpeedViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speedometer)

        mViewModel = ViewModelProviders.of(this).get(SpeedViewModel::class.java)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(AnalogFragment(this@SpeedometerActivity), "ANALOG")
        adapter.addFragment(DigitalFragment(this@SpeedometerActivity), "DIGITAL")
        adapter.addFragment(MapFragment(this@SpeedometerActivity), "MAP")
        viewPager.offscreenPageLimit = 2
        viewPager.adapter = adapter
        tabView.setupWithViewPager(viewPager)

        ad_container_speedo.loadBannerAd(BannerPlacements.BANNER_AD)
        loadInterstitialAd(ADUnitPlacements.COMMON_INTERSTITIAL, onLoaded = { commonInterstitialAd = it } )

        nav_back.setOnClickListener {
            finish()
        }

        speedo_graph.setOnClickListener {
            startActivity(Intent(this, SpeedoGraphActivity::class.java))
        }

        premium_services.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        currentLocation = CurrentLocation(this)

        start_btn.setOnClickListener(::startStopBtn)

        tabView.setupWithViewPager(viewPager)
        tabView.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        start_btn_group.visibility = View.VISIBLE
                        actionBarText.text = getString(R.string.analog_meter)
                    }
                    1 -> {
                        start_btn_group.visibility = View.VISIBLE
                        actionBarText.text = getString(R.string.digital_meter)
                    }
                    else -> {
                        start_btn_group.visibility = View.GONE
                        actionBarText.text = getString(R.string.text_map)
                    }
                }
            }

        })
        actionBarText.text = getString(R.string.analog_meter)

        Callback.getMeterValue1().observe(this, androidx.lifecycle.Observer {
            unitType = it.unit
        })

    }

    fun startStopBtn(v: View) {

        if (start_btn_txt.text == getString(R.string.text_start_now)) {

            isStop = false

            speed_value.text = "0"
            distance_value.text = "0"

            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            val rationale = "Please provide location permission..."
            val options = Permissions.Options().setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning")

            Permissions.check(
                this,
                permissions,
                rationale,
                options,
                object : PermissionHandler() {

                    override fun onGranted() {

                        //loading_layout.visibility = View.VISIBLE
                        val btnText = getString(R.string.text_stop)
                        start_btn_txt.text = btnText
                        mViewModel?.startStopBtnState?.postValue(btnText)
                        start_btn.background = ContextCompat.getDrawable(
                            this@SpeedometerActivity,
                            R.drawable.background_stop_btn
                        )
                        startTime = Calendar.getInstance().timeInMillis
                        timerThread()

                        currentLocation?.getLocation(this@SpeedometerActivity)

                    }
                })

        } else {

            val btnText =getString(R.string.text_start_now)
            start_btn_txt.text = btnText
            mViewModel?.startStopBtnState?.postValue(btnText)

            start_btn.background = ContextCompat.getDrawable(
                this,
                R.drawable.background_start_btn
            )

            handler?.removeCallbacks(updateTimerThread)
            currentLocation?.removeFusedLocationClient()
            val db = Database.getInstance(this)
            endTime = System.currentTimeMillis()
            val date = TimeUtils.getFormatDateTime(endTime, "date")
            val distanceObj = Distance(startTime, endTime, maxSpeed, distance, date, totalTime)
            db.saveInterval(distanceObj)
            Callback.getMeterValue1().removeObservers(this)
            Callback.getLocationData().removeObservers(this)
            isStop = true
            totalTime = 0
            endTime = 0
            paused = false
            mCurrentLocation = null
            lStart = null
            lEnd = null
            distance = 0.0
            maxSpeed = 0.0
            avgSpeed = 0.0
        }

    }

    override fun gotLocation(locale: Location) {
        //loading_layout.visibility = View.GONE
        if (!isStop) {
            getSpeed(locale)
        }
    }

    private fun timerThread() {

        handler = Handler()

        updateTimerThread = object : Runnable {

            override fun run() {
                totalTime = System.currentTimeMillis() - startTime
                time_values.text = TimeUtils.getDurationSpeedo(totalTime)
                handler!!.postDelayed(this, 1000)
            }
        }

        handler!!.postDelayed(updateTimerThread!!, 1000)

    }

    private fun getSpeed(it: Location) {

        Callback.setLocationValue(it)

        when (unitType) {
            "km" -> {
                speed = (it.speed * 18) / 5.toDouble()
            }
            "mph" -> {
                speed = it.speed * 2.2369
            }
            "knot" -> {
                speed = it.speed * 1.94384
            }
        }

        if (speed > maxSpeed) {
            maxSpeed = speed
        }

        mCurrentLocation = it

        if (lStart == null) {
            lStart = mCurrentLocation
            lEnd = mCurrentLocation
        } else {
            lEnd = mCurrentLocation
        }

        updateUi()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi() {

        if (lStart != null && lEnd != null) {

            when (unitType) {

                "km" -> {
                    distance = lStart!!.distanceTo(lEnd).toDouble() / 1000
                    avgSpeed = maxSpeed / 2
                    speed_value.text = "${AppUtils.roundTwoDecimal(avgSpeed)} km"
                    distance_value.text = "${AppUtils.roundTwoDecimal(distance)} km"
                }
                "mph" -> {
                    distance = lStart!!.distanceTo(lEnd).toDouble() / 1609.34
                    avgSpeed = maxSpeed / 2
                    speed_value.text = "${AppUtils.roundTwoDecimal(avgSpeed)} mph"
                    distance_value.text = "${AppUtils.roundTwoDecimal(distance)} mph"
                }
                "knot" -> {
                    distance = lStart!!.distanceTo(lEnd).toDouble() / 1852
                    avgSpeed = maxSpeed / 2
                    speed_value.text = "${AppUtils.roundTwoDecimal(avgSpeed)} knot"
                    distance_value.text = "${AppUtils.roundTwoDecimal(distance)} knot"
                }

            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        currentLocation?.removeFusedLocationClient()
        handler?.removeCallbacks(updateTimerThread!!)
        AppUtils.unit = "km"
    }

    override fun onBackPressed() {
        super.onBackPressed()
        commonInterstitialAd?.apply {
            if (this.isLoaded()) this.showAd(this@SpeedometerActivity)
        }
    }

}