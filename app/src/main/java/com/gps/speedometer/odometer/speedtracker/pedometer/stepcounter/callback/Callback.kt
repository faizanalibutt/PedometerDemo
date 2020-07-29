package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.callback

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.model.Speedo

object Callback {

    @JvmField
    var speedData = MutableLiveData<Location>()

    fun setLocationValue(location: Location) {
        speedData.postValue(location)
    }

    fun getLocationData(): LiveData<Location> {
        return speedData
    }

    @JvmField
    var meterValue = MutableLiveData<Pair<String, String>>()

    fun setMeterValue(pair: Pair<String, String>) {
        meterValue.postValue(pair)
    }

    fun getMeterValue(): LiveData<Pair<String, String>> {
        return meterValue
    }

    @JvmField
    var meterValue1 = MutableLiveData<Speedo>()

    fun setMeterValue1(speedo: Speedo) {
        meterValue1.postValue(speedo)
    }

    fun getMeterValue1(): LiveData<Speedo> {
        return meterValue1
    }

}