package com.faizi.pedometerdemo.ui.fragment;

import com.faizi.pedometerdemo.model.Distance;
import com.faizi.pedometerdemo.util.AppUtils;
import com.faizi.pedometerdemo.util.TimeUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class SpeedAxisValueFormatter extends ValueFormatter
{

    private final BarLineChartBase<?> chart;
    private List<Distance> listCurrentDayInterval;

    public SpeedAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    public SpeedAxisValueFormatter(@NotNull BarChart chart, @NotNull List<Distance> listCurrentDayInterval) {
        this.chart = chart;
        this.listCurrentDayInterval = listCurrentDayInterval;
    }

    @Override
    public String getFormattedValue(float value) {

        return String.valueOf(AppUtils.INSTANCE.roundTwoDecimal(listCurrentDayInterval.get((int) value).getSpeed()));
    }

}
