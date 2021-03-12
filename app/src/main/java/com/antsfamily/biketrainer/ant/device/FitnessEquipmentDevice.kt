package com.antsfamily.biketrainer.ant.device

import android.content.Context
import com.antsfamily.biketrainer.util.orFalse
import com.antsfamily.biketrainer.util.orZero
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pcc.defines.RequestStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FitnessEquipmentDevice @Inject constructor(@ApplicationContext private val context: Context) {

    private var _fitnessEquipment: AntPlusFitnessEquipmentPcc? = null
    private var _type: AntPlusFitnessEquipmentPcc.EquipmentType? = null
    private var subscriptionsDone = false

    private var _cadence: BigDecimal? = null
    val cadence: BigDecimal?
        get() = _cadence
    private var _distance: BigDecimal? = null
    val distance: BigDecimal?
        get() = _distance
    private var _speed: BigDecimal? = null
    val speed: BigDecimal?
        get() = _speed
    private var _power: BigDecimal? = null
    val power: BigDecimal?
        get() = _power

    fun getAccess(
        deviceNumber: Int,
        resultReceivedCallback: (result: RequestAccessResult) -> Unit
    ) {
        AntPlusFitnessEquipmentPcc.requestNewOpenAccess(
            context,
            deviceNumber,
            SEARCH_PROXIMITY_THRESHOLD,
            { result, resultCode, _ ->
                if (resultCode == RequestAccessResult.SUCCESS) {
                    _fitnessEquipment = result
                }
                resultReceivedCallback(resultCode)
            }, { state ->
                if (state == DeviceState.DEAD) {
                    _fitnessEquipment = null
                }
            }, { _, _, type, _ ->
                _type = type
            }
        )
    }

    fun subscribe(errorCallback: (message: String) -> Unit) {
        when (_type) {
            AntPlusFitnessEquipmentPcc.EquipmentType.BIKE -> {
                if (!subscriptionsDone) {
                    _fitnessEquipment?.bikeMethods?.let {
                        it.subscribeBikeDataEvent { _, _, instantaneousCadence, instantaneousPower ->
                            _power = instantaneousPower.toBigDecimal()
                            _cadence = instantaneousCadence.toBigDecimal()
                        }
                    }
                }
                subscriptionsDone = true
            }

            AntPlusFitnessEquipmentPcc.EquipmentType.TRAINER -> {
                if (!subscriptionsDone) {
                    _fitnessEquipment?.trainerMethods?.let {
                        it.subscribeCalculatedTrainerPowerEvent { _, _, _, power ->
                            _power = power
                        }

                        it.subscribeCalculatedTrainerSpeedEvent(object :
                            AntPlusFitnessEquipmentPcc.CalculatedTrainerSpeedReceiver(
                                WHEEL_DIAMETER
                            ) {
                            override fun onNewCalculatedTrainerSpeed(
                                timestamp: Long,
                                eventFlags: EnumSet<EventFlag>?,
                                source: AntPlusFitnessEquipmentPcc.TrainerDataSource?,
                                speed: BigDecimal?
                            ) {
                                _speed = speed.orZero()
                            }
                        })

                        it.subscribeCalculatedTrainerDistanceEvent(object :
                            AntPlusFitnessEquipmentPcc.CalculatedTrainerDistanceReceiver(
                                WHEEL_DIAMETER
                            ) {
                            override fun onNewCalculatedTrainerDistance(
                                timestamp: Long,
                                flags: EnumSet<EventFlag>?,
                                source: AntPlusFitnessEquipmentPcc.TrainerDataSource?,
                                distance: BigDecimal?
                            ) {
                                _distance = distance.orZero()
                            }
                        })

                        /*
                         _, _, count, instantCadence, instantPower, accumulatedPower
                         */
                        it.subscribeRawTrainerDataEvent { _, _, _, _, _, _ ->
                            // no-op
                        }

                        /*
                        _, _, _, wheelTicks, wheelPeriod, torque
                         */
                        it.subscribeRawTrainerTorqueDataEvent { _, _, _, _, _, _ ->
                            // no-op
                        }
                    }
                }
                subscriptionsDone = true
            }
            else -> errorCallback("Invalid or Unrecognized type: $_type")
        }
    }

    fun clear() {
        _fitnessEquipment?.trainerMethods?.requestSetTargetPower(BigDecimal.ZERO) {}
        _fitnessEquipment?.releaseAccess()
        _fitnessEquipment = null
    }

    companion object {
        private const val SEARCH_PROXIMITY_THRESHOLD = 0
        private val WHEEL_DIAMETER = BigDecimal("0.70") //0.70m wheel diameter
    }

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

    /**
     * @param basicResistance = BigDecimal("4.5"), as example
     */
    fun setBasicResistance(
        basicResistance: BigDecimal,
        requestCallback: (status: RequestStatus) -> Unit,
        statusCallback: (isSuccess: Boolean) -> Unit
    ) {
        // TODO The capabilities should be requested before attempting to send new control settings to determine which modes are supported.
        val submitted =
            _fitnessEquipment?.trainerMethods?.requestSetBasicResistance(basicResistance) {
                requestCallback(it)
            }
        statusCallback(submitted.orFalse())
    }

    /**
     * @param targetPower: the target power for fitness equipment operating in target power mode.
     *                     Units: W. Valid range: 0W - 1000W. Resolution: 0.25W
     */
    fun setTargetPower(
        targetPower: BigDecimal,
        requestCallback: (status: RequestStatus) -> Unit,
        statusCallback: (isSuccess: Boolean) -> Unit
    ) {
        val isSuccess = _fitnessEquipment?.trainerMethods?.requestSetTargetPower(targetPower) {
            requestCallback(it)
        }
        statusCallback(isSuccess.orFalse())
    }
}
