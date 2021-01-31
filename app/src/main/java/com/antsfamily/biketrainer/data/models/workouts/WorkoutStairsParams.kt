package com.antsfamily.biketrainer.data.models.workouts

import com.antsfamily.biketrainer.data.models.ProgramType

data class WorkoutStairsParams(
    val type: ProgramType,
    val startPower: Int,
    val endPower: Int,
    val duration: Long
)
