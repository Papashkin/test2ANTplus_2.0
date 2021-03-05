package com.antsfamily.biketrainer.ui.scanning

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.antsfamily.biketrainer.data.models.DeviceItem

class DeviceDiffUtil(
    private val oldDevices: List<DeviceItem>,
    private val newDevices: List<DeviceItem>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldListItemId: Int, newListItemId: Int): Boolean =
        oldDevices[oldListItemId].device.resultID == newDevices[newListItemId].device.resultID

    override fun areContentsTheSame(oldListItemId: Int, newListItemId: Int): Boolean =
        oldDevices[oldListItemId] == newDevices[newListItemId]

    override fun getOldListSize(): Int = oldDevices.size

    override fun getNewListSize(): Int = newDevices.size

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val isSelectedChanged =
            oldDevices[oldItemPosition].isSelected != newDevices[newItemPosition].isSelected
        val isLoadingChanged =
            oldDevices[oldItemPosition].isLoading != newDevices[newItemPosition].isLoading

        val bundle = Bundle().apply {
            if (isSelectedChanged) {
                putBoolean(KEY_IS_SELECTED_CHANGE, true)
            }
            if (isLoadingChanged) {
                putBoolean(KEY_IS_LOADING_CHANGE, true)
            }
        }
        return if (bundle.size() == 0)
            null
        else bundle
    }

    companion object {
        const val KEY_IS_SELECTED_CHANGE = "KEY_IS_SELECTED_CHANGE"
        const val KEY_IS_LOADING_CHANGE = "KEY_IS_LOADING_CHANGE"
    }
}
