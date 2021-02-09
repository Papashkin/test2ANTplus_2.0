package com.antsfamily.biketrainer.data.models.workouts

import java.io.Serializable

data class WorkoutIntervalParams(
    val peakPower: Int,
    val restPower: Int,
    val peakDuration: Long,
    val restDuration: Long,
    val times: Int
): Serializable
