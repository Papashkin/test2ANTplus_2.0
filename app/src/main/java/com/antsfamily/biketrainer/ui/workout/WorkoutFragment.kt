package com.antsfamily.biketrainer.ui.workout

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import com.antsfamily.biketrainer.MainApplication
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.data.models.TrainingParams
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.presentation.workout.WorkoutViewModel
import com.antsfamily.biketrainer.ui.BaseFragment
import com.antsfamily.biketrainer.util.setWorkParams
import com.github.mikephil.charting.data.BarData
import kotlinx.android.synthetic.main.fragment_workout.*

class WorkoutFragment : BaseFragment(R.layout.fragment_workout) {
    companion object {
        const val DEVICES_LIST = "devices list"
        const val PROGRAM_NAME = "program name"
        const val PROFILE_NAME = "profile name"

        fun newInstance(
            devices: ArrayList<MultiDeviceSearchResult>,
            program: String,
            profileName: String
        ): WorkoutFragment = WorkoutFragment().apply {
            this.arguments = bundleOf(
                DEVICES_LIST to devices,
                PROGRAM_NAME to program,
                PROFILE_NAME to profileName
            )
        }
    }

    override val viewModel: WorkoutViewModel by viewModels { withFactory(viewModelFactory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        MainApplication.graph.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val devices = it.getParcelableArrayList(DEVICES_LIST) ?: arrayListOf<MultiDeviceSearchResult>()
            if (!devices.isNullOrEmpty()) {
                viewModel.setDevices(devices)
            }
            val program = it.getString(PROGRAM_NAME)
            if (program != null) {
                viewModel.setProgram(program)
            }
        }

        initListeners()
        initObservers()
    }

    private fun initListeners() {
        buttonBackToScan.setOnClickListener {
            viewModel.onBackClick()
        }
        buttonStartWork.setOnClickListener {
            viewModel.onStartClick()
        }
        buttonStopWork.setOnClickListener {
            viewModel.onStopClick()
        }
    }

    private fun initObservers() {
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it is Int) showToast(it) else showToast(it as String)
            }
        })
        viewModel.chartData.observe(viewLifecycleOwner, Observer {
            if (it != null) setDataToChart(it.first, it.second)
        })
        viewModel.antPlusDialog.observe(viewLifecycleOwner, Observer {
            if (it != null) showDialog(it.first, it.second)
        })
        viewModel.trainingData.observe(viewLifecycleOwner, Observer {
            if (it != null) setTrainingData(it)
        })
    }

    private var alertDialog: AlertDialog? = null
    private fun showDialog(name: String, packageName: String) {
        alertDialog?.dismiss()
        alertDialog = AlertDialog.Builder(this.requireContext())
            .setTitle("Missing Dependency")
            .setMessage("The required service was not found:\n\"$name\"\nYou need to install the ANT+ Plugins service or you may need to update your existing version if you already have it.\nDo you want to launch the Play Store to get it?")
            .setCancelable(true)
            .setPositiveButton("Go to Store") { _, _ ->
                val startStore = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName")
                )
                startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.requireContext().startActivity(startStore)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.create()

        alertDialog?.show()
    }

    private var timeLabels: ArrayList<Float> = arrayListOf()
    private fun setDataToChart(program: BarData, time: ArrayList<Float>) {
        workGraph.visibility = View.VISIBLE
        timeLabels = time
        workGraph.setWorkParams(program)
        workGraph.invalidate()
    }

    private fun setTrainingData(trainingData: TrainingParams) {
        trainingData.apply {
            if (heartRate != null) heartRateValue.text = heartRate
            if (cadence != null) cadenceValue.text = cadence
            if (speed != null) speedValue.text = speed
            if (distance != null) distanceValue.text = distance
            if (power != null) powerValue.text = power
        }
    }
}
