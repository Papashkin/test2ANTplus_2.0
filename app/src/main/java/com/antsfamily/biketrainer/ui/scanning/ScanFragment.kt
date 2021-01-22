package com.antsfamily.biketrainer.ui.scanning

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import com.antsfamily.biketrainer.MainApplication
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.ant.device.SelectedDevice
import com.antsfamily.biketrainer.data.models.Program
import com.antsfamily.biketrainer.presentation.withFactory
import com.antsfamily.biketrainer.presentation.scan.ScanViewModel
import com.antsfamily.biketrainer.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_scan.*

@AndroidEntryPoint
class ScanFragment : BaseFragment(R.layout.fragment_scan) {
    companion object {
        const val SELECTED_PROGRAM = "selected program"
        const val SELECTED_PROFILE = "selected profile"

        fun newInstance(profileName: String, program: Program): ScanFragment = ScanFragment().apply {
            arguments = bundleOf(
                SELECTED_PROGRAM to program.getProgram(),
                SELECTED_PROFILE to profileName
            )
        }
    }

    override val viewModel: ScanViewModel by viewModels { withFactory(viewModelFactory) }

    private var program: String? = null
    private var profileName: String? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.arguments?.apply {
            this.getParcelableArrayList<MultiDeviceSearchResult>("devices")
            profileName = getString(SELECTED_PROFILE)
            program = getString(SELECTED_PROGRAM)
        }

        toolbarScan.setNavigationIcon(R.drawable.ic_arrow_back_32)

        initAdapter()
        initListeners()
        initObservers()
    }

    private lateinit var newDeviceAdapter: NewDeviceAdapter
    private fun initAdapter() {
        activity?.let {
            newDeviceAdapter = NewDeviceAdapter(
                onItemClick = { position ->
                    viewModel.onDeviceClick()
                    newDeviceAdapter.notifyItemChanged(position)
                })
            rvDevices.adapter = newDeviceAdapter
        }
    }

    private fun initListeners() {
        toolbarScan.setNavigationOnClickListener {
            viewModel.onBackPressed()
        }

        btnScan.setOnClickListener {
            viewModel.onScanClick()
        }

        fabContinue.setOnClickListener {
            if (newDeviceAdapter.getSelectedData().isEmpty()) {
                showToast(getString(R.string.scan_no_selected_devices))
            } else {
                viewModel.connectToSelectedDevices(profileName = profileName ?: "", program = program ?: "")
            }
        }
    }

    private fun initObservers() {
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it) showLoading() else hideLoading()
        })
        viewModel.connectButtonVisibility.observe(viewLifecycleOwner, Observer {
            if (it) showConnectionButton() else hideConnectionButton()
        })
        viewModel.devices.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) setNewDevices(it as ArrayList<SelectedDevice>)
        })
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it is Int) showToast(it) else showToast(it as String)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
    }

    private fun setNewDevices(devices: ArrayList<SelectedDevice>) {
        newDeviceAdapter.setDevices(devices)
    }

    private fun showLoading() {
        pbScan.visibility = View.VISIBLE
        btnScan.visibility = View.GONE
    }

    private fun hideLoading() {
        pbScan.visibility = View.GONE
        btnScan.visibility = View.VISIBLE
    }

    private fun hideConnectionButton() {
        fabContinue.hide()
    }

    private fun showConnectionButton() {
        fabContinue.show()
    }

}
