package com.faizi.pedometerdemo.ui.fragment

import android.content.Context
import android.content.res.ColorStateList
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.callback.Callback
import com.faizi.pedometerdemo.model.Speedo
import com.faizi.pedometerdemo.util.AppUtils
import kotlinx.android.synthetic.main.fragment_analog.*
import kotlinx.android.synthetic.main.fragment_analog.view.*
import kotlinx.android.synthetic.main.fragment_analog.view.car_view
import kotlinx.android.synthetic.main.fragment_analog.view.cycle_view
import kotlinx.android.synthetic.main.fragment_analog.view.popup_units
import kotlinx.android.synthetic.main.fragment_analog.view.train_view
import kotlinx.android.synthetic.main.fragment_analog.view.units_text

class AnalogFragment() : Fragment() {

    private lateinit var mContext: Context
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
        ImageViewCompat.setImageTintList(
            view, ColorStateList.valueOf(
                ContextCompat.getColor(mContext, color)
            )
        )
    }

    private fun defaultSettings(view: View) {

        view.speedometer_view.setStartDegree(150)
        view.speedometer_view.setEndDegree(360)
        view.speedometer_view.setMaxSpeed(350f)
        view.speedometer_view.setMinSpeed(0f)

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

        when (unitMain) {
            "km" -> {
                mView.speedometer_view.setSpeedAt((it.speed * 18) / 5.toFloat())
            }

            "mph" -> {
                mView.speedometer_view.setSpeedAt((it.speed * 2.2369).toFloat())
            }

            "knot" -> {
                mView.speedometer_view.setSpeedAt((it.speed * 1.94384).toFloat())
            }
        }
    }

    private fun showPopup(view: View) {

        val popup = PopupMenu(mContext, view.popup_units)
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

}