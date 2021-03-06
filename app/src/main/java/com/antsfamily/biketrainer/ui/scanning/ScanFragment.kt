package com.antsfamily.biketrainer.ui.scanning

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.databinding.FragmentScanBinding
import com.antsfamily.biketrainer.presentation.EventObserver
import com.antsfamily.biketrainer.presentation.scan.ScanViewModel
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.ui.BaseFragment
import com.antsfamily.biketrainer.util.mapDistinct
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScanFragment : BaseFragment(R.layout.fragment_scan) {

    private val args: ScanFragmentArgs by navArgs()

    private var alertDialog: AlertDialog? = null

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
                .observe(viewLifecycleOwner) { newDeviceAdapter.items = it }
            viewModel.state.mapDistinct { it.isContinueButtonVisible }
                .observe(viewLifecycleOwner) { continueFl.isVisible = it }
        }
    }

    private fun observeEvents() {
        viewModel.showDeviceDialogEvent.observe(viewLifecycleOwner, EventObserver {
            if (it != null) showDialog(it.first, it.second)
        })
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

    companion object {
        private const val OPEN_STORE = "Go to store"
        private const val CANCEL = "Cancel"
    }
}
