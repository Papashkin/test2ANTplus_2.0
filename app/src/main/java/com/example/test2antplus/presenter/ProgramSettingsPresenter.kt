package com.example.test2antplus.presenter

import android.annotation.SuppressLint
import com.example.test2antplus.*
import com.example.test2antplus.MainApplication.Companion.PROGRAM_IMAGES_PATH
import com.example.test2antplus.data.programs.Program
import com.example.test2antplus.data.programs.ProgramsRepository
import com.example.test2antplus.ui.view.ProgramSettingsFragment
import com.example.test2antplus.ui.view.ProgramSettingsFragment.Companion.INTERVAL
import com.example.test2antplus.ui.view.ProgramSettingsFragment.Companion.SINGLE
import com.example.test2antplus.ui.view.ProgramSettingsFragment.Companion.STAIRS
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import io.reactivex.Observable
import ru.terrakok.cicerone.Router
import java.io.File
import javax.inject.Inject

@SuppressLint("CheckResult")
class ProgramSettingsPresenter (private val view: ProgramSettingsFragment) {

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var programsRepository: ProgramsRepository

    private lateinit var program: BarDataSet
    private lateinit var programChart: BarChart
    private lateinit var programImagePath: String

    private var programName: String = ""
    private var powerTemp: Float = 0.0f
    private var restPowerTemp: Float = 0.0f
    private var duration: Float = 0.0f
    private var restDuration: Float = 0.0f
    private var intervalCount = 0
    private var programType = 0
    private var entries: ArrayList<BarEntry> = arrayListOf()
    private var descriptors: ArrayList<Float> = arrayListOf()

    init {
        MainApplication.graph.inject(this)
    }

    fun setProgramName(text: String) {
        programName = text
        checkViewsEnabled()
        checkAddFab()
    }

    private fun checkViewsEnabled() {
        if (programName.isNotEmpty() && programType != 0) {
            view.setViewsEnabled()
        } else {
            view.setViewsDisabled()
        }
    }

    fun setTargetPower(power: Float) {
        powerTemp = power
        checkAddFab()
    }

    fun setDuration(time: Float) {
        duration = time*60
        checkAddFab()
    }

    fun setRestPower(power: Float) {
        restPowerTemp = power
        checkAddFab()
    }

    fun setRestDuration(time: Float) {
        restDuration = (time*60)
        checkAddFab()
    }

    fun setIntervalCount(count: Int) {
        intervalCount = count
        checkAddFab()
    }

    fun setProgramType(type: Int) {
        programType = type
        checkViewsEnabled()
        view.setProgramType(type)
    }

    fun onAddClick() {
        when (programType) {
            SINGLE -> {
                setInterval(duration, powerTemp)
                updateChart()
            }
            INTERVAL -> {
                for (interval in 0 until intervalCount) {
                    setInterval(duration, powerTemp)
                    setInterval(restDuration, restPowerTemp)
                }
                updateChart()
            }
            STAIRS -> {
                val middlePower = restPowerTemp + (powerTemp - restPowerTemp) / 2
                val steps = floatArrayOf(restPowerTemp, middlePower, powerTemp)
                val durationForEachStep = duration / 3
                for (i in steps) {
                    setInterval(durationForEachStep, i)
                }
                updateChart()
            }
        }
    }

    private fun setInterval(duration: Float, power: Float) {
        entries.add(BarEntry(entries.size.toFloat(), power))
        descriptors.add(duration)
    }

    private fun updateChart() {
        program = BarDataSet(entries, "Total time: ${descriptors.sum().toLong().fullTimeFormat()}")
//        program.stackLabels = descriptors.map { it.toLong().timeFormat() }.toTypedArray()
        program.barBorderWidth = 0f
        view.updateChart(BarData(program), descriptors)
        clearData()
        view.hideKeyboard()
        view.hideAddPowerFab()
    }

    private fun checkAddFab() {
        when (programType) {
            SINGLE -> {
                if (programName.isNotEmpty() && powerTemp != 0.0f && duration != 0.0f) {
                    view.showAddPowerFab()
                }
            }
            INTERVAL -> {
                if (programName.isNotEmpty() && powerTemp != 0.0f && duration != 0.0f && restDuration != 0.0f && restPowerTemp != 0.0f && intervalCount != 0) {
                    view.showAddPowerFab()
                }
            }
            STAIRS -> {
                if (programName.isNotEmpty() && powerTemp != 0.0f && duration != 0.0f) {
                    view.showAddPowerFab()
                }
            }
        }
    }

    private fun clearData() {
        powerTemp = 0.0f
        duration = 0.0f
        restDuration = 0.0f
        restPowerTemp = 0.0f
        intervalCount = 0
        view.clearTextFields()
    }

    fun saveProgram() {
        if (programName.isEmpty() || entries.isEmpty()) {
            view.showToast(R.string.invalid_data)
        } else {
            prepareToSave()
        }
    }

    private fun prepareToSave() {
        view.showLoading()
        var programValues = ""
        for (i in entries.indices) {
            programValues += "${descriptors[i]}*${entries[i].y}|"
        }

        programsRepository.getProgramByName(programName)
            .compose {
                it.workInAsinc()
            }.subscribe({
                view.showToast(R.string.program_settings_this_program_is_existed)
                view.hideLoading()
            }, {
                view.getChart()
                saveImage(programValues)
            })
    }

    private fun saveImage(programValues: String) {
        Observable.fromCallable {
            programChart.saveProgramAsImage(programImagePath)
        }.compose {
            it.workInAsinc()
        }.subscribe({
            insertToDb(values = programValues)
        },{
            it.printStackTrace()
        })
    }

    private fun insertToDb(values: String) {
        Observable.fromCallable {
            programsRepository.insertProgram(
                Program(
                    id = 0,
                    name = programName,
                    program = values,
                    imagePath = programImagePath
                )
            )
        }.compose {
            it.workInAsinc()
        }.subscribe {
            view.hideLoading()
            view.closeScreen()
            router.exit()
        }
    }

    fun getProgramImagePath(chart: BarChart) {
        programChart = chart
        val file = File(PROGRAM_IMAGES_PATH)
        if (!file.exists()) file.mkdirs()

        programImagePath = "${file.absolutePath}/${programName.convertToLatinScript()}.png"
    }

    fun onBackPressed() {
        router.exit()
    }

}

