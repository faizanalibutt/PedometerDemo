package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.R
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.app.App
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.dialog.ExitDialogue
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.Utility
import kotlin.reflect.KParameter

open class Activity : AppCompatActivity() {

    private val TAG = "BaseActivity"

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            Log.d(TAG, "onCreate")
            Utility.resetActivityTitle(this)
        } catch (exp: ConcurrentModificationException) {}
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (overrideConfiguration != null) {
            val uiMode: Int = overrideConfiguration.uiMode
            overrideConfiguration.setTo(baseContext.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    protected var dialog: AlertDialog? = null

    protected open fun showRateExitDialogue(activity: Activity?, isMenu: Boolean): AlertDialog? {
        val rateUsDialog = ExitDialogue(
            activity as AppCompatActivity, isMenu
        )
        rateUsDialog.window?.setBackgroundDrawable(getDrawable(R.drawable.background_rate_exit_dialog))
        return rateUsDialog
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(App.localeManager?.setLocale(base))
        Log.d(TAG, "attachBaseContext")
    }

}