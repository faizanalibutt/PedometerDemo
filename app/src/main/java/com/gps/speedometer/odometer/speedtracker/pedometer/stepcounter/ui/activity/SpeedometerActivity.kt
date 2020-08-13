package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.dev.bytes.adsmanager.*
import com.dev.bytes.adsmanager.billing.purchaseRemoveAds
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.tabs.TabLayout
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.Database
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.R
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.app.App
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.callback.Callback
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.model.Distance
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.model.Speedo
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.ViewPagerAdapter
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.fragment.AnalogFragment
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.fragment.DigitalFragment
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.fragment.MapFragment
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.vm.SpeedViewModel
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.AppUtils
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.CurrentLocation
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.TimeUtils
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.activity_pedometer.*
import kotlinx.android.synthetic.main.activity_speedometer.*
import kotlinx.android.synthetic.main.activity_speedometer.nav_back
import kotlinx.android.synthetic.main.activity_speedometer.tabView
import kotlinx.android.synthetic.main.activity_speedometer.viewPager
import java.util.*
import kotlin.math.max

class SpeedometerActivity : Activity(), CurrentLocation.LocationResultListener {

    private var avgSpeedInDB: Double = 0.0
    private var maxSpeedInDB: Double = 0.0
    private var distanceInDB: Double = 0.0
    private var isStartStopShown: Boolean = false
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
    var startStopInterstitialAd: InterAdPair? = null
    var backInterstitialAd: InterAdPair? = null
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
        loadInterstitialAd(
            ADUnitPlacements.SPEEDO_START_STOP_INTERSTITIAL,
            onLoaded = { startStopInterstitialAd = it },
            reloadOnClosed = true
        )
        loadInterstitialAd(
            ADUnitPlacements.SPEEDO_BACK_INTERSTITIAL,
            onLoaded = { backInterstitialAd = it })

        nav_back.setOnClickListener {
            onBackPressed()
        }

        /*premium_services.setOnClickListener {
            App.bp?.purchaseRemoveAds(this)
        }

        if (TinyDB.getInstance(this).getBoolean(getString(com.dev.bytes.R.string.is_premium)))
            premium_services.visibility = View.GONE
        else
            AppUtils.animateProButton(this, premium_services)*/

        speedo_graph.setOnClickListener {
            startActivity(Intent(this, SpeedoGraphActivity::class.java))
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
                        ad_container_speedo_map.visibility = View.VISIBLE
                    }
                    1 -> {
                        start_btn_group.visibility = View.VISIBLE
                        actionBarText.text = getString(R.string.digital_meter)
                        ad_container_speedo_map.visibility = View.VISIBLE
                    }
                    2 -> {
                        start_btn_group.visibility = View.GONE
                        actionBarText.text = getString(R.string.text_map)
                        ad_container_speedo_map.visibility = View.GONE
                    }
                    else -> {
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
            Callback.setMeterValue1(Speedo("car", "km", "KM/H", ""))

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
                        showStartStopInter()

                    }
                })

        } else {

            val btnText = getString(R.string.text_start_now)
            start_btn_txt.text = btnText
            mViewModel?.startStopBtnState?.postValue(btnText)
            speed_value.text = "0"
            distance_value.text = "0"
            time_values.text = "00:00"

            start_btn.background = ContextCompat.getDrawable(
                this,
                R.drawable.background_start_btn
            )

            handler?.removeCallbacks(updateTimerThread)
            currentLocation?.removeFusedLocationClient()
            val db = Database.getInstance(this)
            endTime = System.currentTimeMillis()
            val date = TimeUtils.getFormatDateTime(endTime, "date")
            val distanceObj = Distance(startTime, endTime, avgSpeedInDB, distanceInDB, date, totalTime)
            db.saveInterval(distanceObj)
            isStop = true
            totalTime = 0
            endTime = 0
            paused = false
            mCurrentLocation = null
            lStart = null
            lEnd = null
            distance = 0.0
            distanceInDB = 0.0
            maxSpeedInDB = 0.0
            avgSpeedInDB = 0.0
            maxSpeed = 0.0
            avgSpeed = 0.0
            showStartStopInter()
            Callback.setDefaultSpeedo(true)
            Callback.setMeterValue1(Speedo("car", "km", "KM/H", ""))
        }

    }

    fun showStartStopInter() {
        startStopInterstitialAd?.apply {
            if (this.isLoaded()) this.showAd(this@SpeedometerActivity, true)
                .let { isStartStopShown = it }
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
                speed = (it.speed * 3600) / 1000.toDouble()
            }
            "mph" -> {
                speed = it.speed * 2.2369
            }
            "knot" -> {
                speed = it.speed * 1.94384
            }
        }

        val speedDB = (it.speed * 3600) / 1000.toDouble()
        maxSpeedInDB = max(speedDB, maxSpeedInDB)
        avgSpeedInDB = speedDB + maxSpeedInDB / 2

        if (speed > maxSpeed) {
            maxSpeed = speed
        }

        mCurrentLocation = it

        if (lStart == null) {
            lStart = mCurrentLocation
            lStart?.latitude = mCurrentLocation!!.latitude
            lStart?.longitude = mCurrentLocation!!.longitude
            lEnd = mCurrentLocation
            lEnd?.latitude = mCurrentLocation!!.latitude
            lEnd?.longitude = mCurrentLocation!!.longitude
        } else {
            lStart = lEnd
            lStart?.latitude = lEnd!!.latitude
            lStart?.longitude = lEnd!!.longitude
            lEnd = mCurrentLocation
            lEnd?.latitude = mCurrentLocation!!.latitude
            lEnd?.longitude = mCurrentLocation!!.longitude
        }

        updateUi()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi() {

        if (lStart != null && lEnd != null) {

            distanceInDB += ( lStart!!.distanceTo(lEnd).toDouble() / 1000.0 )

            when (unitType) {

                "km" -> {
                    distance += ( lStart!!.distanceTo(lEnd).toDouble() / 1000.0 )
                    avgSpeed = (speed + maxSpeed) / 2
                    speed_value.text = "${AppUtils.roundTwoDecimal(avgSpeed)} km"
                    distance_value.text = "${AppUtils.roundTwoDecimal(distance)} km"
                }
                "mph" -> {
                    distance += ( lStart!!.distanceTo(lEnd).toDouble() / 1609.34 )
                    avgSpeed = (speed + maxSpeed) / 2
                    speed_value.text = "${AppUtils.roundTwoDecimal(avgSpeed)} mph"
                    distance_value.text = "${AppUtils.roundTwoDecimal(distance)} mph"
                }
                "knot" -> {
                    distance += ( lStart!!.distanceTo(lEnd).toDouble() / 1852 )
                    avgSpeed = (speed + maxSpeed) / 2
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
        AppUtils.type = "cycle"
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backInterstitialAd?.apply {
            if (this.isLoaded() && !isStartStopShown)
                this.showAd(this@SpeedometerActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CurrentLocation.REQUEST_LOCATION) {
            if (resultCode == android.app.Activity.RESULT_OK) {

                currentLocation?.getLocation(this@SpeedometerActivity)

            } else if (resultCode == android.app.Activity.RESULT_CANCELED) {
                Toast.makeText(
                    this, "Please enable Gps to get current location.", Toast.LENGTH_SHORT
                ).show()
                val btnText = getString(R.string.text_start_now)
                start_btn_txt.text = btnText
                mViewModel?.startStopBtnState?.postValue(btnText)

                start_btn.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.background_start_btn
                )

                handler?.removeCallbacks(updateTimerThread)
                currentLocation?.removeFusedLocationClient()
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
    }

}