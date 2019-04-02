package com.example.test2antplus.ui.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import com.example.test2antplus.MainApplication
import com.example.test2antplus.R
import com.example.test2antplus.ant.device.BikeCadenceDevice
import com.example.test2antplus.ant.device.BikeSpeedDistanceDevice
import com.example.test2antplus.ant.device.FitnessEquipmentDevice
import com.example.test2antplus.ant.device.HeartRateDevice
import com.example.test2antplus.presenter.WorkPresenter
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.fragment_work.*
import javax.inject.Inject

interface WorkInterface {
    fun setHeartRate(hr: String)
    fun setCadence(cadence: String)
    fun setSpeed(speed: String)
    fun setDistance(distance: String)
    fun setPower(power: String)
    fun onFabClick()
    fun showDialog(name: String, packageName: String)
    fun closeAccess()

    fun showAddButton()
    fun hideAddButton()
}

class WorkFragment : Fragment(), WorkInterface {
    companion object {
        const val DEVICES_LIST = "devices list"
        const val PROGRAM_NAME = "program name"
    }
    private lateinit var presenter: WorkPresenter
    private lateinit var heartRateCensor: HeartRateDevice
    private lateinit var cadenceCensor: BikeCadenceDevice
    private lateinit var speedDistanceCensor: BikeSpeedDistanceDevice
    private lateinit var fitnessEquipmentCensor: FitnessEquipmentDevice

    private var handleHeartRate: PccReleaseHandle<AntPlusHeartRatePcc>? = null
    private var handleCadence: PccReleaseHandle<AntPlusBikeCadencePcc>? = null
    private var handleSpeedDistance: PccReleaseHandle<AntPlusBikeSpeedDistancePcc>? = null
    private var handleEquipment: PccReleaseHandle<AntPlusFitnessEquipmentPcc>? = null
    private var devices: ArrayList<MultiDeviceSearchResult>? = null
    private var programName: String? = null
    private var isHRMInWork = false
    private var isCadenceInWork = false
    private var isSpeedInWork = false
    private var isPowerInWork = false

    @Inject lateinit var appContext: Context

//    private val receiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            val extra = intent?.getStringExtra(ACTION_WORK_SENDING)
//            when (extra) {
//                ARGS_PROGRAM -> {
//                    val programName = intent.getStringExtra(ARGS_PROGRAM)
//                    presenter.setProgram(programName)
//                }
//            }
//        }
//    }

