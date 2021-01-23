package com.antsfamily.biketrainer.ant.device

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedAccumulatedDistanceReceiver
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedSpeedReceiver
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc
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

class BikeSpeedDistanceDevice @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var bsdPcc: AntPlusBikeSpeedDistancePcc? = null
    private var bcPcc: AntPlusBikeCadencePcc? = null
    private var bcReleaseHandle: PccReleaseHandle<AntPlusBikeCadencePcc>? = null
    private var onCadenceReceiveListener: ((cadence: BigDecimal) -> Unit)? = null
    private var onDistanceReceiveListener: ((distance: BigDecimal) -> Unit)? = null
    private var onSpeedReceiveListener: ((speed: BigDecimal) -> Unit)? = null
    private var onToastShowListener: ((text: String) -> Unit)? = null
    private var onDependenciesSetListener: ((name: String, packageName: String) -> Unit)? = null

    private val _resultReceiver: IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc> =
        object : IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc> {
            // Handle the result, connecting to events on success or reporting failure to user.
            override fun onResultReceived(
                result: AntPlusBikeSpeedDistancePcc,
                resultCode: RequestAccessResult,
                initialDeviceState: DeviceState
            ) {
                when (resultCode) {
                    RequestAccessResult.SUCCESS -> {
                        bsdPcc = result
                        bsdPcc?.let {
                            subscribeToEvents()
                        }
                    }
                    RequestAccessResult.CHANNEL_NOT_AVAILABLE -> {
                        showToast("Channel Not Available")
                    }
                    RequestAccessResult.ADAPTER_NOT_DETECTED -> {
                        showToast("ANT Adapter Not Available.\nBuilt-in ANT hardware or external adapter required.")
                    }
                    RequestAccessResult.BAD_PARAMS -> {
                        showToast("Bad request parameters.")
                    }
                    RequestAccessResult.OTHER_FAILURE -> {
                        showToast("RequestAccess failed. See logcat for details.")
                    }
                    RequestAccessResult.DEPENDENCY_NOT_INSTALLED -> {
                        onDependenciesSetListener?.invoke(
                            AntPlusHeartRatePcc.getMissingDependencyName(),
                            AntPlusHeartRatePcc.getMissingDependencyPackageName()
                        )
                    }
                    RequestAccessResult.UNRECOGNIZED -> {
                        showToast("Failed: UNRECOGNIZED.\nPluginLib Upgrade Required?")
                    }
                    else -> {
                        showToast("Unrecognized result: $resultCode")
                    }
                }
            }

            /**
             * Subscribe to all the heart rate events, connecting them to display
             * their data.
             */
            private fun subscribeToEvents() {
                // 2.095m circumference = an average 700cx23mm road tire
                bsdPcc?.subscribeCalculatedSpeedEvent(
                    object : CalculatedSpeedReceiver(BigDecimal(2.095)) {
                        override fun onNewCalculatedSpeed(
                            estTimestamp: Long,
                            eventFlags: EnumSet<EventFlag>,
                            speed: BigDecimal
                        ) {
                            Handler(Looper.getMainLooper()).post {
                                onSpeedReceiveListener?.invoke(
                                    speed.setScale(
                                        1,
                                        BigDecimal.ROUND_HALF_DOWN
                                    )
                                )
                            }
                        }
                    })

                bsdPcc?.subscribeCalculatedAccumulatedDistanceEvent(
                    object : CalculatedAccumulatedDistanceReceiver(BigDecimal(2.095)) {
                        override fun onNewCalculatedAccumulatedDistance(
                            estTimestamp: Long,
                            eventFlags: EnumSet<EventFlag>,
                            distance: BigDecimal
                        ) {
                            Handler(Looper.getMainLooper()).post {
                                onDistanceReceiveListener?.invoke(
                                    distance.setScale(2, BigDecimal.ROUND_HALF_DOWN)
                                )
                            }
                        }
                    })

                bsdPcc?.subscribeRawSpeedAndDistanceDataEvent { _, _, speed, distance ->
                    Handler(Looper.getMainLooper()).post {
                        onSpeedReceiveListener?.invoke(speed)
                        onDistanceReceiveListener?.invoke(distance.toBigDecimal())
                    }
                }

                if (bsdPcc!!.isSpeedAndCadenceCombinedSensor) {
                    Handler(Looper.getMainLooper()).post {
                        bcReleaseHandle = AntPlusBikeCadencePcc.requestAccess(
                            context,
                            bsdPcc!!.antDeviceNumber,
                            0,
                            true,
                            { result, resultCode, _ ->
                                // IPluginAccessResultReceiver<AntPlusBikeCadencePcc> :
                                // Handle the result, connecting to events
                                // on success or reporting failure to user.
                                when (resultCode) {
                                    RequestAccessResult.SUCCESS -> {
                                        bcPcc = result
                                        bcPcc?.let {
                                            it.subscribeCalculatedCadenceEvent { _, _, cadence ->
                                                Handler(Looper.getMainLooper()).post {
                                                    onCadenceReceiveListener?.invoke(cadence)
                                                }
                                            }
                                        }
                                    }
                                    RequestAccessResult.CHANNEL_NOT_AVAILABLE -> {
                                    }

                                    RequestAccessResult.BAD_PARAMS -> {
                                    }

                                    RequestAccessResult.OTHER_FAILURE -> {
                                    }

                                    RequestAccessResult.DEPENDENCY_NOT_INSTALLED -> {
                                    }

                                    else -> showToast("Unrecognized result: $resultCode")
                                }
                            },
                            // Receives state changes and shows it on the status display line

                            { state ->
                                // AntPluginPcc.IDeviceStateChangeReceiver:
                                Handler(Looper.getMainLooper()).post {
                                    if (state == DeviceState.DEAD) {
                                        bcPcc = null
                                    }
                                }
                            })
                    }
                } else {
                    // Subscribe to the events available in the pure cadence profile
                    Handler(Looper.getMainLooper()).post {
                    }

                    bsdPcc?.subscribeCumulativeOperatingTimeEvent { _, _, _ ->
                        Handler(Looper.getMainLooper()).post {
                        }
                    }

                    bsdPcc?.subscribeMotionAndSpeedDataEvent { _, _, _ ->
                        Handler(Looper.getMainLooper()).post {
                        }
                    }
                }
            }
        }

    // Receives state changes and shows it on the status display line
    private val _deviceStateChangeReceiver = AntPluginPcc.IDeviceStateChangeReceiver { state ->
        Handler(Looper.getMainLooper()).post {
            if (state == DeviceState.DEAD) {
                bsdPcc = null
            }
        }
    }

    val resultReceiver: IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc>
        get() = _resultReceiver

    val deviceStateChangeReceiver: AntPluginPcc.IDeviceStateChangeReceiver
        get() = _deviceStateChangeReceiver

    fun setOnCadenceReceiveListener(listener: (cadence: BigDecimal) -> Unit) {
        onCadenceReceiveListener = listener
    }

    fun setOnDistanceReceiveListener(listener: (distance: BigDecimal) -> Unit) {
        onDistanceReceiveListener = listener
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
