package com.antsfamily.biketrainer.ui.scanning

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.antsfamily.biketrainer.ant.device.SelectedDevice

class DeviceDiffUtil(
    private val oldDevices: List<SelectedDevice>,
    private val newDevices: List<SelectedDevice>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldListItemId: Int, newListItemId: Int): Boolean =
        oldDevices[oldListItemId].device.resultID == newDevices[newListItemId].device.resultID

    override fun areContentsTheSame(oldListItemId: Int, newListItemId: Int): Boolean =
        oldDevices[oldListItemId] == newDevices[newListItemId]

    override fun getOldListSize(): Int = oldDevices.size

    override fun getNewListSize(): Int = newDevices.size

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val bundle = Bundle().apply {
            if (oldDevices[oldItemPosition].isSelected != newDevices[newItemPosition].isSelected) {
                putBoolean(KEY_IS_SELECTED_CHANGE, true)
            }
        }
        return if (bundle.size() == 0)
            null
        else bundle
    }

    companion object {
        const val KEY_IS_SELECTED_CHANGE = "KEY_IS_SELECTED_CHANGE"
    }
}
