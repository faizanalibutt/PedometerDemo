package com.faizi.pedometerdemo.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.faizi.pedometerdemo.Database
import com.faizi.pedometerdemo.R
import de.j4velin.pedometer.ui.Activity_Main

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        Database.getInstance(this@MainActivity)

    }

    fun openSpeedo(view: View) {
        startActivity(Intent(this@MainActivity, MainSpeedometerActivity::class.java))
    }

    fun openPedoMeter(view: View) {
        startActivity(Intent(this@MainActivity, Activity_Main::class.java))
    }
}