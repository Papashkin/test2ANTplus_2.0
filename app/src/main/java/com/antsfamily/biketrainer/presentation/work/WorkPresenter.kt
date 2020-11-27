package com.antsfamily.biketrainer.presentation.work

import com.antsfamily.biketrainer.MainApplication
import com.antsfamily.biketrainer.presentation.BaseView
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import ru.terrakok.cicerone.Router
import javax.inject.Inject


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

class WorkPresenter(private val view: WorkView) {

    @Inject
    lateinit var router: Router

    private var timeDescriptors = arrayListOf<Float>()
    private var programEntries = arrayListOf<BarEntry>()

    init {
        MainApplication.graph.inject(this)
    }

    fun onFabClick() {
        view.closeAccess()
        router.exit()
    }

    fun setHeartRate(hr: String) {
        view.setHeartRate(hr)
    }

    fun setCadence(cadence: String) {
        view.setCadence(cadence)
    }

    fun setSpeed(speed: String) {
        view.setSpeed(speed)
    }

    fun setDistance(distance: String) {
        view.setDistance(distance)
    }

    fun setPower(power: String) {
        view.setPower(power)
    }

    fun showDialog(name: String, packageName: String) {
        view.showDialog(name, packageName)
    }

    fun setProgram(program: String) {
        programEntries.clear()
        timeDescriptors.clear()
        programEntries = decompileProgram(program)
        updateChart()
    }


    private fun decompileProgram(programLegend: String): ArrayList<BarEntry> {
        val entries = arrayListOf<BarEntry>()
        var count = 0

        programLegend.split("|").forEach { firstDecompiler ->
            if (firstDecompiler.isNotEmpty()) {
                val timeAndPower = firstDecompiler.split("*")
                timeDescriptors.add(timeAndPower.first().toFloat())
                entries.add(BarEntry(count.toFloat(), timeAndPower.last().toFloat()))
                count += 1
            }
        }
        return entries
    }

    private fun updateChart() {
        val program = BarDataSet(programEntries, "")
        program.barBorderWidth = 0f
        view.setDataToChart(BarData(program), timeDescriptors)
    }
}