package com.example.test2antplus.ui.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.test2antplus.MainApplication
import com.example.test2antplus.R
import com.example.test2antplus.SelectedDevice
import com.example.test2antplus.presenter.ScanPresenter
import com.example.test2antplus.ui.adapter.NewDeviceAdapter
import com.pawegio.kandroid.putParcelableCollection
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.fragment_scan.*
import javax.inject.Inject

interface ScanInterface {
    fun startScan()
    fun stopScan()
    fun addNewDevice(device: SelectedDevice)
    fun showToast(id: Int)
    fun showToast(text: String)
    fun showButtonConnect()
    fun hideButtonConnect()
    fun saveSearchedDevices()
}

class ScanFragment : Fragment(), ScanInterface {
    companion object {
        const val SCAN_LIST = "scan list"
    }

    private lateinit var presenter: ScanPresenter
    private lateinit var newDeviceAdapter: NewDeviceAdapter

    @Inject lateinit var appContext: Context

    fun newInstance(devices: ArrayList<SelectedDevice>): WorkFragment = WorkFragment().apply {
        arguments = Bundle().apply {
            putParcelableCollection(SCAN_LIST, devices)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
     MainApplication.graph.inject(this)
     return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = ScanPresenter(this)

        newDeviceAdapter = NewDeviceAdapter(
            onItemClick = { position ->
                presenter.onDeviceClick()
                newDeviceAdapter.notifyItemChanged(position)
            })

        rvDevices.adapter = newDeviceAdapter

        bStartScan.setOnClickListener {
            presenter.startScan()
        }

        fabContinue.setOnClickListener {
            if (newDeviceAdapter.getSelectedData().isEmpty()) {
                showToast("There are no selected devices")
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
        bStartScan.alpha = 0.6f
        bStartScan.isClickable = false
    }

    override fun stopScan() {
        pbScan.visibility = View.GONE
        bStartScan.alpha = 1f
        bStartScan.isClickable = true
    }

    override fun showToast(id: Int) {
        toast(id)
    }

    override fun showToast(text: String) {
        toast(text)
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