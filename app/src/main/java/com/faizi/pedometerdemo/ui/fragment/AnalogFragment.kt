package com.faizi.pedometerdemo.ui.fragment

import android.Manifest
import android.content.Context
import android.content.res.ColorStateList
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.faizi.pedometerdemo.Database
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.model.Distance
import com.faizi.pedometerdemo.util.AppUtils.roundTwoDecimal
import com.faizi.pedometerdemo.util.CurrentLocation
import com.faizi.pedometerdemo.util.Logger
import com.faizi.pedometerdemo.util.TimeUtils
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.fragment_analog.*
import kotlinx.android.synthetic.main.fragment_analog.view.*
import java.util.*
import java.util.concurrent.TimeUnit

class AnalogFragment() : Fragment(), CurrentLocation.LocationResultListener {

    constructor(context: Context) : this() {
        this.mContext = context
    }

    private lateinit var mContext: Context
    private lateinit var mView: View

    private var speed: Double = 0.0
    private var vehicle: String = "car"
    private var unitMain: String = "km"
    private lateinit var typeMain: String
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_analog, container, false)

        mView.let {
            mView = view
        }

        defaultSettings(view)

        view.cycle_view.setOnClickListener {

            ImageViewCompat.setImageTintList(view.cycle_view, ColorStateList.valueOf(
                ContextCompat.getColor(mContext, R.color.colorPrimary)
            ))

            ImageViewCompat.setImageTintList(view.car_view, ColorStateList.valueOf(
                ContextCompat.getColor(mContext, R.color.black_dark)
            ))

            ImageViewCompat.setImageTintList(view.train_view, ColorStateList.valueOf(
                ContextCompat.getColor(mContext, R.color.black_dark)
            ))

            vehicle = "cycle"
            setValues(vehicle, unitMain, view)
        }

        view.car_view.setOnClickListener {

            ImageViewCompat.setImageTintList(view.cycle_view, ColorStateList.valueOf(
                ContextCompat.getColor(mContext, R.color.black_dark)
            ))

            ImageViewCompat.setImageTintList(view.car_view, ColorStateList.valueOf(
                ContextCompat.getColor(mContext, R.color.colorPrimary)
            ))

            ImageViewCompat.setImageTintList(view.train_view, ColorStateList.valueOf(
                ContextCompat.getColor(mContext, R.color.black_dark)
            ))

            vehicle = "car"
            setValues(vehicle, unitMain, view)

        }

        view.train_view.setOnClickListener {

            ImageViewCompat.setImageTintList(view.cycle_view, ColorStateList.valueOf(
                ContextCompat.getColor(mContext, R.color.black_dark)
            ))

            ImageViewCompat.setImageTintList(view.car_view, ColorStateList.valueOf(
                ContextCompat.getColor(mContext, R.color.black_dark)
            ))

            ImageViewCompat.setImageTintList(view.train_view, ColorStateList.valueOf(
                ContextCompat.getColor(mContext, R.color.colorPrimary)
            ))

            vehicle = "train"
            setValues(vehicle, unitMain, view)

        }

        view.popup_units.setOnClickListener {
            showPopup(it)
        }

        currentLocation = CurrentLocation(mContext)

        view.start_btn.setOnClickListener {

            if (view.start_btn_txt.text == getString(R.string.text_start_now)) {

                isStop = false

                avg_txt.text = "0"

                distance_value.text = "0"

                speed_txt.text = "0"

                val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                val rationale = "Please provide location permission..."
                val options = com.nabinbhandari.android.permissions.Permissions.Options().setRationaleDialogTitle("Info")
                    .setSettingsDialogTitle("Warning")

                Permissions.check(
                    mContext,
                    permissions,
                    rationale,
                    options,
                    object : PermissionHandler() {

                        override fun onGranted() {

                            //loading_layout.visibility = View.VISIBLE
                            view.start_btn_txt.text = getString(R.string.text_stop)
                            view.start_btn.background = ContextCompat.getDrawable(
                                mContext,
                                R.drawable.background_stop_btn
                            )
                            startTime = Calendar.getInstance().timeInMillis
                            Logger.log(startTime.toString())
                            timerThread()

                            currentLocation?.getLocation(this@AnalogFragment)

                        }
                    })

            } else {
                view.start_btn_txt.text = getString(R.string.start_tracking)
                view.start_btn_txt.background = ContextCompat.getDrawable(this,
                    R.drawable.selected_bg
                )

                handler?.removeCallbacks(updateTimerThread)
                currentLocation?.removeFusedLocationClient()
                val db = Database.getInstance(mContext)
                endTime = System.currentTimeMillis()
                val date = TimeUtils.getFormatDateTime(endTime, "date")
                val distanceObj = Distance(startTime, endTime, maxSpeed, distance, date, totalTime)
                db.saveInterval(distanceObj)
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

        return view
    }

    private fun defaultSettings(view: View) {

        view.speedometer_view.setStartDegree(150)
        view.speedometer_view.setEndDegree(360)
        view.speedometer_view.setMaxSpeed(350f)
        view.speedometer_view.setMinSpeed(0f)

        // for digi text in digital meter
        /*val typeface = Typeface.createFromAsset(
            mContext.assets, "fonts/digital.ttf"
        )
        digi_speed_txt.typeface = typeface
        digi_type_txt.typeface = typeface*/

    }

    private fun showPopup(view: View) {
        val popup = PopupMenu(mContext, view.popup_units)
        popup.menuInflater.inflate(R.menu.units_popup_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.title.toString()) {
                "KM/H" -> {
                    unitMain = "km"
                    setValues(vehicle, unitMain, view)
                    view.units_text.text = resources.getString(R.string.km_h_c)
                    //view.digi_type_txt.text = resources.getString(R.string.km_h_c)
                }
                "MPH" -> {
                    unitMain = "mph"
                    setValues(vehicle, unitMain, view)
                    view.units_text.text = resources.getString(R.string.mph_c)
                    //digi_type_txt.text = resources.getString(R.string.mph_c)
                }
                "KNOT" -> {
                    unitMain = "knot"
                    setValues(vehicle, unitMain, view)
                    view.units_text.text = resources.getString(R.string.knot_c)
                    //digi_type_txt.text = resources.getString(R.string.knot_c)
                }
            }
            return@setOnMenuItemClickListener true
        }

        popup.show()
    }

    private fun timerThread() {

        handler = Handler()

        updateTimerThread = object : Runnable {

            override fun run() {
                totalTime = System.currentTimeMillis() - startTime
                val timeInHours = TimeUnit.MILLISECONDS.toHours(totalTime)
                duration_txt.text = TimeUtils.getFormatedTimeMHS(totalTime)
                handler!!.postDelayed(this, 1000)
            }
        }

        handler!!.postDelayed(updateTimerThread, 1000)

    }

    private fun setValues(type: String, unit: String, view: View) {

        when (type) {
            "car" -> {
                if (view.speedometer_view == null) return

                when (unit) {
                    "km" -> {
                        view.speedometer_view.setUnit("Km/h")
                        view.speedometer_view.setMinSpeed(0f)
                        view.speedometer_view.setMaxSpeed(240f)
                        view.speedometer_view.setTicks(0F, 20F, 40F, 60F, 80F, 100F, 120F, 140F, 160F, 180F)
                        view.speedometer_view.setImageSpeedometer(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.kmph
                            )
                        )
                    }
                    "mph" -> {
                        view.speedometer_view.setUnit("MPH")
                        view.speedometer_view.setMinSpeed(0f)
                        view.speedometer_view.setMaxSpeed(90f)
//                        view.speedometer_view.setTicks(0F, 10F, 20F, 30F, 40F, 50F, 60F, 70F, 80F, 90F)
                        view.speedometer_view.setImageSpeedometer(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.mph
                            )
                        )

                    }
                    "knot" -> {
                        view.speedometer_view.setUnit("KNOT")
                        view.speedometer_view.setMinSpeed(0f)
                        view.speedometer_view.setMaxSpeed(63f)
//                        view.speedometer_view.setTicks(0F, 7F, 14F, 21F, 28F, 35F, 42F, 49F, 56F, 63F)
                        view.speedometer_view.setImageSpeedometer(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.knot
                            )
                        )

                    }

                }
            }
            "cycle" -> {
                if (view.speedometer_view == null) return

                when (unit) {
                    "km" -> {
                        view.speedometer_view.setUnit("Km/h")
                        view.speedometer_view.setMinSpeed(0f)
                        view.speedometer_view.setMaxSpeed(72f)
//                        view.speedometer_view.setTicks(0F, 8F, 16F, 24F, 32F, 40F, 48F, 56F, 64F, 72F)
                        view.speedometer_view.setImageSpeedometer(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.c_kmph
                            )
                        )


                    }
                    "mph" -> {
                        view.speedometer_view.setUnit("MPH")
                        view.speedometer_view.setMinSpeed(0f)
                        view.speedometer_view.setMaxSpeed(36f)
//                        view.speedometer_view.setTicks(0F, 4F, 8F, 12F, 16F, 20F, 24F, 28F, 32F, 36F)
                        view.speedometer_view.setImageSpeedometer(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.c_mph
                            )
                        )

                    }
                    "knot" -> {
                        view.speedometer_view.setUnit("KNOT")

                        view.speedometer_view.setMinSpeed(0f)
                        view.speedometer_view.setMaxSpeed(27f)
//                        view.speedometer_view.setTicks(0F, 3F, 6F, 9F, 12F, 15F, 18F, 21F, 24F, 27F)
                        view.speedometer_view.setImageSpeedometer(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.c_knot
                            )
                        )

                    }
                }
            }
            "train" -> {
                if (view.speedometer_view == null) return
                when (unit) {
                    "km" -> {
                        view.speedometer_view.setUnit("Km/h")
                        view.speedometer_view.setMinSpeed(0f)
                        view.speedometer_view.setMaxSpeed(360f)
//                        view.speedometer_view.setTicks(0F, 40F, 80F, 120F, 160F, 200F, 240F, 280F, 320F, 360F)
                        view.speedometer_view.setImageSpeedometer(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.t_kmph
                            )
                        )

                    }
                    "mph" -> {
                        view.speedometer_view.setUnit("MPH")

                        view.speedometer_view.setMinSpeed(0f)
                        view.speedometer_view.setMaxSpeed(180f)
//                        view.speedometer_view.setTicks(0F, 20F, 40F, 60F, 80F, 100F, 120F, 140F, 160F, 180F)
                        view.speedometer_view.setImageSpeedometer(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.t_mph
                            )
                        )

                    }
                    "knot" -> {
                        view.speedometer_view.setUnit("KNOT")

                        view.speedometer_view.setMinSpeed(0f)
                        view.speedometer_view.setMaxSpeed(90f)
//                        view.speedometer_view.setTicks(0F, 10F, 20F, 30F, 40F, 50F, 60F, 70F, 80F, 90F)
                        view.speedometer_view.setImageSpeedometer(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.t_knot
                            )
                        )

                    }
                }
            }
        }
    }

    private fun updateUi(mView: View) {

        if (lStart != null && lEnd != null) {
//            distance += lStart!!.distanceTo(lEnd) / 1000.00
            val mileDis = lStart!!.distanceTo(lEnd).toDouble()
            distance = lStart!!.distanceTo(lEnd).toDouble() / 1000
//            avgSpeed = mileDis/TimeUnit.MILLISECONDS.toSeconds(totalTime)
            avgSpeed = maxSpeed / 2
            avg_txt.text = roundTwoDecimal(avgSpeed).toString()
//            lStart = lEnd
            distance_value.text = roundTwoDecimal(distance).toString()
        }
        speed_txt.text = roundTwoDecimal(maxSpeed).toString()

    }

    override fun gotLocation(locale: Location) {
        //loading_layout.visibility = View.GONE
        if (!isStop) {

            mView.let {
                getSpeed(locale, mView)
            }

            /*val sydney = LatLng(it.latitude, it.longitude)
            val cameraPosition = CameraPosition.Builder().target(sydney).zoom(15f).build()
            mMap?.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    cameraPosition
                )
            )*/
        }
    }

    private fun getSpeed(it: Location, mView: View) {

        when (unitMain) {

            "km" -> {
                mView.speedometer_view.setSpeedAt((it.speed * 18) / 5.toFloat())
                digi_speed_txt.text = roundTwoDecimal(((it.speed * 18) / 5.toDouble())).toString()

            }
            "mph" -> {
                mView.speedometer_view.setSpeedAt((it.speed * 2.2369).toFloat())
                digi_speed_txt.text = roundTwoDecimal(((it.speed * 2.2369))).toString()

            }
            "knot" -> {
                mView.speedometer_view.setSpeedAt((it.speed * 1.94384).toFloat())
//                val f =
                digi_speed_txt.text = roundTwoDecimal(((it.speed * 1.94384))).toString()

            }
        }

        speed = (it.speed * 18) / 5.toDouble()
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

        updateUi(mView)
    }

}