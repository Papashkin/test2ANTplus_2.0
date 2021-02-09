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
import com.antsfamily.biketrainer.databinding.BottomSheetFragmentAddSegmentBinding
import com.antsfamily.biketrainer.presentation.EventObserver
import com.antsfamily.biketrainer.presentation.createprogram.AddSegmentBottomSheetViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.util.afterTextChange
import com.antsfamily.biketrainer.util.mapDistinct
import com.antsfamily.biketrainer.util.orZero
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddSegmentBottomSheetDialogFragment : BaseBottomSheetDialogFragment() {

    private lateinit var behavior: BottomSheetBehavior<View>

    override val viewModel: AddSegmentBottomSheetViewModel by viewModels {
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
        val binding = BottomSheetFragmentAddSegmentBinding.inflate(inflater, container, false)
        binding.root.rootView.minimumHeight = getScreenHeight()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(BottomSheetFragmentAddSegmentBinding.bind(view)) {
            observeState(this)
            observeEvents()
            bindInteractions(this)
        }
    }

    override fun onStart() {
        super.onStart()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun observeState(binding: BottomSheetFragmentAddSegmentBinding) {
        with(binding) {
            viewModel.state.mapDistinct { it.powerError }
                .observe(viewLifecycleOwner) { powerTil.error = it }
            viewModel.state.mapDistinct { it.durationError }
                .observe(viewLifecycleOwner) { durationView.error = it }
        }
    }

    private fun observeEvents() {
        viewModel.setSegmentResult.observe(viewLifecycleOwner, EventObserver {
            setFragmentResult(REQUEST_KEY_SEGMENT, bundleOf(KEY_SEGMENT to it))
            dismiss()
        })
    }

    private fun bindInteractions(binding: BottomSheetFragmentAddSegmentBinding) {
        with(binding) {
            addBtn.setOnClickListener {
                viewModel.onAddClick(
                    powerEt.text.toString().toIntOrNull().orZero(),
                    durationView.getValue()
                )
            }
            durationView.setOnDurationChangeListener { viewModel.onDurationChange() }
            powerEt.afterTextChange { viewModel.onPowerTextChange() }
        }
    }

    private fun getScreenHeight() = Resources.getSystem().displayMetrics.heightPixels

    companion object {
        const val REQUEST_KEY_SEGMENT = "request_key_segment"
        const val KEY_SEGMENT = "key_segment"
    }
}
