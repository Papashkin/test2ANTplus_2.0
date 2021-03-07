package com.antsfamily.biketrainer.presentation.workout

import androidx.lifecycle.viewModelScope
import com.antsfamily.biketrainer.ant.device.*
import com.antsfamily.biketrainer.data.models.program.Program
import com.antsfamily.biketrainer.data.models.program.ProgramData
import com.antsfamily.biketrainer.domain.Result
import com.antsfamily.biketrainer.domain.usecase.GetProgramUseCase
import com.antsfamily.biketrainer.domain.usecase.WorkoutTimerFlow
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import com.antsfamily.biketrainer.util.fullTimeFormat
import com.antsfamily.biketrainer.util.orZero
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class WorkoutViewModel @Inject constructor(
    private val getProgramUseCase: GetProgramUseCase,
    private val heartRateDevice: HeartRateDevice,
    private val cadenceDevice: BikeCadenceDevice,
    private val powerDevice: BikePowerDevice,
    private val speedDistanceDevice: BikeSpeedDistanceDevice,
    private val fitnessEquipmentDevice: FitnessEquipmentDevice,
    private val workoutTimerFlow: WorkoutTimerFlow
) : StatefulViewModel<WorkoutViewModel.State>(State()) {

    data class State(
        val isLoading: Boolean = true,
        val title: String? = null,
        val steps: Pair<Int, Int>? = null,
        val startButtonVisible: Boolean = true,
        val pauseButtonVisible: Boolean = false,
        val stopButtonVisible: Boolean = false,
        val remainingTimeString: String = 0L.fullTimeFormat(),
        val progress: Int = 100,
        val nextStep: Pair<Int, String>? = null,
        val heartRate: Int? = null,
        val cadence: Int? = null,
        val speed: BigDecimal? = null,
        val distance: BigDecimal? = null,
        val power: Int? = null,
        val program: List<ProgramData> = emptyList()
    )

    fun onStop() {
        closeAccessToSensors()
    }

    fun onBackClick() {
        navigateBack()
    }

    fun onStartClick() = viewModelScope.launch {
        // TODO: 15.01.2021
    }

    fun onPauseClick() {

    }

    fun onStopClick() {

    }

    fun onCreate(devices: List<MultiDeviceSearchResult>, programName: String) {
        getProgramUseCase(programName) {
            handleProgramResult(it, devices)
        }
    }

    private fun handleProgramResult(
        result: Result<Program, Error>, devices: List<MultiDeviceSearchResult>
    ) {
        when (result) {
            is Result.Success -> handleProgramSuccessResult(result.successData, devices)
            is Result.Failure -> {
            }
        }
    }

    private fun handleProgramSuccessResult(program: Program, devices: List<MultiDeviceSearchResult>) {
        setDevices(devices)
        changeState {
            it.copy(
                title = program.title,
                steps = Pair(0, program.data.size),
                nextStep = Pair(
                    program.data.firstOrNull()?.power.orZero(),
                    program.data.firstOrNull()?.duration.orZero().fullTimeFormat()
                ),
                remainingTimeString = 0L.fullTimeFormat(), // program.data.sumOf { data -> data.duration }.fullTimeFormat(),
                progress = 100,
                startButtonVisible = true,
                program = program.data
            )
        }
    }

    private fun setDevices(devices: List<MultiDeviceSearchResult>) {
        devices.forEach { device ->
            when (device.antDeviceType) {
                DeviceType.HEARTRATE -> subscribeToHeartRate()
                DeviceType.BIKE_CADENCE -> subscribeToBikeCadence()
                DeviceType.BIKE_SPD -> subscribeToBikeSpeed()
                DeviceType.BIKE_SPDCAD -> subscribeToBikeSpeed()
                DeviceType.BIKE_POWER -> subscribeToBikePower()
                DeviceType.FITNESS_EQUIPMENT -> subscribeToFitnessEquipment()
                else -> {
                    // no-op
                }
            }
        }
        changeState { it.copy(isLoading = false) }
        startWorkoutTimerFlow()
    }

    private fun startWorkoutTimerFlow() = viewModelScope.launch {
        workoutTimerFlow.invoke(PERIOD).collect {
            changeState {
                it.copy(
                    heartRate = getHeartRateValue(),
                    cadence = getCadenceValue(),
                    power = getPowerValue(),
                    speed = getSpeedValue(),
                    distance = getDistanceValue()
                )
            }
        }
    }

    private fun subscribeToBikeCadence() = viewModelScope.launch {
        cadenceDevice.subscribe()
    }

    private fun subscribeToHeartRate() = viewModelScope.launch {
        heartRateDevice.subscribe()
    }

    private fun subscribeToBikeSpeed() = viewModelScope.launch {
        speedDistanceDevice.subscribe()
    }

    private fun subscribeToBikePower() = viewModelScope.launch {
        powerDevice.subscribe()
    }

    private fun subscribeToFitnessEquipment() = viewModelScope.launch {
        fitnessEquipmentDevice.subscribe(::showErrorSnackbar)
    }

    private fun closeAccessToSensors() {
        heartRateDevice.clear()
        cadenceDevice.clear()
        speedDistanceDevice.clear(true)
        powerDevice.clear()
        fitnessEquipmentDevice.clear()
    }

    private fun getHeartRateValue() = heartRateDevice.heartRate

    private fun getCadenceValue() = (cadenceDevice.cadence ?: fitnessEquipmentDevice.cadence)
        ?.setScale(2, RoundingMode.HALF_DOWN)?.toInt()

    private fun getSpeedValue() = (speedDistanceDevice.speed ?: fitnessEquipmentDevice.speed)
        ?.setScale(2, RoundingMode.HALF_DOWN)

    private fun getDistanceValue() =
        (speedDistanceDevice.distance ?: fitnessEquipmentDevice.distance)
            ?.setScale(2, RoundingMode.HALF_DOWN)?.divide(METERS_IN_KILOMETER)

    private fun getPowerValue() = (powerDevice.power ?: fitnessEquipmentDevice.power)
        ?.setScale(2, RoundingMode.HALF_DOWN)?.toInt()

    companion object {
        private const val PERIOD = 1000L
        private val METERS_IN_KILOMETER = BigDecimal(1000)
    }
}
