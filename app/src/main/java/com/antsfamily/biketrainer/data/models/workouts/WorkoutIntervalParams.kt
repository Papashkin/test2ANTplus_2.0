package com.antsfamily.biketrainer.data.models.workouts

data class WorkoutIntervalParams(
    val peakPower: Int,
    val restPower: Int,
    val peakDuration: Long,
    val restDuration: Long,
    val times: Int
)
