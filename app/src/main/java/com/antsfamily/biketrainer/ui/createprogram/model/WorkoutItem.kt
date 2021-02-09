package com.antsfamily.biketrainer.ui.createprogram.model

import com.github.mikephil.charting.data.BarDataSet

data class WorkoutItem(
    val dataSet: BarDataSet,
    val labels: List<Long>
)
