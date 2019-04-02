package com.example.test2antplus.presenter

import com.example.test2antplus.MainApplication
import com.example.test2antplus.navigation.FragmentScreens
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class StartPresenter {
    @Inject
    lateinit var router: Router

    init {
        MainApplication.graph.inject(this)
    }

    fun onProfileClick() {
        router.navigateTo(FragmentScreens.ProfileScreen())
    }

    fun onProgramClick() {
        router.navigateTo(FragmentScreens.ProgramScreen(
            isTime2work = false,
            profile = ""
        ))
    }
}