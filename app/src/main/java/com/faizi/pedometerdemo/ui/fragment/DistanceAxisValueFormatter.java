package com.faizi.pedometerdemo.ui.fragment;

import android.util.Pair;

import com.faizi.pedometerdemo.model.Distance;
import com.faizi.pedometerdemo.util.AppUtils;
import com.faizi.pedometerdemo.util.TimeUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.util.List;


public class DistanceAxisValueFormatter extends ValueFormatter
{

    private final BarLineChartBase<?> chart;
    private String type = null;
    private List<Distance> listCurrentDayInterval;
    private List<Pair<Long, Integer>> listCurrentWeekInterval;

    public DistanceAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    public DistanceAxisValueFormatter(@NotNull BarChart chart, @NotNull List<Distance> listCurrentDayInterval) {
        this.chart = chart;
        this.listCurrentDayInterval = listCurrentDayInterval;
    }

    public DistanceAxisValueFormatter(@NotNull BarChart chart, @NotNull List<Pair<Long, Integer>> listCurrentWeekInterval, @NotNull String type) {
        this.chart = chart;
        this.listCurrentWeekInterval = listCurrentWeekInterval;
        this.type = type;
    }

    @Override
    public String getFormattedValue(float value) {
        if (type == null) {
            return String.valueOf(AppUtils.INSTANCE.roundTwoDecimal(listCurrentDayInterval.get((int) value).getDistance()));
        } else {
            return String.valueOf(AppUtils.INSTANCE.roundTwoDecimal(listCurrentDayInterval.get((int) value).getDistance()));
        }
    }

}
