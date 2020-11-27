package com.antsfamily.biketrainer.ui.work

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import com.antsfamily.biketrainer.MainApplication
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.ant.device.BikeCadenceDevice
import com.antsfamily.biketrainer.ant.device.BikeSpeedDistanceDevice
import com.antsfamily.biketrainer.ant.device.FitnessEquipmentDevice
import com.antsfamily.biketrainer.ant.device.HeartRateDevice
import com.antsfamily.biketrainer.presentation.BaseFragment
import com.antsfamily.biketrainer.presentation.work.WorkPresenter
import com.antsfamily.biketrainer.presentation.work.WorkView
import com.antsfamily.biketrainer.util.setWorkParams
import com.github.mikephil.charting.data.BarData
import kotlinx.android.synthetic.main.fragment_work.*
import javax.inject.Inject


class WorkFragment : BaseFragment(R.layout.fragment_work),
    WorkView {
    companion object {
        const val DEVICES_LIST = "devices list"
        const val PROGRAM_NAME = "program name"
        const val PROFILE_NAME = "profile name"

        fun newInstance(
            devices: ArrayList<MultiDeviceSearchResult>,
            program: String,
            profileName: String
        ): WorkFragment = WorkFragment()
            .apply {
            this.arguments = Bundle().also {
                it.putParcelableArrayList(DEVICES_LIST, devices)
                it.putString(PROGRAM_NAME, program)
                it.putString(PROFILE_NAME, profileName)
            }
        }
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

    private var devices: ArrayList<MultiDeviceSearchResult> = arrayListOf()
    private var timeLabels: ArrayList<Float> = arrayListOf()
    private var program: String? = null
    private var profileName: String? = null

    private var isHRMInWork = false
    private var isCadenceInWork = false
    private var isSpeedInWork = false
    private var isPowerInWork = false

    @Inject
    lateinit var appContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        MainApplication.graph.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter =
            WorkPresenter(this)

        this.arguments?.apply {
            devices = this.getParcelableArrayList(DEVICES_LIST) ?: arrayListOf()
            program = this.getString(PROGRAM_NAME)
            profileName = this.getString(PROFILE_NAME)
        }

        if (program != null) {
            presenter.setProgram(program!!)
        }

        if (devices.isNotEmpty()) {
            devices.forEach {
                if (it.antDeviceType == DeviceType.HEARTRATE && !isHRMInWork) {
                    heartRateCensor = HeartRateDevice(
                        getHearRate = { heartRate ->
                            presenter.setHeartRate(heartRate)
                        },
                        showToast = { text ->
                            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT)
                                .show()
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
                        getSpeed = { speed ->
                            presenter.setSpeed(speed)
                        },
                        showToast = { text ->
                            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT)
                                .show()
                        },
                        setDependencies = { name, packageName ->
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

                if (it.antDeviceType == DeviceType.BIKE_SPD && !isSpeedInWork) {
                    speedDistanceCensor = BikeSpeedDistanceDevice(
                        appContext,
                        getSpeed = { speed ->
                            if (!isCadenceInWork) presenter.setSpeed(speed)
                        },
                        getDistance = { distance ->
                            presenter.setDistance(distance)
                        },
                        getCadence = { cadence ->
                            if (!isCadenceInWork) presenter.setCadence(cadence)
                        },
                        showToast = { text ->
                            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT)
                                .show()
                        },
                        setDependencies = { name, packageName ->
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
                        showToast = { text ->
                            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT)
                                .show()
                        },
                        setDependencies = { name, packageName ->
                            presenter.showDialog(name, packageName)
                        },
                        getPower = { power ->
                            presenter.setPower(power)
                        },
                        getCadence = { cadence ->
                            if (!isCadenceInWork or !isSpeedInWork) presenter.setCadence(cadence)
                        },
                        getSpeed = { speed ->
                            if (!isCadenceInWork or !isSpeedInWork) presenter.setSpeed(speed)
                        },
                        getDistance = { distance ->
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
        }

        setListeners()

    }

    private fun setListeners() {
        buttonBackToScan.setOnClickListener {
            presenter.onFabClick()
        }
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

    override fun setDataToChart(program: BarData, time: ArrayList<Float>) {
        workGraph.visibility = View.VISIBLE
        timeLabels = time
        workGraph.setWorkParams(program)
        workGraph.invalidate()
    }
}