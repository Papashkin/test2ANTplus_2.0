package com.antsfamily.biketrainer.presentation.workout

import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.antsfamily.biketrainer.ant.device.*
import com.antsfamily.biketrainer.data.models.program.Program
import com.antsfamily.biketrainer.data.models.program.ProgramData
import com.antsfamily.biketrainer.domain.Result
import com.antsfamily.biketrainer.domain.usecase.GetProgramUseCase
import com.antsfamily.biketrainer.domain.usecase.WorkoutTimerFlow
import com.antsfamily.biketrainer.presentation.Event
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import com.antsfamily.biketrainer.util.orZero
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.dsi.ant.plugins.antplus.pcc.defines.RequestStatus
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import com.garmin.fit.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.ZoneOffset
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
        val allRounds: Int? = null,
        val currentRound: Int = 0,
        val startButtonVisible: Boolean = true,
        val pauseButtonVisible: Boolean = false,
        val stopButtonVisible: Boolean = false,
        val remainingTime: Long = 0L,
        val progress: Int = 100,
        val currentStep: ProgramData? = null,
        val nextStep: ProgramData? = null,
        val heartRate: Int? = null,
        val cadence: Int? = null,
        val speed: BigDecimal? = null,
        val distance: BigDecimal? = null,
        val power: Int? = null,
        val program: List<ProgramData> = emptyList()
    )

    private val _resetChartHighlightsEvent = MutableLiveData<Event<Unit>>()
    val resetChartHighlightsEvent: LiveData<Event<Unit>>
        get() = _resetChartHighlightsEvent

    private var isWorkoutStarted: Boolean = false
    private var isTargetPowerSetSuccessfully: Boolean = false
    private var currentStepNumber: Int = 0
    private var programName: String? = null
    private var activityFile: File? = null

    private val workoutMessage: MutableList<Mesg> = mutableListOf()

    private val timestamp: Long
        get() = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

    fun onStop() {
        closeAccessToSensors()
    }

    fun onBackClick() {
        navigateBack()
    }

    fun onStartClick() = viewModelScope.launch {
        showLoading()
        createFitFile()
        isWorkoutStarted = true
        changeState {
            it.copy(
                isLoading = false,
                startButtonVisible = false,
                pauseButtonVisible = true,
                stopButtonVisible = false
            )
        }
    }

    fun onPauseClick() {

    }

    fun onStopClick() {

    }

    fun onCreate(devices: List<MultiDeviceSearchResult>, programName: String) {
        this.programName = programName
        getProgramUseCase(programName) {
            handleProgramResult(it, devices)
        }
    }

    private fun handleProgramResult(
        result: Result<Program, Error>, devices: List<MultiDeviceSearchResult>
    ) {
        when (result) {
            is Result.Success -> handleProgramSuccessResult(result.successData, devices)
            is Result.Failure -> showErrorSnackbar("Something went wrong :( \nPlease try again")
        }
    }

    private fun handleProgramSuccessResult(
        program: Program,
        devices: List<MultiDeviceSearchResult>
    ) {
        setDevices(devices)
        changeState {
            it.copy(
                title = program.title,
                allRounds = program.data.size,
                currentRound = currentStepNumber,
                nextStep = program.data[currentStepNumber],
                remainingTime = program.data[currentStepNumber].duration,
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
        hideLoading()
        startWorkoutTimerFlow()
    }

    private fun startWorkoutTimerFlow() = viewModelScope.launch {
        workoutTimerFlow(PERIOD).collect {
            showDataFromSensors()
            if (isWorkoutStarted) {
                setTargetPowerToDevice()
                updateView()
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

    private fun showDataFromSensors() {
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

    private fun setTargetPowerToDevice() {
        if (!isTargetPowerSetSuccessfully) {
            state.value?.currentStep?.let { data ->
                fitnessEquipmentDevice.setTargetPower(
                    data.power.toBigDecimal(),
                    {
                        Log.d(this::class.java.simpleName, "Set target power with result: $it")
                        isTargetPowerSetSuccessfully = it == RequestStatus.SUCCESS
                    }, {
                        isTargetPowerSetSuccessfully = it
                    }
                )
            }
        }
    }

    private fun updateView() {
        val remainingTime = state.value?.remainingTime.orZero().minus(1)
        when {
            (remainingTime > ZERO) -> updateView(remainingTime)
            (remainingTime == ZERO) -> {
                currentStepNumber = currentStepNumber.inc()
                if (currentStepNumber < state.value?.program?.size.orZero()) {
                    val updatedRemainingTime = state.value?.program
                        ?.getOrNull(currentStepNumber)?.duration.orZero()
                    isTargetPowerSetSuccessfully = false
                    updateView(updatedRemainingTime)
                    createLapMessage(timestamp)
                } else {
                    _resetChartHighlightsEvent.postValue(Event(Unit))
                    isWorkoutStarted = false
                    currentStepNumber = 0
                    resetWorkoutFields()
                    showSuccessSnackbar("Your workout is finished! Well done!")
                }
            }
        }
    }

    private fun updateView(remainingTime: Long) {
        changeState { state ->
            state.copy(
                currentRound = currentStepNumber + 1,
                currentStep = state.program.getOrNull(currentStepNumber),
                nextStep = state.program.getOrNull(currentStepNumber + 1),
                progress = remainingTime.times(HUNDRED)
                    .div(state.program.getOrNull(currentStepNumber)?.duration ?: 1).toInt(),
                remainingTime = remainingTime
            )
        }
        createRecordMessage()
    }

    private fun resetWorkoutFields() {
        changeState {
            it.copy(
                currentRound = currentStepNumber,
                currentStep = null,
                nextStep = it.program.getOrNull(currentStepNumber),
                remainingTime = it.program.getOrNull(currentStepNumber)?.duration.orZero(),
                progress = 100,
                startButtonVisible = true,
                pauseButtonVisible = false,
                stopButtonVisible = false,
                program = it.program
            )
        }
    }

    private fun getHeartRateValue() = heartRateDevice.heartRate.orZero()

    private fun getCadenceValue() = (cadenceDevice.cadence ?: fitnessEquipmentDevice.cadence)
        ?.setScale(2, RoundingMode.HALF_DOWN).orZero().toInt()

    private fun getSpeedValue() =
        (speedDistanceDevice.speed ?: fitnessEquipmentDevice.speed)
            ?.setScale(2, RoundingMode.HALF_DOWN).orZero()

    private fun getDistanceValue() =
        (speedDistanceDevice.distance ?: fitnessEquipmentDevice.distance)
            ?.setScale(2, RoundingMode.HALF_DOWN).orZero().divide(METERS_IN_KILOMETER)

    private fun getPowerValue() = (powerDevice.power ?: fitnessEquipmentDevice.power)
        ?.setScale(2, RoundingMode.HALF_DOWN).orZero().toInt()

    private fun createFitFile() {
        val folder = File(Environment.getExternalStorageState() + "/activities")
        if (!folder.exists()) {
            folder.mkdir()
        }
        activityFile = File(folder, "${programName.orEmpty()}.fit")
        workoutMessage.apply {
            add(
                FileIdMesg().apply {
                    type = com.garmin.fit.File.ACTIVITY
                    manufacturer = Manufacturer.DEVELOPMENT
                    timeCreated = DateTime(timestamp)
                }
            )
            add(
                DeviceInfoMesg().apply {
                    manufacturer = Manufacturer.DEVELOPMENT
                    timestamp = DateTime(timestamp)
                }
            )
        }
        createLapMessage(timestamp)
    }

    private fun createRecordMessage() {
        workoutMessage.add(
            RecordMesg().apply {
                timestamp = DateTime(timestamp)
                heartRate = getHeartRateValue().toShort()
                power = getPowerValue()
                distance = getDistanceValue().toFloat()
                cadence = getCadenceValue().toShort()
                speed = getSpeedValue().toFloat()
            }
        )
    }

    private fun createLapMessage(time: Long) {
        workoutMessage.add(
            LapMesg().apply {
                timestamp = DateTime(timestamp)
                startTime = DateTime(time)
                totalElapsedTime = state.value?.program?.getOrNull(currentStepNumber)?.duration.orZero().toFloat()
                totalTimerTime = state.value?.program?.getOrNull(currentStepNumber)?.duration.orZero().toFloat()
            }
        )
    }

    private fun showLoading() {
        changeState { it.copy(isLoading = true) }
    }

    private fun hideLoading() {
        changeState { it.copy(isLoading = false) }
    }

    companion object {
        private const val ZERO = 0L
        private const val PERIOD = 1000L
        private const val HUNDRED = 100
        private val METERS_IN_KILOMETER = BigDecimal(1000)
    }
}
