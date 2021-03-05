package com.antsfamily.biketrainer.data.models

import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch

data class DeviceItem(
    val device: MultiDeviceSearch.MultiDeviceSearchResult,
    val isSelected: Boolean,
    val isLoading: Boolean
) {
    fun isFitnessEquipment(): Boolean = this.device.antDeviceType == DeviceType.FITNESS_EQUIPMENT
}
