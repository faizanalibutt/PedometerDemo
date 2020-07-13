package com.faizi.pedometerdemo.callback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object Callback {

    @JvmField
    var meterValue = MutableLiveData<Pair<String, String>>()

    fun setMeterValue(pair: Pair<String, String>) {
        meterValue.postValue(pair)
    }

    fun getMeterValue(): LiveData<Pair<String, String>> {
        return meterValue
    }

}