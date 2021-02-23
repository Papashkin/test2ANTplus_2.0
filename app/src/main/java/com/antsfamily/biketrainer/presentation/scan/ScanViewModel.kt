package com.antsfamily.biketrainer.presentation.scan

import androidx.lifecycle.viewModelScope
import com.antsfamily.biketrainer.ant.device.SelectedDevice
import com.antsfamily.biketrainer.domain.Result
import com.antsfamily.biketrainer.domain.usecase.BindAntChannelUseCase
import com.antsfamily.biketrainer.domain.usecase.UnbindAntChannelUseCase
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
        val isLoading: Boolean = false,
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
        showLoading()
        doUnbindChannelService()
        navigateBack()
        hideLoading()
    }

    fun onDeviceClick(item: SelectedDevice) {
        changeState { state ->
            state.copy(
                devices = state.devices.map {
                    it.copy(
                        isSelected = if (it.device.resultID == item.device.resultID) {
                            !item.isSelected
                        } else {
                            it.isSelected
                        }
                    )
                }
            )
        }
    }

    // TODO: workout implementation will be soon
//    fun onContinueClick() {
//        doUnbindChannelService()
//    }

    private fun setDeviceSearcherCallbacks() = viewModelScope.launch {
        deviceSearcher.apply {
            setOnDeviceReceiveListener {
                postChangeState { state -> state.copy(devices = state.devices.plus(it)) }
            }
            setOnErrorReceiveListener {
//                showSnackbar(it)
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

    private fun showLoading() {
        changeState { it.copy(isLoading = true) }
    }

    private fun hideLoading() {
        changeState { it.copy(isLoading = false) }
    }

    private fun showCommonErrorSnackbar() {
        showSnackbar("Something went wrong. Please try it later or check ANT+ services")
    }
}
