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
import com.faizi.pedometerdemo.model.DistanceTotal
import com.faizi.pedometerdemo.ui.Dialog_Statistics
import com.faizi.pedometerdemo.util.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_detail_pedo.view.*
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.android.synthetic.main.fragment_report.view.*
import kotlinx.android.synthetic.main.fragment_report.view.average_value
import kotlinx.android.synthetic.main.fragment_report.view.chipGroup
import kotlinx.android.synthetic.main.fragment_report.view.emptyData
import kotlinx.android.synthetic.main.fragment_report.view.text_average
import kotlinx.android.synthetic.main.fragment_report.view.text_total
import kotlinx.android.synthetic.main.fragment_report.view.time_graph
import kotlinx.android.synthetic.main.fragment_report.view.total_value
import org.eazegraph.lib.charts.BarChart
import org.eazegraph.lib.models.BarModel
import kotlin.collections.ArrayList


class DetailReportFragment() : Fragment() {

    private var reportType = ""
    private var listCurrentDayInterval: MutableList<Distance> = ArrayList()
    private var listCurrentWeekInterval: MutableList<DistanceTotal> = ArrayList()

    constructor(report: String) : this() {
        this.reportType = report
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report, container, false)
        val database = Database.getInstance(view.context)

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
            return
        }

        val barChart = view.findViewById<BarChart>(R.id.bargraph)
        if (barChart.data.size > 0) barChart.clearChart()

        var bm: BarModel? = null
        for (distance in listCurrentDayInterval) {
            when (graphType) {
                Graph.TIME -> {
                    // convert any to String
                    bm = BarModel(distance.startTimeFormatted, 0.0f, Color.parseColor("#b38ef1"))
                    bm.value = distance.endTime.toFloat()
                    Logger.logs(
                        String.format(
                            "startTime %s endTime %s",
                            distance.startTimeFormatted, distance.endTimeFormatted
                        )
                    )
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
            barChart.addBar(bm)
        }

        if (barChart.data.size > 0) {
            barChart.startAnimation()
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