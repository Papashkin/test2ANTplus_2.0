package com.antsfamily.biketrainer.ui.scanning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.data.models.DeviceItem
import com.antsfamily.biketrainer.databinding.CardSensorInfoBinding
import com.antsfamily.biketrainer.ui.scanning.DeviceDiffUtil.Companion.KEY_IS_LOADING_CHANGE
import com.antsfamily.biketrainer.ui.scanning.DeviceDiffUtil.Companion.KEY_IS_SELECTED_CHANGE
import javax.inject.Inject

class NewDeviceAdapter @Inject constructor() : RecyclerView.Adapter<NewDeviceAdapter.ViewHolder>() {

    var items: List<DeviceItem> = emptyList()
        set(value) {
            DiffUtil.calculateDiff(DeviceDiffUtil(items, value)).dispatchUpdatesTo(this)
            field = value
        }

    private var onItemClickListener: ((item: DeviceItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
        val binding =
            CardSensorInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(items[position])
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNullOrEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            with(payloads) {
                forEach {
                    if (it is Bundle && hasSelectionChanged(it)) {
                        holder.updateSelection(items[position])
                    }
                    if (it is Bundle && hasLoadingChanged(it)) {
                        holder.updateLoading(items[position])
                    }
                }
            }
        }
    }

    fun setOnItemClickListener(listener: (item: DeviceItem) -> Unit) {
        onItemClickListener = listener
    }

    private fun hasSelectionChanged(bundle: Bundle) =
        bundle.keySet().contains(KEY_IS_SELECTED_CHANGE)

    private fun hasLoadingChanged(bundle: Bundle) = bundle.keySet().contains(KEY_IS_LOADING_CHANGE)

    inner class ViewHolder(val binding: CardSensorInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DeviceItem) {
            with(binding) {
                sensorInfoIv.setImageResource(item.device.antDeviceType.iconId())
                updateSelection(item)
                updateLoading(item)
                sensorInfoTypeTv.text =
                    itemView.resources.getString(item.device.antDeviceType.stringId())

                sensorInfoNumberTv.text = itemView.resources.getString(
                    R.string.card_sensor_number,
                    item.device.antDeviceNumber
                )
                sensorSelectCb.isChecked
                binding.root.setOnClickListener { onItemClickListener?.invoke(item) }
            }
        }

        fun updateSelection(item: DeviceItem) {
            binding.sensorSelectCb.isChecked = item.isSelected
        }

        fun updateLoading(item: DeviceItem) {
            binding.loadingView.isVisible = item.isLoading
        }
    }
}
