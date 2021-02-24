package com.antsfamily.biketrainer.ui.createprogram

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.data.models.workouts.WorkoutIntervalParams
import com.antsfamily.biketrainer.data.models.workouts.WorkoutSegmentParams
import com.antsfamily.biketrainer.data.models.workouts.WorkoutStairsParams
import com.antsfamily.biketrainer.databinding.FragmentCreateProgramBinding
import com.antsfamily.biketrainer.presentation.EventObserver
import com.antsfamily.biketrainer.presentation.createprogram.CreateProgramViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.BaseFragment
import com.antsfamily.biketrainer.ui.createprogram.dialog.AddIntervalBottomSheetDialogFragment.Companion.KEY_INTERVAL
import com.antsfamily.biketrainer.ui.createprogram.dialog.AddIntervalBottomSheetDialogFragment.Companion.REQUEST_KEY_INTERVAL
import com.antsfamily.biketrainer.ui.createprogram.dialog.AddSegmentBottomSheetDialogFragment.Companion.KEY_SEGMENT
import com.antsfamily.biketrainer.ui.createprogram.dialog.AddSegmentBottomSheetDialogFragment.Companion.REQUEST_KEY_SEGMENT
import com.antsfamily.biketrainer.ui.createprogram.dialog.AddStairsBottomSheetDialogFragment.Companion.KEY_STAIRS
import com.antsfamily.biketrainer.ui.createprogram.dialog.AddStairsBottomSheetDialogFragment.Companion.REQUEST_KEY_STAIRS
import com.antsfamily.biketrainer.ui.util.afterTextChange
import com.antsfamily.biketrainer.util.mapDistinct
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateProgramFragment : BaseFragment(R.layout.fragment_create_program) {

    override val viewModel: CreateProgramViewModel by viewModels { withFactory(viewModelFactory) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(FragmentCreateProgramBinding.bind(view)) {
            observeState(this)
            observeEvents(this)
            bindInteractions(this)
        }
        setupFragmentResultListener()
    }

    private fun observeState(binding: FragmentCreateProgramBinding) {
        with(binding) {
            viewModel.state.mapDistinct { it.isLoading }
                .observe(viewLifecycleOwner) { loadingView.isVisible = it }
            viewModel.state.mapDistinct { it.programNameError }
                .observe(viewLifecycleOwner) { programNameTil.error = it }
            viewModel.state.mapDistinct { it.barItem }
                .observe(viewLifecycleOwner) { workoutChart.item = it }
            viewModel.state.mapDistinct { it.isEmptyBarChartVisible }
                .observe(viewLifecycleOwner) { workoutChart.isEmptyDataVisible = it }
            viewModel.state.mapDistinct { it.isBarChartVisible }
                .observe(viewLifecycleOwner) { workoutChart.isBarChartVisible = it }
            viewModel.state.mapDistinct { it.workoutError }
                .observe(viewLifecycleOwner) { workoutChart.error = it }
        }
    }

    private fun observeEvents(binding: FragmentCreateProgramBinding) {
        viewModel.showSuccessSnackBarEvent.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(it)
        })
        viewModel.showSuccessSnackBarMessageEvent.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(it)
        })
        viewModel.clearInputFieldsEvent.observe(viewLifecycleOwner, EventObserver {
            binding.programNameEt.text = null
        })
    }

    private fun bindInteractions(binding: FragmentCreateProgramBinding) {
        with(binding) {
            backBtn.setOnClickListener { viewModel.onBackClick() }
            programNameEt.afterTextChange { viewModel.onProgramNameChange() }
            addIntervalsBtn.setOnClickListener { viewModel.onIntervalsClick() }
            addSegmentBtn.setOnClickListener { viewModel.onSegmentClick() }
            addStairsBtn.setOnClickListener { viewModel.onStairsClick() }
            createBtn.setOnClickListener { viewModel.onCreateClick(programNameEt.text.toString()) }
        }
    }

    private fun setupFragmentResultListener() {
        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY_SEGMENT, viewLifecycleOwner
        ) { _, bundle -> viewModel.onSegmentAdd(bundle[KEY_SEGMENT] as? WorkoutSegmentParams) }
        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY_INTERVAL, viewLifecycleOwner
        ) { _, bundle -> viewModel.onIntervalAdd(bundle[KEY_INTERVAL] as? WorkoutIntervalParams) }
        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY_STAIRS, viewLifecycleOwner
        ) { _, bundle -> viewModel.onStairsAdd(bundle[KEY_STAIRS] as? WorkoutStairsParams) }
    }
}
