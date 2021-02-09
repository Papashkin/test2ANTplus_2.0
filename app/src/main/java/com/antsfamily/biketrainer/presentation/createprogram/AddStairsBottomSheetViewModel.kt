package com.antsfamily.biketrainer.presentation.createprogram

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antsfamily.biketrainer.data.models.ProgramType
import com.antsfamily.biketrainer.data.models.workouts.WorkoutStairsParams
import com.antsfamily.biketrainer.presentation.Event
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import javax.inject.Inject

class AddStairsBottomSheetViewModel @Inject constructor(

) : StatefulViewModel<AddStairsBottomSheetViewModel.State>(State()) {

    data class State(
        val startPowerError: String? = null,
        val endPowerError: String? = null,
        val durationError: String? = null
    )

    private val _setStairsUpResult = MutableLiveData<Event<WorkoutStairsParams>>()
    val setStairsUpResult: LiveData<Event<WorkoutStairsParams>>
        get() = _setStairsUpResult

    private val _setStairsDownResult = MutableLiveData<Event<WorkoutStairsParams>>()
    val setStairsDownResult: LiveData<Event<WorkoutStairsParams>>
        get() = _setStairsDownResult

    private var stairsType: ProgramType? = null

    fun onCreate(type: ProgramType) {
        stairsType = type
    }

    fun onAddClick(startPower: Int, endPower: Int, duration: Long) {
        if (isValid(startPower, endPower, duration)) {
            setResult(startPower, endPower, duration)
        }
    }

    fun onStartPowerTextChange() {
        changeState { it.copy(startPowerError = null) }
    }

    fun onEndPowerTextChange() {
        changeState { it.copy(endPowerError = null) }
    }

    fun onDurationChange() {
        changeState { it.copy(durationError = null) }
    }

    private fun isValid(startPower: Int, endPower: Int, duration: Long): Boolean {
        val isStartPowerValid = startPower > 0
        val isEndPowerValid = endPower > 0
        val isStairsUpWorkout = if (stairsType == ProgramType.STEPS_UP) startPower < endPower else true
        val isStairsDownWorkout = if (stairsType == ProgramType.STEPS_DOWN) startPower > endPower else true
        val isDurationValid = duration > MINIMUM_DURATION

        if (!isDurationValid) {
            changeState { it.copy(durationError = "Duration is invalid. It should be at least more than $MINIMUM_DURATION sec") }
        }
        if (!isStairsUpWorkout) {
            changeState { it.copy(startPowerError = "Start power should be less than the end power") }
        }
        if (!isStairsDownWorkout) {
            changeState { it.copy(startPowerError = "Start power should be more than rest power") }
        }
        if (!isStartPowerValid) {
            changeState { it.copy(startPowerError = "Start power is invalid") }
        }
        if (!isEndPowerValid) {
            changeState { it.copy(endPowerError = "End power is invalid") }
        }
        return isStartPowerValid && isEndPowerValid && isDurationValid && isStairsUpWorkout && isStairsDownWorkout
    }

    private fun setResult(startPower: Int, endPower: Int, duration: Long) {
        if (stairsType == ProgramType.STEPS_UP) {
            _setStairsUpResult.postValue(
                Event(
                    WorkoutStairsParams(
                        startPower = startPower,
                        endPower = endPower,
                        duration = duration,
                        type = ProgramType.STEPS_UP
                    )
                )
            )
        }
        if (stairsType == ProgramType.STEPS_DOWN) {
            _setStairsDownResult.postValue(
                Event(
                    WorkoutStairsParams(
                        startPower = startPower,
                        endPower = endPower,
                        duration = duration,
                        type = ProgramType.STEPS_DOWN
                    )
                )
            )
        }
    }

    companion object {
        private const val MINIMUM_DURATION = 5
    }
}

