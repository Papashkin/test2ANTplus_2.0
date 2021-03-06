package com.antsfamily.biketrainer.ant.device

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Base class to connect, subscribe and clear the Heart Rate Plugin.
 */
class HeartRateDevice @Inject constructor(@ApplicationContext private val context: Context) {

    private var _heartRateMonitor: AntPlusHeartRatePcc? = null

    fun getSensorAccess(
        deviceNumber: Int,
        resultReceivedCallback: (result: RequestAccessResult) -> Unit
    ) {
        AntPlusHeartRatePcc.requestAccess(
            context,
            deviceNumber,
            SEARCH_PROXIMITY_THRESHOLD,
            { result, resultCode, _ ->
                if (resultCode == RequestAccessResult.SUCCESS) {
                    _heartRateMonitor = result
                }
                resultReceivedCallback.invoke(resultCode)
            },
            { state ->
                if (state == DeviceState.DEAD) {
                    _heartRateMonitor = null
                }
            }
        )
    }

    /**
     * Switches the active view to the data display and subscribes to all the data events
     */
    fun subscribe(heartRateCallback: (heartRate: Int) -> Unit) {
        if (_heartRateMonitor == null) return

        _heartRateMonitor?.subscribeHeartRateDataEvent { _, _, computedHeartRate, _, _, _ ->
            Handler(Looper.getMainLooper()).post {
                heartRateCallback.invoke(computedHeartRate)
            }
        }
    }

    fun clear() {
        _heartRateMonitor = null
    }

    companion object {
        private const val SEARCH_PROXIMITY_THRESHOLD = 0
    }
}
