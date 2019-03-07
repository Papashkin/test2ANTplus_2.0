package com.example.test2antplus.presenter

import android.annotation.SuppressLint
import com.example.test2antplus.MainApplication
import com.example.test2antplus.Program
import com.example.test2antplus.data.programs.ProgramsRepository
import com.example.test2antplus.navigation.AppRouter
import com.example.test2antplus.ui.view.ProgramSettingsInterface
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class ProgramSettingsPresenter(private val view: ProgramSettingsInterface) {

    @Inject
    lateinit var router: AppRouter
    @Inject
    lateinit var programsRepository: ProgramsRepository

    private lateinit var program: LineDataSet

    private  var programName: String = ""
    private var powerTemp: Float = 0.0f
    private var duration: Float = 0.0f
    private var entries: ArrayList<Entry> = arrayListOf()

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
        val durationInSeconds = (duration * 60).toLong()
        val lastPoint = if (entries.size == 0) {
            0L
        } else {
            entries.last().x.toLong()
        }

        for (i in lastPoint .. (lastPoint + durationInSeconds)) {
            entries.add(Entry(i.toFloat(), powerTemp))
        }

        program = LineDataSet(entries, programName)
        view.updateBarChart(LineData(program))
        clearData()
        view.hideAddPowerFab()
    }

    private fun checkAddFab() {
        if (programName.isNotEmpty() && powerTemp != 0.0f && duration != 0.0f) {
            view.showAddPowerFab()
        }
    }

    private fun clearData() {
        powerTemp = 0.0f
        duration = 0.0f
        view.clearTextFields()
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