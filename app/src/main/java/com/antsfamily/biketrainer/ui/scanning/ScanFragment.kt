package com.antsfamily.biketrainer.ui.scanning

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.databinding.FragmentScanBinding
import com.antsfamily.biketrainer.presentation.scan.ScanViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.BaseFragment
import com.antsfamily.biketrainer.util.mapDistinct
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScanFragment : BaseFragment(R.layout.fragment_scan) {

    private val args: ScanFragmentArgs by navArgs()

    override val viewModel: ScanViewModel by viewModels { withFactory(viewModelFactory) }

    @Inject
    lateinit var newDeviceAdapter: NewDeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onCreate(args.programName)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(FragmentScanBinding.bind(view)) {
            observeState(this)
            observeEvents()
            bindInteractions(this)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun observeState(binding: FragmentScanBinding) {
        with(binding) {
            viewModel.state.mapDistinct { it.devices }
                .observe(viewLifecycleOwner) { newDeviceAdapter.devices = it }
            viewModel.state.mapDistinct { it.isContinueButtonVisible }
                .observe(viewLifecycleOwner) { continueFl.isVisible = it }
        }
    }

    private fun observeEvents() {
    }

    private fun bindInteractions(binding: FragmentScanBinding) {
        with(binding) {
            backBtn.setOnClickListener { viewModel.onBackClick() }
            devicesRv.adapter = newDeviceAdapter.apply {
                setOnItemClickListener { viewModel.onDeviceClick(it) }
            }
            continueBtn.setOnClickListener { viewModel.onContinueClick() }
        }
    }
}
