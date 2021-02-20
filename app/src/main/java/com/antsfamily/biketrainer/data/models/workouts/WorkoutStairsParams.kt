package com.antsfamily.biketrainer.data.models.workouts

import java.io.Serializable

data class WorkoutStairsParams(
    val startPower: Int,
    val endPower: Int,
    val steps: Int,
    val duration: Long
): Serializable
