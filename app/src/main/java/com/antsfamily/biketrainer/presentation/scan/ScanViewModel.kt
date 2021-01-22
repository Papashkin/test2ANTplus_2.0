package com.antsfamily.biketrainer.presentation.scan

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.dsi.ant.AntService
import com.dsi.ant.plugins.antplus.pcc.MultiDeviceSearch
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.ant.device.SelectedDevice
import com.antsfamily.biketrainer.ant.service.AntRadioServiceConnection
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

val deviceList: EnumSet<DeviceType> = EnumSet.of(
    DeviceType.BIKE_CADENCE,
    DeviceType.BIKE_POWER,
    DeviceType.BIKE_SPD,
    DeviceType.BIKE_SPDCAD,
    DeviceType.CONTROLLABLE_DEVICE,
    DeviceType.FITNESS_EQUIPMENT,
    DeviceType.HEARTRATE
)

class ScanViewModel @Inject constructor(
    private val context: Context
) : StatefulViewModel<ScanViewModel.State>(State()) {
    private val TAG = ScanViewModel::class.java.simpleName.toUpperCase(Locale.getDefault())

    data class State(
        val foundedDevices: ArrayList<SelectedDevice> = arrayListOf(),
        var search: MultiDeviceSearch? = null
    )

    private var connection: AntRadioServiceConnection? = null

    init {
        connection = AntRadioServiceConnection(context)
        doBindChannelService()
    }

    fun onScanClick() = launch {
        startScanAsync().await()
    }

    //    private val foundedDevices: ArrayList<SelectedDevice> = arrayListOf()
    var devices: MutableLiveData<List<SelectedDevice>> = MutableLiveData(listOf())
    private val antCallback = object : MultiDeviceSearch.SearchCallbacks {
        override fun onSearchStopped(reason: RequestAccessResult) {
            val id = when (reason) {
                RequestAccessResult.CHANNEL_NOT_AVAILABLE -> R.string.channel_not_available
                RequestAccessResult.OTHER_FAILURE -> R.string.channel_other_failure
                RequestAccessResult.SEARCH_TIMEOUT -> R.string.channel_search_timeout
                RequestAccessResult.USER_CANCELLED -> R.string.channel_scan_stop
                else -> R.string.channel_unknown
            }
            hideLoading()
            showToast(id)
        }

        override fun onSearchStarted(rssiSupport: MultiDeviceSearch.RssiSupport) {
            showToast(R.string.scan_start)
        }

        override fun onDeviceFound(device: MultiDeviceSearchResult) {
            Log.v(TAG, "New device : id ${device.resultID}, type ${device.antDeviceType}")
            Handler(Looper.getMainLooper()).post {
                val selectedDevice = SelectedDevice(
                    device = device,
                    isSelected = false
                )
                if (devices.value?.contains(selectedDevice) == false) {
//                    foundedDevices.addAll(devices.value ?: listOf())
//                    foundedDevices.add(selectedDevice)
//                    devices.postValue(foundedDevices)
                }
            }
        }
    }

    //    private var search: MultiDeviceSearch? = null
    private fun startScanAsync() = async {
        try {
            showLoading()
//            search = MultiDeviceSearch(context, deviceList, antCallback)
//            delay(20_000)
//            search?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Channel working failed with $e")
        } finally {
            hideLoading()
        }
    }

    private fun doBindChannelService() {
        Log.v(TAG, "Bind channel service...")
        AntService.bindService(context, connection)
    }

    private fun doUnbindChannelService() = launch {
        try {
            Log.v(TAG, "Unbinding channel service...")
            connection?.closeBackgroundScanChannel()
        } catch (e: Exception) {
            Log.e(TAG, "Channel working failed with $e")
        }
    }

    var connectButtonVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    fun onDeviceClick() {
//        val count = foundedDevices.filter { it.isSelected }.size
//        connectButtonVisibility.postValue(count > 0)
    }

    fun connectToSelectedDevices(program: String, profileName: String) {
//        search?.close()
//        router.navigateTo(
//            FragmentScreens.WorkScreen(
//                devices = foundedDevices
//                    .filter { it.isSelected }
//                    .map { it.device } as ArrayList<MultiDeviceSearchResult>,
//                program = program,
//                profileName = profileName
//            ))
    }

    fun onBackPressed() {
        doUnbindChannelService()
//        router.exit()
    }

    fun clear() {
//        search?.close()
        doUnbindChannelService()
        connectButtonVisibility.postValue(false)
        devices.postValue(listOf())
    }
}
