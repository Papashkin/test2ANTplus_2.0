package com.antsfamily.biketrainer.util

import android.content.Context
import android.util.Log
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.ant.device.SelectedDevice
import com.dsi.ant.plugins.antplus.pcc.MultiDeviceSearch
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceSearcher @Inject constructor(@ApplicationContext private val context: Context) {

    private var onErrorReceiveListener: ((id: Int) -> Unit)? = null
    private var onDeviceReceiveListener: ((device: SelectedDevice) -> Unit)? = null

    private val callback = object : MultiDeviceSearch.SearchCallbacks {
        override fun onSearchStopped(reason: RequestAccessResult) {
            Log.d(this@DeviceSearcher::class.java.simpleName, "Search stopped because of $reason")
            val id = when (reason) {
                RequestAccessResult.CHANNEL_NOT_AVAILABLE -> R.string.channel_not_available
                RequestAccessResult.OTHER_FAILURE -> R.string.channel_other_failure
                RequestAccessResult.SEARCH_TIMEOUT -> R.string.channel_search_timeout
                RequestAccessResult.USER_CANCELLED -> R.string.channel_scan_stop
                else -> R.string.channel_unknown
            }
            onErrorReceiveListener?.invoke(id)
        }

        override fun onSearchStarted(rssiSupport: MultiDeviceSearch.RssiSupport) {
            Log.d(this@DeviceSearcher::class.java.simpleName, "Search started")
        }

        override fun onDeviceFound(device: MultiDeviceSearchResult) {
            Log.d(this@DeviceSearcher::class.java.simpleName, "Device found: $device")
            onDeviceReceiveListener?.invoke(SelectedDevice(device = device))
        }
    }

    private var searcher: MultiDeviceSearch? = null

    fun setOnErrorReceiveListener(listener: (id: Int) -> Unit) {
        onErrorReceiveListener = listener
    }

    fun setOnDeviceReceiveListener(listener: (device: SelectedDevice) -> Unit) {
        onDeviceReceiveListener = listener
    }

    fun start() {
        searcher = MultiDeviceSearch(context, DEVICE_LIST, callback)
    }

    fun stop() {
        searcher?.close()
        searcher = null
    }

    companion object {
        private val DEVICE_LIST = EnumSet.of(
            DeviceType.BIKE_CADENCE,
            DeviceType.BIKE_POWER,
            DeviceType.BIKE_SPD,
            DeviceType.BIKE_SPDCAD,
            DeviceType.CONTROLLABLE_DEVICE,
            DeviceType.FITNESS_EQUIPMENT,
            DeviceType.HEARTRATE
        )
    }
}
