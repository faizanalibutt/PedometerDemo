package com.faizi.pedometerdemo.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.faizi.pedometerdemo.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        nav_back.setOnClickListener {
          finish()
        }

    }
}