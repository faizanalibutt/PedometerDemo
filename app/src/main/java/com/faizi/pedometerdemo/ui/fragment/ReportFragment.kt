package com.faizi.pedometerdemo.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.faizi.pedometerdemo.Database
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.util.AppUtils
import com.faizi.pedometerdemo.util.Graph
import com.faizi.pedometerdemo.util.TimeUtils.getDuration
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_report.view.average_value
import kotlinx.android.synthetic.main.fragment_report.view.total_value
import kotlinx.android.synthetic.main.fragment_report_pedo.*
import kotlinx.android.synthetic.main.fragment_report_pedo.view.*
import kotlinx.android.synthetic.main.fragment_report_pedo.view.bargraph1
import kotlinx.android.synthetic.main.fragment_report_pedo.view.chipGroup
import kotlinx.android.synthetic.main.fragment_report_pedo.view.emptyData
import kotlinx.android.synthetic.main.fragment_report_pedo.view.text_average
import kotlinx.android.synthetic.main.fragment_report_pedo.view.text_total

class ReportFragment : Fragment() {

    private var listCurrentWeekInterval: MutableList<Pair<Long, Int>> = ArrayList()

    private var chart: com.github.mikephil.charting.charts.BarChart? = null
    private lateinit var valueFormatter: ValueFormatter

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
            chart!!.visibility = View.INVISIBLE
            return
        }

        valueFormatter = TimeAxisValueFormatter(
            listCurrentWeekInterval,
            chart!!, "pedo"
        )
        chart!!.xAxis.valueFormatter = valueFormatter
        //chart!!.xAxis.setLabelCount(listCurrentWeekInterval.size, false)

        var total = 0.0
        val values: MutableList<BarEntry> = ArrayList()

        for ((index, step) in listCurrentWeekInterval.withIndex()) {

            when (graphType) {

                Graph.TIME -> {

                    var todayOffset = database.getSteps(step.first)
                    var stepsToday = database.currentSteps
                    database.close()

                    if (todayOffset == Integer.MIN_VALUE)
                        todayOffset = -stepsToday

                    stepsToday += todayOffset

                    val stepTime = stepsToday / 10 * 60000.toLong()
                    total += stepTime
                    values.add(BarEntry(index.toFloat(), stepTime.toFloat()))
                    chartData(values, view, graphType)

                }

                Graph.DISTANCE -> {

                    var todayOffset = database.getSteps(step.first)
                    var stepsToday = database.currentSteps
                    database.close()

                    if (todayOffset == Integer.MIN_VALUE)
                        todayOffset = -stepsToday

                    stepsToday += todayOffset

                    // update only every 10 steps when displaying distance
                    val prefs = requireActivity().getSharedPreferences(
                        "pedometer",
                        Context.MODE_PRIVATE
                    )
                    val stepsize =
                        prefs.getFloat("stepsize_value", Fragment_Settings.DEFAULT_STEP_SIZE)

                    var distance_today: Float = stepsToday * stepsize
                    distance_today /= if (prefs.getString("stepsize_unit",
                            Fragment_Settings.DEFAULT_STEP_UNIT) == "cm") {
                        100000f
                    } else {
                        5280f
                    }

                    total += distance_today

                    values.add(BarEntry(index.toFloat(), distance_today))
                    chartData(values, view, graphType)

                }

                Graph.STEP -> {

                    var todayOffset = database.getSteps(step.first)
                    var stepsToday = database.currentSteps
                    database.close()

                    if (todayOffset == Integer.MIN_VALUE)
                        todayOffset = -stepsToday

                    stepsToday += todayOffset

                    total += stepsToday

                    values.add(BarEntry(index.toFloat(), stepsToday.toFloat()))
                    chartData(values, view, graphType)
                }
                else -> {
                }
            }

        }

        when (graphType) {
            Graph.TIME -> {
                view.total_value.text = getDuration(total.toLong())
                view.average_value.text = getDuration(total.toLong() / listCurrentWeekInterval.size)
            }
            Graph.DISTANCE -> {
                view.total_value.text = AppUtils.roundTwoDecimal(total).toString()
                view.average_value.text =
                    AppUtils.roundTwoDecimal(total / listCurrentWeekInterval.size).toString()
            }
            Graph.STEP -> {
                view.total_value.text = AppUtils.roundTwoDecimal(total).toString()
                view.average_value.text =
                    AppUtils.roundTwoDecimal(total / listCurrentWeekInterval.size).toString()
            }
            else -> {
            }
        }

    }


    private fun chartData(
        values: MutableList<BarEntry>,
        view: View,
        graphType: Graph
    ) {

        when (graphType) {
            Graph.TIME -> {
                if (chart!!.data != null && chart!!.data.dataSetCount > 0) {
                    val set1 = chart!!.data.getDataSetByIndex(0) as BarDataSet
                    set1.setDrawValues(true)
                    set1.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return getDuration(value.toLong())
                        }
                    }
                    set1.values = values
                    chart!!.data.notifyDataChanged()
                    chart!!.notifyDataSetChanged()
                } else {
                    val set1 = BarDataSet(values, "")
                    set1.color = ContextCompat.getColor(
                        view.context,
                        R.color.colorPrimaryDark
                    )

                    set1.setDrawValues(true)
                    set1.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return getDuration(value.toLong())
                        }
                    }
                    val dataSets: MutableList<IBarDataSet> = ArrayList()
                    dataSets.add(set1)
                    val data = BarData(dataSets)
                    data.barWidth = 0.2f
                    chart!!.data = data
                    chart!!.setFitBars(true)
                }
                chart!!.invalidate()
            }
            Graph.DISTANCE -> {
                if (chart!!.data != null &&
                    chart!!.data.dataSetCount > 0
                ) {
                    val set1 = chart!!.data.getDataSetByIndex(0) as BarDataSet
                    set1.setDrawValues(true)
                    set1.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return AppUtils.roundTwoDecimal(value.toDouble()).toString()
                        }
                    }
                    set1.values = values
                    chart!!.data.notifyDataChanged()
                    chart!!.notifyDataSetChanged()
                } else {
                    val set1 = BarDataSet(values, "")
                    set1.color = ContextCompat.getColor(
                        view.context,
                        R.color.colorPrimaryDark
                    )

                    set1.setDrawValues(true)
                    set1.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return AppUtils.roundTwoDecimal(value.toDouble()).toString()
                        }
                    }
                    val dataSets: MutableList<IBarDataSet> = ArrayList()
                    dataSets.add(set1)
                    val data = BarData(dataSets)
                    data.barWidth = 0.2f
                    chart!!.data = data
                    chart!!.setFitBars(true)
                }
                chart!!.invalidate()
            }
            Graph.STEP -> {
                if (chart!!.data != null &&
                    chart!!.data.dataSetCount > 0
                ) {
                    val set1 = chart!!.data.getDataSetByIndex(0) as BarDataSet
                    set1.setDrawValues(true)
                    set1.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return AppUtils.roundTwoDecimal(value.toDouble()).toString()
                        }
                    }
                    set1.values = values
                    chart!!.data.notifyDataChanged()
                    chart!!.notifyDataSetChanged()
                } else {
                    val set1 = BarDataSet(values, "")
                    set1.color = ContextCompat.getColor(
                        view.context,
                        R.color.colorPrimaryDark
                    )

                    set1.setDrawValues(true)
                    set1.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return AppUtils.roundTwoDecimal(value.toDouble()).toString()
                        }
                    }
                    val dataSets: MutableList<IBarDataSet> = ArrayList()
                    dataSets.add(set1)
                    val data = BarData(dataSets)
                    data.barWidth = 0.2f
                    chart!!.data = data
                    chart!!.setFitBars(true)
                }
                chart!!.invalidate()
            }
            else -> {}
        }
    }

}