package com.antsfamily.biketrainer.ui.createprogram.dialog

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.antsfamily.biketrainer.BaseBottomSheetDialogFragment
import com.antsfamily.biketrainer.databinding.BottomSheetFragmentAddStairsBinding
import com.antsfamily.biketrainer.presentation.EventObserver
import com.antsfamily.biketrainer.presentation.createprogram.AddStairsBottomSheetViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.util.afterTextChange
import com.antsfamily.biketrainer.util.mapDistinct
import com.antsfamily.biketrainer.util.orZero
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddStairsBottomSheetDialogFragment : BaseBottomSheetDialogFragment() {

    private lateinit var behavior: BottomSheetBehavior<View>

    override val viewModel: AddStairsBottomSheetViewModel by viewModels {
        withFactory(viewModelFactory)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        behavior = BottomSheetBehavior(context, null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = BottomSheetFragmentAddStairsBinding.inflate(inflater, container, false)
        binding.root.rootView.minimumHeight = getScreenHeight()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(BottomSheetFragmentAddStairsBinding.bind(view)) {
            observeState(this)
            observeEvents()
            bindInteractions(this)
        }
    }

    override fun onStart() {
        super.onStart()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun observeState(binding: BottomSheetFragmentAddStairsBinding) {
        with(binding) {
            viewModel.state.mapDistinct { it.startPowerError }
                .observe(viewLifecycleOwner) { startPowerTil.error = it }
            viewModel.state.mapDistinct { it.endPowerError }
                .observe(viewLifecycleOwner) { endPowerTil.error = it }
            viewModel.state.mapDistinct { it.durationError }
                .observe(viewLifecycleOwner) { durationView.error = it }
            viewModel.state.mapDistinct { it.stepCountError }
                .observe(viewLifecycleOwner) { stepCountTil.error = it }
        }
    }

    private fun observeEvents() {
        viewModel.setStairsResult.observe(viewLifecycleOwner, EventObserver {
            setFragmentResult(REQUEST_KEY_STAIRS, bundleOf(KEY_STAIRS to it))
            dismiss()
        })
    }

    private fun bindInteractions(binding: BottomSheetFragmentAddStairsBinding) {
        with(binding) {
            addBtn.setOnClickListener {
                viewModel.onAddClick(
                    startPower = startPowerEt.text.toString().toIntOrNull().orZero(),
                    endPower = endPowerEt.text.toString().toIntOrNull().orZero(),
                    stepCount = stepCountEt.text.toString().toIntOrNull().orZero(),
                    duration = durationView.getValue()
                )
            }
            durationView.setOnDurationChangeListener { viewModel.onDurationChange() }
            startPowerEt.afterTextChange { viewModel.onStartPowerTextChange() }
            endPowerEt.afterTextChange { viewModel.onEndPowerTextChange() }
            stepCountEt.afterTextChange { viewModel.onStepCountChange() }
        }
    }

    private fun getScreenHeight() = Resources.getSystem().displayMetrics.heightPixels

    companion object {
        const val REQUEST_KEY_STAIRS = "request_key_stairs"
        const val KEY_STAIRS = "key_stairs"
    }
}

