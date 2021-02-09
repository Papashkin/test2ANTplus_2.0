package com.antsfamily.biketrainer.presentation.createprogram

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antsfamily.biketrainer.data.models.workouts.WorkoutIntervalParams
import com.antsfamily.biketrainer.presentation.Event
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import javax.inject.Inject

class AddIntervalBottomSheetViewModel @Inject constructor(

) : StatefulViewModel<AddIntervalBottomSheetViewModel.State>(State()) {

    data class State(
        val peakPowerError: String? = null,
        val restPowerError: String? = null,
        val peakDurationError: String? = null,
        val restDurationError: String? = null,
        val countError: String? = null
    )

    private val _setIntervalResult = MutableLiveData<Event<WorkoutIntervalParams>>()
    val setIntervalResult: LiveData<Event<WorkoutIntervalParams>>
        get() = _setIntervalResult

    fun onAddClick(
        peakPower: Int,
        restPower: Int,
        peakDuration: Long,
        restDuration: Long,
        count: Int
    ) {
        if (isValid(peakPower, restPower, peakDuration, restDuration, count)) {
            _setIntervalResult.postValue(
                Event(
                    WorkoutIntervalParams(
                        peakPower = peakPower,
                        peakDuration = peakDuration,
                        restPower = restPower,
                        restDuration = restDuration,
                        times = count
                    )
                )
            )
        }
    }

    fun onPeakPowerTextChange() {
        changeState { it.copy(peakPowerError = null) }
    }

    fun onRestPowerTextChange() {
        changeState { it.copy(restPowerError = null) }
    }

    fun onPeakDurationChange() {
        changeState { it.copy(peakDurationError = null) }
    }

    fun onRestDurationChange() {
        changeState { it.copy(restDurationError = null) }
    }

    fun onCountChange() {
        changeState { it.copy(countError = null) }
    }

    private fun isValid(
        peakPower: Int,
        restPower: Int,
        peakDuration: Long,
        restDuration: Long,
        count: Int
    ): Boolean {
        val isPeakPowerValid = peakPower > 0
        val isRestPowerValid = restPower > 0
        val isPeakPowerMoreThanRestPower = peakPower > restPower
        val isPeakDurationValid = peakDuration > MINIMUM_DURATION
        val isRestDurationValid = restDuration > MINIMUM_DURATION
        val isCountValid = count > 0

        if (!isPeakDurationValid) {
            changeState { it.copy(peakDurationError = "Duration is invalid. It should be at least more than $MINIMUM_DURATION sec") }
        }
        if (!isPeakPowerMoreThanRestPower) {
            changeState { it.copy(peakPowerError = "Peak power should be more than rest power") }
        }
        if (!isPeakPowerValid) {
            changeState { it.copy(peakPowerError = "Peak power is invalid") }
        }
        if (!isRestDurationValid) {
            changeState { it.copy(restDurationError = "Duration is invalid. It should be at least more than $MINIMUM_DURATION sec") }
        }
        if (!isRestPowerValid) {
            changeState { it.copy(restPowerError = "Rest power is invalid") }
        }
        if (!isCountValid) {
            changeState { it.copy(countError = "Counter is invalid") }
        }
        return isPeakPowerValid && isRestPowerValid && isPeakDurationValid && isRestDurationValid && isCountValid
    }

    companion object {
        private const val MINIMUM_DURATION = 5
    }
}
