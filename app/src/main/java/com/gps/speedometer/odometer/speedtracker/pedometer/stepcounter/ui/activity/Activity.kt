package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.activity

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.R
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.dialog.ExitDialogue

abstract class Activity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

    }

    protected var dialog: AlertDialog? = null

    protected open fun showRateExitDialogue(activity: Activity?, isMenu: Boolean): AlertDialog? {
        val rateUsDialog = ExitDialogue (
            activity as AppCompatActivity,
            isMenu
        )
        rateUsDialog.window?.setBackgroundDrawable(getDrawable(R.drawable.background_rate_exit_dialog))
        return rateUsDialog
    }

}