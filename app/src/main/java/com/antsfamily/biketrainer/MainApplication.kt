package com.antsfamily.biketrainer

import android.app.Application
import com.antsfamily.biketrainer.di.AppComponent
import com.antsfamily.biketrainer.di.DaggerAppComponent
import com.antsfamily.biketrainer.di.modules.AppModule
import com.antsfamily.biketrainer.di.modules.NavigationModule

class MainApplication: Application() {
    companion object {
        @JvmStatic
        lateinit var graph: AppComponent

        const val ACTION_WORK_SENDING = "com.antsfamily.biketrainer.presentation.view.workScreen.WorkFragment"
        const val ACTION_PROGRAM_SETTINGS = "com.antsfamily.biketrainer.ui.programs.ProgramsFragment"
        const val UPD_PROGRAMS_LIST = "programs list"
        const val ARGS_PROGRAM = "selected program"

        const val PERMISSION_FOR_APP = 1

        var PROGRAM_IMAGES_PATH: String? = null
    }

    override fun onCreate() {
        super.onCreate()
        initDI()
    }

    private fun initDI() {
        graph = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .navigationModule(NavigationModule())
            .build()

        graph.inject(this)
    }
}
