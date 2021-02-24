package com.antsfamily.biketrainer.ui.scanning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.ant.device.SelectedDevice
import com.antsfamily.biketrainer.databinding.CardSensorInfoBinding
import com.antsfamily.biketrainer.ui.scanning.DeviceDiffUtil.Companion.KEY_IS_SELECTED_CHANGE
import javax.inject.Inject

class NewDeviceAdapter @Inject constructor() :
    RecyclerView.Adapter<NewDeviceAdapter.ViewHolder>() {

    var devices: List<SelectedDevice> = emptyList()
        set(value) {
            DiffUtil.calculateDiff(DeviceDiffUtil(devices, value)).dispatchUpdatesTo(this)
            field = value
        }

    private var onItemClickListener: ((device: SelectedDevice) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
        val binding =
            CardSensorInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = devices.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(devices[position])
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNullOrEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            with(payloads.first()) {
                if (this is Bundle && hasSelectionChanged(this)) {
                    holder.updateSelection(devices[position])
                }
            }
        }
    }

    fun setOnItemClickListener(listener: (device: SelectedDevice) -> Unit) {
        onItemClickListener = listener
    }

    private fun hasSelectionChanged(bundle: Bundle): Boolean =
        bundle.keySet().contains(KEY_IS_SELECTED_CHANGE)

    inner class ViewHolder(val binding: CardSensorInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(device: SelectedDevice) {
            with(binding) {
                sensorInfoIv.setImageResource(device.device.antDeviceType.iconId())
                updateSelection(device)
                sensorInfoTypeTv.text =
                    itemView.resources.getString(device.device.antDeviceType.stringId())

                sensorInfoNumberTv.text = itemView.resources.getString(
                    R.string.card_sensor_number,
                    device.device.antDeviceNumber
                )
                sensorInfoIsSelectedSwitch.setOnClickListener { onItemClickListener?.invoke(device) }
            }
        }

        fun updateSelection(device: SelectedDevice) {
            binding.sensorInfoIsSelectedSwitch.isChecked = device.isSelected
        }
    }
}
