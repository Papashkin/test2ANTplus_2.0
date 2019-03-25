package com.example.test2antplus.presenter

import android.annotation.SuppressLint
import com.example.test2antplus.*
import com.example.test2antplus.MainApplication.Companion.PROGRAM_IMAGES_PATH
import com.example.test2antplus.data.programs.Program
import com.example.test2antplus.data.programs.ProgramsRepository
import com.example.test2antplus.ui.view.ProgramSettingsFragment
import com.example.test2antplus.ui.view.ProgramSettingsFragment.Companion.INTERVAL
import com.example.test2antplus.ui.view.ProgramSettingsFragment.Companion.SEGMENT
import com.example.test2antplus.ui.view.ProgramSettingsFragment.Companion.STEPS_DOWN
import com.example.test2antplus.ui.view.ProgramSettingsFragment.Companion.STEPS_UP
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import io.reactivex.Observable
import ru.terrakok.cicerone.Router
import java.io.File
import javax.inject.Inject

@SuppressLint("CheckResult")
class ProgramSettingsPresenter(private val view: ProgramSettingsFragment) {

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var programsRepository: ProgramsRepository

    private lateinit var program: BarDataSet
    private lateinit var programChart: BarChart
    private lateinit var programImagePath: String

    private var programName: String = ""
    private var programIdFromDb = -1
    private var powerTemp: Float = 0.0f
    private var restPowerTemp: Float = 0.0f
    private var duration: Float = 0.0f
    private var restDuration: Float = 0.0f
    private var intervalCount = 0
    private var programType = SEGMENT
    private var isNewProgram: Boolean = true
    private var entries: ArrayList<BarEntry> = arrayListOf()
    private var selectedEntry: BarEntry? = null
    private var timeDescriptors: ArrayList<Float> = arrayListOf()

    init {
        MainApplication.graph.inject(this)
    }

    fun setProgramName(text: String) {
        programName = text
        saveProgram()
    }

    fun setTargetPower(power: Float) {
        powerTemp = power
    }

    fun setDuration(time: Float) {
        duration = time * 60
    }

    fun setRestPower(power: Float) {
        restPowerTemp = power
    }

    fun setRestDuration(time: Float) {
        restDuration = (time * 60)
    }

    fun setIntervalCount(count: Int) {
        intervalCount = count
    }

    fun onAddClick() {
        if (checkAddFab()) {
            if (selectedEntry == null) {
                when (programType) {
                    SEGMENT -> {
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
                    STEPS_UP -> {
                        val middlePower = restPowerTemp + (powerTemp - restPowerTemp) / 2
                        val steps = floatArrayOf(restPowerTemp, middlePower, powerTemp)
                        val durationForEachStep = duration / 3
                        for (i in steps) {
                            setInterval(durationForEachStep, i)
                        }
                        updateChart()
                    }
                    STEPS_DOWN -> {
                        val middlePower = restPowerTemp + (powerTemp - restPowerTemp) / 2
                        val steps = floatArrayOf(powerTemp, middlePower, restPowerTemp)
                        val durationForEachStep = duration / 3
                        for (i in steps) {
                            setInterval(durationForEachStep, i)
                        }
                        updateChart()
                    }
                }
            } else {
                updateBarEntry(duration, powerTemp)
            }
        } else {
            view.showToast(R.string.invalid_data)
        }
    }

    fun onModifyClick(entry: BarEntry) {
        programType = SEGMENT
        val index = entries.indexOf(entry)
        powerTemp = entry.y
        selectedEntry = entries[index]
        duration = timeDescriptors[index]
        view.setProgramType(programType, Pair(powerTemp, duration))
        view.showProgramBottomDialog()
    }

    private fun setInterval(duration: Float, power: Float) {
        entries.add(BarEntry(entries.size.toFloat(), power))
        timeDescriptors.add(duration)
    }

    private fun updateChart() {
        program = BarDataSet(entries, "Total time: ${timeDescriptors.sum().toLong().fullTimeFormat()}")
        program.barBorderWidth = 0f
        view.updateChart(BarData(program), timeDescriptors)
        clearData()
        view.hideKeyboard()
    }

    private fun updateBarEntry(duration: Float, power: Float) {
        val index = entries.indexOf(selectedEntry)
        entries[index].y = power
        timeDescriptors[index] = duration
        updateChart()
    }

    private fun checkAddFab(): Boolean {
        return when (programType) {
            SEGMENT -> {
                if (powerTemp != 0.0f && duration != 0.0f) {
                    view.hideProgramBottomDialog()
                    view.hideKeyboard()
                    true
                } else {
                    false
                }
            }
            INTERVAL -> {
                if (powerTemp != 0.0f && duration != 0.0f && restDuration != 0.0f && restPowerTemp != 0.0f && intervalCount != 0) {
                    view.hideProgramBottomDialog()
                    view.hideKeyboard()
                    true
                } else {
                    false
                }
            }
            STEPS_UP -> {
                if (powerTemp != 0.0f && restPowerTemp != 0.0f && duration != 0.0f) {
                    view.hideProgramBottomDialog()
                    view.hideKeyboard()
                    true
                } else {
                    false
                }
            }
            else -> {
                if (powerTemp != 0.0f && restPowerTemp != 0.0f && duration != 0.0f) {
                    view.hideProgramBottomDialog()
                    view.hideKeyboard()
                    true
                } else {
                    false
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
        if (selectedEntry != null) {
            selectedEntry = null
        }
        view.clearTextFields()
    }

    private fun saveProgram() {
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
            programValues += "${timeDescriptors[i]}*${entries[i].y}|"
        }
        if (isNewProgram) {
            checkProgramName(programValues)
        } else {
            getProgramId(programValues)
        }
    }

    private fun checkProgramName(programValues: String) {
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

    private fun getProgramId(programValues: String) {
        programsRepository.getProgramIdByName(programName)
            .compose {
                it.workInAsinc()
            }.subscribe({
                programIdFromDb = it!!
                view.getChart()
                saveImage(programValues)
            }, {
                it.printStackTrace()
            })
    }

    private fun saveImage(programValues: String) {
        Observable.fromCallable {
            programChart.saveProgramAsImage(programImagePath)
        }.compose {
            it.workInAsinc()
        }.subscribe({
            if (isNewProgram) {
                insertToDb(values = programValues)
            } else {
                updateInDb(values = programValues)
            }
        }, {
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

    private fun updateInDb(values: String) {
        Observable.fromCallable {
            programsRepository.updateProgram(
                Program(
                    id = programIdFromDb,
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
        if (!entries.isEmpty()) {
            view.showBackDialog()
        } else {
            onExit()
        }
    }

    fun onExit() {
        router.exit()
    }

    fun showSaveDialog() {
        if (isNewProgram) {
            view.showProgramNameDialog()
        } else {
            prepareToSave()
        }
    }

    fun addProgramClick(type: Int) {
        programType = type
        view.setProgramType(type, null)
        view.showProgramBottomDialog()
    }

    fun onCancelClick() {
        clearData()
        view.hideProgramBottomDialog()
    }

    fun onNewProgramCreate() {
        isNewProgram = true
    }

    fun onEditExistedProgramOpen(program: Pair<String, String>, imagePath: String?) {
        isNewProgram = false
        programName = program.first
        programImagePath = imagePath!!
        entries = decompileProgram(program.second)
        updateChart()
    }

    private fun decompileProgram(programLegend: String): ArrayList<BarEntry> {
        val entries = arrayListOf<BarEntry>()
        timeDescriptors.clear()
        entries.clear()
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

}