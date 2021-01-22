package com.antsfamily.biketrainer.presentation.programSettings

import androidx.lifecycle.MutableLiveData
import com.antsfamily.biketrainer.MainApplication
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.data.local.repositories.ProgramsRepository
import com.antsfamily.biketrainer.data.models.Program
import com.antsfamily.biketrainer.data.models.ProgramType
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import com.antsfamily.biketrainer.util.saveProgramAsImage
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class ProgramSettingsViewModel @Inject constructor(
    private val programsRepository: ProgramsRepository
) : StatefulViewModel<ProgramSettingsViewModel.State>(State()) {

    data class State(
        var programName: String = "",
        var programIdFromDb: Int = -1,
        var powerTemp: Float = 0.0f,
        var restPowerTemp: Float = 0.0f,
        var duration: Float = 0.0f,
        var restDuration: Float = 0.0f,
        var intervalCount: Int = 0,
        var programType: ProgramType = ProgramType.SEGMENT,
        var isNewProgram: Boolean = true,
        var entries: ArrayList<BarEntry> = arrayListOf(),
        var selectedEntry: BarEntry? = null,
        var timeDescriptors: ArrayList<Float> = arrayListOf()
    )

    private lateinit var program: BarDataSet
    private lateinit var programChart: BarChart
    private lateinit var programImagePath: String

//    private var programName: String = ""
//    private var programIdFromDb = -1
//    private var powerTemp: Float = 0.0f
//    private var restPowerTemp: Float = 0.0f
//    private var duration: Float = 0.0f
//    private var restDuration: Float = 0.0f
//    private var intervalCount = 0
//    private var programType = ProgramType.SEGMENT
//    private var isNewProgram: Boolean = true
//    private var entries: ArrayList<BarEntry> = arrayListOf()
//    private var selectedEntry: BarEntry? = null
//    private var timeDescriptors: ArrayList<Float> = arrayListOf()

    var barChart: MutableLiveData<Pair<BarData, ArrayList<Float>>?> = MutableLiveData(null)
    var programTypeAndData: MutableLiveData<Triple<ProgramType, Float?, Float?>> = MutableLiveData(Triple(ProgramType.SEGMENT, null, null))
    var programDialog: MutableLiveData<Boolean> = MutableLiveData(false)
    var backDialog: MutableLiveData<Boolean> = MutableLiveData(false)
    var chartGetter: MutableLiveData<Boolean> = MutableLiveData(false)

    fun setProgramName(text: String) {
//        programName = text
    }

    fun setTargetPower(power: Float) {
//        powerTemp = power
    }

    fun setDuration(time: Float) {
//        duration = time * 60
    }

    fun setRestPower(power: Float) {
//        restPowerTemp = power
    }

    fun setRestDuration(time: Float) {
//        restDuration = (time * 60)
    }

    fun setIntervalCount(count: Int) {
//        intervalCount = count
    }

    fun onAddClick() {
        if (checkAddFab()) {
//            if (selectedEntry == null) {
//                when (programType) {
//                    ProgramType.SEGMENT -> {
//                        setInterval(duration, powerTemp)
//                        updateChart()
//                    }
//                    ProgramType.INTERVAL -> {
//                        for (interval in 0 until intervalCount) {
//                            setInterval(duration, powerTemp)
//                            setInterval(restDuration, restPowerTemp)
//                        }
//                        updateChart()
//                    }
//                    ProgramType.STEPS_UP -> {
//                        val middlePower = restPowerTemp + (powerTemp - restPowerTemp) / 2
//                        val steps = floatArrayOf(restPowerTemp, middlePower, powerTemp)
//                        val durationForEachStep = duration / 3
//                        for (i in steps) {
//                            setInterval(durationForEachStep, i)
//                        }
//                        updateChart()
//                    }
//                    ProgramType.STEPS_DOWN -> {
//                        val middlePower = restPowerTemp + (powerTemp - restPowerTemp) / 2
//                        val steps = floatArrayOf(powerTemp, middlePower, restPowerTemp)
//                        val durationForEachStep = duration / 3
//                        for (i in steps) {
//                            setInterval(durationForEachStep, i)
//                        }
//                        updateChart()
//                    }
//                }
//            } else {
//                updateBarEntry(duration, powerTemp)
//            }
        } else {
            showToast(R.string.invalid_data)
        }
    }

    fun onModifyClick(entry: BarEntry) {
//        programType = ProgramType.SEGMENT
//        val index = entries.indexOf(entry)
//        powerTemp = entry.y
//        selectedEntry = entries[index]
//        duration = timeDescriptors[index]
//        programTypeAndData.postValue(Triple(programType, powerTemp, duration))
        programDialog.postValue(true)
    }

    fun onSaveClick() {
//        if (programName.isBlank()) {
//            showToast(R.string.invalid_program_name)
//            return
//        }
//        if (entries.isEmpty()) {
//            showToast(R.string.invalid_data)
//            return
//        }
        prepareToSave()
    }

    private fun setInterval(duration: Float, power: Float) {
//        entries.add(BarEntry(entries.size.toFloat(), power))
//        timeDescriptors.add(duration)
    }

    private fun updateChart() {
//        program =
//            BarDataSet(entries, "Total time: ${timeDescriptors.sum().toLong().fullTimeFormat()}")
        program.barBorderWidth = 0f
//        barChart.postValue(Pair(BarData(program), timeDescriptors))
        clearData()
        hideKeyboard()
    }

    private fun updateBarEntry(duration: Float, power: Float) {
//        val index = entries.indexOf(selectedEntry)
//        entries[index].y = power
//        timeDescriptors[index] = duration
        updateChart()
    }

    private fun checkAddFab(): Boolean {
//        return when (programType) {
//            ProgramType.SEGMENT -> {
//                if (powerTemp != 0.0f && duration != 0.0f) {
//                    programDialog.postValue(false)
//                    hideKeyboard()
//                    true
//                } else {
//                    false
//                }
//            }
//            ProgramType.INTERVAL -> {
//                if (powerTemp != 0.0f && duration != 0.0f && restDuration != 0.0f && restPowerTemp != 0.0f && intervalCount != 0) {
//                    programDialog.postValue(false)
//                    hideKeyboard()
//                    true
//                } else {
//                    false
//                }
//            }
//            ProgramType.STEPS_UP -> {
//                if (powerTemp != 0.0f && restPowerTemp != 0.0f && duration != 0.0f) {
//                    programDialog.postValue(false)
//                    hideKeyboard()
//                    true
//                } else {
//                    false
//                }
//            }
//            ProgramType.STEPS_DOWN -> {
//                if (powerTemp != 0.0f && restPowerTemp != 0.0f && duration != 0.0f) {
//                    programDialog.postValue(false)
//                    hideKeyboard()
//                    true
//                } else {
//                    false
//                }
//            }
//        }
        return false
    }

    private fun clearData() {
        changeState { State() }
//        powerTemp = 0.0f
//        duration = 0.0f
//        restDuration = 0.0f
//        restPowerTemp = 0.0f
//        intervalCount = 0
//        if (selectedEntry != null) {
//            selectedEntry = null
//        }
    }

    private fun prepareToSave() {
        var programValues = ""
//        for (i in entries.indices) {
//            programValues += "${timeDescriptors[i]}*${entries[i].y}|"
//        }
//        if (isNewProgram) {
//            checkProgramName(programValues)
//        } else {
//            getProgramId(programValues)
//        }
    }

    private fun checkProgramName(programValues: String) = launch {
        try {
            showLoading()
//            val programWithSameName = programsRepository.getProgramByName(programName)
//            if (programWithSameName == null) {
//                chartGetter.postValue(true)
//                saveImageAsync(programValues).await()
//            } else {
//                showToast(R.string.program_settings_this_program_is_existed)
//                hideLoading()
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            hideLoading()
        }
    }

    private fun getProgramId(programValues: String) = launch {
        try {
            showLoading()
//            val id = programsRepository.getProgramIdByName(programName)
//            programIdFromDb = id
            chartGetter.postValue(true) // view.getChart()
            saveImageAsync(programValues).await()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            hideLoading()
        }
    }

    private fun saveImageAsync(programValues: String) = async {
        try {
            showLoading()
            programChart.saveProgramAsImage(programImagePath)
//            if (isNewProgram) {
//                insertToDb(values = programValues)
//            } else {
//                updateInDb(values = programValues)
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            hideLoading()
        }
    }

    private fun insertToDb(values: String) = launch {
        try {
            showLoading()
            programsRepository.insertProgram(
                Program(
                    id = 0,
                    name = "",
//                    name = programName,
                    program = values,
                    imagePath = programImagePath
                )
            )
//            router.exit()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            hideLoading()
        }
    }

    private fun updateInDb(values: String) = launch {
        try {
            showLoading()
            programsRepository.updateProgram(
                Program(
                    id = 2, // programIdFromDb,
                    name = "", //programName,
                    program = values,
                    imagePath = programImagePath
                )
            )
//            router.exit()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            hideLoading()
        }
    }

    fun getProgramImagePath(chart: BarChart) {
        programChart = chart
        val file = File(MainApplication.PROGRAM_IMAGES_PATH!!)
        if (!file.exists()) file.mkdirs()
//        programImagePath = "${file.absolutePath}/${programName.convertToLatinScript()}.png"
    }

    fun onBackPressed() {
//        if (entries.isNotEmpty()) {
//            backDialog.postValue(true)
//        } else {
//            onExit()
//        }
    }

    fun onExit() {
//        router.exit()
    }

    fun clear() {
        clearLiveDataValues()
        barChart.postValue(null)
        programTypeAndData.postValue(Triple(ProgramType.SEGMENT, null, null))
        programDialog.postValue(false)
        backDialog.postValue(false)
        chartGetter.postValue(false)
        clearData()
    }

    fun addProgramClick(type: ProgramType) {
//        programType = type
        programTypeAndData.postValue(Triple(type, null, null))
        programDialog.postValue(true)
    }

    fun onCancelClick() {
        clearData()
        programDialog.postValue(false)
    }

    fun onNewProgramCreate() {
//        isNewProgram = true
    }

    fun onEditExistedProgramOpen(program: Pair<String, String>, imagePath: String?) {
//        isNewProgram = false
//        programName = program.first
//        programImagePath = imagePath!!
//        entries = decompileProgram(program.second)
//        updateChart()
    }

    private fun decompileProgram(programLegend: String): ArrayList<BarEntry> {
        val entries = arrayListOf<BarEntry>()
//        timeDescriptors.clear()
        entries.clear()
        var count = 0

        programLegend.split("|").forEach { firstDecompiler ->
            if (firstDecompiler.isNotEmpty()) {
                val timeAndPower = firstDecompiler.split("*")
//                timeDescriptors.add(timeAndPower.first().toFloat())
                entries.add(BarEntry(count.toFloat(), timeAndPower.last().toFloat()))
                count += 1
            }
        }
        return entries
    }
}
