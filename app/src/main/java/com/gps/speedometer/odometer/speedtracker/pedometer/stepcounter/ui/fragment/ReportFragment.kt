package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dev.bytes.adsmanager.ADUnitPlacements
import com.dev.bytes.adsmanager.loadNativeAd
import com.faizi.pedometerdemo.Database
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.util.AppUtils
import com.faizi.pedometerdemo.util.Graph
import com.faizi.pedometerdemo.util.TimeUtils.getDuration
import com.faizi.pedometerdemo.util.Util
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.Util
import kotlinx.android.synthetic.main.fragment_report.view.*
import kotlinx.android.synthetic.main.fragment_report.view.average_value
import kotlinx.android.synthetic.main.fragment_report.view.total_value
import kotlinx.android.synthetic.main.fragment_report_pedo.*
import kotlinx.android.synthetic.main.fragment_report_pedo.view.*
import kotlinx.android.synthetic.main.fragment_report_pedo.view.bargraph1
import kotlinx.android.synthetic.main.fragment_report_pedo.view.chipGroup
import kotlinx.android.synthetic.main.fragment_report_pedo.view.text_average
import kotlinx.android.synthetic.main.fragment_report_pedo.view.text_total
import kotlin.math.pow

@SuppressLint("SetTextI18n")
class ReportFragment : Fragment() {

    private var today: Boolean = true
    private lateinit var mView: View
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
        xAxis.setDrawAxisLine(false)
        xAxis.textSize = 8f
//                xAxis.setCenterAxisLabels(true)
        xAxis.granularity = 1f // only intervals of 1 day

        chart!!.axisLeft.setDrawGridLines(false)
        chart!!.axisRight.setDrawGridLines(false)
        chart!!.axisLeft.isEnabled = false
        chart!!.axisRight.isEnabled = false
        chart!!.legend.isEnabled = false
        chart!!.axisLeft.axisMinimum = 0f
        chart!!.axisRight.axisMinimum = 0f

