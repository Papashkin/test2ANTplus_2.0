package com.antsfamily.biketrainer.util

import java.util.concurrent.TimeUnit

/**
 * Time in format "00:00"
 */
fun Long.timeFormat(): String {
    val minutes = TimeUnit.SECONDS.toMinutes(this) - (TimeUnit.SECONDS.toHours(this) * 60)
    val seconds = TimeUnit.SECONDS.toSeconds(this) - (TimeUnit.SECONDS.toMinutes(this) * 60)

    return String.format("%02d:%02d", minutes, seconds)
}

/**
 * Time in format "0:00:00"
 */
fun Long.fullTimeFormat(): String {
    val hours = TimeUnit.SECONDS.toHours(this)
    val minutes = TimeUnit.SECONDS.toMinutes(this) - (TimeUnit.SECONDS.toHours(this) * 60)
    val seconds = TimeUnit.SECONDS.toSeconds(this) - (TimeUnit.SECONDS.toMinutes(this) * 60)

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
