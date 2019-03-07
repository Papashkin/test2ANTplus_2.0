package com.example.test2antplus.presenter

import androidx.lifecycle.LifecycleOwner
import com.example.test2antplus.MainApplication
import com.example.test2antplus.navigation.AppRouter
import com.example.test2antplus.navigation.Screens
import com.example.test2antplus.ui.view.ProgramInterface
import javax.inject.Inject

class ProgramPresenter(private val view: ProgramInterface, owner: LifecycleOwner) {

    @Inject
    lateinit var router: AppRouter

    init {
        MainApplication.graph.inject(this)
    }

    fun addProgram() {
        router.navigateTo(Screens.PROGRAM_SETTINGS_FRAGMENT)
    }


}