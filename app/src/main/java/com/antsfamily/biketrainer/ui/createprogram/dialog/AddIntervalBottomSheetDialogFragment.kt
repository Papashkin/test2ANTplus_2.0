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
import com.antsfamily.biketrainer.databinding.BottomSheetFragmentAddIntervalBinding
import com.antsfamily.biketrainer.presentation.EventObserver
import com.antsfamily.biketrainer.presentation.createprogram.AddIntervalBottomSheetViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.util.afterTextChange
import com.antsfamily.biketrainer.util.mapDistinct
import com.antsfamily.biketrainer.util.orZero
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIntervalBottomSheetDialogFragment : BaseBottomSheetDialogFragment() {

    private lateinit var behavior: BottomSheetBehavior<View>

    override val viewModel: AddIntervalBottomSheetViewModel by viewModels {
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
        val binding = BottomSheetFragmentAddIntervalBinding.inflate(inflater, container, false)
        binding.root.rootView.minimumHeight = getScreenHeight()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(BottomSheetFragmentAddIntervalBinding.bind(view)) {
            observeState(this)
            observeEvents()
            bindInteractions(this)
        }
    }

    override fun onStart() {
        super.onStart()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun observeState(binding: BottomSheetFragmentAddIntervalBinding) {
        with(binding) {
            viewModel.state.mapDistinct { it.restPowerError }
                .observe(viewLifecycleOwner) { restPowerTil.error = it }
            viewModel.state.mapDistinct { it.peakPowerError }
                .observe(viewLifecycleOwner) { peakPowerTil.error = it }
            viewModel.state.mapDistinct { it.restDurationError }
                .observe(viewLifecycleOwner) { restPowerDurationView.error = it }
            viewModel.state.mapDistinct { it.peakDurationError }
                .observe(viewLifecycleOwner) { peakPowerDurationView.error = it }
            viewModel.state.mapDistinct { it.countError }
                .observe(viewLifecycleOwner) { timesTil.error = it }
        }
    }

    private fun observeEvents() {
        viewModel.setIntervalResult.observe(viewLifecycleOwner, EventObserver {
            setFragmentResult(REQUEST_KEY_INTERVAL, bundleOf(KEY_INTERVAL to it))
            dismiss()
        })
    }

    private fun bindInteractions(binding: BottomSheetFragmentAddIntervalBinding) {
        with(binding) {
            addBtn.setOnClickListener {
                viewModel.onAddClick(
                    peakPower = peakPowerEt.text.toString().toIntOrNull().orZero(),
                    restPower = restPowerEt.text.toString().toIntOrNull().orZero(),
                    peakDuration = peakPowerDurationView.getValue(),
                    restDuration = restPowerDurationView.getValue(),
                    count = timesEt.text.toString().toIntOrNull().orZero()
                )
            }
            peakPowerDurationView.setOnDurationChangeListener { viewModel.onPeakDurationChange() }
            restPowerDurationView.setOnDurationChangeListener { viewModel.onRestDurationChange() }
            peakPowerEt.afterTextChange { viewModel.onPeakPowerTextChange() }
            restPowerEt.afterTextChange { viewModel.onRestPowerTextChange() }
            timesEt.afterTextChange { viewModel.onCountChange() }
        }
    }

    private fun getScreenHeight() = Resources.getSystem().displayMetrics.heightPixels

    companion object {
        const val REQUEST_KEY_INTERVAL = "request_key_interval"
        const val KEY_INTERVAL = "key_interval"
    }
}

