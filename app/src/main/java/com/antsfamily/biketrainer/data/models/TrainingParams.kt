package com.antsfamily.biketrainer.data.models

data class TrainingParams(
    var heartRate: String? = null,
    var cadence: String? = null,
    var power: String? = null,
    var speed: String? = null,
    var distance: String? = null
)
