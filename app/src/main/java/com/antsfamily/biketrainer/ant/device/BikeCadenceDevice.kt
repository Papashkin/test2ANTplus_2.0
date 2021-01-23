package com.antsfamily.biketrainer.ant.device

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedSpeedReceiver
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import dagger.hilt.android.qualifiers.ApplicationContext
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class BikeCadenceDevice @Inject constructor(@ApplicationContext private val context: Context) {

    private var bsReleaseHandle: PccReleaseHandle<AntPlusBikeSpeedDistancePcc>? = null
    private var bcPcc: AntPlusBikeCadencePcc? = null
    private var bsPcc: AntPlusBikeSpeedDistancePcc? = null
    private var onCadenceReceiveListener: ((cadence: BigDecimal) -> Unit)? = null
    private var onSpeedReceiveListener: ((speed: BigDecimal) -> Unit)? = null
    private var onToastShowListener: ((text: String) -> Unit)? = null
    private var onDependenciesSetListener: ((name: String, packageName: String) -> Unit)? = null

    private val _resultReceiver = object : IPluginAccessResultReceiver<AntPlusBikeCadencePcc> {
        // Handle the result, connecting to events on success or reporting failure to user.
        override fun onResultReceived(
            result: AntPlusBikeCadencePcc,
            resultCode: RequestAccessResult,
            initialDeviceState: DeviceState
        ) {
            when (resultCode) {
                RequestAccessResult.SUCCESS -> {
                    bcPcc = result
                    subscribeToEvents()
                }
                RequestAccessResult.CHANNEL_NOT_AVAILABLE -> {
                    showToast("Channel Not Available")
                }
                RequestAccessResult.ADAPTER_NOT_DETECTED -> {
                    showToast("ANT Adapter Not Available.\nBuilt-in ANT hardware or external adapter required.")
                }
                RequestAccessResult.BAD_PARAMS -> {
                    // Note: Since we compose all the params ourself, we should never see this result
                    showToast("Bad request parameters.")
                }
                RequestAccessResult.OTHER_FAILURE -> {
                    showToast("RequestAccess failed. See logcat for details.")
                }
                RequestAccessResult.DEPENDENCY_NOT_INSTALLED -> {
                    onDependenciesSetListener?.invoke(
                        AntPlusBikeCadencePcc.getMissingDependencyName(),
                        AntPlusBikeCadencePcc.getMissingDependencyPackageName()
                    )
                }
                RequestAccessResult.UNRECOGNIZED -> {
                    showToast("Failed: UNRECOGNIZED. PluginLib Upgrade Required?")
                }
                else -> showToast("Unrecognized result: $resultCode")
            }
        }

        /**
         * Subscribe to all the heart rate events, connecting them to display their data.
         */
        private fun subscribeToEvents() {
            bcPcc?.subscribeCalculatedCadenceEvent { _, _, cadence ->
                Handler(Looper.getMainLooper()).post { onCadenceReceiveListener?.invoke(cadence) }
            }

            bcPcc?.subscribeRawCadenceDataEvent { _, _, _, _ ->
                Handler(Looper.getMainLooper()).post {}
            }

            if (bcPcc?.isSpeedAndCadenceCombinedSensor == true) {
                Handler(Looper.getMainLooper()).post {
                    bsReleaseHandle = AntPlusBikeSpeedDistancePcc.requestAccess(
                        context,
                        bcPcc!!.antDeviceNumber,
                        0,
                        true,
                        { result, resultCode, _ ->
                            // Handle the result, connecting to events on success or reporting failure to user.
                            when (resultCode) {
                                RequestAccessResult.SUCCESS -> {
                                    bsPcc = result
                                    bsPcc?.subscribeCalculatedSpeedEvent(object :
                                        CalculatedSpeedReceiver(BigDecimal(2.095)) {
                                        override fun onNewCalculatedSpeed(
                                            estTimestamp: Long,
                                            eventFlags: EnumSet<EventFlag>,
                                            speed: BigDecimal
                                        ) {
                                            Handler(Looper.getMainLooper()).post {
                                                onSpeedReceiveListener?.invoke(speed)
                                            }
                                        }

                                    })
                                }

                                RequestAccessResult.CHANNEL_NOT_AVAILABLE -> {
                                }//tv_calculatedSpeed.setText("CHANNEL NOT AVAILABLE")

                                RequestAccessResult.BAD_PARAMS -> {
                                }//tv_calculatedSpeed.setText("BAD_PARAMS")

                                RequestAccessResult.OTHER_FAILURE -> {
                                } //tv_calculatedSpeed.setText("OTHER FAILURE")

                                RequestAccessResult.DEPENDENCY_NOT_INSTALLED -> {
                                } //tv_calculatedSpeed.setText("DEPENDENCY NOT INSTALLED")

                                else -> {
                                }//tv_calculatedSpeed.setText("UNRECOGNIZED ERROR: $resultCode")
                            }
                        },
                        // Receives state changes and shows it on the status display line
                        { newDeviceState ->
                            Handler(Looper.getMainLooper()).post {
                                if (newDeviceState != DeviceState.TRACKING) {
                                }
                                if (newDeviceState == DeviceState.DEAD) {
                                    bsPcc = null
                                }
                            }
                        })
                }
            } else {
                // Subscribe to the events available in the pure cadence profile
                bcPcc?.subscribeCumulativeOperatingTimeEvent { _, _, _ ->
                    Handler(Looper.getMainLooper()).post {
                    }
                }

                bcPcc?.subscribeBatteryStatusEvent { _, _, _, _ ->
                    Handler(Looper.getMainLooper()).post {
                    }
                }

                bcPcc?.subscribeMotionAndCadenceDataEvent { _, _, isPedallingStopped ->
                    Handler(Looper.getMainLooper()).post {
                        if (isPedallingStopped) onToastShowListener?.invoke("Крути, сука!")
                    }
                }
            }
        }
    }

    // Receives state changes and shows it on the status display line
    private val _deviceStateChangeReceiver = AntPluginPcc.IDeviceStateChangeReceiver { state ->
        Handler(Looper.getMainLooper()).post {
            if (state == DeviceState.DEAD) {
                bcPcc = null
            }
        }
    }

    val resultReceiver: IPluginAccessResultReceiver<AntPlusBikeCadencePcc>
        get() = _resultReceiver

    val deviceStateChangeReceiver: AntPluginPcc.IDeviceStateChangeReceiver
        get() = _deviceStateChangeReceiver

    fun setOnCadenceReceiveListener(listener: (cadence: BigDecimal) -> Unit) {
        onCadenceReceiveListener = listener
    }

    fun setOnSpeedReceiveListener(listener: (speed: BigDecimal) -> Unit) {
        onSpeedReceiveListener = listener
    }

    fun setOnToastShowListener(listener: (text: String) -> Unit) {
        onToastShowListener = listener
    }

    fun setOnDependenciesSetListener(listener: (name: String, packageName: String) -> Unit) {
        onDependenciesSetListener = listener
    }

    private fun showToast(text: String) {
        onToastShowListener?.invoke(text)
    }
}
