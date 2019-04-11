package com.example.test2antplus.presentation.view.programSettings

import com.example.test2antplus.presentation.presenter.BaseView
import com.github.mikephil.charting.data.BarData

interface ProgramSettingsView : BaseView {
    fun updateChart(data: BarData, duration: ArrayList<Float>)
    fun showLoading()
    fun hideLoading()
    fun clearTextFields()
    fun setProgramType(type: Int, powerAndTime: Pair<Float, Float>?)
    fun closeScreen()
    fun getChart()
    fun showProgramBottomDialog()
    fun hideProgramBottomDialog()
    fun showBackDialog()
    fun showProgramNameDialog()
}