        // add a nice and smooth animation
        chart!!.animateY(2000)

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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ad_container = view.findViewById<FrameLayout>(R.id.ad_container_pedo)
        view.context.loadNativeAd(
            ad_container,
            R.layout.ad_unified_common,
            ADUnitPlacements.COMMON_NATIVE_AD, true
        )
        mView = view
    }

    override fun onResume() {
        super.onResume()
        mView.let {
            mView.step_graph.isChecked = true
            getIntervalsDataWeekly(Database.getInstance(mView.context), mView, Graph.STEP)
        }
    }

    private fun getIntervalsDataWeekly(database: Database, view: View, graphType: Graph) {

        listCurrentWeekInterval = database.getLastEntries(7)

        if (listCurrentWeekInterval.size == 0) {
            view.text_total.visibility = View.VISIBLE
            view.text_average.visibility = View.VISIBLE
            view.total_value.visibility = View.VISIBLE
            view.average_value.visibility = View.VISIBLE
            view.chipGroup.visibility = View.VISIBLE
            chart!!.setNoDataText("No Data Found")
            chart!!.setBackgroundColor(ContextCompat.getColor(view.context, R.color.white))
            return
        }

        valueFormatter = TimeAxisValueFormatter(
            listCurrentWeekInterval,
            chart!!, "pedo"
        )
        chart!!.xAxis.valueFormatter = valueFormatter

        when (graphType) {

            Graph.TIME -> {
                chart!!.xAxis.labelCount = listCurrentWeekInterval.size
                chart!!.axisRight.axisMaximum =
                    Util.getRandom(10.toDouble().pow(3.toDouble()).toInt()).toFloat()
                chart!!.axisLeft.axisMaximum =
                    Util.getRandom(10.toDouble().pow(3.toDouble()).toInt()).toFloat()
                chart!!.axisLeft.labelCount = listCurrentWeekInterval.size
                chart!!.axisRight.labelCount = listCurrentWeekInterval.size
            }

            Graph.DISTANCE -> {
                chart!!.xAxis.labelCount = listCurrentWeekInterval.size
                chart!!.axisRight.axisMaximum = 10000f
                chart!!.axisLeft.axisMaximum = 10000f
                chart!!.axisLeft.labelCount = listCurrentWeekInterval.size
                chart!!.axisRight.labelCount = listCurrentWeekInterval.size
            }

            Graph.STEP -> {
                chart!!.xAxis.labelCount = listCurrentWeekInterval.size
                chart!!.axisRight.axisMaximum = 8640f
                chart!!.axisLeft.axisMaximum = 8640f
                chart!!.axisLeft.labelCount = listCurrentWeekInterval.size
                chart!!.axisRight.labelCount = listCurrentWeekInterval.size
            }

            else -> {
            }
        }

        var total = 0.0
        chart!!.clear()
        today = true
        val values: MutableList<BarEntry> = ArrayList()

        for ((index, step) in listCurrentWeekInterval.withIndex()) {

            when (graphType) {

                Graph.TIME -> {

                    var steps: Int

                    if (today) {
                        var todayOffset = database.getSteps(step.first)
                        steps = database.currentSteps
                        database.close()

                        if (todayOffset == Integer.MIN_VALUE)
                            todayOffset = -steps

                        steps += todayOffset
                        today = false
                    } else
                        steps = step.second

                    val stepTime = steps / 150 * 60000.toLong()
                    total += stepTime

                    values.add(BarEntry(index.toFloat(), stepTime.toFloat()))
                    chartData(values, view, graphType)

                }

                Graph.DISTANCE -> {

                    var steps: Int

                    if (today) {
                        var todayOffset = database.getSteps(step.first)
                        steps = database.currentSteps
                        database.close()

                        if (todayOffset == Integer.MIN_VALUE)
                            todayOffset = -steps

                        steps += todayOffset
                        today = false
                    } else
                        steps = step.second

                    // update only every 10 steps when displaying distance
                    val prefs = requireActivity().getSharedPreferences(
                        "pedometer",
                        Context.MODE_PRIVATE
                    )
                    val stepsize =
                        prefs.getFloat("stepsize_value", Fragment_Settings.DEFAULT_STEP_SIZE)

                    var distance: Float = steps * stepsize
                    distance /= if (prefs.getString(
                            "stepsize_unit",
                            Fragment_Settings.DEFAULT_STEP_UNIT
                        ) == "cm"
                    ) {
                        100000f
                    } else {
                        5280f
                    }

                    total += distance
                    values.add(BarEntry(index.toFloat(), distance))
                    chartData(values, view, graphType)

                }

                Graph.STEP -> {

                    var steps: Int

                    if (today) {
                        var todayOffset = database.getSteps(step.first)
                        steps = database.currentSteps
                        database.close()

                        if (todayOffset == Integer.MIN_VALUE)
                            todayOffset = -steps

                        steps += todayOffset
                        today = false
                    } else
                        steps = step.second

                    total += steps
                    values.add(BarEntry(index.toFloat(), steps.toFloat()))
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
                view.total_value.text = "${AppUtils.roundTwoDecimal(total).toString()} km"
                view.average_value.text =
                    "${AppUtils.roundTwoDecimal(total / listCurrentWeekInterval.size).toString()} km"
            }
            Graph.STEP -> {
                view.total_value.text = "${AppUtils.roundTwoDecimal(total).toString()} km"
                view.average_value.text =
                    "${AppUtils.roundTwoDecimal(total / listCurrentWeekInterval.size).toString()} km"
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
                    chart!!.data = data
                    if (listCurrentWeekInterval.size > 6) {
                        data.barWidth = 0.1f
                        chart!!.setScaleMinima(
                            5f, 0f
                        )
                        chart!!.moveViewToAnimated(
                            listCurrentWeekInterval.size - 1.toFloat(),
                            0f, YAxis.AxisDependency.RIGHT, 10000
                        )
                    } else {
                        data.barWidth = 0.2f
                        chart!!.setScaleMinima(1f, 0f)
                    }
                    chart!!.setFitBars(true)
                }
                chart!!.invalidate()
            }
            Graph.DISTANCE -> {
                if (chart!!.data != null &&
                    chart!!.data.dataSetCount > 0
                ) {
                    val set1 = chart!!.data.getDataSetByIndex(0) as BarDataSet
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
                            return "${AppUtils.roundTwoDecimal(value.toDouble())} km"
                        }
                    }
                    val dataSets: MutableList<IBarDataSet> = ArrayList()
                    dataSets.add(set1)
                    val data = BarData(dataSets)
                    chart!!.data = data
                    if (listCurrentWeekInterval.size > 6) {
                        data.barWidth = 0.1f
                        chart!!.setScaleMinima(
                            5f, 0f
                        )
                        chart!!.moveViewToAnimated(
                            listCurrentWeekInterval.size - 1.toFloat(),
                            0f, YAxis.AxisDependency.RIGHT, 10000
                        )
                    } else {
                        data.barWidth = 0.2f
                        chart!!.setScaleMinima(1f, 0f)
                    }
                    chart!!.setFitBars(true)
                }
                chart!!.invalidate()
            }
            Graph.STEP -> {
                if (chart!!.data != null &&
                    chart!!.data.dataSetCount > 0
                ) {
                    val set1 = chart!!.data.getDataSetByIndex(0) as BarDataSet
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
                            return "${AppUtils.roundTwoDecimal(value.toDouble())} km"
                        }
                    }
                    val dataSets: MutableList<IBarDataSet> = ArrayList()
                    dataSets.add(set1)
                    val data = BarData(dataSets)
                    chart!!.data = data
                    if (listCurrentWeekInterval.size > 6) {
                        data.barWidth = 0.1f
                        chart!!.setScaleMinima(
                            5f, 0f
                        )
                        chart!!.moveViewToAnimated(
                            listCurrentWeekInterval.size - 1.toFloat(),
                            0f, YAxis.AxisDependency.RIGHT, 10000
                        )
                    } else {
                        data.barWidth = 0.2f
                        chart!!.setScaleMinima(1f, 0f)
                    }
                    chart!!.setFitBars(true)
                }
                chart!!.invalidate()
            }
            else -> {
            }
        }
    }

}