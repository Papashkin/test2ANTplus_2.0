package com.antsfamily.biketrainer.ui.scanning

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.antsfamily.biketrainer.R
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType

@DrawableRes
fun DeviceType.iconId(): Int = when (this) {
    DeviceType.CONTROLLABLE_DEVICE,
    DeviceType.FITNESS_EQUIPMENT -> R.drawable.ic_bike_trainer
    DeviceType.HEARTRATE -> R.drawable.ic_heart_rate
    DeviceType.BIKE_POWER,
    DeviceType.BIKE_SPDCAD,
    DeviceType.BIKE_CADENCE,
    DeviceType.BIKE_SPD -> R.drawable.ic_bike_sensor
    else -> -1
}

@StringRes
fun DeviceType.stringId(): Int = when (this) {
    DeviceType.BIKE_POWER -> R.string.sensor_bike_power
    DeviceType.CONTROLLABLE_DEVICE -> R.string.sensor_controllable_device
    DeviceType.FITNESS_EQUIPMENT -> R.string.sensor_fitness_equipment
    DeviceType.HEARTRATE -> R.string.sensor_heart_rate
    DeviceType.BIKE_SPDCAD -> R.string.sensor_speed_cadence
    DeviceType.BIKE_CADENCE -> R.string.sensor_speed
    DeviceType.BIKE_SPD -> R.string.sensor_cadence
    else -> -1
}

