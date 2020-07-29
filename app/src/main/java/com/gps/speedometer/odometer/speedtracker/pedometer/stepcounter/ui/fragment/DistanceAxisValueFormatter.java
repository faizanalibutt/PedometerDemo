package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.fragment;

import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.model.Distance;
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.AppUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class DistanceAxisValueFormatter extends ValueFormatter
{

    private final BarLineChartBase<?> chart;
    private List<Distance> listCurrentDayInterval;

    public DistanceAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    public DistanceAxisValueFormatter(@NotNull BarChart chart, @NotNull List<Distance> listCurrentDayInterval) {
        this.chart = chart;
        this.listCurrentDayInterval = listCurrentDayInterval;
    }

    @Override
    public String getFormattedValue(float value) {

        return String.valueOf(AppUtils.INSTANCE.roundTwoDecimal(listCurrentDayInterval.get((int) value).getDistance()) + " km");
    }

}
