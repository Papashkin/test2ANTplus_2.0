package com.antsfamily.biketrainer.ant.device

import android.content.Context
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
import javax.inject.Singleton

@Singleton
class BikeSpeedDistanceDevice @Inject constructor(@ApplicationContext private val context: Context) {

    private var _speedDistanceSensor: AntPlusBikeSpeedDistancePcc? = null
    private var _cadenceSensor: AntPlusBikeCadencePcc? = null

    private var _cadence: BigDecimal? = null
    val cadence: BigDecimal?
        get() = _cadence
    private var _distance: BigDecimal? = null
    val distance: BigDecimal?
        get() = _distance
    private var _speed: BigDecimal? = null
    val speed: BigDecimal?
        get() = _speed

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
                    _speedDistanceSensor = null
                }
            }
        )
    }

    /**
     * Subscribe to all the heart rate events, connecting them to display
     * their data.
     */
    fun subscribe() {
        _speedDistanceSensor?.let {
            it.subscribeCalculatedSpeedEvent(
                object : CalculatedSpeedReceiver(WHEEL_CIRCUMFERENCE) {
                    override fun onNewCalculatedSpeed(
                        estTimestamp: Long,
                        eventFlags: EnumSet<EventFlag>,
                        speed: BigDecimal
                    ) {
                        _speed = speed
                    }
                })

            it.subscribeCalculatedAccumulatedDistanceEvent(
                object : CalculatedAccumulatedDistanceReceiver(WHEEL_CIRCUMFERENCE) {
                    override fun onNewCalculatedAccumulatedDistance(
                        estTimestamp: Long,
                        eventFlags: EnumSet<EventFlag>,
                        distance: BigDecimal
                    ) {
                        _distance = distance
                    }
                })

//            it.subscribeRawSpeedAndDistanceDataEvent { _, _, speed, distance ->
//                Handler(Looper.getMainLooper()).post {
//                    onSpeedReceiveListener?.invoke(speed)
//                    onDistanceReceiveListener?.invoke(distance.toBigDecimal())
//                }
//            }
        }

        _cadenceSensor?.let {
            it.subscribeCalculatedCadenceEvent { _, _, cadence ->
                _cadence = cadence
            }
        }
    }

    fun clear(isCombinedSensor: Boolean) {
        _speedDistanceSensor?.releaseAccess()
        _speedDistanceSensor = null
        if (isCombinedSensor) {
            _cadenceSensor?.releaseAccess()
            _cadenceSensor = null
        }
    }

    private fun handleBikeSpeedAccessResult(
        result: AntPlusBikeSpeedDistancePcc?,
        resultCode: RequestAccessResult,
        resultReceivedCallback: (result: RequestAccessResult) -> Unit
    ) {
        if (resultCode == RequestAccessResult.SUCCESS) {
            _speedDistanceSensor = result
        }
        if (_speedDistanceSensor?.isSpeedAndCadenceCombinedSensor == true) {
            getCombinedSensor(resultReceivedCallback)
        } else {
            resultReceivedCallback.invoke(resultCode)
        }
    }

    private fun getCombinedSensor(resultReceivedCallback: (result: RequestAccessResult) -> Unit) {
        AntPlusBikeCadencePcc.requestAccess(
            context,
            _speedDistanceSensor?.antDeviceNumber.orZero(),
            SEARCH_PROXIMITY_THRESHOLD,
            true,
            /* IPluginAccessResultReceiver<AntPlusBikeCadencePcc> :
                Handle the result, connecting to events on success or reporting failure to user. */
            { result, resultCode, _ ->
                handleBikeCadenceAccessResult(result, resultCode, resultReceivedCallback)
                if (resultCode == RequestAccessResult.SUCCESS) {
                    _cadenceSensor = result
                }
                resultReceivedCallback.invoke(resultCode)
            },
            { state ->
                if (state == DeviceState.DEAD) {
                    _cadenceSensor = null
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
            _cadenceSensor = result
        }
        resultReceivedCallback.invoke(resultCode)
    }

    companion object {
        private const val SEARCH_PROXIMITY_THRESHOLD = 0
        private val WHEEL_CIRCUMFERENCE = BigDecimal(2.095) // an average 700cx23mm road tire (in meters)
    }
}
