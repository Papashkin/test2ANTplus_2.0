package com.antsfamily.biketrainer.ui.scan

import androidx.recyclerview.widget.DiffUtil
import com.antsfamily.biketrainer.ant.device.SelectedDevice

class DeviceCallback(
    private val oldList: ArrayList<SelectedDevice>,
    private val newList: ArrayList<SelectedDevice>
): DiffUtil.Callback() {

    override fun areItemsTheSame(oldListItemId: Int, newListItemId: Int): Boolean {
        val oldDevice = oldList[oldListItemId]
        val newDevice = newList[newListItemId]
        return oldDevice == newDevice
    }

    override fun areContentsTheSame(oldListItemId: Int, newListItemId: Int): Boolean {
        val oldDevice = oldList[oldListItemId]
        val newDevice = newList[newListItemId]
        return (oldDevice.device.resultID == newDevice.device.resultID && oldDevice.isSelected == newDevice.isSelected)
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size
}