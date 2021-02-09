package com.antsfamily.biketrainer.ui.createprogram

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.data.models.workouts.WorkoutIntervalParams
import com.antsfamily.biketrainer.data.models.workouts.WorkoutSegmentParams
import com.antsfamily.biketrainer.data.models.workouts.WorkoutStairsParams
import com.antsfamily.biketrainer.databinding.FragmentCreateProgramBinding
import com.antsfamily.biketrainer.presentation.createprogram.CreateProgramViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.BaseFragment
import com.antsfamily.biketrainer.ui.createprogram.dialog.AddIntervalBottomSheetDialogFragment.Companion.KEY_INTERVAL
import com.antsfamily.biketrainer.ui.createprogram.dialog.AddIntervalBottomSheetDialogFragment.Companion.REQUEST_KEY_INTERVAL
import com.antsfamily.biketrainer.ui.createprogram.dialog.AddSegmentBottomSheetDialogFragment.Companion.KEY_SEGMENT
import com.antsfamily.biketrainer.ui.createprogram.dialog.AddSegmentBottomSheetDialogFragment.Companion.REQUEST_KEY_SEGMENT
import com.antsfamily.biketrainer.ui.createprogram.dialog.AddStairsBottomSheetDialogFragment.Companion.KEY_STAIRS_DOWN
import com.antsfamily.biketrainer.ui.createprogram.dialog.AddStairsBottomSheetDialogFragment.Companion.KEY_STAIRS_UP
import com.antsfamily.biketrainer.ui.createprogram.dialog.AddStairsBottomSheetDialogFragment.Companion.REQUEST_KEY_STAIRS_DOWN
import com.antsfamily.biketrainer.ui.createprogram.dialog.AddStairsBottomSheetDialogFragment.Companion.REQUEST_KEY_STAIRS_UP
import com.antsfamily.biketrainer.ui.util.afterTextChange
import com.antsfamily.biketrainer.util.mapDistinct
import com.antsfamily.biketrainer.util.setCommonParams
import com.antsfamily.biketrainer.util.timeFormat
import com.github.mikephil.charting.data.BarData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateProgramFragment : BaseFragment(R.layout.fragment_create_program) {

    override val viewModel: CreateProgramViewModel by viewModels { withFactory(viewModelFactory) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(FragmentCreateProgramBinding.bind(view)) {
            observeState(this)
            observeEvents()
            bindInteractions(this)
        }
        setupFragmentResultListener()
    }

    private fun observeState(binding: FragmentCreateProgramBinding) {
        with(binding) {
            viewModel.state.mapDistinct { it.programNameError }
                .observe(viewLifecycleOwner) { programNameEt.error = it }
            viewModel.state.mapDistinct { it.barChart }
                .observe(viewLifecycleOwner) { it?.let { updateChart(it) } }
        }
    }

    private fun observeEvents() {
        // TODO implement
    }

    private fun bindInteractions(binding: FragmentCreateProgramBinding) {
        with(binding) {
            backBtn.setOnClickListener { viewModel.onBackClick() }
            programNameEt.afterTextChange { viewModel.onProgramNameChange() }
            addIntervalsBtn.setOnClickListener { viewModel.onIntervalsClick() }
            addSegmentBtn.setOnClickListener { viewModel.onSegmentClick() }
            addUpstairsBtn.setOnClickListener { viewModel.onUpstairsClick() }
            addDownstairsBtn.setOnClickListener { viewModel.onDownstairsClick() }
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
            REQUEST_KEY_STAIRS_UP, viewLifecycleOwner
        ) { _, bundle -> viewModel.onStairsUpAdd(bundle[KEY_STAIRS_UP] as? WorkoutStairsParams) }
        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY_STAIRS_DOWN, viewLifecycleOwner
        ) { _, bundle -> viewModel.onStairsDownAdd(bundle[KEY_STAIRS_DOWN] as? WorkoutStairsParams) }
    }

//    private fun initObservers() {
//        viewModel.keyboard.observe(viewLifecycleOwner) {
//            if (it) showKeyboard() else hideKeyboard()
//        }
//        viewModel.loading.observe(viewLifecycleOwner) {
//            if (it) showLoading() else hideLoading()
//        }
//        viewModel.toast.observe(viewLifecycleOwner) {
//            if (it != null) {
//                if (it is Int) showToast(it) else showToast(it as String)
//            }
//        }
//        viewModel.programDialog.observe(viewLifecycleOwner) {
//            if (it) showProgramBottomDialog() else hideProgramBottomDialog()
//        }
//        viewModel.barChart.observe(viewLifecycleOwner) {
//            if (it != null) updateChart(it.first, it.second)
//        }
//        viewModel.programTypeAndData.observe(viewLifecycleOwner) {
//            setProgramType(it.first, it.second, it.third)
//        }
//        viewModel.chartGetter.observe(viewLifecycleOwner) {
//            if (it) getChart()
//        }
//        viewModel.backDialog.observe(viewLifecycleOwner) {
//            if (it) showBackDialog()
//        }
//    }

    private fun FragmentCreateProgramBinding.updateChart(data: Pair<BarData, ArrayList<Long>>?) {
        data?.let { (data, duration) ->
            programChart.visibility = View.VISIBLE
            val timeLabels = duration.map { it.timeFormat() }
            programChart.setCommonParams(data, timeLabels)
            programChart.invalidate()
        }
    }

//    private fun getChart() {
//        viewModel.getProgramImagePath(chartProgram)
//    }

//    private fun showBackDialog() {
//        val alertDialog = AlertDialog.Builder(requireContext())
//        alertDialog.setMessage(getString(R.string.dialog_save_program_question))
//        alertDialog.setCancelable(false)
//        alertDialog.setPositiveButton(resources.getText(R.string.dialog_yes)) { dialog, _ ->
//            viewModel.onSaveClick()
//            dialog.dismiss()
//        }
//        alertDialog.setNegativeButton(resources.getText(R.string.dialog_no)) { dialog, _ ->
//            viewModel.onExit()
//            dialog.dismiss()
//        }
//        alertDialog.create().show()
//    }
}
