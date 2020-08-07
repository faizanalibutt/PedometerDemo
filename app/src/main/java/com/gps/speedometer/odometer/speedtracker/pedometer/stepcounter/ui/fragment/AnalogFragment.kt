package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.fragment

import android.content.Context
import android.content.res.ColorStateList
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.R
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.callback.Callback
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.model.Speedo
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.AppUtils
import kotlinx.android.synthetic.main.fragment_analog.*
import kotlinx.android.synthetic.main.fragment_analog.view.*
import kotlinx.android.synthetic.main.fragment_analog.view.car_view
import kotlinx.android.synthetic.main.fragment_analog.view.cycle_view
import kotlinx.android.synthetic.main.fragment_analog.view.digi_type_txt
import kotlinx.android.synthetic.main.fragment_analog.view.popup_units
import kotlinx.android.synthetic.main.fragment_analog.view.train_view

class AnalogFragment() : Fragment() {

    private var mContext: Context? = null
    private lateinit var mView: View

    private var vehicle: String = "car"
    private var unitMain: String = "km"

    // secondary constructor to pass context
    constructor(context: Context) : this() {
        this.mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_analog, container, false)

        view.let {
            mView = view
        }

        defaultSettings(view)

        view.cycle_view.setOnClickListener {

            vehicle = "cycle"
            Callback.setMeterValue1(Speedo(vehicle, unitMain, units_text.text.toString(), ""))
        }

        view.car_view.setOnClickListener {

            vehicle = "car"
            Callback.setMeterValue1(Speedo(vehicle, unitMain, units_text.text.toString(), ""))

        }

        view.train_view.setOnClickListener {

            vehicle = "train"
            Callback.setMeterValue1(Speedo(vehicle, unitMain, units_text.text.toString(), ""))

        }

        view.popup_units.setOnClickListener {
            showPopup(mView)
        }

        return view
    }

    private fun changeVehicleView(view: ImageView, color: Int) {
        if (mContext != null) {
            ImageViewCompat.setImageTintList(
                view, ColorStateList.valueOf(
                    ContextCompat.getColor(mContext!!, color)
                )
            )
        }
    }

    private fun defaultSettings(view: View) {

        mView.speedometer_view.max = 240
        mView.speedometer_view.progress = 0
        mView.digi_type_txt.text = resources.getString(R.string.km_h_c)

        val speedObserver = Observer<Location> {
            getSpeed(it)
        }

        val meterObserver = Observer<Speedo> {

            vehicle = it.type
            unitMain = it.unit
            setValues(it.type, it.unit, view)

            units_text.text = it.unit_text

            when (it.type) {
                "cycle" -> {
                    changeVehicleView(view.cycle_view, R.color.colorPrimary)
                    changeVehicleView(view.car_view, R.color.black_dark)
                    changeVehicleView(view.train_view, R.color.black_dark)
                }
                "car" -> {
                    changeVehicleView(view.cycle_view, R.color.black_dark)
                    changeVehicleView(view.car_view, R.color.colorPrimary)
                    changeVehicleView(view.train_view, R.color.black_dark)
                }
                "train" -> {
                    changeVehicleView(view.cycle_view, R.color.black_dark)
                    changeVehicleView(view.car_view, R.color.black_dark)
                    changeVehicleView(view.train_view, R.color.colorPrimary)
                }
            }
        }

        Callback.getLocationData().observe(viewLifecycleOwner, speedObserver)
        Callback.getMeterValue1().observe(viewLifecycleOwner, meterObserver)

    }

    private fun getSpeed(it: Location) {

        AppUtils.unit = unitMain
        AppUtils.type = vehicle

        when (unitMain) {
            "km" -> {
                mView.speedometer_view.progress = AppUtils.roundTwoDecimal((it.speed * 3600 ) / 1000.toDouble()).toInt()
            }

            "mph" -> {
                mView.speedometer_view.progress = AppUtils.roundTwoDecimal(it.speed * 2.2369).toInt()
            }

            "knot" -> {
                mView.speedometer_view.progress = AppUtils.roundTwoDecimal(it.speed * 1.94384).toInt()
            }
        }
    }

    private fun showPopup(view: View) {

        val popup = PopupMenu(mContext!!, view.popup_units)
        popup.menuInflater.inflate(R.menu.units_popup_menu, popup.menu)

        popup.setOnMenuItemClickListener {
            when (it.title.toString()) {
                "KM/H" -> {
                    unitMain = "km"
                    Callback.setMeterValue1(Speedo(vehicle, unitMain, resources.getString(R.string.km_h_c), ""))
                }
                "MPH" -> {
                    unitMain = "mph"
                    Callback.setMeterValue1(Speedo(vehicle, unitMain, resources.getString(R.string.mph_c), ""))
                }
                "KNOT" -> {
                    unitMain = "knot"
                    Callback.setMeterValue1(Speedo(vehicle, unitMain, resources.getString(R.string.knot_c), ""))
                }
            }
            return@setOnMenuItemClickListener true
        }

        popup.show()
    }

    private fun setValues(type: String, unit: String, view: View) {

        when (type) {
            "car" -> {
                if (view.speedometer_view == null) return

                when (unit) {
                    "km" -> {

                        mView.speedometer_view.max = 240
                        mView.digi_type_txt.text = "Km/h"

                        view.speedometer_view_img.setImageResource(
                            R.drawable.ic_car_kmph
                        )
                    }
                    "mph" -> {

                        mView.speedometer_view.max = 150
                        mView.digi_type_txt.text = "mph"

                        view.speedometer_view_img.setImageResource(
                            R.drawable.ic_car_mph
                        )

                    }
                    "knot" -> {

                        mView.speedometer_view.max = 128
                        mView.digi_type_txt.text = "knot"

                        view.speedometer_view_img.setImageResource(
                            R.drawable.ic_car_knot
                        )

                    }
                }
            }

            "cycle" -> {
                if (view.speedometer_view == null) return

                when (unit) {
                    "km" -> {

                        mView.speedometer_view.max = 72
                        mView.digi_type_txt.text = "Km/h"

                        view.speedometer_view_img.setImageResource(
                            R.drawable.ic_cycle_kmph
                        )


                    }
                    "mph" -> {

                        mView.speedometer_view.max = 36
                        mView.digi_type_txt.text = "mph"

                        view.speedometer_view_img.setImageResource(
                            R.drawable.ic_cycle_mph
                        )

                    }
                    "knot" -> {

                        mView.speedometer_view.max = 27
                        mView.digi_type_txt.text = "knot"

                        view.speedometer_view_img.setImageResource(
                            R.drawable.ic_cycle_knot
                        )

                    }
                }
            }

            "train" -> {
                if (view.speedometer_view == null) return
                when (unit) {
                    "km" -> {

                        mView.speedometer_view.max = 360
                        mView.digi_type_txt.text = "Km/h"

                        view.speedometer_view_img.setImageResource(
                            R.drawable.ic_train_kmph
                        )

                    }
                    "mph" -> {

                        mView.speedometer_view.max = 220
                        mView.digi_type_txt.text = "mph"

                        view.speedometer_view_img.setImageResource(
                            R.drawable.ic_train_mph
                        )

                    }
                    "knot" -> {

                        mView.speedometer_view.max = 200
                        mView.digi_type_txt.text = "knot"

                        view.speedometer_view_img.setImageResource(
                            R.drawable.ic_train_knot
                        )

                    }
                }
            }
        }
    }

}