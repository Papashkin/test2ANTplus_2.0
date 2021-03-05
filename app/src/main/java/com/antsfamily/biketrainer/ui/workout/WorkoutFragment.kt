package com.antsfamily.biketrainer.ui.workout

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.data.models.WorkoutSensorValues
import com.antsfamily.biketrainer.databinding.FragmentWorkoutBinding
import com.antsfamily.biketrainer.presentation.EventObserver
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.presentation.workout.WorkoutViewModel
import com.antsfamily.biketrainer.ui.BaseFragment
import com.antsfamily.biketrainer.util.mapDistinct
import com.antsfamily.biketrainer.util.orZero
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkoutFragment : BaseFragment(R.layout.fragment_workout) {

    private val args: WorkoutFragmentArgs by navArgs()

    override val viewModel: WorkoutViewModel by viewModels { withFactory(viewModelFactory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onCreate(args.devices.toList(), args.program)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(FragmentWorkoutBinding.bind(view)) {
            observeState(this)
            observeEvents()
            bindInteractions(this)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    private fun bindInteractions(binding: FragmentWorkoutBinding) {
        with(binding) {
            backBtn.setOnClickListener { viewModel.onBackClick() }
            startWorkoutBtn.setOnClickListener { viewModel.onStartClick() }
            pauseWorkoutBtn.setOnClickListener { viewModel.onPauseClick() }
            stopWorkoutBtn.setOnClickListener { viewModel.onStopClick() }
        }
    }

    private fun observeEvents() {
        viewModel.showSuccessSnackBarEvent.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(it)
        })
        viewModel.showSuccessSnackBarMessageEvent.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(it)
        })
        viewModel.showDeviceDialogEvent.observe(viewLifecycleOwner, EventObserver {
            if (it != null) showDialog(it.first, it.second)
        })
    }

    private fun observeState(binding: FragmentWorkoutBinding) {
        with(binding) {
            viewModel.state.mapDistinct { it.isLoading }
                .observe(viewLifecycleOwner) { loadingView.isVisible = it }
            viewModel.state.mapDistinct { it.title }
                .observe(viewLifecycleOwner) { titleTv.text = it }
            viewModel.state.mapDistinct { it.steps }.observe(viewLifecycleOwner) {
                workoutStepsTv.text =
                    getString(R.string.workout_round, it?.first.orZero(), it?.second.orZero())
            }
            viewModel.state.mapDistinct { it.nextStep }.observe(viewLifecycleOwner) {
                workoutNextStepValueTv.text =
                    getString(
                        R.string.workout_next_round_value,
                        it?.first?.toString() ?: EMPTY_DATA,
                        it?.second
                    )
            }
            viewModel.state.mapDistinct { it.startButtonVisible }
                .observe(viewLifecycleOwner) { startWorkoutBtn.isVisible = it }
            viewModel.state.mapDistinct { it.pauseButtonVisible }
                .observe(viewLifecycleOwner) { pauseWorkoutBtn.isVisible = it }
            viewModel.state.mapDistinct { it.stopButtonVisible }
                .observe(viewLifecycleOwner) { stopWorkoutBtn.isVisible = it }
            viewModel.state.mapDistinct { it.sensorsData }
                .observe(viewLifecycleOwner) { setSensorsDara(it) }
            viewModel.state.mapDistinct { it.progress }
                .observe(viewLifecycleOwner) { this.stepCountdownRb.progress = it }
            viewModel.state.mapDistinct { it.remainingTimeString }
                .observe(viewLifecycleOwner) { workoutRemainingTimeTv.text = it }
        }
    }

    private var alertDialog: AlertDialog? = null
    private fun showDialog(name: String, packageName: String) {
        alertDialog?.dismiss()
        alertDialog = AlertDialog.Builder(this.requireContext())
            .setTitle("Missing Dependency")
            .setMessage("The required service was not found:\n\"$name\"\nYou need to install the ANT+ Plugins service or you may need to update your existing version if you already have it.\nDo you want to launch the Play Store to get it?")
            .setCancelable(true)
            .setPositiveButton(OPEN_STORE) { _, _ ->
                val startStore = Intent(
                    Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")
                )
                startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.requireContext().startActivity(startStore)
            }
            .setNegativeButton(CANCEL) { dialog, _ -> dialog.dismiss() }
            .create()
        alertDialog?.show()
    }

    private fun FragmentWorkoutBinding.setSensorsDara(data: WorkoutSensorValues) {
        with(data) {
            workoutHeartRateTv.text =
                getString(R.string.workout_heart_rate, heartRate?.toString() ?: EMPTY_DATA)
            workoutCadenceTv.text =
                getString(R.string.workout_cadence, cadence?.toString() ?: EMPTY_DATA)
            workoutSpeedTv.text = getString(R.string.workout_speed, speed?.toString() ?: EMPTY_DATA)
            workoutPowerTv.text = getString(R.string.workout_power, power?.toString() ?: EMPTY_DATA)
            workoutDistanceTv.text =
                getString(R.string.workout_distance, distance?.toString() ?: EMPTY_DATA)
        }
    }

    companion object {
        private const val EMPTY_DATA = "--"
        private const val OPEN_STORE = "Go to store"
        private const val CANCEL = "Cancel"
    }
}
