package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.R
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.callback.Callback
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.activity.SpeedometerActivity
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.vm.SpeedViewModel
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.AppUtils
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.NetworkUtils
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.fragment_digital.view.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import kotlinx.android.synthetic.main.fragment_map.view.digi_speed_txt
import kotlinx.android.synthetic.main.fragment_map.view.digi_type_txt


class MapFragment() : Fragment(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var mContext: Context? = null
    private lateinit var mView: View

    constructor(context: Context) : this() {
        this.mContext = context
    }

    var mViewModel: SpeedViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        view.let {
            mView = view
        }

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.speedometer_map_view) as SupportMapFragment
        mapFragment.getMapAsync(this)

        defaultSettings(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.digi_type_txt_value.text = "0.0"
        activity?.let { act ->
            mViewModel = ViewModelProviders.of(act).get(SpeedViewModel::class.java)

            mViewModel?.startStopBtnState?.observe(viewLifecycleOwner, Observer {
                if (it == view.context.resources.getString(R.string.text_start_now)) {
                    view.btn_state.setTextColor(
                        ContextCompat.getColor(
                            view.context,
                            R.color.colorPrimary
                        )
                    )
                } else {
                    view.btn_state.setTextColor(
                        ContextCompat.getColor(
                            view.context,
                            R.color.stop_btn_color
                        )
                    )
                }
                view.btn_state.text = it
            })
            (act as? SpeedometerActivity)?.let { view.view2.setOnClickListener(it::startStopBtn) }
        }

        view.let {
            mView = view
        }

        Callback.setDefaultSpeedo(true)

    }

    private fun defaultSettings(view: View) {

        val speedObserver = Observer<Location> {
            getSpeed(it)
        }

        val defaultObserver = Observer<Boolean> {
            if (it) {
                mView.digi_speed_txt.max = 240
                mView.digi_speed_txt.progress = 0
                mView.digi_type_txt.text = resources.getString(R.string.km_h_c)
                mView.digi_type_txt_value.text = "0.0"
            }
        }

        Callback.getLocationData().observe(viewLifecycleOwner, speedObserver)
        Callback.getDefaultSpeedoValues().observe(viewLifecycleOwner, defaultObserver)

    }

    var location: Location? = null
    private fun getSpeed(it: Location?) {

        location = it
        it.let {
            val sydney = LatLng(it!!.latitude, it.longitude)
            val cameraPosition = CameraPosition.Builder().target(sydney).zoom(15f).build()
            mMap?.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    cameraPosition
                )
            )
        }

        when (AppUtils.type) {
            "car" -> {
                when (AppUtils.unit) {

                    "km" -> {
                        mView.digi_speed_txt.max = 240
                        mView.digi_speed_txt.progress =
                            AppUtils.roundTwoDecimal((location!!.speed * 3600) / 1000.toDouble())
                                .toInt()
                        mView.digi_type_txt.text = resources.getString(R.string.km_h_c)
                        mView.digi_type_txt_value.text =
                            "${AppUtils.roundOneDecimal((location!!.speed * 3600) / 1000.toDouble())}"
                    }

                    "mph" -> {
                        mView.digi_speed_txt.max = 150
                        mView.digi_speed_txt.progress =
                            AppUtils.roundTwoDecimal(((location!!.speed * 2.2369))).toInt()
                        mView.digi_type_txt.text = resources.getString(R.string.mph_c)
                        mView.digi_type_txt_value.text =
                            "${AppUtils.roundOneDecimal(location!!.speed * 2.2369)}"
                    }

                    "knot" -> {
                        mView.digi_speed_txt.max = 128
                        mView.digi_speed_txt.progress =
                            AppUtils.roundTwoDecimal(location!!.speed * 1.94384).toInt()
                        mView.digi_type_txt.text = resources.getString(R.string.knot_c)
                        mView.digi_type_txt_value.text =
                            "${AppUtils.roundOneDecimal(location!!.speed * 1.94384)}"
                    }

                }
            }
            "cycle" -> {
                when (AppUtils.unit) {

                    "km" -> {
                        mView.digi_speed_txt.max = 72
                        mView.digi_speed_txt.progress =
                            AppUtils.roundTwoDecimal((location!!.speed * 3600) / 1000.toDouble())
                                .toInt()
                        mView.digi_type_txt.text = resources.getString(R.string.km_h_c)
                        mView.digi_type_txt_value.text =
                            "${AppUtils.roundOneDecimal((location!!.speed * 3600) / 1000.toDouble())}"
                    }

                    "mph" -> {
                        mView.digi_speed_txt.max = 36
                        mView.digi_speed_txt.progress =
                            AppUtils.roundTwoDecimal(location!!.speed * 2.2369).toInt()
                        mView.digi_type_txt.text = resources.getString(R.string.mph_c)
                        mView.digi_type_txt_value.text =
                            "${AppUtils.roundOneDecimal(location!!.speed * 2.2369)}"
                    }

                    "knot" -> {
                        mView.digi_speed_txt.max = 27
                        mView.digi_speed_txt.progress =
                            AppUtils.roundTwoDecimal(((location!!.speed * 1.94384))).toInt()
                        mView.digi_type_txt.text = resources.getString(R.string.knot_c)
                        mView.digi_type_txt_value.text =
                            "${AppUtils.roundOneDecimal(location!!.speed * 1.94384)}"
                    }

                }
            }
            "train" -> {
                when (AppUtils.unit) {

                    "km" -> {
                        mView.digi_speed_txt.max = 360
                        mView.digi_speed_txt.progress =
                            AppUtils.roundTwoDecimal((location!!.speed * 3600) / 1000.toDouble())
                                .toInt()
                        mView.digi_type_txt.text = resources.getString(R.string.km_h_c)
                        mView.digi_type_txt_value.text =
                            "${AppUtils.roundOneDecimal((location!!.speed * 3600) / 1000.toDouble())}"
                    }

                    "mph" -> {
                        mView.digi_speed_txt.max = 220
                        mView.digi_speed_txt.progress =
                            AppUtils.roundTwoDecimal(((location!!.speed * 2.2369))).toInt()
                        mView.digi_type_txt.text = resources.getString(R.string.mph_c)
                        mView.digi_type_txt_value.text =
                            "${AppUtils.roundOneDecimal(location!!.speed * 2.2369)}"
                    }

                    "knot" -> {
                        mView.digi_speed_txt.max = 200
                        mView.digi_speed_txt.progress =
                            AppUtils.roundTwoDecimal(((location!!.speed * 1.94384))).toInt()
                        mView.digi_type_txt.text = resources.getString(R.string.knot_c)
                        mView.digi_type_txt_value.text =
                            "${AppUtils.roundOneDecimal(location!!.speed * 1.94384)}"
                    }

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!NetworkUtils.isOnline(mView.context))
            Snackbar
                .make(
                    mView.speedometer_map_view, "Please Check Internet Connection",
                    Snackbar.LENGTH_LONG
                )/*.setAction("Turn On Wifi") {
                    Toast.makeText(mView.context, "Snackbar clicked", Toast.LENGTH_LONG).show()
                }*/.show()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        googleMap?.uiSettings?.isScrollGesturesEnabled = false
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val rationale = "Please provide location permission..."
        val options = Permissions.Options().setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning")

        if (mContext != null) {
            Permissions.check(
                mContext,
                permissions,
                rationale,
                options,
                object : PermissionHandler() {

                    @SuppressLint("MissingPermission")
                    override fun onGranted() {
                        mMap?.isMyLocationEnabled = true
                        mMap?.uiSettings?.isScrollGesturesEnabled = false
                    }
                }
            )
        }
    }

}