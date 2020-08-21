/*
 * Copyright 2014 Thomas Hoffmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.dev.bytes.adsmanager.ADUnitPlacements;
import com.dev.bytes.adsmanager.NativeAdsManagerKt;
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.BuildConfig;
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.Database;
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.R;
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.SensorListener;
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.ui.activity.PedometerActivity;
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.AppUtils;
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.Logger;
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.TimeUtils;
import com.gps.speedometer.odometer.speedtracker.pedometer.stepcounter.util.Util;
import com.natasa.progressviews.CircleSegmentBar;
import com.natasa.progressviews.utils.ProgressStartPoint;

import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class PedoMeterFragmentNew extends Fragment implements SensorEventListener {

    private TextView stepsView, totalView, miles;
    private PieModel sliceGoal, sliceCurrent;
    //    private PieChart pg;
    private View start_btn;
    ImageView step_btn_img;
    TextView step_btn_txt, timeValue;
    private View mView;
    private CircleSegmentBar segmentBar;
    private String pedoState;

    private int todayOffset, total_start, goal, since_boot, total_days;
    public final static NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());

//    SpeedViewModel mViewModel = null;

    private void initSegmentProgressBar() {
        segmentBar = mView.findViewById(R.id.pedo_process_graph1);
        segmentBar.setCircleViewPadding(1);
        segmentBar.setSegmentWidth(1);
        segmentBar.setStartPositionInDegrees(ProgressStartPoint.BOTTOM);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_pedometer_new, null);

        mView = v;

        stepsView = v.findViewById(R.id.steps);
        miles = v.findViewById(R.id.distance_value);
        timeValue = v.findViewById(R.id.time_value);

        start_btn = v.findViewById(R.id.start_btn);
        step_btn_img = v.findViewById(R.id.step_btn_img);
        step_btn_txt = v.findViewById(R.id.step_btn_txt);

        // slice for the steps taken today
        sliceCurrent = new PieModel("", 0, Color.parseColor("#99CC00"));

        // slice for the "missing" steps until reaching the goal
        sliceGoal = new PieModel("", Fragment_Settings.DEFAULT_GOAL, Color.parseColor("#CC0000"));

        initSegmentProgressBar();


        start_btn.setOnClickListener(v1 -> {

            if (step_btn_txt.getText() == v.getContext().getString(R.string.text_resume)) {

                AppUtils.INSTANCE.getDefaultPreferences
                        (
                                (AppCompatActivity) requireActivity()
                        ).edit().putString("pedo_state", "stop").apply();

                startService(v, true);
                setUpListener(true);
                startPedometer();
                if (getActivity() instanceof PedometerActivity)
                    ((PedometerActivity) getActivity()).showStartStopInter();

                step_btn_txt.setText(v.getContext().getString(R.string.text_stop));
                step_btn_img.setVisibility(View.GONE);
                start_btn.setBackground(v.getContext().getDrawable(R.drawable.background_stop_btn));

            } else if (step_btn_txt.getText() == v.getContext().getString(R.string.text_stop)) {

                AppUtils.INSTANCE.getDefaultPreferences
                        (
                                (AppCompatActivity) requireActivity()
                        ).edit().putString("pedo_state", "resume").apply();

                startService(v, false);
                setUpListener(false);
                if (getActivity() instanceof PedometerActivity)
                    ((PedometerActivity) getActivity()).showStartStopInter();

                step_btn_txt.setText(v.getContext().getString(R.string.text_resume));
                step_btn_img.setVisibility(View.VISIBLE);
                start_btn.setBackground(v.getContext().getDrawable(R.drawable.background_start_btn));

            } else {

                AppUtils.INSTANCE.getDefaultPreferences
                        (
                                (AppCompatActivity) requireActivity()
                        ).edit().putString("pedo_state", "stop").apply();

                startService(v, true);
                setUpListener(true);
                startPedometer();
                if (getActivity() instanceof PedometerActivity)
                    ((PedometerActivity) getActivity()).showStartStopInter();

                step_btn_txt.setText(v.getContext().getString(R.string.text_stop));
                step_btn_img.setVisibility(View.GONE);
                start_btn.setBackground(v.getContext().getDrawable(R.drawable.background_stop_btn));

            }

        });

        pedoState = AppUtils.INSTANCE.getDefaultPreferences(
                (AppCompatActivity) requireActivity()
        ).getString("pedo_state", null);

        if (pedoState != null && pedoState.equals("stop")) {

            setUpListener(true);
            startService(v, true);
            startPedometer();

            step_btn_txt.setText(v.getContext().getString(R.string.text_stop));
            step_btn_img.setVisibility(View.GONE);
            start_btn.setBackground(v.getContext().getDrawable(R.drawable.background_stop_btn));

        } else if (pedoState != null && pedoState.equals("resume")) {

            setUpListener(false);
            startService(v, false);

            step_btn_txt.setText(v.getContext().getString(R.string.text_resume));
            step_btn_img.setVisibility(View.VISIBLE);
            start_btn.setBackground(v.getContext().getDrawable(R.drawable.background_start_btn));

        }

        step_btn_txt.setSelected(true);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isStepSensorAvailable();
        FrameLayout ad_container = view.findViewById(R.id.ad_container_pedo);
        NativeAdsManagerKt.loadNativeAd(view.getContext(), ad_container,
                R.layout.ad_unified_common,
                ADUnitPlacements.COMMON_NATIVE_AD, true, null,
                null, null, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        pedoState = AppUtils.INSTANCE.getDefaultPreferences(
                (AppCompatActivity) requireActivity()
        ).getString("pedo_state", null);
        if (pedoState != null && pedoState.equals("stop")) {
            setUpListener(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pedoState = AppUtils.INSTANCE.getDefaultPreferences(
                (AppCompatActivity) requireActivity()
        ).getString("pedo_state", null);
        if (pedoState != null && pedoState.equals("stop")) {
            setUpListener(false);
            Database db = Database.getInstance(getActivity());
            db.saveCurrentSteps(since_boot);
            db.close();
        }
    }

    private void startService(View v, boolean start) {
        if (start)
            v.getContext().startService(new Intent(getActivity(), SensorListener.class));
        else {
            v.getContext().stopService(new Intent(getActivity(), SensorListener.class));
        }
    }

    public void setUpListener(boolean option) {
        if (option) {
            SensorManager sm = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (sensor == null) {
                new AlertDialog.Builder(getActivity()).setTitle(R.string.no_sensor)
                        .setMessage(R.string.no_sensor_explain)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(final DialogInterface dialogInterface) {
                                requireActivity().finish();
                            }
                        }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
            } else
                sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI, 0);
        } else {
            try {
                SensorManager sm =
                        (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
                sm.unregisterListener(this);
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Logger.log(e);
            }
        }
    }

    private void isStepSensorAvailable() {
        SensorManager sm = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (sensor == null) {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.no_sensor)
                    .setMessage(R.string.no_sensor_explain)
                    .setOnDismissListener(
                            dialogInterface ->
                                    requireActivity().finish()
                    ).setPositiveButton(
                            android.R.string.ok,
                    (dialogInterface, i) -> dialogInterface.dismiss()
            ).create().show();
        }
    }

    private void startPedometer() {

        Database db = Database.getInstance(getActivity());

        //if (BuildConfig.DEBUG) db.logState();
        // read todays offset
        todayOffset = db.getSteps(Util.getToday());

        SharedPreferences prefs =
                getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE);

        goal = prefs.getInt("goal", Fragment_Settings.DEFAULT_GOAL);
        since_boot = db.getCurrentSteps();
        int pauseDifference = since_boot - prefs.getInt("pauseCount", since_boot);

        since_boot -= pauseDifference;
        total_start = db.getTotalWithoutToday();
        total_days = db.getDays();

        db.close();

        stepsDistanceChanged();
    }

    /**
     * Call this method if the Fragment should update the "steps"/"km" text in
     * the pie graph as well as the pie and the bars graphs.
     */
    private void stepsDistanceChanged() {
        ((TextView) mView.findViewById(R.id.unit)).setText(getString(R.string.text_steps));
        updatePie();
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, int accuracy) {
        // won't happen
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (BuildConfig.DEBUG) Logger.log(
                "UI - sensorChanged | todayOffset: " + todayOffset + " since boot: " +
                        event.values[0]);
        if (event.values[0] > Integer.MAX_VALUE || event.values[0] == 0) {
            return;
        }
        if (todayOffset == Integer.MIN_VALUE) {
            // no values for today
            // we dont know when the reboot was, so set todays steps to 0 by
            // initializing them with -STEPS_SINCE_BOOT
            todayOffset = -(int) event.values[0];
            Database db = Database.getInstance(getActivity());
            db.insertNewDay(Util.getToday(), (int) event.values[0]);
            db.close();
        }
        since_boot = (int) event.values[0];
        updatePie();
    }

    /**
     * Updates the pie graph to show todays steps/distance as well as the
     * yesterday and total values. Should be called when switching from step
     * count to distance.
     */
    private void updatePie() {
        if (BuildConfig.DEBUG) Logger.log("UI - update steps: " + since_boot);
        // todayOffset might still be Integer.MIN_VALUE on first start
        int steps_today = Math.max(todayOffset + since_boot, 0);
        sliceCurrent.setValue(steps_today);
        segmentBar.setProgress((float) steps_today);
        if (goal - steps_today > 0) {
            // goal not reached yet
            /*if (pg.getData().size() == 1) {
                // can happen if the goal value was changed: old goal value was
                // reached but now there are some steps missing for the new goal
                pg.addPieSlice(sliceGoal);
            }*/
            sliceGoal.setValue(goal - steps_today);
        } else {
            // goal reached
            /*pg.clearChart();
            pg.addPieSlice(sliceCurrent);*/
        }
//        pg.update();

        stepsView.setText(formatter.format(steps_today));
        SharedPreferences prefs =
                mView.getContext().getSharedPreferences("pedometer", Context.MODE_PRIVATE);
        float stepsize = prefs.getFloat("stepsize_value", Fragment_Settings.DEFAULT_STEP_SIZE);
        float distance_today = steps_today * stepsize;
        if (Objects.equals(prefs.getString
                ("stepsize_unit", Fragment_Settings.DEFAULT_STEP_UNIT), "cm")) {
            distance_today /= 160934;
        } else {
            distance_today /= 5280;
        }
        miles.setText("" + AppUtils.INSTANCE.roundTwoDecimal(distance_today));
        // TODO: 7/15/2020 increase step count to 150
        timeValue.setText(TimeUtils.INSTANCE.getDurationSpeedo(BuildConfig.DEBUG ?
                (steps_today / 10) * 60000 : (steps_today / 150) * 60000));
    }

}
