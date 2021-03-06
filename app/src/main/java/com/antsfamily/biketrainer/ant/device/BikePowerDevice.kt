package com.antsfamily.biketrainer.ant.device

import android.content.Context
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import dagger.hilt.android.qualifiers.ApplicationContext
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BikePowerDevice @Inject constructor(@ApplicationContext private val context: Context) {

    private var _powerMeter: AntPlusBikePowerPcc? = null

    private var _power: BigDecimal? = null
    val power: BigDecimal?
        get() = _power

    fun getAccess(
        deviceNumber: Int,
        resultReceivedCallback: (result: RequestAccessResult) -> Unit
    ) {
        AntPlusBikePowerPcc.requestAccess(
            context,
            deviceNumber,
            SEARCH_PROXIMITY_THRESHOLD,
            /* Handle the result, connecting to events on success or reporting failure to user. */
            { result, resultCode, _ ->
                if (resultCode == RequestAccessResult.SUCCESS) {
                    _powerMeter = result
                }
                resultReceivedCallback(resultCode)
            },
            { state ->
                if (state == DeviceState.DEAD) {
                    _powerMeter = null
                }
            }
        )
    }

    /**
     * Subscribe to all the heart rate events, connecting them to display their data.
     */
    fun subscribe() {
        _powerMeter?.let {
            it.subscribeCalculatedPowerEvent { _, _, _, power ->
                _power = power
            }
        }
    }

    fun clear() {
        _powerMeter = null
    }

    companion object {
        private const val SEARCH_PROXIMITY_THRESHOLD = 0
    }
}
