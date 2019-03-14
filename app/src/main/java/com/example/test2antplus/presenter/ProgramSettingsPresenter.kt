package com.example.test2antplus.presenter

import android.annotation.SuppressLint
import com.example.test2antplus.MainApplication
import com.example.test2antplus.data.programs.Program
import com.example.test2antplus.data.programs.ProgramsRepository
import com.example.test2antplus.formatToTime
import com.example.test2antplus.ui.view.ProgramSettingsFragment.Companion.INTERVAL
import com.example.test2antplus.ui.view.ProgramSettingsFragment.Companion.SINGLE
import com.example.test2antplus.ui.view.ProgramSettingsInterface
import com.example.test2antplus.workInAsinc
import com.github.mikephil.charting.data.*
import io.reactivex.Observable
import ru.terrakok.cicerone.Router
import java.util.*
import javax.inject.Inject

class ProgramSettingsPresenter(private val view: ProgramSettingsInterface) {

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var programsRepository: ProgramsRepository

    private lateinit var program: BarDataSet

    private var programName: String = ""
    private var powerTemp: Float = 0.0f
    private var restPowerTemp: Float = 0.0f
    private var duration: Float = 0.0f
    private var restDuration: Float = 0.0f
    private var intervalCount = 0
    private var programType = 0
    private var entries: ArrayList<BarEntry> = arrayListOf()
    private var descriptors: ArrayList<String> = arrayListOf()

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
        }
    }

    private fun setInterval(duration: Float, power: Float) {
        entries.add(BarEntry(entries.size.toFloat(), power))
        descriptors.add(duration.toLong().formatToTime())
    }

    private fun updateChart() {
        program = BarDataSet(entries, programName)
        program.barBorderWidth = 1f
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

    @SuppressLint("CheckResult")
    fun saveProgram() {
        when (programType) {
            SINGLE -> {
                if (programName.isNotEmpty() || powerTemp != 0.0f || duration != 0.0f) {
                    view.showToast("invalid data")
                } else {
                    prepareToSave()
                }
            }
            INTERVAL -> {
                if (programName.isNotEmpty() || powerTemp != 0.0f || duration != 0.0f || restDuration != 0.0f || restPowerTemp != 0.0f || intervalCount != 0) {
                    view.showToast("invalid data")
                } else {
                    prepareToSave()
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun prepareToSave() {
        view.showLoading()
        var programValues = ""
        entries.forEach {
            programValues += "${it.x}*${it.y}|"
        }

        programsRepository.getProgramByName(programName)
            .compose {
                it.workInAsinc()
            }.subscribe({
                view.showToast("The program with the same name is exist in database")
                view.hideLoading()
            }, {
                insertToDb(programValues)
            })
    }

    @SuppressLint("CheckResult")
    private fun insertToDb(values: String) {
        Observable.fromCallable {
            programsRepository.insertProgram(
                Program(
                    id = 0,
                    name = programName,
                    program = values
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
}