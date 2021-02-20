package com.antsfamily.biketrainer.presentation.createprogram

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antsfamily.biketrainer.data.models.program.ProgramData
import com.antsfamily.biketrainer.data.models.workouts.WorkoutIntervalParams
import com.antsfamily.biketrainer.data.models.workouts.WorkoutSegmentParams
import com.antsfamily.biketrainer.data.models.workouts.WorkoutStairsParams
import com.antsfamily.biketrainer.domain.Result
import com.antsfamily.biketrainer.domain.usecase.SaveProgramUseCase
import com.antsfamily.biketrainer.navigation.CreateProgramToAddInterval
import com.antsfamily.biketrainer.navigation.CreateProgramToAddSegment
import com.antsfamily.biketrainer.navigation.CreateProgramToAddStairs
import com.antsfamily.biketrainer.presentation.Event
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import com.antsfamily.biketrainer.ui.createprogram.model.WorkoutItem
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class CreateProgramViewModel @Inject constructor(
    private val saveProgramUseCase: SaveProgramUseCase
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

    private val _clearInputFieldsEvent = MutableLiveData<Event<Unit>>()
    val clearInputFieldsEvent: LiveData<Event<Unit>>
        get() = _clearInputFieldsEvent

    private var dataSet: MutableList<ProgramData> = mutableListOf()

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

    fun onStairsClick() {
        navigateTo(CreateProgramToAddStairs)
    }

    fun onCreateClick(name: String) {
        if (isValid(name)) {
            saveProgram(name)
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

    fun onStairsAdd(stairs: WorkoutStairsParams?) {
        stairs?.let {
            setStairs(it)
            updateChart()
        }
    }

    private fun setSegment(workout: WorkoutSegmentParams) {
        dataSet.add(ProgramData(workout.power, workout.duration))
    }

    private fun setInterval(workout: WorkoutIntervalParams) {
        for (interval in 0 until workout.times) {
            setSegment(WorkoutSegmentParams(workout.peakPower, workout.restDuration))
            setSegment(WorkoutSegmentParams(workout.restPower, workout.restDuration))
        }
    }

    private fun setStairs(workout: WorkoutStairsParams) {
        val stepPower = (workout.endPower - workout.startPower) / workout.steps
        val durationForEachStep = workout.duration / workout.steps
        for (index in 0 until workout.steps) {
            setSegment(
                WorkoutSegmentParams(
                    workout.startPower + stepPower.times(index),
                    durationForEachStep
                )
            )
        }
    }

    private fun updateChart() {
        val workoutItem = WorkoutItem(
            entries = dataSet.mapIndexed { index, programData ->
                BarEntry(index.toFloat(), programData.power.toFloat())
            },
            labels = dataSet.map { it.duration }
        )
        changeState {
            it.copy(
                barItem = workoutItem,
                isEmptyBarChartVisible = it.barItem == null,
                isBarChartVisible = it.barItem != null,
                workoutError = null
            )
        }
    }

    private fun isValid(name: String): Boolean {
        val isNameValid = name.isNotBlank()
        val isWorkoutValid = dataSet.isNotEmpty()

        if (!isNameValid) {
            changeState { it.copy(programNameError = "Program name is invalid") }
        }
        if (!isWorkoutValid) {
            changeState { it.copy(workoutError = "Program should contain at least 1 segment") }
        }

        return isNameValid && isWorkoutValid
    }

    private fun saveProgram(name: String) = viewModelScope.launch {
        showLoading()
        saveProgramUseCase(
            SaveProgramUseCase.Params(Random().nextInt(), name, dataSet),
            ::handleSaveProgramResult
        )
    }

    private fun handleSaveProgramResult(result: Result<Unit, Error>) {
        when (result) {
            is Result.Success -> {
                showToast("Program was successfully saved")
                refreshState()
            }
            is Result.Failure -> {
                showToast("Something went wrong. Please try it again later or change the name of the program")
            }
        }
        hideLoading()
    }

    private fun refreshState() {
        changeState { State() }
        _clearInputFieldsEvent.postValue(Event(Unit))
    }

    private fun showLoading() {
        changeState { it.copy(isLoading = true) }
    }

    private fun hideLoading() {
        changeState { it.copy(isLoading = false) }
    }
}
