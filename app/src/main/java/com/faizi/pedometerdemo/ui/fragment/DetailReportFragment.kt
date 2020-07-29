package com.faizi.pedometerdemo.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.faizi.pedometerdemo.Database
import com.faizi.pedometerdemo.R
import com.faizi.pedometerdemo.model.Distance
import com.faizi.pedometerdemo.model.DistanceTotal
import com.faizi.pedometerdemo.util.AppUtils
import com.faizi.pedometerdemo.util.Graph
import com.faizi.pedometerdemo.util.TimeUtils.getDuration
import com.faizi.pedometerdemo.util.TimeUtils.getFormatDateTime
import com.faizi.pedometerdemo.util.Util
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.android.synthetic.main.fragment_report.view.*
import kotlin.math.pow

@SuppressLint("SetTextI18n")
class DetailReportFragment() : Fragment() {

    private var reportType = ""
    private var listCurrentDayInterval: MutableList<Distance> = ArrayList()
    private var listCurrentWeekInterval: MutableList<DistanceTotal> = ArrayList()

    private var chart: com.github.mikephil.charting.charts.BarChart? = null

    constructor(report: String) : this() {
        this.reportType = report
    }

    private lateinit var valueFormatter: ValueFormatter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report, container, false)
        val database = Database.getInstance(view.context)

        when (reportType) {

            "week" -> {

                chart = view.bargraph1

                // set chart properties
                chart!!.description.isEnabled = false
                chart!!.setDrawBarShadow(true)
                chart!!.setDrawGridBackground(false)

                val xAxis = chart!!.xAxis

                xAxis.position = XAxisPosition.BOTTOM
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

                chipGroup.setOnCheckedChangeListener { chip_group, i ->
                    when (chip_group.findViewById<Chip>(i)) {
                        time_graph -> {
                            getIntervalsDataWeekly(database, view, Graph.TIME)
                        }
                        distance_graph -> {
                            getIntervalsDataWeekly(database, view, Graph.DISTANCE)
                        }
                        speed_graph -> {
                            getIntervalsDataWeekly(database, view, Graph.SPEED)
                        }
                    }
                }

                view.time_graph.isChecked = true
                getIntervalsDataWeekly(database, view, Graph.TIME)

            }

            "today" -> {

                chart = view.bargraph1

                // set chart properties
                chart!!.description.isEnabled = false
                chart!!.setDrawBarShadow(true)
                chart!!.setDrawGridBackground(false)

                val xAxis = chart!!.xAxis

                xAxis.position = XAxisPosition.BOTTOM
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
                chipGroup.setOnCheckedChangeListener { chip_group, i ->
                    when (chip_group.findViewById<Chip>(i)) {
                        time_graph -> {
                            getIntervalsData(database, view, Graph.TIME)
                            view.total_value.text = getDuration(
                                database.getTodayTotalTime(
                                    getFormatDateTime(Util.getToday(), "date")
                                )
                            )
                            view.average_value.text = getDuration(
                                database.getTodayAverageTime(
                                    getFormatDateTime(Util.getToday(), "date")
                                )
                            )
                        }

                        distance_graph -> {

                            getIntervalsData(database, view, Graph.DISTANCE)

                            view.total_value.text = "${AppUtils.roundTwoDecimal(
                                database.getTodayTotalDistance(
                                    getFormatDateTime(Util.getToday(), "date")
                                )
                            )} km"

                            view.average_value.text = "${AppUtils.roundTwoDecimal(
                                database.getTodayTotalDistance(
                                    getFormatDateTime(Util.getToday(), "date")
                                )
                            )} km"

                        }

                        speed_graph -> {
                            getIntervalsData(database, view, Graph.SPEED)

                            view.total_value.text = "${AppUtils.roundTwoDecimal(
                                database.getTodayTotalSpeed(
                                    getFormatDateTime(Util.getToday(), "date")
                                )
                            )} km"

                            view.average_value.text = "${AppUtils.roundTwoDecimal(
                                database.getTodayAverageSpeed(
                                    getFormatDateTime(Util.getToday(), "date")
                                )
                            )} km"
                        }
                    }
                }
                // default show up Time it is.
                val totalValue = database.getTodayTotalTime(
                    getFormatDateTime(Util.getToday(), "date")
                )
                val averageValue = database.getTodayAverageTime(
                    getFormatDateTime(Util.getToday(), "date")
                )
                view.time_graph.isChecked = true
                view.total_value.text = if (totalValue == 0L) "00" else getDuration(
                    totalValue
                )
                view.average_value.text = if (averageValue == 0L) "00" else getDuration(
                    averageValue
                )
                getIntervalsData(database, view, Graph.TIME)
            }
        }

        return view
    }


    private fun getIntervalsData(database: Database, view: View, graphType: Graph) {

        listCurrentDayInterval = database.getCurrentDayIntervals(
            getFormatDateTime(Util.getToday(), "date")
        )

        if (listCurrentDayInterval.size == 0) {
            view.text_total.visibility = View.VISIBLE
            view.text_average.visibility = View.VISIBLE
            view.total_value.visibility = View.VISIBLE
            view.average_value.visibility = View.VISIBLE
            view.chipGroup.visibility = View.VISIBLE
            chart!!.setNoDataText("No Data Found")
            chart!!.setBackgroundColor(ContextCompat.getColor(view.context, R.color.white))
            return
        }

        when (graphType) {

            Graph.TIME -> {
                valueFormatter =
                    TimeAxisValueFormatter(
                        chart!!,
                        listCurrentDayInterval
                    )
                chart!!.xAxis.valueFormatter = valueFormatter
                chart!!.xAxis.labelCount = listCurrentDayInterval.size
                chart!!.axisRight.axisMaximum =
                    Util.getRandom(10.toDouble().pow(5.toDouble()).toInt()).toFloat()
                chart!!.axisLeft.axisMaximum =
                    Util.getRandom(10.toDouble().pow(5.toDouble()).toInt()).toFloat()
                chart!!.axisLeft.labelCount = listCurrentDayInterval.size
                chart!!.axisRight.labelCount = listCurrentDayInterval.size
            }

            Graph.DISTANCE -> {
                valueFormatter =
                    DistanceAxisValueFormatter(
                        chart!!,
                        listCurrentDayInterval
                    )
                chart!!.xAxis.valueFormatter = valueFormatter
                chart!!.xAxis.labelCount = listCurrentDayInterval.size
                chart!!.axisRight.axisMaximum = 10000f
                chart!!.axisLeft.axisMaximum = 10000f
                chart!!.axisLeft.labelCount = listCurrentDayInterval.size
                chart!!.axisRight.labelCount = listCurrentDayInterval.size
            }

            Graph.SPEED -> {
                valueFormatter =
                    SpeedAxisValueFormatter(
                        chart!!,
                        listCurrentDayInterval
                    )
                chart!!.xAxis.valueFormatter = valueFormatter
                chart!!.xAxis.labelCount = listCurrentDayInterval.size
                chart!!.axisRight.axisMaximum = 8640f
                chart!!.axisLeft.axisMaximum = 8640f
                chart!!.axisLeft.labelCount = listCurrentDayInterval.size
                chart!!.axisRight.labelCount = listCurrentDayInterval.size
            }

            else -> {
            }

        }

        val values: MutableList<BarEntry> = ArrayList()
        chart!!.clear()

        for ((index, distance) in listCurrentDayInterval.withIndex()) {
            when (graphType) {

                Graph.TIME -> {
                    values.add(BarEntry(index.toFloat(), distance.endTime.toFloat()))
                    chartData(values, view, graphType)
                }

                Graph.DISTANCE -> {
                    values.add(BarEntry(index.toFloat(), distance.distance.toFloat()))
                    chartData(values, view, graphType)
                }

                Graph.SPEED -> {
                    values.add(BarEntry(index.toFloat(), distance.speed.toFloat()))
                    chartData(values, view, graphType)
                }

                else -> {
                }
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
                            return getFormatDateTime(value.toLong(), "time")
                        }
                    }

                    val dataSets: MutableList<IBarDataSet> = ArrayList()
                    dataSets.add(set1)
                    val data = BarData(dataSets)
                    chart!!.data = data
                    if (listCurrentDayInterval.size > 6) {
                        data.barWidth = 0.1f
                        chart!!.setScaleMinima(
                            if (listCurrentDayInterval.size > 10)
                                listCurrentDayInterval.size / 5.toFloat()
                            else
                                5f, 0f
                        )
                        chart!!.moveViewToAnimated(
                            listCurrentDayInterval.size - 1.toFloat(),
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
                    if (listCurrentDayInterval.size > 6) {
                        data.barWidth = 0.1f
                        chart!!.setScaleMinima(
                            if (listCurrentDayInterval.size > 10)
                                listCurrentDayInterval.size / 5.toFloat()
                            else
                                5f, 0f
                        )
                        chart!!.moveViewToAnimated(
                            listCurrentDayInterval.size - 1.toFloat(),
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
            Graph.SPEED -> {
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
                    if (listCurrentDayInterval.size > 6) {
                        data.barWidth = 0.1f
                        chart!!.setScaleMinima(
                            if (listCurrentDayInterval.size > 10)
                                listCurrentDayInterval.size / 5.toFloat()
                            else
                                5f, 0f
                        )
                        chart!!.moveViewToAnimated(
                            listCurrentDayInterval.size - 1.toFloat(),
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

    private fun getIntervalsDataWeekly(database: Database, view: View, graphType: Graph) {

        listCurrentWeekInterval = database.getWeekIntervals(
            getFormatDateTime(Util.getToday(), "date"),
            getFormatDateTime(Util.getRandom(-6), "date")
        )

//        if (listCurrentDayInterval.size < 7) {
//            database.saveInterval(Distance(0, 0,
//                0.toDouble(), 0.toDouble(), "", 0))
//        }

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

        when (graphType) {

            Graph.TIME -> {
                valueFormatter =
                    TimeAxisValueFormatterWeek(
                        chart!!,
                        listCurrentWeekInterval
                    )
                chart!!.xAxis.valueFormatter = valueFormatter
                chart!!.xAxis.labelCount = listCurrentWeekInterval.size
                chart!!.axisRight.axisMaximum =
                    Util.getRandom(10.toDouble().pow(5.toDouble()).toInt()).toFloat()
                chart!!.axisLeft.axisMaximum =
                    Util.getRandom(10.toDouble().pow(5.toDouble()).toInt()).toFloat()
                chart!!.axisLeft.labelCount = listCurrentWeekInterval.size
                chart!!.axisRight.labelCount = listCurrentWeekInterval.size
            }

            Graph.DISTANCE -> {
                valueFormatter =
                    DistanceAxisValueFormatterWeek(
                        chart!!,
                        listCurrentWeekInterval
                    )
                chart!!.xAxis.valueFormatter = valueFormatter
                chart!!.xAxis.labelCount = listCurrentWeekInterval.size
                chart!!.axisRight.axisMaximum = 10000f
                chart!!.axisLeft.axisMaximum = 10000f
                chart!!.axisLeft.labelCount = listCurrentWeekInterval.size
                chart!!.axisRight.labelCount = listCurrentWeekInterval.size
            }

            Graph.SPEED -> {
                valueFormatter =
                    SpeedAxisValueFormatterWeek(
                        chart!!,
                        listCurrentWeekInterval
                    )
                chart!!.xAxis.valueFormatter = valueFormatter
                chart!!.xAxis.labelCount = listCurrentWeekInterval.size
                chart!!.axisRight.axisMaximum = 8640f
                chart!!.axisLeft.axisMaximum = 8640f
                chart!!.axisLeft.labelCount = listCurrentWeekInterval.size
                chart!!.axisRight.labelCount = listCurrentWeekInterval.size
            }

            else -> {
            }
        }

        val values: MutableList<BarEntry> = ArrayList()
        chart!!.clear()
        var total = 0.0
        var average = 0.0

        for ((index, distance) in listCurrentWeekInterval.withIndex()) {

            when (graphType) {
                Graph.TIME -> {

                    values.add(BarEntry(index.toFloat(), distance.sumTime.toFloat()))
                    chartDataWeek(values, view, graphType)

                    total += distance.sumTime
                    average += distance.avgTime

                }
                Graph.DISTANCE -> {

                    values.add(BarEntry(index.toFloat(), distance.sumDistance.toFloat()))
                    chartDataWeek(values, view, graphType)

                    total += distance.sumDistance
                    average += distance.avgDistance

                }
                Graph.SPEED -> {
                    values.add(BarEntry(index.toFloat(), distance.sumSpeed.toFloat()))
                    chartDataWeek(values, view, graphType)

                    total += distance.sumSpeed
                    average += distance.avgSpeed
                }
                else -> {
                }
            }

        }

        when (graphType) {
            Graph.TIME -> {
                view.total_value.text = getDuration(total.toLong())
                view.average_value.text =
                    getDuration(average.toLong() / listCurrentWeekInterval.size)
            }
            Graph.DISTANCE -> {
                view.total_value.text = "${AppUtils.roundTwoDecimal(total).toString()} km"
                view.average_value.text =
                    "${AppUtils.roundTwoDecimal(average / listCurrentWeekInterval.size).toString()} km"
            }
            Graph.SPEED -> {
                view.total_value.text = "${AppUtils.roundTwoDecimal(total).toString()} km"
                view.average_value.text =
                    "${AppUtils.roundTwoDecimal(average / listCurrentWeekInterval.size).toString()} km"
            }
            else -> {
            }
        }

    }

    private fun chartDataWeek(
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
            Graph.SPEED -> {
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