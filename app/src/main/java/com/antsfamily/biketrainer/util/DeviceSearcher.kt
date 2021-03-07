package com.antsfamily.biketrainer.util

import android.content.Context
import android.util.Log
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

    private var onSearchStopListener: ((reason: RequestAccessResult) -> Unit)? = null
    private var onDeviceReceiveListener: ((device: MultiDeviceSearchResult) -> Unit)? = null

    private val callback = object : MultiDeviceSearch.SearchCallbacks {
        override fun onSearchStopped(reason: RequestAccessResult) {
            Log.d(this@DeviceSearcher::class.java.simpleName, "Search stopped because of $reason")
            onSearchStopListener?.invoke(reason)
        }

        override fun onSearchStarted(rssiSupport: MultiDeviceSearch.RssiSupport) {
            Log.d(this@DeviceSearcher::class.java.simpleName, "Search started")
        }

        override fun onDeviceFound(device: MultiDeviceSearchResult) {
            Log.d(this@DeviceSearcher::class.java.simpleName, "Device found: $device")
            onDeviceReceiveListener?.invoke(device)
        }
    }

    private var searcher: MultiDeviceSearch? = null

    fun setOnSearchStopListener(listener: (reason: RequestAccessResult) -> Unit) {
        onSearchStopListener = listener
    }

    fun setOnDeviceReceiveListener(listener: (device: MultiDeviceSearchResult) -> Unit) {
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
            DeviceType.FITNESS_EQUIPMENT,
            DeviceType.HEARTRATE
        )
    }
}