    fun newInstance(devices: java.util.ArrayList<MultiDeviceSearchResult>, programName: String?): WorkFragment = WorkFragment().apply {
        this.arguments = Bundle().also {
            it.putParcelableArrayList(DEVICES_LIST, devices)
            it.putString(PROGRAM_NAME, programName)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MainApplication.graph.inject(this)
        return inflater.inflate(R.layout.fragment_work, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = WorkPresenter(this)

        this.arguments?.apply {
            devices = this.getParcelableArrayList(DEVICES_LIST)
            programName = this.getString(PROGRAM_NAME)
        }

        if (programName != null) {
            presenter.setProgram(programName!!)
        } else {
            presenter.setEmptyProgram()
        }

        devices?.forEach {
            if (it.antDeviceType == DeviceType.HEARTRATE && !isHRMInWork) {
                heartRateCensor = HeartRateDevice(
                    getHearRate = { heartRate ->
                        presenter.setHeartRate(heartRate)
                    },
                    showToast = { text ->
                        toast(text)
                    },
                    setDependencies = { name, packageName ->
                        presenter.showDialog(name, packageName)
                    })
                handleHeartRate = AntPlusHeartRatePcc.requestAccess(
                    appContext,
                    it.antDeviceNumber,
                    0,
                    heartRateCensor.baseIPluginAccessResultReceiver,
                    heartRateCensor.baseDeviceChangeReceiver
                )
                isHRMInWork = true
            }

            if (it.antDeviceType == DeviceType.BIKE_CADENCE && !isCadenceInWork) {
                cadenceCensor = BikeCadenceDevice(
                    appContext,
                    getCadence = { cadence ->
                        presenter.setCadence(cadence)
                    },
                    getSpeed = {speed ->
                        presenter.setSpeed(speed)
                    },
                    showToast = {text ->
                        toast(text)
                    },
                    setDependencies = {name, packageName ->
                        presenter.showDialog(name, packageName)
                    })

                handleCadence = AntPlusBikeCadencePcc.requestAccess(
                    appContext,
                    it.antDeviceNumber,
                    0,
                    false,
                    cadenceCensor.mResultReceiver,
                    cadenceCensor.mDeviceStateChangeReceiver
                )
                isCadenceInWork = true
            }

            if (it.antDeviceType== DeviceType.BIKE_SPD && !isSpeedInWork) {
                speedDistanceCensor = BikeSpeedDistanceDevice(
                    appContext,
                    getSpeed = {speed ->
                        if (!isCadenceInWork) presenter.setSpeed(speed)
                    },
                    getDistance = {distance ->
                        presenter.setDistance(distance)
                    },
                    getCadence = {cadence ->
                        if (!isCadenceInWork) presenter.setCadence(cadence)
                    },
                    showToast = {text ->
                        toast(text)
                    },
                    setDependencies = {name, packageName ->
                        presenter.showDialog(name, packageName)
                    })

                handleSpeedDistance = AntPlusBikeSpeedDistancePcc.requestAccess(
                    appContext,
                    it.antDeviceNumber,
                    0,
                    false,
                    speedDistanceCensor.mResultReceiver,
                    speedDistanceCensor.mDeviceStateChangeReceiver
                )
                isSpeedInWork = true
            }

            if (it.antDeviceType == DeviceType.FITNESS_EQUIPMENT && !isPowerInWork) {
                fitnessEquipmentCensor = FitnessEquipmentDevice(
                    showToast = {text ->
                        toast(text)
                    },
                    setDependencies = {name, packageName ->
                        presenter.showDialog(name, packageName)
                    },
                    getPower = {power ->
                        presenter.setPower(power)
                    },
                    getCadence = { cadence ->
                        if (!isCadenceInWork or !isSpeedInWork) presenter.setCadence(cadence)
                    },
                    getSpeed = {speed ->
                        if (!isCadenceInWork or !isSpeedInWork) presenter.setSpeed(speed)
                    },
                    getDistance = {distance ->
                        if (!isSpeedInWork) presenter.setDistance(distance)
                    })

                handleEquipment = AntPlusFitnessEquipmentPcc.requestNewOpenAccess(
                    appContext,
                    it.antDeviceNumber,
                    0,
                    fitnessEquipmentCensor.mPluginAccessResultReceiver,
                    fitnessEquipmentCensor.mDeviceStateChangeReceiver,
                    fitnessEquipmentCensor.mFitnessEquipmentStateReceiver
                )
                isPowerInWork = true
            }
        }

        buttonBackToScan.setOnClickListener {
            presenter.onFabClick()
        }

        fabSelectProgram.setOnClickListener {
            presenter.selectProgram()
        }
    }

    override fun onFabClick() {
        presenter.onFabClick()
    }

    override fun setHeartRate(hr: String) {
        heartRateValue.text = hr
    }

    override fun showDialog(name: String, packageName: String) {
        val alertDialog = AlertDialog.Builder(appContext)
        alertDialog.setTitle("Missing Dependency")
        alertDialog.setMessage("The required service was not found:\n\"$name\"\nYou need to install the ANT+ Plugins service or you may need to update your existing version if you already have it.\nDo you want to launch the Play Store to get it?")
        alertDialog.setCancelable(true)
        alertDialog.setPositiveButton("Go to Store") { _, _ ->
            val startStore = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName")
            )
            startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(startStore)
        }
        alertDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.create().show()
    }

    override fun setCadence(cadence: String) {
        cadenceValue.text = cadence
    }

    override fun setDistance(distance: String) {
        distanceValue.text = distance
    }

    override fun setSpeed(speed: String) {
        speedValue.text = speed
    }

    override fun setPower(power: String) {
        powerValue.text = power
    }

    override fun closeAccess() {
        handleHeartRate?.close()
        handleCadence?.close()
        handleSpeedDistance?.close()
        handleEquipment?.close()
    }

    override fun hideAddButton() {
        fabSelectProgram.hide()
        workGraph.visibility = View.VISIBLE
    }

    override fun showAddButton() {
        fabSelectProgram.show()
        workGraph.visibility = View.INVISIBLE
    }
}