package com.example.test2antplus.presenter

import com.example.test2antplus.MainApplication
import com.example.test2antplus.navigation.AppRouter
import com.example.test2antplus.navigation.Screens
import javax.inject.Inject

class StartPresenter {
    @Inject
    lateinit var router: AppRouter

    init {
        MainApplication.graph.inject(this)
    }

    fun onProfileClick() {
        router.navigateTo(Screens.PROFILES_FRAGMENT)
    }

    fun onProgramClick() {
        router.navigateTo(Screens.PROGRAM_FRAGMENT)
    }
}