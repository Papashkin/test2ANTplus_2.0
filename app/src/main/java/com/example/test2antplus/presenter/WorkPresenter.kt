package com.example.test2antplus.presenter

import com.example.test2antplus.MainApplication
import com.example.test2antplus.navigation.AppRouter
import com.example.test2antplus.navigation.Screens
import com.example.test2antplus.ui.view.WorkInterface
import javax.inject.Inject

class WorkPresenter(private val view: WorkInterface) {

    @Inject lateinit var router: AppRouter

    init {
        MainApplication.graph.inject(this)
    }

    fun onFabClick() {
        view.closeAccess()
        router.backTo(Screens.SCAN_FRAGMENT)
    }

    fun setHeartRate(hr: String) {
        view.setHeartRate(hr)
    }

    fun setCadence(cadence: String) {
        view.setCadence(cadence)
    }

    fun setSpeed(speed: String) {
        view.setSpeed(speed)
    }

    fun setDistance(distance: String) {
        view.setDistance(distance)
    }

    fun setPower(power: String) {
        view.setPower(power)
    }

    fun showDialog(name: String, packageName: String) {
        view.showDialog(name, packageName)
    }

    fun selectProgram() {
        router.navigateTo(Screens.PROGRAM_FRAGMENT)
    }
}