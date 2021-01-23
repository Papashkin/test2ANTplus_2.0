package com.antsfamily.biketrainer.presentation.workout

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antsfamily.biketrainer.ant.device.BikeCadenceDevice
import com.antsfamily.biketrainer.ant.device.BikeSpeedDistanceDevice
import com.antsfamily.biketrainer.ant.device.FitnessEquipmentDevice
import com.antsfamily.biketrainer.ant.device.HeartRateDevice
import com.antsfamily.biketrainer.data.models.WorkoutCensorValues
import com.antsfamily.biketrainer.presentation.Event
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
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class WorkoutViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cadenceCensor: BikeCadenceDevice,
    private val heartRateCensor: HeartRateDevice,
    private val speedDistanceCensor: BikeSpeedDistanceDevice,
    private val fitnessEquipmentCensor: FitnessEquipmentDevice
) : StatefulViewModel<WorkoutViewModel.State>(State()) {

    data class State(
        val censorsData: WorkoutCensorValues = WorkoutCensorValues(),
        var chartData: Pair<BarData, ArrayList<Float>>? = null
    )

    private var _showDeviceDialogEvent = MutableLiveData<Event<Pair<String, String>?>>()
    val showDeviceDialogEvent: LiveData<Event<Pair<String, String>?>>
        get() = _showDeviceDialogEvent

    private val timer: Timer = Timer()
    private val task = object : TimerTask() {
        override fun run() {
            displayTrainingData()
        }
    }
    private var handleHeartRate: PccReleaseHandle<AntPlusHeartRatePcc>? = null
    private var handleCadence: PccReleaseHandle<AntPlusBikeCadencePcc>? = null
    private var handleSpeedDistance: PccReleaseHandle<AntPlusBikeSpeedDistancePcc>? = null
    private var handleEquipment: PccReleaseHandle<AntPlusFitnessEquipmentPcc>? = null
    private var isHRMInWork = false
    private var isCadenceInWork = false
    private var isSpeedInWork = false
    private var isPowerInWork = false
    private var timeDescriptors = arrayListOf<Float>()
    private var programEntries = arrayListOf<BarEntry>()
    private var workoutValues: WorkoutCensorValues = WorkoutCensorValues()

    init {
        setupCadenceCensor()
        setupHeartRateCensor()
        setupSpeedDistanceCensor()
        setupFitnessEquipmentCensor()
        setupTimer()
    }

    fun onStop() {
        closeAccess()
    }

    fun onStopClick() {
        timer.cancel()
    }

    fun onBackClick() {
        // TODO: 23.01.2021
    }

    fun onStartClick() = launch {
        // TODO: 15.01.2021
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

    private fun setHeartRateAccess(device: MultiDeviceSearchResult) = launch {
        handleHeartRate = AntPlusHeartRatePcc.requestAccess(
            context,
            device.antDeviceNumber,
            0,
            heartRateCensor.baseIPluginAccessResultReceiver,
            heartRateCensor.baseDeviceChangeReceiver
        )
        isHRMInWork = true
    }

    private fun setCadenceAccess(device: MultiDeviceSearchResult) = launch {
        handleCadence = AntPlusBikeCadencePcc.requestAccess(
            context,
            device.antDeviceNumber,
            0,
            true,
            cadenceCensor.resultReceiver,
            cadenceCensor.deviceStateChangeReceiver
        )
        isCadenceInWork = true
    }

    private fun setSpeedAccess(device: MultiDeviceSearchResult) = launch {
        handleSpeedDistance = AntPlusBikeSpeedDistancePcc.requestAccess(
            context,
            device.antDeviceNumber,
            0,
            true,
            speedDistanceCensor.resultReceiver,
            speedDistanceCensor.deviceStateChangeReceiver
        )
        isSpeedInWork = true
    }

    private fun setFitnessEquipmentAccess(device: MultiDeviceSearchResult) = launch {
        handleEquipment = AntPlusFitnessEquipmentPcc.requestNewOpenAccess(
            context,
            device.antDeviceNumber,
            0,
            fitnessEquipmentCensor.pluginAccessResultReceiver,
            fitnessEquipmentCensor.deviceStateChangeReceiver,
            fitnessEquipmentCensor.fitnessEquipmentStateReceiver
        )
        isPowerInWork = true
    }

    private fun closeAccess() {
        handleHeartRate?.close()
        handleCadence?.close()
        handleSpeedDistance?.close()
        handleEquipment?.close()
    }

    private fun showDialog(name: String, packageName: String) {
        _showDeviceDialogEvent.postValue(Event(Pair(name, packageName)))
    }

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

    private fun updateChart() {
        val program = BarDataSet(programEntries, "")
        program.barBorderWidth = 0f
        changeState { it.copy(chartData = Pair(BarData(program), timeDescriptors)) }
    }

    private fun setupCadenceCensor() {
        cadenceCensor.apply {
            setOnCadenceReceiveListener { cadence -> getCadenceValue(cadence) }
            setOnSpeedReceiveListener { speed -> getSpeedValue(speed) }
            setOnToastShowListener { showToast(it) }
            setOnDependenciesSetListener { name, packageName -> showDialog(name, packageName) }
        }
    }

    private fun setupHeartRateCensor() {
        heartRateCensor.apply {
            setOnHeartRateReceiveListener { heartRate -> getHeartRateValue(heartRate) }
            setOnToastShowListener { showToast(it) }
            setOnDependenciesSetListener { name, packageName -> showDialog(name, packageName) }
        }
    }

    private fun setupSpeedDistanceCensor() {
        speedDistanceCensor.apply {
            setOnCadenceReceiveListener { cadence -> if (!isCadenceInWork) getCadenceValue(cadence) }
            setOnDistanceReceiveListener { distance -> getDistanceValue(distance) }
            setOnSpeedReceiveListener { speed -> if (!isCadenceInWork) getSpeedValue(speed) }
            setOnToastShowListener { showToast(it) }
            setOnDependenciesSetListener { name, packageName -> showDialog(name, packageName) }
        }
    }

    private fun setupFitnessEquipmentCensor() {
        fitnessEquipmentCensor.apply {
            setOnCadenceReceiveListener { if (!isCadenceInWork or !isSpeedInWork) getCadenceValue(it) }
            setOnDistanceReceiveListener { if (!isSpeedInWork) getDistanceValue(it) }
            setOnPowerReceiveListener { getPowerValue(it) }
            setOnSpeedReceiveListener { if (!isCadenceInWork or !isSpeedInWork) getSpeedValue(it) }
            setOnShowToastListener { showToast(it) }
            setOnSetDependenciesListener { name, packageName -> showDialog(name, packageName) }
        }
    }

    private fun setupTimer() {
        timer.schedule(task, 2_000, 3_000)
    }

    private fun displayTrainingData() = launch {
        changeState { it.copy(censorsData = workoutValues) }
    }

    private fun getHeartRateValue(rate: Int) {
        workoutValues.heartRate = rate
    }

    private fun getCadenceValue(cadence: BigDecimal) {
        workoutValues.cadence = cadence
    }

    private fun getSpeedValue(speed: BigDecimal) {
        workoutValues.speed = speed
    }

    private fun getDistanceValue(distance: BigDecimal) {
        workoutValues.distance = distance
    }

    private fun getPowerValue(power: BigDecimal) {
        workoutValues.power = power
    }
}
