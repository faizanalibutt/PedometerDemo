package com.faizi.pedometerdemo.callback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object Callback {

    @JvmField
    val mutableLiveData = MutableLiveData<Boolean>()
    /*@JvmField
    val transferMutableLiveData = MutableLiveData<CrackTransfer>()*/
    @JvmField
    val updateDialogMutable = MutableLiveData<Any>()
    @JvmField
    val showHotspot = MutableLiveData<String>()
    @JvmField
    val showQr = MutableLiveData<Boolean>()
    @JvmField
    val transferProgress = MutableLiveData<Boolean>()
    @JvmField
    val onSenderActionNeed = MutableLiveData<Any>()
    @JvmField
    val ratingData = MutableLiveData<Float>()
    @JvmField
    val onAppAction = MutableLiveData<Any>()

    @JvmStatic
    fun getColor(): LiveData<Boolean> {
        return mutableLiveData
    }

    @JvmStatic
    fun setColor(select: Boolean) {
        mutableLiveData.value = select
    }

    /*@JvmStatic
    fun getCrackTransfer(): LiveData<CrackTransfer> {
        return transferMutableLiveData
    }

    @JvmStatic
    fun setCrackTransfer(crack: CrackTransfer) {
        transferMutableLiveData.postValue(crack)
    }*/

    @JvmStatic
    fun getDialogInfo(): LiveData<Any> {
        return updateDialogMutable
    }

    @JvmStatic
    fun setDialogInfo(mObject: Any) {
        updateDialogMutable.value = mObject
    }

    @JvmStatic
    fun getHotspotName(): LiveData<String> {
        return showHotspot
    }

    @JvmStatic
    fun setHotspotName(mObject: String) {
        showHotspot.value = mObject
    }

    @JvmStatic
    fun setQrCode(showing: Boolean) {
        showQr.postValue(showing)
    }

    @JvmStatic
    fun getQrCode(): LiveData<Boolean> {
        return showQr
    }

    @JvmStatic
    fun setTransferProgress(showing: Boolean) {
        transferProgress.postValue(showing)
    }

    @JvmStatic
    fun getTransferProgress(): LiveData<Boolean> {
        return transferProgress
    }

    @JvmStatic
    fun setSenderAction(action: Any) {
        onSenderActionNeed.postValue(action)
    }

    @JvmStatic
    fun getSenderAction(): LiveData<Any> {
        return onSenderActionNeed
    }

    @JvmStatic
    fun getRating(): MutableLiveData<Float> {
        return ratingData
    }

    @JvmStatic
    fun setRating(rating: Float) {
        ratingData.value = rating
    }

    @JvmStatic
    fun getAppAction(): MutableLiveData<Any> {
        return onAppAction
    }

    @JvmStatic
    fun setAppAction(any: Any) {
        onAppAction.postValue(any)
    }

}