package com.faizi.pedometerdemo.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.callback.Callback
import com.faizi.pedometerdemo.util.AppUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.fragment_map.view.*


class MapFragment() : Fragment(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private lateinit var mContext: Context
    private lateinit var mView: View

    constructor(context: Context) : this() {
        this.mContext = context
    }

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

    private fun defaultSettings(view: View) {

        // for digi text in digital meter set font type to look digital
        val typeface = Typeface.createFromAsset(
            mContext.assets, "fonts/digital.ttf"
        )
        view.digi_speed_txt.typeface = typeface
        view.digi_type_txt.typeface = typeface

        val speedObserver = Observer<Location> {
            getSpeed(it)
        }

        Callback.getLocationData().observe(viewLifecycleOwner, speedObserver)

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

        when (AppUtils.unit) {

            "km" -> {
                mView.digi_speed_txt.text = AppUtils.roundTwoDecimal(((location!!.speed * 2.2369))).toString()
                mView.digi_type_txt.text = resources.getString(R.string.km_h_c)
            }

            "mph" -> {
                mView.digi_speed_txt.text = AppUtils.roundTwoDecimal(((location!!.speed * 2.2369))).toString()
                mView.digi_type_txt.text = resources.getString(R.string.mph_c)
            }

            "knot" -> {
                mView.digi_speed_txt.text = AppUtils.roundTwoDecimal(((location!!.speed * 1.94384))).toString()
                mView.digi_type_txt.text = resources.getString(R.string.knot_c)
            }

        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap

        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val rationale = "Please provide location permission..."
        val options = Permissions.Options().setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning")

        Permissions.check(
            mContext,
            permissions,
            rationale,
            options,
            object : PermissionHandler() {

                @SuppressLint("MissingPermission")
                override fun onGranted() {
                    mMap?.isMyLocationEnabled = true
                }
            }
        )
    }

}