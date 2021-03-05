package com.antsfamily.biketrainer.presentation.workout

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.antsfamily.biketrainer.ant.device.BikeCadenceDevice
import com.antsfamily.biketrainer.ant.device.BikeSpeedDistanceDevice
import com.antsfamily.biketrainer.ant.device.FitnessEquipmentDevice
import com.antsfamily.biketrainer.ant.device.HeartRateDevice
import com.antsfamily.biketrainer.data.models.WorkoutSensorValues
import com.antsfamily.biketrainer.data.models.program.Program
import com.antsfamily.biketrainer.domain.Result
import com.antsfamily.biketrainer.domain.usecase.GetProgramUseCase
import com.antsfamily.biketrainer.presentation.Event
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import com.antsfamily.biketrainer.util.fullTimeFormat
import com.antsfamily.biketrainer.util.orZero
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.inject.Inject

class WorkoutViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getProgramUseCase: GetProgramUseCase,
    private val cadenceCensor: BikeCadenceDevice,
    private val heartRateCensor: HeartRateDevice,
    private val speedDistanceCensor: BikeSpeedDistanceDevice,
    private val fitnessEquipmentCensor: FitnessEquipmentDevice
) : StatefulViewModel<WorkoutViewModel.State>(State()) {

    data class State(
        val isLoading: Boolean = true,
        val title: String? = null,
        val steps: Pair<Int, Int>? = null,
        val startButtonVisible: Boolean = true,
        val pauseButtonVisible: Boolean = false,
        val stopButtonVisible: Boolean = false,
        val sensorsData: WorkoutSensorValues = WorkoutSensorValues(),
        val remainingTimeString: String = 0L.fullTimeFormat(),
        val progress: Int = 100,
        val nextStep: Pair<Int, String>? = null
    )

    private var _showDeviceDialogEvent = MutableLiveData<Event<Pair<String, String>?>>()
    val showDeviceDialogEvent: LiveData<Event<Pair<String, String>?>>
        get() = _showDeviceDialogEvent

    private val timer: Timer = Timer()
    private val task = object : TimerTask() {
        override fun run() {
            displayTrainingData()
        }
    }
    private var handleHeartRate: PccReleaseHandle<AntPlusHeartRatePcc>? = null
    private var handleCadence: PccReleaseHandle<AntPlusBikeCadencePcc>? = null
    private var handleSpeedDistance: PccReleaseHandle<AntPlusBikeSpeedDistancePcc>? = null
    private var handleEquipment: PccReleaseHandle<AntPlusFitnessEquipmentPcc>? = null
    private var isHRMInWork = false
    private var isCadenceInWork = false
    private var isSpeedInWork = false
    private var isPowerInWork = false
    private var workoutValues: WorkoutSensorValues = WorkoutSensorValues()

    init {
        setupCadenceCensor()
        setupHeartRateCensor()
        setupSpeedDistanceCensor()
        setupFitnessEquipmentCensor()
        setupTimer()
    }

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
        timer.cancel()
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
            is Result.Success -> handleProgramSuccessResult(result.successData)
            is Result.Failure -> {
            }
        }
        setDevices(devices)
    }

    private fun handleProgramSuccessResult(program: Program) {
        changeState {
            it.copy(
                isLoading = false,
                title = program.title,
                steps = Pair(0, program.data.size),
                nextStep = Pair(
                    program.data.firstOrNull()?.power.orZero(),
                    program.data.firstOrNull()?.duration.orZero().fullTimeFormat()
                ),
                remainingTimeString = 0L.fullTimeFormat(), // program.data.sumOf { data -> data.duration }.fullTimeFormat(),
                progress = 100,
                startButtonVisible = true
            )
        }
    }

    private fun setDevices(devices: List<MultiDeviceSearchResult>) {
        devices.forEach {
            if (it.antDeviceType == DeviceType.HEARTRATE && !isHRMInWork) {
                setHeartRateAccess(it)
            }
            if (it.antDeviceType == DeviceType.BIKE_CADENCE && !isCadenceInWork) {
                setCadenceAccess(it)
            }
            if (it.antDeviceType == DeviceType.BIKE_SPDCAD && !isCadenceInWork) {
                setCadenceAccess(it)
            }
            if (it.antDeviceType == DeviceType.BIKE_SPD && !isSpeedInWork) {
                setSpeedAccess(it)
            }
            if (it.antDeviceType == DeviceType.FITNESS_EQUIPMENT && !isPowerInWork) {
                setFitnessEquipmentAccess(it)
            }
        }
    }

    private fun setHeartRateAccess(device: MultiDeviceSearchResult) = viewModelScope.launch {
        handleHeartRate = AntPlusHeartRatePcc.requestAccess(
            context,
            device.antDeviceNumber,
            0,
            heartRateCensor.baseIPluginAccessResultReceiver,
            heartRateCensor.baseDeviceChangeReceiver
        )
        isHRMInWork = true
    }

    private fun setCadenceAccess(device: MultiDeviceSearchResult) = viewModelScope.launch {
        handleCadence = AntPlusBikeCadencePcc.requestAccess(
            context,
            device.antDeviceNumber,
            0,
            true,
            cadenceCensor.resultReceiver,
            cadenceCensor.deviceStateChangeReceiver
        )
        isCadenceInWork = true
    }

    private fun setSpeedAccess(device: MultiDeviceSearchResult) = viewModelScope.launch {
        handleSpeedDistance = AntPlusBikeSpeedDistancePcc.requestAccess(
            context,
            device.antDeviceNumber,
            0,
            true,
            speedDistanceCensor.resultReceiver,
            speedDistanceCensor.deviceStateChangeReceiver
        )
        isSpeedInWork = true
    }

    private fun setFitnessEquipmentAccess(device: MultiDeviceSearchResult) = viewModelScope.launch {
        handleEquipment = AntPlusFitnessEquipmentPcc.requestNewOpenAccess(
            context,
            device.antDeviceNumber,
            0,
            fitnessEquipmentCensor.pluginAccessResultReceiver,
            fitnessEquipmentCensor.deviceStateChangeReceiver,
            fitnessEquipmentCensor.fitnessEquipmentStateReceiver
        )
        isPowerInWork = true
    }

    private fun closeAccessToSensors() {
        handleHeartRate?.close()
        handleCadence?.close()
        handleSpeedDistance?.close()
        handleEquipment?.close()
    }

    private fun showDialog(name: String, packageName: String) {
        _showDeviceDialogEvent.postValue(Event(Pair(name, packageName)))
    }

    private fun setupCadenceCensor() {
        cadenceCensor.apply {
            setOnCadenceReceiveListener { cadence -> getCadenceValue(cadence) }
            setOnSpeedReceiveListener { speed -> getSpeedValue(speed) }
            setOnToastShowListener { showErrorSnackbar(it) }
            setOnDependenciesSetListener { name, packageName -> showDialog(name, packageName) }
        }
    }

    private fun setupHeartRateCensor() {
        heartRateCensor.apply {
            setOnHeartRateReceiveListener { heartRate -> getHeartRateValue(heartRate) }
            setOnToastShowListener { showErrorSnackbar(it) }
            setOnDependenciesSetListener { name, packageName -> showDialog(name, packageName) }
        }
    }

    private fun setupSpeedDistanceCensor() {
        speedDistanceCensor.apply {
            setOnCadenceReceiveListener { cadence -> if (!isCadenceInWork) getCadenceValue(cadence) }
            setOnDistanceReceiveListener { distance -> getDistanceValue(distance) }
            setOnSpeedReceiveListener { speed -> if (!isCadenceInWork) getSpeedValue(speed) }
            setOnToastShowListener { showErrorSnackbar(it) }
            setOnDependenciesSetListener { name, packageName -> showDialog(name, packageName) }
        }
    }

    private fun setupFitnessEquipmentCensor() {
        fitnessEquipmentCensor.apply {
            setOnCadenceReceiveListener { if (!isCadenceInWork or !isSpeedInWork) getCadenceValue(it) }
            setOnDistanceReceiveListener { if (!isSpeedInWork) getDistanceValue(it) }
            setOnPowerReceiveListener { getPowerValue(it) }
            setOnSpeedReceiveListener { if (!isCadenceInWork or !isSpeedInWork) getSpeedValue(it) }
            setOnShowToastListener { showErrorSnackbar(it) }
            setOnSetDependenciesListener { name, packageName -> showDialog(name, packageName) }
            setOnDeviceStateListener { state -> checkFitnessEquipmentState(state) }
        }
    }

    private fun checkFitnessEquipmentState(state: AntPlusFitnessEquipmentPcc.EquipmentState) {
        when (state) {
            AntPlusFitnessEquipmentPcc.EquipmentState.READY -> {
                showSuccessSnackbar("Bike trainer is ready to use")
            }
            AntPlusFitnessEquipmentPcc.EquipmentState.IN_USE -> {
                showErrorSnackbar("Bike trainer you selected is already in use.\nPlease select another controllable device")
                handleEquipment?.close()
            }
            else -> handleEquipment?.close()
        }
    }

    private fun setupTimer() {
        timer.schedule(task, 2_000, 3_000)
    }

    private fun displayTrainingData() = viewModelScope.launch {
        changeState { it.copy(sensorsData = workoutValues) }
    }

    private fun getHeartRateValue(rate: Int) {
        workoutValues.heartRate = rate
    }

    private fun getCadenceValue(cadence: BigDecimal) {
        workoutValues.cadence = cadence.intValueExact()
    }

    private fun getSpeedValue(speed: BigDecimal) {
        workoutValues.speed = speed.setScale(2, RoundingMode.HALF_DOWN)
    }

    private fun getDistanceValue(distance: BigDecimal) {
        workoutValues.distance = distance.setScale(2, RoundingMode.HALF_DOWN)
    }

    private fun getPowerValue(power: BigDecimal) {
        workoutValues.power = power.intValueExact()
    }
}
