package com.antsfamily.biketrainer.presentation.createprogram

import com.antsfamily.biketrainer.MainApplication
import com.antsfamily.biketrainer.data.local.repositories.ProgramsRepository
import com.antsfamily.biketrainer.data.models.Program
import com.antsfamily.biketrainer.data.models.ProgramType
import com.antsfamily.biketrainer.data.models.workouts.WorkoutIntervalParams
import com.antsfamily.biketrainer.data.models.workouts.WorkoutSegmentParams
import com.antsfamily.biketrainer.data.models.workouts.WorkoutStairsParams
import com.antsfamily.biketrainer.navigation.CreateProgramToAddInterval
import com.antsfamily.biketrainer.navigation.CreateProgramToAddSegment
import com.antsfamily.biketrainer.navigation.CreateProgramToAddStairs
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import com.antsfamily.biketrainer.ui.createprogram.model.WorkoutItem
import com.antsfamily.biketrainer.util.fullTimeFormat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject

class CreateProgramViewModel @Inject constructor(
    private val programsRepository: ProgramsRepository
) : StatefulViewModel<CreateProgramViewModel.State>(State()) {

    data class State(
        val isLoading: Boolean = false,
        val isEmptyBarChartVisible: Boolean = true,
        val isBarChartVisible: Boolean = false,
        val programName: String? = null,
        val programNameError: String? = null,
        val barItem: WorkoutItem? = null,
        val workoutError: String? = null
    )

    private lateinit var program: BarDataSet
    private lateinit var programChart: BarChart
    private lateinit var programImagePath: String

    private var entries: ArrayList<BarEntry> = arrayListOf()
    private var timeDescriptors: ArrayList<Long> = arrayListOf()

    fun onBackClick() {
        navigateBack()
    }

    fun onProgramNameChange() {
        changeState { it.copy(programNameError = null) }
    }

    fun onIntervalsClick() {
        navigateTo(CreateProgramToAddInterval)
    }

    fun onSegmentClick() {
        navigateTo(CreateProgramToAddSegment)
    }

    fun onUpstairsClick() {
        navigateTo(CreateProgramToAddStairs(ProgramType.STEPS_UP))
    }

    fun onDownstairsClick() {
        navigateTo(CreateProgramToAddStairs(ProgramType.STEPS_DOWN))
    }

    fun onCreateClick(name: String) {
        if (isValid(name)) {
            prepareToSave()
        }
    }

    fun onSegmentAdd(segment: WorkoutSegmentParams?) {
        segment?.let {
            setSegment(it)
            updateChart()
        }
    }

    fun onIntervalAdd(interval: WorkoutIntervalParams?) {
        interval?.let {
            setInterval(it)
            updateChart()
        }
    }

    fun onStairsUpAdd(stairs: WorkoutStairsParams?) {
        stairs?.let {
            setStairs(it)
            updateChart()
        }
    }

    fun onStairsDownAdd(stairs: WorkoutStairsParams?) {
        stairs?.let {
            setStairs(it)
            updateChart()
        }
    }

    fun onSaveClick() {
        // TODO: 09.02.2021  
        prepareToSave()
    }

    private fun setSegment(workout: WorkoutSegmentParams) {
        entries.add(BarEntry(entries.size.toFloat(), workout.power.toFloat()))
        timeDescriptors.add(workout.duration)
    }

    private fun setInterval(workout: WorkoutIntervalParams) {
        for (interval in 0 until workout.times) {
            setSegment(WorkoutSegmentParams(workout.peakPower, workout.restDuration))
            setSegment(WorkoutSegmentParams(workout.restPower, workout.restDuration))
        }
    }

    private fun setStairs(workout: WorkoutStairsParams) {
        val middlePower = (workout.endPower + workout.startPower) / 2
        val durationForEachStep = workout.duration / 3
        setSegment(WorkoutSegmentParams(workout.startPower, durationForEachStep))
        setSegment(WorkoutSegmentParams(middlePower, durationForEachStep))
        setSegment(WorkoutSegmentParams(workout.endPower, durationForEachStep))
    }

    private fun updateChart() {
        program = BarDataSet(entries, "Total time: ${timeDescriptors.sum().fullTimeFormat()}")
        changeState {
            it.copy(
                barItem = WorkoutItem(program, timeDescriptors),
                isEmptyBarChartVisible = entries.isEmpty(),
                isBarChartVisible = entries.isNotEmpty(),
                workoutError = null
            )
        }
    }

    private fun isValid(name: String): Boolean {
        val isNameValid = !name.isNullOrBlank()
        val isWorkoutValid = entries.isNotEmpty()

        if (!isNameValid) {
            changeState { it.copy(programNameError = "Program name is invalid") }
        }
        if (!isWorkoutValid) {
            changeState { it.copy(workoutError = "Program should contain at least 1 segment") }
        }

        return isNameValid && isWorkoutValid
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
//            showLoading()
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
//            hideLoading()
        }
    }

    private fun getProgramId(programValues: String) = launch {
        try {
            showLoading()
//            val id = programsRepository.getProgramIdByName(programName)
//            programIdFromDb = id
//            chartGetter.postValue(true) // view.getChart()
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
//            programChart.saveProgramAsImage(programImagePath)
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

    private fun showLoading() {}
    private fun hideLoading() {}
}
