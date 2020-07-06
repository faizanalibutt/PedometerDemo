package com.faizi.pedometerdemo.ui.activity

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.dialog.ExitDialogue

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