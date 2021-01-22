package com.antsfamily.biketrainer.ui.scanning

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.ant.device.SelectedDevice
import kotlinx.android.synthetic.main.card_sensor_info.view.*

class NewDeviceAdapter(
    private val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<NewDeviceAdapter.DeviceViewHolder>() {
    private var devices: ArrayList<SelectedDevice> = arrayListOf()
    private lateinit var devicesDiffUtil: DeviceCallback

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_sensor_info, parent, false)
        return DeviceViewHolder(view)
    }

    override fun getItemCount(): Int = devices.size

    override fun onBindViewHolder(viewHolder: DeviceViewHolder, position: Int) {
        viewHolder.bind(this.devices[position], position)
    }

    fun setDevices(newDevices: ArrayList<SelectedDevice>) {
        devices.clear()
        devices.addAll(newDevices)
        devicesDiffUtil = DeviceCallback(this.getData(), newDevices)
        val productDiffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(devicesDiffUtil, false)
        productDiffResult.dispatchUpdatesTo(this)
        notifyItemInserted(devices.size)
    }

    fun getSelectedData(): ArrayList<MultiDeviceSearch.MultiDeviceSearchResult> = devices
        .filter { it.isSelected }
        .map { item -> item.device } as ArrayList<MultiDeviceSearch.MultiDeviceSearchResult>

    private fun getData(): ArrayList<SelectedDevice> = devices

    inner class DeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(device: SelectedDevice, position: Int) {
            itemView.tvDeviceName.text = device.device.deviceDisplayName
            itemView.tvDeviceNumber.text = device.device.antDeviceNumber.toString()
            itemView.tvDeviceType.text = device.device.antDeviceType.toString()
            itemView.checkBoxIsSelected.isChecked = device.isSelected
            itemView.checkBoxIsSelected.setOnClickListener {
                device.isSelected = !device.isSelected
                onDeviceClick(position)
            }
            itemView.setOnClickListener {
                device.isSelected = !device.isSelected
                onDeviceClick(position)
            }
        }

        private fun onDeviceClick(position: Int) {
            itemView.checkBoxIsSelected.toggle()
            onItemClick.invoke(position)
        }
    }
}
