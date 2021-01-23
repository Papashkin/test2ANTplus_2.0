package com.antsfamily.biketrainer.ant.device

import android.os.Handler
import android.os.Looper
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc
import javax.inject.Inject

/**
 * Base class to connects to Heart Rate Plugin
 * and display all the event data.
 */
class HeartRateDevice @Inject constructor() {
    private var hrPcc: AntPlusHeartRatePcc? = null
    private var onShowToastListener: ((text: String) -> Unit)? = null
    private var onSetDependenciesListener: ((name: String, packageName: String) -> Unit)? = null
    private var onHeartRateReceiveListener: ((heartRate: Int) -> Unit)? = null

    private val _baseIPluginAccessResultReceiver =
        AntPluginPcc.IPluginAccessResultReceiver<AntPlusHeartRatePcc> { result, resultCode, _ ->
            // Handle the result, connecting to events on success or reporting failure to user.
            when (resultCode) {
                RequestAccessResult.SUCCESS -> {
                    hrPcc = result
                    subscribeToHrEvents()
                }
                RequestAccessResult.CHANNEL_NOT_AVAILABLE -> showToast("Channel is not available.")
                RequestAccessResult.ADAPTER_NOT_DETECTED -> showToast("ANT Adapter Not Available.\nBuilt-in ANT hardware or external adapter required.")
                RequestAccessResult.BAD_PARAMS -> showToast("Bad request parameters.")
                RequestAccessResult.OTHER_FAILURE -> showToast("RequestAccess failed.\nSee logcat for details.")

                RequestAccessResult.DEPENDENCY_NOT_INSTALLED -> {
                    onSetDependenciesListener?.invoke(
                        AntPlusHeartRatePcc.getMissingDependencyName(),
                        AntPlusHeartRatePcc.getMissingDependencyPackageName()
                    )
                }
                RequestAccessResult.UNRECOGNIZED -> showToast("Failed: UNRECOGNIZED.\nPluginLib Upgrade Required?")
                else -> showToast("Unrecognized result: $resultCode")
            }
        }

    private val _baseDeviceChangeReceiver = AntPluginPcc.IDeviceStateChangeReceiver { state ->
        if (state == DeviceState.DEAD) {
            hrPcc = null
        }
    }

    val baseIPluginAccessResultReceiver: AntPluginPcc.IPluginAccessResultReceiver<AntPlusHeartRatePcc>
        get() = _baseIPluginAccessResultReceiver

    val baseDeviceChangeReceiver: AntPluginPcc.IDeviceStateChangeReceiver
        get() = _baseDeviceChangeReceiver

    fun setOnHeartRateReceiveListener(listener: (heartRate: Int) -> Unit) {
        onHeartRateReceiveListener = listener
    }

    fun setOnToastShowListener(listener: (text: String) -> Unit) {
        onShowToastListener = listener
    }

    fun setOnDependenciesSetListener(listener: (name: String, packageName: String) -> Unit) {
        onSetDependenciesListener = listener
    }

    /**
     * Switches the active view to the data display
     * and subscribes to all the data events
     */
    private fun subscribeToHrEvents() {
        if (hrPcc == null) return

        hrPcc?.let {
            it.subscribeHeartRateDataEvent { _, _, computedHeartRate, _, _, _ ->
                Handler(Looper.getMainLooper()).post {
                    onHeartRateReceiveListener?.invoke(computedHeartRate)
                }
            }
        }
    }

    private fun showToast(text: String) {
        onShowToastListener?.invoke(text)
    }
}
