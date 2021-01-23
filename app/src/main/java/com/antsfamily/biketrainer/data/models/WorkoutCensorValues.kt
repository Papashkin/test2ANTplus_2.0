package com.antsfamily.biketrainer.data.models

import java.math.BigDecimal

data class WorkoutCensorValues(
    var heartRate: Int = 0,
    var cadence: BigDecimal = BigDecimal.ZERO,
    var power: BigDecimal = BigDecimal.ZERO,
    var speed: BigDecimal = BigDecimal.ZERO,
    var distance: BigDecimal = BigDecimal.ZERO
)
