package com.example.test2antplus.presentation.view.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import com.example.test2antplus.MainApplication
import com.example.test2antplus.R
import com.example.test2antplus.ant.device.SelectedDevice
import com.example.test2antplus.data.repositories.programs.Program
import com.example.test2antplus.presentation.presenter.scan.ScanPresenter
import com.example.test2antplus.presentation.BaseFragment
import com.pawegio.kandroid.putParcelableCollection
import kotlinx.android.synthetic.main.fragment_scan.*


class ScanFragment : BaseFragment(), ScanView {
    companion object {
        const val SELECTED_PROGRAM = "selected program"
        const val SELECTED_PROFILE = "selected profile"
    }

    private lateinit var presenter: ScanPresenter
    private lateinit var newDeviceAdapter: NewDeviceAdapter

    private var program: String? = null
    private var profileName: String? = null

    fun newInstance(profileName: String, program: Program): ScanFragment = ScanFragment().apply {
        arguments = Bundle().apply {
            putString(SELECTED_PROFILE, profileName)
            putString(SELECTED_PROGRAM, program.getProgram())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MainApplication.graph.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_scan, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = ScanPresenter(this)

        this.arguments?.apply {
            this.getParcelableArrayList<MultiDeviceSearchResult>("devices")
            profileName = getString(SELECTED_PROFILE)
            program = getString(SELECTED_PROGRAM)
        }

        toolbarScan.setNavigationIcon(R.drawable.ic_arrow_back_32)

        setAdapter()

        setListeners()
    }

    private fun setAdapter() {
        activity?.let {
            newDeviceAdapter = NewDeviceAdapter(
                onItemClick = { position ->
                    presenter.onDeviceClick()
                    newDeviceAdapter.notifyItemChanged(position)
                })

            rvDevices.adapter = newDeviceAdapter
        }
    }

    private fun setListeners() {
        toolbarScan.setNavigationOnClickListener {
            presenter.onBackPressed()
        }

        btnScan.setOnClickListener {
            presenter.startScan()
        }

        fabContinue.setOnClickListener {
            if (newDeviceAdapter.getSelectedData().isEmpty()) {
                showToast(getString(R.string.scan_no_selected_devices))
            } else {
                presenter.connectToSelectedDevices(profileName = profileName ?: "", program = program ?: "")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbindChannel()
    }

    override fun addNewDevice(device: SelectedDevice) {
        newDeviceAdapter.addDevice(device)
    }

    override fun startScan() {
        pbScan.visibility = View.VISIBLE
        btnScan.visibility = View.GONE
    }

    override fun stopScan() {
        pbScan.visibility = View.GONE
        btnScan.visibility = View.VISIBLE
    }

    override fun hideButtonConnect() {
        fabContinue.hide()
    }

    override fun showButtonConnect() {
        fabContinue.show()
    }

    override fun saveSearchedDevices() {
        this.arguments = Bundle().apply {
            this.putParcelableCollection("devices", newDeviceAdapter.getAllData())
        }
    }
}