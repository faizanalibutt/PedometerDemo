package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.R
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.activity.Activity
import kotlin.math.roundToInt

object AppUtils {

    var unit = "km"

    fun getDefaultPreferences(activity: AppCompatActivity): SharedPreferences {
        return activity.getSharedPreferences("pedo", MODE_PRIVATE)
    }

    fun getDefaultPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("pedo", MODE_PRIVATE)
    }

    fun roundTwoDecimal(d: Double): Double {
        return try {
            (d * 100.0).roundToInt() / 100.0
        } catch (e: Exception) {
            0.0
        } catch (e: IllegalArgumentException) {
            0.0
        }
    }

    fun animateProButton(activity: Activity, imagePro: ImageView) {
        val animation_zoom_enter =
            AnimationUtils.loadAnimation(activity, R.anim.pro_btn_animation)

        val hanlder = Handler()
        hanlder.postDelayed(object : Runnable {
            override fun run() {
                imagePro.startAnimation(animation_zoom_enter)
                hanlder.postDelayed(this, 1000)
            }
        }, 500)
    }

}