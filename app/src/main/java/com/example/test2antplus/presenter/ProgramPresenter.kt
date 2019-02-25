package com.example.test2antplus.presenter

import android.annotation.SuppressLint
import com.example.test2antplus.MainApplication
import com.example.test2antplus.Program
import com.example.test2antplus.data.programs.ProgramsRepository
import com.example.test2antplus.navigation.AppRouter
import com.example.test2antplus.ui.view.ProgramFragment
import com.github.mikephil.charting.data.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class ProgramPresenter(private val view: ProgramFragment) {

    @Inject
    lateinit var router: AppRouter
    @Inject
    lateinit var programsRepository: ProgramsRepository

    private lateinit var programName: String
    private lateinit var program: BarDataSet

    private var powerTemp: Float = 0.0f
    private var duration: Float = 0.0f
    private var entries: ArrayList<BarEntry> = arrayListOf()

    init {
        MainApplication.graph.inject(this)
    }

    fun setProgramName(text: String) {
        programName = text
        checkAddFab()
    }

    fun setTargetPower(power: Float) {
        powerTemp = power
        checkAddFab()
    }

    fun setDuration(time: Float) {
        duration = time
        checkAddFab()
    }

    fun onAddClick() {
        program = BarDataSet(entries, programName)
        view.updateBarChart(BarData(program))
        clearData()
        view.hideAddPowerFab()
    }

    private fun checkAddFab() {
        if (programName.isNotEmpty() && powerTemp != 0.0f && duration != 0.0f) {
            entries.add(BarEntry(powerTemp, duration))
            view.showAddPowerFab()
        }
    }

    private fun clearData() {
        powerTemp = 0.0f
        duration = 0.0f
    }

    @SuppressLint("CheckResult")
    fun saveProgram() {
        view.showLoading()
        var programValues = ""
        entries.forEach {
            programValues += "${it.x}*${it.y}|"
        }

        Observable.fromCallable {
            programsRepository.insertProgram(
                Program(
                    id = 0,
                    name = programName,
                    program = programValues
                )
            )
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                view.hideLoading()
                router.exit()
            }
    }
}