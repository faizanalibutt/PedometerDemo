package com.faizi.pedometerdemo.ui.fragment

import android.graphics.Color
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
import com.faizi.pedometerdemo.util.TimeUtils
import com.faizi.pedometerdemo.util.Util
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.android.synthetic.main.fragment_report.view.*
import org.eazegraph.lib.charts.BarChart
import org.eazegraph.lib.models.BarModel


class DetailReportFragment() : Fragment() {

    private var reportType = ""
    private var listCurrentDayInterval: MutableList<Distance> = ArrayList()
    private var listCurrentWeekInterval: MutableList<DistanceTotal> = ArrayList()

    private var chart: com.github.mikephil.charting.charts.BarChart? = null

    constructor(report: String) : this() {
        this.reportType = report
    }
    lateinit var timeFormatter: ValueFormatter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report, container, false)
        val database = Database.getInstance(view.context)
        chart = view.bargraph1

        // set chart properties
        chart!!.description.isEnabled = false
        timeFormatter = DayAxisValueFormatter(chart)

        chart!!.setMaxVisibleValueCount(20)
        chart!!.setDrawBarShadow(true)
        chart!!.setDrawGridBackground(false)

        val xAxis = chart!!.xAxis

        xAxis.position = XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f // only intervals of 1 day
        xAxis.valueFormatter = timeFormatter

        chart!!.axisLeft.setDrawGridLines(false)
        chart!!.axisRight.setDrawGridLines(false)
        chart!!.axisLeft.isEnabled = false
        chart!!.axisRight.isEnabled = false
        chart!!.legend.isEnabled = false

        // add a nice and smooth animation
        chart!!.animateY(1500)

        when (reportType) {

            "week" -> {
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
                val chipGroup: ChipGroup = view.findViewById(R.id.chipGroup)
                chipGroup.setOnCheckedChangeListener { chip_group, i ->
                    when (chip_group.findViewById<Chip>(i)) {
                        time_graph -> {
                            getIntervalsData(database, view, Graph.TIME)
                            view.total_value.text = TimeUtils.getDuration(
                                database.getTodayTotalTime(
                                    TimeUtils.getFormatDateTime(Util.getToday(), "date")
                                )
                            )
                            view.average_value.text = TimeUtils.getDuration(
                                database.getTodayAverageTime(
                                    TimeUtils.getFormatDateTime(Util.getToday(), "date")
                                )
                            )
                        }

                        distance_graph -> {

                            getIntervalsData(database, view, Graph.DISTANCE)

                            view.total_value.text = AppUtils.roundTwoDecimal(
                                database.getTodayTotalDistance(
                                    TimeUtils.getFormatDateTime(Util.getToday(), "date")
                                )
                            ).toString()

                            view.average_value.text = AppUtils.roundTwoDecimal(
                                database.getTodayAverageDistance(
                                    TimeUtils.getFormatDateTime(Util.getToday(), "date")
                                )
                            ).toString()

                        }

                        speed_graph -> {
                            getIntervalsData(database, view, Graph.SPEED)

                            view.total_value.text = AppUtils.roundTwoDecimal(
                                database.getTodayTotalSpeed(
                                    TimeUtils.getFormatDateTime(Util.getToday(), "date")
                                )
                            ).toString()

                            view.average_value.text = AppUtils.roundTwoDecimal(
                                database.getTodayAverageSpeed(
                                    TimeUtils.getFormatDateTime(Util.getToday(), "date")
                                )
                            ).toString()
                        }
                    }
                }
                // default show up Time it is.
                view.time_graph.isChecked = true
                view.total_value.text = TimeUtils.getDuration(
                    database.getTodayTotalTime(
                        TimeUtils.getFormatDateTime(Util.getToday(), "date")
                    )
                )
                view.average_value.text = TimeUtils.getDuration(
                    database.getTodayAverageTime(
                        TimeUtils.getFormatDateTime(Util.getToday(), "date")
                    )
                )
                getIntervalsData(database, view, Graph.TIME)
            }
        }

        return view
    }


    private fun getIntervalsData(database: Database, view: View, graphType: Graph) {
        listCurrentDayInterval = database.getCurrentDayIntervals(
            TimeUtils.getFormatDateTime(Util.getToday(), "date")
        )

        if (listCurrentDayInterval.size == 0) {
            view.emptyData.visibility = View.VISIBLE
            view.text_total.visibility = View.INVISIBLE
            view.text_average.visibility = View.INVISIBLE
            view.chipGroup.visibility = View.INVISIBLE
            chart!!.visibility = View.INVISIBLE
            return
        }


        val values: MutableList<BarEntry> = ArrayList()
        var set1: BarDataSet? = null

        val barChart = view.findViewById<BarChart>(R.id.bargraph)
        if (barChart.data.size > 0) barChart.clearChart()

        var bm: BarModel? = null
        for ((index, distance) in listCurrentDayInterval.withIndex()) {
            when (graphType) {

                Graph.TIME -> {

                    values.add(BarEntry(distance.startTime.toFloat(), distance.endTime.toFloat()))

                    if (chart!!.data != null &&
                        chart!!.data.dataSetCount > 0
                    ) {
                        set1 = chart!!.data.getDataSetByIndex(0) as BarDataSet
                        set1.values = values
                        chart!!.data.notifyDataChanged()
                        chart!!.notifyDataSetChanged()
                    } else {
                        set1 = BarDataSet(values, "")
                        set1.color = ContextCompat.getColor(
                            view.context,
                            R.color.colorPrimaryDark
                        )
                        set1.setDrawValues(true)
                        set1.setValueFormatter(timeFormatter)
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
                    bm = BarModel(
                        "Distance",
                        distance.distance.toFloat(),
                        Color.parseColor("#5b0ce1")
                    )
                    distance.distance.toFloat()
                }

                Graph.SPEED -> {
                    bm = BarModel(
                        "Distance",
                        distance.speed.toFloat(),
                        Color.parseColor("#009688")
                    )
                    distance.speed.toFloat()
                }

                else -> {
                }
            }
//            barChart.addBar(bm)
        }

        if (barChart.data.size > 0) {
//            barChart.startAnimation()
        } else {
            barChart.visibility = View.INVISIBLE
        }
    }

    private fun getIntervalsDataWeekly(database: Database, view: View, graphType: Graph) {

        listCurrentWeekInterval = database.getWeekIntervals(
            TimeUtils.getFormatDateTime(Util.getToday(), "date"),
            TimeUtils.getFormatDateTime(Util.getRandom(-6), "date")
        )

        if (listCurrentWeekInterval.size == 0) {
            view.emptyData.visibility = View.VISIBLE
            view.text_total.visibility = View.INVISIBLE
            view.text_average.visibility = View.INVISIBLE
            view.chipGroup.visibility = View.INVISIBLE
            chart!!.visibility = View.INVISIBLE
            return
        }

        val barChart = view.findViewById<BarChart>(R.id.bargraph)
        if (barChart.data.size > 0) barChart.clearChart()

        var bm: BarModel? = null
        var total = 0.0
        var average = 0.0
        for (distance in listCurrentWeekInterval) {

            when (graphType) {
                Graph.TIME -> {
                    bm = BarModel(TimeUtils.getDuration(distance.sumTime), 0.0f, Color.parseColor("#b38ef1"))
                    total += distance.sumTime
                    average += distance.avgTime
                    bm.value = distance.endTime.toFloat()
                }
                Graph.DISTANCE -> {
                    bm = BarModel(
                        "Distance",
                        distance.distance.toFloat(),
                        Color.parseColor("#5b0ce1")
                    )
                    total += distance.sumDistance
                    average += distance.avgDistance
                    bm.value = distance.sumDistance.toFloat()
                }
                Graph.SPEED -> {
                    bm = BarModel(
                        "Distance",
                        distance.speed.toFloat(),
                        Color.parseColor("#009688")
                    )
                    total += distance.sumSpeed
                    average += distance.avgSpeed
                    bm.value = distance.sumSpeed.toFloat()
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
            Graph.SPEED -> {
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