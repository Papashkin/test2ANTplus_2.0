package com.example.test2antplus.presenter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.dsi.ant.AntService
import com.dsi.ant.plugins.antplus.pcc.MultiDeviceSearch
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import com.example.test2antplus.MainApplication
import com.example.test2antplus.R
import com.example.test2antplus.SelectedDevice
import com.example.test2antplus.ant.service.AntRadioServiceConnection
import com.example.test2antplus.navigation.FragmentScreens
import com.example.test2antplus.ui.view.ScanFragment
import com.example.test2antplus.workInAsinc
import com.pawegio.kandroid.runOnUiThread
import io.reactivex.Observable
import ru.terrakok.cicerone.Router
import java.util.*
import javax.inject.Inject

class ScanPresenter(private val view: ScanFragment) {
    companion object {
        const val TAG = "test2antplus"
    }

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var context: Context

    private var search: MultiDeviceSearch? = null
    private var connection: AntRadioServiceConnection
    private val foundedDevices: ArrayList<SelectedDevice> = arrayListOf()

    init {
        MainApplication.graph.inject(this)
        connection = AntRadioServiceConnection(context)
        doBindChannelService()
    }

    private fun doBindChannelService() {
        Log.v(TAG, "Bind channel service...")
        AntService.bindService(context, connection)
    }

    private val deviceList = EnumSet.of(
        DeviceType.BIKE_CADENCE,
        DeviceType.BIKE_POWER,
        DeviceType.BIKE_SPD,
        DeviceType.BIKE_SPDCAD,
        DeviceType.CONTROLLABLE_DEVICE,
        DeviceType.FITNESS_EQUIPMENT,
        DeviceType.HEARTRATE
    )

    private val antCallback = object : MultiDeviceSearch.SearchCallbacks {
        override fun onSearchStopped(reason: RequestAccessResult) {
            val id = when (reason) {
                RequestAccessResult.CHANNEL_NOT_AVAILABLE -> R.string.channel_not_available
                RequestAccessResult.OTHER_FAILURE -> R.string.channel_other_failure
                RequestAccessResult.SEARCH_TIMEOUT -> R.string.channel_search_timeout
                RequestAccessResult.USER_CANCELLED -> R.string.channel_scan_stop
                else -> R.string.channel_unknown
            }
            view.stopScan()
            view.showToast(id)
        }

        override fun onSearchStarted(rssiSupport: MultiDeviceSearch.RssiSupport) {
            view.showToast(context.getString(R.string.scan_start))
        }

        override fun onDeviceFound(device: MultiDeviceSearchResult) {
            Log.v(TAG, "New device : id ${device.resultID}, type ${device.antDeviceType}")
            runOnUiThread {
                val selectedDevice = SelectedDevice(
                    device = device,
                    isSelected = false
                )
                if (!foundedDevices.contains(selectedDevice)) {
                    foundedDevices.add(selectedDevice)
                }
                view.addNewDevice(selectedDevice)
            }
        }
    }

    fun startScan() {
        search = MultiDeviceSearch(context, deviceList, antCallback)
        view.startScan()
    }

    fun unbindChannel() {
        doUnbindChannelService()
    }

    @SuppressLint("CheckResult")
    private fun doUnbindChannelService() {
        Log.v(TAG, "Unbinding channel service...")
        Observable.just(
            connection.closeBackgroundScanChannel()
        ).compose {
            it.workInAsinc()
        }.subscribe({
            Log.v(TAG, "Channel service was unbinded.")
        }, {
            it.printStackTrace()
        })
    }

    fun onDeviceClick() {
        val count = foundedDevices.filter {
            it.isSelected
        }.size
        if (count > 0) {
            view.showButtonConnect()
        } else {
            view.hideButtonConnect()
        }
    }

    @SuppressLint("CheckResult")
    fun connectToSelectedDevices() {
        view.saveSearchedDevices()
        search?.close()
        router.navigateTo(FragmentScreens.WorkScreen(
            devices = foundedDevices.filter {
                it.isSelected
            }.map {
                it.device
            } as ArrayList<MultiDeviceSearchResult>))
    }

    fun onBackPressed() {
        router.exit()
    }
}