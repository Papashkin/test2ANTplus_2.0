package com.example.test2antplus.ant.device

import android.content.Context
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedSpeedReceiver
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import com.pawegio.kandroid.runOnUiThread
import java.math.BigDecimal
import java.util.*

class BikeCadenceDevice(
    private val context: Context,
    private val getCadence: (cadence: String) -> Unit,
    private val getSpeed: (speed: String) -> Unit,
    private val showToast: (text: String) -> Unit,
    private val setDependencies: (name: String, packageName: String) -> Unit
) {
    private var bsReleaseHandle: PccReleaseHandle<AntPlusBikeSpeedDistancePcc>? = null
    private var bcPcc: AntPlusBikeCadencePcc? = null
    private var bsPcc: AntPlusBikeSpeedDistancePcc? = null

    val mResultReceiver =
        object : IPluginAccessResultReceiver<AntPlusBikeCadencePcc> {
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
                        showToast.invoke("Channel Not Available")
                    }
                    RequestAccessResult.ADAPTER_NOT_DETECTED -> {
                        showToast.invoke("ANT Adapter Not Available.\nBuilt-in ANT hardware or external adapter required.")
                    }
                    RequestAccessResult.BAD_PARAMS -> {
                        // Note: Since we compose all the params ourself, we should never see this result
                        showToast.invoke("Bad request parameters.")
                    }
                    RequestAccessResult.OTHER_FAILURE -> {
                        showToast.invoke("RequestAccess failed. See logcat for details.")
                    }
                    RequestAccessResult.DEPENDENCY_NOT_INSTALLED -> {
                        setDependencies.invoke(
                            AntPlusBikeCadencePcc.getMissingDependencyName(),
                            AntPlusBikeCadencePcc.getMissingDependencyPackageName()
                        )
                    }
                    RequestAccessResult.UNRECOGNIZED -> {
                        showToast.invoke("Failed: UNRECOGNIZED. PluginLib Upgrade Required?")
                    }
                    else -> {
                        showToast.invoke("Unrecognized result: $resultCode")
                    }
                }
            }

            /**
             * Subscribe to all the heart rate events, connecting them to display their data.
             */
            private fun subscribeToEvents() {
                bcPcc?.subscribeCalculatedCadenceEvent { _, _, cadence ->
                    runOnUiThread {
                        getCadence.invoke(cadence.toString())
                    }
                }

                bcPcc?.subscribeRawCadenceDataEvent { _, _, _, _ ->
                    runOnUiThread {
                    }
                }

                if (bcPcc!!.isSpeedAndCadenceCombinedSensor) {
                    runOnUiThread {
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
                                                runOnUiThread {
                                                    getSpeed.invoke(speed.toString())
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
                                runOnUiThread {
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
                        runOnUiThread {
                        }
                    }

                    bcPcc?.subscribeBatteryStatusEvent { _, _, _, _ ->
                        runOnUiThread {
                        }
                    }

                    bcPcc?.subscribeMotionAndCadenceDataEvent { _, _, isPedallingStopped ->
                        runOnUiThread {
                            if (isPedallingStopped) showToast.invoke("Крути, сука!")
                        }
                    }
                }
            }
        }

    // Receives state changes and shows it on the status display line
    val mDeviceStateChangeReceiver = AntPluginPcc.IDeviceStateChangeReceiver { state ->
        runOnUiThread {
            if (state == DeviceState.DEAD) {
                bcPcc = null
            }
        }
    }
}