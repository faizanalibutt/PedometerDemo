package com.faizi.pedometerdemo.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.text.format.DateUtils
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

object AppUtils {

    var unit = "km"

    fun getDefaultPreferences(activity: AppCompatActivity): SharedPreferences {
        return activity.getSharedPreferences("pedo", MODE_PRIVATE)
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

}