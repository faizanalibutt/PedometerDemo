package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Display
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import com.dev.bytes.adsmanager.billing.purchaseRemoveAds
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.R
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.app.App
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.activity.Activity
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.activity.MainActivity

class RemoveAdsDialog(private var ctx: Context) : Dialog(ctx) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_remove_ads_dialogue)

        findViewById<View>(R.id.iv_close_remove_ads).setOnClickListener { dismiss() }
        findViewById<View>(R.id.btn_remove).setOnClickListener {
            (ctx as? MainActivity)?.let { App.bp?.purchaseRemoveAds(it) }
        }

    }

    companion object {
        @JvmStatic
        fun show(ctx: Activity) = RemoveAdsDialog(ctx).apply {
            show()
            setCancelable(false)
            val window = window
            if (window != null) {
                val display: Display = ctx.windowManager.defaultDisplay
                val size = Point()
                display.getSize(size)
                val width = size.x
                window.setBackgroundDrawable(ContextCompat.getDrawable(ctx, R.drawable.remove_ads_dialogue_background))
                val windowLayoutParams = window.attributes
                windowLayoutParams.dimAmount = 0.7f
                windowLayoutParams.width = width - convertDpToPx(ctx, 70f).toInt()
                window.attributes = windowLayoutParams
            }
        }
    }

    fun convertDpToPx(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }
}