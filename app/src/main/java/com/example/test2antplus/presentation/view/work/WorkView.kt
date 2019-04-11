package com.example.test2antplus.presentation.view.work

import com.example.test2antplus.presentation.presenter.BaseView
import com.github.mikephil.charting.data.BarData

interface WorkView : BaseView {
    fun setHeartRate(hr: String)
    fun setCadence(cadence: String)
    fun setSpeed(speed: String)
    fun setDistance(distance: String)
    fun setPower(power: String)
    fun showDialog(name: String, packageName: String)
    fun closeAccess()
    fun setDataToChart(program: BarData, time: ArrayList<Float>)
}