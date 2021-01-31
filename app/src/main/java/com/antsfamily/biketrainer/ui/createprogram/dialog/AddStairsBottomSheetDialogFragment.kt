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
import androidx.navigation.fragment.navArgs
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

    private val args: AddStairsBottomSheetDialogFragmentArgs by navArgs()

    private lateinit var behavior: BottomSheetBehavior<View>

    override val viewModel: AddStairsBottomSheetViewModel by viewModels {
        withFactory(viewModelFactory)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        behavior = BottomSheetBehavior(context, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onCreate(args.type)
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
        }
    }

    private fun observeEvents() {
        viewModel.setStairsDownResult.observe(viewLifecycleOwner, EventObserver {
            setFragmentResult(REQUEST_KEY_STAIRS_DOWN, bundleOf(KEY_STAIRS_DOWN to it))
            dismiss()
        })
        viewModel.setStairsUpResult.observe(viewLifecycleOwner, EventObserver {
            setFragmentResult(REQUEST_KEY_STAIRS_UP, bundleOf(KEY_STAIRS_UP to it))
            dismiss()
        })
    }

    private fun bindInteractions(binding: BottomSheetFragmentAddStairsBinding) {
        with(binding) {
            addBtn.setOnClickListener {
                viewModel.onAddClick(
                    startPower = startPowerEt.text.toString().toIntOrNull().orZero(),
                    endPower = endPowerEt.text.toString().toIntOrNull().orZero(),
                    duration = durationView.getValue()
                )
            }
            durationView.setOnDurationChangeListener { viewModel.onDurationChange() }
            startPowerEt.afterTextChange { viewModel.onStartPowerTextChange() }
            endPowerEt.afterTextChange { viewModel.onEndPowerTextChange() }
        }
    }

    private fun getScreenHeight() = Resources.getSystem().displayMetrics.heightPixels

    companion object {
        const val REQUEST_KEY_STAIRS_UP = "request_key_stairs_up"
        const val KEY_STAIRS_UP = "key_stairs_up"

        const val REQUEST_KEY_STAIRS_DOWN = "request_key_stairs_down"
        const val KEY_STAIRS_DOWN = "key_stairs_down"
    }
}

