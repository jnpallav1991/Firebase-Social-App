package com.encoding.socialapp.utils

import android.content.Context
import android.text.format.DateUtils
import java.util.*
import java.util.concurrent.TimeUnit

object CustomDateUtils : DateUtils() {
    fun getCustomRelDateTime(c: Context?, timeInMillis: Long): String {
        val format = FORMAT_ABBREV_ALL or FORMAT_SHOW_YEAR or FORMAT_SHOW_TIME
        return getRelativeDateTimeString(
            c, timeInMillis, SECOND_IN_MILLIS, YEAR_IN_MILLIS, format
        ).toString()
    }

    fun getRelDateTime(c: Context?, past: Date): String {
        // val sdf = SimpleDateFormat("hh.mm a d/MM/yy", Locale.ENGLISH)
        //val past = sdf.parse("2016.02.05 AD at 23:59:30")
        val now = Date()
        val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
        val hours: Long = TimeUnit.MILLISECONDS.toHours(now.time - past.time)
        val days: Long = TimeUnit.MILLISECONDS.toDays(now.time - past.time)

        if (seconds < 60) {
            return "$seconds seconds ago"
        } else if (minutes < 60) {
            return "$minutes minutes ago"
        } else if (hours < 24) {
            return "$hours hours ago"
        } else {
            return "$days days ago"
        }
        //val cal = Calendar.getInstance()
        //cal.timeInMillis = timeInMillis
        // return sdf.format(cal.time)

    }
}