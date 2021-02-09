package com.antsfamily.biketrainer.data.models.workouts

import com.antsfamily.biketrainer.data.models.ProgramType
import java.io.Serializable

data class WorkoutStairsParams(
    val type: ProgramType,
    val startPower: Int,
    val endPower: Int,
    val duration: Long
): Serializable
