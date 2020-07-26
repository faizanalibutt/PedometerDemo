package com.faizi.pedometerdemo.ui.fragment;

import android.util.Pair;

import com.faizi.pedometerdemo.model.Distance;
import com.faizi.pedometerdemo.util.Logger;
import com.faizi.pedometerdemo.util.TimeUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class TimeAxisValueFormatter extends ValueFormatter {

    private final BarLineChartBase<?> chart;
    private List<Distance> listCurrentDayInterval;
    private List<Pair<Long, Integer>> listCurrentWeekInterval;
    private String type = null;

    public TimeAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    public TimeAxisValueFormatter(@NotNull BarChart chart, @NotNull List<Distance> listCurrentDayInterval) {
        this.chart = chart;
        this.listCurrentDayInterval = listCurrentDayInterval;
    }

    public TimeAxisValueFormatter(@NotNull List<Pair<Long, Integer>> listCurrentWeekInterval, @NotNull BarChart chart, String type) {
        this.chart = chart;
        this.listCurrentWeekInterval = listCurrentWeekInterval;
        this.type = type;
    }

    @Override
    public String getFormattedValue(float value) {
        try {
            int index = Math.round(value);

            if (type == null) {
                if (index >= listCurrentDayInterval.size()) {
                    index = listCurrentDayInterval.size() - 1;
                }
                return TimeUtils.INSTANCE.getFormatDateTime
                        (listCurrentDayInterval.get((int) index).getStartTime(), "time");
            } else {
                if (index >= listCurrentWeekInterval.size()) {
                    index = listCurrentWeekInterval.size() - 1;
                }
                return TimeUtils.INSTANCE.getFormatDate((listCurrentWeekInterval.get((int) index).first));
            }

        } catch (Exception exp) {
            Logger.log(exp + " value is: " + value);
            return "";
        }

    }

}
