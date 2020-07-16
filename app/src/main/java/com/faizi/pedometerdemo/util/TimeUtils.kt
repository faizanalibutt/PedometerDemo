package com.faizi.pedometerdemo.util

import android.content.Context
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object TimeUtils {

    fun formatDateTime(context: Context, millis: Long): CharSequence {
        return DateUtils.formatDateTime(
            context,
            millis,
            DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE
        )
    }

    fun getFormatDateTime(millis: Long, type: String) : String {
        return if (type == "time")
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(millis))
        else
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))
    }

    fun getFormatedTimeMH(millis: Long): String = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)))

    fun getFormatedTimeMH2(millis: Long): String = String.format("%02dh%02dm", TimeUnit.MILLISECONDS.toHours(millis),
        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)))

    fun getFormatedTimeMHS(millis: Long): String = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))

    fun getFormatedTimeMHS2(millis: Long): String = String.format("%02dh%02dm%02ds", TimeUnit.MILLISECONDS.toHours(millis),
        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))

    fun getDuration(milliseconds: Long): String {
        val string = StringBuilder()

        val calculator = ElapsedTime.ElapsedTimeCalculator(milliseconds / 1000L)

        val hours = calculator.crop(3600L)
        val minutes = calculator.crop(60L)
        val seconds = calculator.leftTime

        if (hours > 0L) {
            /*if (hours < 10L)
                string.append("0")*/

            string.append(hours)
            if (hours > 0L && minutes > 0L && seconds > 0L)
                string.append("h ")
            else if (hours > 0L && minutes > 0L && seconds == 0L)
                string.append("h ")
            else if (hours > 0L && minutes == 0L && seconds > 0L)
                string.append("h ")
            else
                string.append(" h")
        }

        if (minutes > 0L) {
            /*if (minutes < 10L)
                string.append("0")*/

            string.append(minutes)
            if (hours > 0L && minutes > 0L && seconds > 0L)
                string.append("m ")
            else if (hours > 0L && minutes > 0L && seconds == 0L)
                string.append("m ")
            else if (hours == 0L && minutes > 0L && seconds > 0L)
                string.append("m ")
            else
                string.append(" m")
        }

        if (seconds > 0L) {
            /*if (seconds < 10L)
                string.append("0")*/

            string.append(seconds)
            if (hours > 0L && minutes > 0L && seconds > 0L)
                string.append("s")
            else if (hours == 0L && minutes > 0L && seconds > 0L)
                string.append("s ")
            else if (hours > 0L && minutes == 0L && seconds > 0L)
                string.append("s ")
            else
                string.append(" s")
        } else {
            if (hours != 0L || minutes != 0L)
                return string.toString()
        }

        return string.toString()
    }

    /*fun getFriendlyElapsedTime(context: Context, estimatedTime: Long): String {
        val elapsedTime = ElapsedTime(estimatedTime)
        val appendList = ArrayList<String>()

        if (elapsedTime.years > 0)
            appendList.add(context.getString(R.string.text_yearCountShort, elapsedTime.years))

        if (elapsedTime.months > 0)
            appendList.add(context.getString(R.string.text_monthCountShort, elapsedTime.months))

        if (elapsedTime.years == 0L) {
            if (elapsedTime.days > 0)
                appendList.add(context.getString(R.string.text_dayCountShort, elapsedTime.days))

            if (elapsedTime.months == 0L) {
                if (elapsedTime.hours > 0)
                    appendList.add(
                        context.getString(
                            R.string.text_hourCountShort,
                            elapsedTime.hours
                        )
                    )

                if (elapsedTime.days == 0L) {
                    if (elapsedTime.minutes > 0)
                        appendList.add(
                            context.getString(
                                R.string.text_minuteCountShort,
                                elapsedTime.minutes
                            )
                        )

                    if (elapsedTime.hours == 0L)
                    // always applied
                        appendList.add(
                            context.getString(
                                R.string.text_secondCountShort,
                                elapsedTime.seconds
                            )
                        )
                }
            }
        }

        val stringBuilder = StringBuilder()

        for (appendItem in appendList) {
            if (stringBuilder.length > 0)
                stringBuilder.append(" ")

            stringBuilder.append(appendItem)
        }

        return stringBuilder.toString()
    }*/

    /*fun getTimeAgo(context: Context, time: Long): String {
        val differ = ((System.currentTimeMillis() - time) / 1000).toInt()

        if (differ == 0)
            return context.getString(R.string.text_timeJustNow)
        else if (differ < 60)
            return context.resources.getQuantityString(R.plurals.text_secondsAgo, differ, differ)
        else if (differ < 3600)
            return context.resources.getQuantityString(
                R.plurals.text_minutesAgo,
                differ / 60,
                differ / 60
            )

        return context.getString(R.string.text_longAgo)
    }*/

}