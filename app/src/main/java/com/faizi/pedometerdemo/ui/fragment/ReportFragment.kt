package com.faizi.pedometerdemo.ui.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.faizi.pedometerdemo.Database
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.util.AppUtils
import com.faizi.pedometerdemo.util.Graph
import com.faizi.pedometerdemo.util.TimeUtils
import com.faizi.pedometerdemo.util.TimeUtils.getDuration
import com.faizi.pedometerdemo.util.Util
import com.github.mikephil.charting.components.XAxis
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.android.synthetic.main.fragment_report.view.*
import kotlinx.android.synthetic.main.fragment_report.view.average_value
import kotlinx.android.synthetic.main.fragment_report.view.total_value
import kotlinx.android.synthetic.main.fragment_report_pedo.*
import kotlinx.android.synthetic.main.fragment_report_pedo.distance_graph
import kotlinx.android.synthetic.main.fragment_report_pedo.time_graph
import kotlinx.android.synthetic.main.fragment_report_pedo.view.*
import kotlinx.android.synthetic.main.fragment_report_pedo.view.bargraph1
import kotlinx.android.synthetic.main.fragment_report_pedo.view.chipGroup
import kotlinx.android.synthetic.main.fragment_report_pedo.view.emptyData
import kotlinx.android.synthetic.main.fragment_report_pedo.view.text_average
import kotlinx.android.synthetic.main.fragment_report_pedo.view.text_total
import kotlinx.android.synthetic.main.fragment_report_pedo.view.time_graph
import org.eazegraph.lib.charts.BarChart
import org.eazegraph.lib.models.BarModel

class ReportFragment : Fragment() {

    private var listCurrentWeekInterval: MutableList<Pair<Long, Int>> = ArrayList()

    private var chart: com.github.mikephil.charting.charts.BarChart? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_report_pedo, container, false)

        chart = view.bargraph1

        // set chart properties
        chart!!.description.isEnabled = false
        chart!!.setDrawBarShadow(true)
        chart!!.setDrawGridBackground(false)

        val xAxis = chart!!.xAxis

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.isGranularityEnabled = true
        xAxis.granularity = 1f // only intervals of 1 day

        chart!!.axisLeft.setDrawGridLines(false)
        chart!!.axisRight.setDrawGridLines(false)
        chart!!.axisLeft.isEnabled = false
        chart!!.axisRight.isEnabled = false
        chart!!.legend.isEnabled = false

        // add a nice and smooth animation
        chart!!.animateY(1500)

        val chipGroup: ChipGroup = view.findViewById(R.id.chipGroup)
        val database = Database.getInstance(view.context)

        chipGroup.setOnCheckedChangeListener { chip_group, i ->
            when (chip_group.findViewById<Chip>(i)) {
                time_graph -> {
                    getIntervalsDataWeekly(database, view, Graph.TIME)
                }
                distance_graph -> {
                    getIntervalsDataWeekly(database, view, Graph.DISTANCE)
                }
                step_graph -> {
                    getIntervalsDataWeekly(database, view, Graph.STEP)
                }
            }
        }

        view.step_graph.isChecked = true
        getIntervalsDataWeekly(database, view, Graph.STEP)

        return view
    }

    private fun getIntervalsDataWeekly(database: Database, view: View, graphType: Graph) {

        listCurrentWeekInterval = database.getLastEntries(7)

        if (listCurrentWeekInterval.size == 0) {
            view.emptyData.visibility = View.VISIBLE
            view.text_total.visibility = View.INVISIBLE
            view.text_average.visibility = View.INVISIBLE
            view.chipGroup.visibility = View.INVISIBLE
            return
        }

        val barChart = view.findViewById<BarChart>(R.id.bargraph)
        if (barChart.data.size > 0) barChart.clearChart()

        var bm: BarModel? = null
        var total = 0.0
        var average = 0.0
        for (step in listCurrentWeekInterval) {

            when (graphType) {
                Graph.TIME -> {
                    bm = BarModel(getDuration(step.second / 10 * 60000.toLong()), 0.0f, Color.parseColor("#b38ef1"))
                    total += step.second / 10 * 60000.toLong()
                    average += step.second / 10 * 60000.toLong()
                    bm.value = step.second / 10 * 60000.toLong().toFloat()
                }
                Graph.DISTANCE -> {
                    // update only every 10 steps when displaying distance
                    val prefs = activity!!.getSharedPreferences(
                        "pedometer",
                        Context.MODE_PRIVATE
                    )
                    val stepsize =
                        prefs.getFloat("stepsize_value", Fragment_Settings.DEFAULT_STEP_SIZE)
                    var distance_today: Float = step.second * stepsize
                    distance_today /= if (prefs.getString(
                            "stepsize_unit",
                            Fragment_Settings.DEFAULT_STEP_UNIT
                        )
                        == "cm"
                    ) {
                        100000f
                    } else {
                        5280f
                    }
                    bm = BarModel(
                        "Distance",
                        distance_today,
                        Color.parseColor("#5b0ce1")
                    )
                    total += distance_today
                    average += distance_today
                    bm.value = distance_today
                }
                Graph.STEP -> {
                    bm = BarModel(
                        "Distance",
                        step.second.toFloat(),
                        Color.parseColor("#009688")
                    )
                    total += step.second
                    average += step.second
                    bm.value = step.second.toFloat()
                }
                else -> {}
            }

            barChart.addBar(bm)
        }

        when (graphType) {
            Graph.TIME -> {
                view.total_value.text = TimeUtils.getDuration(total.toLong())
                view.average_value.text = TimeUtils.getDuration(average.toLong() / listCurrentWeekInterval.size)
            }
            Graph.DISTANCE -> {
                view.total_value.text = AppUtils.roundTwoDecimal(total).toString()
                view.average_value.text = AppUtils.roundTwoDecimal(average / listCurrentWeekInterval.size).toString()
            }
            Graph.STEP -> {
                view.total_value.text = AppUtils.roundTwoDecimal(total).toString()
                view.average_value.text = AppUtils.roundTwoDecimal(average / listCurrentWeekInterval.size).toString()
            }
            else -> {}
        }

        if (barChart.data.size > 0) {
            barChart.startAnimation()
        } else {
            barChart.visibility = View.INVISIBLE
        }
    }

}