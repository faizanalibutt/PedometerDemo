package com.faizi.pedometerdemo.util

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

object AppUtils {

    fun getDefaultPreferences(activity: AppCompatActivity): SharedPreferences {
        return activity.getSharedPreferences("pedo", MODE_PRIVATE)
    }
}