package com.antsfamily.biketrainer.presentation.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.antsfamily.biketrainer.ant.device.*
import com.antsfamily.biketrainer.data.models.DeviceItem
import com.antsfamily.biketrainer.domain.Result
import com.antsfamily.biketrainer.domain.usecase.BindAntChannelUseCase
import com.antsfamily.biketrainer.domain.usecase.UnbindAntChannelUseCase
import com.antsfamily.biketrainer.navigation.ScanToWorkout
import com.antsfamily.biketrainer.presentation.Event
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import com.antsfamily.biketrainer.util.DeviceSearcher
import com.antsfamily.biketrainer.util.getErrorMessageOrNull
import com.dsi.ant.plugins.antplus.pcc.*
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScanViewModel @Inject constructor(
    private val bindAntChannelUseCase: BindAntChannelUseCase,
    private val unbindAntChannelUseCase: UnbindAntChannelUseCase,
    private val deviceSearcher: DeviceSearcher,
    private val heartRateDevice: HeartRateDevice,
    private val bikeCadenceDevice: BikeCadenceDevice,
    private val bikeSpeedDistanceDevice: BikeSpeedDistanceDevice,
    private val bikePowerDevice: BikePowerDevice,
    private val fitnessEquipmentDevice: FitnessEquipmentDevice
) : StatefulViewModel<ScanViewModel.State>(State()) {

    data class State(
        val devices: List<DeviceItem> = emptyList(),
        val isContinueButtonVisible: Boolean = false,
    )

    private var _showDeviceDialogEvent = MutableLiveData<Event<Pair<String, String>?>>()
    val showDeviceDialogEvent: LiveData<Event<Pair<String, String>?>>
        get() = _showDeviceDialogEvent

    private var programName: String? = null

    init {
        setDeviceSearcherCallbacks()
    }

    fun onResume() {
        doBindChannelService()
    }

    fun onPause() {
        doUnbindChannelService()
    }

    fun onCreate(programName: String) {
        this.programName = programName
    }

    fun onBackClick() {
        navigateBack()
    }

    fun onDeviceClick(item: DeviceItem) {
        showDeviceLoading(item.device)
        if (isDeviceAvailableToSelect(item.device)) {
            getAccessToSensor(item.device)
        } else {
            clearSensorAccess(item.device)
        }
    }

    fun onContinueClick() {
        programName?.let { program ->
            state.value?.devices?.filter { it.isSelected }?.map { it.device }?.let { devices ->
                navigateTo(ScanToWorkout(devices, program))
            }
        }
    }

    private fun getAccessToSensor(device: MultiDeviceSearchResult) {
        when (device.antDeviceType) {
            DeviceType.HEARTRATE -> getHeartRateAccess(device)
            DeviceType.BIKE_CADENCE -> getBikeCadenceAccess(device)
            DeviceType.BIKE_SPD -> getBikeSpeedAccess(device, false)
            DeviceType.BIKE_SPDCAD -> getBikeSpeedAccess(device, true)
            DeviceType.BIKE_POWER -> getBikePowerAccess(device)
            DeviceType.FITNESS_EQUIPMENT -> getFitnessEquipmentAccess(device)
            else -> {
                // no-op
            }
        }
    }

    private fun clearSensorAccess(device: MultiDeviceSearchResult) {
        when (device.antDeviceType) {
            DeviceType.HEARTRATE -> heartRateDevice.clear()
            DeviceType.BIKE_CADENCE -> bikeCadenceDevice.clear()
            DeviceType.BIKE_SPD -> bikeSpeedDistanceDevice.clear(false)
            DeviceType.BIKE_SPDCAD -> bikeSpeedDistanceDevice.clear(true)
            DeviceType.BIKE_POWER -> bikePowerDevice.clear()
            DeviceType.FITNESS_EQUIPMENT -> fitnessEquipmentDevice.clear()
            else -> {
                // no-op
            }
        }
        revertDeviceSelected(device)
        hideDeviceLoading(device)
        checkContinueViewVisibility()
    }

    private fun getHeartRateAccess(device: MultiDeviceSearchResult) = viewModelScope.launch {
        heartRateDevice.getSensorAccess(device.antDeviceNumber) {
            handleAccessRequestResult(it, device)
        }
    }

    private fun getBikeCadenceAccess(device: MultiDeviceSearchResult) = viewModelScope.launch {
        bikeCadenceDevice.getAccess(device.antDeviceNumber) {
            handleAccessRequestResult(it, device)
        }
    }

    private fun getBikeSpeedAccess(device: MultiDeviceSearchResult, isCombinedSensor: Boolean) =
        viewModelScope.launch {
            bikeSpeedDistanceDevice.getSensorAccess(device.antDeviceNumber, isCombinedSensor) {
                handleAccessRequestResult(it, device)
            }
        }

    private fun getBikePowerAccess(device: MultiDeviceSearchResult) = viewModelScope.launch {
        bikePowerDevice.getAccess(device.antDeviceNumber) {
            handleAccessRequestResult(it, device)
        }
    }

    private fun getFitnessEquipmentAccess(device: MultiDeviceSearchResult) = viewModelScope.launch {
        fitnessEquipmentDevice.getAccess(device.antDeviceNumber) {
            handleAccessRequestResult(it, device)
        }
    }

    private fun handleAccessRequestResult(
        result: RequestAccessResult,
        device: MultiDeviceSearchResult
    ) {
        hideDeviceLoading(device)
        when (result) {
            RequestAccessResult.SUCCESS -> setDeviceSelected(device)
            RequestAccessResult.DEPENDENCY_NOT_INSTALLED -> checkDependencyAndShowDialog(device.antDeviceType)
            else -> showErrorSnackbar(result.getErrorMessageOrNull())
        }
        checkContinueViewVisibility()
    }

    private fun checkDependencyAndShowDialog(type: DeviceType) {
        val (dependencyName, dependencyPackage) = when (type) {
            DeviceType.HEARTRATE -> AntPlusHeartRatePcc.getMissingDependencyName() to AntPlusHeartRatePcc.getMissingDependencyPackageName()
            DeviceType.BIKE_CADENCE -> AntPlusBikeCadencePcc.getMissingDependencyName() to AntPlusBikeCadencePcc.getMissingDependencyPackageName()
            DeviceType.BIKE_SPD,
            DeviceType.BIKE_SPDCAD -> AntPlusBikeSpeedDistancePcc.getMissingDependencyName() to AntPlusBikeSpeedDistancePcc.getMissingDependencyPackageName()
            DeviceType.BIKE_POWER -> AntPlusBikePowerPcc.getMissingDependencyName() to AntPlusBikePowerPcc.getMissingDependencyPackageName()
            DeviceType.FITNESS_EQUIPMENT -> AntPlusFitnessEquipmentPcc.getMissingDependencyName() to AntPlusFitnessEquipmentPcc.getMissingDependencyPackageName()
            else -> null to null
        }
        dependencyName?.let { name ->
            dependencyPackage?.let { pack ->
                showDialog(name, pack)
            }
        }
    }

    private fun isDeviceAvailableToSelect(device: MultiDeviceSearchResult): Boolean {
        return state.value?.devices?.firstOrNull { it.device == device }?.isSelected == false
    }

    private fun setDeviceSelected(device: MultiDeviceSearchResult) {
        changeState { state ->
            state.copy(
                devices = state.devices.map {
                    if (it.device == device) it.copy(isSelected = true, isLoading = false) else it
                }
            )
        }
    }

    private fun revertDeviceSelected(device: MultiDeviceSearchResult) {
        changeState { state ->
            state.copy(
                devices = state.devices.map {
                    if (it.device == device) it.copy(isSelected = false, isLoading = false) else it
                }
            )
        }
    }

    private fun showDeviceLoading(device: MultiDeviceSearchResult) {
        changeState { state ->
            state.copy(devices = state.devices.map {
                if (it.device == device) it.copy(isLoading = true) else it
            })
        }
    }

    private fun hideDeviceLoading(device: MultiDeviceSearchResult) {
        changeState { state ->
            state.copy(devices = state.devices.map {
                if (it.device == device) it.copy(isLoading = false) else it
            })
        }
    }

    private fun showDialog(name: String, packageName: String) {
        _showDeviceDialogEvent.postValue(Event(Pair(name, packageName)))
    }

    private fun setDeviceSearcherCallbacks() = viewModelScope.launch {
        deviceSearcher.apply {
            setOnDeviceReceiveListener { result ->
                postChangeState { state ->
                    state.copy(
                        devices = if (state.devices.any { it.device.resultID == result.resultID }) {
                            state.devices
                        } else {
                            state.devices.plus(
                                DeviceItem(result, isSelected = false, isLoading = false)
                            )
                        }
                    )
                }
            }
            setOnErrorReceiveListener { showErrorSnackbar("Something went wrong :(") }
        }
    }

    private fun doBindChannelService() = viewModelScope.launch {
        bindAntChannelUseCase(Unit, ::handleChannelBindResult)
    }

    private fun handleChannelBindResult(isSuccess: Boolean) {
        if (isSuccess) {
            startDeviceSearching()
        } else {
            showCommonErrorSnackbar()
        }
    }

    private fun startDeviceSearching() = viewModelScope.launch { deviceSearcher.start() }

    private fun stopDeviceSearching() = viewModelScope.launch { deviceSearcher.stop() }

    private fun doUnbindChannelService() = viewModelScope.launch {
        unbindAntChannelUseCase(Unit, ::handleChannelUnbindResult)
    }

    private fun handleChannelUnbindResult(result: Result<Unit, Error>) {
        when (result) {
            is Result.Success -> stopDeviceSearching()
            is Result.Failure -> showCommonErrorSnackbar()
        }
    }

    private fun checkContinueViewVisibility() {
        changeState { state ->
            state.copy(
                isContinueButtonVisible = state.devices.any { it.isSelected && it.isFitnessEquipment() }
            )
        }
    }

    private fun showCommonErrorSnackbar() {
        showErrorSnackbar("Something went wrong. Please try it later or check ANT+ services")
    }
}
