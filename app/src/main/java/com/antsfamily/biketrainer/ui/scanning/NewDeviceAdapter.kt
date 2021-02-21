package com.antsfamily.biketrainer.ui.scanning

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.ant.device.SelectedDevice
import com.antsfamily.biketrainer.databinding.CardSensorInfoBinding
import javax.inject.Inject

class NewDeviceAdapter @Inject constructor() :
    RecyclerView.Adapter<NewDeviceAdapter.DeviceViewHolder>() {

    var devices: List<SelectedDevice> = emptyList()
        set(value) {
            DiffUtil.calculateDiff(DeviceCallback(devices, value)).dispatchUpdatesTo(this)
            field = value
        }

    private var onItemClickListener: ((device: SelectedDevice) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): DeviceViewHolder {
        val binding =
            CardSensorInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeviceViewHolder(binding)
    }

    override fun getItemCount(): Int = devices.size

    override fun onBindViewHolder(viewHolder: DeviceViewHolder, position: Int) {
        viewHolder.bind(devices[position])
    }

    fun setOnItemClickListener(listener: (device: SelectedDevice) -> Unit) {
        onItemClickListener = listener
    }

    inner class DeviceViewHolder(val binding: CardSensorInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(device: SelectedDevice) {
            with(binding) {
                sensorInfoIv.setImageResource(device.device.antDeviceType.iconId())

                sensorInfoIsSelectedSwitch.apply {
                    isChecked = device.isSelected
                    setOnClickListener { onItemClickListener?.invoke(device) }
                }

                sensorInfoTypeTv.text =
                    itemView.resources.getString(device.device.antDeviceType.stringId())

                sensorInfoNumberTv.text = itemView.resources.getString(
                    R.string.card_sensor_number,
                    device.device.antDeviceNumber
                )

                root.setOnClickListener { onItemClickListener?.invoke(device) }
            }
        }
    }
}
