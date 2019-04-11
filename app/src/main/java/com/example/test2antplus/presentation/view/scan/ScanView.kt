package com.example.test2antplus.presentation.view.scan

import com.example.test2antplus.ant.device.SelectedDevice
import com.example.test2antplus.presentation.presenter.BaseView

interface ScanView : BaseView {
    fun startScan()
    fun stopScan()
    fun addNewDevice(device: SelectedDevice)
    fun showButtonConnect()
    fun hideButtonConnect()
    fun saveSearchedDevices()
}