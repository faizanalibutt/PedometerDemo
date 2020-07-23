package com.faizi.pedometerdemo.ui.fragment;

import com.faizi.pedometerdemo.model.Distance;
import com.faizi.pedometerdemo.model.DistanceTotal;
import com.faizi.pedometerdemo.util.AppUtils;
import com.faizi.pedometerdemo.util.Logger;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class SpeedAxisValueFormatterWeek extends ValueFormatter
{

    private final BarLineChartBase<?> chart;
    private List<DistanceTotal> listCurrentWeekInterval;

    public SpeedAxisValueFormatterWeek(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    public SpeedAxisValueFormatterWeek(@NotNull BarChart chart, @NotNull List<DistanceTotal> listCurrentWeekInterval) {
        this.chart = chart;
        this.listCurrentWeekInterval = listCurrentWeekInterval;
    }

    @Override
    public String getFormattedValue(float value) {

        try {
            int index = Math.round(value);
            if (index >= listCurrentWeekInterval.size()) {
                index = listCurrentWeekInterval.size() - 1;
            }
            return listCurrentWeekInterval.get((int) index).getDate();
        } catch (Exception exp) {
            Logger.log(exp + " value is: " + value);
            return "";
        }
    }

}
