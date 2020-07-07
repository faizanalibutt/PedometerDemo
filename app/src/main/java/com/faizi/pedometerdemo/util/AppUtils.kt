package com.faizi.pedometerdemo.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.text.format.DateUtils
import androidx.appcompat.app.AppCompatActivity

object AppUtils {

    fun getDefaultPreferences(activity: AppCompatActivity): SharedPreferences {
        return activity.getSharedPreferences("pedo", MODE_PRIVATE)
    }

    fun formatDateTime(context: Context, millis: Long): CharSequence {
        return DateUtils.formatDateTime(
            context,
            millis,
            DateUtils.FORMAT_SHOW_TIME
        )
    }
}