package com.example.test2antplus.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.test2antplus.MainApplication
import com.example.test2antplus.R
import com.example.test2antplus.SelectedDevice
import com.example.test2antplus.presenter.ScanPresenter
import com.example.test2antplus.ui.adapter.NewDeviceAdapter
import com.pawegio.kandroid.putParcelableCollection
import kotlinx.android.synthetic.main.fragment_scan.*

interface ScanInterface {
    fun startScan()
    fun stopScan()
    fun addNewDevice(device: SelectedDevice)
    fun showButtonConnect()
    fun hideButtonConnect()
    fun saveSearchedDevices()
}

class ScanFragment : BaseFragment(), ScanInterface {
    companion object {
//        const val SCAN_LIST = "scan list"
    }

    private lateinit var presenter: ScanPresenter
    private lateinit var newDeviceAdapter: NewDeviceAdapter

//    fun newInstance(devices: ArrayList<SelectedDevice>): WorkFragment = WorkFragment().apply {
//        arguments = Bundle().apply {
//            putParcelableCollection(SCAN_LIST, devices)
//        }
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
     MainApplication.graph.inject(this)
     return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = ScanPresenter(this)

        toolbarScan.setNavigationIcon(R.drawable.ic_arrow_back_32)
        toolbarScan.setNavigationOnClickListener {
            presenter.onBackPressed()
        }

        newDeviceAdapter = NewDeviceAdapter(
            onItemClick = { position ->
                presenter.onDeviceClick()
                newDeviceAdapter.notifyItemChanged(position)
            })

        rvDevices.adapter = newDeviceAdapter

        btnScan.setOnClickListener {
            presenter.startScan()
        }

        fabContinue.setOnClickListener {
            if (newDeviceAdapter.getSelectedData().isEmpty()) {
                showToast(getString(R.string.scan_no_selected_devices))
            } else {
                presenter.connectToSelectedDevices()
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