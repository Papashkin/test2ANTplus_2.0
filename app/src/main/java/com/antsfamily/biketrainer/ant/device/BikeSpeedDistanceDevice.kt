package com.antsfamily.biketrainer.ant.device

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.antsfamily.biketrainer.util.orZero
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedAccumulatedDistanceReceiver
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedSpeedReceiver
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import dagger.hilt.android.qualifiers.ApplicationContext
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class BikeSpeedDistanceDevice @Inject constructor(@ApplicationContext private val context: Context) {
    private var _speedDistance: AntPlusBikeSpeedDistancePcc? = null
    private var _cadence: AntPlusBikeCadencePcc? = null
    private var onCadenceReceiveListener: ((cadence: BigDecimal) -> Unit)? = null
    private var onDistanceReceiveListener: ((distance: BigDecimal) -> Unit)? = null
    private var onSpeedReceiveListener: ((speed: BigDecimal) -> Unit)? = null

    fun getSensorAccess(
        deviceNumber: Int,
        isCombinedSensor: Boolean,
        resultReceivedCallback: (result: RequestAccessResult) -> Unit
    ) {
        AntPlusBikeSpeedDistancePcc.requestAccess(
            context,
            deviceNumber,
            SEARCH_PROXIMITY_THRESHOLD,
            isCombinedSensor,
            { result, resultCode, _ ->
                handleBikeSpeedAccessResult(result, resultCode, resultReceivedCallback)
            },
            { state ->
                if (state == DeviceState.DEAD) {
                    _speedDistance = null
                }
            }
        )
    }

    /**
     * Subscribe to all the heart rate events, connecting them to display
     * their data.
     */
    fun subscribe() {
        _speedDistance?.let {
            it.subscribeCalculatedSpeedEvent(
                object : CalculatedSpeedReceiver(WHEEL_CIRCUMFERENCE) {
                    override fun onNewCalculatedSpeed(
                        estTimestamp: Long,
                        eventFlags: EnumSet<EventFlag>,
                        speed: BigDecimal
                    ) {
                        Handler(Looper.getMainLooper()).post {
                            onSpeedReceiveListener?.invoke(
                                speed.setScale(1, BigDecimal.ROUND_HALF_DOWN)
                            )
                        }
                    }
                })

            it.subscribeCalculatedAccumulatedDistanceEvent(
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

            it.subscribeRawSpeedAndDistanceDataEvent { _, _, speed, distance ->
                Handler(Looper.getMainLooper()).post {
                    onSpeedReceiveListener?.invoke(speed)
                    onDistanceReceiveListener?.invoke(distance.toBigDecimal())
                }
            }
        }

        _cadence?.let {
            it.subscribeCalculatedCadenceEvent { _, _, cadence ->
                Handler(Looper.getMainLooper()).post {
                    onCadenceReceiveListener?.invoke(cadence)
                }
            }
        }
    }

    fun setOnCadenceReceiveListener(listener: (cadence: BigDecimal) -> Unit) {
        onCadenceReceiveListener = listener
    }

    fun setOnDistanceReceiveListener(listener: (distance: BigDecimal) -> Unit) {
        onDistanceReceiveListener = listener
    }

    fun setOnSpeedReceiveListener(listener: (speed: BigDecimal) -> Unit) {
        onSpeedReceiveListener = listener
    }

    fun clear(isCombinedSensor: Boolean) {
        _speedDistance = null
        if (isCombinedSensor) {
            _cadence = null
        }
    }

    private fun handleBikeSpeedAccessResult(
        result: AntPlusBikeSpeedDistancePcc?,
        resultCode: RequestAccessResult,
        resultReceivedCallback: (result: RequestAccessResult) -> Unit
    ) {
        if (resultCode == RequestAccessResult.SUCCESS) {
            _speedDistance = result
        }
        if (_speedDistance?.isSpeedAndCadenceCombinedSensor == true) {
            getCombinedSensor(resultReceivedCallback)
        } else {
            resultReceivedCallback.invoke(resultCode)
        }
    }

    private fun getCombinedSensor(resultReceivedCallback: (result: RequestAccessResult) -> Unit) {
        AntPlusBikeCadencePcc.requestAccess(
            context,
            _speedDistance?.antDeviceNumber.orZero(),
            SEARCH_PROXIMITY_THRESHOLD,
            true,
            /* IPluginAccessResultReceiver<AntPlusBikeCadencePcc> :
                Handle the result, connecting to events on success or reporting failure to user. */
            { result, resultCode, _ ->
                handleBikeCadenceAccessResult(result, resultCode, resultReceivedCallback)
                if (resultCode == RequestAccessResult.SUCCESS) {
                    _cadence = result
                }
                resultReceivedCallback.invoke(resultCode)
            },
            { state ->
                if (state == DeviceState.DEAD) {
                    _cadence = null
                }
            }
        )
    }

    private fun handleBikeCadenceAccessResult(
        result: AntPlusBikeCadencePcc?,
        resultCode: RequestAccessResult,
        resultReceivedCallback: (result: RequestAccessResult) -> Unit
    ) {
        if (resultCode == RequestAccessResult.SUCCESS) {
            _cadence = result
        }
        resultReceivedCallback.invoke(resultCode)
    }

    companion object {
        private const val SEARCH_PROXIMITY_THRESHOLD = 0
        private val WHEEL_CIRCUMFERENCE = BigDecimal(2.095) // an average 700cx23mm road tire
    }
}
