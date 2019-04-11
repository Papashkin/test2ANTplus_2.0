package com.example.test2antplus.presentation.view.scan

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch
import com.example.test2antplus.R
import com.example.test2antplus.ant.device.SelectedDevice

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

    fun addDevice(newDevice: SelectedDevice) {
        val oldDevices = this.getData()
        if (!devices.contains(newDevice)) {
            devices.add(newDevice)
        }
        devicesDiffUtil = DeviceCallback(oldDevices, devices)
        val productDiffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(devicesDiffUtil, false)
        productDiffResult.dispatchUpdatesTo(this)
        this.notifyItemInserted(devices.size)
    }

    fun getSelectedData(): ArrayList<MultiDeviceSearch.MultiDeviceSearchResult> = devices.filter {
        it.isSelected
    }.map {
            item -> item.device
    } as ArrayList<MultiDeviceSearch.MultiDeviceSearchResult>

    fun getAllData() = devices.map { it.device }

    private fun getData(): ArrayList<SelectedDevice> = devices

    inner class DeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val deviceName = view.findViewById<TextView>(R.id.tvDeviceName)
        private val deviceNumber = view.findViewById<TextView>(R.id.tvDeviceNumber)
        private val deviceType = view.findViewById<TextView>(R.id.tvDeviceType)
        private val checkBox = view.findViewById<CheckBox>(R.id.checkBoxIsSelected)

        fun bind(device: SelectedDevice, position: Int) {
            deviceName.text = device.device.deviceDisplayName
            deviceNumber.text = device.device.antDeviceNumber.toString()
            deviceType.text = device.device.antDeviceType.toString()
            checkBox.isChecked = device.isSelected
            checkBox.setOnClickListener {
                device.isSelected = !device.isSelected
                onDeviceClick(position)
            }
            itemView.setOnClickListener {
                device.isSelected = !device.isSelected
                onDeviceClick(position)
            }
        }

        private fun onDeviceClick(position: Int) {
            checkBox.toggle()
            onItemClick.invoke(position)
        }
    }
}