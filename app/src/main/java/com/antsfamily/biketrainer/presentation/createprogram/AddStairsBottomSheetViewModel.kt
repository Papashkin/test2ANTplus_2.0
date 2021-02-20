package com.antsfamily.biketrainer.presentation.createprogram

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antsfamily.biketrainer.data.models.program.ProgramType
import com.antsfamily.biketrainer.data.models.workouts.WorkoutStairsParams
import com.antsfamily.biketrainer.presentation.Event
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import javax.inject.Inject

class AddStairsBottomSheetViewModel @Inject constructor() :
    StatefulViewModel<AddStairsBottomSheetViewModel.State>(State()) {

    data class State(
        val startPowerError: String? = null,
        val endPowerError: String? = null,
        val stepCountError: String? = null,
        val durationError: String? = null
    )

    private val _setStairsResult = MutableLiveData<Event<WorkoutStairsParams>>()
    val setStairsResult: LiveData<Event<WorkoutStairsParams>>
        get() = _setStairsResult

    fun onAddClick(startPower: Int, endPower: Int, stepCount: Int, duration: Long) {
        if (isValid(startPower, endPower, stepCount, duration)) {
            setResult(startPower, endPower, stepCount, duration)
        }
    }

    fun onStartPowerTextChange() {
        changeState { it.copy(startPowerError = null) }
    }

    fun onEndPowerTextChange() {
        changeState { it.copy(endPowerError = null) }
    }

    fun onStepCountChange() {
        changeState { it.copy(stepCountError = null) }
    }

    fun onDurationChange() {
        changeState { it.copy(durationError = null) }
    }

    private fun isValid(startPower: Int, endPower: Int, stepCount: Int, duration: Long): Boolean {
        val isStartPowerValid = startPower > 0
        val isEndPowerValid = endPower > 0
        val isStepCountValid = (stepCount > MINIMUM_STEPS) and (stepCount <= MAXIMUM_STEPS)
        val isDurationValid = duration > MINIMUM_DURATION

        if (!isDurationValid) {
            changeState { it.copy(durationError = "Duration is invalid. It should be at least more than $MINIMUM_DURATION sec") }
        }
        if (!isStepCountValid) {
            changeState { it.copy(stepCountError = "Step count should be in interval from $MINIMUM_STEPS to $MAXIMUM_STEPS") }
        }
        if (!isStartPowerValid) {
            changeState { it.copy(startPowerError = "Start power is invalid") }
        }
        if (!isEndPowerValid) {
            changeState { it.copy(endPowerError = "End power is invalid") }
        }
        return isStartPowerValid && isEndPowerValid && isDurationValid && isStepCountValid
    }

    private fun setResult(startPower: Int, endPower: Int, stepCount: Int, duration: Long) {
            _setStairsResult.postValue(
                Event(
                    WorkoutStairsParams(
                        startPower = startPower,
                        endPower = endPower,
                        duration = duration,
                        steps = stepCount
                    )
                )
            )
    }

    companion object {
        private const val MINIMUM_DURATION = 5
        private const val MINIMUM_STEPS = 3
        private const val MAXIMUM_STEPS = 20
    }
}

