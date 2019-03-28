package com.example.test2antplus.presenter

import com.example.test2antplus.MainApplication
import com.example.test2antplus.navigation.FragmentScreens
import com.example.test2antplus.ui.view.WorkInterface
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class WorkPresenter(private val view: WorkInterface) {

    @Inject lateinit var router: Router

    init {
        MainApplication.graph.inject(this)
    }

    fun onFabClick() {
        view.closeAccess()
        router.backTo(FragmentScreens.ScanScreen())
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
        router.navigateTo(FragmentScreens.ProgramScreen(isTime2work = true))
    }
}