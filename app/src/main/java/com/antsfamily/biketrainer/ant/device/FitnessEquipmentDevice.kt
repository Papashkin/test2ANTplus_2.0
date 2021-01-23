package com.antsfamily.biketrainer.ant.device

import android.os.Handler
import android.os.Looper
import com.antsfamily.biketrainer.util.orZero
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pcc.defines.RequestStatus
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class FitnessEquipmentDevice @Inject constructor() {

    private var fePcc: AntPlusFitnessEquipmentPcc? = null
    private var subscriptionsDone = false
    private var onShowToastListener: ((text: String) -> Unit)? = null
    private var onSetDependenciesListener: ((name: String, packageName: String) -> Unit)? = null
    private var onPowerReceiveListener: ((power: BigDecimal) -> Unit)? = null
    private var onCadenceReceiveListener: ((cadence: BigDecimal) -> Unit)? = null
    private var onSpeedReceiveListener: ((speed: BigDecimal) -> Unit)? = null
    private var onDistanceReceiveListener: ((distance: BigDecimal) -> Unit)? = null

//    private var settings: Settings
//    private var files: ArrayList<FitFiles>

//    b = getIntent().getExtras();
//    if(b != null)
//    {
//        String name = b.getString(Dialog_ConfigSettings.SETTINGS_NAME);
//        Settings.Gender gender = Settings.Gender.FEMALE;
//        if(b.getBoolean(Dialog_ConfigSettings.SETTINGS_GENDER))
//            gender = Settings.Gender.MALE;
//        short age = b.getShort(Dialog_ConfigSettings.SETTINGS_AGE);
//        float height = b.getFloat(Dialog_ConfigSettings.SETTINGS_HEIGHT);
//        float weight = b.getFloat(Dialog_ConfigSettings.SETTINGS_WEIGHT);
//
//        settings = new Settings(name, gender, age, height, weight);
//
//        if(b.getBoolean(Dialog_ConfigSettings.INCLUDE_WORKOUT))
//        {
//            try
//            {
//                // Make available a FIT workout file to the fitness equipment
//                // The sample file included with this project was obtained from the FIT SDK, v7.10
//                InputStream is = getAssets().open("WorkoutRepeatSteps.fit");
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                int next;
//                while((next = is.read()) != -1)
//                    bos.write(next);
//                bos.flush();
//                is.close();
//                FitFile workoutFile = new FitFile(bos.toByteArray());
//                workoutFile.setFileType((short) 5);  // Make sure to set the File Type, so this information is also available to the fitness equipment
//                // Refer to the FIT SDK for more details on FIT file types
//                files = new FitFile[] { workoutFile};
//            }
//            catch (IOException e)
//            {
//                files = null;
//            }
//        }
//    }

    private val requestFinishedReceiver = AntPlusCommonPcc.IRequestFinishedReceiver { status ->
        Handler(Looper.getMainLooper()).post {
            when (status) {
                RequestStatus.SUCCESS -> showToast("Request Successfully Sent")
                RequestStatus.FAIL_PLUGINS_SERVICE_VERSION -> showToast("Plugin Service Upgrade Required?")
                else -> showToast("Request Failed to be Sent")
            }
        }
    }

    private val _pluginAccessResultReceiver =
        AntPluginPcc.IPluginAccessResultReceiver<AntPlusFitnessEquipmentPcc> { result, resultCode, _ ->
            when (resultCode) {
                RequestAccessResult.SUCCESS -> {
                    fePcc = result
                    fePcc?.let {
                        subscribeToEvents()
                    }
                }
                RequestAccessResult.CHANNEL_NOT_AVAILABLE -> showToast("Channel Not Available")
                RequestAccessResult.ADAPTER_NOT_DETECTED -> {
                    showToast("ANT Adapter Not Available. Built-in ANT hardware or external adapter required.")
                }
                RequestAccessResult.BAD_PARAMS -> showToast("Bad request parameters.")
                RequestAccessResult.OTHER_FAILURE -> {
                    showToast("RequestAccess failed. See logcat for details.")
                }
                RequestAccessResult.DEPENDENCY_NOT_INSTALLED -> {
                    onSetDependenciesListener?.invoke(
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

    //Receives state changes and shows it on the status display line
    private val _deviceStateChangeReceiver = AntPluginPcc.IDeviceStateChangeReceiver {
        Handler(Looper.getMainLooper()).post {
            /**
             * Note: The state here is the state of our data receiver channel which is closed
             * until the ANTFS session is established
             */
        }
    }

    private val _fitnessEquipmentStateReceiver =
        AntPlusFitnessEquipmentPcc.IFitnessEquipmentStateReceiver { _, _, equipmentType, equipmentState ->
            Handler(Looper.getMainLooper()).post {
                val wheelDiameter = BigDecimal("0.70") //0.70m wheel diameter

                when (equipmentType) {
                    AntPlusFitnessEquipmentPcc.EquipmentType.BIKE -> {
                        if (!subscriptionsDone) {
                            fePcc?.bikeMethods?.let {
                                it.subscribeBikeDataEvent { _, _, instantaneousCadence, instantaneousPower ->
                                    Handler(Looper.getMainLooper()).post {
                                        onPowerReceiveListener?.invoke(instantaneousPower.toBigDecimal())
                                        onCadenceReceiveListener?.invoke(instantaneousCadence.toBigDecimal())
                                    }
                                }
                            }
                        }
                        subscriptionsDone = true
                    }

                    AntPlusFitnessEquipmentPcc.EquipmentType.TRAINER -> {
                        if (!subscriptionsDone) {
                            fePcc?.trainerMethods?.let {
                                it.subscribeCalculatedTrainerPowerEvent { _, _, _, power ->
                                    onPowerReceiveListener?.invoke(power)
                                }

                                it.subscribeCalculatedTrainerSpeedEvent(object :
                                    AntPlusFitnessEquipmentPcc.CalculatedTrainerSpeedReceiver(
                                        wheelDiameter
                                    ) {
                                    override fun onNewCalculatedTrainerSpeed(
                                        timestamp: Long,
                                        eventFlags: EnumSet<EventFlag>?,
                                        source: AntPlusFitnessEquipmentPcc.TrainerDataSource?,
                                        speed: BigDecimal?
                                    ) {
                                        Handler(Looper.getMainLooper()).post {
                                            onSpeedReceiveListener?.invoke(speed.orZero())
                                        }
                                    }
                                })

                                it.subscribeCalculatedTrainerDistanceEvent(object :
                                    AntPlusFitnessEquipmentPcc.CalculatedTrainerDistanceReceiver(
                                        wheelDiameter
                                    ) {
                                    override fun onNewCalculatedTrainerDistance(
                                        timestamp: Long,
                                        flags: EnumSet<EventFlag>?,
                                        source: AntPlusFitnessEquipmentPcc.TrainerDataSource?,
                                        distance: BigDecimal?
                                    ) {
                                        Handler(Looper.getMainLooper()).post {
                                            onDistanceReceiveListener?.invoke(distance.orZero())
                                        }
                                    }
                                })

                                it.subscribeRawTrainerDataEvent { _, _, count, instantCadence, instantPower, accumulatedPower ->
                                    Handler(Looper.getMainLooper()).post {
                                        //                                        tv_estTimestamp.text = timestamp.toString()
//                                        textView_TrainerUpdateEventCount.text = count.toString()
//                                        textView_TrainerInstantaneousCadence.text = if (instantCadence == -1) {
//                                            "N/A"
//                                        } else {
//                                            "$instantCadence RPM"
//                                        }
//                                        textView_TrainerInstantaneousPower.text = if (instantPower == -1) {
//                                            "N/A"
//                                        } else {
//                                            "$instantPower W"
//                                        }
//                                        textView_TrainerAccumulatedPower.text = if (accumulatedPower == -1) {
//                                            "N/A"
//                                        } else {
//                                            "$accumulatedPower W"
//                                        }
                                    }
                                }

                                it.subscribeRawTrainerTorqueDataEvent { _, _, _, wheelTicks, wheelPeriod, torque ->
                                    Handler(Looper.getMainLooper()).post { }
                                }
                            }
                        }
                        subscriptionsDone = true
                    }

                    AntPlusFitnessEquipmentPcc.EquipmentType.UNKNOWN -> {
                    } //tv_feType.text = "UNKNOWN"
                    AntPlusFitnessEquipmentPcc.EquipmentType.UNRECOGNIZED -> {
                        showToast("UNRECOGNIZED type, PluginLib upgrade required?")
                    }
                    else -> {
                        showToast("INVALID: $equipmentType")
                    }
                }

                when (equipmentState) {
                    AntPlusFitnessEquipmentPcc.EquipmentState.UNRECOGNIZED -> {
                        showToast("Failed: UNRECOGNIZED. PluginLib Upgrade Required?")
                    }
                    else -> showToast("INVALID: $equipmentState")
                }
            }
        }

    val pluginAccessResultReceiver: AntPluginPcc.IPluginAccessResultReceiver<AntPlusFitnessEquipmentPcc>
        get() = _pluginAccessResultReceiver

    val deviceStateChangeReceiver: AntPluginPcc.IDeviceStateChangeReceiver
        get() = _deviceStateChangeReceiver

    val fitnessEquipmentStateReceiver: AntPlusFitnessEquipmentPcc.IFitnessEquipmentStateReceiver
        get() = _fitnessEquipmentStateReceiver


    fun setOnShowToastListener (listener: (text: String) -> Unit) {
        onShowToastListener = listener
    }
    fun setOnSetDependenciesListener (listener: (name: String, packageName: String) -> Unit) {
        onSetDependenciesListener = listener
    }
    fun setOnPowerReceiveListener (listener: (power: BigDecimal) -> Unit) {
        onPowerReceiveListener = listener
    }
    fun setOnCadenceReceiveListener (listener: (cadence: BigDecimal) -> Unit) {
        onCadenceReceiveListener = listener
    }
    fun setOnSpeedReceiveListener (listener: (speed: BigDecimal) -> Unit) {
        onSpeedReceiveListener = listener
    }
    fun setOnDistanceReceiveListener (listener: (distance: BigDecimal) -> Unit) {
        onDistanceReceiveListener = listener
    }

    /**
     * @param basicResistance = BigDecimal("4.5"), as example
     */
    fun setBasicResistance(basicResistance: BigDecimal) {
        // TODO The capabilities should be requested before attempting to send new control settings to determine which modes are supported.
        val submitted = fePcc?.trainerMethods?.requestSetBasicResistance(
            basicResistance,
            requestFinishedReceiver
        )
        submitted?.apply {
            showToast("Request Could not be Made")
        }
    }

    /**
     * @param targetPower: BigDecimal("42.25") is the same as 42.25%
     */
    fun setTargetPower(targetPower: BigDecimal) {
        // TODO The capabilities should be requested before attempting to send new control settings to determine which modes are supported.
        val submitted =
            fePcc?.trainerMethods?.requestSetTargetPower(targetPower, requestFinishedReceiver)
        submitted?.apply {
            showToast("Request Could not be Made")
        }
    }

    private fun subscribeToEvents() {
        fePcc?.subscribeGeneralFitnessEquipmentDataEvent { _, _, _, cumulativeDistance, instantaneousSpeed,
                                                           virtualInstantaneousSpeed, instantaneousHeartRate, heartRateDataSource ->
            Handler(Looper.getMainLooper()).post {
                //                tv_estTimestamp.text = estTimestamp.toString()
//
//                tv_time.text = if (elapsedTime == BigDecimal(-1)) {
//                    "Invalid"
//                } else {
//                    "$elapsedTime s"
//                }
//
//                tv_distance.text = if (cumulativeDistance == -1L) {
//                    "Invalid"
//                } else {
//                    "$cumulativeDistance m"
//                }
//
//                tv_speed.text = if (instantaneousSpeed == BigDecimal(-1)) {
//                    "Invalid"
//                } else {
//                    "$instantaneousSpeed m/s"
//                }
//
//                if (virtualInstantaneousSpeed) tv_speed.text = "${tv_speed.getText} (Virtual)"
//
//                tv_heartRate.text = if (instantaneousHeartRate == -1) {
//                    "Invalid"
//                } else {
//                    "$instantaneousHeartRate bpm"
//                }

                when (heartRateDataSource!!) {
                    AntPlusFitnessEquipmentPcc.HeartRateDataSource.ANTPLUS_HRM -> {

                    }
                    AntPlusFitnessEquipmentPcc.HeartRateDataSource.EM_5KHz -> {

                    }
                    AntPlusFitnessEquipmentPcc.HeartRateDataSource.HAND_CONTACT_SENSOR -> {

                    }
                    AntPlusFitnessEquipmentPcc.HeartRateDataSource.UNKNOWN -> {
//                        tv_heartRateSource.text = heartRateDataSource.toString()
                    }
                    AntPlusFitnessEquipmentPcc.HeartRateDataSource.UNRECOGNIZED -> {
                        showToast("Failed: UNRECOGNIZED. PluginLib Upgrade Required?")
                    }
                }
            }
        }

        fePcc?.subscribeLapOccuredEvent { _, _, _ ->
            Handler(Looper.getMainLooper()).post { }
        }

        fePcc?.subscribeGeneralSettingsEvent { _, _, _, inclinePercentage, resistanceLevel ->
            Handler(Looper.getMainLooper()).post {
                //                tv_estTimestamp.text = estTimestamp.toString()
//
//                tv_cycleLength.text = if (cycleLength == BigDecimal(-1)) {
//                    "Invalid"
//                } else {
//                    "$cycleLength m"
//                }
//                tv_inclinePercentage.text = if (inclinePercentage == BigDecimal(0x7FFF)) {
//                    "Invalid"
//                } else {
//                    "$inclinePercentage %"
//                }
//                //TODO If this is a Fitness Equipment Controls device, this represents the current set resistance level at 0.5% per unit from 0% to 100%
//                tv_resistanceLevel.text = if (resistanceLevel == -1) {
//                    "Invalid"
//                } else {
//                    resistanceLevel.toString()
//                }
            }
        }

        fePcc?.subscribeGeneralMetabolicDataEvent { _, _, instantaneousMetabolicEquivalents, instantaneousCaloricBurn, cumulativeCalories ->
            Handler(Looper.getMainLooper()).post {
                //                tv_estTimestamp.text = estTimestamp.toString()
//                tv_mets.text = if (instantaneousMetabolicEquivalents == BigDecimal(-1)) {
//                    "Invalid"
//                } else {
//                    "$instantaneousMetabolicEquivalents METs"
//                }
//                tv_caloricBurn.text = if (instantaneousCaloricBurn == BigDecimal(-1)) {
//                    "Invalid"
//                } else {
//                    "$instantaneousCaloricBurn kCal/h"
//                }
//                tv_calories.text = if (cumulativeCalories == -1L) {
//                    "Invalid"
//                } else {
//                    "$cumulativeCalories kCal"
//                }
            }
        }
    }

    private fun showToast(text: String) {
        onShowToastListener?.invoke(text)
    }
}
