package com.antsfamily.biketrainer.ui.createprogram.model

import com.antsfamily.biketrainer.util.fullTimeFormat
import com.antsfamily.biketrainer.util.timeFormat
import com.github.mikephil.charting.data.BarEntry

data class WorkoutItem(
    val entries: List<BarEntry>,
    val labels: List<Long>
) {
    fun getLabelInTimeFormat() = labels.map { it.timeFormat() }
    fun getTotalTime() = labels.sum().fullTimeFormat()
}
