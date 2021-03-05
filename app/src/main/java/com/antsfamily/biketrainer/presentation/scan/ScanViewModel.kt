package com.antsfamily.biketrainer.presentation.scan

import androidx.lifecycle.viewModelScope
import com.antsfamily.biketrainer.ant.device.SelectedDevice
import com.antsfamily.biketrainer.domain.Result
import com.antsfamily.biketrainer.domain.usecase.BindAntChannelUseCase
import com.antsfamily.biketrainer.domain.usecase.UnbindAntChannelUseCase
import com.antsfamily.biketrainer.navigation.ScanToWorkout
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import com.antsfamily.biketrainer.util.DeviceSearcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScanViewModel @Inject constructor(
    private val bindAntChannelUseCase: BindAntChannelUseCase,
    private val unbindAntChannelUseCase: UnbindAntChannelUseCase,
    private val deviceSearcher: DeviceSearcher
) : StatefulViewModel<ScanViewModel.State>(State()) {

    data class State(
        val devices: List<SelectedDevice> = emptyList(),
        val isContinueButtonVisible: Boolean = false,
    )

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

    fun onDeviceClick(item: SelectedDevice) {
        changeState { state ->
            state.copy(
                devices = state.devices.map {
                    it.copy(
                        isSelected = if (it.device == item.device) {
                            !it.isSelected
                        } else {
                            it.isSelected
                        }
                    )
                }
            )
        }
        checkContinueViewVisibility()
    }

    fun onContinueClick() {
        programName?.let{ program ->
            state.value?.devices?.filter { it.isSelected }?.map { it.device }?.let { devices ->
                navigateTo(ScanToWorkout(devices, program))
            }
        }
    }

    private fun setDeviceSearcherCallbacks() = viewModelScope.launch {
        deviceSearcher.apply {
            setOnDeviceReceiveListener {
                postChangeState { state -> state.copy(devices = state.devices.plus(it)) }
            }
            setOnErrorReceiveListener {
                showErrorSnackbar("Something went wrong :(")
            }
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
