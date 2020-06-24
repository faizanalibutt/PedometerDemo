package com.faizi.pedometerdemo.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.faizi.pedometerdemo.Database
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.model.Distance
import org.eazegraph.lib.charts.BarChart
import org.eazegraph.lib.models.BarModel

class SpeedoGraphActivity : AppCompatActivity() {

    var columns = arrayOf<String>(
        "start_time",
        "end_time"
    )

    val list: MutableList<Distance> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speedo_graph)

        val barChart = findViewById<BarChart>(R.id.bargraph)
        if (barChart.data.size > 0) barChart.clearChart()

        var bm: BarModel


        val database = Database.getInstance(this@SpeedoGraphActivity)
        val cursor = database.query(Database.TABLE_SPEED, columns,
            null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val start_time = cursor.getLong(cursor.getColumnIndex("start_time"))
                val end_time = cursor.getLong(cursor.getColumnIndex("end_time"))
                //val avg_spedd = cursor.getDouble(cursor.getColumnIndex("speed"))
                //val distance = cursor.getDouble(cursor.getColumnIndex("distance"))
                val distanceObj = Distance(start_time, end_time, 0.0, 0.0)
                bm = BarModel(start_time.toString(), 0.0f, Color.parseColor("#99CC00"))
                bm.value = end_time.toFloat()
                barChart.addBar(bm)
                list.add(distanceObj)
                cursor.moveToNext()
            }
        }

        if (barChart.data.size > 0) {
            barChart.setOnClickListener {
                Dialog_Statistics.getDialog(this, 0).show()
            }
            barChart.startAnimation()
        } else {
            barChart.visibility = View.GONE
        }
    }
}