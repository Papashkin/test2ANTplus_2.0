package com.example.test2antplus.ant.device

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc
import com.pawegio.kandroid.runOnUiThread

/**
 * Base class to connects to Heart Rate Plugin
 * and display all the event data.
 */
class HeartRateDevice(
    private val getHearRate: (heartRate: String) -> Unit,
    private val showToast: (text: String) -> Unit,
    private val setDependencies: (name: String, packageName: String) -> Unit
) {
    private var hrPcc: AntPlusHeartRatePcc? = null

    val baseIPluginAccessResultReceiver =
        AntPluginPcc.IPluginAccessResultReceiver<AntPlusHeartRatePcc> { result, resultCode, _ ->
            // Handle the result, connecting to events on success or reporting failure to user.
            when (resultCode) {
                RequestAccessResult.SUCCESS -> {
                    hrPcc = result
                    subscribeToHrEvents()
                }
                RequestAccessResult.CHANNEL_NOT_AVAILABLE -> {
                    showToast.invoke("Channel is not available.")
                }

                RequestAccessResult.ADAPTER_NOT_DETECTED -> {
                    showToast.invoke("ANT Adapter Not Available.\\nBuilt-in ANT hardware or external adapter required.")
                }

                RequestAccessResult.BAD_PARAMS -> {
                    showToast.invoke("Bad request parameters.")
                }

                RequestAccessResult.OTHER_FAILURE -> {
                    showToast.invoke("RequestAccess failed.\nSee logcat for details.")
                }

                RequestAccessResult.DEPENDENCY_NOT_INSTALLED -> {
                    setDependencies.invoke(
                        AntPlusHeartRatePcc.getMissingDependencyName(),
                        AntPlusHeartRatePcc.getMissingDependencyPackageName()
                    )
                }
                RequestAccessResult.UNRECOGNIZED -> {
                    showToast.invoke("Failed: UNRECOGNIZED.\nPluginLib Upgrade Required?")
                }
                else -> {
                    showToast.invoke("Unrecognized result: $resultCode")
                }
            }
        }

    val baseDeviceChangeReceiver = AntPluginPcc.IDeviceStateChangeReceiver { state ->
        if (state == DeviceState.DEAD) {
            hrPcc = null
        }
    }

    /**
     * Switches the active view to the data display
     * and subscribes to all the data events
     */
    private fun subscribeToHrEvents() {
        if (hrPcc == null) return

        hrPcc?.let {
            it.subscribeHeartRateDataEvent { _, _, computedHeartRate, _, _, dataState ->
                val textHeartRate =
                    computedHeartRate.toString() +
                            (if (AntPlusHeartRatePcc.DataState.ZERO_DETECTED == dataState) "*" else "")
                runOnUiThread {
                    getHearRate.invoke(textHeartRate)
                }
            }
        }
    }
}