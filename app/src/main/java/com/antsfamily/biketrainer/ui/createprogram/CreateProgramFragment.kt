package com.antsfamily.biketrainer.ui.createprogram

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.databinding.FragmentCreateProgramBinding
import com.antsfamily.biketrainer.presentation.programSettings.CreateProgramViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.BaseFragment
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
            observeEvents()
            bindInteractions(this)
        }
    }

    private fun observeState(binding: FragmentCreateProgramBinding) {
        with(binding) {
            viewModel.state.mapDistinct { it.programNameError }
                .observe(viewLifecycleOwner) { programNameEt.error = it }
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

//    private fun updateChart(data: BarData, duration: ArrayList<Float>) {
//        chartProgram.visibility = View.VISIBLE
//        val timeLabels = duration.map { it.toLong().timeFormat() }
//        chartProgram.setCommonParams(data, timeLabels)
//        chartProgram.invalidate()
//    }

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
