package com.antsfamily.biketrainer.ui.workout

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.data.models.TrainingParams
import com.antsfamily.biketrainer.presentation.EventObserver
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.presentation.workout.WorkoutViewModel
import com.antsfamily.biketrainer.ui.BaseFragment
import com.antsfamily.biketrainer.util.setWorkParams
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import com.github.mikephil.charting.data.BarData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_workout.*

@AndroidEntryPoint
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val devices =
                it.getParcelableArrayList(DEVICES_LIST) ?: arrayListOf<MultiDeviceSearchResult>()
            if (!devices.isNullOrEmpty()) {
                viewModel.setDevices(devices)
            }
            val program = it.getString(PROGRAM_NAME)
            if (program != null) {
                viewModel.setProgram(program)
            }
        }
        initListeners()
        observeState()
        observeEvents()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    private fun initListeners() {
        buttonBackToScan.setOnClickListener { viewModel.onBackClick() }
        buttonStartWork.setOnClickListener { viewModel.onStartClick() }
        buttonStopWork.setOnClickListener { viewModel.onStopClick() }
    }

    private fun observeEvents() {
        viewModel.showSnackBarEvent.observe(viewLifecycleOwner, EventObserver {
            showToast(it)
        })
        viewModel.showDeviceDialogEvent.observe(viewLifecycleOwner, EventObserver {
            if (it != null) showDialog(it.first, it.second)
        })
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) {

        }
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
