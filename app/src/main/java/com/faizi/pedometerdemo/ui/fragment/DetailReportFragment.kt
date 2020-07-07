package com.faizi.pedometerdemo.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.faizi.pedometerdemo.Database
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.model.Distance
import com.faizi.pedometerdemo.ui.Dialog_Statistics
import com.faizi.pedometerdemo.util.AppUtils
import com.faizi.pedometerdemo.util.CommonUtils
import com.faizi.pedometerdemo.util.Graph
import com.faizi.pedometerdemo.util.Logger
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.android.synthetic.main.fragment_report.view.*
import org.eazegraph.lib.charts.BarChart
import org.eazegraph.lib.models.BarModel


class DetailReportFragment() : Fragment() {

    private var reportType = ""
    private var listCurrentDayInterval: MutableList<Distance> = ArrayList()

    constructor(report: String) : this() {
        this.reportType = report
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report, container, false)

        when (reportType) {
            "week" -> {
                /*val chipGroup: ChipGroup = view.findViewById(R.id.chipGroup)
                time_graph.isCheckable = true
                val database = Database.getInstance(view.context)

                chipGroup.setOnCheckedChangeListener { chip_group, i ->
                    when (chip_group.findViewById<Chip>(i)) {
                        time_graph -> { getIntervalsData(arrayOf("start_time", "end_time"), database) }
                        distance_graph -> { getIntervalsData(arrayOf("distance"), database) }
                        speed_graph -> { getIntervalsData(arrayOf("speed"), database) }
                    }
                }*/
            }
            "today" -> {
                val chipGroup: ChipGroup = view.findViewById(R.id.chipGroup)
                val database = Database.getInstance(view.context)
                chipGroup.setOnCheckedChangeListener { chip_group, i ->
                    when (chip_group.findViewById<Chip>(i)) {
                        time_graph -> {
                            getIntervalsData(
                                database,
                                view,
                                Graph.TIME
                            )
                        }
                        distance_graph -> {
                            getIntervalsData(database, view, Graph.DISTANCE)
                        }
                        speed_graph -> {
                            getIntervalsData(
                                database,
                                view,
                                Graph.SPEED
                            )
                        }
                    }
                }
                // default show up Time it is.
                view.time_graph.isChecked = true
                getIntervalsData(database, view, Graph.TIME)
            }
        }

        return view
    }

    private fun getIntervalsData(
        database: Database,
        view: View,
        graphType: Graph
    ) {
        listCurrentDayInterval = database.currentDayIntervals
        val barChart = view.findViewById<BarChart>(R.id.bargraph)
        if (barChart.data.size > 0) barChart.clearChart()

        var bm: BarModel? = null
        var totalValue = 0
        for (distance in listCurrentDayInterval) {
            when (graphType) {
                Graph.TIME -> {
                    // convert any to String
                    bm = BarModel(distance.startTimeFormatted, 0.0f, Color.parseColor("#b38ef1"))
                    bm.value = distance.endTime.toFloat()
                    Logger.log(distance.startTime.toString())
                    totalValue += distance.totalTime.toInt()
                    view.total_value.text = CommonUtils.getFormatedTimeMHS(distance.totalTime)
                }
                Graph.DISTANCE -> {
                    bm = BarModel("Distance", distance.distance.toFloat(), Color.parseColor("#5b0ce1"))
                    distance.distance.toFloat()
                }
                Graph.SPEED -> {
                    bm = BarModel("Distance", distance.distance.toFloat(), Color.parseColor("#009688"))
                    distance.speed.toFloat()
                }
                else -> {
                }
            }
            barChart.addBar(bm)
        }

        if (barChart.data.size > 0) {
            barChart.setOnClickListener {
                Dialog_Statistics.getDialog(view.context, 0).show()
            }
            barChart.startAnimation()
        } else {
            barChart.visibility = View.VISIBLE
        }
    }

}