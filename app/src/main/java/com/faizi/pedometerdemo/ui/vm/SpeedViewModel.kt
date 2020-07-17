package com.faizi.pedometerdemo.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SpeedViewModel : ViewModel() {

    var startStopBtnState = MutableLiveData<String>()
}