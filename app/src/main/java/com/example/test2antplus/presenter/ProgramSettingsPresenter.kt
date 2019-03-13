package com.example.test2antplus.presenter

import android.annotation.SuppressLint
import com.example.test2antplus.MainApplication
import com.example.test2antplus.Program
import com.example.test2antplus.data.programs.ProgramsRepository
import com.example.test2antplus.navigation.AppRouter
import com.example.test2antplus.ui.view.ProgramSettingsFragment.Companion.INTERVAL
import com.example.test2antplus.ui.view.ProgramSettingsFragment.Companion.SINGLE
import com.example.test2antplus.ui.view.ProgramSettingsInterface
import com.example.test2antplus.workInAsinc
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject

class ProgramSettingsPresenter(private val view: ProgramSettingsInterface) {

    @Inject
    lateinit var router: AppRouter
    @Inject
    lateinit var programsRepository: ProgramsRepository

    private lateinit var program: LineDataSet

    private var programName: String = ""
    private var powerTemp: Float = 0.0f
    private var restPowerTemp: Float = 0.0f
    private var duration: Float = 0.0f
    private var restDuration: Float = 0.0f
    private var intervalCount = 0
    private var programType = 0
    private var entries: ArrayList<Entry> = arrayListOf()

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
        duration = time
        checkAddFab()
    }

    fun setRestPower(power: Float) {
        restPowerTemp = power
        checkAddFab()
    }

    fun setRestDuration(time: Float) {
        restDuration = time
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
        val durationInSeconds = (duration * 60).toLong()
        val lastPoint = if (entries.size == 0) {
            0L
        } else {
            entries.last().x.toLong()
        }

        for (i in lastPoint until (lastPoint + durationInSeconds)) {
            entries.add(Entry(i.toFloat(), power))
        }
    }

    private fun updateChart() {
        program = LineDataSet(entries, programName)
        program.setDrawFilled(true)
        view.updateChart(LineData(program))
        clearData()
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
            router.exit()
        }
    }
}