package com.antsfamily.biketrainer.data.models

import java.math.BigDecimal

data class WorkoutSensorValues(
    var heartRate: Int? = null,
    var cadence: Int? = null,
    var power: Int? = null,
    var speed: BigDecimal? = null,
    var distance: BigDecimal? = null
)
