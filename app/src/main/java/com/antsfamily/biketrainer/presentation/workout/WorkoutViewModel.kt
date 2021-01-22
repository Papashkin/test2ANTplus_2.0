package com.antsfamily.biketrainer.presentation.workout

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.antsfamily.biketrainer.ant.device.BikeCadenceDevice
import com.antsfamily.biketrainer.ant.device.BikeSpeedDistanceDevice
import com.antsfamily.biketrainer.ant.device.FitnessEquipmentDevice
import com.antsfamily.biketrainer.ant.device.HeartRateDevice
import com.antsfamily.biketrainer.data.models.TrainingParams
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class WorkoutViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : StatefulViewModel<WorkoutViewModel.State>(State()) {

    data class State(
        val trainingParams: TrainingParams = TrainingParams(),
        val trainingData: TrainingParams? = null
    )

    fun onBackClick() {
        closeAccess()
        clear()
        clearLiveDataValues()
    }

    private fun clear() {
        trainingData.postValue(null)
        antPlusDialog.postValue(null)
    }

    private fun closeAccess() {
        handleHeartRate?.close()
        handleCadence?.close()
        handleSpeedDistance?.close()
        handleEquipment?.close()
    }

    private val timer: Timer = Timer()
    private val task = object : TimerTask() {
        override fun run() {
            displayTrainingData()
        }
    }

    fun onStartClick() = launch {
// TODO: 15.01.2021  
    }

    init {
        timer.schedule(task, 2_000, 3_000)
    }

//    private val trainingParams = TrainingParams()
    var trainingData: MutableLiveData<TrainingParams?> = MutableLiveData(null)
    fun displayTrainingData() = launch {
//        trainingData.postValue(trainingParams)
    }

    fun onStopClick() {
        timer.cancel()
    }

    fun setDevices(devices: ArrayList<MultiDeviceSearchResult>) {
        devices.forEach {
            if (it.antDeviceType == DeviceType.HEARTRATE && !isHRMInWork) {
                setHeartRateAccess(it)
            }
            if (it.antDeviceType == DeviceType.BIKE_CADENCE && !isCadenceInWork) {
                setCadenceAccess(it)
            }
            if (it.antDeviceType == DeviceType.BIKE_SPD && !isSpeedInWork) {
                setSpeedAccess(it)
            }
            if (it.antDeviceType == DeviceType.FITNESS_EQUIPMENT && !isPowerInWork) {
                setFitnessEquipmentAccess(it)
            }
        }
    }

    private lateinit var heartRateCensor: HeartRateDevice
    private var handleHeartRate: PccReleaseHandle<AntPlusHeartRatePcc>? = null
    private var isHRMInWork = false
    private fun setHeartRateAccess(device: MultiDeviceSearchResult) = launch {
        heartRateCensor = HeartRateDevice(
            getHearRate = { heartRate ->
                getHeartRateValue(heartRate)
            },
            showToast = { text ->
                showToast(text)
            },
            setDependencies = { name, packageName ->
                showDialog(name, packageName)
            })
        handleHeartRate = AntPlusHeartRatePcc.requestAccess(
            context,
            device.antDeviceNumber,
            0,
            heartRateCensor.baseIPluginAccessResultReceiver,
            heartRateCensor.baseDeviceChangeReceiver
        )
        isHRMInWork = true
    }

    private lateinit var cadenceCensor: BikeCadenceDevice
    private var handleCadence: PccReleaseHandle<AntPlusBikeCadencePcc>? = null
    private var isCadenceInWork = false
    private fun setCadenceAccess(device: MultiDeviceSearchResult) = launch {
        cadenceCensor = BikeCadenceDevice(
            context,
            getCadence = { cadence ->
                getCadenceValue(cadence)
            },
            getSpeed = { speed ->
                getSpeedValue(speed)
            },
            showToast = { text ->
                showToast(text)
            },
            setDependencies = { name, packageName ->
                showDialog(name, packageName)
            })

        handleCadence = AntPlusBikeCadencePcc.requestAccess(
            context,
            device.antDeviceNumber,
            0,
            true,
            cadenceCensor.mResultReceiver,
            cadenceCensor.mDeviceStateChangeReceiver
        )
        isCadenceInWork = true
    }

    private lateinit var speedDistanceCensor: BikeSpeedDistanceDevice
    private var handleSpeedDistance: PccReleaseHandle<AntPlusBikeSpeedDistancePcc>? = null
    private var isSpeedInWork = false
    private fun setSpeedAccess(device: MultiDeviceSearchResult) = launch {
        speedDistanceCensor = BikeSpeedDistanceDevice(
            context,
            getSpeed = { speed ->
                if (!isCadenceInWork) getSpeedValue(speed)
            },
            getDistance = { distance ->
                getDistanceValue(distance)
            },
            getCadence = { cadence ->
                if (!isCadenceInWork) getCadenceValue(cadence)
            },
            showToast = { text ->
                showToast(text)
            },
            setDependencies = { name, packageName ->
                showDialog(name, packageName)
            })

        handleSpeedDistance = AntPlusBikeSpeedDistancePcc.requestAccess(
            context,
            device.antDeviceNumber,
            0,
            true,
            speedDistanceCensor.mResultReceiver,
            speedDistanceCensor.mDeviceStateChangeReceiver
        )
        isSpeedInWork = true
    }

    private lateinit var fitnessEquipmentCensor: FitnessEquipmentDevice
    private var handleEquipment: PccReleaseHandle<AntPlusFitnessEquipmentPcc>? = null
    private var isPowerInWork = false
    private fun setFitnessEquipmentAccess(device: MultiDeviceSearchResult) = launch {
        fitnessEquipmentCensor = FitnessEquipmentDevice(
            showToast = { text ->
                showToast(text)
            },
            setDependencies = { name, packageName ->
                showDialog(name, packageName)
            },
            getPower = { power ->
                getPowerValue(power)
            },
            getCadence = { cadence ->
                if (!isCadenceInWork or !isSpeedInWork) getCadenceValue(cadence)
            },
            getSpeed = { speed ->
                if (!isCadenceInWork or !isSpeedInWork) getSpeedValue(speed)
            },
            getDistance = { distance ->
                if (!isSpeedInWork) getDistanceValue(distance)
            })

        handleEquipment = AntPlusFitnessEquipmentPcc.requestNewOpenAccess(
            context,
            device.antDeviceNumber,
            0,
            fitnessEquipmentCensor.mPluginAccessResultReceiver,
            fitnessEquipmentCensor.mDeviceStateChangeReceiver,
            fitnessEquipmentCensor.mFitnessEquipmentStateReceiver
        )
        isPowerInWork = true
    }

    private fun getHeartRateValue(hr: String) {
//        trainingParams.heartRate = hr
    }

    private fun getCadenceValue(cadence: String) {
//        trainingParams.cadence = cadence
    }

    private fun getSpeedValue(speed: String) {
//        trainingParams.speed = speed
    }

    private fun getDistanceValue(distance: String) {
//        trainingParams.distance = distance
    }

    private fun getPowerValue(power: String) {
//        trainingParams.power = power
    }

    var antPlusDialog: MutableLiveData<Pair<String, String>?> = MutableLiveData(null)
    private fun showDialog(name: String, packageName: String) {
        antPlusDialog.postValue(Pair(name, packageName))
    }

    private var timeDescriptors = arrayListOf<Float>()
    private var programEntries = arrayListOf<BarEntry>()
    fun setProgram(program: String) {
        programEntries.clear()
        timeDescriptors.clear()
        programEntries = decompileProgram(program)
        updateChart()
    }

    private fun decompileProgram(programLegend: String): ArrayList<BarEntry> {
        val entries = arrayListOf<BarEntry>()
        var count = 0

        programLegend.split("|").forEach { firstDecompiler ->
            if (firstDecompiler.isNotEmpty()) {
                val timeAndPower = firstDecompiler.split("*")
                timeDescriptors.add(timeAndPower.first().toFloat())
                entries.add(BarEntry(count.toFloat(), timeAndPower.last().toFloat()))
                count += 1
            }
        }
        return entries
    }

    var chartData: MutableLiveData<Pair<BarData, ArrayList<Float>>?> = MutableLiveData(null)
    private fun updateChart() {
        val program = BarDataSet(programEntries, "")
        program.barBorderWidth = 0f
        chartData.postValue(
            Pair(BarData(program), timeDescriptors)
        )
    }
}
